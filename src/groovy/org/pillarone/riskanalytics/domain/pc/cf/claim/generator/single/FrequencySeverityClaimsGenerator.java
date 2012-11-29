package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.single;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.ReinsuranceContractBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single.ISingleActualClaimsStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single.ISingleUpdatingMethodologyStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single.SingleActualClaimsStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single.SingleUpdatingMethodologyStrategyType;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencySeverityClaimsGenerator extends AbstractClaimsGenerator {

    static Log LOG = LogFactory.getLog(FrequencySeverityClaimsGenerator.class);

    private FrequencySeverityClaimsModel subClaimsModel = new FrequencySeverityClaimsModel();
    private IReinsuranceContractBaseStrategy parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(
            ReinsuranceContractBaseType.PLEASESELECT, new HashMap());
    private ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker.class, "");
    private PayoutPatternBase parmPayoutPatternBase = PayoutPatternBase.PERIOD_START_DATE;
    private ISingleActualClaimsStrategy parmActualClaims = SingleActualClaimsStrategyType.getDefault();
    private ISingleUpdatingMethodologyStrategy parmUpdatingMethodology = SingleUpdatingMethodologyStrategyType.getDefault();

    private ConstrainedMultiDimensionalParameter parmDeterministicClaims = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d}), Arrays.asList(REAL_PERIOD, CLAIM_VALUE),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));

    /* Injected from framework */


    protected void doCalculation() {
        try {
//            if (provideClaims(phase)) {
            // A deal may commute before the end of the contract period. We may hence want to terminate claims generation
            // Depending on the outcome in the experience account.
            initIteration(periodStore, periodScope, PHASE_CLAIMS_CALCULATION);
            IPeriodCounter periodCounter = periodScope.getPeriodCounter();
            List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();

            List<Factors> runoffFactors = null;
            // Check that the period we are in covers new claims.
            if (periodScope.getCurrentPeriod() < globalLastCoveredPeriod) {
                List<ClaimRoot> baseClaims;
                if (globalDeterministicMode) {
                    baseClaims = getDeterministicClaims(parmDeterministicClaims, periodScope, ClaimType.SINGLE);
                }
                else {
                    List<Factors> severityFactors = IndexUtils.filterFactors(inFactors, subClaimsModel.getParmSeverityIndices(),
                            IndexMode.STEPWISE_PREVIOUS, BaseDateMode.START_OF_PROJECTION, null);
                    baseClaims = subClaimsModel.baseClaims(inUnderwritingInfo, inEventFrequencies, inEventSeverities,
                            severityFactors, parmParameterizationBasis, this, periodScope);
                }
                checkBaseClaims(baseClaims, globalSanityChecks, iterationScope);
                PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern, IPayoutPatternMarker.class);
                List<GrossClaimRoot> grossClaimRoots = parmUpdatingMethodology.updatingClaims(baseClaims, parmActualClaims,
                        periodCounter, globalUpdateDate, inPatterns, periodScope.getCurrentPeriod(), DAYS_360,
                        parmPayoutPatternBase, payoutPattern, globalSanityChecks);
                runoffFactors = new ArrayList<Factors>();
                storeClaimsWhichOccurInFuturePeriods(grossClaimRoots, periodStore);
                claims = cashflowsInCurrentPeriod(grossClaimRoots, runoffFactors, periodScope);
            }
            developClaimsOfFormerPeriods(claims, periodCounter, runoffFactors);
            checkCashflowClaims(claims, globalSanityChecks);
            setTechnicalProperties(claims);
            outClaims.addAll(claims);
//            }
//            else {
//                prepareProvidingClaimsInNextPeriodOrNot(phase);
//            }
        }
        catch (SimulationException e) {
            throw new SimulationException("Problem in claims generator in Iteration : "
                    + iterationScope.getCurrentIteration() + ". Period :" + periodScope.getCurrentPeriod()
                    + " with seed : " + simulationScope.getSimulation().getRandomSeed().toString()
                    +  "\n \n " + e.getMessage(), e);
        }
    }

    public FrequencySeverityClaimsModel getSubClaimsModel() {
        return subClaimsModel;
    }

    public void setSubClaimsModel(FrequencySeverityClaimsModel subClaimsModel) {
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

    public PayoutPatternBase getParmPayoutPatternBase() {
        return parmPayoutPatternBase;
    }

    public void setParmPayoutPatternBase(PayoutPatternBase parmPayoutPatternBase) {
        this.parmPayoutPatternBase = parmPayoutPatternBase;
    }

    public ISingleActualClaimsStrategy getParmActualClaims() {
        return parmActualClaims;
    }

    public void setParmActualClaims(ISingleActualClaimsStrategy parmActualClaims) {
        this.parmActualClaims = parmActualClaims;
    }

    public ISingleUpdatingMethodologyStrategy getParmUpdatingMethodology() {
        return parmUpdatingMethodology;
    }

    public void setParmUpdatingMethodology(ISingleUpdatingMethodologyStrategy parmUpdatingMethodology) {
        this.parmUpdatingMethodology = parmUpdatingMethodology;
    }

    public ConstrainedMultiDimensionalParameter getParmDeterministicClaims() {
        return parmDeterministicClaims;
    }

    public void setParmDeterministicClaims(ConstrainedMultiDimensionalParameter parmDeterministicClaims) {
        this.parmDeterministicClaims = parmDeterministicClaims;
    }


}


