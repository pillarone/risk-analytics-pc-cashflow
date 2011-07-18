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

    public IParameterObjectClassifier getType() {
        return CoverAttributeStrategyType.INWARDLEGALENTITIES;
    }

    public Map getParameters() {
        Map<String, ComboBoxTableMultiDimensionalParameter> parameters = new HashMap<String, ComboBoxTableMultiDimensionalParameter>(1);
        parameters.put("legalEntities", legalEntities);
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

    // todo(sku): think about deriving covered uw info indirectly
    public List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source) {
        source.clear();
        return null;
    }
}
