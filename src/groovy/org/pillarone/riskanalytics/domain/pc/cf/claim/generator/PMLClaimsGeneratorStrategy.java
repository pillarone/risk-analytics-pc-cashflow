package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier;
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.utils.*;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PMLClaimsGeneratorStrategy extends AbstractSingleClaimsGeneratorStrategy {

    public static final String RETURN_PERIOD = "return period";
    public static final String MAX_CLAIM = "maximum claim";

    private ConstrainedMultiDimensionalParameter pmlData = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[0d], [0d]]"), Arrays.asList("return period", "maximum claim"),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));
    private DistributionModified claimsSizeModification
            = DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap());
    private FrequencySeverityClaimType produceClaim = FrequencySeverityClaimType.AGGREGATED_EVENT;

    private Map<Integer, RandomDistribution> claimsSizeDistribution = new HashMap<Integer, RandomDistribution>();
    private Map<Integer, RandomDistribution> frequencyDistribution = new HashMap<Integer, RandomDistribution>();

    public IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.PML;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("pmlData", pmlData);
        parameters.put("claimsSizeModification", claimsSizeModification);
        parameters.put("produceClaim", produceClaim);
        return parameters;
    }

    /**
     * @param uwInfos               is ignored
     * @param uwInfosFilterCriteria is ignored
     * @param factorsPackets        is ignored
     * @param periodScope
     * @return
     */
    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                          List<FactorsPacket> factorsPackets, PeriodScope periodScope,
                                          List<SystematicFrequencyPacket> systematicFrequencies, IPerilMarker filterCriteria) {
        setClaimsSizeGenerator(periodScope);
        setClaimNumberGenerator(periodScope);
        ClaimType claimType = produceClaim == FrequencySeverityClaimType.SINGLE ? ClaimType.SINGLE : ClaimType.AGGREGATED_EVENT;
        return generateClaims(claimType, null, periodScope);
    }

    private void setClaimsSizeGenerator(PeriodScope periodScope) {
        Integer currentPeriod = periodScope.getCurrentPeriod();
        RandomDistribution distribution = claimsSizeDistribution.get(currentPeriod);
        if (distribution == null) {
            lazyInitializeDistributionMaps(currentPeriod);
            distribution = claimsSizeDistribution.get(currentPeriod);
        }
        setGenerator(distribution);
    }

    private void setClaimNumberGenerator(PeriodScope periodScope) {
        Integer currentPeriod = periodScope.getCurrentPeriod();
        RandomDistribution distribution = frequencyDistribution.get(currentPeriod);
        if (distribution == null) {
            lazyInitializeDistributionMaps(currentPeriod);
            distribution = frequencyDistribution.get(currentPeriod);
        }
        setClaimNumberGenerator(distribution);
    }

    private void lazyInitializeDistributionMaps(int currentPeriod) {
        if (claimsSizeDistribution.get(currentPeriod) == null) {
            List<Double> observations = new ArrayList<Double>(pmlData.getValueRowCount());
            List<Double> frequencies = new ArrayList<Double>();
            List<Double> cumProbabilities = new ArrayList<Double>();
            int columnIndexReturnPeriod = pmlData.getColumnIndex(RETURN_PERIOD);
            int columnIndexMaxClaim = pmlData.getColumnIndex(MAX_CLAIM);
            for (int row = pmlData.getTitleRowCount(); row < pmlData.getRowCount(); row++) {
                if (!observations.contains(InputFormatConverter.getDouble(pmlData.getValueAt(row, columnIndexMaxClaim)))) {
                    observations.add(InputFormatConverter.getDouble(pmlData.getValueAt(row, columnIndexMaxClaim)));
                    double returnPeriod = InputFormatConverter.getDouble(pmlData.getValueAt(row, columnIndexReturnPeriod));
                    double frequency = 1 / returnPeriod;
                    frequencies.add(frequency);
                    cumProbabilities.add(1d - frequency / frequencies.get(0));
                }
            }

            // todo(jwa): refactor, makes no sense to introduce a TMDP, fill distributions directly
            Map<String, TableMultiDimensionalParameter> parameters = new HashMap<String, TableMultiDimensionalParameter>();
            TableMultiDimensionalParameter table = new TableMultiDimensionalParameter(
                    Arrays.asList(observations, cumProbabilities),
                    Arrays.asList("observations", "cumulative probabilities"));
            parameters.put("discreteEmpiricalCumulativeValues", table);
            claimsSizeDistribution.put(currentPeriod, DistributionType.getStrategy(DistributionType.DISCRETEEMPIRICALCUMULATIVE, parameters));

            double lambda = frequencies.get(0);
            if (claimsSizeModification.getType().equals(DistributionModifier.TRUNCATED)
                    || claimsSizeModification.getType().equals(DistributionModifier.TRUNCATEDSHIFT)) {
                double min = (Double) claimsSizeModification.getParameters().get("min");
                double max = (Double) claimsSizeModification.getParameters().get("max");
                List<Double> observationsAndBoundaries = new ArrayList<Double>();
                observationsAndBoundaries.addAll(observations);
                observationsAndBoundaries.add(min);
                observationsAndBoundaries.add(max);
                Collections.sort(observationsAndBoundaries);
                int indexMin = observationsAndBoundaries.indexOf(min);
                int indexMax = observationsAndBoundaries.lastIndexOf(max);
                if ((indexMax - 1) == observations.size()) {
                    lambda = frequencies.get(indexMin);
                }
                else {
                    lambda = frequencies.get(indexMin) - frequencies.get(indexMax - 2);
                }
            }
            else if (claimsSizeModification.getType().equals(DistributionModifier.LEFTTRUNCATEDRIGHTCENSOREDSHIFT)) {
                double min = (Double) claimsSizeModification.getParameters().get("min");
                List<Double> observationsAndMin = new ArrayList<Double>();
                observationsAndMin.addAll(observations);
                observationsAndMin.add(min);
                Collections.sort(observationsAndMin);
                int indexMin = observationsAndMin.indexOf(min);
                lambda = frequencies.get(indexMin);
            }
            Map<String, Double> lambdaParam = new HashMap<String, Double>();
            lambdaParam.put("lambda", lambda);
            frequencyDistribution.put(currentPeriod, DistributionType.getStrategy(DistributionType.POISSON, lambdaParam));
        }
    }

    public List<ClaimRoot> calculateClaims(List<UnderwritingInfoPacket> uwInfos, List uwInfosFilterCriteria,
                                           List<EventDependenceStream> eventStreams,
                                           IPerilMarker filterCriteria, PeriodScope periodScope) {
        return new ArrayList<ClaimRoot>();
    }
}
