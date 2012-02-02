package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective;

import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ClaimStorage;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.DoubleValue;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.AbstractProportionalReinsuranceContract;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LossPortfolioTransferContract extends AbstractProportionalReinsuranceContract {

    private double cededShare = 0;
    private double reinsurancePremium = 0;

    private DoubleValue annualAggregateLimitUltimate = new DoubleValue();
    private DoubleValue annualAggregateLimitPaid = new DoubleValue();
    private DoubleValue annualAggregateLimitReported = new DoubleValue();

    public LossPortfolioTransferContract(double cededShare, double limit, double reinsurancePremium) {
        this.cededShare = cededShare;
        this.reinsurancePremium = reinsurancePremium;
        annualAggregateLimitUltimate.value = limit;
        annualAggregateLimitPaid.value = limit;
        annualAggregateLimitReported.value = limit;
    }

    public ClaimCashflowPacket calculateClaimCeded(ClaimCashflowPacket grossClaim, ClaimStorage storage, IPeriodCounter periodCounter) {
        double quotaShareUltimate = 0;
        IClaimRoot cededBaseClaim;
        if (storage.hasReferenceCeded()) {
            cededBaseClaim = storage.getCededClaimRoot();
        }
        else {
            quotaShareUltimate = adjustedQuote(grossClaim.ultimate(), annualAggregateLimitUltimate);
            cededBaseClaim = storage.lazyInitCededClaimRoot(quotaShareUltimate);
        }

        double quotaShareReported = adjustedQuote(grossClaim.getReportedIncrementalIndexed(), annualAggregateLimitReported);
        double quotaSharePaid = adjustedQuote(grossClaim.getPaidIncrementalIndexed(), annualAggregateLimitPaid);
        ClaimCashflowPacket cededClaim = ClaimUtils.getCededClaim(grossClaim, storage, quotaShareUltimate,
                quotaShareReported, quotaSharePaid, true);
        add(grossClaim, cededClaim);
        return cededClaim;
    }

    /**
     *
     * @param claimProperty
     * @param annualAggregateLimit
     * @return has a negative sign as claimProperty is negative
     */
    private double adjustedQuote(double claimProperty, DoubleValue annualAggregateLimit) {
        if (claimProperty == 0) return -1;
        Double cededClaimProperty = Math.min(claimProperty * -cededShare, annualAggregateLimit.value);
        annualAggregateLimit.minus(cededClaimProperty);
        return (cededClaimProperty / claimProperty);
    }

    public void calculatePremium(List<UnderwritingInfoPacket> netUnderwritingInfos, double coveredByReinsurers, boolean fillNet) {
        double totalGrossPremiumWritten = 0;
        double totalGrossPremiumPaid = 0;
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            totalGrossPremiumWritten += grossUnderwritingInfo.getPremiumWritten();
            totalGrossPremiumPaid += grossUnderwritingInfo.getPremiumPaid();
        }
        double cededSharePremiumWritten = reinsurancePremium / totalGrossPremiumWritten;
        double cededSharePremiumPaid = reinsurancePremium / totalGrossPremiumPaid;
        for (UnderwritingInfoPacket grossUnderwritingInfo : grossUwInfos) {
            CededUnderwritingInfoPacket cededUnderwritingInfo = CededUnderwritingInfoPacket.scale(grossUnderwritingInfo,
                    contractMarker, 1, cededSharePremiumWritten, cededSharePremiumPaid, 1);
            UnderwritingInfoUtils.applyMarkers(grossUnderwritingInfo, cededUnderwritingInfo);
            cededUwInfos.add(cededUnderwritingInfo);
            netUnderwritingInfos.add(grossUnderwritingInfo.getNet(cededUnderwritingInfo, true));
        }
    }

    @Override
    public void calculateCommission() {
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", AAL ultimate: ");
        buffer.append(annualAggregateLimitUltimate);
        buffer.append(", AAL reported: ");
        buffer.append(annualAggregateLimitReported);
        buffer.append(", AAL paid: ");
        buffer.append(annualAggregateLimitPaid);
        return buffer.toString();
    }
}
