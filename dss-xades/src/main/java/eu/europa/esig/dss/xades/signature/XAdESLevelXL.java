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
package eu.europa.esig.dss.xades.signature;

import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.ValidationData;
import eu.europa.esig.dss.validation.ValidationDataContainer;
import eu.europa.esig.dss.xades.validation.XAdESSignature;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Set;

/**
 * XL profile of XAdES signature
 *
 */
public class XAdESLevelXL extends XAdESLevelX {

	/**
	 * The default constructor for XAdESLevelXL.
	 *
	 * @param certificateVerifier {@link CertificateVerifier}
	 */
	public XAdESLevelXL(final CertificateVerifier certificateVerifier) {
		super(certificateVerifier);
	}

	/**
	 * Adds CertificateValues and RevocationValues segments to UnsignedSignatureProperties.<br>
	 *
	 * An XML electronic signature MAY contain at most one:<br>
	 * - CertificateValues element and<br>
	 * - RevocationValues element.
	 *
	 * @see XAdESLevelBaselineT#extendSignatures(List<AdvancedSignature>)
	 */
	@Override
	protected void extendSignatures(List<AdvancedSignature> signatures) {
		super.extendSignatures(signatures);

		for (AdvancedSignature signature : signatures) {
			initializeSignatureBuilder((XAdESSignature) signature);
			if (xadesSignature.hasLTAProfile()) {
				continue;
			}

			// NOTE: do not force sources reload for certificate and revocation sources
			// in order to ensure the same validation data as on -C level
			xadesSignature.resetTimestampSource();

			assertExtendSignatureToXLPossible();

			checkSignatureIntegrity();
		}

		// Perform signature validation
		ValidationDataContainer validationDataContainer = documentValidator.getValidationData(signatures);

		for (AdvancedSignature signature : signatures) {
			initializeSignatureBuilder((XAdESSignature) signature);
			if (xadesSignature.hasLTAProfile()) {
				continue;
			}

			Element levelXUnsignedProperties = (Element) unsignedSignaturePropertiesDom.cloneNode(true);
			String indent = removeOldCertificateValues();
			removeOldRevocationValues();

			final ValidationData validationDataForInclusion = getValidationDataForInclusion(validationDataContainer, signature);

			Set<CertificateToken> certificateValuesToAdd = validationDataForInclusion.getCertificateTokens();
			Set<CRLToken> crlsToAdd = validationDataForInclusion.getCrlTokens();
			Set<OCSPToken> ocspsToAdd = validationDataForInclusion.getOcspTokens();

			incorporateCertificateValues(unsignedSignaturePropertiesDom, certificateValuesToAdd, indent);
			incorporateRevocationValues(unsignedSignaturePropertiesDom, crlsToAdd, ocspsToAdd, indent);

			unsignedSignaturePropertiesDom = indentIfPrettyPrint(unsignedSignaturePropertiesDom, levelXUnsignedProperties);
		}

	}

	/**
	 * Checks if the extension is possible.
	 */
	private void assertExtendSignatureToXLPossible() {
		final SignatureLevel signatureLevel = params.getSignatureLevel();
		if (SignatureLevel.XAdES_XL.equals(signatureLevel) && xadesSignature.hasLTAProfile()) {
			final String exceptionMessage = "Cannot extend the signature. The signature is already extended with [%s]!";
			throw new DSSException(String.format(exceptionMessage, "XAdES A"));
		} else if (xadesSignature.areAllSelfSignedCertificates()) {
			throw new DSSException("Cannot extend the signature. The signature contains only self-signed certificate chains!");
		}
	}

}
