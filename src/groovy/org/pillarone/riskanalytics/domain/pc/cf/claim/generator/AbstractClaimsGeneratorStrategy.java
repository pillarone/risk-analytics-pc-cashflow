package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.math.distribution.*;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory;
import umontreal.iro.lecuyer.probdist.Distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractClaimsGeneratorStrategy extends AbstractParameterObject implements IClaimsGeneratorStrategy {

    private Map<String, IRandomNumberGenerator> cachedClaimSizeGenerators = new HashMap<String, IRandomNumberGenerator>();
    private IRandomNumberGenerator claimSizeGenerator;
    protected IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator();
    protected Distribution modifiedClaimsSizeDistribution;
    protected double shift;

    public List<ClaimRoot> generateClaim(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                         ExposureBase severityBase, ClaimType claimType,
                                         List<FactorsPacket> factorsPackets, PeriodScope periodScope) {
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, 1, periodScope);
    }

    protected List<ClaimRoot> generateClaims(double scaleFactor, int claimNumber, PeriodScope periodScope) {
        return ClaimsGeneratorUtils.generateClaims(scaleFactor, claimSizeGenerator, dateGenerator, claimNumber,
                claimType(), periodScope);
    }

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           ExposureBase severityBase, PeriodScope periodScope,
                                           List<Double> severities, List<EventPacket> events) {
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return calculateClaims(severityScalingFactor, periodScope, severities, events);
    }

    protected List<ClaimRoot> calculateClaims(double scaleFactor, PeriodScope periodScope,
                                              List<Double> severities, List<EventPacket> events) {
        return ClaimsGeneratorUtils.calculateClaims(scaleFactor, modifiedClaimsSizeDistribution, claimType(), periodScope,
                severities, events, shift);
    }

    protected List<ClaimRoot> getClaims(List<Double> claimValues, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        List<EventPacket> events = ClaimsGeneratorUtils.generateEvents(claimType(), claimValues.size(), periodScope, dateGenerator);
        for (int i = 0; i < claimValues.size(); i++) {
            DateTime occurrenceDate = events == null ?
                    DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue()) : events.get(i).getDate();
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = periodScope.getCurrentPeriodStartDate();
            EventPacket event = events == null ? null : events.get(i);
            baseClaims.add(new ClaimRoot(claimValues.get(i) * -1, claimType(), exposureStartDate, occurrenceDate, event));
        }
        return baseClaims;
    }

    protected void setGenerator(RandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier);
        if (cachedClaimSizeGenerators.containsKey(key)) {
            claimSizeGenerator = cachedClaimSizeGenerators.get(key);
        }
        else {
            claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier);
            cachedClaimSizeGenerators.put(key, claimSizeGenerator);
        }
    }

    protected void setGenerator(RandomDistribution distribution) {
        setGenerator(distribution, DistributionModifier.getStrategy(DistributionModifier.NONE, null));
    }

    protected void setDateGenerator(RandomDistribution distribution) {
        dateGenerator = RandomNumberGeneratorFactory.getGenerator(distribution);
    }

    protected String key(AbstractRandomDistribution distribution, DistributionModified modifier) {
        return String.valueOf(distribution.hashCode()) + String.valueOf(modifier.hashCode());
    }


    protected final static String CLAIMS_SIZE_BASE = "claimsSizeBase";
    protected final static String CLAIMS_SIZE_DISTRIBUTION = "claimsSizeDistribution";
    protected final static String CLAIMS_SIZE_MODIFICATION = "claimsSizeModification";
    protected final static String FREQUENCY_BASE = "frequencyBase";
    protected final static String FREQUENCY_DISTRIBUTION = "frequencyDistribution";
    protected final static String FREQUENCY_MODIFICATION = "frequencyModification";
    protected final static String OCCURRENCE_DATE_DISTRIBUTION = "occurrenceDateDistribution";

    public Distribution getModifiedClaimsSizeDistribution() {
        return modifiedClaimsSizeDistribution;
    }

    public void setModifiedClaimsSizeDistribution(Distribution modifiedClaimsSizeDistribution) {
        this.modifiedClaimsSizeDistribution = modifiedClaimsSizeDistribution;
    }

    public void setModifiedDistribution(RandomDistribution distribution, DistributionModified modifier) {
        Distribution dist = distribution.getDistribution();
        if (modifier.getType().equals(DistributionModifier.CENSORED) || modifier.getType().equals(DistributionModifier.CENSOREDSHIFT)) {
            modifiedClaimsSizeDistribution = new CensoredDistribution(dist, (Double) modifier.getParameters().get("min"),
                    (Double) modifier.getParameters().get("max"));
        }
        else if (modifier.getType().equals(DistributionModifier.TRUNCATED) || modifier.getType().equals(DistributionModifier.TRUNCATEDSHIFT)) {
            Double leftBoundary = (Double) modifier.getParameters().get("min");
            Double rightBoundary = (Double) modifier.getParameters().get("max");
            modifiedClaimsSizeDistribution = new TruncatedDistribution(dist, leftBoundary, rightBoundary);
        }
        else if (modifier.getType().equals(DistributionModifier.LEFTTRUNCATEDRIGHTCENSOREDSHIFT)) {
            Double leftBoundary = (Double) modifier.getParameters().get("min");
            Double rightBoundary = (Double) modifier.getParameters().get("max");
            modifiedClaimsSizeDistribution = new CensoredDistribution(new TruncatedDistribution(dist,
                    leftBoundary, Double.POSITIVE_INFINITY),
                    Double.NEGATIVE_INFINITY, rightBoundary);
        }
        else {
            modifiedClaimsSizeDistribution = dist;
        }
        shift = modifier.getParameters().get("shift") == null ? 0 : (Double) modifier.getParameters().get("shift");
    }

    public double getShift() {
        return shift;
    }

    public void setShift(double shift) {
        this.shift = shift;
    }
}
