package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IGetAPDates {

    public DateTime getAPDate(IPeriodCounter periodCounter);
}
