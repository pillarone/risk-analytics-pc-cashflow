package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractsPerilsCoverAttributeStrategy extends AbstractContractsCoverAttributeStrategy
        implements ICoverAttributeStrategy, IContractCover {

    private ComboBoxTableMultiDimensionalParameter perils;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.CONTRACTSPERILS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("contracts", contracts);
        parameters.put("perils", perils);
        return parameters;
    }

    List<IPerilMarker> getCoveredPerils() {
        return (List<IPerilMarker>) perils.getValuesAsObjects(0, false);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredPerils = getCoveredPerils();
        List coveredContracts = getCoveredReinsuranceContracts();
        for (ClaimCashflowPacket claim : source) {
            if (coveredPerils.contains(claim.peril()) && coveredContracts.contains(claim.reinsuranceContract())) {
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
