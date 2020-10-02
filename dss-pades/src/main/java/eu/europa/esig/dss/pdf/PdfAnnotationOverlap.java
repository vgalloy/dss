package eu.europa.esig.dss.pdf;

import eu.europa.esig.dss.validation.PdfModification;

public class PdfAnnotationOverlap implements PdfModification {
	
	private final int page;
	
	public PdfAnnotationOverlap(int page) {
		this.page = page;
	}

	@Override
	public int getPage() {
		return page;
	}

}
