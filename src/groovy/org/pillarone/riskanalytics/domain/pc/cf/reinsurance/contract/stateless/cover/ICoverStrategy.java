package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover;


import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICoverStrategy extends IParameterObject {

    void coveredClaims(List<ClaimCashflowPacket> source);

}

