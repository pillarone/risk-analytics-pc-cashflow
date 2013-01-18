package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.dependancy;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IterationStore;
import org.pillarone.riskanalytics.core.components.PeriodStore;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType;
import org.pillarone.riskanalytics.domain.utils.math.copula.ICopulaStrategy;
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket;

/**
 * author simon.parten @ art-allianz . com
 */
public class MultiPeriodCopula extends Component {

    private ICopulaStrategy parmCopulaStrategy = CopulaType.getDefault();
    private Integer globalLastCoveredPeriod;
    private boolean globalSanityChecks;
    private PeriodScope periodScope;
    private PeriodStore periodStore;

    public static final String ALL_PERIOD_DEPENDANCY = "All period dependancy";

    private PacketList<DependancePacket> outProbabilities = new PacketList(DependancePacket .class);

    @Override
    protected void doCalculation() {
        initIteration();
        DependancePacket dependancePacket = (DependancePacket) periodStore.get(ALL_PERIOD_DEPENDANCY, -periodScope.getCurrentPeriod());
        if(dependancePacket == null) {
            throw new SimulationException("No dependancies generated. Contract development");
        }
        outProbabilities.add(dependancePacket);
    }

    private void initIteration() {
        if(periodScope.isFirstPeriod()) {
           DependancePacket dependancePacket = parmCopulaStrategy.getDependanceAllPeriod(globalLastCoveredPeriod);
            periodStore.put(ALL_PERIOD_DEPENDANCY, dependancePacket);
        }
    }

    public ICopulaStrategy getParmCopulaStrategy() {
        return parmCopulaStrategy;
    }

    public void setParmCopulaStrategy(ICopulaStrategy parmCopulaStrategy) {
        this.parmCopulaStrategy = parmCopulaStrategy;
    }

    public Integer getGlobalLastCoveredPeriod() {
        return globalLastCoveredPeriod;
    }

    public void setGlobalLastCoveredPeriod(Integer globalLastCoveredPeriod) {
        this.globalLastCoveredPeriod = globalLastCoveredPeriod;
    }

    public boolean isGlobalSanityChecks() {
        return globalSanityChecks;
    }

    public void setGlobalSanityChecks(boolean globalSanityChecks) {
        this.globalSanityChecks = globalSanityChecks;
    }

    public PeriodScope getPeriodScope() {
        return periodScope;
    }

    public void setPeriodScope(PeriodScope periodScope) {
        this.periodScope = periodScope;
    }

    public PeriodStore getPeriodStore() {
        return periodStore;
    }

    public void setPeriodStore(PeriodStore periodStore) {
        this.periodStore = periodStore;
    }
}
