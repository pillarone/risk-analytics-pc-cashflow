package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.ThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContract extends BaseReinsuranceContract implements IReinsuranceContractMarker {

    private IPeriodStrategy parmCoveredPeriod = PeriodStrategyType.getDefault();
    private IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getDefault();
    private Boolean parmVirtual = Boolean.FALSE;
    private ThresholdStore termDeductible;
    private EqualUsagePerPeriodThresholdStore termLimit;

    protected void initSimulation() {
        super.initSimulation();
        if (firstIterationAndPeriod()) {
            parmCoveredPeriod.initStartCover(iterationScope.getPeriodScope().getCurrentPeriodStartDate());
        }
    }


    protected void initIteration() {
        super.initIteration();
        if (iterationScope.getPeriodScope().isFirstPeriod()) {
            termDeductible = new ThresholdStore(parmContractStrategy.getTermDeductible());
            termLimit = new EqualUsagePerPeriodThresholdStore(parmContractStrategy.getTermLimit());
        }
    }

    /**
     * Add for every covered period a new contract instance to the periodStore. Generally contracts of different periods
     * are completely independent except there is a common term clause.
     */
    protected void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContract(getInUnderwritingInfo(), termDeductible, termLimit));
        }
    }

    /**
     * Filter according to covered period, occurrence date of claim and defaulting counter parties.
     * All incoming claims are removed if there is no counter party left.
     */
    protected void timeFilter() {
        super.timeFilter();
        DateTime noCoverAfter = counterPartyFactors.allCounterPartiesDefaultAfter();
        List<ClaimCashflowPacket> uncoveredClaims = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket grossClaim : getInClaims()) {
            if (!parmCoveredPeriod.isCovered(grossClaim.getOccurrenceDate())
                    || (noCoverAfter != null && grossClaim.getOccurrenceDate().isAfter(noCoverAfter))) {
                uncoveredClaims.add(grossClaim);
            }
        }
        getInClaims().removeAll(uncoveredClaims);

        List<UnderwritingInfoPacket> uncoveredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        for (UnderwritingInfoPacket underwritingInfo : getInUnderwritingInfo()) {
            if (!parmCoveredPeriod.isCovered(underwritingInfo.getExposure().getInceptionDate())
                    || (noCoverAfter != null && underwritingInfo.getExposure().getInceptionDate().isAfter(noCoverAfter))) {
                uncoveredUnderwritingInfo.add(underwritingInfo);
            }
        }
        getInUnderwritingInfo().removeAll(uncoveredUnderwritingInfo);
    }

    protected boolean isCurrentPeriodCovered() {
        DateTime periodStart = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
        DateTime periodEnd = iterationScope.getPeriodScope().getNextPeriodStartDate();
        return parmCoveredPeriod.isCovered(periodStart) || parmCoveredPeriod.isCovered(periodEnd);
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }

    public IPeriodStrategy getParmCoveredPeriod() {
        return parmCoveredPeriod;
    }

    public void setParmCoveredPeriod(IPeriodStrategy parmCoveredPeriod) {
        this.parmCoveredPeriod = parmCoveredPeriod;
    }

    public Boolean getParmVirtual() {
        return parmVirtual;
    }

    public void setParmVirtual(Boolean parmVirtual) {
        this.parmVirtual = parmVirtual;
    }
}
