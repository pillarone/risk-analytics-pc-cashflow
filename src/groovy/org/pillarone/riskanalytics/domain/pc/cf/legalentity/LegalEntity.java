package org.pillarone.riskanalytics.domain.pc.cf.legalentity;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.AbstractStore;
import org.pillarone.riskanalytics.core.components.MultiPhaseComponent;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.DefaultProbabilities;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.constant.Rating;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;
import umontreal.iro.lecuyer.probdist.BinomialDist;

import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class LegalEntity extends MultiPhaseComponent implements ILegalEntityMarker {

    private Rating parmRating = Rating.NO_DEFAULT;
    private ConstrainedString parmRecoveryPattern = new ConstrainedString(IRecoveryPatternMarker.class, "");

    private PacketList<DefaultProbabilities> inDefaultProbabilities = new PacketList<DefaultProbabilities>(DefaultProbabilities.class);
    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> inClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> inUnderwritingInfoCeded = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

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

    private PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket.class);

    private static final String PHASE_DEFAULT = "Phase Default";
    private static final String PHASE_CALC = "Phase Calculation";

    private IRandomNumberGenerator generator = RandomNumberGeneratorFactory.getBinomialGenerator();
    private IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();

    private PeriodStore periodStore;
    private PeriodScope periodScope;
    private static final String IS_DEFAULT = "IS_DEFAULT";

    @Override
    public void doCalculation(String phase) {
        if (phase.equals(PHASE_DEFAULT)) {
            DateTime dateOfDefault = defaultOfReinsurer(inDefaultProbabilities.get(0).getDefaultProbability(parmRating));
            outLegalEntityDefault.add(new LegalEntityDefault(this, dateOfDefault));
        }
        if (phase.equals(PHASE_CALC)) {
            for (ClaimCashflowPacket grossClaim : inClaims) {
                if (grossClaim.legalEntity().equals(this)) {
                    outClaimsPrimaryInsurer.add(grossClaim);
                }
            }
            avoidVoidClaimList(outClaimsPrimaryInsurer);
            outClaimsGross.addAll(outClaimsPrimaryInsurer);
            for (UnderwritingInfoPacket grossUnderwritingInfo : inUnderwritingInfo) {
                if (grossUnderwritingInfo.legalEntity().equals(this)) {

                    outUnderwritingInfoPrimeryInsurer.add(grossUnderwritingInfo);
                }
            }
            avoidVoidUnderwritingInfoList(outUnderwritingInfoPrimeryInsurer);
            outUnderwritingInfoGross.addAll(outUnderwritingInfoPrimeryInsurer);
            for (ClaimCashflowPacket cededClaim : inClaimsCeded) {
                if (cededClaim.legalEntity().equals(this)) {
                    outClaimsCeded.add(cededClaim);
                }
            }
            avoidVoidClaimList(outClaimsCeded);
            for (ClaimCashflowPacket inwardClaim : inClaimsInward) {
                if (inwardClaim.legalEntity().equals(this)) {
                    outClaimsReinsurer.add(inwardClaim);
                }
            }
            if (!avoidVoidClaimList(outClaimsReinsurer)) {
                outClaimsGross.addAll(outClaimsReinsurer);
            }
            for (CededUnderwritingInfoPacket cededUnderwritingInfo : inUnderwritingInfoCeded) {
                if (cededUnderwritingInfo.legalEntity().equals(this)) {
                    outUnderwritingInfoCeded.add(cededUnderwritingInfo);
                }
            }
            avoidVoidCededUnderwritingInfoList(outUnderwritingInfoCeded);
            for (UnderwritingInfoPacket inwardUnderwritingInfo : inUnderwritingInfoInward) {
                if (inwardUnderwritingInfo.legalEntity().equals(this)) {

                    outUnderwritingInfoReinsurer.add(inwardUnderwritingInfo);
                }
            }
            avoidVoidUnderwritingInfoList(outUnderwritingInfoReinsurer);
            outUnderwritingInfoGross.addAll(outUnderwritingInfoReinsurer);

            outClaimsNet.add(ClaimUtils.calculateNetClaim(outClaimsGross, outClaimsCeded));
            outUnderwritingInfoNet.addAll(UnderwritingInfoUtils.calculateNetUnderwritingInfo(outUnderwritingInfoGross, outUnderwritingInfoCeded));
            fillContractFinancials();
        }
    }

    private void fillContractFinancials() {
        ContractFinancialsPacket contractFinancials = new ContractFinancialsPacket(outClaimsCeded, outClaimsNet,
                outUnderwritingInfoCeded, outUnderwritingInfoNet);
        outContractFinancials.add(contractFinancials);
    }


    private boolean avoidVoidClaimList(PacketList<ClaimCashflowPacket> packetList) {
        if (packetList.isEmpty()) {
            DateTime startOfPeriod = periodScope.getCurrentPeriodStartDate();
            IClaimRoot baseClaim = new ClaimRoot(0, ClaimType.AGGREGATED, startOfPeriod, startOfPeriod);
            packetList.add(new ClaimCashflowPacket(baseClaim, 0, 0, 0, 0, 0, 0, null, startOfPeriod, periodScope.getCurrentPeriod()));
            return true;
        }
        return false;
    }

    private boolean avoidVoidUnderwritingInfoList(PacketList<UnderwritingInfoPacket> packetList) {
        if (packetList.isEmpty()) {
            packetList.add(new UnderwritingInfoPacket());
            return true;
        }
        return false;
    }

    private boolean avoidVoidCededUnderwritingInfoList(PacketList<CededUnderwritingInfoPacket> packetList) {
        if (packetList.isEmpty()) {
            packetList.add(new CededUnderwritingInfoPacket());
            return true;
        }
        return false;
    }

    private DateTime defaultOfReinsurer(double probability) {
        DateTime dateOfDefault = (DateTime) periodStore.get(IS_DEFAULT, AbstractStore.LAST_PERIOD);
        if (dateOfDefault == null) {
            ((BinomialDist) generator.getDistribution()).setParams(1, probability);
            boolean isDefault = ((Integer) generator.nextValue()) == 1;
            if (isDefault) {
                dateOfDefault = DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue());
                periodStore.put(IS_DEFAULT, dateOfDefault);
            }
        }
        return dateOfDefault;
    }

    public void allocateChannelsToPhases() {
        setTransmitterPhaseInput(inDefaultProbabilities, PHASE_DEFAULT);
        setTransmitterPhaseOutput(outLegalEntityDefault, PHASE_DEFAULT);

        setTransmitterPhaseInput(inClaims, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsGross, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsPrimaryInsurer, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoGross, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoPrimeryInsurer, PHASE_CALC);

        setTransmitterPhaseInput(inClaimsCeded, PHASE_CALC);
        setTransmitterPhaseInput(inClaimsInward, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfoCeded, PHASE_CALC);
        setTransmitterPhaseInput(inUnderwritingInfoInward, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsReinsurer, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsCeded, PHASE_CALC);
        setTransmitterPhaseOutput(outClaimsNet, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoReinsurer, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoCeded, PHASE_CALC);
        setTransmitterPhaseOutput(outUnderwritingInfoNet, PHASE_CALC);
        setTransmitterPhaseOutput(outContractFinancials, PHASE_CALC);
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

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<DefaultProbabilities> getInDefaultProbabilities() {
        return inDefaultProbabilities;
    }

    public void setInDefaultProbabilities(PacketList<DefaultProbabilities> inDefaultProbabilities) {
        this.inDefaultProbabilities = inDefaultProbabilities;
    }

    public PacketList<ContractFinancialsPacket> getOutContractFinancials() {
        return outContractFinancials;
    }

    public void setOutContractFinancials(PacketList<ContractFinancialsPacket> outContractFinancials) {
        this.outContractFinancials = outContractFinancials;
    }
}
