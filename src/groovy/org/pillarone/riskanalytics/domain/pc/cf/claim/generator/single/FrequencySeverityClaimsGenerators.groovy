package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.single

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerators

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FrequencySeverityClaimsGenerators extends AbstractClaimsGenerators {

    @Override
    Component createDefaultSubComponent() {
        new FrequencySeverityClaimsGenerator()
    }
}
