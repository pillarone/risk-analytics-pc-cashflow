package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.IReinsuranceContractBaseStrategy;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.LossesOccurringContractBase;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventSeverity;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket;
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

    public List<ClaimRoot> generateClaim(List<UnderwritingInfoPacket> uwInfos, List<Factors> severityFactors,
                                         List uwInfosFilterCriteria, ExposureBase severityBase,
                                         PeriodScope periodScope) {
        lazyInitClaimsSizeGenerator();
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return generateClaims(severityScalingFactor, severityFactors, 1, periodScope, new LossesOccurringContractBase());
    }

    public List<ClaimRoot> generateClaims(double scaleFactor, List<Factors> severityFactors, int claimNumber, PeriodScope periodScope,
                                          IReinsuranceContractBaseStrategy contractBase) {
        lazyInitClaimsSizeGenerator();
        return ClaimsGeneratorUtils.generateClaims(scaleFactor, severityFactors, claimSizeGenerator, dateGenerator, claimNumber,
                claimType(), periodScope, contractBase);
    }

    /**
     * This function is required to be overridden by the overriding class. Often the override will call the
     * {@link #setGenerator(org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution, org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified)}
     * function.
     */
    abstract void lazyInitClaimsSizeGenerator();

    /**
     * This function attempts to lookup a random distribution and modifier pair against their hash key in the
     * {@link AbstractClaimsGeneratorStrategy#cachedClaimSizeGenerators} hash map. It then sets the
     * {@link AbstractClaimsGeneratorStrategy#claimSizeGenerator} for this class.
     * @param distribution random distribution
     * @param modifier modification for that distribution
     */
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

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           ExposureBase severityBase, PeriodScope periodScope,
                                           List<EventSeverity> eventSeverities) {
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, severityBase, uwInfosFilterCriteria);
        return calculateClaims(severityScalingFactor, periodScope, eventSeverities);
    }

    public List<ClaimRoot> calculateDependantClaimsWithContractBase(DependancePacket dependancePacket, IPerilMarker filterCriteria, PeriodScope periodScope, IReinsuranceContractBaseStrategy contractBase, Double underwritingInfoScaleFactor, List<Factors> indexSeverityFactors) {
        throw new SimulationException("Not implemented !");
    }

    public List<ClaimRoot> calculateClaims(double scaleFactor, PeriodScope periodScope,
                                           List<EventSeverity> eventSeverities) {
        return ClaimsGeneratorUtils.calculateClaims(scaleFactor, modifiedClaimsSizeDistribution, claimType(), periodScope,
                eventSeverities, shift);
    }

    protected List<ClaimRoot> getClaims(List<Double> claimValues, PeriodScope periodScope) {
        List<ClaimRoot> baseClaims = new ArrayList<ClaimRoot>();
        List<EventPacket> events = ClaimsGeneratorUtils.generateEventsOrNull(claimType(), claimValues.size(), periodScope, dateGenerator);
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

    public IRandomNumberGenerator getDateGenerator() {
        return dateGenerator;
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
        if(modifiedClaimsSizeDistribution == null) {
            throw new SimulationException("claims distribution is null. Have you called the lazyInit method ? Have you initialised the component correctly? ");
        }
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
