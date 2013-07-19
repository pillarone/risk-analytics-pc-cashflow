package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public interface ICededRoot extends IClaimRoot {

    IClaimRoot getGrossClaim();

}
