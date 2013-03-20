package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.single;

import org.apache.commons.lang.ArrayUtils;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.IClaimsGeneratorStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.IExposureBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.*;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier;
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.IVaryingParametersDistributionStrategy;
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FrequencySeverityClaimsModel extends Component {

//    private IExposureBaseStrategy parmFrequencyBase = ExposureBaseType.getDefault();
    private ComboBoxTableMultiDimensionalParameter parmFrequencyIndices = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Frequency Index"), IFrequencyIndexMarker.class);

    private IVaryingParametersDistributionStrategy parmFrequencyDistribution = VaryingParametersDistributionType.getDefault();
    private DistributionModified parmFrequencyModification = DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap());

    private IExposureBaseStrategy parmSeverityBase = ExposureBaseType.getDefault();
    private ComboBoxTableMultiDimensionalParameter parmSeverityIndices = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Severity Index"), ISeverityIndexMarker.class);
    private IVaryingParametersDistributionStrategy parmSeverityDistribution = VaryingParametersDistributionType.getDefault();
    private DistributionModified parmSeverityModification = DistributionModifier.getStrategy(DistributionModifier.NONE,Collections.emptyMap());

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
        IClaimsGeneratorStrategy claimsGeneratorStrategy = ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY_SIMPLIFIED_INDEX,
                ArrayUtils.toMap(new Object[][]
                               {{"frequencyIndices", parmFrequencyIndices},
//                                {"frequencyBase", parmFrequencyBase},
                                {"frequencyDistribution", parmFrequencyDistribution.getDistribution(period)},
                                {"frequencyModification", parmFrequencyModification},
                                {"claimsSizeBase", parmSeverityBase.exposureBase()},
                                {"claimsSizeDistribution", parmSeverityDistribution.getDistribution(period)},
                                {"claimsSizeModification", parmSeverityModification},
                                {"produceClaim", FrequencySeverityClaimType.SINGLE}}));
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
                                      PeriodScope periodScope,
                                      List<DependancePacket> dependancePackets) {
        int period = periodScope.getCurrentPeriod();
        double scaleFactor = parmSeverityBase.factor(inUnderwritingInfo);
        List<EventSeverity> eventSeverities = ClaimsGeneratorUtils.filterEventSeverities(inEventSeverities, filterCriteria);
        DependancePacket dependancePacket = ClaimUtils.checkForDependance(filterCriteria, dependancePackets);
        if ( dependancePacket.isDependantGenerator(filterCriteria) ) {
            throw new SimulationException("Freq severity dependance not implemented");
//            return claimsModel(period).calculateClaims(-scaleFactor, periodScope, eventSeverities);
        }
        else {
//            return claimsModel(period).generateClaims(-scaleFactor, severityFactors, 1, periodScope, contractBase);
            List<ClaimRoot> baseClaims = claimsModel(period).calculateClaims(-scaleFactor, periodScope, eventSeverities);
            baseClaims = claimsModel(period).generateClaims(baseClaims, inUnderwritingInfo, severityFactors, Collections.emptyList(), new ArrayList<FactorsPacket>(), periodScope, inEventFrequencies, filterCriteria);
            List<ClaimRoot> baseClaimsCorrectedSign = new ArrayList<ClaimRoot>();
            for (ClaimRoot claim : baseClaims) {
                baseClaimsCorrectedSign.add(new ClaimRoot(-claim.getUltimate(), claim));
            }
            return baseClaimsCorrectedSign;
        }
    }

    public IVaryingParametersDistributionStrategy getParmFrequencyDistribution() {
        return parmFrequencyDistribution;
    }

    public void setParmFrequencyDistribution(IVaryingParametersDistributionStrategy parmFrequencyDistribution) {
        this.parmFrequencyDistribution = parmFrequencyDistribution;
    }

    public DistributionModified getParmFrequencyModification() {
        return parmFrequencyModification;
    }

    public void setParmFrequencyModification(DistributionModified parmFrequencyModification) {
        this.parmFrequencyModification = parmFrequencyModification;
    }

    public IExposureBaseStrategy getParmSeverityBase() {
        return parmSeverityBase;
    }

    public void setParmSeverityBase(IExposureBaseStrategy parmSeverityBase) {
        this.parmSeverityBase = parmSeverityBase;
    }

    public ComboBoxTableMultiDimensionalParameter getParmFrequencyIndices() {
        return parmFrequencyIndices;
    }

    public void setParmFrequencyIndices(ComboBoxTableMultiDimensionalParameter parmFrequencyIndices) {
        this.parmFrequencyIndices = parmFrequencyIndices;
    }

    public ComboBoxTableMultiDimensionalParameter getParmSeverityIndices() {
        return parmSeverityIndices;
    }

    public void setParmSeverityIndices(ComboBoxTableMultiDimensionalParameter parmSeverityIndices) {
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
