package org.pillarone.riskanalytics.domain.pc.cf.exposure;

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
    private static List<UnderwritingInfoPacket> filterUnderwritingInfo(List<UnderwritingInfoPacket> underwritingInfos, List coverCriteria) {
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
}
