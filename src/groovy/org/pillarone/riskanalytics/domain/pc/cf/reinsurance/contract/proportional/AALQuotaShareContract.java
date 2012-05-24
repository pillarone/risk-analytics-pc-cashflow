package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.AalLimitStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.ICommission;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AALQuotaShareContract extends AALAADQuotaShareContract {

    public AALQuotaShareContract(double quotaShare, ICommission commission, AalLimitStrategy limit) {
        super(quotaShare, commission, limit);
    }
}
