package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorUtils;
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase.LossesOccurringContractBase;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexUtils;
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
abstract public class AbstractExternalValuesByIterationStrategy extends AbstractParameterObject implements IExternalValuesStrategy {

    public static final String ITERATION = "iteration";
    public static final String VALUE = "value";

    private ListMultimap<Integer, Double> internalValueByIteration = ArrayListMultimap.create();

    abstract public ConstrainedMultiDimensionalParameter table();
    abstract public PeriodApplication usage();

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
                                          IRandomNumberGenerator dateGenerator, int iteration) {
        lazyInitializeDistributionMaps();
        if (usage().equals(PeriodApplication.ALLPERIODS) || periodScope.isFirstPeriod()) {
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
            int columnIndexIteration = table().getColumnIndex(ITERATION);
            int columnIndexValue = table().getColumnIndex(VALUE);
            for (int row = table().getTitleRowCount(); row < table().getRowCount(); row++) {
                int iteration = InputFormatConverter.getInt(table().getValueAt(row, columnIndexIteration));
                double value = InputFormatConverter.getDouble(table().getValueAt(row, columnIndexValue));
                internalValueByIteration.put(iteration, value);
            }
        }
    }
}
