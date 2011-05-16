package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;

import java.util.List;
import java.util.TreeMap;


/**
 * Assigns a commission rate and calculates the commission on ceded premium based on the loss ratio
 * (total losses / total premium).
 * <p/>
 * The commission rate is a right-continuous step-function of the loss ratio, with a finite number of jumps.
 * Each step interval, or "commission band", is realized internally as a key-value pair in a Java Map object.
 * Each map entry's key is the interval's left endpoint, and the map's value is the commission rate that
 * applies for loss ratios in the interval. Because the interval's right endpoint is not stored in the map,
 * we use the following conventions for defining & evaluating the resulting step function:
 * <ol>
 * <li>A first band, from -Infinity, with commission 0, is always added.
 * <li>Intermediate bands must be given in order of increasing lower limit.
 * <li>Each band applies to loss ratios inclusive of the lower limit, but
 * exclusive of the upper limit (which is the next band's lower limit, if any).
 * Otherwise, the last commission is used for all sufficiently large
 * loss ratios (i.e. at or above the last band's lower limit).
 * <li>The last band given effectively has no upper limit.
 * The caller must therefore specify a last band with commission 0 under typical use cases.
 * </ol>
 *
 * @author shartmann (at) munichre (dot) com, ben.ginsberg (at) intuitive-collaboration.com
 */
public class SlidingCommission extends AbstractCommission {

    private TreeMap<Double, Double> commissionRatePerLossRatio;

    // todo(sku): replace argument with an object
    public SlidingCommission(TreeMap<Double, Double> commissionRatePerLossRatio, BasedOnClaimProperty useClaims) {
        this.commissionRatePerLossRatio = commissionRatePerLossRatio;
        super.useClaims = useClaims;
    }

    public void calculateCommission(List<ClaimCashflowPacket> claims, List<CededUnderwritingInfoPacket> underwritingInfos,
                                    boolean isFirstPeriod, boolean isAdditive) {
        double totalClaims = sumClaims(claims);
        double totalPremium = sumPremiumPaid(underwritingInfos);
        double totalLossRatio = totalClaims / -totalPremium;
        double commissionRate;
        double fixedCommissionRate = commissionRatePerLossRatio.lastEntry().getValue();
        if (totalLossRatio < commissionRatePerLossRatio.firstKey()) {
            commissionRate = commissionRatePerLossRatio.firstEntry().getValue();
        }
        else {
            commissionRate = commissionRatePerLossRatio.floorEntry(totalLossRatio).getValue();
        }

        double variableCommissionRate = commissionRate - fixedCommissionRate;
        adjustCommissionProperties(underwritingInfos, isAdditive, commissionRate, fixedCommissionRate, variableCommissionRate);
    }
}
