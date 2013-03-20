package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimUtils;
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.LegalEntityDefault;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity;
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ICoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixCoverAttributeStrategy;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adds multi counterparty functionality and a cover strategy
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public abstract class MultiCounterPartyBaseReinsuranceContract extends BaseReinsuranceContract implements IReinsuranceContractMarker {

    private static Log LOG = LogFactory.getLog(MultiCounterPartyBaseReinsuranceContract.class);

    private PacketList<LegalEntityDefault> inLegalEntityDefault = new PacketList<LegalEntityDefault>(LegalEntityDefault.class);
    private PacketList<ClaimCashflowPacket> outClaimsInward = new PacketList<ClaimCashflowPacket>(ClaimCashflowPacket.class);
    private PacketList<UnderwritingInfoPacket> outUnderwritingInfoInward = new PacketList<UnderwritingInfoPacket>(UnderwritingInfoPacket.class);

    private ConstrainedMultiDimensionalParameter parmReinsurers = new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), LegalEntityPortionConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(LegalEntityPortionConstraints.IDENTIFIER));
    private ICoverAttributeStrategy parmCover = CoverAttributeStrategyType.getDefault();


    /**
     * This object is filled with the initial counter party factors according to parmReinsurers
     */
    protected CounterPartyState counterPartyFactorsInit;
    /**
     * Contains the covered ratio per counter party and date for a whole iteration. Before every iteration it is re-filled
     * according to counterPartyFactorsInit. Whenever a default occurs, the factor of that specific counter party and
     * the overall factor have to be adjusted (updateCounterPartyFactors()).
     */
    protected CounterPartyState counterPartyFactors;


    @Override
    protected void doCalculation() {
        super.doCalculation();
        splitCededClaimsByCounterParty();
        splitCededUnderwritingInfoByCounterParty();
    }

    /**
     * initialize counterPartyFactorsInit
     */
    @Override
    protected void initSimulation() {
        if (firstIterationAndPeriod()) {
            counterPartyFactorsInit = new CounterPartyState();
            DateTime validAsOf = iterationScope.getPeriodScope().getCurrentPeriodStartDate();
            List<LegalEntity> counterParties = parmReinsurers.getValuesAsObjects(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX);
            for (int row = parmReinsurers.getTitleRowCount(); row < parmReinsurers.getRowCount(); row++) {
                ILegalEntityMarker legalEntity = counterParties.get(row - 1);
                double coveredPortion = (Double) parmReinsurers.getValueAt(row, LegalEntityPortionConstraints.PORTION_COLUMN_INDEX);
                counterPartyFactorsInit.addCounterPartyFactor(validAsOf, legalEntity, coveredPortion, true);
            }
        }
    }

    /**
     * reset counterPartyFactors
     */
    @Override
    protected void initIteration() {
        if (iterationScope.getPeriodScope().isFirstPeriod()) {
            counterPartyFactors = new CounterPartyState(counterPartyFactorsInit);
        }
    }

    @Override
    protected void initPeriod(Set<IReinsuranceContract> contracts) {
        updateCounterPartyFactors();
        super.initPeriod(contracts);
    }

    private void updateCounterPartyFactors() {
        List<LegalEntity> counterParties = parmReinsurers.getValuesAsObjects(LegalEntityPortionConstraints.COMPANY_COLUMN_INDEX);
        for (LegalEntityDefault legalEntityDefault : inLegalEntityDefault) {
            if (counterParties.contains(legalEntityDefault.getLegalEntity())) {
                DateTime dateOfDefault = legalEntityDefault.getDateOfDefault();
                if (dateOfDefault != null) {
                    counterPartyFactors.addCounterPartyFactor(dateOfDefault, legalEntityDefault.getLegalEntity(),
                            legalEntityDefault.getFirstInstantRecovery(), false);
                }
            }
        }
    }

    /**
     * filter according to covered claims generators, segments and companies (parmCover)
     */
    protected void coverFilter() {
        if (!(parmCover instanceof MatrixCoverAttributeStrategy && ((MatrixCoverAttributeStrategy) parmCover).mergerRequired())) {
            parmCover.coveredClaims(inClaims);
            parmCover.coveredUnderwritingInfo(inUnderwritingInfo, inClaims);
        }
    }

    @Override
    protected double coveredByReinsurers(DateTime updateDate) {
        return counterPartyFactors.getCoveredByReinsurers(updateDate);
    }

    /**
     * This method fills the outClaimsInward channel if it is wired.
     * Whereas the outClaimsCeded channel contains the total ceded claim, the outClaimsInward channel needs the ceded
     * claim splitted up by counter party by applying the factors provided by counterPartyFactors. The sign is reverted.
     */
    private void splitCededClaimsByCounterParty() {
        if (isSenderWired(outClaimsInward)) {
            for (ClaimCashflowPacket cededClaim : outClaimsCeded) {
                if (ClaimUtils.notTrivialValues(cededClaim)) {
                    for (Map.Entry<ILegalEntityMarker, Double> legalEntityAndFactor : counterPartyFactors.getFactors(cededClaim.getUpdateDate()).entrySet()) {
                        ClaimCashflowPacket counterPartyCededClaim = ClaimUtils.scale(cededClaim, -legalEntityAndFactor.getValue(), true, true);
                        counterPartyCededClaim.setMarker(legalEntityAndFactor.getKey());
                        outClaimsInward.add(counterPartyCededClaim);
                    }
                }
            }
        }
    }

    /**
     * This method fills the outUnderwritingInfoInward channel if it is wired.
     * Whereas the outUnderwritingInfoInward channel contains the total ceded claim, the outUnderwritingInfoCeded channel
     * needs the ceded claim splitted up by counter party by applying the factors provided by counterPartyFactors. The
     * sign is reverted.
     */
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

    public PacketList<LegalEntityDefault> getInLegalEntityDefault() {
        return inLegalEntityDefault;
    }

    public void setInLegalEntityDefault(PacketList<LegalEntityDefault> inLegalEntityDefault) {
        this.inLegalEntityDefault = inLegalEntityDefault;
    }

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
}
