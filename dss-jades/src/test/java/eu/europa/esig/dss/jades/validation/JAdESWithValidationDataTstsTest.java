package eu.europa.esig.dss.jades.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.FoundCertificatesProxy;
import eu.europa.esig.dss.diagnostic.FoundRevocationsProxy;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.enumerations.CertificateOrigin;
import eu.europa.esig.dss.enumerations.CertificateRefOrigin;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.enumerations.TokenExtractionStategy;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.x509.revocation.crl.CRL;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.x509.CertificateRef;
import eu.europa.esig.dss.spi.x509.revocation.OfflineRevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.RevocationRef;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLRef;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPRef;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.SignatureCertificateSource;
import eu.europa.esig.dss.validation.SignedDocumentValidator;

public class JAdESWithValidationDataTstsTest extends AbstractJAdESTestValidation {

	@Override
	protected DSSDocument getSignedDocument() {
		return new FileDocument("src/test/resources/validation/jades-with-sigAndRefsTst.json");
	}
	
	@Override
	protected SignedDocumentValidator getValidator(DSSDocument signedDocument) {
		SignedDocumentValidator validator = super.getValidator(signedDocument);
		validator.setTokenExtractionStategy(TokenExtractionStategy.EXTRACT_CERTIFICATES_AND_REVOCATION_DATA);
		return validator;
	}
	
	@Override
	protected void verifySourcesAndDiagnosticData(List<AdvancedSignature> advancedSignatures, DiagnosticData diagnosticData) {
		assertEquals(1, advancedSignatures.size());
		AdvancedSignature advancedSignature = advancedSignatures.get(0);
		
		SignatureCertificateSource certificateSource = advancedSignature.getCertificateSource();
		assertEquals(2, certificateSource.getKeyInfoCertificates().size());
		
		List<CertificateRef> completeCertificateRefs = certificateSource.getCompleteCertificateRefs();
		assertEquals(3, completeCertificateRefs.size());
		for (CertificateRef certificateRef : completeCertificateRefs) {
			assertNotNull(certificateRef.getCertDigest());
			assertNotNull(certificateRef.getCertDigest().getAlgorithm());
			assertTrue(Utils.isArrayNotEmpty(certificateRef.getCertDigest().getValue()));
			
			assertNotNull(certificateRef.getCertificateIdentifier());
			assertNotNull(certificateRef.getCertificateIdentifier().getIssuerName());
			assertNotNull(certificateRef.getCertificateIdentifier().getSerialNumber());
		}
		
		List<CertificateRef> attributeCertificateRefs = certificateSource.getAttributeCertificateRefs();
		assertEquals(1, attributeCertificateRefs.size());
		for (CertificateRef certificateRef : attributeCertificateRefs) {
			assertNotNull(certificateRef.getCertDigest());
			assertNotNull(certificateRef.getCertDigest().getAlgorithm());
			assertTrue(Utils.isArrayNotEmpty(certificateRef.getCertDigest().getValue()));
			
			assertNotNull(certificateRef.getCertificateIdentifier());
			assertNotNull(certificateRef.getCertificateIdentifier().getIssuerName());
			assertNotNull(certificateRef.getCertificateIdentifier().getSerialNumber());
		}
		
		OfflineRevocationSource<CRL> crlSource = advancedSignature.getCRLSource();
		
		List<RevocationRef<CRL>> crlCompleteRefs = crlSource.getCompleteRevocationRefs();
		assertEquals(1, crlCompleteRefs.size());
		for (RevocationRef<CRL> crlRef : crlCompleteRefs) {
			assertTrue(crlRef instanceof CRLRef);
			assertNotNull(((CRLRef)crlRef).getCrlIssuer());
			assertNotNull(((CRLRef)crlRef).getCrlIssuedTime());
			
			assertNotNull(crlRef.getDigest());
			assertNotNull(crlRef.getDigest().getAlgorithm());
			assertTrue(Utils.isArrayNotEmpty(crlRef.getDigest().getValue()));
		}
		
		OfflineRevocationSource<OCSP> ocspSource = advancedSignature.getOCSPSource();
		
		List<RevocationRef<OCSP>> ocspCompleteRefs = ocspSource.getCompleteRevocationRefs();
		assertEquals(1, ocspCompleteRefs.size());
		for (RevocationRef<OCSP> ocspRef : ocspCompleteRefs) {
			assertTrue(ocspRef instanceof OCSPRef);
			
			assertNotNull(ocspRef.getDigest());
			assertNotNull(ocspRef.getDigest().getAlgorithm());
			assertTrue(Utils.isArrayNotEmpty(ocspRef.getDigest().getValue()));
		}
	}
	
