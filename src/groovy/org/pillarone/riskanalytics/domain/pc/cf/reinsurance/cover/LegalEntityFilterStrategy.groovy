package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class LegalEntityFilterStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {
    private ComboBoxTableMultiDimensionalParameter legalEntities;

    public IParameterObjectClassifier getType() {
        return RetrospectiveCoverAttributeStrategyType.LEGALENTITIES;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("legalEntities", legalEntities);
        return parameters;
    }

    List<ISegmentMarker> getCoveredLegalEntities() {
        return (List<ISegmentMarker>) legalEntities.getValuesAsObjects(0, true);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredReserves = getCoveredLegalEntities();
        for (ClaimCashflowPacket claim : source) {
            if (coveredReserves.contains(claim.legalEntity()) && claim.reserve() != null) {
                filteredClaims.add(claim);
            }
        }
        source.clear();
        if (!filteredClaims.isEmpty()) {
            source.addAll(filteredClaims);
        }
        return filteredClaims;
    }

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        Set<ISegmentMarker> coveredSegments = new HashSet<ISegmentMarker>();
        for (ClaimCashflowPacket claim : coveredGrossClaims) {
            coveredSegments.add(claim.segment());
        }
        for (UnderwritingInfoPacket underwritingInfo: source) {
            if (coveredSegments.contains(underwritingInfo.segment())) {
                filteredUnderwritingInfo.add(underwritingInfo);
            }
        }
        source.clear();
        if (!filteredUnderwritingInfo.isEmpty()) {
            source.addAll(filteredUnderwritingInfo);
        }
        return filteredUnderwritingInfo;
    }
}
