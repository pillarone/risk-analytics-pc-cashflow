package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountUtils;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountedValuesPacket;
import org.pillarone.riskanalytics.domain.pc.cf.discounting.NetPresentValuesPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.IPropReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.*;

/**
 * General implementation guidelines:<ul>
 *     <li>We use a claim/underwriting packet centric approach and avoid history tracking within this and derived classes.</li>
 *     <li>There are specific claim storage objects (@link ClaimStorage) keeping track of a minimal information of each
 *          key claim. So this base class and its derived class don't track individual claims.</li>
 *     <li>For every single covered period a contract object is instantiated implementing IReinsuranceContract. These
 *          objects are linked with claims in fillGrossClaims() by creating ClaimHistoryAndApplicableContract objects.</li>
 *     <li>If information needs to be shared among several periods specific objects are added on this class. Available
 *          implementations are so far ThresholdStore and EqualUsagePerPeriodThresholdStore.</li>
 *     <li>This abstract base class should not contain any parameters to keep it easily extensible for the UI purposes.</li>
 * </ul>
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class BaseReinsuranceContract extends Component implements IReinsuranceContractMarker {

    private static Log LOG = LogFactory.getLog(BaseReinsuranceContract.class);

    protected IterationScope iterationScope;
    protected PeriodStore periodStore;

    protected PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    /** PMO-1635: gets GNPI underwriting info always */
    protected PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    protected PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);

    /** Contains gross claims covered in the current periods according to their time and cover filter. This includes
     *  gross claims for which there is no cover left or no cover available as the counter party has gone default. */
    protected PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    protected PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    protected PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    protected PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    protected PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    protected PacketList<UnderwritingInfoPacket> outUnderwritingInfoGNPI = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    protected PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    protected PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket.class);
    protected PacketList<CommissionPacket> outCommission = new PacketList<CommissionPacket>(CommissionPacket.class);
    protected PacketList<DiscountedValuesPacket> outDiscountedValues = new PacketList<DiscountedValuesPacket>(DiscountedValuesPacket.class);
    protected PacketList<NetPresentValuesPacket> outNetPresentValues = new PacketList<NetPresentValuesPacket>(NetPresentValuesPacket.class);

    protected Boolean isProportionalContract;
    protected Boolean hasMultipleContractsPerPeriod = false;


    @Override
    protected void doCalculation() {
        // check cover
        initSimulation();
        initIteration();
        filterInChannels();
        updateContractParameters();
        Set<IReinsuranceContract> contracts = fillGrossClaims();
        initPeriod(contracts);
        initContracts(contracts);
        IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
        calculateCededClaims(periodCounter);
        processUnderwritingInfo();
        fillUnderwritingInfoGNPIChannel();
        discountClaims(periodCounter);
        fillContractFinancialsChannel(periodCounter);
    }

    protected void initSimulation() {
    }

    protected void initIteration() {
    }

    /**
     * Resetting contract member variables like deductibles, limits, ...
     * @param contracts to be prepared for the calculation of the current period
     */
    protected void initPeriod(Set<IReinsuranceContract> contracts) {
        initIsProportionalContract(contracts);
        initHasMultipleContractsPerPeriod(contracts);
        int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
        if (isProportionalContract) {
            for (int period = 0; period < currentPeriod; period++) {
                List<IReinsuranceContract> periodContracts = (List<IReinsuranceContract>) periodStore.get(REINSURANCE_CONTRACT, -currentPeriod + period);
                if (periodContracts != null) {
                    for (IReinsuranceContract contract : periodContracts) {
                        // for all proportional contracts underwriting info of preceding periods needs to be cleared
                        contract.initPeriod(currentPeriod, inFactors);
                    }
                }
            }
        }
        else {
            for (IReinsuranceContract contract : contracts) {
                contract.initPeriod(currentPeriod, inFactors);
            }
        }
    }

    /**
     * filter according to covered period and covered claims generators, segments and companies
     */
    private void filterInChannels() {
        timeFilter();
        coverFilter();
    }

    /**
     *  Filter according to covered period, occurrence date of claim and defaulting counter parties.
     */
    abstract protected void timeFilter();

    /**
     * filter according to covered claims generators, segments and companies (parmCover)
     */
    abstract protected void coverFilter();

    /**
     * Add for every covered period a new contract instance to the periodStore. Generally contracts of different periods
     * are completely independent except there is a common term clause.
     */
    abstract protected void updateContractParameters();

    /**
     * Make sure a ClaimStorage object is created for every new CashflowClaimPacket and put in the first time slot of
     * the periodStore with key CLAIM_HISTORY. This object contains the incremental history for paid and reported.
     * Put a list of claims sorted by update date with an update in this period containing a reference to their history
     * to the current periodStore time slot using key GROSS_CLAIMS.
     *
     * @return all contracts with new claim updates in current period
     */
    private Set<IReinsuranceContract> fillGrossClaims() {
        Set<IReinsuranceContract> contracts = new HashSet<IReinsuranceContract>();
        IPeriodCounter periodCounter = iterationScope.getPeriodScope().getPeriodCounter();
        Map<IClaimRoot, ClaimStorage> claimsHistories =
                (HashMap<IClaimRoot, ClaimStorage>) periodStore.getFirstPeriod(CLAIM_HISTORY);
        List<ClaimHistoryAndApplicableContract> currentPeriodGrossClaims = new ArrayList<ClaimHistoryAndApplicableContract>();
        int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
        if (claimsHistories == null) {
            // executed in first period
            claimsHistories = new HashMap<IClaimRoot, ClaimStorage>();
            periodStore.put(CLAIM_HISTORY, claimsHistories);
            for (ClaimCashflowPacket claim : inClaims) {
                // claimsHistory needs to be queried for first period too as there might be several claim updates in it
                ClaimStorage claimStorage = claimsHistories.get(claim.getKeyClaim());
                // PMO-1963: calculation of an occurrence period before the projection start does not work
                //           as current retrospective contracts don't cover specific periods all is mapped to period 0
                int occurrencePeriod = 0;
                if (claim.reserve() == null) {
                    // claim source is not a reserve generator
                    occurrencePeriod = claim.occurrencePeriod(periodCounter);
                }
                if (claimStorage == null) {
                    // first time this claim enters this contract
                    claimStorage = newClaimOccurredInCurrentPeriod(claim, claimsHistories);
                }
                updateCurrentPeriodGrossClaims(contracts, currentPeriodGrossClaims, currentPeriod, claim, occurrencePeriod, claimStorage);
            }
        }
        else {
            for (ClaimCashflowPacket claim : inClaims) {
                // PMO-1963: calculation of an occurrence period before the projection start does not work
                //           as current retrospective contracts don't cover specific periods all is mapped to period 0
                int occurrencePeriod = 0;
                if (claim.reserve() == null) {
                    occurrencePeriod = claim.occurrencePeriod(periodCounter);
                }
                ClaimStorage claimStorage = claimsHistories.get(claim.getKeyClaim());
                if (currentPeriod == occurrencePeriod && claimStorage == null) {
                    claimStorage = newClaimOccurredInCurrentPeriod(claim, claimsHistories);
                }
                if (claimStorage != null)  {
                    updateCurrentPeriodGrossClaims(contracts, currentPeriodGrossClaims, currentPeriod, claim, occurrencePeriod, claimStorage);
                }
                else {
                    LOG.error("claimStorage is null");
                }
            }
        }
        // sort currentPeriodGrossClaims by updateDate
        Collections.sort(currentPeriodGrossClaims, SortClaimHistoryAndApplicableContract.getInstance());
        periodStore.put(GROSS_CLAIMS, currentPeriodGrossClaims);
        return contracts;
    }

    /**
     *
     * @param contracts the contract covering the claim is added to the set
     * @param currentPeriodGrossClaims is extended with the ClaimHistoryAndApplicableContract of the claim, claimStorage and contract
     * @param currentPeriod used to get the applicable contract from the periodStore
     * @param claim is used to fill currentPeriodGrossClaims
     * @param occurrencePeriod used to get the applicable contract from the periodStore
     * @param claimStorage is used to fill currentPeriodGrossClaims
     */
    private void updateCurrentPeriodGrossClaims(Set<IReinsuranceContract> contracts,
                                                List<ClaimHistoryAndApplicableContract> currentPeriodGrossClaims,
                                                int currentPeriod, ClaimCashflowPacket claim, int occurrencePeriod,
                                                ClaimStorage claimStorage) {
        List<IReinsuranceContract> periodContracts = (List<IReinsuranceContract>) periodStore.get(REINSURANCE_CONTRACT, occurrencePeriod - currentPeriod);
        contracts.addAll(periodContracts);
        for (IReinsuranceContract contract : periodContracts) {
            ClaimHistoryAndApplicableContract claimWithHistory = new ClaimHistoryAndApplicableContract(claim, claimStorage, contract);
            currentPeriodGrossClaims.add(claimWithHistory);
        }
    }

    /**
     *
     * @param claim is used to fill the claimsHistories
     * @param claimsHistories gets a new element using the key claim and a ClaimStorage created of the claim
     * @return contract covering the claim
     */
    // todo(sku): avoid mixing function and method
    private ClaimStorage newClaimOccurredInCurrentPeriod(ClaimCashflowPacket claim, Map<IClaimRoot, ClaimStorage> claimsHistories) {
        ClaimStorage claimStorage = new ClaimStorage(claim);
        claimsHistories.put(claim.getKeyClaim(), claimStorage);
        return claimStorage;
    }

    /**
     * Calls contract.initPeriodClaims for each contract
     * @param contracts
     */
    private void initContracts(Set<IReinsuranceContract> contracts) {
        for (IReinsuranceContract contract : contracts) {
            List<ClaimHistoryAndApplicableContract> currentPeriodGrossClaims = (List<ClaimHistoryAndApplicableContract>) periodStore.get(GROSS_CLAIMS);
            List<ClaimCashflowPacket> contractGrossClaims = new ArrayList<ClaimCashflowPacket>();
            for (ClaimHistoryAndApplicableContract grossClaim : currentPeriodGrossClaims) {
                if (grossClaim.hasContract(contract)) {
                    contractGrossClaims.add(grossClaim.getGrossClaim());
                }
            }
            contract.initPeriodClaims(contractGrossClaims);
        }
    }

    /**
     * This has to be done on a claims by claims level and not by contract to consider paid dates and parameters shared
     * among several periods correctly.
     * @param periodCounter
     */
    private void calculateCededClaims(IPeriodCounter periodCounter) {
        List<ClaimHistoryAndApplicableContract> currentPeriodGrossClaims = (List<ClaimHistoryAndApplicableContract>) periodStore.get(GROSS_CLAIMS);
        Map<IClaimRoot, IClaimRoot> netBaseClaimPerGrossClaim = (Map<IClaimRoot, IClaimRoot>) periodStore.get(NET_BASE_CLAIMS);
        if (netBaseClaimPerGrossClaim == null) {
            netBaseClaimPerGrossClaim = new HashMap<IClaimRoot, IClaimRoot>();
            periodStore.put(NET_BASE_CLAIMS, netBaseClaimPerGrossClaim);
        }
        for (ClaimHistoryAndApplicableContract grossClaim : currentPeriodGrossClaims) {
            ClaimCashflowPacket cededClaim = null;
            ListMultimap<ClaimCashflowPacket, ClaimCashflowPacket> cededClaimsByGrossClaim = ArrayListMultimap.create();
            if (!grossClaim.isTrivialContract()) {
                cededClaim = grossClaim.getCededClaim(periodCounter);
                double coveredByReinsurers = coveredByReinsurers(grossClaim.getUpdateDate());
                if (coveredByReinsurers != 1) {
                    cededClaim = ClaimUtils.scale(cededClaim, coveredByReinsurers, true, true);
                }
                ClaimUtils.applyMarkers(grossClaim.getGrossClaim(), cededClaim);
                cededClaim.setMarker(this);
                if (hasMultipleContractsPerPeriod) {
                    cededClaimsByGrossClaim.put(grossClaim.getGrossClaim(), cededClaim);
                }
                else {
                    outClaimsCeded.add(cededClaim);
                    if (isSenderWired(outClaimsGross) || isSenderWired(outDiscountedValues) || isSenderWired(outNetPresentValues)) {
                        // fill outClaimsGross temporarily if discounting or net present values are calculated as they are relaying on it
                        outClaimsGross.add(grossClaim.getGrossClaim());
                    }
                }
            }
            if (hasMultipleContractsPerPeriod) {
                mergeCededClaimsForMultipleContractsInSamePeriod(cededClaimsByGrossClaim);
            }
            if (isSenderWired(outClaimsNet) || isSenderWired(outDiscountedValues) || isSenderWired(outNetPresentValues)) {
                // fill outClaimsNet temporarily if discounting or net present values are calculated as they are relaying on it
                IClaimRoot netBaseClaim = netBaseClaimPerGrossClaim.get(grossClaim.getGrossClaim().getKeyClaim());
                ClaimCashflowPacket netClaim;
                if (netBaseClaim != null) {
                    netClaim = ClaimUtils.getNetClaim(grossClaim.getGrossClaim(), cededClaim, netBaseClaim, this);
                }
                else {
                    netClaim = ClaimUtils.getNetClaim(grossClaim.getGrossClaim(), cededClaim, this);
                    netBaseClaimPerGrossClaim.put(grossClaim.getGrossClaim().getKeyClaim(), netClaim.getBaseClaim());
                }
                outClaimsNet.add(netClaim);
            }
        }
        periodStore.put(NET_BASE_CLAIMS, netBaseClaimPerGrossClaim, 1);
    }

    protected double coveredByReinsurers(DateTime updateDate) {
        return 1;
    }

    /**
     * merge ceded claim of same gross claim and update date to avoid multiple additions of gross claims
     * and correct cover in following contracts based on ceded
     * @param cededClaimsByGrossClaim
     */
    private void mergeCededClaimsForMultipleContractsInSamePeriod(ListMultimap<ClaimCashflowPacket, ClaimCashflowPacket> cededClaimsByGrossClaim) {
        for (Map.Entry<ClaimCashflowPacket, ClaimCashflowPacket> entry : cededClaimsByGrossClaim.entries()) {
            List<ClaimCashflowPacket> cededClaims = (List<ClaimCashflowPacket>) entry.getValue();
            outClaimsCeded.add(ClaimUtils.sum(cededClaims, true));
            if (isSenderWired(outClaimsGross) || isSenderWired(outDiscountedValues) || isSenderWired(outNetPresentValues)) {
                // fill outClaimsGross temporarily if discounting or net present values are calculated as they are relaying on it
                outClaimsGross.add(entry.getKey());
            }
        }
    }

    private void processUnderwritingInfo() {
        int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
        // map underwriting info to corresponding contracts
        Set<IReinsuranceContract> contracts = new HashSet<IReinsuranceContract>();
        if (!inUnderwritingInfo.isEmpty()) {
            for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
                int inceptionPeriod = currentPeriod;
                if (underwritingInfo.getExposure() != null) {
                    inceptionPeriod = underwritingInfo.getExposure().getInceptionPeriod();
                }
                List<IReinsuranceContract> periodContracts = (List<IReinsuranceContract>) periodStore.get(REINSURANCE_CONTRACT, inceptionPeriod - currentPeriod);
                if (periodContracts != null) {
                    for (IReinsuranceContract contract : periodContracts) {
                        contracts.add(contract);
                        contract.add(underwritingInfo);
                    }
                }
            }
        }
        else {
            for (int period = 0; period <= currentPeriod; period++) {
                List<IReinsuranceContract> periodContracts = (List<IReinsuranceContract>) periodStore.get(REINSURANCE_CONTRACT, period - currentPeriod);
                if (periodContracts != null) {
                    for (IReinsuranceContract contract : periodContracts) {
                        contracts.add(contract);
                    }
                }
            }
        }
        if (isSenderWired(outUnderwritingInfoGross)) {
            outUnderwritingInfoGross.addAll(inUnderwritingInfo);
        }
        for (IReinsuranceContract contract : contracts) {
            double coveredByReinsurers = coveredByReinsurers(iterationScope.getPeriodScope().getCurrentPeriodStartDate());
            contract.calculateUnderwritingInfo(outUnderwritingInfoCeded, outUnderwritingInfoNet, coveredByReinsurers,
                    isSenderWired(outUnderwritingInfoNet));
        }
        for (UnderwritingInfoPacket cededUnderwritingPacket : outUnderwritingInfoCeded) {
            cededUnderwritingPacket.setMarker(this);
        }
        for (UnderwritingInfoPacket netUnderwritingPacket : outUnderwritingInfoNet) {
            netUnderwritingPacket.setMarker(this);
        }
    }

    private void fillUnderwritingInfoGNPIChannel() {
        if (isSenderWired(outUnderwritingInfoGNPI)) {
            if (isProportionalContract()) {
                if (inUnderwritingInfo.size() == outUnderwritingInfoCeded.size()) {
                    for (int i = 0; i < inUnderwritingInfo.size(); i++) {
                        // todo(sku): this implementation is dangerous: it assumes the same order of in and out uw info items
                        if (inUnderwritingInfo.get(i).getOriginal().equals(outUnderwritingInfoCeded.get(i).getOriginal())) {
                            outUnderwritingInfoGNPI.add(inUnderwritingInfo.get(i).getNet(outUnderwritingInfoCeded.get(i), true));
                        }
                        else {
                            throw new RuntimeException("original uw info mismatch.");
                        }
                    }
                }
                else {
                    throw new RuntimeException("different number of incoming GNPI and ceded uw info.");
                }
            }
            else {
                // non proportional contracts don't affect incoming underwriting info
                for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
                    UnderwritingInfoPacket clone = (UnderwritingInfoPacket) underwritingInfo.clone();
                    clone.setMarker(this);
                    outUnderwritingInfoGNPI.add(clone);
                }
            }
        }
    }

    /**
     * Fills outDiscountedValues and outNetPresentValues if wired by using helper methods of DiscountUtils.
     * @param periodCounter
     */
    private void discountClaims(IPeriodCounter periodCounter) {
        if (isSenderWired(outDiscountedValues) || isSenderWired(outNetPresentValues)) {
            DiscountUtils.getDiscountedGrossValues(outClaimsGross, periodStore, periodCounter);
            DiscountUtils.getDiscountedNetValuesAndFillOutChannels(outClaimsCeded, outClaimsNet, outDiscountedValues,
                    outNetPresentValues, periodStore, iterationScope);
            if (!isSenderWired(outClaimsGross)) {
                outClaimsGross.clear();
            }
            if (!isSenderWired(outClaimsNet)) {
                outClaimsNet.clear();
            }
        }
    }

    private void fillContractFinancialsChannel(IPeriodCounter periodCounter) {
        if (isSenderWired(outContractFinancials)) {
            outContractFinancials.addAll(ContractFinancialsPacket.getContractFinancialsPacketsByInceptionPeriod(outClaimsCeded,
                    outClaimsNet, outUnderwritingInfoCeded, outUnderwritingInfoNet, periodCounter));
        }
    }


    protected boolean firstIterationAndPeriod() {
        return iterationScope.isFirstIteration() && iterationScope.getPeriodScope().isFirstPeriod();
    }

    protected boolean isCurrentPeriodCovered() {
        return true;
    }

    // periodStore keys
    protected static final String REINSURANCE_CONTRACT = "reinsurance contract";
    private static final String GROSS_CLAIMS = "gross claims";
    private static final String CLAIM_HISTORY = "claim history";
    private static final String NET_BASE_CLAIMS = "net base claims";

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

    public PacketList<ContractFinancialsPacket> getOutContractFinancials() {
        return outContractFinancials;
    }

    public void setOutContractFinancials(PacketList<ContractFinancialsPacket> outContractFinancials) {
        this.outContractFinancials = outContractFinancials;
    }

    public PacketList<CommissionPacket> getOutCommission() {
        return outCommission;
    }

    public void setOutCommission(PacketList<CommissionPacket> outCommission) {
        this.outCommission = outCommission;
    }

    public PacketList<ClaimCashflowPacket> getOutClaimsGross() {
        return outClaimsGross;
    }

    public void setOutClaimsGross(PacketList<ClaimCashflowPacket> outClaimsGross) {
        this.outClaimsGross = outClaimsGross;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoGross() {
        return outUnderwritingInfoGross;
    }

    public void setOutUnderwritingInfoGross(PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross) {
        this.outUnderwritingInfoGross = outUnderwritingInfoGross;
    }

    private void initIsProportionalContract(Set<IReinsuranceContract> contracts) {
        if (isProportionalContract == null) {
            isProportionalContract = !contracts.isEmpty() && contracts.iterator().next() instanceof IPropReinsuranceContract;
        }
    }

    private void initHasMultipleContractsPerPeriod(Set<IReinsuranceContract> contracts) {
        if (hasMultipleContractsPerPeriod == null) {
            hasMultipleContractsPerPeriod = !contracts.isEmpty() && contracts.iterator().next() instanceof IMultipleContractsPerPeriod;
        }
    }

    public boolean isProportionalContract() {
        return isProportionalContract;
    }

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoGNPI() {
        return outUnderwritingInfoGNPI;
    }

    public void setOutUnderwritingInfoGNPI(PacketList<UnderwritingInfoPacket> outUnderwritingInfoGNPI) {
        this.outUnderwritingInfoGNPI = outUnderwritingInfoGNPI;
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
}
