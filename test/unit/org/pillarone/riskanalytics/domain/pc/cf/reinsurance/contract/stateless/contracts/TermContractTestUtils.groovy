package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.contracts;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory;
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.AdditionalPremiumConstraints;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.LayerConstraints;

import java.util.List
import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import com.google.common.collect.ListMultimap
import org.pillarone.riskanalytics.domain.test.SpreadsheetImporter
import com.google.common.collect.ArrayListMultimap
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;

public class TermContractTestUtils {

    public static TermReinsuranceContract getLayerContract(Double termExcess, Double termLimit, List<Integer> periods,
                                                List<Integer> layers, List<Double> shares,
                                                List<Double> periodExcess, List<Double> periodLimit,
                                                List<Double> claimExcess, List<Double> claimLimit, DateTime beginOfCover) {
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(beginOfCover, 3)
        int numberOfLayers = layers.size()
        return new TermReinsuranceContract(
                parmContractStructure: TemplateContractType.getStrategy(
                        TemplateContractType.NONPROPORTIONAL,
                        [
                                'termLimit': termLimit,
                                'termExcess': termExcess,
                                'termAP': new ConstrainedMultiDimensionalParameter(
                                        [[0d], [0d], [0d]], AdditionalPremiumConstraints.columnHeaders,
                                        ConstraintsFactory.getConstraints(AdditionalPremiumConstraints.IDENTIFIER)
                                ),
                                'structure': new ConstrainedMultiDimensionalParameter(
                                        [periods, layers, shares, periodLimit, periodExcess, claimLimit, claimExcess,
                                                [0d] * numberOfLayers, ['PREMIUM'] * numberOfLayers],
                                        LayerConstraints.columnHeaders, ConstraintsFactory.getConstraints(LayerConstraints.IDENTIFIER)
                                )
                        ]
                ),
                iterationScope: iterationScope,
                periodStore: iterationScope.periodStores[0])
    }

    public static PatternPacket trivialPayoutPattern = new PatternPacket.TrivialPattern(IPayoutPatternMarker.class);
    public static PatternPacket trivialReportingPattern = new PatternPacket.TrivialPattern(IReportingPatternMarker.class);


    public static TermReinsuranceContract getOneLayerContract(double claimExcess, double claimLimit, DateTime beginOfCover) {
        getLayerContract(0, 10000000000, [1], [0], [1], [0], [0], [claimExcess], [claimLimit], beginOfCover)
    }

    public static TermReinsuranceContract getLayerContract(Double termExcess, Double termLimit, TestLayers layers, DateTime beginOfCover) {
        return getLayerContract(termExcess, termLimit, layers.periods, layers.layers, layers.shares, layers.periodExcess,
                layers.periodLimit, layers.claimExcess, layers.claimLimits, beginOfCover)
    }

    public static Double paidSumOfCalendarYear(List<ClaimCashflowPacket> claims, int calendarYear) {
        List<ClaimCashflowPacket> calendarYearClaims = []
        for (ClaimCashflowPacket claim : claims) {
            if (claim.occurrenceDate.year == calendarYear) {
                calendarYearClaims << claim
            }
        }
        if (calendarYearClaims.size() > 0) {
            return (Double) calendarYearClaims.paidIncrementalIndexed.sum()
        }
        else {
            return 0d
        }
    }

    public static List<GrossClaimRoot> getClaimRoots(List<Double> ultimates, DateTime occurrenceDate, PatternPacket payoutPattern) {
        List<GrossClaimRoot> claimRoots = []
        for (int i = 0; i < ultimates.size(); i++) {
            if (ultimates.get(i) != 0) {
                claimRoots << new GrossClaimRoot(ultimates.get(i), ClaimType.ATTRITIONAL, occurrenceDate, occurrenceDate,
                        payoutPattern, TermContractTestUtils.trivialReportingPattern)
            }
        }
        claimRoots
    }

    public static ListMultimap<Integer, Double> getUltimatesByPeriod(SpreadsheetImporter importer, String sheet) {
        ListMultimap<Integer, Double> ultimatesPerPeriod = ArrayListMultimap.create()
        for (int row = 8; row < 14; row++) {
            Map claim = importer.cells([
                    sheet: sheet, //startRow: row, // startRow counting starts at 0, first line with content
                    cellMap: ["C$row": 'ultimateP0', "D$row": 'ultimateP1', "E$row": 'ultimateP2']])
            for (int period = 0; period < 3; period++) {
                Double ultimate = (Double) claim["ultimateP$period"]
                if (ultimate != null) {
                    // invert ultimate sign as claim have a negative sign in pc-cashflow plugin
                    ultimatesPerPeriod.put(period, ultimate)
                }
            }
        }
        ultimatesPerPeriod
    }

    public static class TestLayerPeriodContractParams {
        int layer
        int period
        double share
        double claimLimit
        double claimExcess
        double periodLimit
        double periodExcess

        public TestLayerPeriodContractParams(int layer, int period, Map<String, Double> params) {
            this.layer = layer
            this.period = period
            share = params['share'] ? params['share'] : 0d
            claimLimit = params['claimLimit'] ? params['claimLimit'] : 0d
            claimExcess = params['claimExcess'] ? params['claimExcess'] : 0d
            periodLimit = params['periodLimit'] ? params['periodLimit'] : 0d
            periodExcess = params['periodExcess'] ? params['periodExcess'] : 0d
        }

        /**
         * as soon as there is a positive share this contract is not trivial. If all limits are zero it will work as a simple quote
         * @return
         */
        boolean notTrivial() {
            share > 0
        }
    }

    public static class TestLayers {
        Map<TestLayerPeriod, TestLayerPeriodContractParams> layerPeriodContractParams = [:]

        List<Integer> layers = []
        List<Integer> periods = []
        List<Double> shares = []
        List<Double> claimLimits = []
        List<Double> claimExcess = []
        List<Double> periodLimit = []
        List<Double> periodExcess = []
        int maxPeriod = 0

        void add(TestLayerPeriodContractParams params) {
            if (params.notTrivial()) {
                layerPeriodContractParams.put(new TestLayerPeriod(params.layer, params.period), params)
            }
        }

        void fillListMembers() {
            for (int layer = 0; layer < 2; layer++) {
                for (int period = 1; period < 4; period++) {
                    TestLayerPeriodContractParams layerPeriodParams = layerPeriodContractParams.get(new TermContractTestUtils.TestLayerPeriod(layer, period))
                    if (layerPeriodParams != null && layerPeriodParams.notTrivial()) {
                        layers << layer
                        periods << period
                        maxPeriod = Math.max(maxPeriod, period)
                        shares << layerPeriodParams.share
                        claimLimits << layerPeriodParams.claimLimit
                        claimExcess << layerPeriodParams.claimExcess
                        periodLimit << layerPeriodParams.periodLimit
                        periodExcess << layerPeriodParams.periodExcess
                    }
                }
            }
            maxPeriod-- // as counting on UI starts with 1 and is mapped internally to 0
        }
    }

    public static class TestLayerPeriod {
        int layer
        // UI period numbering starting with 1
        int period

        public TestLayerPeriod(int layer, int period) {
            this.layer = layer
            this.period = period
        }

        @Override
        boolean equals(Object obj) {
            if (obj instanceof TestLayerPeriod) {
                return layer == obj?.layer && period == obj?.period
            }
            false
        }

        @Override
        int hashCode() {
            HashCodeBuilder builder = new HashCodeBuilder()
            builder.append(layer)
            builder.append(period)
            builder.toHashCode()
        }
    }
}