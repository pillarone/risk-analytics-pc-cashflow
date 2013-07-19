package org.pillarone.riskanalytics.domain.pc.cf.claim;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public interface ICededRoot extends IClaimRoot {

    IClaimRoot getGrossClaim();

}
