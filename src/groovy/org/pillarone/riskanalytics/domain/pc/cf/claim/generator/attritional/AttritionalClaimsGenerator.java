package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationState;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.ReinsuranceContractBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.BaseDateMode;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexMode;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.AggregateActualClaimsStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.AggregateUpdatingMethodologyStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.IAggregateActualClaimsStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.IAggregateUpdatingMethodologyStrategy;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGenerator extends AbstractClaimsGenerator {

    static Log LOG = LogFactory.getLog(AttritionalClaimsGenerator.class);

    private AttritionalClaimsModel subClaimsModel = new AttritionalClaimsModel();
    private IReinsuranceContractBaseStrategy parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(
            ReinsuranceContractBaseType.PLEASESELECT, new HashMap());
    private ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker.class, "");
    private PayoutPatternBase parmPayoutPatternBase = PayoutPatternBase.PERIOD_START_DATE;
    private IAggregateActualClaimsStrategy parmActualClaims = AggregateActualClaimsStrategyType.getDefault();
    private IAggregateUpdatingMethodologyStrategy parmUpdatingMethodology = AggregateUpdatingMethodologyStrategyType.getDefault();
    private ConstrainedMultiDimensionalParameter parmDeterministicClaims = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d}), Arrays.asList(REAL_PERIOD, CLAIM_VALUE),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    private boolean globalTrivialIndices;

    private IterationScope iterationScope;


    @Override
    protected void doCalculation(String phase) {
//        A deal may commute before the end of the contract period. We may hence want to terminate claims generation
//        Depending on the outcome in the experience account.
        initIteration(periodStore, periodScope, phase);

//        In this phase check the commutation state from last period. If we are not commuted then calculate claims.
        if (phase.equals(PHASE_CLAIMS_CALCULATION)) {
            CommutationState commutationState = (CommutationState) (periodStore.get(COMMUTATION_STATE));

            if (!(commutationState.isCommuted() || commutationState.isCommuteThisPeriod())) {
                IPeriodCounter periodCounter = periodScope.getPeriodCounter();
                List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
                List<GrossClaimRoot> claimsAfterSplit = new ArrayList<GrossClaimRoot>();
                List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();

                List<Factors> runoffFactors = null;
//                Check that the period we are in covers new claims.
                if (periodScope.getCurrentPeriod() < globalLastCoveredPeriod) {
                    if (globalDeterministicMode) {
                        baseClaims = getDeterministicClaims(parmDeterministicClaims, periodScope, ClaimType.ATTRITIONAL);
                    } else {
                        List<Factors> severityFactors = IndexUtils.filterFactors(inFactors, subClaimsModel.getParmSeverityIndices(),
                                IndexMode.STEPWISE_PREVIOUS, BaseDateMode.START_OF_PROJECTION, null);
                        baseClaims = subClaimsModel.baseClaims(inUnderwritingInfo, inEventFrequencies, inEventSeverities,
                                severityFactors, parmParameterizationBasis, this, periodScope);
                    }
                    baseClaims = parmUpdatingMethodology.updatingUltimate(baseClaims, parmActualClaims, periodCounter,
                            globalUpdateDate, inPatterns, periodScope.getCurrentPeriod(), DAYS_360, parmPayoutPatternBase);
                    checkBaseClaims(baseClaims);
                    runoffFactors = new ArrayList<Factors>();
                    List<GrossClaimRoot> grossClaimRoots = baseClaimsOfCurrentPeriodAdjustedPattern(baseClaims, parmPayoutPattern, parmActualClaims,
                            periodScope, parmPayoutPatternBase);
                    claimsAfterSplit = parmParameterizationBasis.splitClaims(grossClaimRoots, periodScope);
                    storeClaimsWhichOccurInFuturePeriods(claimsAfterSplit, periodStore);
                    claims = cashflowsInCurrentPeriod(claimsAfterSplit, runoffFactors, periodScope);
                }
                developClaimsOfFormerPeriods(claims, periodCounter, runoffFactors);
                checkCashflowClaims(claims);
                doCashflowChecks(claims, claimsAfterSplit, baseClaims);
                setTechnicalProperties(claims);
                outClaims.addAll(claims);
            }

//            In this phase; check for incoming commutation information. If there is none, i.e the inCommutationChannel is null,
//            Then assume that we want no commutation behaviour. Add an indefinite commutationState object to the environment.
        } else if (phase.equals(PHASE_STORE_COMMUTATION_STATE)) {
            if (inCommutationState != null && inCommutationState.size() == 1) {
                CommutationState packet = inCommutationState.get(0);
                periodStore.put(COMMUTATION_STATE, packet, 1);
            } else {
                throw new SimulationException("Found different to one commutationState in inCommutationState. Period: "
                        + periodScope.getCurrentPeriod() + " Number of Commutation states: " + inCommutationState.size());
            }
        } else {
            throw new SimulationException("Unknown phase: " + phase);
        }
    }

    private void doCashflowChecks(List<ClaimCashflowPacket> claims, List<GrossClaimRoot> grossClaimRoots, List<ClaimRoot> baseClaims) {
        for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {

        }
    }

    private List<ClaimCashflowPacket> cashflowsInCurrentPeriod(List<GrossClaimRoot> grossClaimRoots, List<Factors> runOffFactors, PeriodScope periodScope) {
        List<ClaimCashflowPacket> claimCashflowPackets = new ArrayList<ClaimCashflowPacket>();
        IPeriodCounter counter = this.periodScope.getPeriodCounter();
//                claimCashflowPackets.addAll(grossClaimRoot.getClaimCashflowPackets(periodScope.getPeriodCounter(), null, false));
        for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
            if (grossClaimRoot.exposureStartInCurrentPeriod(periodScope)) {
                claimCashflowPackets.addAll(grossClaimRoot.getClaimCashflowPackets(counter, runOffFactors, !globalTrivialIndices));
                outOccurenceUltimateClaims.add(grossClaimRoot.occurenceCashflow(counter));
            }
        }
        return claimCashflowPackets;
    }


    private void storeClaimsWhichOccurInFuturePeriods(List<GrossClaimRoot> grossClaimRoots, PeriodStore periodStore) {
        List<GrossClaimRoot> claimsNotOcurringThisPeriod = new ArrayList<GrossClaimRoot>();

        for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
            boolean occurrenceInCurrentPeriod = grossClaimRoot.occurrenceInCurrentPeriod(periodScope);
            if (!grossClaimRoot.hasTrivialPayout() || !occurrenceInCurrentPeriod) {
//        add claim only to period store if development in future periods is required or occurrence is delayed
                claimsNotOcurringThisPeriod.add(grossClaimRoot);
            }
        }

        if (!grossClaimRoots.isEmpty()) {
            periodStore.put(AbstractClaimsGenerator.GROSS_CLAIMS, claimsNotOcurringThisPeriod);
        }
    }

    private void checkBaseClaims(List<ClaimRoot> baseClaims) {
        if (globalSanityChecks) {
            for (ClaimRoot baseClaim : baseClaims) {
                if (baseClaim.getUltimate() > 0) {
                    throw new SimulationException("Positive claim detected... i.e an inflow of cash!: " + baseClaim.toString());
                }
                if(iterationScope.isFirstIteration()) {
                    LOG.info("claim root : " + baseClaim.toString());
                }
            }
        }

    }

    /**
     * Do some checks on the claims in this period.
     *
     * @param cashflowPackets
     */

    private void checkCashflowClaims(List<ClaimCashflowPacket> cashflowPackets) {
        if (globalSanityChecks) {
            for (ClaimCashflowPacket cashflowPacket : cashflowPackets) {
                if (cashflowPacket.getPaidIncrementalIndexed() > 0) {
                    throw new SimulationException("Positive claim detected... i.e an inflow of cash!; " + cashflowPacket.toString() + " \n" + "Period Info  " + periodScope.toString());
                }
            }
        }

    }

    /**
     * @param periodStore the period store for this object
     * @param phase
     */
    private void initIteration(PeriodStore periodStore, PeriodScope periodScope, String phase) {
        if (periodScope.isFirstPeriod() && phase.equals(PHASE_CLAIMS_CALCULATION)) {
            periodStore.put(COMMUTATION_STATE, new CommutationState());
        }
    }

    public AttritionalClaimsModel getSubClaimsModel() {
        return subClaimsModel;
    }

    public void setSubClaimsModel(AttritionalClaimsModel subClaimsModel) {
        this.subClaimsModel = subClaimsModel;
    }

    public IReinsuranceContractBaseStrategy getParmParameterizationBasis() {
        return parmParameterizationBasis;
    }

    public void setParmParameterizationBasis(IReinsuranceContractBaseStrategy parmParameterizationBasis) {
        this.parmParameterizationBasis = parmParameterizationBasis;
    }

    public ConstrainedString getParmPayoutPattern() {
        return parmPayoutPattern;
    }

    public void setParmPayoutPattern(ConstrainedString parmPayoutPattern) {
        this.parmPayoutPattern = parmPayoutPattern;
    }

    public ConstrainedMultiDimensionalParameter getParmDeterministicClaims() {
        return parmDeterministicClaims;
    }

    public void setParmDeterministicClaims(ConstrainedMultiDimensionalParameter parmDeterministicClaims) {
        this.parmDeterministicClaims = parmDeterministicClaims;
    }

    public IAggregateActualClaimsStrategy getParmActualClaims() {
        return parmActualClaims;
    }

    public void setParmActualClaims(IAggregateActualClaimsStrategy parmActualClaims) {
        this.parmActualClaims = parmActualClaims;
    }

    public IAggregateUpdatingMethodologyStrategy getParmUpdatingMethodology() {
        return parmUpdatingMethodology;
    }

    public void setParmUpdatingMethodology(IAggregateUpdatingMethodologyStrategy parmUpdatingMethodology) {
        this.parmUpdatingMethodology = parmUpdatingMethodology;
    }


    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }

    public PayoutPatternBase getParmPayoutPatternBase() {
        return parmPayoutPatternBase;
    }

    public void setParmPayoutPatternBase(PayoutPatternBase parmPayoutPatternBase) {
        this.parmPayoutPatternBase = parmPayoutPatternBase;
    }

    public boolean isGlobalTrivialIndices() {
        return globalTrivialIndices;
    }

    public void setGlobalTrivialIndices(boolean globalTrivialIndices) {
        this.globalTrivialIndices = globalTrivialIndices;
    }
}
