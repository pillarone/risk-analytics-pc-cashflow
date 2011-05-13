package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.SlidingCommissionStrategy
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionBasedOnClaims
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacketTests

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SlidingCommissionStrategyTests extends GroovyTestCase {
    
    private static final double EPSILON = 1E-14

    private ICommissionStrategy commissionStrategy
    private List<CededUnderwritingInfoPacket> uwInfo
    private List<ClaimCashflowPacket> claims

    static ICommissionStrategy getSlidingCommissionStrategy(Map<Double, Double> bands = [:]) {
        // convert/split bands, a map with entries [LossRatioLowerLimit: CommissionRate], to two arrays:
        List<Double> LossRatios
        List<Double> Commissions
        if (bands.size() > 0) {
            LossRatios = bands.keySet().asList().sort()
            Commissions = LossRatios.collect {bands.get(it)}
        }
        else {
            // default commission bands if none specified
            LossRatios = [0.0d, 0.1d, 0.2d, 0.5d]
            Commissions = [0.2d, 0.10d, 0.05d, 0d]
        }
        ICommissionStrategy commissionStrategy = CommissionStrategyType.getStrategy(
                CommissionStrategyType.SLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [LossRatios, Commissions],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
                'useClaims': CommissionBasedOnClaims.PAID])
        return commissionStrategy
    }

    void setUp() {
        // default values for test cases below (e.g. for those that don't use the above method's default bands)
        commissionStrategy = getSlidingCommissionStrategy([
                0.1d: 0.07d, // 7% on 10-40%, i.e., Commission is 7% if LossRatio is in [0.1, 0.4)
                0.4d: 0.05d, // 5% on 40-50%, i.e., Commission is 5% if LossRatio is in [0.4, 0.5)
                0.5d: 0.03d, // 3% on 50-60%, i.e., Commission is 3% if LossRatio is in [0.5, 0.6)
                0.6d: 0d,    // 0% from 60%, i.e., Commission is 0% if LossRatio is in [0.6, +Infinity)
        ])
        uwInfo = [new CededUnderwritingInfoPacket(premiumPaid: -100)]
        claims = [ClaimCashflowPacketTests.getClaimCashflowPacket(0, 0, null)]
    }

    /** this is a simple example testing that the sliding commission strategy:
     * (1) calculates the correct loss ratio;
     * (2) selects the correct band;
     * (3) uses the corresponding commission from that band; and
     * (4) distributes this commission proportionally to the premium written of each UnderwritingInfo packet,
     * (5) without the "additive" option.
     */
    void testUsage() {
        ICommissionStrategy commissionStrategy = getSlidingCommissionStrategy() // uses method's default bands
        // default commission bands: 20% Commission on [0,10%) LossRatio, 10% on [10%,20%), 5% on [20%,50%), 0% at or over 50% loss ratio

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200, commission: 50)
        CededUnderwritingInfoPacket underwritingInfo100 = new CededUnderwritingInfoPacket(premiumPaid: -100, commission: 5)
        // so total premium written is 300

        ClaimCashflowPacket claim05 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 5, null)
        ClaimCashflowPacket claim15 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null)
        // so total loss is 20

        List underwritingInfos = [underwritingInfo200, underwritingInfo100]
        List claims = [claim05, claim15]
        // so loss ratio is 20/300 < 10%, and therefore the commission should be 20%

        commissionStrategy.calculator.calculateCommission claims, underwritingInfos, false, false

        assertEquals '# outUnderwritingInfo packets', 2, underwritingInfos.size()
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 200 * 0.2, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionFixed
        assertEquals 'underwritingInfo100', 100 * 0.2, underwritingInfos[1].commission
        assertEquals 'underwritingInfo200', 100 * 0.2, underwritingInfos[1].commissionVariable
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[1].commissionFixed

        commissionStrategy = getSlidingCommissionStrategy([0.1d: 0.07d, 0.4d: 0.05d, 0.5d: 0.03d, 0.6d: 0.02d,])
        CededUnderwritingInfoPacket underwritingInfo300 = new CededUnderwritingInfoPacket(premiumPaid: -300)
        ClaimCashflowPacket claim180 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 180, null)
        underwritingInfos = [underwritingInfo300]
        claims = [claim180]
        commissionStrategy.calculator.calculateCommission claims, underwritingInfos, false, false

        assertEquals "underwritingInfo600", 300 * 0.02, underwritingInfos[0].commission
        assertEquals 'underwritingInfo200', 0d, underwritingInfos[0].commissionVariable
        assertEquals 'underwritingInfo200', 300 * 0.02, underwritingInfos[0].commissionFixed

    }

    void testAdditiveUsage() {
        ICommissionStrategy commissionStrategy = getSlidingCommissionStrategy()
        // default commission bands: 20% Commission on [0,10%) LossRatio, 10% on [10%,20%), 5% on [20%,50%), 0% at or over 50% loss ratio

        CededUnderwritingInfoPacket underwritingInfo200 = new CededUnderwritingInfoPacket(premiumPaid: -200, commission: 50)
        List underwritingInfo = [underwritingInfo200]
        List claims = [ClaimCashflowPacketTests.getClaimCashflowPacket(0, 25, null)]
        // loss ratio is 25/200 = 12.5%, in [10%, 20%], so the commission should be 10%

        commissionStrategy.calculator.calculateCommission claims, underwritingInfo, false, true
        // Nota bene: this is the "additive" usage!

        assertEquals '# outUnderwritingInfo packets', 1, underwritingInfo.size()
        assertEquals 'underwritingInfo200', 50 + 200 * 0.1, underwritingInfo[0].commission
    }

    /**
     * Test cases testPercentageSelectionCaseX (for X in {0a,0b,0c,1,2,3a,3b,4})
     * correspond to the spreadsheet found in Jira here:
     *
     */
    void testPercentageSelectionCase0a() {
        claims[0].paidIncremental = 1e-6
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 7d, uwInfo[0].commission, EPSILON
    }

    void testPercentageSelectionCase0b() {
        claims[0].paidIncremental = 0d
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 7d, uwInfo[0].commission, EPSILON
    }

    void testPercentageSelectionCase0c() {
        claims[0].paidIncremental = 10d - 1e-6
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 7d, uwInfo[0].commission, EPSILON
    }

    void testPercentageSelectionCase1() {
        claims[0].paidIncremental = 10d
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 7d, uwInfo[0].commission, EPSILON
    }

    void testPercentageSelectionCase2() {
        claims[0].paidIncremental = 40d
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 5d, uwInfo[0].commission
    }

    void testPercentageSelectionCase3a() {
        claims[0].paidIncremental = 50d
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 3d, uwInfo[0].commission
    }

    void testPercentageSelectionCase3b() {
        claims[0].paidIncremental = 60d - 1e-6
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 3d, uwInfo[0].commission
    }

    void testPercentageSelectionCase4() {
        claims[0].paidIncremental = 60d
        commissionStrategy.calculator.calculateCommission claims, uwInfo, false, false
        double lossRatio = claims.paidIncremental.sum() / -uwInfo.premiumPaid.sum() * 1E2
        assertEquals '# outUnderwritingInfo packets', 1, uwInfo.size()
        assertEquals "Underwriting Commission (%) resulting from Loss Ratio of ${lossRatio}%", 0, uwInfo[0].commission, EPSILON
    }

    void testEqualLossRatios() {

        ICommissionStrategy commissionStrategy = CommissionStrategyType.getStrategy(
                CommissionStrategyType.SLIDINGCOMMISSION,
                ['commissionBands': new ConstrainedMultiDimensionalParameter(
                        [[0d, 0.0d, 0.1d, 0.1d, 0.1d, 0.2d, 0.2d], [0.7d, 0.6d, 0.6d, 0.5d, 0.4d, 0.35d, 0.3d]],
                        [SlidingCommissionStrategy.LOSS_RATIO, SlidingCommissionStrategy.COMMISSION],
                        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),
                 useClaims: CommissionBasedOnClaims.PAID ])

        CededUnderwritingInfoPacket UnderwritingInfoPacket200 = new CededUnderwritingInfoPacket(premiumPaid: -200, 
                commission: 50, commissionFixed: 20, commissionVariable: 30)
        CededUnderwritingInfoPacket UnderwritingInfoPacket100 = new CededUnderwritingInfoPacket(premiumPaid: -100, 
                commission: 5, commissionFixed: 4, commissionVariable: 1)

        ClaimCashflowPacket claim15 = ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null)

        List UnderwritingInfoPackets = [UnderwritingInfoPacket200, UnderwritingInfoPacket100]
        List claims = [claim15]

        commissionStrategy.calculator.calculateCommission claims, UnderwritingInfoPackets, false, true
        assertEquals '# outUnderwritingInfoPacket packets', 2, UnderwritingInfoPackets.size()
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.6 + 50, UnderwritingInfoPackets[0].commission
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3 + 30, UnderwritingInfoPackets[0].commissionVariable
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3 + 20, UnderwritingInfoPackets[0].commissionFixed
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.6 + 5, UnderwritingInfoPackets[1].commission
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3 + 1, UnderwritingInfoPackets[1].commissionVariable
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3 + 4, UnderwritingInfoPackets[1].commissionFixed

        commissionStrategy.calculator.calculateCommission claims, UnderwritingInfoPackets, false, false
        assertEquals '# outUnderwritingInfoPacket packets', 2, UnderwritingInfoPackets.size()
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.6, UnderwritingInfoPackets[0].commission
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commissionVariable
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commissionFixed
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.6, UnderwritingInfoPackets[1].commission
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commissionVariable
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commissionFixed

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 15, null))

        commissionStrategy.calculator.calculateCommission claims, UnderwritingInfoPackets, false, false
        assertEquals '# outUnderwritingInfoPacket packets', 2, UnderwritingInfoPackets.size()
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.4, UnderwritingInfoPackets[0].commission
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.1, UnderwritingInfoPackets[0].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commissionFixed, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.4, UnderwritingInfoPackets[1].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.1, UnderwritingInfoPackets[1].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commissionFixed, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 25, null))

        commissionStrategy.calculator.calculateCommission claims, UnderwritingInfoPackets, false, false
        assertEquals '# outUnderwritingInfoPacket packets', 2, UnderwritingInfoPackets.size()
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.4, UnderwritingInfoPackets[0].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.1, UnderwritingInfoPackets[0].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commissionFixed, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.4, UnderwritingInfoPackets[1].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.1, UnderwritingInfoPackets[1].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commissionFixed, EPSILON

        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 5, null))

        commissionStrategy.calculator.calculateCommission claims, UnderwritingInfoPackets, false, false
        assertEquals '# outUnderwritingInfoPacket packets', 2, UnderwritingInfoPackets.size()
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 0d, UnderwritingInfoPackets[0].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commissionFixed, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 0d, UnderwritingInfoPackets[1].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commissionFixed, EPSILON


        claims.add(ClaimCashflowPacketTests.getClaimCashflowPacket(0, 30, null))

        commissionStrategy.calculator.calculateCommission claims, UnderwritingInfoPackets, false, false
        assertEquals '# outUnderwritingInfoPacket packets', 2, UnderwritingInfoPackets.size()
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 0d, UnderwritingInfoPackets[0].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket200', 200 * 0.3, UnderwritingInfoPackets[0].commissionFixed, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commission, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 0d, UnderwritingInfoPackets[1].commissionVariable, EPSILON
        assertEquals 'UnderwritingInfoPacket100', 100 * 0.3, UnderwritingInfoPackets[1].commissionFixed, EPSILON
    }
}
