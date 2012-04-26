package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class AbsoluteBaseStrategy extends AbstractParameterObject implements IExposureBaseStrategy {

    public double factor(PacketList<UnderwritingInfoPacket> underwritingInfos) {
        return 1;
    }

    @Override
    public void coveredUnderwritingInfo(PacketList<UnderwritingInfoPacket> underwritingInfos) {
        // keep all packets, no modification required
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
