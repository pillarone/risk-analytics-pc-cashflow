package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IStructuringStrategy extends IParameterObject {

    List<ClaimCashflowPacket> filterClaims(List<ClaimCashflowPacket > claims);
    List<UnderwritingInfoPacket> filterUnderwritingInfos(List<UnderwritingInfoPacket> underwritingInfos);
    List<CededUnderwritingInfoPacket> filterUnderwritingInfosCeded(List<CededUnderwritingInfoPacket> underwritingInfos);
}
