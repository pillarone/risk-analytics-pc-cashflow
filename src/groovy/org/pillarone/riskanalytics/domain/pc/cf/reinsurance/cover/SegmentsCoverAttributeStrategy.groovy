package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class SegmentsCoverAttributeStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {
    private ComboBoxTableMultiDimensionalParameter segments;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.SEGMENTS;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("segments", segments);
        return parameters;
    }

    List<ISegmentMarker> getCoveredSegments() {
        return (List<ISegmentMarker>) segments.getValuesAsObjects();
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredSegments = getCoveredSegments();
        for (ClaimCashflowPacket claim : source) {
            if (coveredSegments.contains(claim.segment())) {
                filteredClaims.add(claim);
            }
        }
        source.clear();
        if (filteredClaims.size() == 0) {
            filteredClaims.add(new ClaimCashflowPacket());
        }
        else {
            source.addAll(filteredClaims);
        }
        return filteredClaims;
    }

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        List coveredSegments = getCoveredSegments();
        for (UnderwritingInfoPacket underwritingInfo: source) {
            if (coveredSegments.contains(underwritingInfo.segment())) {
                filteredUnderwritingInfo.add(underwritingInfo);
            }
        }
        source.clear();
        if (filteredUnderwritingInfo.size() == 0) {
            filteredUnderwritingInfo.add(new UnderwritingInfoPacket());
        }
        else {
            source.addAll(filteredUnderwritingInfo);
        }
        return filteredUnderwritingInfo;
    }
}