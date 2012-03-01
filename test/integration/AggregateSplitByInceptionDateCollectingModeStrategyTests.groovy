import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.output.CollectorMapping
import models.gira.GIRAModel
import org.pillarone.riskanalytics.domain.pc.cf.output.AggregateSplitPerSourceCollectingModeStrategy
import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateSplitByInceptionDateCollectingModeStrategyTests extends ModelTest {

    Class getModelClass() {
        GIRAModel
    }

    String getResultConfigurationFileName() {
        'TestGIRACYClaimUwResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'CY Split for Claims and Underwriting'
    }

    String getParameterFileName() {
        'TestGIRAInceptionPeriodsParameters'
    }

    String getParameterDisplayName() {
        'Inception Periods'
    }

    protected ICollectorOutputStrategy getOutputStrategy() {
        new DBOutput()
    }

    int getIterationCount() {
        1
    }

    void setUp() {
        super.setUp()
        assertNotNull new CollectorMapping(collectorName: AggregateSplitPerSourceCollectingModeStrategy.IDENTIFIER).save()
    }

    void postSimulationEvaluation() {
        correctPaths()
        correctFields(['developedResultIndexed', 'appliedIndexValue', 'IBNRIndexed', 'reservesIndexed', 'outstandingIndexed',
                'paidIncrementalIndexed', 'reportedIncrementalIndexed', 'ultimate', 'premiumWritten', 'premiumPaid',])
        correctReportingClaimsResults()
        correctPaidClaimsResults()
        correctPremiumResults()
    }

    void correctPaths() {
        List<String> paths = [
                'GIRA:underwritingSegments:subMarine:outUnderwritingInfo',
                'GIRA:underwritingSegments:subMarine:period:2012:outUnderwritingInfo',
                'GIRA:underwritingSegments:subMarine:period:2013:outUnderwritingInfo',
                'GIRA:underwritingSegments:subMarine:period:2014:outUnderwritingInfo',
                'GIRA:underwritingSegments:subMarine:period:2015:outUnderwritingInfo',
                'GIRA:claimsGenerators:subMarine:outClaims',
                'GIRA:claimsGenerators:subMarine:period:2012:outClaims',
                'GIRA:claimsGenerators:subMarine:period:2013:outClaims',
                'GIRA:claimsGenerators:subMarine:period:2014:outClaims',
                'GIRA:claimsGenerators:subMarine:period:2015:outClaims'
        ]
        def collectedPaths = PathMapping.list()
        // -2 to ignore the subsubcomponents paths
        assertEquals '# of paths correct', paths.size(), collectedPaths.size() - 2

        for (int i = 0; i < collectedPaths.size(); i++) {
            if (collectedPaths[i].pathName.contains("subcomponents")) continue
//            def init = paths.contains(collectedPaths[i].pathName)
//            if (!paths.remove(collectedPaths[i].pathName)) {
//                println collectedPaths[i].pathName
//            }
            assertTrue "$i ${collectedPaths[i].pathName} found", paths.remove(collectedPaths[i].pathName)
        }

        assertTrue "all paths found $paths.size()", paths.size() == 0
    }

    void correctFields(List<String> fields) {
        def collectedFields = FieldMapping.list()
        assertEquals '# of fields correct', fields.size(), collectedFields.size()

        for (FieldMapping field : collectedFields) {
            assertTrue "${field.fieldName}", fields.remove(field.fieldName)
        }
        assertTrue 'all field found', fields.size() == 0
    }

    void correctReportingClaimsResults() {
        Map<PeriodPath, Double> resultsPerPath = new LinkedHashMap<PeriodPath, Double>()
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 0)]=-80000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 0)]=-80000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 1)]=-94000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 1)]=-10000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2013:outClaims', 1)]=-84000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 2)]=-103500
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 2)]=-5000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2013:outClaims', 2)]=-10500
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2014:outClaims', 2)]=-88000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 3)]=-113440.47619047621
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 3)]=-5000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2013:outClaims', 3)]=-5250
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2014:outClaims', 3)]=-11000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2015:outClaims', 3)]=-92190.4761904762

        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "reportedIncrementalIndexed") {
                assertEquals "${result.path.pathName}, P${result.period}", resultsPerPath.get(new PeriodPath(result)), result.value, 1E-8d
            }
        }
    }

    void correctPaidClaimsResults() {
        Map<PeriodPath, Double> resultsPerPath = new LinkedHashMap<PeriodPath, Double>()
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 0)]=-40000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 0)]=-40000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 1)]=-72000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 1)]=-30000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2013:outClaims', 1)]=-42000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 2)]=-95500
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 2)]=-20000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2013:outClaims', 2)]=-31500
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2014:outClaims', 2)]=-44000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:outClaims', 3)]=-110095.23809523808
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2012:outClaims', 3)]=-10000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2013:outClaims', 3)]=-21000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2014:outClaims', 3)]=-33000
        resultsPerPath[new PeriodPath('GIRA:claimsGenerators:subMarine:period:2015:outClaims', 3)]=-46095.23809523808

        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "paidIncrementalIndexed") {
                assertEquals "${result.path.pathName}, P${result.period}", resultsPerPath.get(new PeriodPath(result)), result.value, 1E-8d
            }
        }
    }

    void correctPremiumResults() {
        Map<String, Double> resultsPerPath = new LinkedHashMap<String, Double>()
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:outUnderwritingInfo', 0)]=100000
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:period:2012:outUnderwritingInfo', 0)]=100000
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:outUnderwritingInfo', 1)]=105000
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:period:2013:outUnderwritingInfo', 1)]=105000
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:outUnderwritingInfo', 2)]=110000
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:period:2014:outUnderwritingInfo', 2)]=110000
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:outUnderwritingInfo', 3)]=115238.09523809524
        resultsPerPath[new PeriodPath('GIRA:underwritingSegments:subMarine:period:2015:outUnderwritingInfo', 3)]=115238.09523809524

        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "premiumPaid") {
                assertEquals "${result.path.pathName}, P${result.period}", resultsPerPath.get(new PeriodPath(result)), result.value, 1E-8d
            }
        }
    }
    
    private class PeriodPath {
        String path
        int period

        PeriodPath(SingleValueResult result) {
            path = result.path
            period = result.period
        }

        PeriodPath(String path, int period) {
            this.path = path
            this.period = period
        }

        public int hashCode() {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCodeBuilder.append(path);
            hashCodeBuilder.append(period);
            return hashCodeBuilder.toHashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof PeriodPath) {
                    return ((PeriodPath) obj).path.equals(path) && ((PeriodPath) obj).period.equals(period)
            } else {
                return false;
            }
        }
    }
}
