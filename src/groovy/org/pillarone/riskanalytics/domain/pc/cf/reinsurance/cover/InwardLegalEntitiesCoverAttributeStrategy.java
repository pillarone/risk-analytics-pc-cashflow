package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class InwardLegalEntitiesCoverAttributeStrategy extends AbstractParameterObject
        implements ICoverAttributeStrategy, ILegalEntityCover {

    private ComboBoxTableMultiDimensionalParameter legalEntities;
    private LegalEntityCoverMode legalEntityCoverMode;

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.LEGALENTITIES;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("legalEntities", legalEntities);
        parameters.put("legalEntityCoverMode", legalEntityCoverMode);
        return parameters;
    }

    public List<ILegalEntityMarker> getCoveredLegalEntities() {
        return (List<ILegalEntityMarker>) legalEntities.getValuesAsObjects(0, true);
    }

    public List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List coveredLegalEntities = getCoveredLegalEntities();
        for (ClaimCashflowPacket claim : source) {
            if (coveredLegalEntities.contains(claim.legalEntity())) {
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

    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source) {
        List<UnderwritingInfoPacket> filteredUnderwritingInfo = new ArrayList<UnderwritingInfoPacket>();
        List coveredLegalEntities = getCoveredLegalEntities();
        for (UnderwritingInfoPacket uwInfo : source) {
            if (coveredLegalEntities.contains(uwInfo.legalEntity())) {
                filteredUnderwritingInfo.add(uwInfo);
            }
        }
        if (filteredUnderwritingInfo.size() == 0) {
            filteredUnderwritingInfo.add(new UnderwritingInfoPacket());
            source.clear();
        }
        else {
            source.clear();
            source.addAll(filteredUnderwritingInfo);
        }
        return filteredUnderwritingInfo;
    }

    /** this property has no effect within the covered methods but is used for the wiring */
    public LegalEntityCoverMode getLegalEntityCoverMode() {
        return legalEntityCoverMode;
    }
}
