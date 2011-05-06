package org.pillarone.riskanalytics.domain.pc.cf.dependency;

import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.GeneratorCachingComponent;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType;
import org.pillarone.riskanalytics.domain.utils.math.copula.ICopulaStrategy;
import org.pillarone.riskanalytics.domain.utils.math.distribution.*;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
public class MultipleProbabilitiesCopula extends GeneratorCachingComponent {

    private RandomDistribution parmFrequencyDistribution = FrequencyDistributionType.getDefault();
    private ICopulaStrategy parmCopulaStrategy = CopulaType.getDefault();

    private DistributionModified modifier = DistributionModifier.getDefault();
    private IRandomNumberGenerator generator;
    private PeriodScope periodScope;

    private PacketList<EventDependenceStream> outEventSeverities = new PacketList<EventDependenceStream>(EventDependenceStream.class);
    private PacketList<SystematicFrequencyPacket> outEventFrequencies = new PacketList<SystematicFrequencyPacket>(SystematicFrequencyPacket.class);

    public void validateParameterization() {
        if (parmFrequencyDistribution == null) {
            throw new IllegalStateException("MultipleProbabilitiesCopula.missingDistribution");
        }
        super.validateParameterization();
    }

    public void doCalculation() {

        buildEventFrequencyPacket();
        setGenerator(getCachedGenerator(parmFrequencyDistribution, modifier));
        int frequency = getGenerator().nextValue().intValue();

        List<EventPacket> events = ClaimsGeneratorUtils.generateEvents(frequency, periodScope);
        for (int i = 0; i < frequency; i++) {
            outEventSeverities.add(new EventDependenceStream(parmCopulaStrategy.getTargetNames(),
                    buildEventSeverities(events.get(i))));
        }
    }

    private void buildEventFrequencyPacket() {
       SystematicFrequencyPacket packet = new SystematicFrequencyPacket();
        packet.setFrequencyDistribution(parmFrequencyDistribution);
        packet.setTargets(parmCopulaStrategy.getTargetNames());
        outEventFrequencies.add(packet);
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

    public DistributionModified getModifier() {
        return modifier;
    }

    public void setModifier(DistributionModified modifier) {
        this.modifier = modifier;
    }

    public RandomDistribution getParmFrequencyDistribution() {
        return parmFrequencyDistribution;
    }

    public void setParmFrequencyDistribution(RandomDistribution parmDistribution) {
        this.parmFrequencyDistribution = parmDistribution;
    }

    public PacketList<EventDependenceStream> getOutEventSeverities() {
        return outEventSeverities;
    }

    public void setOutEventSeverities(PacketList<EventDependenceStream> outEventSeverities) {
        this.outEventSeverities = outEventSeverities;
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

    public IRandomNumberGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(IRandomNumberGenerator generator) {
        this.generator = generator;
    }

    public PacketList<SystematicFrequencyPacket> getOutEventFrequencies() {
        return outEventFrequencies;
    }

    public void setOutEventFrequencies(PacketList<SystematicFrequencyPacket> outEventFrequencies) {
        this.outEventFrequencies = outEventFrequencies;
    }
}
