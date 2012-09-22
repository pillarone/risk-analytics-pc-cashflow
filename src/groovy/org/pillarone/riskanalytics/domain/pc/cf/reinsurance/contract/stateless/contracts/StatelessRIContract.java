package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts;

import com.google.common.collect.SetMultimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.SimulationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ICededRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.ExposureBaseType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.filter.IExposureBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.*;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.CoverStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ICoverStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.IncurredAllocation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.TermIncurredCalculation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl.ProportionalToGrossPaidAllocation;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl.TermPaidRespectIncurredByClaim;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies.NonPropTemplateContractStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.*;

/**
 * author simon.parten @ art-allianz . com
 */
public class StatelessRIContract extends Component implements IReinsuranceContractMarker {

    private static Log LOG = LogFactory.getLog(StatelessRIContract.class);

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
    private PacketList<AllPeriodUnderwritingInfoPacket> inAllPeriodUnderwritingInfo = new PacketList<AllPeriodUnderwritingInfoPacket>(AllPeriodUnderwritingInfoPacket.class);
//    Need to wire up some premiums. Hell on earth.

    /**
     * Out information to be filled
     */
    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket.class);

    private IReinsuranceContractStrategy parmContractStructure = TemplateContractType.getDefault();

    /**
     * User parameters
     */
    private ContractCoverBase parmCoverageBase = ContractCoverBase.LOSSES_OCCURING;
    private ICoverStrategy parmCover = CoverStrategyType.getDefault();
    private IExposureBaseStrategy parmContractBase = ExposureBaseType.getDefault();

    /**
     * MAGIC STRINGS
     */

    public static final String GROSS_CLAIMS = "All covered cashflows";
    public static final String CEDED_CLAIMS = "All ceded cashflows";
    public static final String CEDED_INCURRED_CLAIMS = "All ceded cashflows";
    public static final String UWINFO = "All period UW info";

    @Override
    protected void doCalculation() {
        initIteration();
        doFilter();
        List<ClaimCashflowPacket> claims = new ArrayList<ClaimCashflowPacket>();
        claims.addAll(inClaims);
        periodStore.put(GROSS_CLAIMS, claims);
        periodStore.put(CEDED_CLAIMS, new ArrayList<ClaimCashflowPacket>()); /* Otherwise null pointer on startup - overwrite this list later in this method */
        List<ICededRoot> allIncurredCededClaims = new ArrayList<ICededRoot>();
        List<ClaimCashflowPacket> allCashflowsToDate = allClaimsToDate(periodScope, periodStore, GROSS_CLAIMS);
        List<ClaimCashflowPacket> cededCashflowsToDate = allClaimsToDate(periodScope, periodStore, CEDED_CLAIMS);
        SetMultimap<IClaimRoot, IClaimRoot> incurredClaims = RIUtilities.incurredClaims(allCashflowsToDate);
        Set<ICededRoot> allIncurredCeded = RIUtilities.incurredCededClaims(cededCashflowsToDate, IncurredClaimBase.BASE);
        allIncurredCededClaims.addAll(new ArrayList<ICededRoot>(allIncurredCeded));

        ScaledPeriodLayerParameters layerParameters = ((ScaledPeriodLayerParameters) ((NonPropTemplateContractStrategy) parmContractStructure).scalablePeriodLayerParameters());
        layerParameters.setExposureBase(parmContractBase.exposureBase());
        layerParameters.setCounter(periodScope.getPeriodCounter());
        AllPeriodUnderwritingInfoPacket packet = (AllPeriodUnderwritingInfoPacket) iterationStore.get(UWINFO, -periodScope.getCurrentPeriod());
        layerParameters.setUwInfo(packet);
        List<LayerParameters> layers = layerParameters.getLayers(periodScope.getCurrentPeriod());
        Double termLimit = ((NonPropTemplateContractStrategy) parmContractStructure).getTermLimit();
        Double termExcess = ((NonPropTemplateContractStrategy) parmContractStructure).getTermDeductible();


//        Incurred calc
        Set<IClaimRoot> incurredClaimsInContractPeriod = RIUtilities.incurredClaimsByDate(periodScope.getCurrentPeriodStartDate(), periodScope.getNextPeriodStartDate().minusMillis(1),
                incurredClaims, parmCoverageBase, IncurredClaimBase.BASE);
        IncurredClaimAndAP incurredPeriodResult = incurredResultsThisSimPeriod(incurredClaimsInContractPeriod);
        IncurredAllocation incurredAllocation = new IncurredAllocation();
        TermIncurredCalculation incurredCalc = new TermIncurredCalculation();
        List<ClaimCashflowPacket> paidClaims;
        List<ContractFinancialsPacket> contractFinancialsPacket;
        try {
            final Map<Integer, Double> cededIncurredByPeriod = incurredCalc.cededIncurredsByPeriods(incurredClaims.keys(), periodScope, termExcess, termLimit, layerParameters, parmCoverageBase);
            final Set<IClaimRoot> allIncurredClaims = RIUtilities.incurredClaims(allCashflowsToDate, IncurredClaimBase.BASE);
            final List<ICededRoot> cededClaims = incurredAllocation.allocateClaims(incurredPeriodResult.getIncurredClaim(), allIncurredClaims, periodScope, parmCoverageBase);
            allIncurredCededClaims.addAll(cededClaims);


            final Map<Integer, Double> incrementalPaidInThisSimulationPeriod = incrementalPaidAmountByModelPeriod(allCashflowsToDate);

            IPaidAllocation iRiPaidAllocation = new ProportionalToGrossPaidAllocation();
            paidClaims = iRiPaidAllocation.allocatePaid(incrementalPaidInThisSimulationPeriod, inClaims,
                    cededCashflowsToDate, periodScope, parmCoverageBase, allIncurredCededClaims, globalSanityChecks);
            contractFinancialsPacket = ContractFinancialsPacket.getContractFinancialsPacketsByInceptionPeriod(
                    paidClaims, new ArrayList<ClaimCashflowPacket>(), new ArrayList<CededUnderwritingInfoPacket>(),
                    new ArrayList<UnderwritingInfoPacket>(), periodScope.getPeriodCounter());
        } catch (SimulationException ex) {
            throw new SimulationException(" Insanity detected in structure module : " + this.getName() + "\n In iteration :"
                    + iterationScope.getCurrentIteration() + "\n Period : " + periodScope.getCurrentPeriod() + ". Seed : " + simulationScope.getSimulation().getRandomSeed() + "   .... Please see logs.   \n" + ex.getMessage(), ex);
        }
        RIUtilities.addMarkers(paidClaims, this);
        outClaimsCeded.addAll(paidClaims);
        outContractFinancials.addAll(contractFinancialsPacket);
        periodStore.put(CEDED_CLAIMS, paidClaims);

    }

    private void initIteration() {
        if (iterationScope.isFirstIteration() && periodScope.isFirstPeriod()) {
            if (parmContractBase.exposureBase() != ExposureBase.ABSOLUTE) {
                AllPeriodUnderwritingInfoPacket uwInfo = inAllPeriodUnderwritingInfo.get(0);
                iterationStore.put(UWINFO, uwInfo);
            } else {
                AllPeriodUnderwritingInfoPacket uwInfo = new AllPeriodUnderwritingInfoPacket();
                iterationStore.put(UWINFO, uwInfo);
            }
        }
    }

    private IncurredClaimAndAP incurredResultsThisSimPeriod(Set<IClaimRoot> incurredClaimsInContractPeriod) {

        IIncurredCalculation incurredCalc = new TermIncurredCalculation();

        ScaledPeriodLayerParameters layerParameters = ((ScaledPeriodLayerParameters) ((NonPropTemplateContractStrategy) parmContractStructure).scalablePeriodLayerParameters());
        layerParameters.setExposureBase(parmContractBase.exposureBase());
        layerParameters.setCounter(periodScope.getPeriodCounter());
        AllPeriodUnderwritingInfoPacket packet = (AllPeriodUnderwritingInfoPacket) iterationStore.get(UWINFO, -periodScope.getCurrentPeriod());
        layerParameters.setUwInfo(packet);
        List<LayerParameters> layers = layerParameters.getLayers(periodScope.getCurrentPeriod());
        Double termLimit = ((NonPropTemplateContractStrategy) parmContractStructure).getTermLimit();
        Double termExcess = ((NonPropTemplateContractStrategy) parmContractStructure).getTermDeductible();

        List<ClaimCashflowPacket> claims = allClaimsToDate(periodScope, periodStore, GROSS_CLAIMS);
        List<IClaimRoot> incurredClaims = new ArrayList<IClaimRoot>(RIUtilities.incurredClaims(claims, IncurredClaimBase.BASE));

        double incurredInPeriod = incurredCalc.cededIncurredRespectTerm(incurredClaims, layerParameters, periodScope, termExcess, termLimit, periodScope.getPeriodCounter(), parmCoverageBase);
        double additionalPremiumInperiod = incurredCalc.additionalPremiumAllLayers(incurredClaimsInContractPeriod, layers, 0d);
        return new IncurredClaimAndAP(incurredInPeriod, additionalPremiumInperiod);
    }

    private Map<Integer, Double> incrementalPaidAmountByModelPeriod(List<ClaimCashflowPacket> allPaidClaims) {
        ScaledPeriodLayerParameters layerParameters = ((NonPropTemplateContractStrategy) parmContractStructure).scalablePeriodLayerParameters();
        layerParameters.setExposureBase(parmContractBase.exposureBase());
        layerParameters.setCounter(periodScope.getPeriodCounter());
        AllPeriodUnderwritingInfoPacket packet = (AllPeriodUnderwritingInfoPacket) iterationStore.get(UWINFO, -periodScope.getCurrentPeriod());
        layerParameters.setUwInfo(packet);
        List<LayerParameters> layers = layerParameters.getLayers(periodScope.getCurrentPeriod());
        Double termLimit = ((NonPropTemplateContractStrategy) parmContractStructure).getTermLimit();
        Double termExcess = ((NonPropTemplateContractStrategy) parmContractStructure).getTermDeductible();


        List<ClaimCashflowPacket> claims = allClaimsToDate(periodScope, periodStore, GROSS_CLAIMS);

        TermPaidRespectIncurredByClaim iPaidCalculation = new TermPaidRespectIncurredByClaim();
        DateTime endPriorPeriod = periodScope.getCurrentPeriodStartDate().minusMillis(1);
        Map<Integer, Double> paidByModelPeriod = iPaidCalculation.cededIncrementalPaidRespectTerm(claims, layerParameters,
                periodScope, parmCoverageBase, termLimit, termExcess, endPriorPeriod, periodScope.getNextPeriodStartDate(), globalSanityChecks);
        return paidByModelPeriod;
    }


    List<ClaimCashflowPacket> allClaimsToDate(PeriodScope periodScope, PeriodStore periodStore, String storeKey) {
        List<ClaimCashflowPacket> allClaims = new ArrayList<ClaimCashflowPacket>();
        for (int i = 0; i <= periodScope.getCurrentPeriod(); i++) {
            List<ClaimCashflowPacket> tempClaims = (List<ClaimCashflowPacket>) periodStore.get(storeKey, -i);
            allClaims.addAll(tempClaims);
        }
        return allClaims;
    }

    private void doFilter() {
        DateTime coverStart = periodScope.getPeriodCounter().startOfFirstPeriod();
        DateTime coverEnd = globalCover.getEndCover();

        List<ClaimCashflowPacket> uncoveredClaims = GRIUtilities.uncoveredClaims(parmCoverageBase, coverStart, coverEnd, inClaims);
        inClaims.removeAll(uncoveredClaims);
        outClaimsGross.addAll(inClaims);
        parmCover.coveredClaims(inClaims);
        parmContractBase.coveredUnderwritingInfo(inUnderwritingInfo);
    }

    public boolean isProportionalContract() {
        return false;
    }

    public IterationScope getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(IterationScope iterationScope) {
        this.iterationScope = iterationScope;
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

    public PacketList<ClaimCashflowPacket> getInClaims() {
        return inClaims;
    }

    public void setInClaims(PacketList<ClaimCashflowPacket> inClaims) {
        this.inClaims = inClaims;
    }

    public PacketList<UnderwritingInfoPacket> getInUnderwritingInfo() {
        return inUnderwritingInfo;
    }

    public void setInUnderwritingInfo(PacketList<UnderwritingInfoPacket> inUnderwritingInfo) {
        this.inUnderwritingInfo = inUnderwritingInfo;
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

    public PacketList<ContractFinancialsPacket> getOutContractFinancials() {
        return outContractFinancials;
    }

    public void setOutContractFinancials(PacketList<ContractFinancialsPacket> outContractFinancials) {
        this.outContractFinancials = outContractFinancials;
    }

    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStructure;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStructure = parmContractStrategy;
    }

    public ContractCoverBase getParmCoverageBase() {
        return parmCoverageBase;
    }

    public void setParmCoverageBase(ContractCoverBase parmCoverageBase) {
        this.parmCoverageBase = parmCoverageBase;
    }

    public ICoverStrategy getParmCover() {
        return parmCover;
    }

    public void setParmCover(ICoverStrategy parmCover) {
        this.parmCover = parmCover;
    }

    public IExposureBaseStrategy getParmContractBase() {
        return parmContractBase;
    }

    public void setParmContractBase(IExposureBaseStrategy parmContractBase) {
        this.parmContractBase = parmContractBase;
    }

    public IPeriodStrategy getGlobalCover() {
        return globalCover;
    }

    public void setGlobalCover(IPeriodStrategy globalCover) {
        this.globalCover = globalCover;
    }

    public boolean isGlobalSanityChecks() {
        return globalSanityChecks;
    }

    public void setGlobalSanityChecks(boolean globalSanityChecks) {
        this.globalSanityChecks = globalSanityChecks;
    }

    public IReinsuranceContractStrategy getParmContractStructure() {
        return parmContractStructure;
    }

    public void setParmContractStructure(IReinsuranceContractStrategy parmContractStructure) {
        this.parmContractStructure = parmContractStructure;
    }

    public PacketList<AllPeriodUnderwritingInfoPacket> getInAllPeriodUnderwritingInfo() {
        return inAllPeriodUnderwritingInfo;
    }

    public void setInAllPeriodUnderwritingInfo(PacketList<AllPeriodUnderwritingInfoPacket> inAllPeriodUnderwritingInfo) {
        this.inAllPeriodUnderwritingInfo = inAllPeriodUnderwritingInfo;
    }

    public IterationStore getIterationStore() {
        return iterationStore;
    }

    public void setIterationStore(IterationStore iterationStore) {
        this.iterationStore = iterationStore;
    }

    public SimulationScope getSimulationScope() {
        return simulationScope;
    }

    public void setSimulationScope(SimulationScope simulationScope) {
        this.simulationScope = simulationScope;
    }
}
