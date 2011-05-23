package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket

/**
 * @author shartmann (at) munichre (dot) com
 */
class NoCommissionStrategyTests extends GroovyTestCase {

    void testUsage() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: 100, commission: 0)
        CededUnderwritingInfoPacket underwritingInfo100plus5 = new CededUnderwritingInfoPacket(premiumPaid: 100, commission: 5)
        CededUnderwritingInfoPacket underwritingInfo200plus50 = new CededUnderwritingInfoPacket(premiumPaid: 200, commission: 50)
        List underwritingInfos = [underwritingInfo100, underwritingInfo100plus5, underwritingInfo200plus50]

        commissionStrategy.calculator.calculateCommission null, underwritingInfos, false, false

        assertEquals '# outUnderwritingInfo packets', 3, underwritingInfos.size()
        assertEquals 'underwritingInfo100', 0, underwritingInfos[0].commission
        assertEquals 'underwritingInfo100plus5', 5, underwritingInfos[1].commission
        assertEquals 'underwritingInfo200plus50', 50, underwritingInfos[2].commission

    }

    /**
     * passing isAdditive=true doesn't really make sense when there is no commission, but we test that the result is the same anyway
     */
    void testUsageAdditive() {
        ICommissionStrategy commissionStrategy =
            CommissionStrategyType.getStrategy(CommissionStrategyType.NOCOMMISSION, [:])

        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: 100, commission: 0)
        CededUnderwritingInfoPacket underwritingInfo100plus5 = new CededUnderwritingInfoPacket(premiumPaid: 100, commission: 5)
        CededUnderwritingInfoPacket underwritingInfo200plus50 = new CededUnderwritingInfoPacket(premiumPaid: 200, commission: 50)
        List underwritingInfos = [underwritingInfo100, underwritingInfo100plus5, underwritingInfo200plus50]

        commissionStrategy.calculator.calculateCommission null, underwritingInfos, false, true

        assertEquals '# outUnderwritingInfo packets', 3, underwritingInfos.size()
        assertEquals 'underwritingInfo100', 0, underwritingInfos[0].commission
        assertEquals 'underwritingInfo100plus5', 5, underwritingInfos[1].commission
        assertEquals 'underwritingInfo200plus50', 50, underwritingInfos[2].commission

    }
}
