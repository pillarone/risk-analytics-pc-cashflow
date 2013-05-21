package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.TestClaimsGenerator
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
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.global.SimulationConstants
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.AggregateActualClaimsStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGeneratorTests extends GroovyTestCase {

    private static final double EPSILON = 1E-10

    AttritionalClaimsGenerator createGenerator(int numberOfPeriods) {
        ConstraintsFactory.registerConstraint(new PeriodDistributionsConstraints())
        AttritionalClaimsModel model = new AttritionalClaimsModel(parmSeverityDistribution: VaryingParametersDistributionType.getStrategy(
                VaryingParametersDistributionType.CONSTANT, ["constant": new ConstrainedMultiDimensionalParameter([[1, 3], [1000d, 2000d]],
                        [DistributionParams.PERIOD.toString(), DistributionParams.CONSTANT.toString()],
                        ConstraintsFactory.getConstraints(PeriodDistributionsConstraints.IDENTIFIER))]))
        AttritionalClaimsGenerator generator = new AttritionalClaimsGenerator(subClaimsModel: model)
        generator.parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(ReinsuranceContractBaseType.LOSSESOCCURRING_NO_SPLIT, [:])
        generator.periodScope = TestPeriodScopeUtilities.getPeriodScope(new DateTime(2012, 1, 1, 0, 0, 0, 0), numberOfPeriods)
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.iterationScope = new IterationScope()
        generator.globalLastCoveredPeriod= numberOfPeriods
        generator.globalUpdateDate = new DateTime(2012, 1, 1, 0, 0, 0, 0)
        generator.globalSanityChecks = true
        generator.globalTrivialIndices = true
        generator
    }

    void testInceptionClaimHasOneDayRemovedFromPattern() {
        AttritionalClaimsGenerator generator = createGenerator(1)
        PatternPacket trivialReportingPattern = new PatternPacket(IPayoutPatternMarker.class, [1d], [new Period().plusMonths(12)], false)
        trivialReportingPattern.origin = new PayoutPattern(name: 'nothing')
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, trivialReportingPattern.origin.name)
        generator.parmPayoutPattern.selectedComponent = trivialReportingPattern.origin
        generator.inPatterns << trivialReportingPattern

        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, false)
        assertEquals "P0 ultimate value", 1000d, (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON
        assertEquals "Check one day removed from inception pattern", generator.outClaims[-1].getDate(), new DateTime(2013, 1, 1 ,0, 0,0,0).minusDays(1)

    }

    void testInceptionClaimHasOneDayRemovedFromPatternWithAggUpdateStrategy() {
        AttritionalClaimsGenerator generator = createGenerator(1)
        PatternPacket trivialReportingPattern = new PatternPacket(IPayoutPatternMarker.class, [1d], [new Period().plusMonths(12)], false)
        trivialReportingPattern.origin = new PayoutPattern(name: 'nothing')
        generator.parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, trivialReportingPattern.origin.name)
        generator.parmPayoutPattern.selectedComponent = trivialReportingPattern.origin
        generator.inPatterns << trivialReportingPattern

        generator.parmActualClaims = new AggregateActualClaimsStrategy()

        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, false)
        assertEquals "P0 ultimate value", 1000d, (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON
        assertEquals "Check one day removed from inception pattern", generator.outClaims[-1].getDate(), new DateTime(2013, 1, 1 ,0, 0,0,0).minusDays(1)

    }

    void testRelativeCalibrationPremium() {
        RiskBands riskBands = new RiskBands()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        AttritionalClaimsGenerator generator = createGenerator(3)
        generator.subClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.PREMIUM, ['underwritingInfo': uwInfoComboBox])
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)

        generator.inUnderwritingInfo.add(underwritingInfo)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P0 ultimate values", 1000d * 1000d, (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inUnderwritingInfo.add(underwritingInfo)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P2 ultimate values", 2000d * 1000d, (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON
    }

    void testRelativeCalibrationExposure() {
        RiskBands riskBands = new RiskBands()
        ComboBoxTableMultiDimensionalParameter uwInfoComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["motor hull"], ["Underwriting Information"], IUnderwritingInfoMarker)
        uwInfoComboBox.comboBoxValues.put('motor hull', riskBands)
        AttritionalClaimsGenerator generator = createGenerator(3)
        generator.subClaimsModel.parmSeverityBase = ExposureBaseType.getStrategy(ExposureBaseType.EXPOSURE, ['underwritingInfo': uwInfoComboBox])
        UnderwritingInfoPacket underwritingInfo = new UnderwritingInfoPacket(premiumWritten: 1000, numberOfPolicies: 20, origin: riskBands)

        generator.inUnderwritingInfo.add(underwritingInfo)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P0 ultimate values", 20000d, (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inUnderwritingInfo.add(underwritingInfo)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P2 ultimate values", 40000d,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON
    }

    void testDeterministicParameterization() {
        AttritionalClaimsGenerator generator = createGenerator(3)
        generator.setGlobalDeterministicMode(true)
        generator.parmDeterministicClaims = new ConstrainedMultiDimensionalParameter([[1d, 2d, 3d], [1300d,0d, 2100d]],
                [AttritionalClaimsGenerator.REAL_PERIOD, AttritionalClaimsGenerator.CLAIM_VALUE],
                ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))

        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P0 ultimate values", 1300d,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)

        assertEquals "P0 ultimate values", 0d,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)

        assertEquals "P2 ultimate values", 2100d,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON
    }

    /* void testRiskAttachingMode() {
        int underlyingContractLength = 12
        AttritionalClaimsGenerator generator = createGenerator(3)
        generator.parmParameterizationBasis = ReinsuranceContractBaseType.getStrategy(
                ReinsuranceContractBaseType.RISKATTACHING, ['underlyingContractLength': underlyingContractLength])
        generator.doCalculation()
        int numberOfClaimsOccurrencePeriod0 = 5
        assertEquals "P0 ultimate claims", numberOfClaimsOccurrencePeriod0, generator.outClaims.size()      // fails on Jenkins at this point
        assertEquals "P0 total ultimate", -1000d / 12d * numberOfClaimsOccurrencePeriod0, generator.outClaims*.ultimate().sum()
        for (int i = 0; i < numberOfClaimsOccurrencePeriod0; i++) {
            assertEquals "P0 occurrence and inception in same year", generator.outClaims[i].occurrenceDate.year, generator.outClaims[i].getBaseClaim().getExposureStartDate().year
        }

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.doCalculation()
        int numberOfClaimsInceptionOccurrencePeriod1 = 6
        int numberOfClaimsOccurrencePeriod1 = numberOfClaimsInceptionOccurrencePeriod1 + (underlyingContractLength - numberOfClaimsOccurrencePeriod0)
        assertEquals "P1 ultimate claims", numberOfClaimsOccurrencePeriod1, generator.outClaims.size()
        assertEquals "P1 total ultimate", -1000d / 12d * numberOfClaimsOccurrencePeriod1, generator.outClaims*.ultimate().sum(), EPSILON
        for (int i = 0; i < numberOfClaimsInceptionOccurrencePeriod1; i++) {
            assertEquals "P1 occurrence and inception in same year", generator.outClaims[i].occurrenceDate.year, generator.outClaims[i].getBaseClaim().getExposureStartDate().year
        }
        // claims with inception dates of previous periods are added after claims of most recent period
        for (int i = numberOfClaimsInceptionOccurrencePeriod1; i < numberOfClaimsOccurrencePeriod1; i++) {
            assertEquals "P1 occurrence after inception", generator.outClaims[i].occurrenceDate.year, generator.outClaims[i].getBaseClaim().getExposureStartDate().year + 1
        }
    } */

     void testIndices() {
        SeverityIndex marine = new SeverityIndex()
        AttritionalClaimsGenerator generator = createGenerator(3)
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ComboBoxTableMultiDimensionalParameter aBox = new ComboBoxTableMultiDimensionalParameter(
                 Arrays.asList("Marine"), Arrays.asList("Severity Index"), ISeverityIndexMarker.class)
         aBox.comboBoxValues = ["Marine" : marine]
        generator.subClaimsModel.parmSeverityIndices =  aBox

        FactorsPacket severityTimeSeries = new FactorsPacket()
        severityTimeSeries.add(new DateTime(2011, 1, 1, 0, 0, 0, 0), 1.1d)
        severityTimeSeries.add(new DateTime(2012, 1, 1, 0, 0, 0, 0), 1.05d)
        severityTimeSeries.add(new DateTime(2013, 1, 1, 0, 0, 0, 0), 0.95d)
        severityTimeSeries.add(new DateTime(2014, 1, 1, 0, 0, 0, 0), 1.1d)
        severityTimeSeries.origin = marine

        generator.inFactors.addAll(severityTimeSeries)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)
        assertEquals "P0 ultimate values", 1000d,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)

        assertEquals "P1 ultimate values", 1000d * 0.95 / 1.05, (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)
        TestClaimsGenerator.doClaimsCalcWithNoCommutation(generator, true)

        assertEquals "P2 ultimate values", 2000d * 1.1 / 1.05,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON
    }


     void testClaimsStopOnCommutation() {
        SeverityIndex marine = new SeverityIndex()
        AttritionalClaimsGenerator generator = createGenerator(3)
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
         generator.subClaimsModel.parmSeverityIndices = new ComboBoxTableMultiDimensionalParameter(
                 Arrays.asList("Marine"), Arrays.asList("Severity Index"), ISeverityIndexMarker.class)

        FactorsPacket severityTimeSeries = new FactorsPacket()
        severityTimeSeries.add(new DateTime(2011, 1, 1, 0, 0, 0, 0), 1.1d)
        severityTimeSeries.add(new DateTime(2012, 1, 1, 0, 0, 0, 0), 1.05d)
        severityTimeSeries.add(new DateTime(2013, 1, 1, 0, 0, 0, 0), 0.95d)
        severityTimeSeries.origin = marine

        generator.inFactors.addAll(severityTimeSeries)
        TestClaimsGenerator.addPayoutPattern(generator)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState(true, 1, CommutationBehaviour.DEFAULT, 1d, 1, new DateTime(2012, 12, 31, 0, 0, 0, 0), true)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)
        assertEquals "P0 ultimate values", 1000d,  (Double) generator.outClaims*.ultimate().sum(), SimulationConstants.EPSILON

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)

         TestClaimsGenerator.addPayoutPattern(generator)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState(true, 1, CommutationBehaviour.DEFAULT, 1d, 1, new DateTime(2011, 12, 31, 0, 0, 0, 0), false)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)

//        There should be no claims as we are commuted.
        assertEquals "P1 claims", 0, generator.outClaims.size()

        generator.reset()
        generator.periodScope.prepareNextPeriod()
        generator.inFactors.addAll(severityTimeSeries)
        TestClaimsGenerator.addPayoutPattern(generator)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_CLAIMS_CALCULATION)
        generator.inCommutationState << new CommutationState(true, 1, CommutationBehaviour.DEFAULT, 1d, 1, new DateTime(2011, 12, 31, 0, 0, 0, 0), false)
        generator.doCalculation(AbstractClaimsGenerator.PHASE_STORE_COMMUTATION_STATE)
        //        There should be no claims as we are commuted.
        assertEquals "P1 claims", 0, generator.outClaims.size()
    }
}
