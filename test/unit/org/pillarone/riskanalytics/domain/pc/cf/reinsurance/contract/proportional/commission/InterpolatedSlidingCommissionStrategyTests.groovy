package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValuePerPeriod
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.InterpolatedSlidingCommissionStrategy
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacketTests

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class InterpolatedSlidingCommissionStrategyTests extends GroovyTestCase {
    
    private static final double EPSILON = 1E-14

    private ICommissionStrategy commissionStrategy
    private List<UnderwritingInfoPacket> uwInfo
    private List<ClaimCashflowPacket> claims

    static ICommissionStrategy getInterpolatedSlidingCommissionStrategy(List<Double> lossRatios, List<Double> commissionRates) {

        ICommissionStrategy commissionStrategy = CommissionStrategyType.getStrategy(
                CommissionStrategyType.INTERPOLATEDSLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [lossRatios, commissionRates],
                        [InterpolatedSlidingCommissionStrategy.LOSS_RATIO, InterpolatedSlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
                 'useClaims': CommissionBase.PAID])
        return commissionStrategy
    }

    void testOneRatio() {
        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy([0.35d], [0.2d])

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200, commission: 50)
        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 5)

        ClaimCashflowPacket claim70 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 70, null)
        ClaimCashflowPacket claim10 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 10, null)
        ClaimCashflowPacket claim25 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 25, null)
        ClaimCashflowPacket claim100 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 100, null)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim10, claim70]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 0)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable

        claims.add(claim25)

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 1)
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable

        claims.add(claim100)

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 2)
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 3)
        assertEquals 'underwritingInfo200', 200 * 0.2 + 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.2 + 200 * 0.2, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.2 + 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.2 + 100 * 0.2, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable

        underwritingInfos[0].commission = 25
        underwritingInfos[0].commissionFixed = 10
        underwritingInfos[0].commissionVariable = 15
        underwritingInfos[1].commission = 30
        underwritingInfos[1].commissionFixed = 25
        underwritingInfos[1].commissionVariable = 5
        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 4)
        assertEquals 'underwritingInfo200', 200 * 0.2 + 25, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.2 + 10, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 15d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.2 + 30, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.2 + 25, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 5d, underwritingInfos[1].commissionVariable
    }

    void testTwoRatios() {
        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy([0.3d, 0.5d], [0.4d, 0.3d])

        UnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200, commission: 50,
                commissionFixed: 10, commissionVariable: 40)
        UnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 5,
                commissionFixed: 2, commissionVariable: 3)

        ClaimCashflowPacket claim60 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 60, null)
        ClaimCashflowPacket claim10 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 10, null)
        ClaimCashflowPacket claim20 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 20, null)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim60, claim10]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 0)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.4 + 50, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3 + 10, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 200 * 0.1 + 40d, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.4 + 5, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3 + 2, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.1 + 3, underwritingInfos[1].commissionVariable, EPSILON

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 1)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.4, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.1, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.4, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.1, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(claim20)

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 2)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.4, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.1, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.4, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.1, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 30, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 3)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
//        assertEquals 'underwritingInfo200', 200 * (0.4 * 0.5 + 0.3 * 0.5), underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
//        assertEquals 'underwritingInfo200', 200 * 0.05, underwritingInfos[0].commissionVariable, EPSILON
//        assertEquals 'underwritingInfo100', 100 * (0.4 * 0.5 + 0.3 * 0.5), underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
//        assertEquals 'underwritingInfo100', 100 * 0.05, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 4)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * (0.4 * 0.25 + 0.3 * 0.75), underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.025, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * (0.4 * 0.25 + 0.3 * 0.75), underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.025, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 5)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * (0.4 * 0.25 + 0.3 * 0.75) + 200 * 0.3, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3 + 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.025 + 0d, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * (0.4 * 0.25 + 0.3 * 0.75) + 100 * 0.3, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3 + 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.025 + 0d, underwritingInfos[1].commissionVariable, EPSILON

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 6)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 0.1, null))

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable
    }

    void testManyRatiosIncludingJumps() {
        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy(
                [0.2d, 0.2d, 0.3d, 0.3d, 0.3d, 0.4d, 0.5d, 0.5d],
                [0.6d, 0.5d, 0.4d, 0.3d, 0.2d, 0.1d, 0.05d, 0.01d])

        UnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200, commission: 50, commissionFixed: 10, commissionVariable: 40)
        UnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 5, commissionFixed: 2, commissionVariable: 3)

        ClaimCashflowPacket claim30 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 30, null)

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim30]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 0)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.6 + 50, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01 + 10, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.59 + 40, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.6 + 5, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01 + 2, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.59 + 3, underwritingInfos[1].commissionVariable, EPSILON

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 1)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.6, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.59, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.6, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.59, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 30, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 2)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.5 + 200 * 0.6, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01 + 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.49 + 200 * 0.59, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.5 + 100 * 0.6, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01 + 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.49 + 100 * 0.59, underwritingInfos[1].commissionVariable, EPSILON

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 3)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.5, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.49, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.5, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.49, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 4)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.45, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.44, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.45, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.44, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 5)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo200', 200 * 0.19, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed
        assertEquals 'underwritingInfo100', 100 * 0.19, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 6)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.15, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.14, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.15, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.14, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 7.5, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 7)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * (0.75 * 0.1 + 0.25 * 0.2), underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.115, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * (0.75 * 0.1 + 0.25 * 0.2), underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.115, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 7.5, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 8)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.1, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.09, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.1, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.09, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 9)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * (0.5 * 0.1 + 0.5 * 0.05), underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.065, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * (0.5 * 0.1 + 0.5 * 0.05), underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.065, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 10)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 50, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 11)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commission, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.01, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commission, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.01, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable, EPSILON
    }

    void testStrategyHasStepFunctionsOnly() {

        ICommissionStrategy commissionStrategy = getInterpolatedSlidingCommissionStrategy(
                [0.1d, 0.1d, 0.1d, 0.2d, 0.2d], [0.6d, 0.5d, 0.4d, 0.4d, 0.3d])

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200,
                commission: 50, commissionFixed: 20, commissionVariable: 30)
        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100,
                commission: 5, commissionFixed: 1, commissionVariable: 4)

        ClaimCashflowPacket claim15 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null)

        List<CededUnderwritingInfoPacket> underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List<ClaimCashflowPacket> claims = [claim15]

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, true, 0)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.6 + 50, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3 + 20, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3 + 30, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.6 + 5, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3 + 1, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3 + 4, underwritingInfos[1].commissionVariable, EPSILON

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 1)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.6, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.6, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 2)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.4, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.1, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.4, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.1, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 25, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 3)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.4, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 200 * 0.1, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.4, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.1, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 5, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 4)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 30, null))

        commissionStrategy.getCalculator(new DoubleValuePerPeriod()).calculateCommission(claims, underwritingInfos, false, 5)
        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.3, underwritingInfos[0].commissionFixed, EPSILON
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable, EPSILON
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commission
        assertEquals 'underwritingInfo100', 100 * 0.3, underwritingInfos[1].commissionFixed, EPSILON
        assertEquals 'underwritingInfo100', 0d, underwritingInfos[1].commissionVariable, EPSILON
    }
}
