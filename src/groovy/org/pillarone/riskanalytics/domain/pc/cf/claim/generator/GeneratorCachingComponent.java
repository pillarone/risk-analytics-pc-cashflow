package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
// todo(sku): test if really necessary. Try to remove!
public abstract class GeneratorCachingComponent extends Component {

    private Map<String, IRandomNumberGenerator> generators = new HashMap<String, IRandomNumberGenerator>();

    protected IRandomNumberGenerator getCachedGenerator(RandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier);
        IRandomNumberGenerator generator;

        if (generators.containsKey(key)) {
            generator = generators.get(key);
        } else {
            try {
                generator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("['GeneratorCachingComponent.illegalDistributionDistributionModifier','"
                        +this.getNormalizedName()+"','"+ex.getLocalizedMessage()+"']");
            }
            generators.put(key, generator);
        }
        return generator;
    }

    private String key(RandomDistribution distribution, DistributionModified modifier) {
        return String.valueOf(distribution.hashCode()) + String.valueOf(modifier.hashCode());
    }

}

