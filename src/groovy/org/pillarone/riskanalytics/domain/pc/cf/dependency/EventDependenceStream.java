package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.packets.Packet;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class EventDependenceStream extends Packet {

    public EventDependenceStream() {
    }

    public EventDependenceStream(Map<String, EventSeverity> eventDependenceStream) {
        this.setEventDependenceStream(eventDependenceStream);
    }

    public EventDependenceStream(List<String> targets, List<EventSeverity> eventSeverities) {
        Map<String, EventSeverity> eventDependenceStream = new HashMap<String, EventSeverity>(targets.size());
        for (int i = 0; i < targets.size(); i++) {
            eventDependenceStream.put(targets.get(i), eventSeverities.get(i));
        }
        this.setEventDependenceStream(eventDependenceStream);
    }

    private Map<String, EventSeverity> eventDependenceStream;


    public Map<String, EventSeverity> getEventDependenceStream() {
        return eventDependenceStream;
    }

    public void setEventDependenceStream(Map<String, EventSeverity> eventDependenceStream) {
        this.eventDependenceStream = eventDependenceStream;
    }
}
