package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import com.sun.org.apache.xerces.internal.impl.xs.ElementPSVImpl

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractAADTests extends GroovyTestCase {

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
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)

    static ReinsuranceContract getQuotaShareContract(double quotaShare, DateTime beginOfCover) {
        return getQuotaShareContract(quotaShare, 300, beginOfCover)
    }

    static ReinsuranceContract getQuotaShareContract(double quotaShare, double aad, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, [
                        'quotaShare': quotaShare,
                        'limit': LimitStrategyType.getStrategy(LimitStrategyType.AAD, ['aad' : aad]),
                        'commission': CommissionStrategyType.getNoCommission()
                ]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    /** three claims without development */
    void testUsage() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 300, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims800)

        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1000)

        GrossClaimRoot claimRoot1200 = new GrossClaimRoot(-1200, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims1200 = claimRoot1200.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 800 ceded reported incremental', 100, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0 800 ceded reported cumulated', 100, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P0 800 ceded paid incremental', 100, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P0 800 ceded paid cumulated', 100, quotaShare20.outClaimsCeded[0].paidCumulated
        assertEquals 'P0 800 ceded reserved', 0, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P0 800 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P0 800 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr()

        assertEquals 'P0 1000 ceded ultimate', 200, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0 1000 ceded reported incremental', 200, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P0 1000 ceded reported cumulated', 200, quotaShare20.outClaimsCeded[1].reportedCumulated
        assertEquals 'P0 1000 ceded paid incremental', 200, quotaShare20.outClaimsCeded[1].paidIncremental
        assertEquals 'P0 1000 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[1].paidCumulated
        assertEquals 'P0 1000 ceded reserved', 0, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P0 1000 ceded outstanding', 0, quotaShare20.outClaimsCeded[1].outstanding()
        assertEquals 'P0 1000 ceded ibnr', 0, quotaShare20.outClaimsCeded[1].ibnr()

        assertEquals 'P0 1200 ceded ultimate', 240, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P0 1200 ceded reported incremental', 240, quotaShare20.outClaimsCeded[2].reportedIncremental
        assertEquals 'P0 1200 ceded reported cumulated', 240, quotaShare20.outClaimsCeded[2].reportedCumulated
        assertEquals 'P0 1200 ceded paid incremental', 240, quotaShare20.outClaimsCeded[2].paidIncremental
        assertEquals 'P0 1200 ceded paid cumulated', 240, quotaShare20.outClaimsCeded[2].paidCumulated
        assertEquals 'P0 1200 ceded reserved', 0, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'P0 1200 ceded outstanding', 0, quotaShare20.outClaimsCeded[2].outstanding()
        assertEquals 'P0 1200 ceded ibnr', 0, quotaShare20.outClaimsCeded[2].ibnr()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

    /** three claims with one development per period, includes negative ibnr and outstanding in single claims
     *  with a correction over the portfolio */
    void testUsageDevelopment() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims800)

        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1000)

        GrossClaimRoot claimRoot1200 = new GrossClaimRoot(-1200, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1200 = claimRoot1200.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 800 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0 800 ceded cumulated reported', 0, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P0 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0 800 ceded reserved', 100, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P0 800 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P0 800 ceded ibnr', 100, quotaShare20.outClaimsCeded[0].ibnr()
        assertEquals 'P0 1000 ceded ultimate', 200, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0 1000 ceded incremental reported', 48, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'P0 1000 ceded cumulated reported', 48, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'P0 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P0 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'P0 1000 ceded reserved', 200, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P0 1000 ceded outstanding', 48, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'P0 1000 ceded ibnr', 152, quotaShare20.outClaimsCeded[1].ibnr()
        assertEquals 'P0 1200 ceded ultimate', 240, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P0 1200 ceded incremental reported', 72, quotaShare20.outClaimsCeded[2].reportedIncremental
        assertEquals 'P0 1200 ceded cumulated reported', 72, quotaShare20.outClaimsCeded[2].reportedCumulated
        assertEquals 'P0 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P0 1200 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'P0 1200 ceded reserved', 240, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'P0 1200 ceded outstanding', 72, quotaShare20.outClaimsCeded[2].outstanding()
        assertEquals 'P0 1200 ceded ibnr', 168, quotaShare20.outClaimsCeded[2].ibnr()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 800 ceded incremental reported', 48, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P1 800 ceded cumulated reported', 48, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'P1 800 ceded incremental paid', 4, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P1 800 ceded cumulated paid', 4, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P1 800 ceded reserved', 96, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'P1 800 ceded outstanding', 44, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'P1 800 ceded ibnr', 52, quotaShare20.outClaimsCeded[0].ibnr()
        assertEquals 'P1 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P1 1000 ceded incremental reported', 60, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'P1 1000 ceded cumulated reported', 108, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'P1 1000 ceded incremental paid', 80, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P1 1000 ceded cumulated paid', 80, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'P1 1000 ceded reserved', 120, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P1 1000 ceded outstanding', 28, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'P1 1000 ceded ibnr', 92, quotaShare20.outClaimsCeded[1].ibnr()
        assertEquals 'P1 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P1 1200 ceded incremental reported', 72, quotaShare20.outClaimsCeded[2].reportedIncremental
        assertEquals 'P1 1200 ceded cumulated reported', 144, quotaShare20.outClaimsCeded[2].reportedCumulated
        assertEquals 'P1 1200 ceded incremental paid', 96, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P1 1200 ceded cumulated paid', 96, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'P1 1200 ceded reserved', 144, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'P1 1200 ceded outstanding', 48, quotaShare20.outClaimsCeded[2].outstanding()
        assertEquals 'P1 1200 ceded ibnr', 96, quotaShare20.outClaimsCeded[2].ibnr()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 800 ceded incremental reported', 32, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P2 800 ceded cumulated reported', 80, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P2 800 ceded incremental paid', 48, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2 800 ceded cumulated paid', 52, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P2 800 ceded reserved', 48, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'P2 800 ceded outstanding', 28, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P2 800 ceded ibnr', 20, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'P2 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P2 1000 ceded incremental reported', 40, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'P2 1000 ceded cumulated reported', 148, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'P2 1000 ceded incremental paid', 60, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P2 1000 ceded cumulated paid', 140, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'P2 1000 ceded reserved', 60, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P2 1000 ceded outstanding', 8, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'P2 1000 ceded ibnr', 52, quotaShare20.outClaimsCeded[1].ibnr()
        assertEquals 'P2 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P2 1200 ceded incremental reported', 48, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'P2 1200 ceded cumulated reported', 192, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'P2 1200 ceded incremental paid', 72, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P2 1200 ceded cumulated paid', 168, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'P2 1200 ceded reserved', 72, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'P2 1200 ceded outstanding', 24, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'P2 1200 ceded ibnr', 48, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 800 ceded incremental reported', 28.8, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P3 800 ceded cumulated reported', 108.8, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P3 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3 800 ceded cumulated paid', 76, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P3 800 ceded reserved', 24, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'P3 800 ceded outstanding', 32.8, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P3 800 ceded ibnr', -8.8, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'P3 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P3 1000 ceded incremental reported', 36, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'P3 1000 ceded cumulated reported', 184, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'P3 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P3 1000 ceded cumulated paid', 170, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'P3 1000 ceded reserved', 30, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P3 1000 ceded outstanding', 14, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'P3 1000 ceded ibnr', 16, quotaShare20.outClaimsCeded[1].ibnr()
        assertEquals 'P3 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P3 1200 ceded incremental reported', 43.2, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'P3 1200 ceded cumulated reported', 235.2, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'P3 1200 ceded incremental paid', 36, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P3 1200 ceded cumulated paid', 204, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'P3 1200 ceded reserved', 36, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'P3 1200 ceded outstanding', 31.2, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'P3 1200 ceded ibnr', 4.8, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 800 ceded incremental reported', 3.2, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P4 800 ceded cumulated reported', 112, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'P4 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P4 800 ceded cumulated paid', 100, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'P4 800 ceded reserved', 0, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'P4 800 ceded outstanding', 12, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'P4 800 ceded ibnr', -12, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'P4 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P4 1000 ceded incremental reported', 4, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'P4 1000 ceded cumulated reported', 188, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'P4 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P4 1000 ceded cumulated paid', 200, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'P4 1000 ceded reserved', 0, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'P4 1000 ceded outstanding', -12, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'P4 1000 ceded ibnr', 12, quotaShare20.outClaimsCeded[1].ibnr()
        assertEquals 'P4 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P4 1200 ceded incremental reported', 4.8, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'P4 1200 ceded cumulated reported', 240, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'P4 1200 ceded incremental paid', 36, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P4 1200 ceded cumulated paid', 240, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'P4 1200 ceded reserved', 0, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'P4 1200 ceded outstanding', 0, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'P4 1200 ceded ibnr', 0, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P5 number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

     /** three claims with two developments in 1st period, includes negative outstanding in single claims
      *  with a correction over the portfolio */
    void testUsageTwoDevelopmentsFirstPeriod() {
                ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 300, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims800)

        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1000)

        GrossClaimRoot claimRoot1200 = new GrossClaimRoot(-1200, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims1200 = claimRoot1200.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 6, quotaShare20.outClaimsCeded.size()
        assertEquals 'M0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M0 800 ceded incremental reported', 52, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'M0 800 ceded cumulated reported', 52, quotaShare20.outClaimsCeded[0].reportedCumulated
        assertEquals 'M0 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M0 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M0 800 ceded reserved', 100, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'M0 800 ceded outstanding', 52, quotaShare20.outClaimsCeded[0].outstanding()
        assertEquals 'M0 800 ceded ibnr', 48, quotaShare20.outClaimsCeded[0].ibnr()
        assertEquals 'M0 1000 ceded ultimate', 200, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M0 1000 ceded incremental reported', 140, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M0 1000 ceded cumulated reported', 140, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M0 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M0 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M0 1000 ceded reserved', 200, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'M0 1000 ceded outstanding', 140, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M0 1000 ceded ibnr', 60, quotaShare20.outClaimsCeded[1].ibnr()
        assertEquals 'M0 1200 ceded ultimate', 240, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M0 1200 ceded incremental reported', 168, quotaShare20.outClaimsCeded[2].reportedIncremental
        assertEquals 'M0 1200 ceded cumulated reported', 168, quotaShare20.outClaimsCeded[2].reportedCumulated
        assertEquals 'M0 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'M0 1200 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'M0 1200 ceded reserved', 240, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'M0 1200 ceded outstanding', 168, quotaShare20.outClaimsCeded[2].outstanding()
        assertEquals 'M0 1200 ceded ibnr', 72, quotaShare20.outClaimsCeded[2].ibnr()
        assertEquals 'M3 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[3].ultimate()
        assertEquals 'M3 800 ceded incremental reported', 16, quotaShare20.outClaimsCeded[3].reportedIncremental, EPSILON
        assertEquals 'M3 800 ceded cumulated reported', 68, quotaShare20.outClaimsCeded[3].reportedCumulated, EPSILON
        assertEquals 'M3 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[3].paidIncremental, EPSILON
        assertEquals 'M3 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[3].paidCumulated, EPSILON
        assertEquals 'M3 800 ceded reserved', 100, quotaShare20.outClaimsCeded[3].reserved()
        assertEquals 'M3 800 ceded outstanding', 68, quotaShare20.outClaimsCeded[3].outstanding(), EPSILON
        assertEquals 'M3 800 ceded ibnr', 32, quotaShare20.outClaimsCeded[3].ibnr(), EPSILON
        assertEquals 'M3 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[4].ultimate()
        assertEquals 'M3 1000 ceded incremental reported', 20, quotaShare20.outClaimsCeded[4].reportedIncremental, EPSILON
        assertEquals 'M3 1000 ceded cumulated reported', 160, quotaShare20.outClaimsCeded[4].reportedCumulated, EPSILON
        assertEquals 'M3 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[4].paidIncremental, EPSILON
        assertEquals 'M3 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[4].paidCumulated, EPSILON
        assertEquals 'M3 1000 ceded reserved', 200, quotaShare20.outClaimsCeded[4].reserved()
        assertEquals 'M3 1000 ceded outstanding', 160, quotaShare20.outClaimsCeded[4].outstanding(), EPSILON
        assertEquals 'M3 1000 ceded ibnr', 40, quotaShare20.outClaimsCeded[4].ibnr(), EPSILON
        assertEquals 'M3 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[5].ultimate()
        assertEquals 'M3 1200 ceded incremental reported', 24, quotaShare20.outClaimsCeded[5].reportedIncremental, EPSILON
        assertEquals 'M3 1200 ceded cumulated reported', 192, quotaShare20.outClaimsCeded[5].reportedCumulated, EPSILON
        assertEquals 'M3 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[5].paidIncremental, EPSILON
        assertEquals 'M3 1200 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[5].paidCumulated, EPSILON
        assertEquals 'M3 1200 ceded reserved', 240, quotaShare20.outClaimsCeded[5].reserved()
        assertEquals 'M3 1200 ceded outstanding', 192, quotaShare20.outClaimsCeded[5].outstanding(), EPSILON
        assertEquals 'M3 1200 ceded ibnr', 48, quotaShare20.outClaimsCeded[5].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'M12 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M12 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M12 800 ceded incremental reported', 16, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M12 800 ceded cumulated reported', 84, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M12 800 ceded incremental paid', 80, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M12 800 ceded cumulated paid', 80, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M12 800 ceded reserved', 20, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'M12 800 ceded outstanding', 4, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M12 800 ceded ibnr', 16, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'M12 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M12 1000 ceded incremental reported', 20, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M12 1000 ceded cumulated reported', 180, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M12 1000 ceded incremental paid', 100, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M12 1000 ceded cumulated paid', 100, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M12 1000 ceded reserved', 100, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'M12 1000 ceded outstanding', 80, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M12 1000 ceded ibnr', 20, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M12 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M12 1200 ceded incremental reported', 24, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'M12 1200 ceded cumulated reported', 216, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'M12 1200 ceded incremental paid', 120, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'M12 1200 ceded cumulated paid', 120, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'M12 1200 ceded reserved', 120, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'M12 1200 ceded outstanding', 96, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'M12 1200 ceded ibnr', 24, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'M24 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M24 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M24 800 ceded incremental reported', 16, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M24 800 ceded cumulated reported', 100, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M24 800 ceded incremental paid', 16, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M24 800 ceded cumulated paid', 96, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M24 800 ceded reserved', 4, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'M24 800 ceded outstanding', 4, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M24 800 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'M24 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M24 1000 ceded incremental reported', 20, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M24 1000 ceded cumulated reported', 200, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M24 1000 ceded incremental paid', 20, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M24 1000 ceded cumulated paid', 120, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M24 1000 ceded reserved', 80, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'M24 1000 ceded outstanding', 80, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M24 1000 ceded ibnr', 0, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M24 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M24 1200 ceded incremental reported', 24, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'M24 1200 ceded cumulated reported', 240, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'M24 1200 ceded incremental paid', 24, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'M24 1200 ceded cumulated paid', 144, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'M24 1200 ceded reserved', 96, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'M24 1200 ceded outstanding', 96, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'M24 1200 ceded ibnr', 0, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'M36 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M36 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M36 800 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M36 800 ceded cumulated reported', 100, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M36 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M36 800 ceded cumulated paid', 96, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M36 800 ceded reserved', 4, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'M36 800 ceded outstanding', 4, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M36 800 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'M36 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M36 1000 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M36 1000 ceded cumulated reported', 200, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M36 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M36 1000 ceded cumulated paid', 120, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M36 1000 ceded reserved', 80, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'M36 1000 ceded outstanding', 80, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M36 1000 ceded ibnr', 0, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M36 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M36 1200 ceded incremental reported', 0, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'M36 1200 ceded cumulated reported', 240, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'M36 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'M36 1200 ceded cumulated paid', 144, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'M36 1200 ceded reserved', 96, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'M36 1200 ceded outstanding', 96, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'M36 1200 ceded ibnr', 0, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'M48 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M48 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M48 800 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M48 800 ceded cumulated reported', 100, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M48 800 ceded incremental paid', 48, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M48 800 ceded cumulated paid', 144, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M48 800 ceded reserved', -44, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'M48 800 ceded outstanding', -44, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M48 800 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
        assertEquals 'M48 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M48 1000 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M48 1000 ceded cumulated reported', 200, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M48 1000 ceded incremental paid', 60, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M48 1000 ceded cumulated paid', 180, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M48 1000 ceded reserved', 20, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'M48 1000 ceded outstanding', 20, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M48 1000 ceded ibnr', 0, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M48 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M48 1200 ceded incremental reported', 0, quotaShare20.outClaimsCeded[2].reportedIncremental, EPSILON
        assertEquals 'M48 1200 ceded cumulated reported', 240, quotaShare20.outClaimsCeded[2].reportedCumulated, EPSILON
        assertEquals 'M48 1200 ceded incremental paid', 72, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'M48 1200 ceded cumulated paid', 216, quotaShare20.outClaimsCeded[2].paidCumulated, EPSILON
        assertEquals 'M48 1200 ceded reserved', 24, quotaShare20.outClaimsCeded[2].reserved()
        assertEquals 'M48 1200 ceded outstanding', 24, quotaShare20.outClaimsCeded[2].outstanding(), EPSILON
        assertEquals 'M48 1200 ceded ibnr', 0, quotaShare20.outClaimsCeded[2].ibnr(), EPSILON
    }

    /** claims occur in different periods, make sure both get the whole AAL or more generally a new contract instance */
    void testIndependenceOfContractsPerPeriod() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 300, date20110101)
        quotaShare20.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims800)

        quotaShare20.doCalculation()
        assertEquals 'P0 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'M0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M0 800 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M0 800 ceded cumulated reported', 0, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M0 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M0 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M0 800 ceded reserved', 100, quotaShare20.outClaimsCeded[0].reserved()
        assertEquals 'M0 800 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M0 800 ceded ibnr', 100, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1000)
        quotaShare20.doCalculation()

        assertEquals 'P1 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M12 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M12 800 ceded incremental reported', 36, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M12 800 ceded cumulated reported', 36, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M12 800 ceded incremental paid', 4, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M12 800 ceded cumulated paid', 4, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M12 800 ceded reserved', 96, quotaShare20.outClaimsCeded[1].reserved()
        assertEquals 'M12 800 ceded outstanding', 32, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M12 800 ceded ibnr', 64, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M0 1000 ceded ultimate', 140, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M0 1000 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M0 1000 ceded cumulated reported', 0, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M0 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M0 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M0 1000 ceded reserved', 140, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'M0 1000 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M0 1000 ceded ibnr', 140, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M24 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M24 800 ceded incremental reported', 32, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M24 800 ceded cumulated reported', 68, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M24 800 ceded incremental paid', 48, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M24 800 ceded cumulated paid', 52, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M24 800 ceded reserved', 48, quotaShare20.outClaimsCeded[1].reserved(), EPSILON
        assertEquals 'M24 800 ceded outstanding', 16, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M24 800 ceded ibnr', 32, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M12 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M12 1000 ceded incremental reported', 60, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M12 1000 ceded cumulated reported', 60, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M12 1000 ceded incremental paid', 20, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M12 1000 ceded cumulated paid', 20, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M12 1000 ceded reserved', 120, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'M12 1000 ceded outstanding', 40, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M12 1000 ceded ibnr', 80, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P3 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M36 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M36 800 ceded incremental reported', 28.8, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M36 800 ceded cumulated reported', 96.8, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M36 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M36 800 ceded cumulated paid', 76, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M36 800 ceded reserved', 24, quotaShare20.outClaimsCeded[1].reserved(), EPSILON
        assertEquals 'M36 800 ceded outstanding', 20.8, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M36 800 ceded ibnr', 3.2, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M24 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M24 1000 ceded incremental reported', 40, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M24 1000 ceded cumulated reported', 100, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M24 1000 ceded incremental paid', 60, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M24 1000 ceded cumulated paid', 80, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M24 1000 ceded reserved', 60, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'M24 1000 ceded outstanding', 20, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M24 1000 ceded ibnr', 40, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M48 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M48 800 ceded incremental reported', 3.2, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON
        assertEquals 'M48 800 ceded cumulated reported', 100, quotaShare20.outClaimsCeded[1].reportedCumulated, EPSILON
        assertEquals 'M48 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'M48 800 ceded cumulated paid', 100, quotaShare20.outClaimsCeded[1].paidCumulated, EPSILON
        assertEquals 'M48 800 ceded reserved', 0, quotaShare20.outClaimsCeded[1].reserved(), EPSILON
        assertEquals 'M48 800 ceded outstanding', 0, quotaShare20.outClaimsCeded[1].outstanding(), EPSILON
        assertEquals 'M48 800 ceded ibnr', 0, quotaShare20.outClaimsCeded[1].ibnr(), EPSILON
        assertEquals 'M36 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M36 1000 ceded incremental reported', 36, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M36 1000 ceded cumulated reported', 136, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M36 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M36 1000 ceded cumulated paid', 110, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M36 1000 ceded reserved', 30, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'M36 1000 ceded outstanding', 26, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M36 1000 ceded ibnr', 4, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'P5 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'M48 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M48 1000 ceded incremental reported', 4, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'M48 1000 ceded cumulated reported', 140, quotaShare20.outClaimsCeded[0].reportedCumulated, EPSILON
        assertEquals 'M48 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'M48 1000 ceded cumulated paid', 140, quotaShare20.outClaimsCeded[0].paidCumulated, EPSILON
        assertEquals 'M48 1000 ceded reserved', 0, quotaShare20.outClaimsCeded[0].reserved(), EPSILON
        assertEquals 'M48 1000 ceded outstanding', 0, quotaShare20.outClaimsCeded[0].outstanding(), EPSILON
        assertEquals 'M48 1000 ceded ibnr', 0, quotaShare20.outClaimsCeded[0].ibnr(), EPSILON
    }

}


