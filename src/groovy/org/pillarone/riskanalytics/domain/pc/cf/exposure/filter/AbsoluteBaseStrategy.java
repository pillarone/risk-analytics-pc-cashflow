package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter;

import com.google.common.collect.Lists;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.AllPeriodUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AbsoluteBaseStrategy extends AbstractParameterObject implements IExposureBaseStrategy {

    public double factor(PacketList<UnderwritingInfoPacket> underwritingInfos) {
        return 1;
    }

    public List filteredUnderwritingSegments() {
        return new ArrayList();
    }

    public void coveredUnderwritingInfo(PacketList<UnderwritingInfoPacket> underwritingInfos) {
        // keep all packets, no modification required
    }

    public List<AllPeriodUnderwritingInfoPacket> coveredAllPeriodUnderwritingInfo(PacketList<AllPeriodUnderwritingInfoPacket> allPeriodUnderwritingInfos) {
        return Lists.newArrayList();
    }

    public ExposureBase exposureBase() {
        return ExposureBase.ABSOLUTE;
    }

    public IParameterObjectClassifier getType() {
        return ExposureBaseType.ABSOLUTE;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }
}
