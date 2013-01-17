package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class TestClaimsGenerator {

    static ClaimsGenerator getAttritionalClaimsGenerator(String name, IterationScope iterationScope, double constant) {
        ClaimsGenerator generator = new ClaimsGenerator(name: name)
        generator.periodScope = iterationScope.periodScope
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.parmClaimsModel = ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                "claimsSizeBase": ExposureBase.ABSOLUTE,
                "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: constant]),
                "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        return generator
    }

    static ClaimsGenerator getFrequencySeverityClaimsGenerator(String name, IterationScope iterationScope, double frequency,
                                                              double claimSize, List<IUnderwritingInfoMarker> riskBands = null,
                                                              FrequencyBase frequencyBase = FrequencyBase.ABSOLUTE,
                                                              ExposureBase claimsSizeBase = ExposureBase.ABSOLUTE,
                                                              FrequencySeverityClaimType claimType = FrequencySeverityClaimType.SINGLE) {
        ClaimsGenerator generator = new ClaimsGenerator(name: name)
        generator.periodScope = iterationScope.periodScope
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.parmClaimsModel = ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                        [], FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                        ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                "frequencyBase": frequencyBase,
                "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: frequency]),
                "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                "claimsSizeBase": claimsSizeBase,
                "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: claimSize]),
                "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                "produceClaim": claimType])

        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                [riskBands*.name], ["Underwriting Information"], IUnderwritingInfoMarker)
        for (IUnderwritingInfoMarker riskBand : riskBands) {
            uwInfoComboBox.comboBoxValues[riskBand.name] = riskBand
        }
        generator.parmUnderwritingSegments = uwInfoComboBox

        return generator
    }

    static ClaimsGenerator getOccurenceAndSeverityClaimsGenerator(String name, IterationScope iterationScope, double frequency,
                                                              double claimSize, List<IUnderwritingInfoMarker> riskBands = null,
                                                              FrequencyBase frequencyBase = FrequencyBase.ABSOLUTE,
                                                              ExposureBase claimsSizeBase = ExposureBase.ABSOLUTE,
                                                              FrequencySeverityClaimType claimType = FrequencySeverityClaimType.SINGLE,
                                                              RandomDistribution occurrenceDateDistribution = DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d])) {
        ClaimsGenerator generator = new ClaimsGenerator(name: name)
        generator.periodScope = iterationScope.periodScope
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.parmClaimsModel = ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                        [], FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                        ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                "frequencyBase": frequencyBase,
                'occurrenceDateDistribution' : occurrenceDateDistribution,
                "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: frequency]),
                "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                "claimsSizeBase": claimsSizeBase,
                "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: claimSize]),
                "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                "produceClaim": claimType])

        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                [riskBands*.name], ["Underwriting Information"], IUnderwritingInfoMarker)
        for (IUnderwritingInfoMarker riskBand : riskBands) {
            uwInfoComboBox.comboBoxValues[riskBand.name] = riskBand
        }
        generator.parmUnderwritingSegments = uwInfoComboBox

        return generator
    }
}
