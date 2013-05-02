package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationState
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional.AttritionalClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.single.FrequencySeverityClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.single.FrequencySeverityClaimsModel
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IFrequencyIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutPattern
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType

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

    static FrequencySeverityClaimsGenerator getFrequencySeveritySimpleIndexClaimsGenerator(String name, IterationScope iterationScope) {
        FrequencySeverityClaimsGenerator generator = new FrequencySeverityClaimsGenerator(name: name)
        generator.periodScope = iterationScope.periodScope
        generator.periodStore = new PeriodStore(generator.periodScope)
        FrequencySeverityClaimsModel claimsModel = new FrequencySeverityClaimsModel()


        generator.subClaimsModel = claimsModel
        return generator
    }

    public static void doClaimsCalcWithNoCommutation(AbstractClaimsGenerator generator, boolean addPattern = false) {
        if(addPattern) {
            addPayoutPattern(generator)
        }
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState()
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)
    }

    public static void addPayoutPattern (AttritionalClaimsGenerator generator ) {
        PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);
        trivialReportingPattern.origin = new PayoutPattern(name: 'nothing')
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, trivialReportingPattern.origin.name)
        generator.parmPayoutPattern.selectedComponent = trivialReportingPattern.origin
        generator.inPatterns << trivialReportingPattern
    }

    static ClaimsGenerator getFrequencySeveritySimplifiedIndexClaimsGenerator(String name, IterationScope iterationScope, double frequency,
                                                              double claimSize, List<IUnderwritingInfoMarker> riskBands = null,
                                                              FrequencyBase frequencyBase = FrequencyBase.ABSOLUTE,
                                                              ExposureBase claimsSizeBase = ExposureBase.ABSOLUTE,
                                                              FrequencySeverityClaimType claimType = FrequencySeverityClaimType.SINGLE) {
        ClaimsGenerator generator = new ClaimsGenerator(name: name)
        generator.periodScope = iterationScope.periodScope
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.parmClaimsModel = ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY_SIMPLIFIED_INDEX, [
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
