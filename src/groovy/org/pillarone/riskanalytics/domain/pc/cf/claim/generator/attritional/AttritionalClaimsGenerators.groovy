package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.attritional

import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.AbstractClaimsGenerators

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AttritionalClaimsGenerators extends AbstractClaimsGenerators {

    @Override
    Component createDefaultSubComponent() {
        new AttritionalClaimsGenerator()
    }

}
