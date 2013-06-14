package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

import java.util.*;

public class ExclusionCoverStrategy extends AbstractParameterObject implements IExclusionCoverStrategy {

    private ComboBoxTableMultiDimensionalParameter grossClaims = new ComboBoxTableMultiDimensionalParameter(
            Collections.emptyList(), Arrays.asList("Excluded Perils"), IPerilMarker.class);


    public Map getParameters() {
        Map params = new HashMap<String, Object>();
        params.put("grossClaims", grossClaims);
        return params;
    }

    public List<IPerilMarker> getExcludedPerils() {
        return (List<IPerilMarker>) grossClaims.getValuesAsObjects(0, true);
    }

    public IParameterObjectClassifier getType() {
        return ExclusionStrategyType.SELECTED;
    }

    public void exclusionClaims(final List<ClaimCashflowPacket> source) {
        final List<ClaimCashflowPacket> filteredClaims = Lists.newArrayList();
        List<IPerilMarker> excludedPerils = getExcludedPerils();
        if(getExcludedPerils().size() == 0 ) {
            return;
        }
        for (ClaimCashflowPacket claim : source) {
            if (excludedPerils.contains(claim.peril())){
                continue;
            }
            filteredClaims.add(claim);
        }
        source.clear();
        source.addAll(filteredClaims);
    }

    public ComboBoxTableMultiDimensionalParameter getGrossClaims() {
        return grossClaims;
    }

    public void setGrossClaims(final ComboBoxTableMultiDimensionalParameter grossClaims) {
        this.grossClaims = grossClaims;
    }
}

