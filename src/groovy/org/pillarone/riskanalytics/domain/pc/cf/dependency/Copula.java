package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorUtils;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType;
import org.pillarone.riskanalytics.domain.utils.math.copula.ICopulaStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class Copula extends Component {

    private PacketList<EventDependenceStream> outEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);

    private ICopulaStrategy parmCopulaStrategy = CopulaType.getDefault();
    private PeriodScope periodScope;

    public void doCalculation() {

        List<EventPacket> events = ClaimsGeneratorUtils.generateEvents(1, getPeriodScope());
        outEventSeverities.add(buildEventDependenceStream(events.get(0)));
    }

    private EventDependenceStream buildEventDependenceStream(EventPacket event) {
        return new EventDependenceStream(getTargetNames(), buildEventSeverities(event));
    }

    protected List<String> getTargetNames() {
        return parmCopulaStrategy.getTargetNames();
    }

    private List<EventSeverity> buildEventSeverities(EventPacket event) {
        List<EventSeverity> eventSeverities = new ArrayList<EventSeverity>();
        List<Number> probabilities = parmCopulaStrategy.getRandomVector();
        for (int i = 0; i < probabilities.size(); i++) {
            EventSeverity eventSeverity = new EventSeverity();
            eventSeverity.setValue((Double) probabilities.get(i));
            eventSeverity.setEvent(event);
            eventSeverities.add(eventSeverity);
        }
        return eventSeverities;
    }

    public ICopulaStrategy getParmCopulaStrategy() {
        return parmCopulaStrategy;
    }

    public void setParmCopulaStrategy(ICopulaStrategy parmCopulaStrategy) {
        this.parmCopulaStrategy = parmCopulaStrategy;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PacketList<EventDependenceStream> getOutEventSeverities() {
        return outEventSeverities;
    }

    public void setOutEventSeverities(PacketList<EventDependenceStream> outEventSeverities) {
        this.outEventSeverities = outEventSeverities;
    }
}