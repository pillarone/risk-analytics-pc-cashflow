package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractAndBase {
    public ReinsuranceContract reinsuranceContract;
    public ReinsuranceContractBase contractBase;

    public ReinsuranceContractAndBase(ReinsuranceContract reinsuranceContract, ReinsuranceContractBase contractBase) {
        this.reinsuranceContract = reinsuranceContract;
        this.contractBase = contractBase;
    }

    @Override
    public String toString() {
        return reinsuranceContract + " (" + contractBase + ") ";
    }
}
