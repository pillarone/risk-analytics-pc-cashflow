package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.io.Serializable;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICommission extends Serializable{

    void calculateCommission(List<ClaimCashflowPacket> cededClaims, List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                             boolean isAdditive, Integer occurrencePeriod);
}
