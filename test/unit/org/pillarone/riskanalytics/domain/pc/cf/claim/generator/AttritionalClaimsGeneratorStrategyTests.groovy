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

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class AttritionalClaimsGeneratorStrategyTests extends GroovyTestCase {

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    ClaimsGenerator claimsGenerator
    RiskBands riskBands = new RiskBands()

    void setUp() {

        claimsGenerator = new ClaimsGenerator()
        claimsGenerator.periodScope = TestPeriodScopeUtilities.getPeriodScope(date20110101, 5)
        claimsGenerator.periodStore = new PeriodStore(claimsGenerator.periodScope)
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)

        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel(ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),]))
        //claimsGenerator.setSimulationScope(new SimulationScope(model: new ClaimsModel(), iterationScope: new IterationScope(periodScope: new PeriodScope())))

        ConstraintsFactory.registerConstraint(new DoubleConstraints())
    }

    void testUsage() {
        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()
    }

    void testRelativeClaims() {
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)
        claimsGenerator.inUnderwritingInfo.add(underwritingInfo)

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123000, claimsGenerator.outClaims[0].ultimate()
    }

    void testAttritional() {

        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()
    }

    void testOccurrenceAttritional() {

        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL_WITH_DATE, [
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "occurrenceDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 0.957]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.doCalculation()

        assertEquals "one attritional claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of attritional claim", -123, claimsGenerator.outClaims[0].ultimate()
        assertEquals "correct fraction of period of attritional claim", date20110101.plusDays((int) Math.floor(0.957 * 365d)), claimsGenerator.outClaims[0].getDate()
    }

    void testFrequencyAverageAttritional() {

        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motorHull', riskBands)
        claimsGenerator.setParmUnderwritingInformation(uwInfoComboBox)
        claimsGenerator.setParmClaimsModel ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.FREQUENCY_AVERAGE_ATTRITIONAL, [
                        "frequencyBase": FrequencyBase.ABSOLUTE,
                        "frequencyDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 1]),
                        "frequencyModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),
                        "claimsSizeBase": ExposureBase.PREMIUM_WRITTEN,
                        "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: 123]),
                        "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])

        claimsGenerator.doCalculation()

        assertEquals "one single claim", 1, claimsGenerator.outClaims.size()
        assertEquals "correct value of claim", -123, claimsGenerator.outClaims[0].ultimate()
    }

}
