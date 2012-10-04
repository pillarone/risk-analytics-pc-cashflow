package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import org.pillarone.riskanalytics.core.parameterization.*;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.StatelessRIContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts.TermReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ReinsuranceContractAndBase;
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class SelectedCoverStrategy extends AbstractParameterObject implements ICoverStrategy {

    private ComboBoxTableMultiDimensionalParameter grossClaims = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("Covered Perils"), IPerilMarker.class);
    private ConstrainedMultiDimensionalParameter structures = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[],[]]"),
            Arrays.asList(ContractBasedOn.CONTRACT, ContractBasedOn.BASED_ON),
            ConstraintsFactory.getConstraints(ContractBasedOn.IDENTIFIER));

    public IParameterObjectClassifier getType() {
        return CoverStrategyType.SELECTED;
    }

    public Map getParameters() {
        Map params = new HashMap<String, Object>();
        params.put("grossClaims", grossClaims);
        params.put("structures", structures);
        return params;
    }

    private List<IPerilMarker> getCoveredPerils() {
        return (List<IPerilMarker>) grossClaims.getValuesAsObjects(0, true);
    }

    private List<ReinsuranceContractAndBase> coveredContractsAndBase;
    private List<IReinsuranceContractMarker> coveredContracts;
    private List<IReinsuranceContractMarker> coveredContractsCoveringCeded;
    private ClaimValidator claimValidator = new ClaimValidator();

    private void lazyInitCoveredContracts() {
        if (coveredContracts == null) {
            coveredContracts = new ArrayList<IReinsuranceContractMarker>();
            coveredContractsCoveringCeded = new ArrayList<IReinsuranceContractMarker>();
            if (contractBasedCover()) {
                for (ReinsuranceContractAndBase contract : coveredContractsAndBase) {
                    coveredContracts.add(contract.reinsuranceContract);
                    if (contract.contractBase.equals(ReinsuranceContractBase.CEDED)) {
                        coveredContractsCoveringCeded.add(contract.reinsuranceContract);
                    }
                }
            }
        }
    }

    public List<ReinsuranceContractAndBase> getCoveredReinsuranceContractsAndBase() {
        if (coveredContractsAndBase == null) {
            coveredContractsAndBase = new ArrayList<ReinsuranceContractAndBase>();
            for (int row = structures.getTitleRowCount(); row < structures.getRowCount(); row++) {
                IReinsuranceContractMarker contract = (IReinsuranceContractMarker) structures.getValueAtAsObject(row, ReinsuranceContractBasedOn.CONTRACT_COLUMN_INDEX);
                String contractBase = (String) structures.getValueAt(row, ReinsuranceContractBasedOn.BASED_ON_COLUMN_INDEX);
                coveredContractsAndBase.add(new ReinsuranceContractAndBase(contract, ReinsuranceContractBase.valueOf(contractBase)));
            }
        }
        return Collections.unmodifiableList(coveredContractsAndBase);
    }

    public void coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredPerils = getCoveredPerils();
//        This call to no cover initialises the coveredContracts field... crucial to avoid a null pointer later, and not entirely obvious.
        if(noCover()) {
            return;
        }
        for (ClaimCashflowPacket claim : source) {
            if (perilBasedCover() && contractBasedCover()) {
                if (coveredContracts.contains(claim.reinsuranceContract()) && claim.reserve() == null
                        && coveredPerils.contains(claim.peril())) {
                    if (coveredContractsCoveringCeded.contains(claim.reinsuranceContract())) {
                        filteredClaims.add(claimValidator.invertClaimSign(claim));
                    }
                    else {
                        filteredClaims.add(ClaimValidator.positiveNominalUltimate(claim));
                    }
                }
            }
            else if (perilBasedCover()) {
                if (coveredPerils.contains(claim.peril())) {
                    filteredClaims.add(ClaimValidator.positiveNominalUltimate(claim));
                }
            }
            else if (contractBasedCover()) {
                if (coveredContracts.contains(claim.reinsuranceContract()) && claim.reserve() == null) {
                    if (coveredContractsCoveringCeded.contains(claim.reinsuranceContract())) {
                        filteredClaims.add(claimValidator.invertClaimSign(claim));
                    }
                    else {
                        filteredClaims.add(ClaimValidator.positiveNominalUltimate(claim));
                    }
                }
            }
            else {
                // nothing to be filtered
            }
        }
        source.clear();
        if (!filteredClaims.isEmpty()) {
            source.addAll(filteredClaims);
        }
    }

    public boolean noCover() {
        lazyInitCoveredContracts();
        return coveredContracts.isEmpty() && getCoveredPerils().isEmpty();
    }

    public boolean contractBasedCover() {
        return structures.getRowCount() - structures.getTitleRowCount() > 0;
    }

    public boolean perilBasedCover() {
        return grossClaims.getRowCount() - grossClaims.getTitleRowCount() > 0;
    }
}

