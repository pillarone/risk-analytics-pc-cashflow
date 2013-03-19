package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.IExposureBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.BaseReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.EqualUsagePerPeriodThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.IPeriodDependingThresholdStore;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.CoverStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ICoverStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.NonPropTemplateContractStrategy;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class TermReinsuranceContract extends BaseReinsuranceContract implements IReinsuranceContractMarker {

    private ICoverStrategy parmCover = CoverStrategyType.getDefault();
    private ContractCoverBase parmCoverageBase = ContractCoverBase.LOSSES_OCCURING;
    private IExposureBaseStrategy parmContractBase = ExposureBaseType.getDefault();
    private IReinsuranceContractStrategy parmContractStructure = TemplateContractType.getDefault();
    // todo(sku): implement as soon as spec from Chris is available
//    private ConstrainedMultiDimensionalParameter parmDefaultRate = new ConstrainedMultiDimensionalParameter(
//                    GroovyUtils.toList("[[1],[0d],[0d]]"), DefaultConstraints.columnHeaders,
//            ConstraintsFactory.getConstraints(DefaultConstraints.IDENTIFIER));

    private IPeriodDependingThresholdStore termDeductible;
    private IPeriodDependingThresholdStore termLimit;

    @Override
    protected void initIteration() {
        super.initIteration();
        if (iterationScope.getPeriodScope().isFirstPeriod()) {
            termDeductible = new EqualUsagePerPeriodThresholdStore(parmContractStructure.getTermDeductible());
            termLimit = new EqualUsagePerPeriodThresholdStore(parmContractStructure.getTermLimit());
        }
    }

    /**
     * Filter according to covered periods, occurrence or inception date of claim are used depending on parmCoverageBase.
     */
    @Override
    protected void timeFilter() {
        List<ClaimCashflowPacket> uncoveredClaims = new ArrayList<ClaimCashflowPacket>();
        if (parmContractStructure instanceof NonPropTemplateContractStrategy) {
            IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
            for (ClaimCashflowPacket grossClaim : inClaims) {
                Integer period;
                // todo(sku): extend enum with period() method to avoid uncovered enum states
                if (parmCoverageBase.equals(ContractCoverBase.LOSSES_OCCURING)) {
                    period = grossClaim.occurrencePeriod(periodCounter);
                }
                else if(parmCoverageBase.equals(ContractCoverBase.LOSSES_OCCURING)) {
                    period = grossClaim.getBaseClaim().getInceptionPeriod(periodCounter);
                } else {
                    throw new SimulationException("Unknown coverage base in reinsurance contract : " + parmContractBase.toString() );
                }

                if(period == null) {
                    throw new SimulationException("Period is null; this should not be possible");
                }
            }
            inClaims.removeAll(uncoveredClaims);
        }
    }

    protected void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
            periodStore.put(REINSURANCE_CONTRACT, parmContractStructure.getContracts(
                    currentPeriod, inUnderwritingInfo, parmContractBase.exposureBase(), termDeductible, termLimit, new ArrayList<ClaimCashflowPacket>()));
        }
    }

    /**
     * Remove all claims and underwriting info that is not used in this contract according to parmCover for claims and
     * parmContractBase for underwriting info.
     */
    @Override
    protected void coverFilter() {
        parmCover.coveredClaims(inClaims);
        parmContractBase.coveredUnderwritingInfo(inUnderwritingInfo);
    }

    public boolean isProportionalContract() {
        return false;
    }

    public ICoverStrategy getParmCover() {
        return parmCover;
    }

    public void setParmCover(ICoverStrategy parmCover) {
        this.parmCover = parmCover;
    }

    public IExposureBaseStrategy getParmContractBase() {
        return parmContractBase;
    }

    public void setParmContractBase(IExposureBaseStrategy parmContractBase) {
        this.parmContractBase = parmContractBase;
    }
    public IReinsuranceContractStrategy getParmContractStructure() {
        return parmContractStructure;
    }

    public void setParmContractStructure(IReinsuranceContractStrategy parmContractStructure) {
        this.parmContractStructure = parmContractStructure;
    }

    public ContractCoverBase getParmCoverageBase() {
        return parmCoverageBase;
    }

    public void setParmCoverageBase(ContractCoverBase parmCoverageBase) {
        this.parmCoverageBase = parmCoverageBase;
    }

    public IPeriodDependingThresholdStore getTermDeductible() {
        return termDeductible;
    }

    public void setTermDeductible(IPeriodDependingThresholdStore termDeductible) {
        this.termDeductible = termDeductible;
    }

    public IPeriodDependingThresholdStore getTermLimit() {
        return termLimit;
    }

    public void setTermLimit(IPeriodDependingThresholdStore termLimit) {
        this.termLimit = termLimit;
    }
}
