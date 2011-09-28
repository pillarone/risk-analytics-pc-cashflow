package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.OriginalClaimsCoverAttributeStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])
    PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);
    PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)

    static ReinsuranceContract getQuotaShareContract(double quotaShare, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, [
                        'quotaShare': quotaShare,
                        'limit': LimitStrategyType.getDefault(),
                        'commission': CommissionStrategyType.getNoCommission()
                ]),
                parmCover : CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
    }

    /** one claim without development */
    void testUsage() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)

        quotaShare20.inClaims.addAll(claims)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', 200, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 ceded paid incremental', 200, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P0 ceded reported incremental', 200, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

    /** one claim with one development per period */
    void testUsageDevelopment() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)

        quotaShare20.inClaims.addAll(claims)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0 ceded ultimate', 200, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 ceded reported incremental', 60, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P0 ceded reported cumulated', 60, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0 ceded paid incremental', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P0 ceded paid cumulated', 0, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P0 ceded reservedIndexed', 200, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0 ceded outstandingIndexed', 60, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0 ceded ibnrIndexed', 140, quotaShare20.outClaimsCeded[0].ibnrIndexed()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 ceded reported incremental', 60, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P1 ceded reported cumulated', 120, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P1 ceded paid incremental', 80, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P1 ceded paid cumulated', 80, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', 120, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', 40, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P1 ceded ibnrIndexed', 80, quotaShare20.outClaimsCeded[0].ibnrIndexed()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 ceded reported incremental', 40, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2 ceded reported cumulated', 160, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2 ceded paid incremental', 60, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P2 ceded paid cumulated', 140, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P2 ceded reservedIndexed', 60, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', 20, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P2 ceded ibnrIndexed', 40, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 ceded reported incremental', 36, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3 ceded reported cumulated', 196, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P3 ceded paid incremental', 30, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P3 ceded paid cumulated', 170, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P3 ceded reservedIndexed', 30, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P3 ceded outstandingIndexed', 26, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P3 ceded ibnrIndexed', 4, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 ceded reported incremental', 4, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P4 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P4 ceded paid incremental', 30, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P4 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P4 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P4 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P4 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

     /** one claim with two developments in 1st period */
    void testUsageTwoDevelopmentsFirstPeriod() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)

        quotaShare20.inClaims.addAll(claims)

        quotaShare20.doCalculation()
        assertEquals 'P0 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimate', 200, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded reported incremental', 140, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P0.0 ceded reported cumulated', 140, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0.0 ceded paid incremental', 2, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P0.0 ceded paid cumulated', 2, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P0.0 ceded reservedIndexed', 198, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0.0 ceded outstandingIndexed', 138, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0.0 ceded ibnrIndexed', 60, quotaShare20.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P0.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0.1 ceded reported incremental', 20, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P0.1 ceded reported cumulated', 160, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P0.1 ceded paid incremental', 18, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'P0.1 ceded paid cumulated', 20, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'P0.1 ceded reservedIndexed', 180, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P0.1 ceded outstandingIndexed', 140, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P0.1 ceded ibnrIndexed', 40, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P1 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 ceded reported incremental', 20, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P1 ceded reported cumulated', 180, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P1 ceded paid incremental', 100, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P1 ceded paid cumulated', 120, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P1 ceded reservedIndexed', 80, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P1 ceded outstandingIndexed', 60, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P1 ceded ibnrIndexed', 20, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 ceded reported incremental', 20, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2 ceded paid incremental', 20, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P2 ceded paid cumulated', 140, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P2 ceded reservedIndexed', 60, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P2 ceded outstandingIndexed', 60, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P2 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()
        assertEquals 'P3 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P3 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3 ceded reported incremental', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P3 ceded paid incremental', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P3 ceded paid cumulated', 140, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P3 ceded reservedIndexed', 60, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P3 ceded outstandingIndexed', 60, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P3 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 ceded reported incremental', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P4 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P4 ceded paid incremental', 60, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P4 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P4 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P4 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P4 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P5 number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }


    void testCoverNone() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter
        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.NONE, [:]);

        GrossClaimRoot claimRoot = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims = claimRoot.getClaimCashflowPackets(periodCounter, true)

        quotaShare20.inClaims.addAll(claims)

        quotaShare20.doCalculation()
        assertEquals 'number of covered claims', 0, quotaShare20.inClaims.size()
        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

    void testCoverPerils() {
        ClaimsGenerator perilMotor = new ClaimsGenerator(name: "motor")
        ClaimsGenerator perilMotorHull = new ClaimsGenerator(name: "motor hull")

        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter
        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.PERILS,
                    ['perils':new ComboBoxTableMultiDimensionalParameter([['motor']],['Covered Perils'], IPerilMarker),]),])
        quotaShare20.name = 'marine'
        ((OriginalClaimsCoverAttributeStrategy) quotaShare20.parmCover).filter.perils.comboBoxValues['motor'] = perilMotor

        GrossClaimRoot claimRoot = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)

        List<ClaimCashflowPacket> claimsMotor = claimRoot.getClaimCashflowPackets(periodCounter, true)
        List<ClaimCashflowPacket> claimsMotorHull = claimRoot.getClaimCashflowPackets(periodCounter, true)
        claimsMotor*.setMarker(perilMotor)
        claimsMotorHull*.setMarker(perilMotorHull)

        quotaShare20.inClaims.addAll(claimsMotor)
        quotaShare20.inClaims.addAll(claimsMotorHull)

        quotaShare20.doCalculation()
        assertEquals '0 number of covered claims', 1, quotaShare20.inClaims.size()
        assertEquals '0 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()

        quotaShare20.reset()

        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.PERILS,
                    ['perils':new ComboBoxTableMultiDimensionalParameter([['motor','motor hull']],['Covered Perils'], IPerilMarker),]),])
        ((OriginalClaimsCoverAttributeStrategy) quotaShare20.parmCover).filter.perils.comboBoxValues['motor'] = perilMotor
        ((OriginalClaimsCoverAttributeStrategy) quotaShare20.parmCover).filter.perils.comboBoxValues['motor hull'] = perilMotorHull

        quotaShare20.inClaims.addAll(claimsMotor)
        quotaShare20.inClaims.addAll(claimsMotorHull)

        quotaShare20.doCalculation()
        assertEquals '1 number of covered claims', 2, quotaShare20.inClaims.size()
        assertEquals '1 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()

    }

    void testCoverSegments() {
        Segment segmentMotor = new Segment(name: 'motor')
        Segment segmentMotorHull = new Segment(name: 'motor hull')

        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter
        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['motor']],['Segments'], ISegmentMarker),]),])
        ((OriginalClaimsCoverAttributeStrategy) quotaShare20.parmCover).filter.segments.comboBoxValues['motor'] = segmentMotor
        GrossClaimRoot claimRoot = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)

        List<ClaimCashflowPacket> claimsMotor = claimRoot.getClaimCashflowPackets(periodCounter, true)
        List<ClaimCashflowPacket> claimsMotorHull = claimRoot.getClaimCashflowPackets(periodCounter, true)
        claimsMotor*.setMarker(segmentMotor)
        claimsMotorHull*.setMarker(segmentMotorHull)

        quotaShare20.inClaims.addAll(claimsMotor)
        quotaShare20.inClaims.addAll(claimsMotorHull)

        UnderwritingInfoPacket underwritingInfoMotor = new UnderwritingInfoPacket(segment: segmentMotor,
                exposure: new ExposureInfo(date20110101, periodCounter))
        UnderwritingInfoPacket underwritingInfoMotorHull = new UnderwritingInfoPacket(segment: segmentMotorHull,
                exposure: new ExposureInfo(date20110101, periodCounter))

        quotaShare20.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoMotorHull

        quotaShare20.doCalculation()
        assertEquals '0 number of covered claims', 1, quotaShare20.inClaims.size()
        assertEquals '0 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals '0 number of covered uw infos', 1, quotaShare20.inUnderwritingInfo.size()
        assertEquals '0 number of ceded uw infos', 1, quotaShare20.inUnderwritingInfo.size()

        quotaShare20.reset()

        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS,
                ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS,
                    ['segments':new ComboBoxTableMultiDimensionalParameter([['motor','motor hull']],['Segments'], ISegmentMarker),]),])
        ((OriginalClaimsCoverAttributeStrategy) quotaShare20.parmCover).filter.segments.comboBoxValues['motor'] = segmentMotor
        ((OriginalClaimsCoverAttributeStrategy) quotaShare20.parmCover).filter.segments.comboBoxValues['motor hull'] = segmentMotorHull

        quotaShare20.inClaims.addAll(claimsMotor)
        quotaShare20.inClaims.addAll(claimsMotorHull)
        quotaShare20.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoMotorHull

        quotaShare20.doCalculation()
        assertEquals '1 number of covered claims', 2, quotaShare20.inClaims.size()
        assertEquals '1 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals '0 number of covered uw infos', 2, quotaShare20.inUnderwritingInfo.size()
        assertEquals '0 number of ceded uw infos', 2, quotaShare20.inUnderwritingInfo.size()
    }

    void testUnderwritingCededNet() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        Segment segmentMotor = new Segment(name: 'motor')
        Segment segmentMotorHull = new Segment(name: 'motor hull')
        UnderwritingInfoPacket underwritingInfoMotor = new UnderwritingInfoPacket(segment: segmentMotor,
                premiumWritten: 1000, premiumPaid: 800, numberOfPolicies: 10, sumInsured: 10000, maxSumInsured: 5000,
                exposure: new ExposureInfo(date20110101, periodCounter))
        UnderwritingInfoPacket underwritingInfoMotorHull = new UnderwritingInfoPacket(segment: segmentMotorHull,
                premiumWritten: 1200, premiumPaid: 700, numberOfPolicies: 20, sumInsured: 20000, maxSumInsured: 3000,
                exposure: new ExposureInfo(date20110101, periodCounter))
        quotaShare20.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoMotorHull

        quotaShare20.doCalculation()
        assertEquals 'number of ceded uw', 2, quotaShare20.outUnderwritingInfoCeded.size()
        assertEquals 'number of net uw', 2, quotaShare20.outUnderwritingInfoNet.size()
    }
    
    void testUnderwritingGNPIonGrossProportional() {

    }

    void testUnderwritingGNPIonGNPIProportional() {

    }
}
