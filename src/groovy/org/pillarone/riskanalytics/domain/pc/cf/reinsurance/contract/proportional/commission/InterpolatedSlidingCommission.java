package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;

import java.util.List;
import java.util.TreeMap;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class InterpolatedSlidingCommission extends AbstractCommission {

    private TreeMap<Double, List<Double>> commissionRatesPerLossRatio;
    private boolean isStartCoverPeriod = true;

    // todo(sku): replace argument with an object
    public InterpolatedSlidingCommission(TreeMap<Double, List<Double>> commissionRatesPerLossRatio,
                                         BasedOnClaimProperty useClaims) {
        this.commissionRatesPerLossRatio = commissionRatesPerLossRatio;
        super.useClaims = useClaims;
    }

    // todo(sku): add proper handling of Div/0 and corresponding test cases
    public void calculateCommission(List<ClaimCashflowPacket> claims, List<CededUnderwritingInfoPacket> underwritingInfos,
                                    boolean isFirstPeriod, boolean isAdditive) {
//        if (!isStartCoverPeriod) return;
//        isStartCoverPeriod = false;
        double summedClaims = sumClaims(claims);
        double summedPremiumPaid = sumPremiumPaid(underwritingInfos);
        double totalLossRatio = summedPremiumPaid == 0 ? 0 : summedClaims / -summedPremiumPaid;
        double commissionRate;
        double lowestEnteredLossRatio = commissionRatesPerLossRatio.firstKey();
        double highestEnteredLossRatio = commissionRatesPerLossRatio.lastKey();
        double fixedCommissionRate = commissionRatesPerLossRatio.get(highestEnteredLossRatio).get(0);
        if (totalLossRatio < lowestEnteredLossRatio) {
            List<Double> associatedCommissionRates = commissionRatesPerLossRatio.get(lowestEnteredLossRatio);
            commissionRate = associatedCommissionRates.get(associatedCommissionRates.size() - 1);
        }
        else if (commissionRatesPerLossRatio.containsKey(Math.min(totalLossRatio, highestEnteredLossRatio))) {
            commissionRate = commissionRatesPerLossRatio.get(Math.min(totalLossRatio, highestEnteredLossRatio)).get(0);
        }
        else {
            double leftLossRatio = commissionRatesPerLossRatio.floorKey(totalLossRatio);
            double rightLossRatio = commissionRatesPerLossRatio.higherKey(totalLossRatio);
            double leftCommissionValue = commissionRatesPerLossRatio.get(leftLossRatio).get(0);
            int size = commissionRatesPerLossRatio.get(rightLossRatio).size();
            double rightCommissionValue = commissionRatesPerLossRatio.get(rightLossRatio).get(size - 1);
            commissionRate = (rightLossRatio - totalLossRatio) / (rightLossRatio - leftLossRatio) * leftCommissionValue +
                    (totalLossRatio - leftLossRatio) / (rightLossRatio - leftLossRatio) * rightCommissionValue;
        }

        double variableCommissionRate = commissionRate - fixedCommissionRate;
        adjustCommissionProperties(underwritingInfos, isAdditive, commissionRate, fixedCommissionRate, variableCommissionRate);
    }
}
