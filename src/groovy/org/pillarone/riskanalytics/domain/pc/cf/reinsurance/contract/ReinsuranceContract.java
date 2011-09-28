package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityDefaultPacket;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ContractFinancialsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.IPropReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.CommissionPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ICoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.IPeriodStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): apply recovery patterns
public class ReinsuranceContract extends Component implements IReinsuranceContractMarker {

    private static Log LOG = LogFactory.getLog(ReinsuranceContract.class);

    private IterationScope iterationScope;
    private PeriodStore periodStore;

    private PacketList<ClaimCashflowPacket> inClaims = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    /** PMO-1635: gets GNPI underwriting info always */
    private PacketList<UnderwritingInfoPacket> inUnderwritingInfo = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<LegalEntityDefaultPacket> inReinsurersDefault = new PacketList<LegalEntityDefaultPacket>(LegalEntityDefaultPacket.class);
    private PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket.class);
    private PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault.class);

    private PacketList<ClaimCashflowPacket> outClaimsGross = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsNet = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsCeded = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<ClaimCashflowPacket> outClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGross = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoNet = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoGNPI = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);
    private PacketList<CededUnderwritingInfoPacket> outUnderwritingInfoCeded
            = new PacketList<CededUnderwritingInfoPacket>(CededUnderwritingInfoPacket.class);
    private PacketList<ContractFinancialsPacket> outContractFinancials = new PacketList<ContractFinancialsPacket>(ContractFinancialsPacket.class);
    private PacketList<CommissionPacket> outCommission = new PacketList<CommissionPacket>(CommissionPacket.class);


    private ConstrainedMultiDimensionalParameter parmReinsurers = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), LegalEntityPortionConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER));
    private ICoverAttributeStrategy parmCover = CoverAttributeStrategyType.getDefault();
    private IPeriodStrategy parmCoveredPeriod = PeriodStrategyType.getDefault();
    private IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getDefault();

    private CounterPartyState counterPartyFactors;

    @Override
    protected void doCalculation() {
        // check cover
        initSimulation();
        initIteration();
        updateCounterPartyFactors();
        filterInChannels();
        updateContractParameters();
        Set<IReinsuranceContract> contracts = fillGrossClaims();
        initContracts(contracts);
        calculateCededClaims(iterationScope.getPeriodScope().getPeriodCounter());
        splitCededClaimsByCounterParty();
        processUnderwritingInfo();
        splitCededUnderwritingInfoByCounterParty();
        processUnderwritingInfoGNPI();
        discountClaims();
    }

    private void initSimulation() {
        if (firstIterationAndPeriod()) {
            parmCoveredPeriod.initStartCover(iterationScope.getPeriodScope().getCurrentPeriodStartDate());
            counterPartyFactors = new CounterPartyState();
        }
    }

    private boolean firstIterationAndPeriod() {
        return iterationScope.isFirstIteration() && iterationScope.getPeriodScope().isFirstPeriod();
    }

    private void initIteration() {
        if (iterationScope.getPeriodScope().isFirstPeriod()) {
            if (counterPartyFactors.newInitializationRequired() || firstIterationAndPeriod()) {
                DateTime validAsOf = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
                List<LegalEntity> counterParties = parmReinsurers.getValuesAsObjects(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX);
                for (int row = parmReinsurers.getTitleRowCount(); row < parmReinsurers.getRowCount(); row++) {
                    ILegalEntityMarker legalEntity = counterParties.get(row - 1);
                    double coveredPortion = (Double) parmReinsurers.getValueAt(row, LegalEntityPortionConstraints.PORTION_COLUMN_INDEX);
                    counterPartyFactors.addCounterPartyFactor(validAsOf, legalEntity, coveredPortion, true);
                }
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
     * filter according to covered period and occurrence date of claim
     */
    private void timeFilter() {
        DateTime noCoverAfter = counterPartyFactors.allCounterPartiesDefaultAfter();
        List<ClaimCashflowPacket> uncoveredClaims = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket grossClaim : inClaims) {
            if (!parmCoveredPeriod.isCovered(grossClaim.getOccurrenceDate())
                    || (noCoverAfter != null && grossClaim.getOccurrenceDate().isAfter(noCoverAfter))) {
                uncoveredClaims.add(grossClaim);
            }
        }
        inClaims.removeAll(uncoveredClaims);

        List<UnderwritingInfoPacket> uncoveredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
            if (!parmCoveredPeriod.isCovered(underwritingInfo.getExposure().getInceptionDate())
                    || (noCoverAfter != null && underwritingInfo.getExposure().getInceptionDate().isAfter(noCoverAfter))) {
                uncoveredUnderwritingInfo.add(underwritingInfo);
            }
        }
        inUnderwritingInfo.removeAll(uncoveredUnderwritingInfo);
    }


    private void updateCounterPartyFactors() {
        List<LegalEntity> counterParties = parmReinsurers.getValuesAsObjects(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX);
        for (LegalEntityDefault legalEntityDefault : inLegalEntityDefault) {
            if (counterParties.contains(legalEntityDefault.getLegalEntity())) {
                DateTime dateOfDefault = legalEntityDefault.getDateOfDefault();
                if (dateOfDefault != null) {
                    counterPartyFactors.addCounterPartyFactor(dateOfDefault, legalEntityDefault.getLegalEntity(), 0d, false);
                }
            }
        }
    }

    /**
     * filter according to covered claims generators, segments and companies (parmCover)
     */
    private void coverFilter() {
        parmCover.coveredClaims(inClaims);
        parmCover.coveredUnderwritingInfo(inUnderwritingInfo);
    }

    /**
     * add in every covered period a new contract to the periodStore
     */
    private void updateContractParameters() {
        if (isCurrentPeriodCovered()) {
            periodStore.put(REINSURANCE_CONTRACT, parmContractStrategy.getContract(inUnderwritingInfo));
        }
    }

    /**
     * Make sure a ClaimStorage object is created for every new CashflowClaimPacket and put in the first time slot of
     * the periodStore with key CLAIM_HISTORY. This object contains the incremental history for paid and reported.
     * Put a list of claims sorted by update date with an update in this period containing a reference to their history to the
     * current periodStore time slot using key GROSS_CLAIMS.
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
            claimsHistories = new HashMap<IClaimRoot, ClaimStorage>();
            periodStore.put(CLAIM_HISTORY, claimsHistories);
            for (ClaimCashflowPacket claim : inClaims) {
                ClaimStorage claimStorage = claimsHistories.get(claim.getKeyClaim());
                int occurrencePeriod = claim.occurrencePeriod(periodCounter);
                if (claimStorage == null) {
                    contracts.add(newClaimOccurredInCurrentPeriod(claim, occurrencePeriod, currentPeriod, claimsHistories,
                            currentPeriodGrossClaims));
                }
                else {
                    IReinsuranceContract contract = (IReinsuranceContract) periodStore.get(REINSURANCE_CONTRACT, occurrencePeriod - currentPeriod);
                    contracts.add(contract);
                    ClaimHistoryAndApplicableContract claimWithHistory = new ClaimHistoryAndApplicableContract(claim, claimStorage, contract);
                    currentPeriodGrossClaims.add(claimWithHistory);
                }
            }
        }
        else {
            for (ClaimCashflowPacket claim : inClaims) {
                int occurrencePeriod = claim.occurrencePeriod(periodCounter);
                ClaimStorage claimStorage = claimsHistories.get(claim.getKeyClaim());
                if (currentPeriod == occurrencePeriod && claimStorage == null) {
                    contracts.add(newClaimOccurredInCurrentPeriod(claim, occurrencePeriod, currentPeriod, claimsHistories,
                            currentPeriodGrossClaims));
                }
                else if (claimStorage != null)  {
                    IReinsuranceContract contract = (IReinsuranceContract) periodStore.get(REINSURANCE_CONTRACT, occurrencePeriod - currentPeriod);
                    contracts.add(contract);
                    ClaimHistoryAndApplicableContract claimWithHistory = new ClaimHistoryAndApplicableContract(claim, claimStorage, contract);
                    currentPeriodGrossClaims.add(claimWithHistory);
                }
                else {
                    LOG.error("claimStorage is null");
                }
            }
        }
        Collections.sort(currentPeriodGrossClaims, SortClaimHistoryAndApplicableContract.getInstance());
        periodStore.put(GROSS_CLAIMS, currentPeriodGrossClaims);
        return contracts;
    }

    private IReinsuranceContract newClaimOccurredInCurrentPeriod(ClaimCashflowPacket claim, int occurrencePeriod,
                            int currentPeriod, Map<IClaimRoot, ClaimStorage> claimsHistories,
                            List<ClaimHistoryAndApplicableContract> currentPeriodGrossClaims) {
        IReinsuranceContract contract = (IReinsuranceContract) periodStore.get(REINSURANCE_CONTRACT, occurrencePeriod - currentPeriod);
        ClaimStorage claimStorage = new ClaimStorage(claim);
        claimsHistories.put(claim.getKeyClaim(), claimStorage);
        ClaimHistoryAndApplicableContract claimWithHistory = new ClaimHistoryAndApplicableContract(claim, claimStorage, contract);
        currentPeriodGrossClaims.add(claimWithHistory);
        return contract;
    }

    private void initContracts(Set<IReinsuranceContract> contracts) {
        for (IReinsuranceContract contract : contracts) {
            contract.initPeriod(inFactors);
            // todo(sku): the following lines are required only if initPeriodClaims() has a non trivial implementation for this contract, avoid!
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
        for (ClaimHistoryAndApplicableContract grossClaim : currentPeriodGrossClaims) {
            ClaimCashflowPacket cededClaim = grossClaim.getCededClaim(periodCounter);
            double coveredByReinsurers = counterPartyFactors.getCoveredByReinsurers(grossClaim.getUpdateDate());
            if (coveredByReinsurers != 1) {
                cededClaim = ClaimUtils.scale(cededClaim, coveredByReinsurers);
            }
            ClaimUtils.applyMarkers(grossClaim.getGrossClaim(), cededClaim);
            cededClaim.setMarker(this);
            outClaimsCeded.add(cededClaim);
            if (isSenderWired(outClaimsGross)) {
                outClaimsGross.add(grossClaim.getGrossClaim());
            }
            if (isSenderWired(outClaimsNet)) {
                ClaimCashflowPacket netClaim = ClaimUtils.getNetClaim(grossClaim.getGrossClaim(), cededClaim);
                outClaimsNet.add(netClaim);
            }
        }
    }

    private void splitCededClaimsByCounterParty() {
        if (isSenderWired(outClaimsInward)) {
            for (ClaimCashflowPacket cededClaim : outClaimsCeded) {
                if (ClaimUtils.notTrivialValues(cededClaim)) {
                    for (Map.Entry<ILegalEntityMarker, Double> legalEntityAndFactor : counterPartyFactors.getFactors(cededClaim.getUpdateDate()).entrySet()) {
                        ClaimCashflowPacket counterPartyCededClaim = ClaimUtils.scale(cededClaim, -legalEntityAndFactor.getValue());
                        counterPartyCededClaim.setMarker(legalEntityAndFactor.getKey());
                        outClaimsInward.add(counterPartyCededClaim);
                    }
                }
            }
        }
    }

    private void processUnderwritingInfo() {
        int currentPeriod = iterationScope.getPeriodScope().getCurrentPeriod();
        // map underwriting info to corresponding contracts
        Set<IReinsuranceContract> contracts = new HashSet<IReinsuranceContract>();
        if (!inUnderwritingInfo.isEmpty()) {
            for (UnderwritingInfoPacket underwritingInfo : inUnderwritingInfo) {
                int inceptionPeriod = underwritingInfo.getExposure().getInceptionPeriod();
                IReinsuranceContract contract = (IReinsuranceContract) periodStore.get(REINSURANCE_CONTRACT, inceptionPeriod - currentPeriod);
                contracts.add(contract);
                contract.add(underwritingInfo);
            }
        }
        else {
            for (int period = 0; period <= currentPeriod; period++) {
                IReinsuranceContract contract = (IReinsuranceContract) periodStore.get(REINSURANCE_CONTRACT, period - currentPeriod);
                if (contract != null) {
                    contracts.add(contract);
                }
            }
        }
        if (isSenderWired(outUnderwritingInfoGross)) {
            outUnderwritingInfoGross.addAll(inUnderwritingInfo);
        }
        for (IReinsuranceContract contract : contracts) {
            // todo(sku): how time consuming are isSenderWired() calls? Might be necessary to cache this information.
            double coveredByReinsurers = counterPartyFactors.getCoveredByReinsurers(iterationScope.getPeriodScope().getCurrentPeriodStartDate());
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

    private void splitCededUnderwritingInfoByCounterParty() {
        if (isSenderWired(outUnderwritingInfoInward)) {
            for (UnderwritingInfoPacket cededUnderwritingInfo : outUnderwritingInfoCeded) {
                for (Map.Entry<ILegalEntityMarker, Double> legalEntityAndFactor : counterPartyFactors.getFactors(cededUnderwritingInfo.getDate()).entrySet()) {
                    UnderwritingInfoPacket counterPartyCededUnderwritingInfo = cededUnderwritingInfo.withFactorsApplied(1, -legalEntityAndFactor.getValue());
                    counterPartyCededUnderwritingInfo.setMarker(legalEntityAndFactor.getKey());
                    outUnderwritingInfoInward.add(counterPartyCededUnderwritingInfo);
                }
            }
        }
    }

    private void processUnderwritingInfoGNPI() {
        if (isSenderWired(outUnderwritingInfoGNPI)) {
            calculateUnderwritingInfoGNPI(inUnderwritingInfo);
        }
    }

    private void calculateUnderwritingInfoGNPI(List<UnderwritingInfoPacket> baseUnderwritingInfos) {
        if (isProportionalContract()) {
            if (baseUnderwritingInfos.size() == outUnderwritingInfoCeded.size()) {
                for (int i = 0; i < baseUnderwritingInfos.size(); i++) {
                    if (baseUnderwritingInfos.get(i).getOriginal().equals(outUnderwritingInfoCeded.get(i).getOriginal())) {
                        outUnderwritingInfoGNPI.add(baseUnderwritingInfos.get(i).getNet(outUnderwritingInfoCeded.get(i), true));
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
            outUnderwritingInfoGNPI.addAll(baseUnderwritingInfos);
        }
    }


    private void discountClaims() {
        // use utility method as discounting is required in several places
    }

    private boolean isCurrentPeriodCovered() {
        DateTime periodStart = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
        DateTime periodEnd = iterationScope.getPeriodScope().getNextPeriodStartDate();
        return parmCoveredPeriod.isCovered(periodStart) || parmCoveredPeriod.isCovered(periodEnd);
    }

// periodStore keys
    private static final String REINSURANCE_CONTRACT = "reinsurance contract";
    private static final String GROSS_CLAIMS = "gross claims";
    private static final String CLAIM_HISTORY = "claim history";

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

    public PacketList<LegalEntityDefaultPacket> getInReinsurersDefault() {
        return inReinsurersDefault;
    }

    public void setInReinsurersDefault(PacketList<LegalEntityDefaultPacket> inReinsurersDefault) {
        this.inReinsurersDefault = inReinsurersDefault;
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

    public ConstrainedMultiDimensionalParameter getParmReinsurers() {
        return parmReinsurers;
    }

    public void setParmReinsurers(ConstrainedMultiDimensionalParameter parmReinsurers) {
        this.parmReinsurers = parmReinsurers;
    }

    public ICoverAttributeStrategy getParmCover() {
        return parmCover;
    }

    public void setParmCover(ICoverAttributeStrategy parmCover) {
        this.parmCover = parmCover;
    }

    /**
     * Defines the kind of contract and parametrization
     */
    public IReinsuranceContractStrategy getParmContractStrategy() {
        return parmContractStrategy;
    }

    public void setParmContractStrategy(IReinsuranceContractStrategy parmContractStrategy) {
        this.parmContractStrategy = parmContractStrategy;
    }

    public IPeriodStrategy getParmCoveredPeriod() {
        return parmCoveredPeriod;
    }

    public void setParmCoveredPeriod(IPeriodStrategy parmCoveredPeriod) {
        this.parmCoveredPeriod = parmCoveredPeriod;
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

    public boolean isProportionalContract() {
        return parmContractStrategy instanceof IPropReinsuranceContract;
    }

    public PacketList<FactorsPacket> getInFactors() {
        return inFactors;
    }

    public void setInFactors(PacketList<FactorsPacket> inFactors) {
        this.inFactors = inFactors;
    }

    /** contains inward claims per counterparty */
    public PacketList<ClaimCashflowPacket> getOutClaimsInward() {
        return outClaimsInward;
    }

    public void setOutClaimsInward(PacketList<ClaimCashflowPacket> outClaimsInward) {
        this.outClaimsInward = outClaimsInward;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoInward() {
        return outUnderwritingInfoInward;
    }

    public void setOutUnderwritingInfoInward(PacketList<UnderwritingInfoPacket> outUnderwritingInfoInward) {
        this.outUnderwritingInfoInward = outUnderwritingInfoInward;
    }

    public PacketList<LegalEntityDefault> getInLegalEntityDefault() {
        return inLegalEntityDefault;
    }

    public void setInLegalEntityDefault(PacketList<LegalEntityDefault> inLegalEntityDefault) {
        this.inLegalEntityDefault = inLegalEntityDefault;
    }

    public PacketList<UnderwritingInfoPacket> getOutUnderwritingInfoGNPI() {
        return outUnderwritingInfoGNPI;
    }

    public void setOutUnderwritingInfoGNPI(PacketList<UnderwritingInfoPacket> outUnderwritingInfoGNPI) {
        this.outUnderwritingInfoGNPI = outUnderwritingInfoGNPI;
    }
}
