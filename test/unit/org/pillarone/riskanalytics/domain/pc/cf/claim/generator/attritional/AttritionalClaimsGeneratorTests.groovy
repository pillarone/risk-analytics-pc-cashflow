package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.ReinsuranceContractBaseType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ISeverityIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndex
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutPattern
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.PeriodDistributionsConstraints
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionParams
import org.pillarone.riskanalytics.domain.utils.math.distribution.varyingparams.VaryingParametersDistributionType
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationState
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationBehaviour
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGeneratorTests extends GroovyTestCase {

    private static final double EPSILON = 1E-10

    AttritionalClaimsGenerator createGenerator() {
        ConstraintsFactory.registerConstraint(new PeriodDistributionsConstraints())
        AttritionalClaimsModel model = new AttritionalClaimsModel(parmSeverityDistribution: VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT, ["constant": new ConstrainedMultiDimensionalParameter([[1, 3], [1000d, 2000d]],
                        [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                        ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER))]))
        AttritionalClaimsGenerator generator = new AttritionalClaimsGenerator(subClaimsModel: model)
        generator.parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(ReinsuranceContractBaseType.LOSSESOCCURRING, [:])
        generator.periodScope = TestPeriodScopeUtilities.getPeriodScope(new DateTime(2012, 1, 1, 0, 0, 0, 0), 4)
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.iterationScope = new IterationScope()
        generator.globalLastCoveredPeriod= 4
        generator.globalUpdateDate = new DateTime(2012, 1, 1, 0, 0, 0, 0)
        generator.globalSanityChecks = true
        generator
    }

    /** different distribution parameters for different periods */
    void testUsage() {
        AttritionalClaimsGenerator generator = createGenerator()
        doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P0 one ultimate claim", 1, generator.outClaims.size()
        assertEquals "P0 ultimate value", -1000d, generator.outClaims[0].ultimate()

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P1 one ultimate claim", 1, generator.outClaims.size()
        assertEquals "P1 ultimate value", -1000d, generator.outClaims[0].ultimate()

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P2 one ultimate claim", 1, generator.outClaims.size()
        assertEquals "P2 ultimate value", -2000d, generator.outClaims[0].ultimate()
    }

    void testPayoutPattern() {
        PatternPacket pattern = new PatternPacket(IPayoutPatternMarker.class, [0.5d, 0.8d, 1.0d],
                [Period.months(0), Period.months(12), Period.months(24)])
        pattern.origin = new PayoutPattern(name: '24m')
        AttritionalClaimsGenerator generator = createGenerator()
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, pattern.origin.name)
        generator.parmPayoutPattern.selectedComponent = pattern.origin

        generator.inPatterns.add(pattern)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -1000d, generator.outClaims[0].ultimate()
        assertEquals "P0 paid values", -500d, generator.outClaims[0].paidIncrementalIndexed

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.inPatterns.add(pattern)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P1 claim2", 2, generator.outClaims.size()
        assertEquals "P1 ultimate values", [-1000d, 0d], generator.outClaims*.ultimate()
        assertEquals "P1 paid values", [-500d, -300.00000000000006d], generator.outClaims*.paidIncrementalIndexed

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.inPatterns.add(pattern)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P2 claim", 3, generator.outClaims.size()
        assertEquals "P2 ultimate values", [-2000d, 0d, 0d], generator.outClaims*.ultimate()
        assertEquals "P2 paid values", [-1000d, -199.99999999999994, -300.00000000000006], generator.outClaims*.paidIncrementalIndexed

        generator.periodScope.prepareNextPeriod()
        generator.reset()
        generator.inPatterns.add(pattern)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P3 claim", 3, generator.outClaims.size()
        assertEquals "P3 ultimate values", [-2000d, 0d, 0d], generator.outClaims*.ultimate()
        assertEquals "P3 paid values", [-1000.0, -199.99999999999994, -600.0000000000001], generator.outClaims*.paidIncrementalIndexed
    }

    void testRelativeCalibrationPremium() {
        RiskBands riskBands = new RiskBands()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        AttritionalClaimsGenerator generator = createGenerator()
        generator.subClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.PREMIUM, ['underwritingInfo': uwInfoComboBox])
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)

        generator.inUnderwritingInfo.add(underwritingInfo)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -1000000d, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        doClaimsCalcWithNoCommutation(generator)

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inUnderwritingInfo.add(underwritingInfo)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P2 claims", 1, generator.outClaims.size()
        assertEquals "P2 ultimate values", -2000000d, generator.outClaims[0].ultimate()
    }

    void testRelativeCalibrationExposure() {
        RiskBands riskBands = new RiskBands()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        AttritionalClaimsGenerator generator = createGenerator()
        generator.subClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.EXPOSURE, ['underwritingInfo': uwInfoComboBox])
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)

        generator.inUnderwritingInfo.add(underwritingInfo)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -20000d, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        doClaimsCalcWithNoCommutation(generator)

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inUnderwritingInfo.add(underwritingInfo)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P2 claims", 1, generator.outClaims.size()
        assertEquals "P2 ultimate values", -40000d, generator.outClaims[0].ultimate()
    }

    void testDeterministicParameterization() {
        AttritionalClaimsGenerator generator = createGenerator()
        generator.setGlobalDeterministicMode(true)
        generator.parmDeterministicClaims = new ConstrainedMultiDimensionalParameter([[1d, 2d, 3d], [1300d,0d, 2100d]],
                [AttritionalClaimsGenerator.REAL_PERIOD, AttritionalClaimsGenerator.CLAIM_VALUE],
                ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))

        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", [-1300d], generator.outClaims*.ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        doClaimsCalcWithNoCommutation(generator)

        assertEquals "P1 claims", 1, generator.outClaims.size()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        doClaimsCalcWithNoCommutation(generator)

        assertEquals "P2 claims", 1, generator.outClaims.size()
        assertEquals "P2 ultimate values", [-2100d], generator.outClaims*.ultimate()
    }

    // todo(sku): fix, runs locally but not on Jenkins
