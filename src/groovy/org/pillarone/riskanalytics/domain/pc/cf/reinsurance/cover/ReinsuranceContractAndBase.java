package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

/**
 * Helper class used for covering preceding contracts.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContractAndBase {

    public IReinsuranceContractMarker reinsuranceContract;
    public ReinsuranceContractBase contractBase;

    public ReinsuranceContractAndBase(IReinsuranceContractMarker reinsuranceContract, ReinsuranceContractBase contractBase) {
        this.reinsuranceContract = reinsuranceContract;
        this.contractBase = contractBase;
    }

    @Override
    public String toString() {
        return reinsuranceContract + " (" + contractBase + ") ";
    }
}
