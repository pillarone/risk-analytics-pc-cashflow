package org.pillarone.riskanalytics.domain.pc.cf.structure;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public interface IStructuringStrategy extends IParameterObject {

    // todo(jwa): for net not filter. but compute after filtering gross and ceded
    List<ClaimCashflowPacket> filterClaims(List<ClaimCashflowPacket > claims);
    List<UnderwritingInfoPacket> filterUnderwritingInfos(List<UnderwritingInfoPacket> underwritingInfos);
}