//    void testRiskAttachingMode() {
//        int underlyingContractLength = 12
//        AttritionalClaimsGenerator generator = createGenerator()
//        generator.parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(
//                ReinsuranceContractBaseType.RISKATTACHING, ['underlyingContractLength': underlyingContractLength])
//        generator.doCalculation()
//        int numberOfClaimsOccurrencePeriod0 = 5
//        assertEquals "P0 ultimate claims", numberOfClaimsOccurrencePeriod0, generator.outClaims.size()      // fails on Jenkins at this point
//        assertEquals "P0 total ultimate", -1000d / 12d * numberOfClaimsOccurrencePeriod0, generator.outClaims*.ultimate().sum()
//        for (int i = 0; i < numberOfClaimsOccurrencePeriod0; i++) {
//            assertEquals "P0 occurrence and inception in same year", generator.outClaims[i].occurrenceDate.year, generator.outClaims[i].getBaseClaim().getExposureStartDate().year
//        }
//
//        generator.reset()
//        generator.periodScope.prepareNextPeriod()
//        generator.doCalculation()
//        int numberOfClaimsInceptionOccurrencePeriod1 = 6
//        int numberOfClaimsOccurrencePeriod1 = numberOfClaimsInceptionOccurrencePeriod1 + (underlyingContractLength - numberOfClaimsOccurrencePeriod0)
//        assertEquals "P1 ultimate claims", numberOfClaimsOccurrencePeriod1, generator.outClaims.size()
//        assertEquals "P1 total ultimate", -1000d / 12d * numberOfClaimsOccurrencePeriod1, generator.outClaims*.ultimate().sum(), EPSILON
//        for (int i = 0; i < numberOfClaimsInceptionOccurrencePeriod1; i++) {
//            assertEquals "P1 occurrence and inception in same year", generator.outClaims[i].occurrenceDate.year, generator.outClaims[i].getBaseClaim().getExposureStartDate().year
//        }
//        // claims with inception dates of previous periods are added after claims of most recent period
//        for (int i = numberOfClaimsInceptionOccurrencePeriod1; i < numberOfClaimsOccurrencePeriod1; i++) {
//            assertEquals "P1 occurrence after inception", generator.outClaims[i].occurrenceDate.year, generator.outClaims[i].getBaseClaim().getExposureStartDate().year + 1
//        }
//
//    }

    void testIndices() {
        SeverityIndex marine = new SeverityIndex()
        AttritionalClaimsGenerator generator = createGenerator()
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        generator.subClaimsModel.parmSeverityIndices = new ConstrainedMultiDimensionalParameter(
                ["Marine"], ["Severity Index"], ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER),
        )
        generator.subClaimsModel.parmSeverityIndices.comboBoxValues.put(0, ["Marine": marine])

        FactorsPacket severityTimeSeries = new FactorsPacket()
        severityTimeSeries.add(new DateTime(2011, 1, 1, 0, 0, 0, 0), 1.1d)
        severityTimeSeries.add(new DateTime(2012, 1, 1, 0, 0, 0, 0), 1.05d)
        severityTimeSeries.add(new DateTime(2013, 1, 1, 0, 0, 0, 0), 0.95d)
        severityTimeSeries.add(new DateTime(2014, 1, 1, 0, 0, 0, 0), 1.1d)
        severityTimeSeries.origin = marine

        generator.inFactors.addAll(severityTimeSeries)
        doClaimsCalcWithNoCommutation(generator)
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -1000d, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)
        doClaimsCalcWithNoCommutation(generator)

        assertEquals "P1 claims", 1, generator.outClaims.size()
        assertEquals "P1 ultimate values", -1000d * 0.95 / 1.05, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)
        doClaimsCalcWithNoCommutation(generator)

        assertEquals "P2 claims", 1, generator.outClaims.size()
        assertEquals "P2 ultimate values", -2000d * 1.1 / 1.05, generator.outClaims[0].ultimate(), EPSILON
    }

    private void doClaimsCalcWithNoCommutation(AttritionalClaimsGenerator generator, boolean addPattern = false) {
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState()
        if(addPattern) {
            addPayoutPattern(generator)
        }
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)
    }

    private void addPayoutPattern (AttritionalClaimsGenerator generator ) {
        List<Integer> nothing = new ArrayList<Integer>()
        nothing << 0
        List<Double> notMuch = new ArrayList<Integer>()
        nothing << 0d

        PatternPacket packet = PatternPacketTests.getPattern(nothing, notMuch, false)
        generator.inPatterns << packet
    }



    void testClaimsStopOnCommutation() {
        SeverityIndex marine = new SeverityIndex()
        AttritionalClaimsGenerator generator = createGenerator()
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        generator.subClaimsModel.parmSeverityIndices = new ConstrainedMultiDimensionalParameter(
                ["Marine"], ["Severity Index"], ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER),
        )
        generator.subClaimsModel.parmSeverityIndices.comboBoxValues.put(0, ["Marine": marine])

        FactorsPacket severityTimeSeries = new FactorsPacket()
        severityTimeSeries.add(new DateTime(2011, 1, 1, 0, 0, 0, 0), 1.1d)
        severityTimeSeries.add(new DateTime(2012, 1, 1, 0, 0, 0, 0), 1.05d)
        severityTimeSeries.add(new DateTime(2013, 1, 1, 0, 0, 0, 0), 0.95d)
        severityTimeSeries.origin = marine

        generator.inFactors.addAll(severityTimeSeries)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState(true, 1, CommutationBehaviour.DEFAULT, 1d, 1, new DateTime(2012, 12, 31, 0, 0, 0, 0), true)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)
        assertEquals "P0 claims", 1, generator.outClaims.size()
        assertEquals "P0 ultimate values", -1000d, generator.outClaims[0].ultimate()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)

        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState(true, 1, CommutationBehaviour.DEFAULT, 1d, 1, new DateTime(2011, 12, 31, 0, 0, 0, 0), false)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)

//        There should be no claims as we are commuted.
        assertEquals "P1 claims", 0, generator.outClaims.size()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState(true, 1, CommutationBehaviour.DEFAULT, 1d, 1, new DateTime(2011, 12, 31, 0, 0, 0, 0), false)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)
        //        There should be no claims as we are commuted.
        assertEquals "P1 claims", 0, generator.outClaims.size()


    }

    // todo: test for events
}
