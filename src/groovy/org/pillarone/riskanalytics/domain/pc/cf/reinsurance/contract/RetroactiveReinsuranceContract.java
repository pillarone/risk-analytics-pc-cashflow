package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.RetroactivePeriodStrategy;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RetroactiveReinsuranceContract extends MultiCounterPartyBaseReinsuranceContract implements IReinsuranceContractMarker {

    private IPeriodStrategy parmCoveredPeriod = PeriodStrategyType.getRetroActiveDefault();
    private IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getDefault();

    private int startOfDevPeriod;

    /**
     * Filter according to covered period, occurrence date of claim and defaulting counter parties.
     * All incoming claims are removed if there is no counter party left.
     */
    protected void timeFilter() {
        DateTime noCoverAfter = counterPartyFactors.allCounterPartiesDefaultAfter();
        List<ClaimCashflowPacket> uncoveredClaims = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket grossClaim : inClaims) {
            if (!getCoveredPeriod().isCovered(grossClaim) || (noCoverAfter != null && grossClaim.getOccurrenceDate().isAfter(noCoverAfter))) {
                uncoveredClaims.add(grossClaim);
            }
        }
        inClaims.removeAll(uncoveredClaims);
    }


    protected void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            startOfDevPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
            IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContracts(
                    periodCounter, inUnderwritingInfo, ExposureBase.ABSOLUTE, null, null, inClaims, inFactors));
        }
    }

    /**
     * Creates a ClaimHistoryAndApplicableContract for every claim-contract combination and adds it to the provided
     * currentPeriodGrossClaims list.
     *
     * @param contracts                the contract covering the claim is added to this set
     * @param currentPeriodGrossClaims is extended with the ClaimHistoryAndApplicableContract of this claim, claimStorage and contract
     * @param currentPeriod            used to get the applicable contract from the periodStore
     * @param claim                    is used to fill currentPeriodGrossClaims
     */
    protected void updateCurrentPeriodGrossClaims(Set<IReinsuranceContract> contracts,
                                                  List<ClaimHistoryAndApplicableContract> currentPeriodGrossClaims,
                                                  int currentPeriod, ClaimCashflowPacket claim, ClaimStorageContainer claimsHistories) {
        List<IReinsuranceContract> periodContracts = (List<IReinsuranceContract>) periodStore.get(REINSURANCE_CONTRACT, startOfDevPeriod - currentPeriod);
        contracts.addAll(periodContracts);

        for (IReinsuranceContract contract : periodContracts) {
            ClaimStorage claimStorage = claimsHistories.get(claim, contract);
            if (claimStorage == null) {
                // first time this claim enters this contract
                claimStorage = ClaimStorage.makeStoredClaim(claim, contract, claimsHistories);
            }
            ClaimHistoryAndApplicableContract claimWithHistory = new ClaimHistoryAndApplicableContract(claim, claimStorage, contract);
            currentPeriodGrossClaims.add(claimWithHistory);
        }
    }

    RetroactivePeriodStrategy getCoveredPeriod() {
        return (RetroactivePeriodStrategy) parmCoveredPeriod;
    }

    @Override
    protected boolean isCurrentPeriodCovered() {
        return getCoveredPeriod().isCoverStartsInPeriod(iterationScope.getPeriodScope());
    }

    public IPeriodStrategy getParmCoveredPeriod() {
        return parmCoveredPeriod;
    }

    public void setParmCoveredPeriod(IPeriodStrategy parmCoveredPeriod) {
        this.parmCoveredPeriod = parmCoveredPeriod;
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }
}
