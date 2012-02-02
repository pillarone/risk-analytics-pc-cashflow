package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RetrospectiveReinsuranceContract extends BaseReinsuranceContract implements IReinsuranceContractMarker {

    private IReinsuranceContractStrategy parmContractStrategy = RetrospectiveReinsuranceContractType.getDefault();

    /**
     * add in every covered period a new contract to the periodStore
     */
    protected void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContract(getInUnderwritingInfo(), null, null));
        }
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }
}
