package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts;

import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.IExposureBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternUtils;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IPremiumContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.additionalPremium.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.caching.IAllContractClaimCache;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumSelectionConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.CoverStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ExclusionStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ICoverStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.IExclusionCoverStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.IncurredAllocation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.TermIncurredCalculation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl.TermPaidRespectIncurredByClaim;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractLayer;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.ContractStructure;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.InitialCededPremium;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.SVPWithDate;
import org.pillarone.riskanalytics.domain.utils.marker.IPremiumInfoMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */

public class PremiumRIContract extends Component implements IReinsuranceContractMarker {

    private static Log LOG = LogFactory.getLog(PremiumRIContract.class);

    /**
     * Injected by framework
     */
    private IterationScope iterationScope;
    private IterationStore iterationStore;
    private PeriodStore periodStore;
    private PeriodScope periodScope;
    private IPeriodStrategy globalCover;
    private SimulationScope simulationScope;
    private boolean globalSanityChecks;

    /**
     * Incoming information
     */
    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> inPremiumPerPeriod = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<AllPeriodUnderwritingInfoPacket> inAllPeriodUnderwritingInfo = new PacketList<AllPeriodUnderwritingInfoPacket>(AllPeriodUnderwritingInfoPacket.class);
    private PacketList<PatternPacket> inPremiumPatterns = new PacketList<PatternPacket>(PatternPacket.class);

