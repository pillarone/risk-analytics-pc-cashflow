package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoUtils {

    /**
     * @param underwritingInfos underwriting info packets to be filtered
     * @param coverCriteria     components such as RiskBands
     * @return an underwriting info packet is added to the list of filtered underwriting info packets if the
     *         originalUnderwritingInfo references an element of the cover criteria. If it didn't match or
     *         originalUnderwritingInfo property is null, the origin property is evaluated.
     */
    public static List<UnderwritingInfoPacket> filterUnderwritingInfo(List<UnderwritingInfoPacket> underwritingInfos, List coverCriteria) {
        List<UnderwritingInfoPacket> filterUnderwritingInfos = new ArrayList<UnderwritingInfoPacket>(underwritingInfos.size());
        if (coverCriteria != null) {
            for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
                if (underwritingInfo.getOriginal() != null
                        && coverCriteria.contains(underwritingInfo.getOriginal())
                        || coverCriteria.contains(underwritingInfo.origin)) {
                    filterUnderwritingInfos.add(underwritingInfo);
                }
            }
        }
        return filterUnderwritingInfos;
    }

    private static double scalingFactor(List<UnderwritingInfoPacket> underwritingInfos, ExposureBase base) {
        double factor = 0d;
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            factor += underwritingInfo.scaleValue(base);
        }
        return factor;
    }

    private static double scalingFactor(List<UnderwritingInfoPacket> underwritingInfos, FrequencyBase base) {
        double factor = 0d;
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            factor += underwritingInfo.scaleValue(base);
        }
        return factor;
    }

    public static double scalingFactor(List<UnderwritingInfoPacket> underwritingInfos, ExposureBase base, List coverCriteria) {
        if (underwritingInfos.isEmpty() || coverCriteria.isEmpty()) return 1d;
        return scalingFactor(filterUnderwritingInfo(underwritingInfos, coverCriteria), base);
    }

    public static double scalingFactor(List<UnderwritingInfoPacket> underwritingInfos, FrequencyBase base, List coverCriteria) {
        if (underwritingInfos.isEmpty() || coverCriteria.isEmpty()) return 1d;
        return scalingFactor(filterUnderwritingInfo(underwritingInfos, coverCriteria), base);
    }
    
    static public UnderwritingInfoPacket aggregate(List<UnderwritingInfoPacket> underwritingInfos) {
        if (underwritingInfos == null || underwritingInfos.size() == 0) {
            return null;
        }
        UnderwritingInfoPacket summedUnderwritingInfo = new UnderwritingInfoPacket();
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            summedUnderwritingInfo.plus(underwritingInfo);
            summedUnderwritingInfo.setExposure(underwritingInfo.getExposure());
        }
        return correctMetaProperties(summedUnderwritingInfo, underwritingInfos);
    }
    
    static public UnderwritingInfoPacket correctMetaProperties(UnderwritingInfoPacket result, List<UnderwritingInfoPacket> underwritingInfos) {
        UnderwritingInfoPacket verifiedResult = (UnderwritingInfoPacket) result.clone();
        ISegmentMarker lob = verifiedResult.getSegment();
        IReinsuranceContractMarker reinsuranceContract = verifiedResult.getReinsuranceContract();
        boolean underwritingInfosOfDifferentLobs = lob == null;
        boolean underwritingInfosOfDifferentContracts = reinsuranceContract == null;
        for (UnderwritingInfoPacket underwritingInfo : underwritingInfos) {
            if (!underwritingInfosOfDifferentLobs && !lob.equals(underwritingInfo.getSegment())) {
                underwritingInfosOfDifferentLobs = true;
            }
            if (!underwritingInfosOfDifferentContracts && !reinsuranceContract.equals(underwritingInfo.getReinsuranceContract())) {
                underwritingInfosOfDifferentContracts = true;
            }
        }
        if (underwritingInfosOfDifferentLobs) {
            verifiedResult.setSegment(null);
        }
        if (underwritingInfosOfDifferentContracts) {
            verifiedResult.setReinsuranceContract(null);
        }
        return verifiedResult;
    }

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
