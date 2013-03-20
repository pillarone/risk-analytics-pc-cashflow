package org.pillarone.riskanalytics.domain.pc.cf.claim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimValidator {

    /** key: original claim root, value: inverted base claim */
    private Map<IClaimRoot, IClaimRoot> invertedByOriginalClaim = new HashMap<IClaimRoot, IClaimRoot>();

    public ClaimCashflowPacket invertClaimSign(ClaimCashflowPacket claim) {
//        Don't tamper with ICededRoot claims.
        if(claim.getBaseClaim() instanceof ICededRoot) {
            return claim;
        }
        if (claim.getNominalUltimate() == 0) {
            return claim;
        }
        else if (claim.getNominalUltimate() > 0) {
            IClaimRoot invertedBaseClaim = invertedByOriginalClaim.get(claim.getKeyClaim());
            if (invertedBaseClaim == null) {
                // claim is positive if ceded claims are covered, inverting sign required
                ClaimCashflowPacket invertedSignClaim = ClaimUtils.scale(claim, -1, true, true);
                invertedByOriginalClaim.put(claim.getKeyClaim(), invertedSignClaim.getBaseClaim());
                return invertedSignClaim;
            }
            else {
                return ClaimUtils.scale(claim, -1, invertedBaseClaim, true);
            }
        }
        else {
            throw new IllegalArgumentException("Incoming ceded claim (nominal ultimate) is negative. " + claim.toString());
        }
    }

    /**
     * @param claim
     * @return claim itself if claim.getNominalUltimate() is not positive. Otherwise an IllegalArgumentException is thrown.
     */
    public static ClaimCashflowPacket positiveNominalUltimate(ClaimCashflowPacket claim) {
        if (claim.getNominalUltimate() > 0) {
            throw new IllegalArgumentException("Positive nominal ultimate not supported! " + claim.toString());
        }
        return claim;
    }

    /**
     * @param claims
     * @return claims itself if all claim*.getNominalUltimate() are not positive. Otherwise an IllegalArgumentException is thrown.
     */
    public static List<ClaimCashflowPacket> positiveNominalUltimates(List<ClaimCashflowPacket> claims) {
        for (ClaimCashflowPacket claim : claims) {
            positiveNominalUltimate(claim);
        }
        return claims;
    }
}
