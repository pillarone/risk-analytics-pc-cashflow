package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.ReinsuranceContractBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
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
    private IAggregateActualClaimsStrategy parmActualClaims = AggregateActualClaimsStrategyType.getDefault();
    private IAggregateUpdatingMethodologyStrategy parmUpdatingMethodology = AggregateUpdatingMethodologyStrategyType.getDefault();
    private ConstrainedMultiDimensionalParameter parmDeterministicClaims = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.convertToListOfList(new Object[]{0d, 0d}), Arrays.asList(REAL_PERIOD, CLAIM_VALUE),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));


    @Override
    protected void doCalculation() {
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();
        List<ClaimRoot> baseClaims;
        List<Factors> severityFactors = new ArrayList<Factors>();
        if (globalDeterministicMode) {
            baseClaims = getDeterministicClaims(parmDeterministicClaims, periodScope, ClaimType.ATTRITIONAL);
        }
        else {
            baseClaims = subClaimsModel.baseClaims(inUnderwritingInfo, inEventFrequencies, inEventSeverities,
                inFactors, parmParameterizationBasis, this, periodScope);
            severityFactors = IndexUtils.filterFactors(inFactors, subClaimsModel.getParmSeverityIndices());
        }
        baseClaims = parmUpdatingMethodology.updatingUltimate(baseClaims, parmActualClaims, periodCounter, globalUpdateDate, inPatterns);
        List<ClaimCashflowPacket> claims = claimsOfCurrentPeriod(baseClaims, parmPayoutPattern, parmActualClaims,
                periodScope, severityFactors);
        developClaimsOfFormerPeriods(claims, periodCounter, severityFactors);
        setTechnicalProperties(claims);
        outClaims.addAll(claims);
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
}