	@Override
	protected void checkOrphanTokens(DiagnosticData diagnosticData) {
		super.checkOrphanTokens(diagnosticData);
		
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		
		FoundCertificatesProxy foundCertificates = signature.foundCertificates();
		assertEquals(2, foundCertificates.getRelatedCertificatesByOrigin(CertificateOrigin.KEY_INFO));
		assertEquals(2, foundCertificates.getRelatedCertificatesByRefOrigin(CertificateRefOrigin.COMPLETE_CERTIFICATE_REFS));
		assertEquals(2, foundCertificates.getRelatedCertificatesByRefOrigin(CertificateRefOrigin.ATTRIBUTE_CERTIFICATE_REFS));
		assertEquals(1, foundCertificates.getOrphanCertificateRefsByRefOrigin(CertificateRefOrigin.COMPLETE_CERTIFICATE_REFS));
		
		FoundRevocationsProxy foundRevocations = signature.foundRevocations();
		assertEquals(0, foundRevocations.getRelatedRevocationData());
		assertEquals(2, foundRevocations.getOrphanRevocationRefs().size());
	}
	
	@Override
	protected void checkTimestamps(DiagnosticData diagnosticData) {
		super.checkTimestamps(diagnosticData);
		
		assertEquals(4, diagnosticData.getTimestampList().size());
		int sigTstCounter = 0;
		int sigAndRfsTstCounter = 0;
		int rfsTstCounter = 0;
		
		for (TimestampWrapper timestampWrapper : diagnosticData.getTimestampList()) {
			assertTrue(timestampWrapper.isMessageImprintDataFound());
			assertTrue(timestampWrapper.isMessageImprintDataIntact());
			
			if (TimestampType.SIGNATURE_TIMESTAMP.equals(timestampWrapper.getType())) {
				assertEquals(1, timestampWrapper.getTimestampedSignatures().size());
				++sigTstCounter;
				
			} else if (TimestampType.VALIDATION_DATA_TIMESTAMP.equals(timestampWrapper.getType())) {
				assertEquals(1, timestampWrapper.getTimestampedSignatures().size());
				assertEquals(3, timestampWrapper.getTimestampedCertificates().size());
				assertEquals(1, timestampWrapper.getTimestampedTimestamps().size());
				assertEquals(1, timestampWrapper.getTimestampedOrphanCertificates().size());
				assertEquals(2, timestampWrapper.getTimestampedOrphanRevocations().size());
				++sigAndRfsTstCounter;
				
			} else if (TimestampType.VALIDATION_DATA_REFSONLY_TIMESTAMP.equals(timestampWrapper.getType())) {
				assertEquals(3, timestampWrapper.getTimestampedCertificates().size());
				assertEquals(1, timestampWrapper.getTimestampedOrphanCertificates().size());
				assertEquals(2, timestampWrapper.getTimestampedOrphanRevocations().size());
				++rfsTstCounter;
				
			}
		}
		assertEquals(1, sigTstCounter);
		assertEquals(2, sigAndRfsTstCounter);
		assertEquals(1, rfsTstCounter);
	}

}