package org.pillarone.riskanalytics.domain.pc.cf.claim.generator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.model.IModelVisitor;
import org.pillarone.riskanalytics.core.model.ModelPath;
import org.pillarone.riskanalytics.core.parameterization.*;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.util.GroovyUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.LossesOccurringContractBase;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventDependenceStream;
import org.pillarone.riskanalytics.domain.pc.cf.dependency.SystematicFrequencyPacket;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FactorsPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints;
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier;
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType;
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ExternalValuesByIterationStrategy extends AbstractParameterObject implements IExternalValuesStrategy {

    static Log LOG = LogFactory.getLog(ExternalValuesByIterationStrategy.class);

    public static final String ITERATION = "iteration";
    public static final String VALUE = "value";

    private ConstrainedMultiDimensionalParameter valueTable = new ConstrainedMultiDimensionalParameter(
            GroovyUtils.toList("[[0], [0d]]"), Arrays.asList("iteration", "value"),
            ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER));
    private PeriodApplication usage = PeriodApplication.FIRSTPERIOD;

    private ListMultimap<Integer, Double> internalValueByIteration = ArrayListMultimap.create();
    private int iteration = 0;

    public IParameterObjectClassifier getType() {
        return ExternalValuesType.BY_ITERATION;
    }

    public Map getParameters() {
        Map<String, Object> parameters = new HashMap<String, Object>(2);
        parameters.put("valueTable", valueTable);
        parameters.put("usage", usage);
        return parameters;
    }

    /**
     *
     * @param baseClaims this list is needed in order to calculate the number of required claims after calculateDependantClaimsWithContractBase
     *                   has been executed i.e. if external severity information is provided and only the missing claim
     *                   number needs to be generated.
     * @param uwInfos
     * @param severityFactors
     * @param uwInfosFilterCriteria
     * @param periodScope
     * @return
     */
    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
                                          PeriodScope periodScope, ClaimType claimType, ExposureBase claimsSizeBase,
                                          IRandomNumberGenerator dateGenerator) {
        lazyInitializeDistributionMaps();
        if (usage.equals(PeriodApplication.ALLPERIODS) || periodScope.isFirstPeriod()) {
            if (periodScope.isFirstPeriod()) {
                iteration++;
            }
            List<Double> ultimates = internalValueByIteration.get(iteration);
            int numberOfClaims = ultimates.size();
            double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, claimsSizeBase, uwInfosFilterCriteria);
            List<EventPacket> events = ClaimsGeneratorUtils.generateEventsOrNull(claimType, numberOfClaims, periodScope, dateGenerator);
            LossesOccurringContractBase contractBase = new LossesOccurringContractBase();
            for (int i = 0; i < numberOfClaims; i++) {
                EventPacket event = events == null ? null : events.get(i);
                // todo(sku): replace with information from underwriting
                DateTime exposureStartDate = contractBase.exposureStartDate(periodScope, dateGenerator);
                double ultimate = ultimates.get(i) * -severityScalingFactor;
                DateTime occurrenceDate = contractBase.occurrenceDate(exposureStartDate, dateGenerator, periodScope, event);
                double scaleFactor = IndexUtils.aggregateFactor(severityFactors, exposureStartDate, periodScope.getPeriodCounter(), exposureStartDate);
                baseClaims.add(new ClaimRoot(ultimate * scaleFactor, claimType, exposureStartDate, occurrenceDate, event));
            }
        }
        return baseClaims;
    }

    private void lazyInitializeDistributionMaps() {
        if (internalValueByIteration.isEmpty()) {
            int columnIndexIteration = valueTable.getColumnIndex(ITERATION);
            int columnIndexValue = valueTable.getColumnIndex(VALUE);
            for (int row = valueTable.getTitleRowCount(); row < valueTable.getRowCount(); row++) {
                int iteration = InputFormatConverter.getInt(valueTable.getValueAt(row, columnIndexIteration));
                double value = InputFormatConverter.getDouble(valueTable.getValueAt(row, columnIndexValue));
                internalValueByIteration.put(iteration, value);
            }
        }
    }
}
