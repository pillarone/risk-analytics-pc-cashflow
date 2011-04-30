package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.core.parameterization.*

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorType extends AbstractParameterObjectClassifier {

//    public static final ClaimsGeneratorType NONE = new ClaimsGeneratorType("none", "NONE", [:])
    public static final ClaimsGeneratorType ATTRITIONAL = new ClaimsGeneratorType("attritional", "ATTRITIONAL", [
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType ATTRITIONAL_WITH_DATE = new ClaimsGeneratorType("attritional with date", "ATTRITIONAL_WITH_DATE", [
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            occurrenceDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0.5d])])
//    public static final ClaimsGeneratorType SEVERITY_OF_EVENT_GENERATOR = new ClaimsGeneratorType("external severity", "SEVERITY_OF_EVENT_GENERATOR", [
//            claimsSizeBase: Exposure.ABSOLUTE,
//            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
//            produceClaim: FrequencySeverityClaimType.AGGREGATED_EVENT])
    public static final ClaimsGeneratorType FREQUENCY_AVERAGE_ATTRITIONAL = new ClaimsGeneratorType("frequency average attritional", "FREQUENCY_AVERAGE_ATTRITIONAL", [
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType FREQUENCY_SEVERITY = new ClaimsGeneratorType("frequency severity", "FREQUENCY_SEVERITY", [
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            produceClaim: FrequencySeverityClaimType.SINGLE])
    public static final ClaimsGeneratorType OCCURRENCE_AND_SEVERITY = new ClaimsGeneratorType("occurrence and severity", "OCCURRENCE_AND_SEVERITY", [
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            occurrenceDistribution: DistributionType.getStrategy(DistributionType.UNIFORM, ["a": 0d, "b": 1d]),
            produceClaim: FrequencySeverityClaimType.SINGLE])
    public static final ClaimsGeneratorType PML = new ClaimsGeneratorType("PML curve", "PML", [
            pmlData: new ConstrainedMultiDimensionalParameter([[0d], [0d]], ["return period", "maximum claim"],
                    ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            produceClaim: FrequencySeverityClaimType.AGGREGATED_EVENT])

    public static final all = [ATTRITIONAL, FREQUENCY_SEVERITY, ATTRITIONAL_WITH_DATE, FREQUENCY_AVERAGE_ATTRITIONAL,
            OCCURRENCE_AND_SEVERITY, /*SEVERITY_OF_EVENT_GENERATOR,*/ PML]

    protected static Map types = [:]
    static {
        ClaimsGeneratorType.all.each {
            ClaimsGeneratorType.types[it.toString()] = it
        }
    }

    private ClaimsGeneratorType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ClaimsGeneratorType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IClaimsGeneratorStrategy getDefault() {
        return new AttritionalClaimsGeneratorStrategy(
                claimsSizeBase: ExposureBase.ABSOLUTE,
                claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d]),
                claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap())
        )
    }

    static IClaimsGeneratorStrategy getStrategy(ClaimsGeneratorType type, Map parameters) {
        IClaimsGeneratorStrategy claimsGenerator;
        switch (type) {
//            case ClaimsGeneratorType.NONE:
//                claimsGenerator = new NoneClaimsGeneratorStrategy()
//                break;
            case ClaimsGeneratorType.ATTRITIONAL:
                claimsGenerator = new AttritionalClaimsGeneratorStrategy(
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"))
                break;
            case ClaimsGeneratorType.ATTRITIONAL_WITH_DATE:
                claimsGenerator = new OccurrenceAttritionalClaimsGeneratorStrategy(
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        occurrenceDistribution: (RandomDistribution) parameters.get("occurrenceDistribution"))
                break;
            case ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL:
                claimsGenerator = new FrequencyAverageAttritionalClaimsGeneratorStrategy(
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"))
                break;
            case ClaimsGeneratorType.FREQUENCY_SEVERITY:
                claimsGenerator = new FrequencySeverityClaimsGeneratorStrategy(
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
                break;
            case ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY:
                claimsGenerator = new OccurrenceFrequencySeverityClaimsGeneratorStrategy(
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        occurrenceDistribution: (RandomDistribution) parameters.get("occurrenceDistribution"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
                break;
//            case ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR:
//                claimsGenerator = new ExternalSeverityClaimsGeneratorStrategy(
//                        claimsSizeBase: (Exposure) parameters.get("claimsSizeBase"),
//                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
//                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
//                break;
            case ClaimsGeneratorType.PML:
                claimsGenerator = new PMLClaimsGeneratorStrategy(
                        pmlData: (ConstrainedMultiDimensionalParameter) parameters.get("pmlData"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        produceClaim : (FrequencySeverityClaimType) parameters.get("produceClaim"))
        }
        return claimsGenerator;
    }

}