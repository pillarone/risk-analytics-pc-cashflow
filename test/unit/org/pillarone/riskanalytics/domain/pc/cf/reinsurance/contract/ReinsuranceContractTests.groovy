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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.GrossPerilsCoverAttributeStrategy
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.GrossSegmentsCoverAttributeStrategy

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
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
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
        assertEquals 'P0 ceded paid incremental', 200, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P0 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P0 ceded reported incremental', 200, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P0 ceded reserved', 0, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P0 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P0 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr()

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
        assertEquals 'P0 ceded reported incremental', 60, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0 ceded reported cumulated', 60, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P0 ceded paid incremental', 0, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P0 ceded paid cumulated', 0, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P0 ceded reserved', 200, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P0 ceded outstanding', 60, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P0 ceded ibnr', 140, quotaShare20.outClaimsCeded[0].ibnr()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 ceded reported incremental', 60, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P1 ceded reported cumulated', 120, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P1 ceded paid incremental', 80, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P1 ceded paid cumulated', 80, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P1 ceded reserved', 120, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P1 ceded outstanding', 40, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P1 ceded ibnr', 80, quotaShare20.outClaimsCeded[0].ibnr()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 ceded reported incremental', 40, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P2 ceded reported cumulated', 160, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P2 ceded paid incremental', 60, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2 ceded paid cumulated', 140, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P2 ceded reserved', 60, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P2 ceded outstanding', 20, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P2 ceded ibnr', 40, quotaShare20.outClaimsCeded[0].ibnr()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 ceded reported incremental', 36, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P3 ceded reported cumulated', 196, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P3 ceded paid incremental', 30, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3 ceded paid cumulated', 170, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P3 ceded reserved', 30, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P3 ceded outstanding', 26, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P3 ceded ibnr', 4, quotaShare20.outClaimsCeded[0].ibnr()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 ceded reported incremental', 4, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P4 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P4 ceded paid incremental', 30, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P4 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P4 ceded reserved', 0, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P4 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P4 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr()


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
        assertEquals 'P0.0 ceded reported incremental', 140, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P0.0 ceded reported cumulated', 140, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P0.0 ceded paid incremental', 2, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0.0 ceded paid cumulated', 2, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P0.0 ceded reserved', 198, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P0.0 ceded outstanding', 138, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P0.0 ceded ibnr', 60, quotaShare20.outClaimsCeded[0].ibnr()
        assertEquals 'P0.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0.1 ceded reported incremental', 20, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'P0.1 ceded reported cumulated', 160, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'P0.1 ceded paid incremental', 18, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P0.1 ceded paid cumulated', 20, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'P0.1 ceded reserved', 180, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P0.1 ceded outstanding', 140, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'P0.1 ceded ibnr', 40, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P1 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 ceded reported incremental', 20, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P1 ceded reported cumulated', 180, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P1 ceded paid incremental', 100, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P1 ceded paid cumulated', 120, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P1 ceded reserved', 80, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P1 ceded outstanding', 60, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P1 ceded ibnr', 20, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 ceded reported incremental', 20, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P2 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P2 ceded paid incremental', 20, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2 ceded paid cumulated', 140, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P2 ceded reserved', 60, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P2 ceded outstanding', 60, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P2 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()
        assertEquals 'P3 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P3 ceded reported incremental', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P3 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P3 ceded paid incremental', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3 ceded paid cumulated', 140, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P3 ceded reserved', 60, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P3 ceded outstanding', 60, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P3 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 ceded reported incremental', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P4 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P4 ceded paid incremental', 60, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P4 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P4 ceded reserved', 0, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P4 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P4 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON

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
        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSPERILS,
                ['perils': new ComboBoxTableMultiDimensionalParameter(['motor'], ['Perils'], IPerilMarker)])
        ((GrossPerilsCoverAttributeStrategy) quotaShare20.parmCover).perils.comboBoxValues['motor'] = perilMotor

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

        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSPERILS,
                ['perils': new ComboBoxTableMultiDimensionalParameter(['motor','motor hull'], ['Perils'], IPerilMarker)])
        ((GrossPerilsCoverAttributeStrategy) quotaShare20.parmCover).perils.comboBoxValues['motor'] = perilMotor
        ((GrossPerilsCoverAttributeStrategy) quotaShare20.parmCover).perils.comboBoxValues['motor hull'] = perilMotorHull

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
        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSSEGMENTS,
                ['segments': new ComboBoxTableMultiDimensionalParameter(['motor'], ['Segments'], ISegmentMarker)])
        ((GrossSegmentsCoverAttributeStrategy) quotaShare20.parmCover).segments.comboBoxValues['motor'] = segmentMotor

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

        quotaShare20.parmCover = CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.GROSSSEGMENTS,
                ['segments': new ComboBoxTableMultiDimensionalParameter(['motor','motor hull'], ['Segments'], ISegmentMarker)])
        ((GrossSegmentsCoverAttributeStrategy) quotaShare20.parmCover).segments.comboBoxValues['motor'] = segmentMotor
        ((GrossSegmentsCoverAttributeStrategy) quotaShare20.parmCover).segments.comboBoxValues['motor hull'] = segmentMotorHull

        quotaShare20.inClaims.addAll(claimsMotor)
        quotaShare20.inClaims.addAll(claimsMotorHull)
        quotaShare20.inUnderwritingInfo << underwritingInfoMotor << underwritingInfoMotorHull

        quotaShare20.doCalculation()
        assertEquals '1 number of covered claims', 2, quotaShare20.inClaims.size()
        assertEquals '1 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals '0 number of covered uw infos', 2, quotaShare20.inUnderwritingInfo.size()
        assertEquals '0 number of ceded uw infos', 2, quotaShare20.inUnderwritingInfo.size()
    }

}
