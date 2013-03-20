package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public interface ICededRoot extends IClaimRoot {

    IClaimRoot getGrossClaim();

}
