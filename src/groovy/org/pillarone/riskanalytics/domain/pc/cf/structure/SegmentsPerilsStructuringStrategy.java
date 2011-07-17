package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class SegmentsPerilsStructuringStrategy extends AbstractParameterObject implements IStructuringStrategy {

    private ComboBoxTableMultiDimensionalParameter segments;
    private ComboBoxTableMultiDimensionalParameter perils;
    private LogicArguments connection;

    public IParameterObjectClassifier getType() {
        return StructuringType.SEGMENTSPERILS;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("segments", segments);
        parameters.put("perils", perils);
        parameters.put("connection", connection);
        return parameters;
    }

    public static final String SEGMENT = "Segment";
    public static final String PERIL = "Peril";

    public List<ClaimCashflowPacket> filterClaims(List<ClaimCashflowPacket> claims) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List segmentsFilterCriteria = ((List) segments.getValuesAsObjects(0, true));
        List perilsFilterCriteria = ((List) perils.getValuesAsObjects(0, true));
        switch (connection) {
            case AND:
                for (ClaimCashflowPacket claim : claims) {
                    if (segmentsFilterCriteria.contains(claim.segment()) && perilsFilterCriteria.contains(claim.peril())) {
                        filteredClaims.add(claim);
                    }
                }
                break;
            case OR:
                for (ClaimCashflowPacket claim : claims) {
                    if (segmentsFilterCriteria.contains(claim.segment()) || perilsFilterCriteria.contains(claim.peril())) {
                        filteredClaims.add(claim);
                    }
                }
                break;

        }
        if (filteredClaims.size() == 0) {
            filteredClaims.add(new ClaimCashflowPacket());
        }
        return filteredClaims;
    }

    public List<UnderwritingInfoPacket> filterUnderwritingInfos(List<UnderwritingInfoPacket> underwritingInfos) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        List segmentsFilterCriteria = ((List) segments.getValuesAsObjects(0, true));
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            if (segmentsFilterCriteria.contains(underwritingInfo.segment())) {
                filteredUnderwritingInfo.add(underwritingInfo);
            }
        }
        if (filteredUnderwritingInfo.size() == 0 && !(connection.equals(LogicArguments.OR) && segments.getValuesAsObjects().size() == 0)) {
            UnderwritingInfoPacket trivialPacket = new UnderwritingInfoPacket();
            trivialPacket.setPremiumWritten(0d);
            filteredUnderwritingInfo.add(trivialPacket);
        }
        return filteredUnderwritingInfo;
    }
}
