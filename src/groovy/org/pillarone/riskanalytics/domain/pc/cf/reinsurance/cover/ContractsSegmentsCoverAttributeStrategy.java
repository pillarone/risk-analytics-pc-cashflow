package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ContractsSegmentsCoverAttributeStrategy extends AbstractContractsCoverAttributeStrategy
        implements ICoverAttributeStrategy, IContractCover {

    private ComboBoxTableMultiDimensionalParameter segments;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.CONTRACTSSEGMENTS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("contracts", contracts);
        parameters.put("segments", segments);
        return parameters;
    }

    List<ISegmentMarker> getCoveredSegments() {
        return (List<ISegmentMarker>) segments.getValuesAsObjects(0, false);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredSegments = getCoveredSegments();
        List coveredContracts = getCoveredReinsuranceContracts();
        for (ClaimCashflowPacket claim : source) {
            if (coveredSegments.contains(claim.segment()) && coveredContracts.contains(claim.reinsuranceContract())) {
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
