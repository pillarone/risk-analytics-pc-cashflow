package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class RelativeBaseStrategy extends AbstractParameterObject implements IExposureBaseStrategy {

    protected ComboBoxTableMultiDimensionalParameter underwritingInfo;

    public Map getParameters() {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("underwritingInfo", underwritingInfo);
        return map;
    }

    public List<IUnderwritingInfoMarker> filteredUnderwritingSegments() {
        return (List<IUnderwritingInfoMarker>) underwritingInfo.getValuesAsObjects(0, true);
    }

    public double factor(PacketList<UnderwritingInfoPacket> underwritingInfos) {
        return UnderwritingInfoUtils.scalingFactor(underwritingInfos, exposureBase(), filteredUnderwritingSegments());
    }
}
