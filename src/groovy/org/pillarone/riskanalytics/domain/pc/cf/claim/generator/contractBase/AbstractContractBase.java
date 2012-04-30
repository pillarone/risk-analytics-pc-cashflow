package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractContractBase extends AbstractParameterObject implements IReinsuranceContractBaseStrategy {

    public DateTime occurrenceDate(EventPacket event) {
        return event.getDate();
    }

    /**
     * @param underwritingInfo
     * @return inception date of the exposure of the provided argument
     */
    public DateTime inceptionDate(UnderwritingInfoPacket underwritingInfo) {
        return underwritingInfo.getExposure().getInceptionDate();
    }

    /**
     * Don't split claims by default
     * @return 1
     */
    public int splittedClaimsNumber() {
        return 1;
    }

}
