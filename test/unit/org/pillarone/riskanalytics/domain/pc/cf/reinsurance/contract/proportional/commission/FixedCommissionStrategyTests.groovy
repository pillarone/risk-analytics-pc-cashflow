package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FixedCommissionStrategyTests extends GroovyTestCase {

    void testUsage() {
        ICommissionStrategy commissionStrategy =
        CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d])

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumWritten: 200,
                premiumPaid: 200, commission: -50, commissionFixed: -40, commissionVariable: -10)
        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumWritten: 100,
                premiumPaid: 100, commission: -5, commissionFixed: -5)
        List underwritingInfos = [underwritingInfo200, underwritingInfo100]

        commissionStrategy.getCalculator(commissionStrategy.initialLossCarriedForward).calculateCommission(null, underwritingInfos, false, 0)

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -200 * 0.3, underwritingInfos[0].commission
        assertEquals ' underwritingInfo200', -200 * 0.3, underwritingInfos[0].commissionFixed
        assertEquals ' underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo200', -100 * 0.3, underwritingInfos[1].commission
        assertEquals ' underwritingInfo200', -100 * 0.3, underwritingInfos[1].commissionFixed
        assertEquals ' underwritingInfo200', 0d, underwritingInfos[1].commissionVariable
    }

    /**
     * Additive: commission is added to prior commission. In a Reinsurance with Bouquet Commission, for example,
     * where all the commissions are wired in series and only one may be applicable to any given uwinfo packet,
     * each reinsurance contract should add its commission to the packet's prior commission.
     */
    void testUsageAdditive() {
        ICommissionStrategy commissionStrategy =
        CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, [commission: 0.3d])

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumWritten: 200, premiumPaid: 200,
                commission: -50, commissionFixed: -40, commissionVariable: -10)
        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumWritten: 100, premiumPaid: 100,
                commission: -5, commissionFixed: -5)
        List underwritingInfos = [underwritingInfo200, underwritingInfo100]

        commissionStrategy.getCalculator(commissionStrategy.initialLossCarriedForward).calculateCommission null, underwritingInfos, true, 0

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', -50 - 200 * 0.3, underwritingInfos[0].commission
        assertEquals ' underwritingInfo200', -40 - 200 * 0.3, underwritingInfos[0].commissionFixed
        assertEquals ' underwritingInfo200', -10d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo200', -5 - 100 * 0.3, underwritingInfos[1].commission
        assertEquals ' underwritingInfo200', -5 - 100 * 0.3, underwritingInfos[1].commissionFixed
        assertEquals ' underwritingInfo200', 0d, underwritingInfos[1].commissionVariable
    }
}
