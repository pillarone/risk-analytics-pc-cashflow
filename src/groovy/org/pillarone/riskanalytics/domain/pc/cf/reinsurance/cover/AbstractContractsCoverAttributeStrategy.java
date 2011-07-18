package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractContractsCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy, IContractCover {

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
                String contractName = (String) contracts.getValueAt(row, ContractBasedOn.CONTRACT_COLUMN_INDEX);
                ReinsuranceContract contract = reinsuranceContracts.get(contractName);
                String contractBase = (String) contracts.getValueAt(row, ContractBasedOn.BASED_ON_COLUMN_INDEX);
                coveredContractsAndBase.add(new ReinsuranceContractAndBase(contract, ContractBase.valueOf(contractBase)));
            }
        }
        return coveredContractsAndBase;
    }

}
