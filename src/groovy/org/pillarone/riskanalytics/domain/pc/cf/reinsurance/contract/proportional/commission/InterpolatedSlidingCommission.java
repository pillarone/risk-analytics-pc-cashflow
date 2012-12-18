package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;
import java.util.TreeMap;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class InterpolatedSlidingCommission extends AbstractCommission {

    private TreeMap<Double, List<Double>> commissionRatesPerLossRatio;

    private double summedCumulatedPremiumCeded = 0;

    // todo(sku): replace argument with an object
    public InterpolatedSlidingCommission(TreeMap<Double, List<Double>> commissionRatesPerLossRatio,
                                         BasedOnClaimProperty useClaims) {
        this.commissionRatesPerLossRatio = commissionRatesPerLossRatio;
        super.useClaims = useClaims;
    }

    public void calculateCommission(List<ClaimCashflowPacket> cededClaims,
                                    List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                    boolean isAdditive) {
        double summedIncrementalPremiumCeded = sumPremium(cededUnderwritingInfos);
        summedCumulatedPremiumCeded += summedIncrementalPremiumCeded;
        double totalLossRatio = summedCumulatedPremiumCeded == 0 ? 0 : sumCumulatedClaims(cededClaims) / -summedCumulatedPremiumCeded;
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
            if (rightLossRatio != leftLossRatio) {
                commissionRate = (rightLossRatio - totalLossRatio) / (rightLossRatio - leftLossRatio) * leftCommissionValue +
                    (totalLossRatio - leftLossRatio) / (rightLossRatio - leftLossRatio) * rightCommissionValue;
            }
            else {
                commissionRate = 0;
            }
        }

        double fixCommission = fixedCommissionRate * -summedIncrementalPremiumCeded;
        double variableCommission = (commissionRate - fixedCommissionRate) * -summedIncrementalPremiumCeded;
        double totalIncrementalCommission = commissionRate * -summedIncrementalPremiumCeded;
        adjustCommissionProperties(cededUnderwritingInfos, isAdditive, totalIncrementalCommission,
                fixCommission, variableCommission);
    }
}
