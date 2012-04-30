package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.Collections;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DefaultContractBase extends AbstractContractBase implements IReinsuranceContractBaseStrategy {

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractBaseType.PLEASESELECT;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                                   PeriodScope periodScope, EventPacket event) {
        throw new NotImplementedException();
    }

    public DateTime inceptionDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator) {
        throw new NotImplementedException();
    }
}
