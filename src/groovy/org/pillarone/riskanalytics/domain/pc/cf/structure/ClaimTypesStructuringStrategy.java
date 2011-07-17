package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class ClaimTypesStructuringStrategy extends AbstractParameterObject implements IStructuringStrategy {

    private ConstrainedMultiDimensionalParameter claimTypes;

    public IParameterObjectClassifier getType() {
        return StructuringType.CLAIMTYPES;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("claimTypes", claimTypes);
        return parameters;
    }

    public List<ClaimCashflowPacket> filterClaims(List<ClaimCashflowPacket> claims){
       List<ClaimCashflowPacket> filteredClaims = new ArrayList<ClaimCashflowPacket>();
        List filteredClaimTypes = claimTypes.getValuesAsObjects(0);
        for (ClaimCashflowPacket claim : claims){
            if (filteredClaimTypes.contains(claim.getBaseClaim().getClaimType().name())){
                filteredClaims.add(claim);
           }
        }
        if (filteredClaims.size() == 0){
            filteredClaims.add(new ClaimCashflowPacket());
        }
        return filteredClaims;
   }

    public List<UnderwritingInfoPacket> filterUnderwritingInfos(List<UnderwritingInfoPacket> underwritingInfos){
       return new ArrayList<UnderwritingInfoPacket>();
   }
}