    /**
     * Out information to be filled
     */
    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket.class);
    private PacketList<AdditionalPremium> outApAll = new PacketList<AdditionalPremium>(AdditionalPremium.class);
    private PacketList<PaidAdditionalPremium> outApAllPaid = new PacketList<PaidAdditionalPremium>(PaidAdditionalPremium.class);


    /* Out packets according to new Spec. Single vlaue packets so that the framework automatically excludes zero lines
     * on the user interface wqen viewing results */

    private PacketList<SingleValuePacket> outIncomingGrossPremium = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<SingleValuePacket> outIncomingPaidPremium = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<SingleValuePacket> outInitialCededPremium = new PacketList<SingleValuePacket>(SingleValuePacket.class);
    private PacketList<SVPWithDate> outPaidInitialPremium = new PacketList<SVPWithDate>(SVPWithDate.class);


    /**
     * User parameters
     */
    private IPremiumContractStrategy parmContractStructure = PremiumContractType.getDefault();
    private ContractCoverBase parmCoverageBase = ContractCoverBase.LOSSES_OCCURING;
    private ICoverStrategy parmCover = CoverStrategyType.getDefault();
    private IExclusionCoverStrategy parmExclusionFilter = ExclusionStrategyType.getDefault();
    private ConstrainedMultiDimensionalParameter parmPremiumCover = new ConstrainedMultiDimensionalParameter(GroovyUtils.toList("[]"),
            Arrays.asList(PremiumSelectionConstraints.PREMIUM_TITLE), ConstraintsFactory.getConstraints(PremiumSelectionConstraints.IDENTIFIER));
    private ConstrainedString parmInitialPremiumPayoutPattern = new ConstrainedString(IPremiumPatternMarker.class, "");
    private IExposureBaseStrategy parmContractBase = ExposureBaseType.getDefault();
    private ClaimCoverType parmCoverType = ClaimCoverType.SINGLE_CLAIM;

    /**
     * MAGIC STRINGS
     */

    public static final String GROSS_CLAIMS = "All covered cashflows";
    public static final String TERM_CALC = "paid term calculation cache";
    public static final String CEDED_CLAIMS = "All ceded cashflows";
    public static final String CEDED_INCURRED_CLAIMS = "All ceded cashflows";
    public static final String UWINFO = "All period UW info";
    private static final String INCOMING_PREMIUM = "Incoming premium";

    @Override
    protected void doCalculation() {
        initSimulation();
        initIteration();
        doClaimsFilter();
        ArrayList<DateTime> reportingDates = getReportingDates();
        PatternPacket initialPremiumPayoutPattern = PatternUtils.filterPattern(inPremiumPatterns, parmInitialPremiumPayoutPattern, IPremiumPatternMarker.class );
        final ContractStructure contractStructure = parmContractStructure.getContractStructure();

        IIncurredCalculation iIncurredCalculation = new TermIncurredCalculation();
        IIncurredAllocation incurredAllocation = new IncurredAllocation();


        IAllContractClaimCache claimStore = (IAllContractClaimCache) periodStore.get(GROSS_CLAIMS, -periodScope.getCurrentPeriod());
        IPaidCalculation paidCalculation = (IPaidCalculation) periodStore.get(TERM_CALC, -periodScope.getCurrentPeriod());
        claimStore.cacheClaims(inClaims, periodScope.getCurrentPeriod());
        final AllClaimsRIOutcome incurredClaimOutcome;

        IncurredLossAndApsAfterTermStructure incurredInPeriod = iIncurredCalculation.cededIncurredAndApsRespectTerm(claimStore, contractStructure,
                periodScope, parmCoverageBase, new NoPremiumPerPeriod());

        IncurredLossWithTerm thisPeriodSLoss = incurredInPeriod.getIncurredLossAfterTermStructure(periodScope.getCurrentPeriod());
        incurredClaimOutcome = incurredAllocation.allocateClaims(thisPeriodSLoss, claimStore, periodScope, parmCoverageBase);

        IPaidCalculation iPaidCalculation = new TermPaidRespectIncurredByClaim();

        if(initialPremiumPayoutPattern.isStochasticHitPattern()) {
            throw new SimulationException("Stochastic hit pattern for initial premium payout detected. This potentially generates" +
                    "stochastic payouts before the update date, and therefore is not allowed. Please change the pattern type");
        }

        List<UnderwritingInfoPacket> premiumsInThisPeriod = processIncomingPremium();
        UnderwritingInfoPacket tempPremium = sumGrossIncomingPremium(premiumsInThisPeriod);
        Collection<ContractLayer> contractLayers = contractStructure.getLayersInPeriod(periodScope.getCurrentPeriod() + 1);
        List<InitialCededPremium> initialPremiums = Lists.newArrayList();
        double initialCededPremium = 0d;
        for (ContractLayer contractLayer : contractLayers) {
            InitialCededPremium initialPremiumOjb = new InitialCededPremium(contractLayer, initialPremiumPayoutPattern);
            initialCededPremium += initialPremiumOjb.initialWrittenPremium();
            initialPremiums.add(initialPremiumOjb);
        }
        outIncomingGrossPremium.add(new SingleValuePacket(tempPremium.getPremiumWritten()));
        outIncomingPaidPremium.add(new SingleValuePacket(tempPremium.getPremiumWritten()));
        outInitialCededPremium.add(new SingleValuePacket(initialCededPremium));

        for (InitialCededPremium initialPremium : initialPremiums) {
            DateTime startDate = periodScope.getCurrentPeriodStartDate();
            for (DateTime reportingDate : reportingDates) {
                double incrementalPaid = initialPremium.incrementalPaid(startDate, reportingDate, periodScope);
                if(incrementalPaid != 0) {
                    outPaidInitialPremium.add(new SVPWithDate(incrementalPaid, reportingDate));
                }
                startDate = reportingDate;
            }
        }

        outClaimsCeded.addAll(incurredClaimOutcome.cededClaims(periodScope.getPeriodCounter()));
        outClaimsNet.addAll(incurredClaimOutcome.netClaims(periodScope.getPeriodCounter()));


        periodScope.getCurrentPeriod();

        DateTime calculationPeriodEnd = periodScope.getCurrentPeriodStartDate().plusMonths(1);


    }

    private void doClaimsFilter() {
        DateTime coverStart = periodScope.getPeriodCounter().startOfFirstPeriod();
        DateTime coverEnd = globalCover.getEndCover();
        List<ClaimCashflowPacket> uncoveredClaims = RIUtilities.uncoveredClaims(parmCoverageBase, coverStart, coverEnd, inClaims);
        inClaims.removeAll(uncoveredClaims);
        parmCover.coveredClaims(inClaims);
        parmExclusionFilter.exclusionClaims(inClaims);
        outClaimsGross.addAll(inClaims);
    }

    private void initIteration() {
        if (periodScope.isFirstPeriod()) {
            final IAllContractClaimCache claimStore = parmCoverType.getClaimCache();
            final IPaidCalculation termCalc = new TermPaidRespectIncurredByClaim();
            periodStore.put(GROSS_CLAIMS, claimStore);
            periodStore.put(TERM_CALC, termCalc);
        }
    }

    private ArrayList<DateTime> getReportingDates() {
        ArrayList<DateTime> reportingDates = new ArrayList<DateTime>();
        DateTime periodEnd;
        DateTime periodStart;
        try {
            periodStart = periodScope.getPeriodCounter().getCurrentPeriodStart();
            periodEnd = periodScope.getPeriodCounter().getCurrentPeriodEnd();
        } catch (NotInProjectionHorizon ex) {
            throw new SimulationException("Failed to instantiate reporting dates", ex);
        }

        DateTime reportingDate = periodStart;
        int i = 1;
        while (periodStart.plusMonths(i).minusMillis(1).isBefore(periodEnd)) {
            reportingDate = periodStart.plusMonths(i).minusMillis(1);
            reportingDates.add(reportingDate);
            i++;
        }
        //                Make sure the final reporting date is the final day of the period.
        if(!reportingDate.isEqual(periodEnd.minusMillis(1))) {
            reportingDates.add(periodEnd.minusMillis(1));
        }
        return reportingDates;
    }

    private UnderwritingInfoPacket sumGrossIncomingPremium(final List<UnderwritingInfoPacket> premiumsInThisPeriod) {
        double grossPremium = 0d;
        double paidPremium = 0d;
        for (UnderwritingInfoPacket underwritingInfoPacket : premiumsInThisPeriod) {
            grossPremium +=  underwritingInfoPacket.getPremiumWritten();
            paidPremium +=  underwritingInfoPacket.getPremiumPaid();
        }
        UnderwritingInfoPacket packet = new UnderwritingInfoPacket();
        packet.setPremiumPaid(paidPremium);
        packet.setPremiumWritten(grossPremium);
        return packet;
    }

    private List<UnderwritingInfoPacket> processIncomingPremium() {
        List<UnderwritingInfoPacket> premiumList = Lists.newArrayList();
        List<IPremiumInfoMarker> underwritingInfoPackets = parmPremiumCover.getValuesAsObjects(PremiumSelectionConstraints.PREMIUM_INDEX);
        for (UnderwritingInfoPacket underwritingInfoPacket : inPremiumPerPeriod) {
            if (
                    periodScope.getPeriodCounter().belongsToCurrentPeriod(underwritingInfoPacket.getDate())
                    &&
                    underwritingInfoPackets.contains( (IPremiumInfoMarker) underwritingInfoPacket.getOrigin() )

            ) {
                premiumList.add(underwritingInfoPacket);
            }
        }
        return premiumList;
    }

    private void initSimulation() {
        if (iterationScope.isFirstIteration() && periodScope.isFirstPeriod()) {

            List<AllPeriodUnderwritingInfoPacket> allPeriodUnderwritingInfoPacketList = parmContractBase.coveredAllPeriodUnderwritingInfo(inAllPeriodUnderwritingInfo);
            if (parmContractBase.exposureBase() != ExposureBase.ABSOLUTE) {
                if(allPeriodUnderwritingInfoPacketList.size() != 1) {
                    throw new SimulationException("You have set a contract to be relative to underwriting information, but the list of filtered underwriting information is of size; " + allPeriodUnderwritingInfoPacketList.size() + ". This is not allowed. " +
                            "One (and only one) piece of underwriting information is required");
                }
                AllPeriodUnderwritingInfoPacket uwInfo = allPeriodUnderwritingInfoPacketList.get(0);
                iterationStore.put(UWINFO, uwInfo);
            } else {
                AllPeriodUnderwritingInfoPacket uwInfo = new AllPeriodUnderwritingInfoPacket();
                iterationStore.put(UWINFO, uwInfo);
            }
        }
    }

    public boolean isProportionalContract() {
        throw new SimulationException("Should never be called");
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(final IterationScope iterationScope) {
        this.iterationScope = iterationScope;
    }

    public IterationStore getIterationStore() {
        return iterationStore;
    }

    public void setIterationStore(final IterationStore iterationStore) {
        this.iterationStore = iterationStore;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(final PeriodStore periodStore) {
        this.periodStore = periodStore;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(final PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public IPeriodStrategy getGlobalCover() {
        return globalCover;
    }

    public void setGlobalCover(final IPeriodStrategy globalCover) {
        this.globalCover = globalCover;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(final SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }

    public boolean isGlobalSanityChecks() {
        return globalSanityChecks;
    }

    public void setGlobalSanityChecks(final boolean globalSanityChecks) {
        this.globalSanityChecks = globalSanityChecks;
    }

    public PacketList<ClaimCashflowPacket> getInClaims() {
        return inClaims;
    }

    public void setInClaims(final PacketList<ClaimCashflowPacket> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(final PacketList<UnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
    }

    public PacketList<UnderwritingInfoPacket> getInPremiumPerPeriod() {
        return inPremiumPerPeriod;
    }

    public void setInPremiumPerPeriod(final PacketList<UnderwritingInfoPacket> inPremiumPerPeriod) {
        this.inPremiumPerPeriod = inPremiumPerPeriod;
    }

    public PacketList<AllPeriodUnderwritingInfoPacket> getInAllPeriodUnderwritingInfo() {
        return inAllPeriodUnderwritingInfo;
    }

    public void setInAllPeriodUnderwritingInfo(final PacketList<AllPeriodUnderwritingInfoPacket> inAllPeriodUnderwritingInfo) {
        this.inAllPeriodUnderwritingInfo = inAllPeriodUnderwritingInfo;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(final PacketList<ClaimCashflowPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsNet() {
        return outClaimsNet;
    }

    public void setOutClaimsNet(final PacketList<ClaimCashflowPacket> outClaimsNet) {
        this.outClaimsNet = outClaimsNet;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsCeded() {
        return outClaimsCeded;
    }

    public void setOutClaimsCeded(final PacketList<ClaimCashflowPacket> outClaimsCeded) {
        this.outClaimsCeded = outClaimsCeded;
    }

    public PacketList<ContractFinancialsPacket> getOutContractFinancials() {
        return outContractFinancials;
    }

    public void setOutContractFinancials(final PacketList<ContractFinancialsPacket> outContractFinancials) {
        this.outContractFinancials = outContractFinancials;
    }

    public PacketList<AdditionalPremium> getOutApAll() {
        return outApAll;
    }

    public void setOutApAll(final PacketList<AdditionalPremium> outApAll) {
        this.outApAll = outApAll;
    }

    public PacketList<PaidAdditionalPremium> getOutApAllPaid() {
        return outApAllPaid;
    }

    public void setOutApAllPaid(final PacketList<PaidAdditionalPremium> outApAllPaid) {
        this.outApAllPaid = outApAllPaid;
    }

    public IPremiumContractStrategy getParmContractStructure() {
        return parmContractStructure;
    }

    public void setParmContractStructure(final IPremiumContractStrategy parmContractStructure) {
        this.parmContractStructure = parmContractStructure;
    }

    public ContractCoverBase getParmCoverageBase() {
        return parmCoverageBase;
    }

    public void setParmCoverageBase(final ContractCoverBase parmCoverageBase) {
        this.parmCoverageBase = parmCoverageBase;
    }

    public ICoverStrategy getParmCover() {
        return parmCover;
    }

    public void setParmCover(final ICoverStrategy parmCover) {
        this.parmCover = parmCover;
    }

    public IExclusionCoverStrategy getParmExclusionFilter() {
        return parmExclusionFilter;
    }

    public void setParmExclusionFilter(final IExclusionCoverStrategy parmExclusionFilter) {
        this.parmExclusionFilter = parmExclusionFilter;
    }

    public ConstrainedMultiDimensionalParameter getParmPremiumCover() {
        return parmPremiumCover;
    }

    public void setParmPremiumCover(final ConstrainedMultiDimensionalParameter parmPremiumCover) {
        this.parmPremiumCover = parmPremiumCover;
    }

    public IExposureBaseStrategy getParmContractBase() {
        return parmContractBase;
    }

    public void setParmContractBase(final IExposureBaseStrategy parmContractBase) {
        this.parmContractBase = parmContractBase;
    }

    public ClaimCoverType getParmCoverType() {
        return parmCoverType;
    }

    public void setParmCoverType(final ClaimCoverType parmCoverType) {
        this.parmCoverType = parmCoverType;
    }

    public PacketList<SingleValuePacket> getOutIncomingGrossPremium() {
        return outIncomingGrossPremium;
    }

    public void setOutIncomingGrossPremium(final PacketList<SingleValuePacket> outIncomingGrossPremium) {
        this.outIncomingGrossPremium = outIncomingGrossPremium;
    }

    public PacketList<SingleValuePacket> getOutIncomingPaidPremium() {
        return outIncomingPaidPremium;
    }

    public void setOutIncomingPaidPremium(final PacketList<SingleValuePacket> outIncomingPaidPremium) {
        this.outIncomingPaidPremium = outIncomingPaidPremium;
    }

    public PacketList<SingleValuePacket> getOutInitialCededPremium() {
        return outInitialCededPremium;
    }

    public void setOutInitialCededPremium(final PacketList<SingleValuePacket> outInitialCededPremium) {
        this.outInitialCededPremium = outInitialCededPremium;
    }

    public PacketList<PatternPacket> getInPremiumPatterns() {
        return inPremiumPatterns;
    }

    public void setInPremiumPatterns(final PacketList<PatternPacket> inPremiumPatterns) {
        this.inPremiumPatterns = inPremiumPatterns;
    }

    public ConstrainedString getParmInitialPremiumPayoutPattern() {
        return parmInitialPremiumPayoutPattern;
    }

    public void setParmInitialPremiumPayoutPattern(final ConstrainedString parmInitialPremiumPayoutPattern) {
        this.parmInitialPremiumPayoutPattern = parmInitialPremiumPayoutPattern;
    }

    public PacketList<SVPWithDate> getOutPaidInitialPremium() {
        return outPaidInitialPremium;
    }

    public void setOutPaidInitialPremium(final PacketList<SVPWithDate> outPaidInitialPremium) {
        this.outPaidInitialPremium = outPaidInitialPremium;
    }
}
