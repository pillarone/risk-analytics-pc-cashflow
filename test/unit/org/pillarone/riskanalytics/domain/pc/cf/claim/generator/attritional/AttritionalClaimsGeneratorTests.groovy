package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.utils.constraint.PeriodDistributionsConstraints
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionParams
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.joda.time.Period
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutPattern
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGeneratorTests extends GroovyTestCase {

    AttritionalClaimsGenerator generator;

    void setUp() {
        ConstraintsFactory.registerConstraint(new PeriodDistributionsConstraints())
        AttritionalClaimsModel model = new AttritionalClaimsModel(parmSeverityDistribution : VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT, ["constant": new ConstrainedMultiDimensionalParameter([[1, 3], [1000d, 2000d]],
                        [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                        ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER))]))
        generator = new AttritionalClaimsGenerator(subClaimsModel: model)
        generator.periodScope = TestPeriodScopeUtilities.getPeriodScope(new DateTime(2012,1,1,0,0,0,0), 4)
        generator.periodStore = new PeriodStore(generator.periodScope)
    }

    /** different distribution parameters for different periods */
    void testUsage() {
        generator.doCalculation()
        assertEquals "P0 one ultimate claim", 1, generator.outClaims.size()
        assertEquals "P0 ultimate value", -1000d, generator.outClaims[0].ultimate()

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.doCalculation()
        assertEquals "P1 one ultimate claim", 1, generator.outClaims.size()
        assertEquals "P1 ultimate value", -1000d, generator.outClaims[0].ultimate()

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.doCalculation()
        assertEquals "P2 one ultimate claim", 1, generator.outClaims.size()
        assertEquals "P2 ultimate value", -2000d, generator.outClaims[0].ultimate()
    }

    void testPayoutPattern() {
        PatternPacket pattern = new PatternPacket(IPayoutPatternMarker.class, [0.5d, 0.8d, 1.0d],
                [Period.months(0), Period.months(12), Period.months(24)])
        pattern.origin = new PayoutPattern(name: '24m')
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, pattern.origin.name)
        generator.parmPayoutPattern.selectedComponent = pattern.origin

        generator.inPatterns.add(pattern)
        generator.doCalculation()
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -1000d, generator.outClaims[0].ultimate()
        assertEquals "P0 paid values", -500d, generator.outClaims[0].paidIncrementalIndexed

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.inPatterns.add(pattern)
        generator.doCalculation()
        assertEquals "P1 claim2", 2, generator.outClaims.size()
        assertEquals "P1 ultimate values", [-1000d, 0d], generator.outClaims*.ultimate()
        assertEquals "P1 paid values", [-500d, -300.00000000000006d], generator.outClaims*.paidIncrementalIndexed

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.inPatterns.add(pattern)
        generator.doCalculation()
        assertEquals "P2 claim", 3, generator.outClaims.size()
        assertEquals "P2 ultimate values", [-2000d, 0d, 0d], generator.outClaims*.ultimate()
        assertEquals "P2 paid values", [-1000d, -199.99999999999994, -300.00000000000006], generator.outClaims*.paidIncrementalIndexed

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.inPatterns.add(pattern)
        generator.doCalculation()
        assertEquals "P3 claim", 3, generator.outClaims.size()
        assertEquals "P3 ultimate values", [-2000d, 0d, 0d], generator.outClaims*.ultimate()
        assertEquals "P3 paid values", [-1000.0, -199.99999999999994, -600.0000000000001], generator.outClaims*.paidIncrementalIndexed
    }

    void testRelativeCalibrationPremium() {
        RiskBands riskBands = new RiskBands()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        generator.subClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.PREMIUM, ['underwritingInfo': uwInfoComboBox])
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)

        generator.inUnderwritingInfo.add(underwritingInfo)
        generator.doCalculation()
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -1000000d, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.periodScope.prepareNextPeriod()
        generator.inUnderwritingInfo.add(underwritingInfo)
        generator.doCalculation()
        assertEquals "P2 claims", 1, generator.outClaims.size()
        assertEquals "P2 ultimate values", -2000000d, generator.outClaims[0].ultimate()
    }

    void testRelativeCalibrationExposure() {
        RiskBands riskBands = new RiskBands()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        generator.subClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.EXPOSURE, ['underwritingInfo': uwInfoComboBox])
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)

        generator.inUnderwritingInfo.add(underwritingInfo)
        generator.doCalculation()
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -20000d, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.periodScope.prepareNextPeriod()
        generator.inUnderwritingInfo.add(underwritingInfo)
        generator.doCalculation()
        assertEquals "P2 claims", 1, generator.outClaims.size()
        assertEquals "P2 ultimate values", -40000d, generator.outClaims[0].ultimate()
    }

    void testDeterministicParameterization() {
        generator.setGlobalDeterministicMode(true)
        generator.parmDeterministicClaims = new ConstrainedMultiDimensionalParameter([[1d, 3d, 3.2], [1300d, 2100d, 1500d]],
                [AttritionalClaimsGenerator.REAL_PERIOD, AttritionalClaimsGenerator.CLAIM_VALUE],
                ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))

        generator.doCalculation()
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", [-1300d], generator.outClaims*.ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.doCalculation()
        assertEquals "P1 claims", 0, generator.outClaims.size()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.doCalculation()
        assertEquals "P2 claims", 2, generator.outClaims.size()
        assertEquals "P2 ultimate values", [-2100d, -1500d], generator.outClaims*.ultimate()
    }

    // todo: test for indices and events
}