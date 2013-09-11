package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless;

import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IPremiumPerPeriod {

    double getPremiumInPeriod(int period);

}
