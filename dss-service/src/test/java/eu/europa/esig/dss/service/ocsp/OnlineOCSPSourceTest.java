/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.service.ocsp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.AlternateUrlsSourceAdapter;
import eu.europa.esig.dss.spi.x509.revocation.RevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;

public class OnlineOCSPSourceTest {

	private CertificateToken certificateToken;
	private CertificateToken rootToken;

	@BeforeEach
	public void init() {
		certificateToken = DSSUtils.loadCertificate(new File("src/test/resources/ec.europa.eu.crt"));
		rootToken = DSSUtils.loadCertificate(new File("src/test/resources/CALT.crt"));
	}

	@Test
	public void testOCSPWithoutNonce() {
		OnlineOCSPSource ocspSource = new OnlineOCSPSource();
		OCSPToken ocspToken = ocspSource.getRevocationToken(certificateToken, rootToken);
		assertNotNull(ocspToken);
		assertNotNull(ocspToken.getBasicOCSPResp());
		assertFalse(ocspToken.isUseNonce());
	}

	@Test
	public void testOCSPWithNonce() {
		OnlineOCSPSource ocspSource = new OnlineOCSPSource();
		ocspSource.setNonceSource(new SecureRandomNonceSource());
		OCSPToken ocspToken = ocspSource.getRevocationToken(certificateToken, rootToken);
		assertNotNull(ocspToken);
		assertTrue(ocspToken.isUseNonce());
		assertTrue(ocspToken.isNonceMatch());
	}

	@Test
	public void testOCSPWithFileCache() {
		FileCacheDataLoader fileCacheDataLoader = new FileCacheDataLoader();
		fileCacheDataLoader.setFileCacheDirectory(new File("target/ocsp-cache"));
		fileCacheDataLoader.setCacheExpirationTime(5000);
		fileCacheDataLoader.setDataLoader(new OCSPDataLoader());

		OnlineOCSPSource ocspSource = new OnlineOCSPSource();
		ocspSource.setDataLoader(fileCacheDataLoader);
		OCSPToken ocspToken = ocspSource.getRevocationToken(certificateToken, rootToken);
		assertNotNull(ocspToken);
		assertNotNull(ocspToken.getBasicOCSPResp());
		assertFalse(ocspToken.isUseNonce());

		ocspToken = ocspSource.getRevocationToken(certificateToken, rootToken);
		assertNotNull(ocspToken);
		assertNotNull(ocspToken.getBasicOCSPResp());
		assertFalse(ocspToken.isUseNonce());
	}

	@Test
	public void testInjectExternalUrls() {
		OnlineOCSPSource ocspSource = new OnlineOCSPSource();
		List<String> alternativeOCSPUrls = new ArrayList<>();
		alternativeOCSPUrls.add("http://wrong.url.com");

		RevocationSource<OCSPToken> currentOCSPSource = new AlternateUrlsSourceAdapter<>(ocspSource,
				alternativeOCSPUrls);
		OCSPToken ocspToken = currentOCSPSource.getRevocationToken(certificateToken, rootToken);
		assertNotNull(ocspToken);
	}
	
	@Test
	public void customCertIDDigestAlgorithmTest() {
		CertificateToken certificateToken = DSSUtils.loadCertificate(new File("src/test/resources/cert.pem"));
		CertificateToken caToken = DSSUtils.loadCertificate(new File("src/test/resources/cert_CA.pem"));
		
		OCSPDataLoader dataLoader = new OCSPDataLoader();
		dataLoader.setTimeoutConnection(10000);
		dataLoader.setTimeoutSocket(10000);

		OnlineOCSPSource ocspSource = new OnlineOCSPSource();
		ocspSource.setDataLoader(dataLoader);

		OCSPToken ocspToken = ocspSource.getRevocationToken(certificateToken, caToken);
		assertEquals(SignatureAlgorithm.RSA_SHA1, ocspToken.getSignatureAlgorithm()); // default value
		
		ocspSource.setCertIDDigestAlgorithm(DigestAlgorithm.SHA256);
		ocspToken = ocspSource.getRevocationToken(certificateToken, caToken);
		assertEquals(SignatureAlgorithm.RSA_SHA256, ocspToken.getSignatureAlgorithm());
	}

}
