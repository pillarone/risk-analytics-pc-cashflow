package org.pillarone.riskanalytics.domain.pc.cf.segment;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class FinancialsPacket extends MultiValuePacket {

    private double netCashflow;
    private double netPremiumPaid;
    private double netPremiumWritten;
    private double netClaimPaid;
    private double netClaimUltimate;
    private double commission;
    private double lossRatio;
    private DateTime inceptionDate;

    public FinancialsPacket() {
    }

    public FinancialsPacket(List<UnderwritingInfoPacket> netUwInfos, List<CededUnderwritingInfoPacket> cededUwInfos,
                            List<ClaimCashflowPacket> netClaims, boolean singlePeriod) {
        if (!netUwInfos.isEmpty()) {
            netPremiumWritten = UnderwritingInfoUtils.aggregate(netUwInfos).getPremiumWritten();
            netPremiumPaid = UnderwritingInfoUtils.aggregate(netUwInfos).getPremiumPaid();
        }
        if (!netClaims.isEmpty()) {
            netClaimUltimate = ClaimUtils.sum(netClaims, true).getNominalUltimate();
            netClaimPaid = ClaimUtils.sum(netClaims, true).getPaidIncrementalIndexed();
        }
        if (!cededUwInfos.isEmpty()) {
            CededUnderwritingInfoPacket aggregateCededUwInfoPacket = UnderwritingInfoUtils.aggregateCeded(cededUwInfos);
            commission = aggregateCededUwInfoPacket.getCommission();
        }
        if (singlePeriod) {
            if (!netClaims.isEmpty()) {
                inceptionDate = netClaims.get(0).getBaseClaim().getExposureStartDate();
            }
            else if (!cededUwInfos.isEmpty()) {
                inceptionDate = cededUwInfos.get(0).getExposure().getInceptionDate();
            }
            else if (!netUwInfos.isEmpty()) {
                inceptionDate = netUwInfos.get(0).getExposure().getInceptionDate();
            }
        }
        netCashflow = netPremiumPaid + netClaimPaid + commission;
        lossRatio = netPremiumWritten == 0 ? 0 : -netClaimUltimate / netPremiumWritten;
    }

    public static List<FinancialsPacket> getFinancialsPacketsByInceptionPeriod(
            List<UnderwritingInfoPacket> netUwInformation, List<CededUnderwritingInfoPacket> cededUwInformation,
            List<ClaimCashflowPacket> netClaims, IPeriodCounter periodCounter) {
        ListMultimap<Integer, ClaimCashflowPacket> netClaimsByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, CededUnderwritingInfoPacket> cededUwInfoByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, UnderwritingInfoPacket> netUwInfoByPeriod = ArrayListMultimap.create();
        Set<Integer> periods = new HashSet<Integer>();
        for (ClaimCashflowPacket claim : netClaims) {
            // todo(sku): improve
            if (claim.getClaimType().equals(ClaimType.AGGREGATED_RESERVES)) continue;
            int period = claim.getBaseClaim().getInceptionPeriod(periodCounter);
            periods.add(period);
            netClaimsByPeriod.put(period, claim);
        }
        for (CededUnderwritingInfoPacket uwInfo : cededUwInformation) {
            periods.add(uwInfo.getExposure().getInceptionPeriod());
            cededUwInfoByPeriod.put(uwInfo.getExposure().getInceptionPeriod(), uwInfo);
        }
        for (UnderwritingInfoPacket uwInfo : netUwInformation) {
            periods.add(uwInfo.getExposure().getInceptionPeriod());
            netUwInfoByPeriod.put(uwInfo.getExposure().getInceptionPeriod(), uwInfo);
        }
        List<FinancialsPacket> packets = new ArrayList<FinancialsPacket>();
        for (Integer period : periods) {
            FinancialsPacket packet = new FinancialsPacket(netUwInfoByPeriod.get(period),
                    cededUwInfoByPeriod.get(period), netClaimsByPeriod.get(period), true);
            packets.add(packet);
        }
        return packets;
    }

    public FinancialsPacket plus(FinancialsPacket other) {
        if (other == null) return this;
        commission += other.commission;
        netClaimPaid += other.netClaimPaid;
        netClaimUltimate += other.netClaimUltimate;
        netPremiumPaid += other.netPremiumPaid;
        netPremiumWritten += other.netPremiumWritten;
        netCashflow = netPremiumPaid + netClaimPaid + commission;
        lossRatio = netPremiumWritten == 0 ? 0 : -netClaimUltimate / netPremiumWritten;
        return this;
    }

    public final static String COMMISSION = "commission";
    public final static String NET_CLAIM_PAID = "netClaimPaid";
    public final static String NET_CLAIM_ULTIMATE = "netClaimUltimate";
    public final static String NET_PREMIUM_PAID = "netPremiumPaid";
    public final static String NET_PREMIUM_WRITTEN = "netPremiumWritten";
    public final static String NET_CASHFLOW = "netCashflow";
    public final static String LOSS_RATIO = "lossRatio";

    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>();
        valuesToSave.put(COMMISSION, commission);    // this and missing default c'tor (final!) leads to failure during result tree building
        valuesToSave.put(NET_CLAIM_PAID, netClaimPaid);
        valuesToSave.put(NET_CLAIM_ULTIMATE, netClaimUltimate);
        valuesToSave.put(NET_PREMIUM_PAID, netPremiumPaid);
        valuesToSave.put(NET_PREMIUM_WRITTEN, netPremiumWritten);
        valuesToSave.put(NET_CASHFLOW, netCashflow);
        valuesToSave.put(LOSS_RATIO, lossRatio);
        return valuesToSave;
    }

    public double getNetCashflow() {
        return netCashflow;
    }

    public void setNetCashflow(double netCashflow) {
        this.netCashflow = netCashflow;
    }

    public double getNetPremiumPaid() {
        return netPremiumPaid;
    }

    public void setNetPremiumPaid(double netPremiumPaid) {
        this.netPremiumPaid = netPremiumPaid;
    }

    public double getNetClaimPaid() {
        return netClaimPaid;
    }

    public void setNetClaimPaid(double netClaimPaid) {
        this.netClaimPaid = netClaimPaid;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getLossRatio() {
        return lossRatio;
    }

    public void setLossRatio(double lossRatio) {
        this.lossRatio = lossRatio;
    }

    public DateTime getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(DateTime inceptionDate) {
        this.inceptionDate = inceptionDate;
    }
}
