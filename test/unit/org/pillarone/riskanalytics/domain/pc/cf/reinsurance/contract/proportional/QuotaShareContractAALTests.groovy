package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractAALTests extends GroovyTestCase {

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

    static ReinsuranceContract getQuotaShareContract(double quotaShare, double aal, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, [
                        'quotaShare': quotaShare,
                        'limit': LimitStrategyType.getStrategy(LimitStrategyType.AAL, ['aal' : aal]),
                        'commission': CommissionStrategyType.getNoCommission()
                ]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    /** one claim without development */
    void testUsage() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
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
        assertEquals 'P0 800 ceded ultimate', 160, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 1000 ceded ultimate', 140, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

    /** one claim with one development per period */
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
        assertEquals 'P0.0 ceded ultimate', 160, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0.0 ceded incremental reported', 48, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0.1 ceded ultimate', 140, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0.1 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P0.1 ceded incremental reported', 60, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P0.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P0.2 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P0.2 ceded incremental reported', 72, quotaShare20.outClaimsCeded[2].reportedIncremental

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1.0 ceded incremental paid', 64, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P1.0 ceded incremental reported', 48, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P1.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P1.1 ceded incremental paid', 80, quotaShare20.outClaimsCeded[1].paidIncremental
        assertEquals 'P1.1 ceded incremental reported', 60, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P1.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P1.2 ceded incremental paid', 96, quotaShare20.outClaimsCeded[2].paidIncremental
        assertEquals 'P1.2 ceded incremental reported', 12, quotaShare20.outClaimsCeded[2].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2.0 ceded incremental paid', 48, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2.0 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P2.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P2.1 ceded incremental paid', 12, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P2.1 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P2.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P2.2 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental
        assertEquals 'P2.2 ceded incremental reported', 0, quotaShare20.outClaimsCeded[2].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3.0 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P3.0 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P3.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P3.1 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncremental
        assertEquals 'P3.1 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P3.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P3.2 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental
        assertEquals 'P3.2 ceded incremental reported', 0, quotaShare20.outClaimsCeded[2].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
//        assertEquals 'P4 summed ceded ultimate', 0, quotaShare20.outClaimsCeded.ultimate().sum()
        assertEquals 'P4 summed ceded reported', 0, quotaShare20.outClaimsCeded.reportedIncremental.sum()
        assertEquals 'P4 summed ceded paid', 0, quotaShare20.outClaimsCeded.paidIncremental.sum()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

     /** one claim with two developments in 1st period */
    void testUsageTwoDevelopmentsFirstPeriod() {
                ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
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
        assertEquals 'P0.0 ceded ultimate', 160, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded incremental paid', 1.6, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0.0 ceded incremental reported', 112, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0.1 ceded ultimate', 140, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0.1 ceded incremental paid', 2, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P0.1 ceded incremental reported', 140, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P0.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P0.2 ceded incremental paid', 2.4, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P0.2 ceded incremental reported', 48, quotaShare20.outClaimsCeded[2].reportedIncremental
        assertEquals 'P1.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[3].ultimate()
        assertEquals 'P1.0 ceded incremental paid', 14.4, quotaShare20.outClaimsCeded[3].paidIncremental, EPSILON
        assertEquals 'P1.0 ceded incremental reported', 0, quotaShare20.outClaimsCeded[3].reportedIncremental
        assertEquals 'P1.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[4].ultimate()
        assertEquals 'P1.1 ceded incremental paid', 18, quotaShare20.outClaimsCeded[4].paidIncremental, EPSILON
        assertEquals 'P1.1 ceded incremental reported', 0, quotaShare20.outClaimsCeded[4].reportedIncremental
        assertEquals 'P1.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[5].ultimate()
        assertEquals 'P1.2 ceded incremental paid', 21.6, quotaShare20.outClaimsCeded[5].paidIncremental, EPSILON
        assertEquals 'P1.2 ceded incremental reported', 0, quotaShare20.outClaimsCeded[5].reportedIncremental

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2.0 ceded incremental paid', 80, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2.0 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P2.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P2.1 ceded incremental paid', 100, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P2.1 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P2.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P2.2 ceded incremental paid', 60, quotaShare20.outClaimsCeded[2].paidIncremental, EPSILON
        assertEquals 'P2.2 ceded incremental reported', 0, quotaShare20.outClaimsCeded[2].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3.0 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental
        assertEquals 'P3.0 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P3.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P3.1 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncremental
        assertEquals 'P3.1 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental
        assertEquals 'P3.2 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P3.2 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncremental
        assertEquals 'P3.2 ceded incremental reported', 0, quotaShare20.outClaimsCeded[2].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
//        assertEquals 'P4 summed ceded ultimate', 0, quotaShare20.outClaimsCeded.ultimate().sum()
        assertEquals 'P4 summed ceded reported', 0, quotaShare20.outClaimsCeded.reportedIncremental.sum(), EPSILON
        assertEquals 'P4 summed ceded paid', 0, quotaShare20.outClaimsCeded.paidIncremental.sum(), EPSILON
    }

    /** claims occur in different periods, make sure both get the whole AAL or more generally a new contract instance */
    void testIndependenceOfContractsPerPeriod() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 120, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims800)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimate', 120, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0.0 ceded incremental reported', 48, quotaShare20.outClaimsCeded[0].reportedIncremental

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims1000)
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1.1 ceded ultimate', 120, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1.1 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P1.1 ceded incremental reported', 60, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P1.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P1.0 ceded incremental paid', 64, quotaShare20.outClaimsCeded[1].paidIncremental
        assertEquals 'P1.0 ceded incremental reported', 48, quotaShare20.outClaimsCeded[1].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2.1 ceded incremental paid', 80, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2.1 ceded incremental reported', 60, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P2.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P2.0 ceded incremental paid', 48, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P2.0 ceded incremental reported', 24, quotaShare20.outClaimsCeded[1].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3.1 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3.1 ceded incremental paid', 40, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3.1 ceded incremental reported', 0, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P3.0 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P3.0 ceded incremental paid', 8, quotaShare20.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P3.0 ceded incremental reported', 0, quotaShare20.outClaimsCeded[1].reportedIncremental


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
//        assertEquals 'P4 summed ceded ultimate', 0, quotaShare20.outClaimsCeded.ultimate().sum()
        assertEquals 'P4 summed ceded reported', 0, quotaShare20.outClaimsCeded.reportedIncremental.sum()
        assertEquals 'P4 summed ceded paid', 0, quotaShare20.outClaimsCeded.paidIncremental.sum()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter, false))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P5 summed ceded reported', 0, quotaShare20.outClaimsCeded.reportedIncremental.sum()
        assertEquals 'P5 summed ceded paid', 0, quotaShare20.outClaimsCeded.paidIncremental.sum()
    }

}


