package org.pillarone.riskanalytics.domain.pc.cf.claim.dependancy;


import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.packets.PacketList;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;

/**
 * @author ali.majidi (at) munichre (dot) com, stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class MultiPeriodCopula extends Component {

//    New GUI component goes here.
    private PeriodScope periodScope;
    private PeriodScope iterationScope;

    public void doCalculation() {
        initIteration();
    }

    private void initIteration() {
        if(periodScope.isFirstPeriod() && iterationScope.isFirstPeriod()){
//            Generate depednancy here.
        }
    }
}