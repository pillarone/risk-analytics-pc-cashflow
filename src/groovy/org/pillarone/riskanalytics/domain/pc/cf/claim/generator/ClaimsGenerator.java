package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimsGenerator extends Component implements IPerilMarker {

    private PeriodScope periodScope;
    private PeriodStore periodStore;
    private boolean globalGenerateNewClaimsInFirstPeriodOnly = true;

    private PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);
    private PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    // attritional, frequency average attritional, ...
    private IClaimsGeneratorStrategy parmClaimsModel = ClaimsGeneratorType.getDefault();
    private ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker.class, "");
    private ConstrainedString parmReportingPattern = new ConstrainedString(IReportingPatternMarker.class, "");

    // period store key
    private static final String GROSS_CLAIMS = "gross claims root";


    protected void doCalculation() {
        List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();
        IPeriodCounter periodCounter = periodScope.getPeriodCounter();

        generateClaimsOfCurrentPeriod(claims, periodCounter);
        developClaimsOfFormerPeriods(claims, periodCounter);

        outClaims.addAll(claims);
    }

    private void generateClaimsOfCurrentPeriod(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter) {
        if (globalGenerateNewClaimsInFirstPeriodOnly && periodScope.isFirstPeriod()
            || !globalGenerateNewClaimsInFirstPeriodOnly) {
            List<ClaimRoot> baseClaims = parmClaimsModel.generateClaims(periodScope);

            PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern);
            PatternPacket reportingPattern = PatternUtils.filterPattern(inPatterns, parmReportingPattern);
            List<GrossClaimRoot> grossClaimRoots = new ArrayList<GrossClaimRoot>();
            for (ClaimRoot baseClaim : baseClaims) {
                GrossClaimRoot grossClaimRoot = new GrossClaimRoot(baseClaim, payoutPattern, reportingPattern);
                if (!grossClaimRoot.hasTrivialPayout()) {
                    // add claim only to period store if development is required
                    grossClaimRoots.add(grossClaimRoot);
                }
                claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, null, true));
            }
            periodStore.put(GROSS_CLAIMS, grossClaimRoots);
        }
    }

    private void developClaimsOfFormerPeriods(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter) {
        if (!periodScope.isFirstPeriod()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            // todo(sku): optimize using globalGenerateNewClaimsInFirstPeriodOnly
            for (int period = 0; period < currentPeriod; period++) {
                int periodOffset = currentPeriod - period;
                List<GrossClaimRoot> grossClaimRoots = (List<GrossClaimRoot>) periodStore.get(GROSS_CLAIMS, -periodOffset);
                if (grossClaimRoots != null) {
                    for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
                        claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, null, false));
                    }
                }
            }
        }
    }


    public IClaimsGeneratorStrategy getParmClaimsModel() {
        return parmClaimsModel;
    }

    public void setParmClaimsModel(IClaimsGeneratorStrategy parmClaimsModel) {
        this.parmClaimsModel = parmClaimsModel;
    }

    /**
     * claims which source is a covered line
     */
    public PacketList<ClaimCashflowPacket> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<ClaimCashflowPacket> outClaims) {
        this.outClaims = outClaims;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<PatternPacket> getInPatterns() {
        return inPatterns;
    }

    public void setInPatterns(PacketList<PatternPacket> inPatterns) {
        this.inPatterns = inPatterns;
    }

    public ConstrainedString getParmPayoutPattern() {
        return parmPayoutPattern;
    }

    public void setParmPayoutPattern(ConstrainedString parmPayoutPattern) {
        this.parmPayoutPattern = parmPayoutPattern;
    }

    public ConstrainedString getParmReportingPattern() {
        return parmReportingPattern;
    }

    public void setParmReportingPattern(ConstrainedString parmReportingPattern) {
        this.parmReportingPattern = parmReportingPattern;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    /** this property is overwritten by the value set in GlobalParameters */
    public boolean isGlobalGenerateNewClaimsInFirstPeriodOnly() {
        return globalGenerateNewClaimsInFirstPeriodOnly;
    }

    public void setGlobalGenerateNewClaimsInFirstPeriodOnly(boolean globalGenerateNewClaimsInFirstPeriodOnly) {
        this.globalGenerateNewClaimsInFirstPeriodOnly = globalGenerateNewClaimsInFirstPeriodOnly;
    }
}