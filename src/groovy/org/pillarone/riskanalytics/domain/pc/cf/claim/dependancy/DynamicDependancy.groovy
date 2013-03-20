package org.pillarone.riskanalytics.domain.pc.cf.claim.dependancy

import org.pillarone.riskanalytics.core.components.DynamicComposedComponent
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.dependancy.MultiPeriodCopula

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */

public class DynamicDependancy extends DynamicComposedComponent {


    public MultiPeriodCopula createDefaultSubComponent() {
        return new MultiPeriodCopula()
    }

    protected void doCalculation() {
        for (MultiPeriodCopula component: componentList) {
            component.start()
        }
    }

    @Override
    void wire() {

    }
}