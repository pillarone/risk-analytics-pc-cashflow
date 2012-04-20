package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RetrospectiveReinsuranceContract extends MultiCounterPartyBaseReinsuranceContract implements IReinsuranceContractMarker {

    private IReinsuranceContractStrategy parmContractStrategy = RetrospectiveReinsuranceContractType.getDefault();

    @Override
    protected void timeFilter() {
    }

    /**
     * add in every covered period a new contract to the periodStore
     */
    protected void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContracts(getInUnderwritingInfo(), null, null));
        }
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }
}
