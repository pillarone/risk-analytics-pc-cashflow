package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.parameterization.*;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.ReinsuranceContractAndBase;
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExclusionCoverStrategy extends AbstractParameterObject implements IExclusionCoverStrategy {

    private ComboBoxTableMultiDimensionalParameter grossClaims = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("Excluded Perils"), IPerilMarker.class);


    public Map getParameters() {
        Map params = new HashMap<String, Object>();
        params.put("grossClaims", grossClaims);
        return params;
    }

    public List<IPerilMarker> getCoveredPerils() {
        return (List<IPerilMarker>) grossClaims.getValuesAsObjects(0, true);
    }

    public IParameterObjectClassifier getType() {
        return ExclusionStrategyType.SELECTED;
    }

    @Override
    public void exclusionClaims(final List<ClaimCashflowPacket> source) {
        final List<ClaimCashflowPacket> filteredClaims = Lists.newArrayList();
        List coveredPerils = getCoveredPerils();
        if(coveredPerils.size() == 0) {
            return;
        }
        for (ClaimCashflowPacket claim : source) {
            if (!coveredPerils.contains(claim.peril())) {
                filteredClaims.add(claim);
            }
        }
        source.clear();
        source.addAll(filteredClaims);
    }
}

