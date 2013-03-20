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
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IFrequencyIndexMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGeneratorType extends AbstractParameterObjectClassifier {

    public static final ClaimsGeneratorType ATTRITIONAL = new ClaimsGeneratorType("attritional", "ATTRITIONAL", [
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType ATTRITIONAL_WITH_DATE = new ClaimsGeneratorType("attritional with date", "ATTRITIONAL_WITH_DATE", [
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            occurrenceDateDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0.5d])])
    public static final ClaimsGeneratorType FREQUENCY_AVERAGE_ATTRITIONAL = new ClaimsGeneratorType("frequency average attritional", "FREQUENCY_AVERAGE_ATTRITIONAL", [
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType FREQUENCY_SEVERITY = new ClaimsGeneratorType("frequency severity", "FREQUENCY_SEVERITY", [
            frequencyIndices: new ConstrainedMultiDimensionalParameter(
                    Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            produceClaim: FrequencySeverityClaimType.SINGLE])
    public static final ClaimsGeneratorType FREQUENCY_SEVERITY_SIMPLIFIED_INDEX = new ClaimsGeneratorType("frequency severity", "FREQUENCY_SEVERITY", [
            frequencyIndices: new ComboBoxTableMultiDimensionalParameter([''], ['Frequency Index'], IFrequencyIndexMarker),
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            produceClaim: FrequencySeverityClaimType.SINGLE])
    public static final ClaimsGeneratorType OCCURRENCE_AND_SEVERITY = new ClaimsGeneratorType("occurrence and severity", "OCCURRENCE_AND_SEVERITY", [
            frequencyIndices: new ConstrainedMultiDimensionalParameter(
                    Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
            frequencyBase: FrequencyBase.ABSOLUTE,
            frequencyDistribution: FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ["constant": 0d]),
            frequencyModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
            occurrenceDateDistribution: DistributionType.getStrategy(DistributionType.UNIFORM, ["a": 0d, "b": 1d]),
            produceClaim: FrequencySeverityClaimType.SINGLE])
    public static final ClaimsGeneratorType PML = new ClaimsGeneratorType("PML curve", "PML", [
            claimsSizeBase: ExposureBase.ABSOLUTE,
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
                        occurrenceDateDistribution: (RandomDistribution) parameters.get("occurrenceDateDistribution"))
                break;
            case ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL:
                claimsGenerator = new FrequencyAverageAttritionalClaimsGeneratorStrategy(
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomFrequencyDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"))
                break;
            case ClaimsGeneratorType.FREQUENCY_SEVERITY:
                claimsGenerator = new FrequencySeverityClaimsGeneratorStrategy(
                        frequencyIndices: (ConstrainedMultiDimensionalParameter) parameters.get("frequencyIndices"),
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomFrequencyDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
                break;
            case ClaimsGeneratorType.FREQUENCY_SEVERITY_SIMPLIFIED_INDEX:
                return new FrequencySeverityClaimsGeneratorSimplifiedIndexStrategy(
                        frequencyIndices: (ComboBoxTableMultiDimensionalParameter) parameters.get("frequencyIndices"),
                        // todo: apply scaling
                        frequencyBase: ExposureBase.ABSOLUTE,
                        frequencyDistribution: (RandomDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
                break;
            case ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY:
                claimsGenerator = new OccurrenceFrequencySeverityClaimsGeneratorStrategy(
                        frequencyIndices: (ConstrainedMultiDimensionalParameter) parameters.get("frequencyIndices"),
                        frequencyBase: (FrequencyBase) parameters.get("frequencyBase"),
                        frequencyDistribution: (RandomFrequencyDistribution) parameters.get("frequencyDistribution"),
                        frequencyModification: (DistributionModified) parameters.get("frequencyModification"),
                        claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        occurrenceDateDistribution: (RandomDistribution) parameters.get("occurrenceDateDistribution"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
                break;
            case ClaimsGeneratorType.PML:
                claimsGenerator = new PMLClaimsGeneratorStrategy(
                       claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                        pmlData: (ConstrainedMultiDimensionalParameter) parameters.get("pmlData"),
                        claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"),
                        produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim"))
        }
        return claimsGenerator;
    }

}