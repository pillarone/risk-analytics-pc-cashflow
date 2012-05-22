package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.IClaimsGeneratorStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.IPeriodDependingClaimsGeneratorStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.IExposureBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ISeverityIndexMarker;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier;
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.IVaryingParametersDistributionStrategy;
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This component is a parameter holder only containing all parameters needed for attritional claims generation and
 * containing a utility method providing baseClaims.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsModel extends Component implements IPeriodDependingClaimsGeneratorStrategy {

    private IExposureBaseStrategy parmSeverityBase = ExposureBaseType.getDefault();
    private ConstrainedMultiDimensionalParameter parmSeverityIndices = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER));
    private IVaryingParametersDistributionStrategy parmSeverityDistribution = VaryingParametersDistributionType.getDefault();
    private DistributionModified parmSeverityModification = DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap());


    @Override
    protected void doCalculation() {
        // this is just a parameter container and not a real component!
    }

    /**
     * @param period
     * @return claims generator strategy for period
     */
    public IClaimsGeneratorStrategy claimsModel(int period) {
        // todo: cache to avoid object creation for every period and iteration
        IClaimsGeneratorStrategy claimsGeneratorStrategy = ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL,
                ArrayUtils.toMap(new Object[][]
                        {{"claimsSizeBase", parmSeverityBase.exposureBase()},
                                {"claimsSizeDistribution", parmSeverityDistribution.getDistribution(period)},
                                {"claimsSizeModification", parmSeverityModification}}));
        return claimsGeneratorStrategy;
    }

    /**
     *
     * @param inUnderwritingInfo is combined with parmSeverityBase in order to filter matching underwriting info and calculate the scale factor
     * @param inEventFrequencies ignored for attritional claims
     * @param inEventSeverities
     * @param severityFactors
     * @param filterCriteria for inEventSeverities
     * @param periodScope
     * @return ClaimRoot objects
     */
    public List<ClaimRoot> baseClaims(PacketList<UnderwritingInfoPacket> inUnderwritingInfo,
                                      PacketList<SystematicFrequencyPacket> inEventFrequencies,
                                      PacketList<EventDependenceStream> inEventSeverities,
                                      List<Factors> severityFactors,
                                      IReinsuranceContractBaseStrategy contractBase,
                                      IPerilMarker filterCriteria,
                                      PeriodScope periodScope) {
        int period = periodScope.getCurrentPeriod();
        double scaleFactor = parmSeverityBase.factor(inUnderwritingInfo);
        List<EventSeverity> eventSeverities = ClaimsGeneratorUtils.filterEventSeverities(inEventSeverities, filterCriteria);
        if (!eventSeverities.isEmpty()) {
            List<Double> severities = ClaimsGeneratorUtils.extractSeverities(eventSeverities);
            List<EventPacket> events = ClaimsGeneratorUtils.extractEvents(eventSeverities);
            return claimsModel(period).calculateClaims(scaleFactor, periodScope, severities, events);
        }
        else {
            return claimsModel(period).generateClaims(scaleFactor, severityFactors, 1, periodScope, contractBase);
        }
    }

    public IExposureBaseStrategy getParmSeverityBase() {
        return parmSeverityBase;
    }

    public void setParmSeverityBase(IExposureBaseStrategy parmSeverityBase) {
        this.parmSeverityBase = parmSeverityBase;
    }

    public ConstrainedMultiDimensionalParameter getParmSeverityIndices() {
        return parmSeverityIndices;
    }

    public void setParmSeverityIndices(ConstrainedMultiDimensionalParameter parmSeverityIndices) {
        this.parmSeverityIndices = parmSeverityIndices;
    }

    public IVaryingParametersDistributionStrategy getParmSeverityDistribution() {
        return parmSeverityDistribution;
    }

    public void setParmSeverityDistribution(IVaryingParametersDistributionStrategy parmSeverityDistribution) {
        this.parmSeverityDistribution = parmSeverityDistribution;
    }

    public DistributionModified getParmSeverityModification() {
        return parmSeverityModification;
    }

    public void setParmSeverityModification(DistributionModified parmSeverityModification) {
        this.parmSeverityModification = parmSeverityModification;
    }


}
