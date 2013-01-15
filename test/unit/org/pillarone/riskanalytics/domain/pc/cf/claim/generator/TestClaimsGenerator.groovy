package org.pillarone.riskanalytics.domain.pc.cf.claim.generator

import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract class TestClaimsGenerator {

    static ClaimsGenerator getAttritionalClaimsGenerator(String name, IterationScope iterationScope, double constant) {
        ClaimsGenerator generator = new ClaimsGenerator(name: name)
        generator.periodScope = iterationScope.periodScope
        generator.periodStore = new PeriodStore(generator.periodScope)
        generator.parmClaimsModel = ClaimsGeneratorType.getStrategy(
                ClaimsGeneratorType.ATTRITIONAL, [
                "claimsSizeBase": ExposureBase.ABSOLUTE,
                "claimsSizeDistribution": DistributionType.getStrategy(DistributionType.CONSTANT, [constant: constant]),
                "claimsSizeModification": DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),])
        return generator
    }
}
