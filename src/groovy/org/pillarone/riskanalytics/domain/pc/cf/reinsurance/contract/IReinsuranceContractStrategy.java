package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategy {
    /**
     * @param underwritingInfoPackets used for scaling relative contract parameters
     * @return fully prepared contract
     */
    IReinsuranceContract getContract(List<UnderwritingInfoPacket> underwritingInfoPackets);
}
