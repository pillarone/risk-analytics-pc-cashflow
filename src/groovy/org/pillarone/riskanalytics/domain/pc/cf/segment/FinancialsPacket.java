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

    private DateTime inceptionDate;
    private double commission;

    // todo(sku): use helper objects for gross, ceded, net
    private double grossCashflow;
    private double grossPremiumPaid;
    private double grossClaimPaid;
    private double grossLossRatioPaidPaid;

    private double grossPremiumRisk;
    private double grossReserveRisk;
    private double grossPremiumReserveRisk;

    private double grossBestEstimate;
    private double grossPremiumWritten;
    private double grossClaimUltimate;
    private double grossLossRatioWrittenUltimate;


    private double netCashflow;
    private double netPremiumPaid;
    private double netClaimPaid;
    private double netLossRatioPaidPaid;

    private double netPremiumRisk;
    private double netReserveRisk;
    private double netPremiumReserveRisk;

    private double netBestEstimate;
    private double netPremiumWritten;
    private double netClaimUltimate;
    private double netLossRatioWrittenUltimate;


    private double cededCashflow;
    private double cededPremiumPaid;
    private double cededClaimPaid;
    private double cededLossRatioPaidPaid;

    private double cededPremiumRisk;
    private double cededReserveRisk;
    private double cededPremiumReserveRisk;

    private double cededBestEstimate;
    private double cededPremiumWritten;
    private double cededClaimUltimate;
    private double cededLossRatioWrittenUltimate;

    public FinancialsPacket() {
    }

    public FinancialsPacket(List<UnderwritingInfoPacket> grossUwInfos, List<UnderwritingInfoPacket> netUwInfos,
                            List<CededUnderwritingInfoPacket> cededUwInfos, List<ClaimCashflowPacket> grossClaims,
                            List<ClaimCashflowPacket> netClaims, List<ClaimCashflowPacket> cededClaims,
                            boolean singlePeriod, Boolean occurrenceInCurrentPeriod) {
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
        if (!grossUwInfos.isEmpty()) {
            grossPremiumWritten = UnderwritingInfoUtils.aggregate(grossUwInfos).getPremiumWritten();
            grossPremiumPaid = UnderwritingInfoUtils.aggregate(grossUwInfos).getPremiumPaid();
        }
        if (!grossClaims.isEmpty()) {
            grossClaimUltimate = ClaimUtils.sum(grossClaims, true).ultimate();
            grossClaimPaid = ClaimUtils.sum(grossClaims, true).getPaidIncrementalIndexed();
        }
        if (!netUwInfos.isEmpty()) {
            netPremiumWritten = UnderwritingInfoUtils.aggregate(netUwInfos).getPremiumWritten();
            netPremiumPaid = UnderwritingInfoUtils.aggregate(netUwInfos).getPremiumPaid();
        }
        if (!netClaims.isEmpty()) {
            netClaimUltimate = ClaimUtils.sum(netClaims, true).ultimate();
            netClaimPaid = ClaimUtils.sum(netClaims, true).getPaidIncrementalIndexed();
        }
        if (!cededUwInfos.isEmpty()) {
            CededUnderwritingInfoPacket aggregateCededUwInfoPacket = UnderwritingInfoUtils.aggregateCeded(cededUwInfos);
            cededPremiumWritten = aggregateCededUwInfoPacket.getPremiumWritten();
            cededPremiumPaid = aggregateCededUwInfoPacket.getPremiumPaid();
            commission = aggregateCededUwInfoPacket.getCommission();
        }
        if (!cededClaims.isEmpty()) {
            cededClaimUltimate = ClaimUtils.sum(cededClaims, true).ultimate();
            cededClaimPaid = ClaimUtils.sum(cededClaims, true).getPaidIncrementalIndexed();
        }

        grossCashflow = grossPremiumPaid + grossClaimPaid;
        cededCashflow = cededPremiumPaid + cededClaimPaid + commission;
        netCashflow = netPremiumPaid + netClaimPaid + commission;

        grossBestEstimate = grossPremiumWritten + grossClaimUltimate;
        cededBestEstimate = cededPremiumWritten + cededClaimUltimate;
        netBestEstimate = netPremiumWritten + netClaimUltimate;

        if (occurrenceInCurrentPeriod == Boolean.TRUE) {
            grossPremiumRisk = grossCashflow;
            netPremiumRisk = netCashflow;
            cededPremiumRisk = cededCashflow;
            
        }
        else if (occurrenceInCurrentPeriod == Boolean.FALSE) {
            grossReserveRisk = grossCashflow;
            netReserveRisk = netCashflow;
            cededReserveRisk = cededCashflow;
        }
        else {
//            Log.debug('occurrenceInCurrentPeriod null!');
        }

        grossPremiumReserveRisk = grossPremiumRisk + grossReserveRisk;
        grossLossRatioWrittenUltimate = grossPremiumWritten == 0 ? 0 : -grossClaimUltimate / grossPremiumWritten;
        grossLossRatioPaidPaid = grossPremiumPaid == 0 ? 0 : -grossClaimPaid / grossPremiumPaid;
        
        netPremiumReserveRisk = netPremiumRisk + netReserveRisk;
        netLossRatioWrittenUltimate = netPremiumWritten == 0 ? 0 : -netClaimUltimate / netPremiumWritten;
        netLossRatioPaidPaid = netPremiumPaid == 0 ? 0 : -netClaimPaid / netPremiumPaid;

        cededPremiumReserveRisk = cededPremiumRisk + cededReserveRisk;
        cededLossRatioWrittenUltimate = cededPremiumWritten == 0 ? 0 : -cededClaimUltimate / cededPremiumWritten;
        cededLossRatioPaidPaid = cededPremiumPaid == 0 ? 0 : -cededClaimPaid / cededPremiumPaid;
    }

    public static List<FinancialsPacket> getFinancialsPacketsByInceptionPeriod(
            List<UnderwritingInfoPacket> grossUwInformation, List<UnderwritingInfoPacket> netUwInformation,
            List<CededUnderwritingInfoPacket> cededUwInformation, List<ClaimCashflowPacket> grossClaims,
            List<ClaimCashflowPacket> netClaims, List<ClaimCashflowPacket> cededClaims, IPeriodCounter periodCounter) {
        ListMultimap<Integer, ClaimCashflowPacket> grossClaimsByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, ClaimCashflowPacket> netClaimsByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, ClaimCashflowPacket> cededClaimsByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, UnderwritingInfoPacket> grossUwInfoByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, CededUnderwritingInfoPacket> cededUwInfoByPeriod = ArrayListMultimap.create();
        ListMultimap<Integer, UnderwritingInfoPacket> netUwInfoByPeriod = ArrayListMultimap.create();
        Set<Integer> periods = new HashSet<Integer>();
        for (ClaimCashflowPacket claim : grossClaims) {
            // todo(sku): improve
            if (claim.getClaimType().equals(ClaimType.AGGREGATED_RESERVES)) continue;
            int period = claim.getBaseClaim().getInceptionPeriod(periodCounter);
            periods.add(period);
            grossClaimsByPeriod.put(period, claim);
        }
        for (ClaimCashflowPacket claim : netClaims) {
            // todo(sku): improve
            if (claim.getClaimType().equals(ClaimType.AGGREGATED_RESERVES)) continue;
            int period = claim.getBaseClaim().getInceptionPeriod(periodCounter);
            periods.add(period);
            netClaimsByPeriod.put(period, claim);
        }
        for (ClaimCashflowPacket claim : cededClaims) {
            // todo(sku): improve
            if (claim.getClaimType().equals(ClaimType.AGGREGATED_RESERVES)) continue;
            int period = claim.getBaseClaim().getInceptionPeriod(periodCounter);
            periods.add(period);
            cededClaimsByPeriod.put(period, claim);
        }
        for (UnderwritingInfoPacket uwInfo : grossUwInformation) {
            periods.add(uwInfo.getExposure().getInceptionPeriod());
            grossUwInfoByPeriod.put(uwInfo.getExposure().getInceptionPeriod(), uwInfo);
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
            FinancialsPacket packet = new FinancialsPacket(grossUwInfoByPeriod.get(period), netUwInfoByPeriod.get(period),
                    cededUwInfoByPeriod.get(period), grossClaimsByPeriod.get(period), netClaimsByPeriod.get(period),
                    cededClaimsByPeriod.get(period), true, period == periodCounter.currentPeriodIndex());
            packets.add(packet);
        }
        return packets;
    }

    public FinancialsPacket plus(FinancialsPacket other) {
        if (other == null) return this;
        commission += other.commission;

        grossClaimPaid += other.grossClaimPaid;
        grossClaimUltimate += other.grossClaimUltimate;
        grossPremiumPaid += other.grossPremiumPaid;
        grossPremiumWritten += other.grossPremiumWritten;
        grossCashflow = grossPremiumPaid + grossClaimPaid + commission;
        grossPremiumRisk += other.grossPremiumRisk;
        grossReserveRisk += other.grossReserveRisk;
        grossPremiumReserveRisk += other.grossPremiumReserveRisk;
        grossLossRatioWrittenUltimate = grossPremiumWritten == 0 ? 0 : -grossClaimUltimate / grossPremiumWritten;
        grossLossRatioPaidPaid = grossPremiumPaid == 0 ? 0 : -grossClaimPaid / grossPremiumPaid;
        grossBestEstimate = grossPremiumWritten + grossClaimUltimate;

        netClaimPaid += other.netClaimPaid;
        netClaimUltimate += other.netClaimUltimate;
        netPremiumPaid += other.netPremiumPaid;
        netPremiumWritten += other.netPremiumWritten;
        netCashflow = netPremiumPaid + netClaimPaid + commission;
        netPremiumRisk += other.netPremiumRisk;
        netReserveRisk += other.netReserveRisk;
        netPremiumReserveRisk += other.netPremiumReserveRisk;
        netLossRatioWrittenUltimate = netPremiumWritten == 0 ? 0 : -netClaimUltimate / netPremiumWritten;
        netLossRatioPaidPaid = netPremiumPaid == 0 ? 0 : -netClaimPaid / netPremiumPaid;
        netBestEstimate = netPremiumWritten + netClaimUltimate;

        cededClaimPaid += other.cededClaimPaid;
        cededClaimUltimate += other.cededClaimUltimate;
        cededPremiumPaid += other.cededPremiumPaid;
        cededPremiumWritten += other.cededPremiumWritten;
        cededCashflow = cededPremiumPaid + cededClaimPaid + commission;
        cededPremiumRisk += other.cededPremiumRisk;
        cededReserveRisk += other.cededReserveRisk;
        cededPremiumReserveRisk += other.cededPremiumReserveRisk;
        cededLossRatioWrittenUltimate = cededPremiumWritten == 0 ? 0 : -cededClaimUltimate / cededPremiumWritten;
        cededLossRatioPaidPaid = cededPremiumPaid == 0 ? 0 : -cededClaimPaid / cededPremiumPaid;
        cededBestEstimate = cededPremiumWritten + cededClaimUltimate;

        return this;
    }

    public final static String COMMISSION = "commission";

    public final static String GROSS_CLAIM_PAID = "grossClaimPaid";
    public final static String GROSS_CLAIM_ULTIMATE = "grossClaimUltimate";
    public final static String GROSS_PREMIUM_PAID = "grossPremiumPaid";
    public final static String GROSS_PREMIUM_WRITTEN = "grossPremiumWritten";
    public final static String GROSS_CASHFLOW = "grossCashflow";
    public final static String GROSS_BEST_ESTIMATE = "grossBestEstimate";
    public final static String GROSS_PREMIUM_RISK = "grossPremiumRisk";
    public final static String GROSS_RESERVE_RISK = "grossReserveRisk";
    public final static String GROSS_PREMIUM_RESERVE_RISK = "grossPremiumReserveRisk";
    public final static String GROSS_LOSS_RATIO_WRITTEN_ULTIMATE = "grossLossRatioWrittenUltimate";
    public final static String GROSS_LOSS_RATIO_PAID_PAID = "grossLossRatioPaidPaid";

    public final static String NET_CLAIM_PAID = "netClaimPaid";
    public final static String NET_CLAIM_ULTIMATE = "netClaimUltimate";
    public final static String NET_PREMIUM_PAID = "netPremiumPaid";
    public final static String NET_PREMIUM_WRITTEN = "netPremiumWritten";
    public final static String NET_CASHFLOW = "netCashflow";
    public final static String NET_BEST_ESTIMATE = "netBestEstimate";
    public final static String NET_PREMIUM_RISK = "netPremiumRisk";
    public final static String NET_RESERVE_RISK = "netReserveRisk";
    public final static String NET_PREMIUM_RESERVE_RISK = "netPremiumReserveRisk";
    public final static String NET_LOSS_RATIO_WRITTEN_ULTIMATE = "netLossRatioWrittenUltimate";
    public final static String NET_LOSS_RATIO_PAID_PAID = "netLossRatioPaidPaid";

    public final static String CEDED_CLAIM_PAID = "cededClaimPaid";
    public final static String CEDED_CLAIM_ULTIMATE = "cededClaimUltimate";
    public final static String CEDED_PREMIUM_PAID = "cededPremiumPaid";
    public final static String CEDED_PREMIUM_WRITTEN = "cededPremiumWritten";
    public final static String CEDED_CASHFLOW = "cededCashflow";
    public final static String CEDED_BEST_ESTIMATE = "cededBestEstimate";
    public final static String CEDED_PREMIUM_RISK = "cededPremiumRisk";
    public final static String CEDED_RESERVE_RISK = "cededReserveRisk";
    public final static String CEDED_PREMIUM_RESERVE_RISK = "cededPremiumReserveRisk";
    public final static String CEDED_LOSS_RATIO_WRITTEN_ULTIMATE = "cededLossRatioWrittenUltimate";
    public final static String CEDED_LOSS_RATIO_PAID_PAID = "cededLossRatioPaidPaid";


    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>();
        valuesToSave.put(COMMISSION, commission);    // this and missing default c'tor (final!) leads to failure during result tree building

        valuesToSave.put(GROSS_CLAIM_PAID, grossClaimPaid);
        valuesToSave.put(GROSS_CLAIM_ULTIMATE, grossClaimUltimate);
        valuesToSave.put(GROSS_PREMIUM_PAID, grossPremiumPaid);
        valuesToSave.put(GROSS_PREMIUM_WRITTEN, grossPremiumWritten);
        valuesToSave.put(GROSS_CASHFLOW, grossCashflow);
        valuesToSave.put(GROSS_BEST_ESTIMATE, grossBestEstimate);
        valuesToSave.put(GROSS_PREMIUM_RISK, grossPremiumRisk);
        valuesToSave.put(GROSS_RESERVE_RISK, grossReserveRisk);
        valuesToSave.put(GROSS_PREMIUM_RESERVE_RISK, grossReserveRisk);
        valuesToSave.put(GROSS_LOSS_RATIO_WRITTEN_ULTIMATE, grossLossRatioWrittenUltimate);
        valuesToSave.put(GROSS_LOSS_RATIO_PAID_PAID, grossLossRatioPaidPaid);
        
        valuesToSave.put(NET_CLAIM_PAID, netClaimPaid);
        valuesToSave.put(NET_CLAIM_ULTIMATE, netClaimUltimate);
        valuesToSave.put(NET_PREMIUM_PAID, netPremiumPaid);
        valuesToSave.put(NET_PREMIUM_WRITTEN, netPremiumWritten);
        valuesToSave.put(NET_CASHFLOW, netCashflow);
        valuesToSave.put(NET_BEST_ESTIMATE, netBestEstimate);
        valuesToSave.put(NET_PREMIUM_RISK, netPremiumRisk);
        valuesToSave.put(NET_RESERVE_RISK, netReserveRisk);
        valuesToSave.put(NET_PREMIUM_RESERVE_RISK, netReserveRisk);
        valuesToSave.put(NET_LOSS_RATIO_WRITTEN_ULTIMATE, netLossRatioWrittenUltimate);
        valuesToSave.put(NET_LOSS_RATIO_PAID_PAID, netLossRatioPaidPaid);

        valuesToSave.put(CEDED_CLAIM_PAID, cededClaimPaid);
        valuesToSave.put(CEDED_CLAIM_ULTIMATE, cededClaimUltimate);
        valuesToSave.put(CEDED_PREMIUM_PAID, cededPremiumPaid);
        valuesToSave.put(CEDED_PREMIUM_WRITTEN, cededPremiumWritten);
        valuesToSave.put(CEDED_CASHFLOW, cededCashflow);
        valuesToSave.put(CEDED_BEST_ESTIMATE, cededBestEstimate);
        valuesToSave.put(CEDED_PREMIUM_RISK, cededPremiumRisk);
        valuesToSave.put(CEDED_RESERVE_RISK, cededReserveRisk);
        valuesToSave.put(CEDED_PREMIUM_RESERVE_RISK, cededReserveRisk);
        valuesToSave.put(CEDED_LOSS_RATIO_WRITTEN_ULTIMATE, cededLossRatioWrittenUltimate);
        valuesToSave.put(CEDED_LOSS_RATIO_PAID_PAID, cededLossRatioPaidPaid);
        
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

    public double getNetLossRatioWrittenUltimate() {
        return netLossRatioWrittenUltimate;
    }

    public void setNetLossRatioWrittenUltimate(double netLossRatioWrittenUltimate) {
        this.netLossRatioWrittenUltimate = netLossRatioWrittenUltimate;
    }

    public double netPremiumRisk() {
        return netPremiumRisk;
    }

    public double netReserveRisk() {
        return netReserveRisk;
    }

    public DateTime getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(DateTime inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public double getNetPremiumWritten() {
        return netPremiumWritten;
    }

    public void setNetPremiumWritten(double netPremiumWritten) {
        this.netPremiumWritten = netPremiumWritten;
    }

    public double getNetClaimUltimate() {
        return netClaimUltimate;
    }

    public void setNetClaimUltimate(double netClaimUltimate) {
        this.netClaimUltimate = netClaimUltimate;
    }

    public double getNetLossRatioPaidPaid() {
        return netLossRatioPaidPaid;
    }

    public void setNetLossRatioPaidPaid(double netLossRatioPaidPaid) {
        this.netLossRatioPaidPaid = netLossRatioPaidPaid;
    }

    public double netPremiumReserveRisk() {
        return netPremiumReserveRisk;
    }

    public double getGrossCashflow() {
        return grossCashflow;
    }

    public void setGrossCashflow(double grossCashflow) {
        this.grossCashflow = grossCashflow;
    }

    public double getGrossPremiumPaid() {
        return grossPremiumPaid;
    }

    public void setGrossPremiumPaid(double grossPremiumPaid) {
        this.grossPremiumPaid = grossPremiumPaid;
    }

    public double getGrossClaimPaid() {
        return grossClaimPaid;
    }

    public void setGrossClaimPaid(double grossClaimPaid) {
        this.grossClaimPaid = grossClaimPaid;
    }

    public double getGrossLossRatioPaidPaid() {
        return grossLossRatioPaidPaid;
    }

    public void setGrossLossRatioPaidPaid(double grossLossRatioPaidPaid) {
        this.grossLossRatioPaidPaid = grossLossRatioPaidPaid;
    }

    public double grossPremiumRisk() {
        return grossPremiumRisk;
    }

    public double grossReserveRisk() {
        return grossReserveRisk;
    }

    public double grossPremiumReserveRisk() {
        return grossPremiumReserveRisk;
    }

    public double getGrossBestEstimate() {
        return grossBestEstimate;
    }

    public void setGrossBestEstimate(double grossBestEstimate) {
        this.grossBestEstimate = grossBestEstimate;
    }

    public double getGrossPremiumWritten() {
        return grossPremiumWritten;
    }

    public void setGrossPremiumWritten(double grossPremiumWritten) {
        this.grossPremiumWritten = grossPremiumWritten;
    }

    public double getGrossClaimUltimate() {
        return grossClaimUltimate;
    }

    public void setGrossClaimUltimate(double grossClaimUltimate) {
        this.grossClaimUltimate = grossClaimUltimate;
    }

    public double getGrossLossRatioWrittenUltimate() {
        return grossLossRatioWrittenUltimate;
    }

    public void setGrossLossRatioWrittenUltimate(double grossLossRatioWrittenUltimate) {
        this.grossLossRatioWrittenUltimate = grossLossRatioWrittenUltimate;
    }

    public double getNetBestEstimate() {
        return netBestEstimate;
    }

    public void setNetBestEstimate(double netBestEstimate) {
        this.netBestEstimate = netBestEstimate;
    }

    public double getCededCashflow() {
        return cededCashflow;
    }

    public void setCededCashflow(double cededCashflow) {
        this.cededCashflow = cededCashflow;
    }

    public double getCededPremiumPaid() {
        return cededPremiumPaid;
    }

    public void setCededPremiumPaid(double cededPremiumPaid) {
        this.cededPremiumPaid = cededPremiumPaid;
    }

    public double getCededClaimPaid() {
        return cededClaimPaid;
    }

    public void setCededClaimPaid(double cededClaimPaid) {
        this.cededClaimPaid = cededClaimPaid;
    }

    public double getCededLossRatioPaidPaid() {
        return cededLossRatioPaidPaid;
    }

    public void setCededLossRatioPaidPaid(double cededLossRatioPaidPaid) {
        this.cededLossRatioPaidPaid = cededLossRatioPaidPaid;
    }

    public double cededPremiumRisk() {
        return cededPremiumRisk;
    }

    public double cededReserveRisk() {
        return cededReserveRisk;
    }

    public double cededPremiumReserveRisk() {
        return cededPremiumReserveRisk;
    }

    public double getCededBestEstimate() {
        return cededBestEstimate;
    }

    public void setCededBestEstimate(double cededBestEstimate) {
        this.cededBestEstimate = cededBestEstimate;
    }

    public double getCededPremiumWritten() {
        return cededPremiumWritten;
    }

    public void setCededPremiumWritten(double cededPremiumWritten) {
        this.cededPremiumWritten = cededPremiumWritten;
    }

    public double getCededClaimUltimate() {
        return cededClaimUltimate;
    }

    public void setCededClaimUltimate(double cededClaimUltimate) {
        this.cededClaimUltimate = cededClaimUltimate;
    }

    public double getCededLossRatioWrittenUltimate() {
        return cededLossRatioWrittenUltimate;
    }

    public void setCededLossRatioWrittenUltimate(double cededLossRatioWrittenUltimate) {
        this.cededLossRatioWrittenUltimate = cededLossRatioWrittenUltimate;
    }
}
