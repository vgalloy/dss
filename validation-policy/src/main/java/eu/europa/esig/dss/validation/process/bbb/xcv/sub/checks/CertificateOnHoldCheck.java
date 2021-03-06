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
package eu.europa.esig.dss.validation.process.bbb.xcv.sub.checks;

import java.util.Date;

import eu.europa.esig.dss.detailedreport.jaxb.XmlSubXCV;
import eu.europa.esig.dss.diagnostic.CertificateRevocationWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.RevocationReason;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;

public class CertificateOnHoldCheck extends ChainItem<XmlSubXCV> {

	private final CertificateRevocationWrapper certificateRevocation;
	private final Date currentTime;

	public CertificateOnHoldCheck(I18nProvider i18nProvider, XmlSubXCV result, CertificateRevocationWrapper certificateRevocation, 
			Date currentTime, LevelConstraint constraint) {
		super(i18nProvider, result, constraint);
		this.certificateRevocation = certificateRevocation;
		this.currentTime = currentTime;
	}

	@Override
	protected boolean process() {
		boolean isOnHold = (certificateRevocation != null) && !certificateRevocation.isStatus() && 
				RevocationReason.CERTIFICATE_HOLD.equals(certificateRevocation.getReason());
		if (isOnHold) {
			isOnHold = certificateRevocation.getRevocationDate() != null && currentTime.compareTo(certificateRevocation.getRevocationDate()) >= 0;
		}
		return !isOnHold;
	}

	@Override
	protected MessageTag getAdditionalInfo() {
		if (certificateRevocation != null && certificateRevocation.getRevocationDate() != null) {
			String revocationDateStr = ValidationProcessUtils.getFormattedDate(certificateRevocation.getRevocationDate());
			return MessageTag.REVOCATION.setArgs(certificateRevocation.getReason(), revocationDateStr);
		}
		return null;
	}

	@Override
	protected MessageTag getMessageTag() {
		return MessageTag.BBB_XCV_ISCOH;
	}

	@Override
	protected MessageTag getErrorMessageTag() {
		return MessageTag.BBB_XCV_ISCOH_ANS;
	}

	@Override
	protected Indication getFailedIndicationForConclusion() {
		return Indication.INDETERMINATE;
	}

	@Override
	protected SubIndication getFailedSubIndicationForConclusion() {
		return SubIndication.TRY_LATER;
	}

}
