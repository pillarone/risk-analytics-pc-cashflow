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
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints

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
                parmCover : CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, [filter: FilterStrategyType.getDefault()]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    void setUp() {
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
    }

    /** three claims without development */
    void testUsage() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 300, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims800)

        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1000)

        GrossClaimRoot claimRoot1200 = new GrossClaimRoot(-1200, ClaimType.AGGREGATED,
                date20110418, date20110701, trivialPayoutPattern, trivialReportingPattern)
        List<ClaimCashflowPacket> claims1200 = claimRoot1200.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 800 ceded ratioReported incremental', 100, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P0 800 ceded ratioReported cumulated', 100, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0 800 ceded paid incremental', 100, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed
        assertEquals 'P0 800 ceded paid cumulated', 100, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed
        assertEquals 'P0 800 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0 800 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0 800 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed()

        assertEquals 'P0 1000 ceded ultimate', 200, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0 1000 ceded ratioReported incremental', 200, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed
        assertEquals 'P0 1000 ceded ratioReported cumulated', 200, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed
        assertEquals 'P0 1000 ceded paid incremental', 200, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed
        assertEquals 'P0 1000 ceded paid cumulated', 200, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed
        assertEquals 'P0 1000 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P0 1000 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[1].outstandingIndexed()
        assertEquals 'P0 1000 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[1].ibnrIndexed()

        assertEquals 'P0 1200 ceded ultimate', 240, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P0 1200 ceded ratioReported incremental', 240, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed
        assertEquals 'P0 1200 ceded ratioReported cumulated', 240, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed
        assertEquals 'P0 1200 ceded paid incremental', 240, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed
        assertEquals 'P0 1200 ceded paid cumulated', 240, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed
        assertEquals 'P0 1200 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'P0 1200 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[2].outstandingIndexed()
        assertEquals 'P0 1200 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[2].ibnrIndexed()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

    /** three claims with one development per period, includes negative ibnrIndexed and outstandingIndexed in single claims
     *  with a correction over the portfolio */
    void testUsageDevelopment() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims800)

        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1000)

        GrossClaimRoot claimRoot1200 = new GrossClaimRoot(-1200, ClaimType.AGGREGATED,
                date20110418, date20110701, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1200 = claimRoot1200.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0 800 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P0 800 ceded cumulated ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P0 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P0 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P0 800 ceded reservedIndexed', 100, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P0 800 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P0 800 ceded ibnrIndexed', 100, quotaShare20.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P0 1000 ceded ultimate', 200, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P0 1000 ceded incremental ratioReported', 48, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P0 1000 ceded cumulated ratioReported', 48, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P0 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'P0 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'P0 1000 ceded reservedIndexed', 200, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P0 1000 ceded outstandingIndexed', 48, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P0 1000 ceded ibnrIndexed', 152, quotaShare20.outClaimsCeded[1].ibnrIndexed()
        assertEquals 'P0 1200 ceded ultimate', 240, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P0 1200 ceded incremental ratioReported', 72, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed
        assertEquals 'P0 1200 ceded cumulated ratioReported', 72, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed
        assertEquals 'P0 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'P0 1200 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'P0 1200 ceded reservedIndexed', 240, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'P0 1200 ceded outstandingIndexed', 72, quotaShare20.outClaimsCeded[2].outstandingIndexed()
        assertEquals 'P0 1200 ceded ibnrIndexed', 168, quotaShare20.outClaimsCeded[2].ibnrIndexed()

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P1 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P1 800 ceded incremental ratioReported', 48, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'P1 800 ceded cumulated ratioReported', 48, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'P1 800 ceded incremental paid', 4, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P1 800 ceded cumulated paid', 4, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P1 800 ceded reservedIndexed', 96, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'P1 800 ceded outstandingIndexed', 44, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'P1 800 ceded ibnrIndexed', 52, quotaShare20.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'P1 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P1 1000 ceded incremental ratioReported', 60, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P1 1000 ceded cumulated ratioReported', 108, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P1 1000 ceded incremental paid', 80, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'P1 1000 ceded cumulated paid', 80, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'P1 1000 ceded reservedIndexed', 120, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P1 1000 ceded outstandingIndexed', 28, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P1 1000 ceded ibnrIndexed', 92, quotaShare20.outClaimsCeded[1].ibnrIndexed()
        assertEquals 'P1 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P1 1200 ceded incremental ratioReported', 72, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed
        assertEquals 'P1 1200 ceded cumulated ratioReported', 144, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed
        assertEquals 'P1 1200 ceded incremental paid', 96, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'P1 1200 ceded cumulated paid', 96, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'P1 1200 ceded reservedIndexed', 144, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'P1 1200 ceded outstandingIndexed', 48, quotaShare20.outClaimsCeded[2].outstandingIndexed()
        assertEquals 'P1 1200 ceded ibnrIndexed', 96, quotaShare20.outClaimsCeded[2].ibnrIndexed()


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P2 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P2 800 ceded incremental ratioReported', 32, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2 800 ceded cumulated ratioReported', 80, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2 800 ceded incremental paid', 48, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P2 800 ceded cumulated paid', 52, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P2 800 ceded reservedIndexed', 48, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'P2 800 ceded outstandingIndexed', 28, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P2 800 ceded ibnrIndexed', 20, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'P2 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P2 1000 ceded incremental ratioReported', 40, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2 1000 ceded cumulated ratioReported', 148, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2 1000 ceded incremental paid', 60, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'P2 1000 ceded cumulated paid', 140, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'P2 1000 ceded reservedIndexed', 60, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P2 1000 ceded outstandingIndexed', 8, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P2 1000 ceded ibnrIndexed', 52, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'P2 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P2 1200 ceded incremental ratioReported', 48, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'P2 1200 ceded cumulated ratioReported', 192, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'P2 1200 ceded incremental paid', 72, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'P2 1200 ceded cumulated paid', 168, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'P2 1200 ceded reservedIndexed', 72, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'P2 1200 ceded outstandingIndexed', 24, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'P2 1200 ceded ibnrIndexed', 48, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P3 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P3 800 ceded incremental ratioReported', 28.8, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3 800 ceded cumulated ratioReported', 108.8, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P3 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P3 800 ceded cumulated paid', 76, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P3 800 ceded reservedIndexed', 24, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'P3 800 ceded outstandingIndexed', 32.8, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P3 800 ceded ibnrIndexed', -8.8, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'P3 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P3 1000 ceded incremental ratioReported', 36, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3 1000 ceded cumulated ratioReported', 184, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P3 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'P3 1000 ceded cumulated paid', 170, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'P3 1000 ceded reservedIndexed', 30, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P3 1000 ceded outstandingIndexed', 14, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P3 1000 ceded ibnrIndexed', 16, quotaShare20.outClaimsCeded[1].ibnrIndexed()
        assertEquals 'P3 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P3 1200 ceded incremental ratioReported', 43.2, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'P3 1200 ceded cumulated ratioReported', 235.2, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'P3 1200 ceded incremental paid', 36, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'P3 1200 ceded cumulated paid', 204, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'P3 1200 ceded reservedIndexed', 36, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'P3 1200 ceded outstandingIndexed', 31.2, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'P3 1200 ceded ibnrIndexed', 4.8, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'P4 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P4 800 ceded incremental ratioReported', 3.2, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'P4 800 ceded cumulated ratioReported', 112, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'P4 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'P4 800 ceded cumulated paid', 100, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'P4 800 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'P4 800 ceded outstandingIndexed', 12, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'P4 800 ceded ibnrIndexed', -12, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'P4 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'P4 1000 ceded incremental ratioReported', 4, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'P4 1000 ceded cumulated ratioReported', 188, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'P4 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'P4 1000 ceded cumulated paid', 200, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'P4 1000 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'P4 1000 ceded outstandingIndexed', -12, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'P4 1000 ceded ibnrIndexed', 12, quotaShare20.outClaimsCeded[1].ibnrIndexed()
        assertEquals 'P4 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'P4 1200 ceded incremental ratioReported', 4.8, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'P4 1200 ceded cumulated ratioReported', 240, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'P4 1200 ceded incremental paid', 36, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'P4 1200 ceded cumulated paid', 240, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'P4 1200 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'P4 1200 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'P4 1200 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P5 number of ceded claims', 0, quotaShare20.outClaimsCeded.size()
    }

     /** three claims with two developments in 1st period, includes negative outstandingIndexed in single claims
      *  with a correction over the portfolio */
    void testUsageTwoDevelopmentsFirstPeriod() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 300, date20110101)
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims800)

        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1000)

        GrossClaimRoot claimRoot1200 = new GrossClaimRoot(-1200, ClaimType.AGGREGATED,
                date20110418, date20110701, payoutPattern, reportingPattern)
        List<ClaimCashflowPacket> claims1200 = claimRoot1200.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 6, quotaShare20.outClaimsCeded.size()
        assertEquals 'M0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M0 800 ceded incremental ratioReported', 52, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed
        assertEquals 'M0 800 ceded cumulated ratioReported', 52, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed
        assertEquals 'M0 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M0 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M0 800 ceded reservedIndexed', 100, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'M0 800 ceded outstandingIndexed', 52, quotaShare20.outClaimsCeded[0].outstandingIndexed()
        assertEquals 'M0 800 ceded ibnrIndexed', 48, quotaShare20.outClaimsCeded[0].ibnrIndexed()
        assertEquals 'M0 1000 ceded ultimate', 200, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M0 1000 ceded incremental ratioReported', 140, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M0 1000 ceded cumulated ratioReported', 140, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M0 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M0 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M0 1000 ceded reservedIndexed', 200, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'M0 1000 ceded outstandingIndexed', 140, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M0 1000 ceded ibnrIndexed', 60, quotaShare20.outClaimsCeded[1].ibnrIndexed()
        assertEquals 'M0 1200 ceded ultimate', 240, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M0 1200 ceded incremental ratioReported', 168, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed
        assertEquals 'M0 1200 ceded cumulated ratioReported', 168, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed
        assertEquals 'M0 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'M0 1200 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'M0 1200 ceded reservedIndexed', 240, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'M0 1200 ceded outstandingIndexed', 168, quotaShare20.outClaimsCeded[2].outstandingIndexed()
        assertEquals 'M0 1200 ceded ibnrIndexed', 72, quotaShare20.outClaimsCeded[2].ibnrIndexed()
        assertEquals 'M3 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[3].ultimate()
        assertEquals 'M3 800 ceded incremental ratioReported', 16, quotaShare20.outClaimsCeded[3].reportedIncrementalIndexed, EPSILON
        assertEquals 'M3 800 ceded cumulated ratioReported', 68, quotaShare20.outClaimsCeded[3].reportedCumulatedIndexed, EPSILON
        assertEquals 'M3 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[3].paidIncrementalIndexed, EPSILON
        assertEquals 'M3 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[3].paidCumulatedIndexed, EPSILON
        assertEquals 'M3 800 ceded reservedIndexed', 100, quotaShare20.outClaimsCeded[3].reservedIndexed()
        assertEquals 'M3 800 ceded outstandingIndexed', 68, quotaShare20.outClaimsCeded[3].outstandingIndexed(), EPSILON
        assertEquals 'M3 800 ceded ibnrIndexed', 32, quotaShare20.outClaimsCeded[3].ibnrIndexed(), EPSILON
        assertEquals 'M3 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[4].ultimate()
        assertEquals 'M3 1000 ceded incremental ratioReported', 20, quotaShare20.outClaimsCeded[4].reportedIncrementalIndexed, EPSILON
        assertEquals 'M3 1000 ceded cumulated ratioReported', 160, quotaShare20.outClaimsCeded[4].reportedCumulatedIndexed, EPSILON
        assertEquals 'M3 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[4].paidIncrementalIndexed, EPSILON
        assertEquals 'M3 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[4].paidCumulatedIndexed, EPSILON
        assertEquals 'M3 1000 ceded reservedIndexed', 200, quotaShare20.outClaimsCeded[4].reservedIndexed()
        assertEquals 'M3 1000 ceded outstandingIndexed', 160, quotaShare20.outClaimsCeded[4].outstandingIndexed(), EPSILON
        assertEquals 'M3 1000 ceded ibnrIndexed', 40, quotaShare20.outClaimsCeded[4].ibnrIndexed(), EPSILON
        assertEquals 'M3 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[5].ultimate()
        assertEquals 'M3 1200 ceded incremental ratioReported', 24, quotaShare20.outClaimsCeded[5].reportedIncrementalIndexed, EPSILON
        assertEquals 'M3 1200 ceded cumulated ratioReported', 192, quotaShare20.outClaimsCeded[5].reportedCumulatedIndexed, EPSILON
        assertEquals 'M3 1200 ceded incremental paid', 3.6, quotaShare20.outClaimsCeded[5].paidIncrementalIndexed, EPSILON
        assertEquals 'M3 1200 ceded cumulated paid', 3.6, quotaShare20.outClaimsCeded[5].paidCumulatedIndexed, EPSILON
        assertEquals 'M3 1200 ceded reservedIndexed', 236.4, quotaShare20.outClaimsCeded[5].reservedIndexed()
        assertEquals 'M3 1200 ceded outstandingIndexed', 188.4, quotaShare20.outClaimsCeded[5].outstandingIndexed(), EPSILON
        assertEquals 'M3 1200 ceded ibnrIndexed', 48, quotaShare20.outClaimsCeded[5].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'M12 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M12 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M12 800 ceded incremental ratioReported', 16, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M12 800 ceded cumulated ratioReported', 84, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M12 800 ceded incremental paid', 78.4, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M12 800 ceded cumulated paid', 78.4, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M12 800 ceded reservedIndexed', 21.6, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M12 800 ceded outstandingIndexed', 5.6, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M12 800 ceded ibnrIndexed', 16, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'M12 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M12 1000 ceded incremental ratioReported', 20, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M12 1000 ceded cumulated ratioReported', 180, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M12 1000 ceded incremental paid', 98, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M12 1000 ceded cumulated paid', 98, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M12 1000 ceded reservedIndexed', 102, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'M12 1000 ceded outstandingIndexed', 82, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M12 1000 ceded ibnrIndexed', 20, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M12 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M12 1200 ceded incremental ratioReported', 24, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'M12 1200 ceded cumulated ratioReported', 216, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'M12 1200 ceded incremental paid', 120, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'M12 1200 ceded cumulated paid', 123.6, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'M12 1200 ceded reservedIndexed', 116.4, quotaShare20.outClaimsCeded[2].reservedIndexed(), EPSILON
        assertEquals 'M12 1200 ceded outstandingIndexed', 92.4, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'M12 1200 ceded ibnrIndexed', 24, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'M24 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M24 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M24 800 ceded incremental ratioReported', 16, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M24 800 ceded cumulated ratioReported', 100, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M24 800 ceded incremental paid', 16, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M24 800 ceded cumulated paid', 94.4, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M24 800 ceded reservedIndexed', 5.6, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M24 800 ceded outstandingIndexed', 5.6, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M24 800 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'M24 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M24 1000 ceded incremental ratioReported', 20, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M24 1000 ceded cumulated ratioReported', 200, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M24 1000 ceded incremental paid', 20, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M24 1000 ceded cumulated paid', 118, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M24 1000 ceded reservedIndexed', 82, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'M24 1000 ceded outstandingIndexed', 82, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M24 1000 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M24 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M24 1200 ceded incremental ratioReported', 24, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'M24 1200 ceded cumulated ratioReported', 240, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'M24 1200 ceded incremental paid', 24, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'M24 1200 ceded cumulated paid', 147.6, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'M24 1200 ceded reservedIndexed', 92.4, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'M24 1200 ceded outstandingIndexed', 92.4, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'M24 1200 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'M36 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M36 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M36 800 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M36 800 ceded cumulated ratioReported', 100, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M36 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M36 800 ceded cumulated paid', 94.4, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M36 800 ceded reservedIndexed', 5.6, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M36 800 ceded outstandingIndexed', 5.6, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M36 800 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'M36 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M36 1000 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M36 1000 ceded cumulated ratioReported', 200, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M36 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M36 1000 ceded cumulated paid', 118, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M36 1000 ceded reservedIndexed', 82, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'M36 1000 ceded outstandingIndexed', 82, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M36 1000 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M36 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M36 1200 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'M36 1200 ceded cumulated ratioReported', 240, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'M36 1200 ceded incremental paid', 0, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'M36 1200 ceded cumulated paid', 147.6, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'M36 1200 ceded reservedIndexed', 92.4, quotaShare20.outClaimsCeded[2].reservedIndexed()
        assertEquals 'M36 1200 ceded outstandingIndexed', 92.4, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'M36 1200 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1200.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'M48 number of ceded claims', 3, quotaShare20.outClaimsCeded.size()
        assertEquals 'M48 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M48 800 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M48 800 ceded cumulated ratioReported', 100, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M48 800 ceded incremental paid', 48, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M48 800 ceded cumulated paid', 142.4, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M48 800 ceded reservedIndexed', -42.4, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M48 800 ceded outstandingIndexed', -42.4, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M48 800 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
        assertEquals 'M48 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M48 1000 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M48 1000 ceded cumulated ratioReported', 200, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M48 1000 ceded incremental paid', 60, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M48 1000 ceded cumulated paid', 178, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M48 1000 ceded reservedIndexed', 22, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'M48 1000 ceded outstandingIndexed', 22, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M48 1000 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M48 1200 ceded ultimate', 0, quotaShare20.outClaimsCeded[2].ultimate()
        assertEquals 'M48 1200 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[2].reportedIncrementalIndexed, EPSILON
        assertEquals 'M48 1200 ceded cumulated ratioReported', 240, quotaShare20.outClaimsCeded[2].reportedCumulatedIndexed, EPSILON
        assertEquals 'M48 1200 ceded incremental paid', 72, quotaShare20.outClaimsCeded[2].paidIncrementalIndexed, EPSILON
        assertEquals 'M48 1200 ceded cumulated paid', 219.6, quotaShare20.outClaimsCeded[2].paidCumulatedIndexed, EPSILON
        assertEquals 'M48 1200 ceded reservedIndexed', 20.4, quotaShare20.outClaimsCeded[2].reservedIndexed(), EPSILON
        assertEquals 'M48 1200 ceded outstandingIndexed', 20.4, quotaShare20.outClaimsCeded[2].outstandingIndexed(), EPSILON
        assertEquals 'M48 1200 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[2].ibnrIndexed(), EPSILON
    }

    /** claims occur in different periods, make sure both get the whole AAL or more generally a new contract instance */
    void testIndependenceOfContractsPerPeriod() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 300, date20110101)
        quotaShare20.parmCoveredPeriod = PeriodStrategyType.getStrategy(PeriodStrategyType.MONTHS, [
                startCover: new DateTime(date20110101), numberOfMonths: 24])
        IPeriodCounter periodCounter = quotaShare20.iterationScope.periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims800)

        quotaShare20.doCalculation()
        assertEquals 'P0 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'M0 800 ceded ultimate', 100, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'M0 800 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M0 800 ceded cumulated ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M0 800 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M0 800 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M0 800 ceded reservedIndexed', 100, quotaShare20.outClaimsCeded[0].reservedIndexed()
        assertEquals 'M0 800 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M0 800 ceded ibnrIndexed', 100, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        GrossClaimRoot claimRoot1000 = new GrossClaimRoot(-1000, ClaimType.AGGREGATED,
                date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims1000 = claimRoot1000.getClaimCashflowPackets(periodCounter)
        quotaShare20.inClaims.addAll(claims1000)
        quotaShare20.doCalculation()

        assertEquals 'P1 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M12 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M12 800 ceded incremental ratioReported', 36, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M12 800 ceded cumulated ratioReported', 36, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M12 800 ceded incremental paid', 4, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M12 800 ceded cumulated paid', 4, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M12 800 ceded reservedIndexed', 96, quotaShare20.outClaimsCeded[1].reservedIndexed()
        assertEquals 'M12 800 ceded outstandingIndexed', 32, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M12 800 ceded ibnrIndexed', 64, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M0 1000 ceded ultimate', 140, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M0 1000 ceded incremental ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M0 1000 ceded cumulated ratioReported', 0, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M0 1000 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M0 1000 ceded cumulated paid', 0, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M0 1000 ceded reservedIndexed', 140, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M0 1000 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M0 1000 ceded ibnrIndexed', 140, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P2 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M24 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M24 800 ceded incremental ratioReported', 32, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M24 800 ceded cumulated ratioReported', 68, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M24 800 ceded incremental paid', 48, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M24 800 ceded cumulated paid', 52, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M24 800 ceded reservedIndexed', 48, quotaShare20.outClaimsCeded[1].reservedIndexed(), EPSILON
        assertEquals 'M24 800 ceded outstandingIndexed', 16, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M24 800 ceded ibnrIndexed', 32, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M12 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M12 1000 ceded incremental ratioReported', 60, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M12 1000 ceded cumulated ratioReported', 60, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M12 1000 ceded incremental paid', 20, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M12 1000 ceded cumulated paid', 20, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M12 1000 ceded reservedIndexed', 120, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M12 1000 ceded outstandingIndexed', 40, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M12 1000 ceded ibnrIndexed', 80, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P3 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M36 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M36 800 ceded incremental ratioReported', 28.8, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M36 800 ceded cumulated ratioReported', 96.8, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M36 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M36 800 ceded cumulated paid', 76, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M36 800 ceded reservedIndexed', 24, quotaShare20.outClaimsCeded[1].reservedIndexed(), EPSILON
        assertEquals 'M36 800 ceded outstandingIndexed', 20.8, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M36 800 ceded ibnrIndexed', 3.2, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M24 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M24 1000 ceded incremental ratioReported', 40, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M24 1000 ceded cumulated ratioReported', 100, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M24 1000 ceded incremental paid', 60, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M24 1000 ceded cumulated paid', 80, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M24 1000 ceded reservedIndexed', 60, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M24 1000 ceded outstandingIndexed', 20, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M24 1000 ceded ibnrIndexed', 40, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON


        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P4 number of ceded claims', 2, quotaShare20.outClaimsCeded.size()
        assertEquals 'M48 800 ceded ultimate', 0, quotaShare20.outClaimsCeded[1].ultimate()
        assertEquals 'M48 800 ceded incremental ratioReported', 3.2, quotaShare20.outClaimsCeded[1].reportedIncrementalIndexed, EPSILON
        assertEquals 'M48 800 ceded cumulated ratioReported', 100, quotaShare20.outClaimsCeded[1].reportedCumulatedIndexed, EPSILON
        assertEquals 'M48 800 ceded incremental paid', 24, quotaShare20.outClaimsCeded[1].paidIncrementalIndexed, EPSILON
        assertEquals 'M48 800 ceded cumulated paid', 100, quotaShare20.outClaimsCeded[1].paidCumulatedIndexed, EPSILON
        assertEquals 'M48 800 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[1].reservedIndexed(), EPSILON
        assertEquals 'M48 800 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[1].outstandingIndexed(), EPSILON
        assertEquals 'M48 800 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[1].ibnrIndexed(), EPSILON
        assertEquals 'M36 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M36 1000 ceded incremental ratioReported', 36, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M36 1000 ceded cumulated ratioReported', 136, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M36 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M36 1000 ceded cumulated paid', 110, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M36 1000 ceded reservedIndexed', 30, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M36 1000 ceded outstandingIndexed', 26, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M36 1000 ceded ibnrIndexed', 4, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON

        quotaShare20.reset()
        quotaShare20.iterationScope.periodScope.prepareNextPeriod()
        quotaShare20.inClaims.addAll(claimRoot800.getClaimCashflowPackets(periodCounter))
        quotaShare20.inClaims.addAll(claimRoot1000.getClaimCashflowPackets(periodCounter))
        quotaShare20.doCalculation()

        assertEquals 'P5 number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'M48 1000 ceded ultimate', 0, quotaShare20.outClaimsCeded[0].ultimate(), EPSILON
        assertEquals 'M48 1000 ceded incremental ratioReported', 4, quotaShare20.outClaimsCeded[0].reportedIncrementalIndexed, EPSILON
        assertEquals 'M48 1000 ceded cumulated ratioReported', 140, quotaShare20.outClaimsCeded[0].reportedCumulatedIndexed, EPSILON
        assertEquals 'M48 1000 ceded incremental paid', 30, quotaShare20.outClaimsCeded[0].paidIncrementalIndexed, EPSILON
        assertEquals 'M48 1000 ceded cumulated paid', 140, quotaShare20.outClaimsCeded[0].paidCumulatedIndexed, EPSILON
        assertEquals 'M48 1000 ceded reservedIndexed', 0, quotaShare20.outClaimsCeded[0].reservedIndexed(), EPSILON
        assertEquals 'M48 1000 ceded outstandingIndexed', 0, quotaShare20.outClaimsCeded[0].outstandingIndexed(), EPSILON
        assertEquals 'M48 1000 ceded ibnrIndexed', 0, quotaShare20.outClaimsCeded[0].ibnrIndexed(), EPSILON
    }

}


