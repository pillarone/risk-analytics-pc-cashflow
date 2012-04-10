package org.pillarone.riskanalytics.domain.pc.cf.exposure.filter;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IExposureBaseStrategy extends IParameterObject {

    double factor(PacketList<UnderwritingInfoPacket> underwritingInfos);

    ExposureBase exposureBase();

}
