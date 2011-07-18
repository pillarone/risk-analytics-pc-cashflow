package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractAndBase {
    public ReinsuranceContract reinsuranceContract;
    public ContractBase contractBase;

    public ReinsuranceContractAndBase(ReinsuranceContract reinsuranceContract, ContractBase contractBase) {
        this.reinsuranceContract = reinsuranceContract;
        this.contractBase = contractBase;
    }

    @Override
    public String toString() {
        return reinsuranceContract + " (" + contractBase + ") ";
    }
}
