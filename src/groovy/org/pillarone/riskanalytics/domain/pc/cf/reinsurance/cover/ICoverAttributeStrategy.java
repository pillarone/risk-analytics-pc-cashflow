package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ICoverAttributeStrategy extends IParameterObject {

    /**
     * Make sure to use an appropriate ClaimValidator method before adding a claim to the covered list.
     * This is especially crucial if ceded claims are covered!
     * @param source
     * @return
     */
    List<ClaimCashflowPacket> coveredClaims(List<ClaimCashflowPacket> source);

    List<UnderwritingInfoPacket> coveredUnderwritingInfo(List<UnderwritingInfoPacket> source, List<ClaimCashflowPacket> coveredGrossClaims);
}
