package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker

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
        assertEquals 'P0 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P0 ceded incremental reported', 60, quotaShare20.outClaimsCeded[0].reportedIncremental

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 ceded incremental paid', 80, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P1 ceded incremental reported', 60, quotaShare20.outClaimsCeded[0].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 ceded incremental paid', 60, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2 ceded incremental reported', 40d, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 ceded incremental paid', 30, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3 ceded incremental reported', 36, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 ceded incremental paid', 30, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P4 ceded incremental reported', 4, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON


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
        assertEquals 'P0.0 ceded incremental paid', 2, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P0.0 ceded incremental reported', 140, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0.1 ceded incremental paid', 18d, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P0.1 ceded incremental reported', 20d, quotaShare20.outClaimsCeded[1].reportedIncremental, EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P1 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 ceded incremental paid', 100, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P1 ceded incremental reported', 20, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 ceded incremental paid', 20, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2 ceded incremental reported', 20d, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()
        assertEquals 'P3 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 ceded incremental paid', 60, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P4 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental, EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        claims = claimRoot.getClaimCashflowPackets(periodCounter, false)
        quotaShare20.inClaims.addAll(claims)
        quotaShare20.doCalculation()

        assertEquals 'P5 number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }
}
