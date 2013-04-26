package org.pillarone.riskanalytics.domain.pc.cf.event;

import org.joda.time.DateTime;

/**
 * author simon.parten @ art-allianz . com
 */
public interface IEvent {

    public DateTime getDate();

    public int getEventID();

}
