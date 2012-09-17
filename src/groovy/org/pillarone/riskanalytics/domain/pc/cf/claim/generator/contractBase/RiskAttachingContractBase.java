package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     *
     * @param periodScope
     * @param dateGenerator
     * @return This is start of the contract exposure... NOT the inception date of the claim.
     */
    public DateTime exposureStartDate(PeriodScope periodScope, IRandomNumberGenerator dateGenerator) {
        return DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue());
//        return periodScope.getCurrentPeriodStartDate();
    }

    /**
     * The original claim and its ultimate is splitted according to the number of the underlying contract length
     * @return underlyingContractLength
     */
    public int splittedClaimsNumber() {
        return underlyingContractLength;
    }


    /**
     * This function splits claims which may occur outside of the occurrence period when the original root claim incepted.
     * The original occurrence date is completely ignored. Instead new uniformly distributed occurrence dates starting
     * at the exposure start date and ending after splittedClaimsNumber * months are generated.
     *
     * @param claimsAfterUpdate
     * @param periodScope
     * @return splits the original ultimate by splittedClaimsNumber()
     */
    public List<GrossClaimRoot> splitClaims(List<GrossClaimRoot> claimsAfterUpdate, PeriodScope periodScope) {
        List<GrossClaimRoot> claimRoots = new ArrayList<GrossClaimRoot>();
        IRandomNumberGenerator dateGen = RandomNumberGeneratorFactory.getUniformGenerator();
        for (GrossClaimRoot claimRoot : claimsAfterUpdate) {
            double splittedUltimate = claimRoot.getUltimate() / (double) splittedClaimsNumber();
            for (int j = 0; j < splittedClaimsNumber(); j++) {
                DateTime occurrenceDate = occurrenceDate(claimRoot.getExposureStartDate(), dateGen, periodScope, claimRoot.getEvent());
                claimRoots.add(new GrossClaimRoot(claimRoot, splittedUltimate, occurrenceDate, claimRoot.getStartDateForPatterns()));
            }
        }
        return claimRoots;
    }

}
