package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilsSegmentsFilterStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter perils;
    private ComboBoxTableMultiDimensionalParameter segments;
    private LogicArguments connection;

    public IParameterObjectClassifier getType() {
        return FilterStrategyType.PERILSSEGMENTS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("perils", perils);
        parameters.put("segments", segments);
        parameters.put("connection", connection);
        return parameters;
    }

    List<IPerilMarker> getCoveredPerils() {
        return (List<IPerilMarker>) perils.getValuesAsObjects(0, false);
    }

    List<ISegmentMarker> getCoveredSegments() {
        return (List<ISegmentMarker>) segments.getValuesAsObjects(0, false);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredPerils = getCoveredPerils();
        List coveredSegments = getCoveredSegments();
        for (ClaimCashflowPacket claim : source) {
            if ((connection == LogicArguments.AND
                    && coveredPerils.contains(claim.peril()) && coveredSegments.contains(claim.segment()))
                || (connection == LogicArguments.OR
                    && coveredPerils.contains(claim.peril()) || coveredSegments.contains(claim.segment()))) {
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

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims) {
        List<UnderwritingInfoPacket> filteredUwInfo = new ArrayList<UnderwritingInfoPacket>();
        Set<ISegmentMarker> coveredSegments = new HashSet<ISegmentMarker>(getCoveredSegments());
        for (ClaimCashflowPacket claim : coveredGrossClaims) {
            coveredSegments.add(claim.segment());
        }
        for (UnderwritingInfoPacket uwInfo : source) {
            if (coveredSegments.contains(uwInfo.segment())) {
                filteredUwInfo.add(uwInfo);
            }
        }
        if (filteredUwInfo.size() == 0) {
            filteredUwInfo.add(new UnderwritingInfoPacket());
            source.clear();
        }
        else {
            source.clear();
            source.addAll(filteredUwInfo);
        }
        return filteredUwInfo;
    }
}
