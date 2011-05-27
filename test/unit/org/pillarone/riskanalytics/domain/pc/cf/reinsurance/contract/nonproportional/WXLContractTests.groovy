package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.PremiumBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): claims with different patterns
class WXLContractTests extends GroovyTestCase {
    public static final Double EPSILON = 1E-10

    PatternPacket annualReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.0d, 0.7d, 0.8d, 0.95d, 1.0d])
    PatternPacket annualFastReportingPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.8d, 0.9d, 0.95d, 0.98d, 1.0d])
    PatternPacket annualReportingPatternInclFirst = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.3d, 0.6d, 0.8d, 0.98d, 1.0d])
    PatternPacket annualPayoutPattern = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0d, 0.4d, 0.7d, 0.85d, 1.0d])
    PatternPacket annualPayoutPattern2 = PatternPacketTests.getPattern([0, 12, 24, 36, 48], [0.4d, 0.6d, 0.75d, 0.9d, 1.0d])

    PatternPacket payoutPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.01d, 0.1d, 0.6d, 0.7d, 1d])
    PatternPacket reportingPattern = PatternPacketTests.getPattern([0, 3, 12, 24, 48], [0.7d, 0.8d, 0.9d, 1d, 1d])

    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)
    DateTime date20110418 = new DateTime(2011,4,18,0,0,0,0)
    DateTime date20110701 = new DateTime(2011,7,1,0,0,0,0)
    DateTime date20120101 = new DateTime(2012,1,1,0,0,0,0)

    static ReinsuranceContract getWXLContract(double attachmentPoint, double limit, double aggregateLimit,
                                              double aggregateDeductible, double premium,
                                              List<Double> reinstatementPremiumFactors,
                                              DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        return new ReinsuranceContract(
                parmContractStrategy : ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, [
                    'aggregateDeductible': aggregateDeductible, 'attachmentPoint': attachmentPoint,
                    'limit': limit, 'aggregateLimit': aggregateLimit, 'premiumBase': PremiumBase.ABSOLUTE,
                    'premium': premium,
                    'premiumAllocation': PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
                    'reinstatementPremiums': new TableMultiDimensionalParameter(reinstatementPremiumFactors, ['Reinstatement Premium'])]),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])

    }


    /**
     * claims occur in different periods, make sure both get the whole cover or more generally a new contract instance
     * no reinstatements and no aggregate deductible applied
     */
    void testIndependenceOfContractsPerPeriod() {
        ReinsuranceContract wxl = getWXLContract(20, 30, 100, 0, 100, [0.2d], date20110101)
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        GrossClaimRoot claimRoot50 = new GrossClaimRoot(-50, ClaimType.SINGLE,
                date20110418, date20110418, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims50 = claimRoot50.getClaimCashflowPackets(periodCounter, true)
        wxl.inClaims.addAll(claims50)
        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        wxl.doCalculation()
        assertEquals 'number of ceded claims', 1, wxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimate', 30, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P0.0 ceded incremental paid', 0, wxl.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P0.0 ceded incremental reported', 0, wxl.outClaimsCeded[0].reportedIncremental
        assertEquals 'P0.0 ceded premium written', -100, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -100, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -100, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        GrossClaimRoot claimRoot70 = new GrossClaimRoot(-70, ClaimType.SINGLE,
                date20120101, date20120101, annualPayoutPattern, annualReportingPatternInclFirst)
        List<ClaimCashflowPacket> claims70 = claimRoot70.getClaimCashflowPackets(periodCounter, true)
        wxl.inClaims.addAll(claims70)
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P1.1 ceded ultimate', 30, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P1.1 ceded incremental paid', 0, wxl.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P1.1 ceded incremental reported', 1, wxl.outClaimsCeded[0].reportedIncremental
        assertEquals 'P1.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P1.0 ceded incremental paid', 0, wxl.outClaimsCeded[1].paidIncremental
        assertEquals 'P1.0 ceded incremental reported', 10, wxl.outClaimsCeded[1].reportedIncremental


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P2.1 ceded ultimate', 0, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P2.1 ceded incremental paid', 8, wxl.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P2.1 ceded incremental reported', 21, wxl.outClaimsCeded[0].reportedIncremental
        assertEquals 'P2.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P2.0 ceded incremental paid', 15, wxl.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P2.0 ceded incremental reported', 10, wxl.outClaimsCeded[1].reportedIncremental, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P3.1 ceded ultimate', 0, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P3.1 ceded incremental paid', 21, wxl.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P3.1 ceded incremental reported', 8, wxl.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P3.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P3.0 ceded incremental paid', 7.5, wxl.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P3.0 ceded incremental reported', 9, wxl.outClaimsCeded[1].reportedIncremental, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 2, wxl.outClaimsCeded.size()
        assertEquals 'P4.1 ceded ultimate', 0, wxl.outClaimsCeded[0].ultimate()
        assertEquals 'P4.1 ceded incremental paid', 1, wxl.outClaimsCeded[0].paidIncremental, EPSILON
        assertEquals 'P4.1 ceded incremental reported', 0, wxl.outClaimsCeded[0].reportedIncremental, EPSILON
        assertEquals 'P4.0 ceded ultimate', 0, wxl.outClaimsCeded[1].ultimate()
        assertEquals 'P4.0 ceded incremental paid', 7.5, wxl.outClaimsCeded[1].paidIncremental, EPSILON
        assertEquals 'P4.0 ceded incremental reported', 1, wxl.outClaimsCeded[1].reportedIncremental, EPSILON

        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        wxl.inClaims.addAll(claimRoot50.getClaimCashflowPackets(periodCounter, false))
        wxl.inClaims.addAll(claimRoot70.getClaimCashflowPackets(periodCounter, false))
        wxl.doCalculation()

        assertEquals 'number of ceded claims', 1, wxl.outClaimsCeded.size()
        assertEquals 'P5 summed ceded reported', 0, wxl.outClaimsCeded.reportedIncremental.sum()
        assertEquals 'P5 summed ceded paid', 0, wxl.outClaimsCeded.paidIncremental.sum()
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 2.5 reinstatements, aggregate limit
     * is not limiting cover
     */
    void testAggregateDeductibleAndLimits() {
        ReinsuranceContract wxl = getWXLContract(100, 200, 1000, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400), getBaseClaim(-400), getBaseClaim(-400),
                getBaseClaim(-400), getBaseClaim(-400)]
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, true)

        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 200, 200], wxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 200, 200], wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P0.0 ceded incremental paids', [0d] * 5, wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P0.0 ceded premium written', -800, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 60, 140], wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P1 ceded premium written', -480, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 140, 60], wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P2 ceded premium written', -720, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', -720, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', -720, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P3 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P3 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P4 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P4 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    /**
     * multiple claims, aggregate deductible delays payments to second period, 1 reinstatement only, aggregate limit with effect
     */
    void testAggregateDeductibleAndLimits2() {
        ReinsuranceContract wxl = getWXLContract(100, 200, 400, 500, 800, [0.6d], date20110101)
        PeriodScope periodScope = wxl.iterationScope.periodScope
        IPeriodCounter periodCounter = periodScope.periodCounter

        List<GrossClaimRoot> claimRoots = [getBaseClaim(-400), getBaseClaim(-400), getBaseClaim(-400),
                getBaseClaim(-400), getBaseClaim(-400)]
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, true)

        UnderwritingInfoPacket uw120 = new UnderwritingInfoPacket(premiumWritten: 120, premiumPaid: 100,
                                            exposure: new ExposureInfo(periodScope));
        wxl.inUnderwritingInfo.add(uw120)

        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P0.0 ceded ultimates', [0, 0, 100, 200, 100], wxl.outClaimsCeded*.ultimate()
        assertEquals 'P0.0 ceded incremental reported', [0, 0, 100, 200, 100], wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P0.0 ceded incremental paids', [0d] * 5, wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P0.0 ceded premium written', -800, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P0.0 ceded premium paid', -800, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P0.0 ceded premium fixed', -800, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P0.0 ceded premium variable', 0.0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P0.0 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P0.0 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P0.0 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P1 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P1 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P1 ceded incremental paids', [0, 0, 0, 60, 140], wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P1 ceded premium written', -480, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P1 ceded premium paid', -480, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P1 ceded premium variable', -480, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P1 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P1 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P1 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P2 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P2 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P2 ceded incremental paids', [0, 0, 100, 100, 0], wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P2 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P2 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P2 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P2 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P2 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P2 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P3 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P3 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P3 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P3 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P3 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P3 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P3 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P3 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON


        wxl.reset()
        wxl.iterationScope.periodScope.prepareNextPeriod()
        addClaimCashflowOfCurrentPeriod(wxl, claimRoots, periodCounter, false)
        wxl.doCalculation()
        assertEquals 'number of ceded claims', 5, wxl.outClaimsCeded.size()
        assertEquals 'P4 ceded ultimates', [0d] * 5, wxl.outClaimsCeded*.ultimate()
        assertEquals 'P4 ceded incremental reported', [0d] * 5, wxl.outClaimsCeded*.reportedIncremental
        assertEquals 'P4 ceded incremental paids', [0d] *5, wxl.outClaimsCeded*.paidIncremental
        assertEquals 'P4 ceded premium written', 0, wxl.outUnderwritingInfoCeded[0].premiumWritten
        assertEquals 'P4 ceded premium paid', 0, wxl.outUnderwritingInfoCeded[0].premiumPaid
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].premiumPaidVariable, EPSILON
        assertEquals 'P4 ceded commission', 0, wxl.outUnderwritingInfoCeded[0].commission, EPSILON
        assertEquals 'P4 ceded premium fixed', 0, wxl.outUnderwritingInfoCeded[0].commissionFixed
        assertEquals 'P4 ceded premium variable', 0, wxl.outUnderwritingInfoCeded[0].commissionVariable, EPSILON
    }

    private GrossClaimRoot getBaseClaim(double ultimate) {
        GrossClaimRoot claimRoot = new GrossClaimRoot(ultimate, ClaimType.SINGLE,
                date20110418, date20110418, annualPayoutPattern2, annualFastReportingPattern)
        return claimRoot
    }

    private void addClaimCashflowOfCurrentPeriod(ReinsuranceContract wxl, List<GrossClaimRoot> baseClaims,
                                                 IPeriodCounter periodCounter, boolean firstPeriod) {
        for (GrossClaimRoot baseClaim : baseClaims) {
            List<ClaimCashflowPacket> claims = baseClaim.getClaimCashflowPackets(periodCounter, firstPeriod)
            wxl.inClaims.addAll(claims)
        }
    }


}
