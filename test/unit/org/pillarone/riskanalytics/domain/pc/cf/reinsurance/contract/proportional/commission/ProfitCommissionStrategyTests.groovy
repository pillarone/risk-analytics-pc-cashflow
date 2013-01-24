package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty

/**
 * @author ben.ginsberg (at) intuitive-collaboration (dot) com
 */
class ProfitCommissionStrategyTests extends GroovyTestCase {
    
    private static final double EPSILON = 1E-14

    static ICommissionStrategy getProfitCommission(double profitCommissionRatio = 0.03d,
                                                   double costRatio = 0.2d,
                                                   boolean lossCarriedForwardEnabled = true, // this is the default value
                                                   double initialLossCarriedForward = 20d,
                                                   double commissionRatio = 0d /* "prior" fixed commission; default=0 */) {
        CommissionStrategyType.getStrategy(CommissionStrategyType.PROFITCOMMISSION, [
                profitCommissionRatio: profitCommissionRatio,
                costRatio: costRatio,
                lossCarriedForwardEnabled: lossCarriedForwardEnabled,
                initialLossCarriedForward: initialLossCarriedForward,
                commissionRatio: commissionRatio,
                useClaims: BasedOnClaimProperty.PAID
        ])
    }

    void testUsage() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket ClaimCashflowPacket = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        List<ClaimCashflowPacket> claims = [ClaimCashflowPacket]

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 0)
        List<CededUnderwritingInfoPacket> underwritingInfo = [underwritingInfo100]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, false)

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo100 commission', 0.8999999999999999, underwritingInfo[0].commission
        assertEquals 'underwritingInfo100 variable commission', 0.8999999999999999, underwritingInfo[0].commissionVariable
        assertEquals 'underwritingInfo100 fixed commission', 0d, underwritingInfo[0].commissionFixed

    }

    /**
     *  Test that the total written premium is correctly distributed/apportioned/split/partitioned across individual premiums
     */
    void testApportioning() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket ClaimCashflowPacket = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        List<ClaimCashflowPacket> claims = [ClaimCashflowPacket]

        CededUnderwritingInfoPacket underwritingInfo20 = new CededUnderwritingInfoPacket(premiumPaid: -20, commission: 1, commissionFixed: 0.2, commissionVariable: 0.8)
        CededUnderwritingInfoPacket underwritingInfo30 = new CededUnderwritingInfoPacket(premiumPaid: -30, commission: 1, commissionFixed: 0.1, commissionVariable: 0.9)
        CededUnderwritingInfoPacket underwritingInfo50 = new CededUnderwritingInfoPacket(premiumPaid: -50, commission: 1, commissionFixed: 0.3, commissionVariable: 0.7)
        List underwritingInfo = [underwritingInfo20, underwritingInfo30, underwritingInfo50]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, true) // test that prior commission is added

        // todo(sku): adjust values due to new definition in PMO-2198
    }

    void testAddition1() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket ClaimCashflowPacket = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        List<ClaimCashflowPacket> claims = [ClaimCashflowPacket]

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 1)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, false)

        // todo(sku): adjust values due to new definition in PMO-2198
    }

    void testAddition2() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket ClaimCashflowPacket = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        List<ClaimCashflowPacket> claims = [ClaimCashflowPacket]

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200, commission: 7)
        List underwritingInfo = [underwritingInfo200]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, false)

        // todo(sku): adjust values due to new definition in PMO-2198
    }

    void testProfitCommissionWithPriorFixedCommission() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0.02d)

        ClaimCashflowPacket ClaimCashflowPacket = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        List<ClaimCashflowPacket> claims = [ClaimCashflowPacket]

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 1,
                commissionVariable: 0.9, commissionFixed: 0.1)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, true)

        // todo(sku): adjust values due to new definition in PMO-2198
    }

    void testUnderflowProtectionBoundary() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket claim1 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        ClaimCashflowPacket claim2 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 10d, null)
        List<ClaimCashflowPacket> claims = [claim1, claim2]

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 5)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, false)
        // todo(sku): adjust values due to new definition in PMO-2198
    }

    void testUnderflowProtectionPastBoundary() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket claim1 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 50d, null)
        ClaimCashflowPacket claim2 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 10d, null)
        ClaimCashflowPacket claim3 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 1d, null)
        List<ClaimCashflowPacket> claims = [claim1, claim2, claim3]

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 1)
        List underwritingInfo = [underwritingInfo100]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, false)
        // todo(sku): adjust values due to new definition in PMO-2198
    }

    void testUsageWithClaimDevelopmentLeanPacket() {
        ICommissionStrategy commissionStrategy = getProfitCommission(0.03d, 0.2d, true, 20d, 0d)

        ClaimCashflowPacket claim1 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 30d, null)
        ClaimCashflowPacket claim2 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 10d, null)
        ClaimCashflowPacket claim3 = ClaimCashflowPacketTests.getClaimCashflowPacket(0d, 10d, null)
        List claims = [claim1, claim2, claim3]

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 0)
        List<CededUnderwritingInfoPacket> underwritingInfo = [underwritingInfo100]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfo, false)

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        // todo(sku): adjust values due to new definition in PMO-2198
    }
}
