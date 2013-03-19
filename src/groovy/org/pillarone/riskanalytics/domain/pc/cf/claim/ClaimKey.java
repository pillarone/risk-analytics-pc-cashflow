package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimKey {

    IClaimRoot claimRoot;
    Map<Class, IComponentMarker> sources = new HashMap<Class, IComponentMarker>();

    public ClaimKey(ClaimCashflowPacket claim) {
        claimRoot = claim.getKeyClaim();
        sources.put(ISegmentMarker.class, claim.segment());
        sources.put(IReinsuranceContractMarker.class, claim.reinsuranceContract());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimKey)) return false;

        ClaimKey claimKey = (ClaimKey) o;

        if (!claimRoot.equals(claimKey.claimRoot)) return false;
        if (!sources.equals(claimKey.sources)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = claimRoot.hashCode();
        result = 31 * result + sources.hashCode();
        return result;
    }
}
