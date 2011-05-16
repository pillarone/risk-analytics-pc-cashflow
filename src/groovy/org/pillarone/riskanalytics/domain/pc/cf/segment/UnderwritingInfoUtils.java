package org.pillarone.riskanalytics.domain.pc.cf.segment;

import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoUtils {
    
    /**
     * @param underwritingInfos        the list of underwritingInfo packets to filter
     * @param contracts                the contract markers to filter by, if any; null means no filtering (all are accepted)
     * @param acceptedUnderwritingInfo the list of underwritingInfo packets whose contract is listed in contracts
     * @param rejectedUnderwritingInfo (if not null) the remaining underwritingInfo packets that were filtered out
     */
    public static void segregateUnderwritingInfoByContract(List<CededUnderwritingInfoPacket> underwritingInfos,
                                                           List<IReinsuranceContractMarker> contracts,
                                                           List<CededUnderwritingInfoPacket> acceptedUnderwritingInfo,
                                                           List<CededUnderwritingInfoPacket> rejectedUnderwritingInfo) {
        if (contracts == null || contracts.size() == 0) {
            acceptedUnderwritingInfo.addAll(underwritingInfos);
        }
        else {
            for (CededUnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                if (contracts.contains(underwritingInfo.getReinsuranceContract())) {
                    acceptedUnderwritingInfo.add(underwritingInfo);
                }
                else if (rejectedUnderwritingInfo != null) {
                    rejectedUnderwritingInfo.add(underwritingInfo);
                }
            }
        }
    }

    public static double sumPremiumWritten(List<UnderwritingInfoPacket> underwritingInfos) {
        double premiumWritten = 0;
        for (UnderwritingInfoPacket packet : underwritingInfos) {
            premiumWritten += packet.getPremiumWritten();
        }
        return premiumWritten;
    }

    public static double sumNumberOfPolicies(List<UnderwritingInfoPacket> underwritingInfos) {
        double policies = 0;
        for (UnderwritingInfoPacket packet : underwritingInfos) {
            policies += packet.getNumberOfPolicies();
        }
        return policies;
    }
}
