package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractsCoverAttributeStrategy extends AbstractContractsCoverAttributeStrategy implements ICoverAttributeStrategy, IContractCover {

    private ConstrainedMultiDimensionalParameter contracts;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.CONTRACTS;
    }

    public Map getParameters() {
        Map<String, ConstrainedMultiDimensionalParameter> parameters = new HashMap<String, ConstrainedMultiDimensionalParameter>(1);
        parameters.put("contracts", contracts);
        return parameters;
    }

//    public List<IReinsuranceContractMarker> getCoveredReinsuranceContracts() {
//        return (List<IReinsuranceContractMarker>) contracts.getColumn(ContractBasedOn.CONTRACT_COLUMN_INDEX);
//    }

//    public List<ReinsuranceContractAndBase> getCoveredReinsuranceContractsAndBase() {
//        List<ReinsuranceContractAndBase> coveredContracts = new ArrayList<ReinsuranceContractAndBase>();
//        for (int row = contracts.getTitleRowCount(); row < contracts.getRowCount(); row++) {
//            coveredContracts.add(new ReinsuranceContractAndBase(
//                    (ReinsuranceContract) contracts.getValueAt(row, ContractBasedOn.CONTRACT_COLUMN_INDEX),
//                    (ContractBase) contracts.getValueAt(row, ContractBasedOn.BASED_ON_COLUMN_INDEX)));
//        }
//        return coveredContracts;
//    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredContracts = getCoveredReinsuranceContracts();
        for (ClaimCashflowPacket claim : source) {
            if (coveredContracts.contains(claim.reinsuranceContract())) {
                filteredClaims.add(claim);
            }
        }
        if (filteredClaims.size() == 0) {
            filteredClaims.add(new ClaimCashflowPacket());
            source.clear();
        }
        else {
            source.clear();
            source.addAll(filteredClaims);
        }
        return filteredClaims;
    }

    // todo(sku): think about deriving covered uw info indirectly
    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source) {
        source.clear();
        return null;
    }
}