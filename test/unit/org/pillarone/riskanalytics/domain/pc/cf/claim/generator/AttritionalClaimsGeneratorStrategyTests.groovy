package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGeneratorStrategyTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    ClaimsGenerator claimsGenerator
    ClaimsGenerator claimsGenerator2
    RiskBands riskBands = new RiskBands()
    RiskBands riskBands2 = new RiskBands()

    List<String> targets
    List<EventSeverity> severities1
    List<EventSeverity> severities2
    RandomDistribution systematicFrequency1
    RandomDistribution systematicFrequency2

    void setUp() {

        claimsGenerator = new ClaimsGenerator(name: "motor hull")
        claimsGenerator2 = new ClaimsGenerator(name: "hail")
        claimsGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        claimsGenerator.periodStore = new PeriodStore(claimsGenerator.periodScope)
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        claimsGenerator.setParmUnderwritingSegments(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))
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

    void testRelativeClaimsForExceptionalCases() {
        // todo(jwa): please decide: either scale factor is 1 for all of the exception cases, or it is 0 for all the cases; now it is inconsistent
        // todo(jwa): ExposureBase.SUM_INSURED yields scaling factor 0; has to be implemented correctly

        // no ingoing underwriting info packets
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()

        // ingoing underwriting info packets list is non-empty and coverCriteria non-empty, but there is no match
        claimsGenerator.reset()
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands2)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -0d, claimsGenerator.outClaims[0].ultimate()

        // no cover criteria
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                [""], ["Underwriting Information"], IUnderwritingInfoMarker)
        claimsGenerator.setParmUnderwritingSegments(uwInfoComboBox)

        claimsGenerator.reset()
        underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()

        // ExposureBase is sum insured
        uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        claimsGenerator.setParmUnderwritingSegments(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.SUM_INSURED,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, sumInsured: 10E6, origin: riskBands)
        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -0d, claimsGenerator.outClaims[0].ultimate()

    }

    void testAttritional() {
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()
    }

    void testAttritionalRelativeClaims() {

        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123000, claimsGenerator.outClaims[0].ultimate()

        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.NUMBER_OF_POLICIES,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123 * 20, claimsGenerator.outClaims[0].ultimate()

    }

    void testAttritionalSystematicSeverities() {

        EventDependenceStream stream1 = new EventDependenceStream(targets, severities1)

        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 10]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        claimsGenerator.inEventSeverities << stream1
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -8, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate

        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 1]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1

        claimsGenerator.doCalculation()
        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -800, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate

    }

    void testOccurrenceAttritional() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct fraction of period of attritional claim", date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[0].getDate()


    }

    void testOccurrenceAttritionalRelativeClaims() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123000, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct fraction of period of attritional claim", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[0].getDate()

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.NUMBER_OF_POLICIES,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123 * 20, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct fraction of period of attritional claim", date20110101.plusDays((int) Math.floor(0.957 * 365d)),
                claimsGenerator.outClaims[0].getDate()
    }

    void testOccurrenceAttritionalSystematicSeverities() {

        EventDependenceStream stream1 = new EventDependenceStream(targets, severities1)

        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 1]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        claimsGenerator.inEventSeverities << stream1
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -0.8, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate

        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 1]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1

        claimsGenerator.doCalculation()
        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -800, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].getDate()
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].occurrenceDate
        assertEquals "occurrence date", new DateTime(2011, 1, 2, 0, 0, 0, 0), claimsGenerator.outClaims[0].baseClaim.occurrenceDate

    }

    void testFrequencyAverageAttritional() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 3]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -3 * 123, claimsGenerator.outClaims[0].ultimate()
    }

    void testFrequencyAverageAttritionalRelativeClaims() {

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 3]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123000 * 3, claimsGenerator.outClaims[0].ultimate()

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 3]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.NUMBER_OF_POLICIES,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123 * 20 * 3, claimsGenerator.outClaims[0].ultimate()
    }

    void testFrequencyAverageAttritionalSystematicSeverities() {

        EventDependenceStream stream1 = new EventDependenceStream(targets, severities1)
        EventDependenceStream stream2 = new EventDependenceStream(targets, severities2)
        SystematicFrequencyPacket sysFrequencyPacket1 = new SystematicFrequencyPacket(targets: targets, frequencyDistribution: systematicFrequency1)

        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a: 0, b: 1]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -0.8 - 0.95, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null


        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 5d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.ABSOLUTE,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.reset()
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -5 * 120, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null


        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 2d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.UNIFORM, [a:0, b:7.5]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -7.5 * (0.8+0.95) * 1000, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null


        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 5d]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 120]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.reset()
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)
        claimsGenerator.inEventSeverities << stream1 << stream2
        claimsGenerator.inEventFrequencies << sysFrequencyPacket1
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -5 * 120 * 1000, claimsGenerator.outClaims[0].ultimate()
        assertEquals "no event", true, claimsGenerator.outClaims[0].baseClaim.event == null

    }


}
