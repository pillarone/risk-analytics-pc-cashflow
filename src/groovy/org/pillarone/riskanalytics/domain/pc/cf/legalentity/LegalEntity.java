package org.pillarone.riskanalytics.domain.pc.cf.legalentity;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.AbstractStore;
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.DefaultProbabilities;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountUtils;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountedValuesPacket;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.NetPresentValuesPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.segment.FinancialsPacket;
import org.pillarone.riskanalytics.domain.utils.constant.Rating;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;
import umontreal.iro.lecuyer.probdist.BinomialDist;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntity extends MultiPhaseComponent implements ILegalEntityMarker {

    private Rating parmRating = Rating.NO_DEFAULT;
    private ConstrainedString parmRecoveryPattern = new ConstrainedString(IRecoveryPatternMarker.class, "");

    private PacketList<DefaultProbabilities> inDefaultProbabilities = new PacketList<DefaultProbabilities>(DefaultProbabilities.class);
    private PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);
    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded2 = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsInward2 = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded2 = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoInward2 = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

    private PacketList<LegalEntityDefault> outLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault.class);

    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsPrimaryInsurer = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsReinsurer = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoPrimeryInsurer = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoReinsurer = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

    private PacketList<FinancialsPacket> outNetFinancials = new PacketList<FinancialsPacket>(FinancialsPacket.class);
    private PacketList<DiscountedValuesPacket> outDiscountedValues = new PacketList<DiscountedValuesPacket>(DiscountedValuesPacket.class);
    private PacketList<NetPresentValuesPacket> outNetPresentValues = new PacketList<NetPresentValuesPacket>(NetPresentValuesPacket.class);

    private static final String PHASE_DEFAULT = "Phase Default";
    private static final String PHASE_CALC = "Phase Calculation";

    private IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getBinomialGenerator();
    private IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();

    private PeriodStore periodStore;
    private IterationScope iterationScope;
    private static final String IS_DEFAULT = "IS_DEFAULT";

    @Override
    public void doCalculation(String phase) {
        if (phase.equals(PHASE_DEFAULT)) {
            DateTime dateOfDefault = defaultOfReinsurer(inDefaultProbabilities.get(0).getDefaultProbability(parmRating));
            PatternPacket recoveryPattern = PatternUtils.filterPattern(inPatterns, parmRecoveryPattern, IRecoveryPatternMarker.class);
            outLegalEntityDefault.add(new LegalEntityDefault(this, dateOfDefault, recoveryPattern));
        }
        if (phase.equals(PHASE_CALC)) {
            for (ClaimCashflowPacket grossClaim : inClaims) {
                if (grossClaim.legalEntity().equals(this)) {
                    outClaimsPrimaryInsurer.add(grossClaim);
                }
            }
            outClaimsGross.addAll(outClaimsPrimaryInsurer);
            for (UnderwritingInfoPacket grossUnderwritingInfo : inUnderwritingInfo) {
                if (grossUnderwritingInfo.legalEntity().equals(this)) {
                    outUnderwritingInfoPrimeryInsurer.add(grossUnderwritingInfo);
                }
            }
            outUnderwritingInfoGross.addAll(outUnderwritingInfoPrimeryInsurer);
            for (ClaimCashflowPacket cededClaim : inClaimsCeded) {
                if (cededClaim.legalEntity().equals(this)) {
                    outClaimsCeded.add(cededClaim);
                }
            }
            for (ClaimCashflowPacket cededClaim : inClaimsCeded2) {
                if (cededClaim.legalEntity().equals(this)) {
                    outClaimsCeded.add(cededClaim);
                }
            }
            for (ClaimCashflowPacket inwardClaim : inClaimsInward) {
                if (inwardClaim.legalEntity().equals(this)) {
                    outClaimsReinsurer.add(inwardClaim);
                }
            }
            for (ClaimCashflowPacket inwardClaim : inClaimsInward2) {
                if (inwardClaim.legalEntity().equals(this)) {
                    outClaimsReinsurer.add(inwardClaim);
                }
            }
            if (!outClaimsReinsurer.isEmpty()) {
                outClaimsGross.addAll(outClaimsReinsurer);
            }
            for (CededUnderwritingInfoPacket cededUnderwritingInfo : inUnderwritingInfoCeded) {
                if (cededUnderwritingInfo.legalEntity().equals(this)) {
                    outUnderwritingInfoCeded.add(cededUnderwritingInfo);
                }
            }
            for (CededUnderwritingInfoPacket cededUnderwritingInfo : inUnderwritingInfoCeded2) {
                if (cededUnderwritingInfo.legalEntity().equals(this)) {
                    outUnderwritingInfoCeded.add(cededUnderwritingInfo);
                }
            }
            for (UnderwritingInfoPacket inwardUnderwritingInfo : inUnderwritingInfoInward) {
                if (inwardUnderwritingInfo.legalEntity().equals(this)) {

                    outUnderwritingInfoReinsurer.add(inwardUnderwritingInfo);
                }
            }
            for (UnderwritingInfoPacket inwardUnderwritingInfo : inUnderwritingInfoInward2) {
                if (inwardUnderwritingInfo.legalEntity().equals(this)) {

                    outUnderwritingInfoReinsurer.add(inwardUnderwritingInfo);
                }
            }
            outUnderwritingInfoGross.addAll(outUnderwritingInfoReinsurer);

            ClaimCashflowPacket netClaim = ClaimUtils.calculateNetClaim(outClaimsGross, outClaimsCeded);
            if (netClaim != null) {
                outClaimsNet.add(netClaim);
            }
            outUnderwritingInfoNet.addAll(UnderwritingInfoUtils.calculateNetUnderwritingInfo(outUnderwritingInfoGross, outUnderwritingInfoCeded));
            fillContractFinancials();
            discountClaims();
        }
    }

    private void fillContractFinancials() {
        if (isSenderWired(outNetFinancials)) {
            FinancialsPacket financials = new FinancialsPacket(outUnderwritingInfoNet, outUnderwritingInfoCeded, outClaimsNet);
            outNetFinancials.add(financials);
        }
    }

    private void discountClaims() {
        if (isSenderWired(outDiscountedValues) || isSenderWired(outNetPresentValues)) {
            IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
            DiscountUtils.getDiscountedGrossValues(outClaimsGross, periodStore, periodCounter);
            DiscountUtils.getDiscountedNetValuesAndFillOutChannels(outClaimsCeded, outClaimsNet, outDiscountedValues,
                    outNetPresentValues, periodStore, iterationScope);
        }
    }

    private DateTime defaultOfReinsurer(double probability) {
        DateTime dateOfDefault = (DateTime) periodStore.get(IS_DEFAULT, AbstractStore.LAST_PERIOD);
        if (dateOfDefault == null) {
            ((BinomialDist) generator.getDistribution()).setParams(1, probability);
            boolean isDefault = ((Integer) generator.nextValue()) == 1;
            if (isDefault) {
                dateOfDefault = DateTimeUtilities.getDate(iterationScope.getPeriodScope(), dateGenerator.nextValue().doubleValue());
                periodStore.put(IS_DEFAULT, dateOfDefault);
            }
        }
        return dateOfDefault;
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inDefaultProbabilities, PHASE_DEFAULT);
        setTransmitterPhaseInput(inPatterns, PHASE_DEFAULT);
        setTransmitterPhaseOutput(outLegalEntityDefault, PHASE_DEFAULT);

        setTransmitterPhaseInput(inClaims, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsGross, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsPrimaryInsurer, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoPrimeryInsurer, PHASE_CALC);

        setTransmitterPhaseInput(inClaimsCeded, PHASE_CALC);
        setTransmitterPhaseInput(inClaimsCeded2, PHASE_CALC);
        setTransmitterPhaseInput(inClaimsInward, PHASE_CALC);
        setTransmitterPhaseInput(inClaimsInward2, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfoCeded2, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfoInward, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfoInward2, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsReinsurer, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsNet, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoReinsurer, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_CALC);
        setTransmitterPhaseOutput(outNetFinancials, PHASE_CALC);
        setTransmitterPhaseOutput(outDiscountedValues, PHASE_CALC);
        setTransmitterPhaseOutput(outNetPresentValues, PHASE_CALC);
    }

    public Rating getParmRating() {
        return parmRating;
    }

    public void setParmRating(Rating parmRating) {
        this.parmRating = parmRating;
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

    public PacketList<ClaimCashflowPacket> getInClaimsInward() {
        return inClaimsInward;
    }

    public void setInClaimsInward(PacketList<ClaimCashflowPacket> inClaimsInward) {
        this.inClaimsInward = inClaimsInward;
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

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfoInward() {
        return inUnderwritingInfoInward;
    }

    public void setInUnderwritingInfoInward(PacketList<UnderwritingInfoPacket> inUnderwritingInfoInward) {
        this.inUnderwritingInfoInward = inUnderwritingInfoInward;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<ClaimCashflowPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsPrimaryInsurer() {
        return outClaimsPrimaryInsurer;
    }

    public void setOutClaimsPrimaryInsurer(PacketList<ClaimCashflowPacket> outClaimsPrimaryInsurer) {
        this.outClaimsPrimaryInsurer = outClaimsPrimaryInsurer;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsReinsurer() {
        return outClaimsReinsurer;
    }

    public void setOutClaimsReinsurer(PacketList<ClaimCashflowPacket> outClaimsReinsurer) {
        this.outClaimsReinsurer = outClaimsReinsurer;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(PacketList<ClaimCashflowPacket> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(PacketList<ClaimCashflowPacket> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoPrimeryInsurer() {
        return outUnderwritingInfoPrimeryInsurer;
    }

    public void setOutUnderwritingInfoPrimeryInsurer(PacketList<UnderwritingInfoPacket> outUnderwritingInfoPrimeryInsurer) {
        this.outUnderwritingInfoPrimeryInsurer = outUnderwritingInfoPrimeryInsurer;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoReinsurer() {
        return outUnderwritingInfoReinsurer;
    }

    public void setOutUnderwritingInfoReinsurer(PacketList<UnderwritingInfoPacket> outUnderwritingInfoReinsurer) {
        this.outUnderwritingInfoReinsurer = outUnderwritingInfoReinsurer;
    }

    public PacketList<CededUnderwritingInfoPacket> getOutUnderwritingInfoCeded() {
        return outUnderwritingInfoCeded;
    }

    public void setOutUnderwritingInfoCeded(PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded) {
        this.outUnderwritingInfoCeded = outUnderwritingInfoCeded;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoNet() {
        return outUnderwritingInfoNet;
    }

    public void setOutUnderwritingInfoNet(PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet) {
        this.outUnderwritingInfoNet = outUnderwritingInfoNet;
    }

    public ConstrainedString getParmRecoveryPattern() {
        return parmRecoveryPattern;
    }

    public void setParmRecoveryPattern(ConstrainedString parmRecoveryPattern) {
        this.parmRecoveryPattern = parmRecoveryPattern;
    }

    public PacketList<LegalEntityDefault> getOutLegalEntityDefault() {
        return outLegalEntityDefault;
    }

    public void setOutLegalEntityDefault(PacketList<LegalEntityDefault> outLegalEntityDefault) {
        this.outLegalEntityDefault = outLegalEntityDefault;
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

    public PacketList<DefaultProbabilities> getInDefaultProbabilities() {
        return inDefaultProbabilities;
    }

    public void setInDefaultProbabilities(PacketList<DefaultProbabilities> inDefaultProbabilities) {
        this.inDefaultProbabilities = inDefaultProbabilities;
    }

    public PacketList<FinancialsPacket> getOutNetFinancials() {
        return outNetFinancials;
    }

    public void setOutNetFinancials(PacketList<FinancialsPacket> outNetFinancials) {
        this.outNetFinancials = outNetFinancials;
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

    public PacketList<PatternPacket> getInPatterns() {
        return inPatterns;
    }

    public void setInPatterns(PacketList<PatternPacket> inPatterns) {
        this.inPatterns = inPatterns;
    }

    public PacketList<ClaimCashflowPacket> getInClaimsCeded2() {
        return inClaimsCeded2;
    }

    public void setInClaimsCeded2(PacketList<ClaimCashflowPacket> inClaimsCeded2) {
        this.inClaimsCeded2 = inClaimsCeded2;
    }

    public PacketList<ClaimCashflowPacket> getInClaimsInward2() {
        return inClaimsInward2;
    }

    public void setInClaimsInward2(PacketList<ClaimCashflowPacket> inClaimsInward2) {
        this.inClaimsInward2 = inClaimsInward2;
    }

    public PacketList<CededUnderwritingInfoPacket> getInUnderwritingInfoCeded2() {
        return inUnderwritingInfoCeded2;
    }

    public void setInUnderwritingInfoCeded2(PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded2) {
        this.inUnderwritingInfoCeded2 = inUnderwritingInfoCeded2;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfoInward2() {
        return inUnderwritingInfoInward2;
    }

    public void setInUnderwritingInfoInward2(PacketList<UnderwritingInfoPacket> inUnderwritingInfoInward2) {
        this.inUnderwritingInfoInward2 = inUnderwritingInfoInward2;
    }
}
