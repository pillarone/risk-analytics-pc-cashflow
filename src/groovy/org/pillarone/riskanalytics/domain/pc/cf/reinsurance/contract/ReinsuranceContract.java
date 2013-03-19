package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ReinsuranceContract extends MultiCounterPartyBaseReinsuranceContract implements IReinsuranceContractMarker {

    private IPeriodStrategy parmCoveredPeriod = PeriodStrategyType.getDefault();
    private IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getDefault();
    private Boolean parmVirtual = Boolean.FALSE;
    private IPeriodDependingThresholdStore termDeductible;
    private IPeriodDependingThresholdStore termLimit;

    @Override
    protected void initSimulation() {
        super.initSimulation();
        if (firstIterationAndPeriod()) {
            parmCoveredPeriod.initStartCover(iterationScope.getPeriodScope().getCurrentPeriodStartDate());
        }
    }

    @Override
    protected void initIteration() {
        super.initIteration();
        if (iterationScope.getPeriodScope().isFirstPeriod()) {
            termDeductible = new EqualUsagePerPeriodThresholdStore(parmContractStrategy.getTermDeductible());
            termLimit = new EqualUsagePerPeriodThresholdStore(parmContractStrategy.getTermLimit());
        }
    }

    /**
     * Add for every covered period a new contract instance to the periodStore. Generally contracts of different periods
     * are completely independent except there is a common term clause.
     */
    protected void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContracts(
                    currentPeriod, inUnderwritingInfo, ExposureBase.ABSOLUTE, termDeductible, termLimit, new ArrayList<ClaimCashflowPacket>()));
        }
    }

    /**
     * Filter according to covered period, occurrence date of claim and defaulting counter parties.
     * All incoming claims are removed if there is no counter party left.
     */
    protected void timeFilter() {
        DateTime noCoverAfter = counterPartyFactors.allCounterPartiesDefaultAfter();
        List<ClaimCashflowPacket> uncoveredClaims = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket grossClaim : inClaims) {
            if (!parmCoveredPeriod.isCovered(grossClaim.getOccurrenceDate())
                    || (noCoverAfter != null && grossClaim.getOccurrenceDate().isAfter(noCoverAfter))) {
                uncoveredClaims.add(grossClaim);
            }
        }
        inClaims.removeAll(uncoveredClaims);

        List<UnderwritingInfoPacket> uncoveredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
            if (!parmCoveredPeriod.isCovered(underwritingInfo.getExposure().getInceptionDate())
                    || (noCoverAfter != null && underwritingInfo.getExposure().getInceptionDate().isAfter(noCoverAfter))) {
                uncoveredUnderwritingInfo.add(underwritingInfo);
            }
        }
        inUnderwritingInfo.removeAll(uncoveredUnderwritingInfo);
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
