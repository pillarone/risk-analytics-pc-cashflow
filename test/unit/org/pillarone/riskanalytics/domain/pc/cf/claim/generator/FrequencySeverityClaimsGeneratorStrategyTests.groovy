package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
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
import umontreal.iro.lecuyer.probdist.Distribution
import umontreal.iro.lecuyer.probdist.TruncatedDist
import umontreal.iro.lecuyer.probdist.ContinuousDistribution
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomFrequencyDistribution

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class FrequencySeverityClaimsGeneratorStrategyTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    ClaimsGenerator claimsGenerator
    RiskBands riskBands = new RiskBands()
    RiskBands riskBands2 = new RiskBands()

    List<String> targets
    List<EventSeverity> severities1
    List<EventSeverity> severities2
    RandomFrequencyDistribution systematicFrequency1
    RandomFrequencyDistribution systematicFrequency2

    void setUp() {

        claimsGenerator = new ClaimsGenerator(name: "motor hull")
        claimsGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        claimsGenerator.periodStore = new PeriodStore(claimsGenerator.periodScope)
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        claimsGenerator.setParmUnderwritingSegments(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])
        ConstraintsFactory.registerConstraint(new DoubleConstraints())

        EventSeverity severity1 = new EventSeverity(value: 0.8, event: new EventPacket(new DateTime(2011, 1, 2, 0, 0, 0, 0)))
        EventSeverity severity2 = new EventSeverity(value: 0.9, event: new EventPacket(new DateTime(2011, 2, 2, 0, 0, 0, 0)))
        EventSeverity severity3 = new EventSeverity(value: 0.95, event: new EventPacket(new DateTime(2011, 3, 2, 0, 0, 0, 0)))
        targets = new ArrayList<String>(["motor hull", "hail"])
        severities1 = new ArrayList<EventSeverity>([severity1, severity2])
        severities2 = new ArrayList<EventSeverity>([severity3, severity2])
        systematicFrequency1 = FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ['constant': 2d])
        systematicFrequency2 = FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, ['constant': 1d])
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
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
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
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000.5, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "number of claims", 40, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123*1000.5, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123*1000.5, claimsGenerator.outClaims[39].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[0].baseClaim.event.getDate() == claimsGenerator.outClaims[0].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.PREMIUM_WRITTEN,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "number of claims", 2000, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123*1000.5, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct value of claim", -123*1000.5, claimsGenerator.outClaims[1999].ultimate()
        assertEquals "event one", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "occurrence date equals event date", true,
                claimsGenerator.outClaims[0].baseClaim.event.getDate() == claimsGenerator.outClaims[0].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.NUMBER_OF_POLICIES,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2d]),
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

    void testFrequencySeveritySystematicSeverities() {

        EventDependenceStream stream1 = new EventDependenceStream(targets, severities1)
        EventDependenceStream stream2 = new EventDependenceStream(targets, severities2)
        SystematicFrequencyPacket sysFrequencyPacket1 = new SystematicFrequencyPacket(targets: targets, frequencyDistribution: systematicFrequency1)

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 12]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 2, claimsGenerator.outClaims.size()
        assertEquals "claim value", -0.8 * 12, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -0.95 * 12, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 4d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1

        claimsGenerator.doCalculation()

        assertEquals "claim number", 4, claimsGenerator.outClaims.size()
        assertEquals "claim value", -120, claimsGenerator.outClaims[0].ultimate()
        assertEquals "claim value", -120, claimsGenerator.outClaims[1].ultimate()
        assertEquals "claim value", -120, claimsGenerator.outClaims[2].ultimate()
        assertEquals "claim value", -120, claimsGenerator.outClaims[3].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "no event", true, claimsGenerator.outClaims[3].baseClaim.event != null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.event.getDate()
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].baseClaim.event.getDate()

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 7.5]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(sumInsured: 2500000.5, premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 2, claimsGenerator.outClaims.size()
        assertEquals "claim value", -0.8 * 7.5 * 1000, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -0.95 * 7.5 * 1000, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate


        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 3d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 3, claimsGenerator.outClaims.size()
        assertEquals "claim value", -120 * 1000, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -120 * 1000, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "claim value", -120 * 1000, claimsGenerator.outClaims[2].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 3d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.SUM_INSURED,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 3, claimsGenerator.outClaims.size()
        assertEquals "claim value", -120 * 2500000.5, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -120 * 2500000.5, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "claim value", -120 * 2500000.5, claimsGenerator.outClaims[2].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate

    }

    void testOccurrenceAndSeverity() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
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
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
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
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
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
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.NUMBER_OF_POLICIES,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
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

    void testOccurrenceAndSeveritySystematicSeverities() {

        EventDependenceStream stream1 = new EventDependenceStream(targets, severities1)
        EventDependenceStream stream2 = new EventDependenceStream(targets, severities2)
        SystematicFrequencyPacket sysFrequencyPacket1 = new SystematicFrequencyPacket(targets: targets, frequencyDistribution: systematicFrequency1)

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2d]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 12]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 2, claimsGenerator.outClaims.size()
        assertEquals "claim value", -0.8 * 12, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -0.95 * 12, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 4d]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.AGGREGATED_EVENT,])

        claimsGenerator.reset()
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1

        claimsGenerator.doCalculation()

        assertEquals "claim number", 4, claimsGenerator.outClaims.size()
        assertEquals "claim value", -120, claimsGenerator.outClaims[0].ultimate()
        assertEquals "claim value", -120, claimsGenerator.outClaims[1].ultimate()
        assertEquals "claim value", -120, claimsGenerator.outClaims[2].ultimate()
        assertEquals "claim value", -120, claimsGenerator.outClaims[3].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event != null
        assertEquals "no event", true, claimsGenerator.outClaims[3].baseClaim.event != null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.event.getDate()
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].baseClaim.event.getDate()
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].getDate()
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].occurrenceDate
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].baseClaim.occurrenceDate
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].baseClaim.event.getDate()
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[3].occurrenceDate
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[3].baseClaim.event.getDate()

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 2d]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 7.5]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 2, claimsGenerator.outClaims.size()
        assertEquals "claim value", -0.8 * 7.5 * 1000, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -0.95 * 7.5 * 1000, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate


        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.OCCURRENCE_AND_SEVERITY, [
                        "frequencyIndices": new ConstrainedMultiDimensionalParameter(
                                Collections.emptyList(), FrequencyIndexSelectionTableConstraints.COLUMN_TITLES,
                                ConstraintsFactory.getConstraints(FrequencyIndexSelectionTableConstraints.IDENTIFIER)),
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant: 3d]),
                        "occurrenceDateDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "produceClaim": FrequencySeverityClaimType.SINGLE,])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "claim number", 3, claimsGenerator.outClaims.size()
        assertEquals "claim value", -120 * 1000, claimsGenerator.outClaims[0].ultimate(), 1E-8
        assertEquals "claim value", -120 * 1000, claimsGenerator.outClaims[1].ultimate(), 1E-8
        assertEquals "claim value", -120 * 1000, claimsGenerator.outClaims[2].ultimate(), 1E-8
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "no event", true, claimsGenerator.outClaims[2].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 3, 2, 0, 0, 0, 0), claimsGenerator.outClaims[1].occurrenceDate
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].getDate()
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].occurrenceDate
        assertEquals "occurrence date",  date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[2].baseClaim.occurrenceDate

    }

}
