package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
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
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class SegmentsStructuringStrategy extends AbstractParameterObject implements IStructuringStrategy {

    private ComboBoxTableMultiDimensionalParameter segments;

    public IParameterObjectClassifier getType() {
        return StructuringType.SEGMENTS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("segments", segments);
        return parameters;
    }

    public static final String SEGMENT = "Segment";

    public List<ClaimCashflowPacket> filterClaims(List<ClaimCashflowPacket> claims) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List filterCriteria = (List) segments.getValuesAsObjects(0, true);
        for (ClaimCashflowPacket claim : claims) {
            if (filterCriteria.contains(claim.segment())) {
                filteredClaims.add(claim);
            }
        }
        if (filteredClaims.size() == 0) {
            filteredClaims.add(new ClaimCashflowPacket());
        }
        return filteredClaims;
    }

    public List<UnderwritingInfoPacket> filterUnderwritingInfos(List<UnderwritingInfoPacket> underwritingInfos) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        List filterCriteria = (List) segments.getValuesAsObjects().get(0);
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            if (filterCriteria.contains(underwritingInfo.segment())) {
                filteredUnderwritingInfo.add(underwritingInfo);
            }
        }
        if (filteredUnderwritingInfo.size()==0){
            UnderwritingInfoPacket trivialPacket = new UnderwritingInfoPacket();
            trivialPacket.setPremiumWritten(0d);
            filteredUnderwritingInfo.add(trivialPacket);
        }
        return filteredUnderwritingInfo;
    }
}
