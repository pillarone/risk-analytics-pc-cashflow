package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractsCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy, IContractCover {

    private ICoverAttributeStrategy filter;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.CONTRACTS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("contracts", contracts);
        parameters.put("filter", filter);
        return parameters;
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredContracts = getCoveredReinsuranceContracts();
        for (ClaimCashflowPacket claim : source) {
            if (coveredContracts.contains(claim.reinsuranceContract())) {
                filteredClaims.add(claim);
            }
        }
        filteredClaims = filter.coveredClaims(filteredClaims);
        source.clear();
        if (!filteredClaims.isEmpty()) {
            source.addAll(filteredClaims);
        }
        return filteredClaims;
    }

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        List coveredContracts = getCoveredReinsuranceContracts();
        for (UnderwritingInfoPacket uwInfo : source) {
            if (coveredContracts.contains(uwInfo.reinsuranceContract())) {
                filteredUnderwritingInfo.add(uwInfo);
            }
        }
        filteredUnderwritingInfo = filter.coveredUnderwritingInfo(filteredUnderwritingInfo, coveredGrossClaims);
        source.clear();
        if (!filteredUnderwritingInfo.isEmpty()) {
            source.addAll(filteredUnderwritingInfo);
        }
        return filteredUnderwritingInfo;
    }

    protected ConstrainedMultiDimensionalParameter contracts;

    private List<ReinsuranceContractAndBase> coveredContractsAndBase;
    private List<IReinsuranceContractMarker> coveredContracts;

    public List<IReinsuranceContractMarker> getCoveredReinsuranceContracts() {
        if (coveredContracts == null) {
            coveredContracts = new ArrayList<IReinsuranceContractMarker>();
            for (ReinsuranceContractAndBase contract : coveredContractsAndBase) {
                coveredContracts.add(contract.reinsuranceContract);
            }
        }
        return coveredContracts;
    }

    public List<ReinsuranceContractAndBase> getCoveredReinsuranceContractsAndBase(Map<String, ReinsuranceContract> reinsuranceContracts) {
        if (coveredContractsAndBase == null) {
            coveredContractsAndBase = new ArrayList<ReinsuranceContractAndBase>();
            for (int row = contracts.getTitleRowCount(); row < contracts.getRowCount(); row++) {
                String contractName = (String) contracts.getValueAt(row, ReinsuranceContractBasedOn.CONTRACT_COLUMN_INDEX);
                ReinsuranceContract contract = reinsuranceContracts.get(contractName);
                String contractBase = (String) contracts.getValueAt(row, ReinsuranceContractBasedOn.BASED_ON_COLUMN_INDEX);
                coveredContractsAndBase.add(new ReinsuranceContractAndBase(contract, ReinsuranceContractBase.valueOf(contractBase)));
            }
        }
        return coveredContractsAndBase;
    }
}
