package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PerilsFilterStrategy extends AbstractParameterObject implements ICoverAttributeStrategy {

    private ComboBoxTableMultiDimensionalParameter perils;

    public IParameterObjectClassifier getType() {
        return FilterStrategyType.PERILS;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("perils", perils);
        return parameters;
    }

    List<IPerilMarker> getCoveredPerils() {
        return (List<IPerilMarker>) perils.getValuesAsObjects(0, true);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredPerils = getCoveredPerils();
        for (ClaimCashflowPacket claim : source) {
            if (coveredPerils.contains(claim.peril())) {
                filteredClaims.add(ClaimValidator.positiveNominalUltimate(claim));
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
