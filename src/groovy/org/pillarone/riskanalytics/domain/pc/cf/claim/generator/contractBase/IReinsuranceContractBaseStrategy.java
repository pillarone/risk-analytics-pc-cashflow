package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractBaseStrategy {

    IParameterObjectClassifier getType();

    DateTime occurrenceDate(DateTime inceptionDate, IRandomNumberGenerator dateGenerator,
                            PeriodScope periodScope, EventPacket event);

    /**
     * @param event
     * @return event date
     */
    DateTime occurrenceDate(EventPacket event);

    /**
     * @param underwritingInfo
     * @return underwritingInfo.getExposure().getInceptionDate()
     */
    DateTime inceptionDate(UnderwritingInfoPacket underwritingInfo);

    /**
     *
     * @param dateGenerator
     * @return a generated date
     */
    DateTime exposureStartDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator);

    int splittedClaimsNumber();

    /**
     * Depending on the contract strategy this function will attempt to split the ultimate claim amount into smaller chunks.
     * The smaller chunks will occur on different dates but retain the reference to the root claim. This should split the
     * claims for the recognising occurence in accounting, but retain the properties required for the RI contracts and payouts.
     *
     *
     *
     * @param claimsAfterUpdate
     * @param periodScope
     * @return
     */
    List<GrossClaimRoot> splitClaims(List<GrossClaimRoot> claimsAfterUpdate, PeriodScope periodScope);
}
