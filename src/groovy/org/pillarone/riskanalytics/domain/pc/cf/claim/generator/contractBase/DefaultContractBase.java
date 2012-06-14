package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class DefaultContractBase extends AbstractContractBase implements IReinsuranceContractBaseStrategy {

    public static final String errorMessage = "Default contractbase is not a valid option at runtime. Please select a different contract base.";

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractBaseType.PLEASESELECT;
    }

    public Map getParameters() {
        return Collections.emptyMap();
    }

    public DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                                   PeriodScope periodScope, EventPacket event) {
        throw new SimulationException(errorMessage);
    }

    public DateTime inceptionDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator) {
        throw new SimulationException(errorMessage);
    }

    public List<GrossClaimRoot> splitClaims(List<GrossClaimRoot> claimsAfterUpdate, PeriodScope periodScope) {
        throw new SimulationException(errorMessage);
    }
}
