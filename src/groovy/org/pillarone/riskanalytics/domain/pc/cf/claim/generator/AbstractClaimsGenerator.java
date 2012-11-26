package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.PayoutPatternBase;
import org.pillarone.riskanalytics.core.components.MultiPhaseComposedComponent;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting.CommutationState;
import org.pillarone.riskanalytics.domain.pc.cf.claim.*;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.aggregate.IAggregateActualClaimsStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reserve.updating.single.ISingleActualClaimsStrategy;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract claims generator providing all common functionality of claims generators, including required channels and
 * several helper methods for keeping track of claims.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractClaimsGenerator extends MultiPhaseComposedComponent implements IPerilMarker, ICorrelationMarker {

    static Log LOG = LogFactory.getLog(AbstractClaimsGenerator.class);

    protected PeriodScope periodScope;
    protected SimulationScope simulationScope;
    protected IterationScope iterationScope;
    protected PeriodStore periodStore;
    protected Integer globalLastCoveredPeriod;
    protected boolean globalSanityChecks;

    protected PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    protected PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    protected PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket.class);
    protected PacketList<EventDependenceStream> inEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);
    protected PacketList<SystematicFrequencyPacket> inEventFrequencies = new PacketList<SystematicFrequencyPacket>(SystematicFrequencyPacket.class);

    protected PacketList<CommutationState> inCommutationState = new PacketList<CommutationState>(CommutationState.class);
    /** don't assume any order in this channel */
    protected PacketList<ClaimCashflowPacket> outClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    protected PacketList<ClaimCashflowPacket> outOccurenceUltimateClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);

    protected boolean globalDeterministicMode = false;
    protected boolean globalTrivialIndices = false;
    protected DateTime globalUpdateDate;

    public static final String REAL_PERIOD = "Period (real number)";
    public static final String CLAIM_VALUE = "Claim value";

    protected static final String COMMUTATION_STATE = "commutation state at end of prior period";

    public static final String PHASE_CLAIMS_CALCULATION = "Claims Calculation";
    public static final String PHASE_STORE_COMMUTATION_STATE = "Store Commutation State";

    public static final DateTimeUtilities.Days360 DAYS_360 = DateTimeUtilities.Days360.US;

    @Override
    public void wire() {
        // do nothing as the sub component is used only to add a hierarchy level without using a strategy/type line
    }

    /**
     * Derives gross claim root for the current period (periodCounter) by applying payout pattern and factors.
     * For actual claims the payout pattern is adjusted according the already existing payout history.
     *
     * @param baseClaims
     * @param parmPayoutPattern
     * @param periodScope needed to derive the payouts in the current period
     * @param base
     * @return GrossClaimRoot objects of this period
     */
    protected List<GrossClaimRoot> baseClaimsOfCurrentPeriodAdjustedPattern(
            List<ClaimRoot> baseClaims, ConstrainedString parmPayoutPattern,
            IAggregateActualClaimsStrategy parmActualClaims, PeriodScope periodScope, PayoutPatternBase base) {
        PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern, IPayoutPatternMarker.class);
        List<GrossClaimRoot> grossClaimRoots = new ArrayList<GrossClaimRoot>();
        if (!baseClaims.isEmpty()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            for (ClaimRoot baseClaim : baseClaims) {
                GrossClaimRoot grossClaimRoot = parmActualClaims.claimWithAdjustedPattern(baseClaim, currentPeriod,
                        payoutPattern, periodScope, globalUpdateDate, DAYS_360, globalSanityChecks, base);
                grossClaimRoots.add(grossClaimRoot);
            }
        }
        return grossClaimRoots;
    }

    /**
     * Derives gross claim root for the current period (periodCounter) by applying payout pattern and factors.
     * For actual claims the payout pattern is adjusted according the already existing payout history.
     *
     * @param baseClaims
     * @param parmPayoutPattern
     * @param periodScope needed to derive the payouts in the current period
     * @param base
     * @return GrossClaimRoot objects of this period
     */
    protected List<GrossClaimRoot> baseClaimsOfCurrentPeriodAdjustedPattern(
            List<ClaimRoot> baseClaims, ConstrainedString parmPayoutPattern,
            ISingleActualClaimsStrategy parmActualClaims, PeriodScope periodScope, PayoutPatternBase base) {
        PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern, IPayoutPatternMarker.class);
        List<GrossClaimRoot> grossClaimRoots = new ArrayList<GrossClaimRoot>();
        if (!baseClaims.isEmpty()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            for (ClaimRoot baseClaim : baseClaims) {
                GrossClaimRoot grossClaimRoot = parmActualClaims.claimWithAdjustedPattern(baseClaim, currentPeriod,
                        payoutPattern, periodScope, globalUpdateDate, DAYS_360, globalSanityChecks, base);
                grossClaimRoots.add(grossClaimRoot);
            }
        }
        return grossClaimRoots;
    }

    /**
     * Looping over GROSS_CLAIMS in the periodStore payouts of the current period are identified and corresponding
     * claim packets generated.
     * @param claims resulting claim packets are attached to this list.
     * @param periodCounter needed to derive the payout in the current period
     * @param factors for payouts in this period
     */
    protected void developClaimsOfFormerPeriods(List<ClaimCashflowPacket> claims, IPeriodCounter periodCounter, List<Factors> factors) {
        if (!periodScope.isFirstPeriod()) {
            int currentPeriod = periodScope.getCurrentPeriod();
            int latestFormerPeriodWithNewClaims = currentPeriod - 1;
            for (int period = 0; period <= latestFormerPeriodWithNewClaims; period++) {
                int periodOffset = currentPeriod - period;
                List<GrossClaimRoot> grossClaimRoots = (List<GrossClaimRoot>) periodStore.get(GROSS_CLAIMS, -periodOffset);
                if (grossClaimRoots != null) {
                    for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
                        claims.addAll(grossClaimRoot.getClaimCashflowPackets(periodCounter, factors, false));
                        outOccurenceUltimateClaims.addAll(grossClaimRoot.occurenceCashflow(periodScope));
                    }
                }
            }
        }
    }

    /**
     * Sets GROSS_CLAIMS of periodStore with claims not occurring in current period
     * @param grossClaimRoots
     * @param periodStore
     */
    protected void storeClaimsWhichOccurInFuturePeriods(List<GrossClaimRoot> grossClaimRoots, PeriodStore periodStore) {
        List<GrossClaimRoot> claimsNotOccurringThisPeriod = new ArrayList<GrossClaimRoot>();

        for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
            boolean occurrenceInCurrentPeriod = grossClaimRoot.occurrenceInCurrentPeriod(periodScope);
            if (!grossClaimRoot.hasTrivialPayout() || !occurrenceInCurrentPeriod) {
                // add claim only to period store if development in future periods is required or occurrence is delayed
                claimsNotOccurringThisPeriod.add(grossClaimRoot);
            }
        }

        if (!grossClaimRoots.isEmpty()) {
            periodStore.put(AbstractClaimsGenerator.GROSS_CLAIMS, claimsNotOccurringThisPeriod);
        }
    }

    /**
     * Beside returning current period ClaimCashflowPacket outOccurenceUltimateClaims is filled
     * @param grossClaimRoots
     * @param runOffFactors
     * @param periodScope
     * @return ClaimCashflowPacket of grossClaimRoot with exposure start in current period
     */
    protected List<ClaimCashflowPacket> cashflowsInCurrentPeriod(List<GrossClaimRoot> grossClaimRoots,
                                                                 List<Factors> runOffFactors, PeriodScope periodScope) {
        List<ClaimCashflowPacket> claimCashflowPackets = new ArrayList<ClaimCashflowPacket>();
        IPeriodCounter counter = this.periodScope.getPeriodCounter();
        for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {
            if (grossClaimRoot.exposureStartInCurrentPeriod(periodScope)) {
                claimCashflowPackets.addAll(grossClaimRoot.getClaimCashflowPackets(counter, runOffFactors, false));
                outOccurenceUltimateClaims.addAll(grossClaimRoot.occurenceCashflow(periodScope));
            }
        }
        return claimCashflowPackets;
    }

    /**
     * Implementation void
     * @param claims
     * @param grossClaimRoots
     * @param baseClaims
     */
    protected void doCashflowChecks(List<ClaimCashflowPacket> claims, List<GrossClaimRoot> grossClaimRoots, List<ClaimRoot> baseClaims) {
        for (GrossClaimRoot grossClaimRoot : grossClaimRoots) {

        }
    }

    /**
     * List containing <b>deterministic</b> claims per period. Only filled if globalDeterministicMode is true.
     */
    private ListMultimap<Integer, ClaimRoot> presetClaimsByPeriod;

    /**
     * Provides the same ClaimRoot objects per period for every iteration
     * @param deterministicClaims parameter containing period and ultimate value
     * @param periodScope required for occurrence date calculation
     * @param claimType
     * @return unmodified (no relative parameterization) ClaimRoot objects
     */
    protected List<ClaimRoot> getDeterministicClaims(ConstrainedMultiDimensionalParameter deterministicClaims,
                                                     PeriodScope periodScope, ClaimType claimType) {
        if (presetClaimsByPeriod == null) {
            presetClaimsByPeriod = loadPresetClaims(deterministicClaims, periodScope, claimType);
        }
        return presetClaimsByPeriod.get(periodScope.getCurrentPeriod());
    }

    /**
     * Reads the deterministic claims parameters
     * @param presetClaims parameter containing period and ultimate value
     * @param periodScope required for occurrence date calculation
     * @param claimType
     * @return list with claims per period
     */
    private static ListMultimap<Integer, ClaimRoot> loadPresetClaims(ConstrainedMultiDimensionalParameter presetClaims,
                                                                     PeriodScope periodScope, ClaimType claimType) {
        ListMultimap<Integer, ClaimRoot> presetClaimsByPeriod = ArrayListMultimap.create();
        int columnIndexPeriod = presetClaims.getColumnIndex(REAL_PERIOD);
        int columnIndexClaimValue = presetClaims.getColumnIndex(CLAIM_VALUE);
        for (int row = 1; row <= presetClaims.getValueRowCount(); row++) {
            double claimValue = InputFormatConverter.getDouble(presetClaims.getValueAt(row, columnIndexClaimValue));
            double periodWithDate = InputFormatConverter.getDouble(presetClaims.getValueAt(row, columnIndexPeriod)) - 1;
            int period = (int) Math.floor(periodWithDate);
            // create the preset claims and place them in the correct period
            if (period >= 0) {
                double fractionOfPeriod = periodWithDate - period;
                DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, period, fractionOfPeriod);
                ClaimRoot claim = new ClaimRoot(claimValue, claimType, occurrenceDate, occurrenceDate);
                presetClaimsByPeriod.put(period, claim);
            }
            else {
                // ignore preset claim data outside the period scope
                throw new SimulationException("Preset claim period was outside the bounds of the iteration scope. Check claim in row; " + (row + 1)  + "."  );
            }
        }
        return presetClaimsByPeriod;
    }

    /**
     * A deal may commute before the end of the contract period. We may hence want to terminate claims generation
     * depending on the outcome in the experience account. Additionally this method does the book keeping for the commutation state
     * @param phase
     * @return true if new claims have to be generated
     */
    protected boolean provideClaims(String phase) {
        initIteration(periodStore, periodScope, phase);
        // In this phase check the commutation state from last period. If we are not commuted then calculate claims.
        if (phase.equals(PHASE_CLAIMS_CALCULATION)) {
            CommutationState commutationState = (CommutationState) (periodStore.get(COMMUTATION_STATE));

            return !(commutationState.isCommuted() || commutationState.isCommuteThisPeriod());
        }
        return false;
    }

    /**
     * In this PHASE_STORE_COMMUTATION_STATE check for incoming commutation information. If there is none, i.e the
     * inCommutationChannel is null, then assume that we want no commutation behaviour. Add an indefinite commutationState
     * object to the environment.
     * @param phase
     */
    protected void prepareProvidingClaimsInNextPeriodOrNot(String phase) {
        if (phase.equals(PHASE_STORE_COMMUTATION_STATE)) {
            if (inCommutationState != null && inCommutationState.size() == 1) {
                CommutationState packet = inCommutationState.get(0);
                periodStore.put(COMMUTATION_STATE, packet, 1);
            } else {
                throw new SimulationException("Found different to one commutationState in inCommutationState. Period: "
                        + periodScope.getCurrentPeriod() + " Number of Commutation states: " + inCommutationState.size());
            }
        }
    }

    /**
     * Adds a default CommutationState packet to the period store at the start of every iteration
     * @param periodStore the period store for this object
     * @param phase to make sure that the packet is added in the PHASE_CLAIMS_CALCULATION only
     */
    protected void initIteration(PeriodStore periodStore, PeriodScope periodScope, String phase) {
        if (periodScope.isFirstPeriod() && phase.equals(PHASE_CLAIMS_CALCULATION)) {
            periodStore.put(COMMUTATION_STATE, new CommutationState());
        }
    }

    // period store key
    public static final String GROSS_CLAIMS = "gross claims root";

    /**
     * Sets the peril marker and origin of each list element
     * @param claims
     */
    protected void setTechnicalProperties(List<ClaimCashflowPacket> claims) {
        for (ClaimCashflowPacket claim : claims) {
            claim.setMarker(this);
            claim.origin = this;
        }
    }

    /**
     * Checks for negative ultimates
     * @param baseClaims
     */
    protected void checkBaseClaims(List<ClaimRoot> baseClaims, boolean sanityChecks, IterationScope iterationScope) {
        if (sanityChecks) {
            for (ClaimRoot baseClaim : baseClaims) {
                if (baseClaim.getUltimate() < 0) {
                    throw new SimulationException("Negative claim detected... i.e an inflow of cash!: " + baseClaim.toString());
                }
                if(iterationScope.isFirstIteration()) {
                    LOG.info("claim root : " + baseClaim.toString());
                }
            }
        }
    }

    /**
     * Do some checks on the claims in this period.
     * Checks for negative paid claims
     * @param cashflowPackets
     * @param sanityChecks
     */

    protected void checkCashflowClaims(List<ClaimCashflowPacket> cashflowPackets, boolean sanityChecks) {
        if (sanityChecks) {
            for (ClaimCashflowPacket cashflowPacket : cashflowPackets) {
                if (cashflowPacket.getPaidIncrementalIndexed() < 0) {
                    throw new SimulationException("Negative claim detected... i.e an inflow of cash!; " + cashflowPacket.toString() + " \n" + "Period Info  " + periodScope.toString());
                }
            }
        }
    }



    public void allocateChannelsToPhases() {
//          Calculation channels --------------------------------------------------------------------------
        setTransmitterPhaseInput(inPatterns, PHASE_CLAIMS_CALCULATION);
        setTransmitterPhaseInput(inEventSeverities, PHASE_CLAIMS_CALCULATION);
        setTransmitterPhaseInput(inEventFrequencies, PHASE_CLAIMS_CALCULATION);
        setTransmitterPhaseInput(inFactors, PHASE_CLAIMS_CALCULATION);
        setTransmitterPhaseInput(inUnderwritingInfo, PHASE_CLAIMS_CALCULATION);

        setTransmitterPhaseOutput(outClaims, PHASE_CLAIMS_CALCULATION);
        setTransmitterPhaseOutput(outOccurenceUltimateClaims, PHASE_CLAIMS_CALCULATION);

//          Commutation channels --------------------------------------------------------------------------
        setTransmitterPhaseInput(inCommutationState, PHASE_STORE_COMMUTATION_STATE);
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    public PacketList<PatternPacket> getInPatterns() {
        return inPatterns;
    }

    public void setInPatterns(PacketList<PatternPacket> inPatterns) {
        this.inPatterns = inPatterns;
    }

    public PacketList<ClaimCashflowPacket> getOutClaims() {
        return outClaims;
    }

    public void setOutClaims(PacketList<ClaimCashflowPacket> outClaims) {
        this.outClaims = outClaims;
    }

    public PacketList<EventDependenceStream> getInEventSeverities() {
        return inEventSeverities;
    }

    public void setInEventSeverities(PacketList<EventDependenceStream> inEventSeverities) {
        this.inEventSeverities = inEventSeverities;
    }

    public PacketList<SystematicFrequencyPacket> getInEventFrequencies() {
        return inEventFrequencies;
    }

    public void setInEventFrequencies(PacketList<SystematicFrequencyPacket> inEventFrequencies) {
        this.inEventFrequencies = inEventFrequencies;
    }

    public boolean isGlobalDeterministicMode() {
        return globalDeterministicMode;
    }

    public void setGlobalDeterministicMode(boolean globalDeterministicMode) {
        this.globalDeterministicMode = globalDeterministicMode;
    }

    public DateTime getGlobalUpdateDate() {
        return globalUpdateDate;
    }

    public void setGlobalUpdateDate(DateTime globalUpdateDate) {
        this.globalUpdateDate = globalUpdateDate;
    }

    public PacketList<CommutationState> getInCommutationState() {
        return inCommutationState;
    }

    public void setInCommutationState(PacketList<CommutationState> inCommutationState) {
        this.inCommutationState = inCommutationState;
    }

    public Integer getGlobalLastCoveredPeriod() {
        return globalLastCoveredPeriod;
    }

    public void setGlobalLastCoveredPeriod(Integer globalLastCoveredPeriod) {
        this.globalLastCoveredPeriod = globalLastCoveredPeriod;
    }

    public ListMultimap<Integer, ClaimRoot> getPresetClaimsByPeriod() {
        return presetClaimsByPeriod;
    }

    public void setPresetClaimsByPeriod(ListMultimap<Integer, ClaimRoot> presetClaimsByPeriod) {
        this.presetClaimsByPeriod = presetClaimsByPeriod;
    }

    public boolean isGlobalSanityChecks() {
        return globalSanityChecks;
    }

    public void setGlobalSanityChecks(boolean globalSanityChecks) {
        this.globalSanityChecks = globalSanityChecks;
    }

    public PacketList<ClaimCashflowPacket> getOutOccurenceUltimateClaims() {
        return outOccurenceUltimateClaims;
    }

    public void setOutOccurenceUltimateClaims(PacketList<ClaimCashflowPacket> outOccurenceUltimateClaims) {
        this.outOccurenceUltimateClaims = outOccurenceUltimateClaims;
    }

    public boolean isGlobalTrivialIndices() {
        return globalTrivialIndices;
    }

    public void setGlobalTrivialIndices(boolean globalTrivialIndices) {
        this.globalTrivialIndices = globalTrivialIndices;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }
}
