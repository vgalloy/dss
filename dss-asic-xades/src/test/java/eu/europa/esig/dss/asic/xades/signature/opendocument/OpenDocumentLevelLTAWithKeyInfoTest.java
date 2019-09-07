package eu.europa.esig.dss.asic.xades.signature.opendocument;

import java.io.File;
import java.util.Date;

import javax.xml.crypto.dsig.CanonicalizationMethod;

import org.junit.Before;

import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.xades.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.signature.DocumentSignatureService;

public class OpenDocumentLevelLTAWithKeyInfoTest extends AbstractOpenDocumentTestSignature {
	
	private DocumentSignatureService<ASiCWithXAdESSignatureParameters> service;
	private ASiCWithXAdESSignatureParameters signatureParameters;
	
	public OpenDocumentLevelLTAWithKeyInfoTest(File fileToTest) {
		super(fileToTest);
	}

	@Before
	public void init() throws Exception {
		signatureParameters = new ASiCWithXAdESSignatureParameters();
		signatureParameters.bLevel().setSigningDate(new Date());
		signatureParameters.setSigningCertificate(getSigningCert());
		signatureParameters.setCertificateChain(getCertificateChain());
		signatureParameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LTA);
		signatureParameters.aSiC().setContainerType(ASiCContainerType.ASiC_E);
		
		// DSS-1548
		signatureParameters.setSignKeyInfo(true);
		signatureParameters.setKeyInfoCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE);

		service = new ASiCWithXAdESService(getCompleteCertificateVerifier());
		service.setTspSource(getGoodTsa());
	}

	@Override
	protected DocumentSignatureService<ASiCWithXAdESSignatureParameters> getService() {
		return service;
	}

	@Override
	protected ASiCWithXAdESSignatureParameters getSignatureParameters() {
		return signatureParameters;
	}
	
	@Override
	protected String getSigningAlias() {
		return GOOD_USER;
	}
}