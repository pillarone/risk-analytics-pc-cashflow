package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionBasedOnClaims
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class QuotaShareContractTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])
    PatternPacket trivialPattern = PatternPacket.PATTERN_TRIVIAL;

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
                        'commission': CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION, [
                                    'profitCommissionRatio' : 0.2d, 'commissionRatio' : 0.1d, 'costRatio' : 0.1d,
                                    'lossCarriedForwardEnabled' : false, 'initialLossCarriedForward' : 0d,
                                    'useClaims': CommissionBasedOnClaims.REPORTED])]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    /** claims occur in different periods, make sure both get the whole AAL or more generally a new contract instance */
    void testIndependenceOfContractsPerPeriod() {
        ReinsuranceContract quotaShare20 = getQuotaShareContract(0.2, 120, date20110101)
        PeriodScope periodScope = quotaShare20.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        GrossClaimRoot claimRoot800 = new GrossClaimRoot(-800, ClaimType.AGGREGATED,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims800 = claimRoot800.getClaimCashflowPackets(periodCounter, true)
        quotaShare20.inClaims.addAll(claims800)
        UnderwritingInfoPacket uw1200 = new UnderwritingInfoPacket(premiumWritten: 1200, premiumPaid: 1000,
                                            exposure: new ExposureInfo(periodScope));
        quotaShare20.inUnderwritingInfo.add(uw1200)

        quotaShare20.doCalculation()
        assertEquals 'number of ceded claims', 1, quotaShare20.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimate', 120, quotaShare20.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded incremental paid', 0, quotaShare20.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0.0 ceded incremental reported', 48, quotaShare20.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0.0 ceded premium written', -240, quotaShare20.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -200, quotaShare20.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -200, quotaShare20.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0, quotaShare20.outUnderwritingInfoCeded[0].premiumPaidVariable
        assertEquals 'P0.0 ceded commission', 42.4, quotaShare20.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded premium fixed', 20, quotaShare20.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded premium variable', 22.4, quotaShare20.outUnderwritingInfoCeded[0].commissionVariable, EPSILON

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


