package org.pillarone.riskanalytics.domain.pc.cf.event;

import org.pillarone.riskanalytics.core.packets.SingleValuePacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class EventSeverity extends SingleValuePacket {
    public String valueLabel = "severity";

    public EventPacket event;

    public String getValueLabel() {
        return "severity";
    }

    public EventPacket getEvent() {
        return event;
    }

    public void setEvent(EventPacket event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "EventSeverity{" +
                "valueLabel='" + valueLabel + '\'' +
                ", event=" + event +
                ", evenSeverity " + this.getValue() +
                '}';
    }
}
