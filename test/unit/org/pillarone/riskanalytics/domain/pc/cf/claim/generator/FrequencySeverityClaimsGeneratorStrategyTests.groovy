package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class FrequencySeverityClaimsGeneratorStrategyTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    ClaimsGenerator claimsGenerator
    RiskBands riskBands = new RiskBands()
    RiskBands riskBands2 = new RiskBands()

    void setUp() {

        claimsGenerator = new ClaimsGenerator()
        claimsGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        claimsGenerator.periodStore = new PeriodStore(claimsGenerator.periodScope)
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
    }



    void testFrequencySeverity() {

        claimsGenerator.doCalculation()

        assertEquals "two claims", 2, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[1].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[1].baseClaim.event == null
        assertEquals "occurrence date ", true,
                claimsGenerator.outClaims[1].getDate() == claimsGenerator.outClaims[1].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.doCalculation()

        assertEquals "two claims", 2, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[1].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[1].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[1].baseClaim.event.getDate() == claimsGenerator.outClaims[1].occurrenceDate
        assertEquals "occurrence date ", true,
                claimsGenerator.outClaims[1].getDate() == claimsGenerator.outClaims[1].occurrenceDate
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[1].baseClaim.event.getDate() == claimsGenerator.outClaims[1].baseClaim.occurrenceDate
    }

    void testFrequencySeverityRelativeClaims() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.NUMBER_OF_POLICIES,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "number of claims", 40, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123000, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123000, claimsGenerator.outClaims[39].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[0].baseClaim.event.getDate() == claimsGenerator.outClaims[0].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.NUMBER_OF_POLICIES,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.NUMBER_OF_POLICIES,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "number of claims", 40, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123 * 20, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123 * 20, claimsGenerator.outClaims[39].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[0].baseClaim.event.getDate() == claimsGenerator.outClaims[0].occurrenceDate
    }

    void testOccurrenceAndSeverity() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.doCalculation()

        assertEquals "number claims", 2, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[1].ultimate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[0].getDate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].getDate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.doCalculation()

        assertEquals "number claims", 2, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[1].ultimate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[0].getDate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].getDate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].baseClaim.event.getDate()
        assertEquals "occurrence date equals event date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].baseClaim.occurrenceDate
        assertEquals "occurrence date equals event date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].occurrenceDate
    }

    void testOccurrenceAndSeverityRelativeClaims() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.NUMBER_OF_POLICIES,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "number of claims", 40, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123000, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123000, claimsGenerator.outClaims[39].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[0].baseClaim.event.getDate() == claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].getDate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].baseClaim.occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.NUMBER_OF_POLICIES,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.NUMBER_OF_POLICIES,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "number of claims", 40, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123 * 20, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123 * 20, claimsGenerator.outClaims[39].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[0].baseClaim.event.getDate() == claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].getDate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].baseClaim.occurrenceDate
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].baseClaim.event.getDate()
        assertEquals "correct date", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[1].occurrenceDate
    }

    /* void testExternalSeverity() {
   claimsGenerator = new ClaimsGenerator()
   ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
           ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
   uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
   claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
   claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
           ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR, [
                   //TODO(2): FrequencyBase.NUMBER_OF_POLICIES with Freq>1
                   "claimsSizeBase": ExposureBase.ABSOLUTE, //TODO(1): PREMIUM_WRITTEN for AttritionalCG
                   "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, ['a': 0d, 'b': 1d]),
                   "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
   claimsGenerator.setParmAssociateExposureBaseInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
   claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
   //TODO(): Test inUnderwritingInfo & inProbabilities (for Attritional) analogously to inEventSeverities
   EventSeverity eventSeverity = new EventSeverity()
   eventSeverity.event = new Event(fractionOfPeriod: 0.3d)
   eventSeverity.value = 0.7d
   EventDependenceStream events = new EventDependenceStream()
   events.severities = [eventSeverity]
   events.marginals = ['motor']
   claimsGenerator.name = 'motor'
   claimsGenerator.inEventSeverities << events
   def channelWired = new TestPretendInChannelWired(claimsGenerator, "inEventSeverities")
   claimsGenerator.doCalculation()

   assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
   assertEquals "correct value of claim", 0.7, claimsGenerator.outClaims[0].ultimate
}

void testExternalSeverityAndUnderwritingInfo() {
   claimsGenerator = new ClaimsGenerator()
   ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
           ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
   uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
   claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
   claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
           ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR, [
                   "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                   "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, ['a': 0d, 'b': 1d]),
                   "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
   claimsGenerator.setParmAssociateExposureBaseInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
   claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
   // wire an external severity
   EventSeverity eventSeverity = new EventSeverity()
   eventSeverity.event = new Event(fractionOfPeriod: 0.3d)
   eventSeverity.value = 0.7d
   EventDependenceStream events = new EventDependenceStream()
   events.severities = [eventSeverity]
   events.marginals = ['motor']
   claimsGenerator.name = 'motor'
   claimsGenerator.inEventSeverities << events
   def channelWired = new TestPretendInChannelWired(claimsGenerator, "inEventSeverities")
   // wire underwriting info
   UnderwritingInfo underwritingInfo = new UnderwritingInfo(premium: 1000d)
   //underwritingInfo.originalUnderwritingInfo = underwritingInfo
   claimsGenerator.inUnderwritingInfo << underwritingInfo
   claimsGenerator.doCalculation()

   assertEquals "one single claim (premium written)", 1, claimsGenerator.outClaims.size()
   assertEquals "correct value of claim (premium written)", 700d, claimsGenerator.outClaims[0].ultimate

   claimsGenerator.outClaims.clear()
   claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
           ClaimsGeneratorType.SEVERITY_OF_EVENT_GENERATOR, [
                   "claimsSizeBase": ExposureBase.ABSOLUTE,
                   "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, ['a': 0d, 'b': 1d]),
                   "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])
   claimsGenerator.doCalculation()

   assertEquals "one single claim (absolute)", 1, claimsGenerator.outClaims.size()
   assertEquals "correct value of claim (absolute)", 0.7d, claimsGenerator.outClaims[0].ultimate
}



void testAttritionalWithInProbability() {
   claimsGenerator = new ClaimsGenerator()
   ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
           ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
   uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
   claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
   claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
           ClaimsGeneratorType.ATTRITIONAL, [
                   "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                   "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                   "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
   claimsGenerator.setParmAssociateExposureBaseInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
   claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
   // wire one inProbability
   DependenceStream probabilities = new DependenceStream()
   probabilities.probabilities = [1.0d]
   probabilities.marginals = ['motor']
   claimsGenerator.name = 'motor'
   claimsGenerator.inProbabilities << probabilities
   def channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
   claimsGenerator.doCalculation()

   assertEquals "one attritional claim (p=1)", 1, claimsGenerator.outClaims.size()
   assertEquals "correct attritional claim size (p=1)", 123, claimsGenerator.outClaims[0].ultimate

   claimsGenerator.outClaims.clear()
   claimsGenerator.inProbabilities.clear()
   claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
           ClaimsGeneratorType.ATTRITIONAL, [
                   "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                   //"claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                   "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0d, b: 1d]),
                   "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
   probabilities.probabilities = [0.578d]
   probabilities.marginals = ['motor']
   claimsGenerator.name = 'motor'
   claimsGenerator.inProbabilities << probabilities
   channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
   claimsGenerator.doCalculation()

   /*
    * The result is the same with probability 0, because a claimsGenerator MUST provide at least one
    * claim for each iteration & period; otherwise, the key statistical figures would be incorrect.
    */
    //     assertEquals "one attritional claim (p=0)", 1, claimsGenerator.outClaims.size()
    /*
    * We would need to use a uniform distribution to see an effect.
    */
    /*   assertEquals "correct attritional claim size (p=0)", 0.578, claimsGenerator.outClaims[0].ultimate
}

void testAttritionalWithTwoInProbabilities() {
   claimsGenerator = new ClaimsGenerator()
   ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
           ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
   uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
   claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
   claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
           ClaimsGeneratorType.ATTRITIONAL, [
                   "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                   "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                   "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
   claimsGenerator.setParmAssociateExposureBaseInfo(RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:]))
   claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel()))
   // wire a first inProbability
   DependenceStream probabilities1 = new DependenceStream()
   probabilities1.probabilities = [0d]
   probabilities1.marginals = ['motors']
   // wire a second inProbability
   DependenceStream probabilities2 = new DependenceStream()
   probabilities2.probabilities = [1d]
   probabilities2.marginals = ['motors']
   claimsGenerator.name = 'motors'
   claimsGenerator.inProbabilities << probabilities1 << probabilities2
   /*
    * Note that the names (probabilities*.marginals[0] & claimsGenerator.name) must all match
    * in order for both inProbabilites to pass through the filter filterProbabilities() and
    * generate the expected error.
    */
    /*     def channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
          channelWired = new TestPretendInChannelWired(claimsGenerator, "inProbabilities")
          shouldFail(IllegalArgumentException, { claimsGenerator.doCalculation() })
      }


      void testPartitionFunctionForTruncatedDensity() {
          Distribution dist = DistributionType.getLognormalDistribution(1.0, 1.0)
          double partitionFunction = dist.cdf(105000) - dist.cdf(95000)
          assertEquals "partition function on truncated interval", 0.0, partitionFunction


          Distribution distTruncated = new TruncatedDist((ContinuousDistribution) dist, (Double) 95000, (Double) 105000)
          assertEquals " density left from interval", 0d, distTruncated.density(94000)
          assertEquals " density within interval", Double.POSITIVE_INFINITY, distTruncated.density(100000)
          assertEquals " density right from interval", 0d, distTruncated.density(106000)
          assertEquals " inverse F(0.9)", Double.POSITIVE_INFINITY, distTruncated.inverseF(0.9)
          assertEquals " cumulative distribution of 1000000", Double.NaN, distTruncated.cdf(100000)

          Distribution dist2 = DistributionType.getLognormalDistribution(1.0, 1.0)
          double partitionFunction2 = dist2.cdf(5) - dist2.cdf(2)
          assertEquals "partition function on truncated interval", true, partitionFunction2 > 0 && partitionFunction2 < Double.POSITIVE_INFINITY

          Distribution dist2Truncated = new TruncatedDist((ContinuousDistribution) dist2, 2d, 5d)
          assertEquals " density left from interval", 0d, dist2Truncated.density(1)
          assertEquals " density at left boundary", dist2.density(2) / partitionFunction2, dist2Truncated.density(2)
          assertEquals " density within interval", dist2.density(3) / partitionFunction2, dist2Truncated.density(3)
          assertEquals " density at right boundary", dist2.density(5) / partitionFunction2, dist2Truncated.density(5)
          assertEquals " density right from interval", 0d, dist2Truncated.density(6)
          assertEquals " inverse F(0.9)", true, dist2Truncated.inverseF(0.9) > 2 && dist2Truncated.inverseF(0.9) < 5
      }


    */
}
