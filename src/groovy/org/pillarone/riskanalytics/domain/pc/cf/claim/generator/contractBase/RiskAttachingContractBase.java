package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class RiskAttachingContractBase extends AbstractContractBase implements IReinsuranceContractBaseStrategy {

    private Integer underlyingContractLength;

    public IParameterObjectClassifier getType() {
        return ReinsuranceContractBaseType.RISKATTACHING;
    }

    public Map getParameters() {
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("underlyingContractLength", underlyingContractLength);
        return params;
    }

    /**
     * @param inceptionDate
     * @param dateGenerator
     * @param periodScope
     * @param event ignored in this strategy
     * @return occurrence date x days after inception date
     */
    public DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                                   PeriodScope periodScope, EventPacket event) {
        int days = (int) (dateGenerator.nextValue().doubleValue() * (365/12d) * underlyingContractLength);
        return inceptionDate.plusDays(days);
    }

    /**
     * @param periodScope
     * @param dateGenerator
     * @return generate a new date according using dateGenerator and periodScope
     */
    public DateTime inceptionDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator) {
        return DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue());
    }

    /**
     * The original claim and its ultimate is splitted according to the number of the underlying contract length
     * @return underlyingContractLength
     */
    public int splittedClaimsNumber() {
        return underlyingContractLength;
    }
}
