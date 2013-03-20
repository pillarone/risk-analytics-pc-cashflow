package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;

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
            int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContracts(
                    currentPeriod, inUnderwritingInfo, ExposureBase.ABSOLUTE, null, null, new ArrayList<ClaimCashflowPacket>()));
        }
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }
}
