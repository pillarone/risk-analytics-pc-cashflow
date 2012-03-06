package org.pillarone.riskanalytics.domain.pc.cf.segment;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountUtils;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountedValuesPacket;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.NetPresentValuesPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion;
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion;
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion;
import org.pillarone.riskanalytics.domain.utils.marker.*;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): problem with no packets after default -> zero packets required!
public class Segment extends MultiPhaseComponent implements ISegmentMarker {

    private IterationScope iterationScope;
    private PeriodStore periodStore;
    public static final String NET_PRESENT_VALUE_GROSS = "netPresentValueGross";
    public static final String NET_PRESENT_VALUE_CEDED = "netPresentValueCeded";
    public static final String NET_PRESENT_VALUE_NET = "netPresentValueNet";
    public static final String DISCOUNTED_INCREMENTAL_PAID_GROSS = "discountedIncrementalPaidGross";
    public static final String DISCOUNTED_RESERVED_GROSS = "discountedReservedGross";
    public static final String FILTERED_FACTORS = "filteredFactors";

    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    // todo: remove inReserves as soon as PMO-1733 is solved (and collect within inClaims)
    private PacketList<ClaimCashflowPacket> inReserves = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    // todo: remove inReserves as soon as PMO-1733 is solved (and collect within inClaimsCeded)
    private PacketList<ClaimCashflowPacket> inReservesCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    // todo: remove inReserves as soon as PMO-1733 is solved (and collect within inUnderwritingInfoCeded)
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded2
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault.class);

    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    /** contains one aggregate net claim only */
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<DiscountedValuesPacket> outDiscountedValues = new PacketList<DiscountedValuesPacket>(DiscountedValuesPacket.class);
    private PacketList<NetPresentValuesPacket> outNetPresentValues = new PacketList<NetPresentValuesPacket>(NetPresentValuesPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<FinancialsPacket> outNetFinancials = new PacketList<FinancialsPacket>(FinancialsPacket.class);

    private ConstrainedString parmCompany = new ConstrainedString(ILegalEntityMarker.class, "");
    private ConstrainedMultiDimensionalParameter parmClaimsPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(PERIL, PORTION), ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER));
    private ConstrainedMultiDimensionalParameter parmUnderwritingPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(UNDERWRITING, PORTION),
            ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER));
    private ConstrainedMultiDimensionalParameter parmReservesPortions = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"), Arrays.asList(RESERVE, PORTION), ConstraintsFactory.getConstraints(ReservePortion.IDENTIFIER));
    private ComboBoxTableMultiDimensionalParameter parmDiscounting = new ComboBoxTableMultiDimensionalParameter(
            Arrays.asList(""), Arrays.asList("Discount Index"), IDiscountMarker.class);

    private Map<IClaimRoot, IClaimRoot> incomingScaledBaseClaimMapping = new HashMap<IClaimRoot, IClaimRoot>();
    private Map<IClaimRoot, IClaimRoot> incomingScaledBaseReservesMapping = new HashMap<IClaimRoot, IClaimRoot>();

    private static final String PERIL = "Claims Generator";
    private static final String RESERVE = "Reserves Generator";
    private static final String UNDERWRITING = "Underwriting";
    private static final String PORTION = "Portion";

    private static final String PHASE_GROSS = "Phase Gross";
    private static final String PHASE_NET = "Phase Net";

    private DateTime dateOfDefault;

    @Override
    public void doCalculation(String phase) {
        initIteration(phase);
        if (defaultNotBeforeCurrentPeriodStart()) {
            if (phase.equals(PHASE_GROSS)) {
                getSegmentClaims(dateOfDefault);
                getSegmentReserves(dateOfDefault);
                getSegmentUnderwritingInfo(dateOfDefault);
                if (discountedValuesRequired()) {
                    IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
                    DiscountUtils.getDiscountedGrossValues(inFactors, parmDiscounting, outClaimsGross, periodStore, periodCounter);
                }
            }
            else if (phase.equals(PHASE_NET)) {
                filterCededClaims();
                calculateNetClaims();
                filterCededUnderwritingInfo();
                calculateNetUnderwritingInfo();
                if (periodStore.exists(FILTERED_FACTORS)) {
                    DiscountUtils.getDiscountedNetValuesAndFillOutChannels(outClaimsCeded, outClaimsNet, outDiscountedValues,
                            outNetPresentValues, periodStore, iterationScope);
                }
                fillContractFinancials();
            }
        }
    }

    private void fillContractFinancials() {
        if (isSenderWired(outNetFinancials)) {
            FinancialsPacket financials = new FinancialsPacket(outUnderwritingInfoNet, outUnderwritingInfoCeded, outClaimsNet);
            outNetFinancials.add(financials);
        }
    }

    private boolean defaultNotBeforeCurrentPeriodStart() {
        if (dateOfDefault == null) {
            dateOfDefault = getDateOfDefault();
        }
        return dateOfDefault == null || dateOfDefault.isAfter(iterationScope.getPeriodScope().getCurrentPeriodStartDate());
    }

    private DateTime getDateOfDefault() {
        for (LegalEntityDefault legalEntityDefault : inLegalEntityDefault) {
            if (legalEntityDefault.getLegalEntity().equals(parmCompany.getSelectedComponent())) {
                return legalEntityDefault.getDateOfDefault();
            }
        }
        return null;
    }

    private double recovery(DateTime updateDate, DateTime dateOfDefault) {
        if (dateOfDefault != null) {
            for (LegalEntityDefault legalEntityDefault : inLegalEntityDefault) {
                if (legalEntityDefault.getLegalEntity().equals(parmCompany.getSelectedComponent())) {
                    return legalEntityDefault.getDateOfDefault().isAfter(updateDate) ? 1d : legalEntityDefault.getFirstInstantRecovery();
                }
            }
        }
        return 1d;
    }

    private void initIteration(String phase) {
        if (phase.equals(PHASE_GROSS) && iterationScope.getPeriodScope().isFirstPeriod()) {
            incomingScaledBaseClaimMapping.clear();
            incomingScaledBaseReservesMapping.clear();
            dateOfDefault = null;
        }
    }

    private void calculateNetUnderwritingInfo() {
        if (isSenderWired(outUnderwritingInfoNet)) {
            outUnderwritingInfoNet.addAll(UnderwritingInfoUtils.calculateNetUnderwritingInfo(outUnderwritingInfoGross, inUnderwritingInfoCeded));
        }
    }

    private void calculateNetClaims() {
        if (isSenderWired(outClaimsNet) && !outClaimsGross.isEmpty()) {
            outClaimsNet.addAll(ClaimUtils.calculateNetClaims(outClaimsGross, outClaimsCeded));
        }
    }

    private void filterCededUnderwritingInfo() {
        for (CededUnderwritingInfoPacket underwritingInfo : inUnderwritingInfoCeded) {
            if (underwritingInfo.segment().equals(this)) {
                outUnderwritingInfoCeded.add(underwritingInfo);
            }
        }
        for (CededUnderwritingInfoPacket underwritingInfo : inUnderwritingInfoCeded2) {
            if (underwritingInfo.segment().equals(this)) {
                outUnderwritingInfoCeded.add(underwritingInfo);
            }
        }
    }

    private void filterCededClaims() {
        for (ClaimCashflowPacket claim : inClaimsCeded) {
            if (claim.segment().equals(this)) {
                outClaimsCeded.add(claim);
            }
        }
        for (ClaimCashflowPacket claim : inReservesCeded) {
            if (claim.segment().equals(this)) {
                outClaimsCeded.add(claim);
            }
        }
    }

    private void getSegmentUnderwritingInfo(DateTime dateOfDefault) {
        if (inUnderwritingInfo.size() > 0) {
            List<UnderwritingInfoPacket> lobUnderwritingInfos = new ArrayList<UnderwritingInfoPacket>();
            int portionColumn = parmUnderwritingPortions.getColumnIndex(PORTION);
            for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
                String originName = underwritingInfo.riskBand().getNormalizedName();
                int row = parmUnderwritingPortions.getColumnByName(UNDERWRITING).indexOf(originName);
                if (row > -1) {
                    UnderwritingInfoPacket lobUnderwritingInfo = (UnderwritingInfoPacket) underwritingInfo.copy();
                    lobUnderwritingInfo.setOriginal(lobUnderwritingInfo);
                    double segmentPortion = InputFormatConverter.getDouble(parmUnderwritingPortions.getValueAt(row + 1, portionColumn));
                    segmentPortion *= recovery(lobUnderwritingInfo.getDate(), dateOfDefault);
                    lobUnderwritingInfo.setPremiumWritten(lobUnderwritingInfo.getPremiumWritten() * segmentPortion);
                    lobUnderwritingInfo.setPremiumPaid(lobUnderwritingInfo.getPremiumPaid() * segmentPortion);
                    lobUnderwritingInfo.setSumInsured(lobUnderwritingInfo.getSumInsured() * segmentPortion);
                    lobUnderwritingInfo.setMaxSumInsured(lobUnderwritingInfo.getMaxSumInsured() * segmentPortion);
                    lobUnderwritingInfo.origin = this;
                    lobUnderwritingInfo.setSegment(this);
                    lobUnderwritingInfo.setLegalEntity((ILegalEntityMarker) parmCompany.getSelectedComponent());
                    lobUnderwritingInfos.add(lobUnderwritingInfo);
                }
            }
            outUnderwritingInfoGross.addAll(lobUnderwritingInfos);
        }
    }

    private void getSegmentClaims(DateTime dateOfDefault) {
        if (inClaims.size() > 0) {
            List<ClaimCashflowPacket> segmentClaims = new ArrayList<ClaimCashflowPacket>();
            int portionColumn = parmClaimsPortions.getColumnIndex(PORTION);
            for (ClaimCashflowPacket marketClaim : inClaims) {
                // todo(jwa): if-statement needed as soon as reserves contained within inClaims
                if (marketClaim.peril() != null) {
                    String originName = marketClaim.peril().getNormalizedName();
                    int row = parmClaimsPortions.getColumnByName(PERIL).indexOf(originName);
                    if (row > -1) {
                        ClaimCashflowPacket segmentClaim = (ClaimCashflowPacket) marketClaim.copy();
                        // PMO-750: claim mergers in reinsurance program won't work with reference to market claims
                        segmentClaim.origin = this;
                        segmentClaim.setMarker(this);
                        segmentClaim.setMarker((IComponentMarker) parmCompany.getSelectedComponent());
                        double scaleFactor = InputFormatConverter.getDouble(parmClaimsPortions.getValueAt(row + 1, portionColumn));
                        scaleFactor *= recovery(segmentClaim.getUpdateDate(), dateOfDefault);
                        IClaimRoot scaledSegmentBaseClaim = incomingScaledBaseClaimMapping.get(marketClaim.getBaseClaim());
                        if (scaledSegmentBaseClaim == null) {
                            ClaimCashflowPacket scaledSegementClaim = ClaimUtils.scale(segmentClaim, scaleFactor, true, false);
                            incomingScaledBaseClaimMapping.put(marketClaim.getBaseClaim(), scaledSegementClaim.getBaseClaim());
                            segmentClaims.add(scaledSegementClaim);
                        }
                        else {
                            ClaimCashflowPacket scaledSegementClaim = ClaimUtils.scale(segmentClaim, scaleFactor, scaledSegmentBaseClaim, false);
                            segmentClaims.add(scaledSegementClaim);
                        }
                    }
                }
            }
            outClaimsGross.addAll(segmentClaims);
        }
    }

    private void getSegmentReserves(DateTime dateOfDefault) {
        if (inReserves.size() > 0) {
            List<ClaimCashflowPacket> segmentReserves = new ArrayList<ClaimCashflowPacket>();
            int portionColumn = parmReservesPortions.getColumnIndex(PORTION);
            for (ClaimCashflowPacket marketClaim : inReserves) {
                // todo(jwa): if-statement needed as soon as reserves contained within inClaims
                if (marketClaim.reserve() != null) {
                    String originName = marketClaim.reserve().getNormalizedName();
                    int row = parmReservesPortions.getColumnByName(RESERVE).indexOf(originName);
                    if (row > -1) {
                        ClaimCashflowPacket segmentReserve = (ClaimCashflowPacket) marketClaim.copy();
                        // PMO-750: claim mergers in reinsurance program won't work with reference to market claims
                        segmentReserve.origin = this;
                        segmentReserve.setMarker(this);
                        segmentReserve.setMarker((IComponentMarker) parmCompany.getSelectedComponent());
                        double scaleFactor = InputFormatConverter.getDouble(parmReservesPortions.getValueAt(row + 1, portionColumn));
                        scaleFactor *= recovery(segmentReserve.getUpdateDate(), dateOfDefault);
                        IClaimRoot scaledSegmentBaseReserve = incomingScaledBaseReservesMapping.get(marketClaim.getBaseClaim());
                        if (scaledSegmentBaseReserve == null) {
                            ClaimCashflowPacket scaledSegementClaim = ClaimUtils.scale(segmentReserve, scaleFactor, true, false);
                            incomingScaledBaseReservesMapping.put(marketClaim.getBaseClaim(), scaledSegementClaim.getBaseClaim());
                            segmentReserves.add(scaledSegementClaim);
                        }
                        else {
                            ClaimCashflowPacket scaledSegementClaim = ClaimUtils.scale(segmentReserve, scaleFactor, scaledSegmentBaseReserve, false);
                            segmentReserves.add(scaledSegementClaim);
                        }
                    }
                }
            }
            outClaimsGross.addAll(segmentReserves);
        }
    }

    private boolean discountedValuesRequired() {
        return isSenderWired(outDiscountedValues) && inFactors.size() > 0;
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inClaims, PHASE_GROSS);
        setTransmitterPhaseInput(inReserves, PHASE_GROSS);
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_GROSS);
        setTransmitterPhaseInput(inFactors, PHASE_GROSS);
        setTransmitterPhaseInput(inLegalEntityDefault, PHASE_GROSS);
        setTransmitterPhaseOutput(outClaimsGross, PHASE_GROSS);
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_GROSS);

        setTransmitterPhaseInput(inClaimsCeded, PHASE_NET);
        setTransmitterPhaseInput(inReservesCeded, PHASE_NET);
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_NET);
        setTransmitterPhaseInput(inUnderwritingInfoCeded2, PHASE_NET);
        setTransmitterPhaseOutput(outClaimsNet, PHASE_NET);
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_NET);
        setTransmitterPhaseOutput(outDiscountedValues, PHASE_NET);
        setTransmitterPhaseOutput(outNetPresentValues, PHASE_NET);
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_NET);
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_NET);
        setTransmitterPhaseOutput(outNetFinancials, PHASE_NET);
    }

    @Override
    public String toString() {
        return getNormalizedName();
    }

    public PacketList<ClaimCashflowPacket> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<ClaimCashflowPacket> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<ClaimCashflowPacket> getInClaimsCeded() {
        return inClaimsCeded;
    }

    public void setInClaimsCeded(PacketList<ClaimCashflowPacket> inClaimsCeded) {
        this.inClaimsCeded = inClaimsCeded;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<CededUnderwritingInfoPacket> getInUnderwritingInfoCeded() {
        return inUnderwritingInfoCeded;
    }

    public void setInUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded) {
        this.inUnderwritingInfoCeded = inUnderwritingInfoCeded;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<ClaimCashflowPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<ClaimCashflowPacket> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<ClaimCashflowPacket> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }

    public PacketList<CededUnderwritingInfoPacket> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public ConstrainedMultiDimensionalParameter getParmClaimsPortions() {
        return parmClaimsPortions;
    }

    public void setParmClaimsPortions(ConstrainedMultiDimensionalParameter parmClaimsPortions) {
        this.parmClaimsPortions = parmClaimsPortions;
    }

    public ConstrainedMultiDimensionalParameter getParmUnderwritingPortions() {
        return parmUnderwritingPortions;
    }

    public void setParmUnderwritingPortions(ConstrainedMultiDimensionalParameter parmUnderwritingPortions) {
        this.parmUnderwritingPortions = parmUnderwritingPortions;
    }

    public ConstrainedString getParmCompany() {
        return parmCompany;
    }

    public void setParmCompany(ConstrainedString parmCompany) {
        this.parmCompany = parmCompany;
    }

    public ConstrainedMultiDimensionalParameter getParmReservesPortions() {
        return parmReservesPortions;
    }

    public void setParmReservesPortions(ConstrainedMultiDimensionalParameter parmReservesPortions) {
        this.parmReservesPortions = parmReservesPortions;
    }

    public PacketList<ClaimCashflowPacket> getInReserves() {
        return inReserves;
    }

    public void setInReserves(PacketList<ClaimCashflowPacket> inReserves) {
        this.inReserves = inReserves;
    }

    public ComboBoxTableMultiDimensionalParameter getParmDiscounting() {
        return parmDiscounting;
    }

    public void setParmDiscounting(ComboBoxTableMultiDimensionalParameter parmDiscounting) {
        this.parmDiscounting = parmDiscounting;
    }

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    public PacketList<DiscountedValuesPacket> getOutDiscountedValues() {
        return outDiscountedValues;
    }

    public void setOutDiscountedValues(PacketList<DiscountedValuesPacket> outDiscountedValues) {
        this.outDiscountedValues = outDiscountedValues;
    }

    public PacketList<NetPresentValuesPacket> getOutNetPresentValues() {
        return outNetPresentValues;
    }

    public void setOutNetPresentValues(PacketList<NetPresentValuesPacket> outNetPresentValues) {
        this.outNetPresentValues = outNetPresentValues;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }

    public PacketList<LegalEntityDefault> getInLegalEntityDefault() {
        return inLegalEntityDefault;
    }

    public void setInLegalEntityDefault(PacketList<LegalEntityDefault> inLegalEntityDefault) {
        this.inLegalEntityDefault = inLegalEntityDefault;
    }

    public PacketList<FinancialsPacket> getOutNetFinancials() {
        return outNetFinancials;
    }

    public void setOutNetFinancials(PacketList<FinancialsPacket> outNetFinancials) {
        this.outNetFinancials = outNetFinancials;
    }

    public PacketList<ClaimCashflowPacket> getInReservesCeded() {
        return inReservesCeded;
    }

    public void setInReservesCeded(PacketList<ClaimCashflowPacket> inReservesCeded) {
        this.inReservesCeded = inReservesCeded;
    }

    public PacketList<CededUnderwritingInfoPacket> getInUnderwritingInfoCeded2() {
        return inUnderwritingInfoCeded2;
    }

    public void setInUnderwritingInfoCeded2(PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded2) {
        this.inUnderwritingInfoCeded2 = inUnderwritingInfoCeded2;
    }
}
