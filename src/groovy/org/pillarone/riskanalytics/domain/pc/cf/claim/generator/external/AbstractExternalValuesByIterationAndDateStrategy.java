package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.core.simulation.engine.id.IIdGenerator;
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
abstract public class AbstractExternalValuesByIterationAndDateStrategy extends AbstractParameterObject implements IExternalValuesStrategy{

    public static final String ITERATION = "iteration";
    public static final String VALUE = "value";
    public static final String DATE = "date";

    abstract public ConstrainedMultiDimensionalParameter table();

    private ListMultimap<IterationPeriod, DateDouble> internalValueByIteration = ArrayListMultimap.create();

    /**
     *
     * @param baseClaims this list is needed in order to calculate the number of required claims after calculateDependantClaimsWithContractBase
     *                   has been executed i.e. if external severity information is provided and only the missing claim
     *                   number needs to be generated.
     * @param uwInfos
     * @param severityFactors
     * @param uwInfosFilterCriteria
     * @param periodScope
     * @param idGenerator
     * @return
     */
    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
                                          PeriodScope periodScope, ClaimType claimType, ExposureBase claimsSizeBase,
                                          IRandomNumberGenerator dateGenerator, int iteration, IIdGenerator idGenerator) {
        lazyInitializeDistributionMaps(periodScope);
        List<DateDouble> ultimates = internalValueByIteration.get(new IterationPeriod(iteration, periodScope.getCurrentPeriod()));
        int numberOfClaims = ultimates.size();
        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, claimsSizeBase, uwInfosFilterCriteria);
        List<EventPacket> events = ClaimsGeneratorUtils.generateEventsOrNull(claimType, numberOfClaims, periodScope, dateGenerator);
        LossesOccurringContractBase contractBase = new LossesOccurringContractBase();
        for (int i = 0; i < numberOfClaims; i++) {
            EventPacket event = events == null ? null : events.get(i);
            // todo(sku): replace with information from underwriting
            DateTime exposureStartDate = contractBase.exposureStartDate(periodScope, dateGenerator);
            double ultimate = ultimates.get(i).value * -severityScalingFactor;
            DateTime occurrenceDate = ultimates.get(i).date;
            double scaleFactor = IndexUtils.aggregateFactor(severityFactors, exposureStartDate, periodScope.getPeriodCounter(), exposureStartDate);
            baseClaims.add(new ClaimRoot(ultimate * scaleFactor, claimType, exposureStartDate, occurrenceDate, idGenerator.nextValue(), event));
        }
        return baseClaims;
    }

    private void lazyInitializeDistributionMaps(PeriodScope periodScope) {
        if (internalValueByIteration.isEmpty()) {
            int columnIndexIteration = table().getColumnIndex(ITERATION);
            int columnIndexDate = table().getColumnIndex(DATE);
            int columnIndexValue = table().getColumnIndex(VALUE);
            for (int row = table().getTitleRowCount(); row < table().getRowCount(); row++) {
                int iteration = InputFormatConverter.getInt(table().getValueAt(row, columnIndexIteration));
                double value = InputFormatConverter.getDouble(table().getValueAt(row, columnIndexValue));
                DateTime date = (DateTime) table().getValueAt(row, columnIndexDate);
                int period = periodScope.getPeriodCounter().belongsToPeriod(date);
                internalValueByIteration.put(new IterationPeriod(iteration, period), new DateDouble(date, value));
            }
        }
    }

    private class IterationPeriod {
        int iteration;
        int period;

        private IterationPeriod(int iteration, int period) {
            this.iteration = iteration;
            this.period = period;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IterationPeriod that = (IterationPeriod) o;

            if (iteration != that.iteration) return false;
            if (period != that.period) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = iteration;
            result = 31 * result + period;
            return result;
        }
    }

    private class DateDouble {
        DateTime date;
        double value;

        private DateDouble(DateTime date, double value) {
            this.date = date;
            this.value = value;
        }
    }
}
