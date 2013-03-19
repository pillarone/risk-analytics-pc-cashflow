import org.pillarone.riskanalytics.core.simulation.engine.ModelTest
import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.SingleValueResult
import org.pillarone.riskanalytics.core.output.TestDBOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import models.gira.GIRAModel
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class AggregateSplitPerSourceCollectingModeStrategyTests extends ModelTest {

    Class getModelClass() {
        GIRAModel
    }

    String getResultConfigurationFileName() {
        'TestGIRASplitPerSourceResultConfiguration'
    }

    String getResultConfigurationDisplayName() {
        'Split per Source'
    }

    String getParameterFileName() {
        'TestGIRASplitPerSourceParameters'
    }

    String getParameterDisplayName() {
        'Split per Source Test Parameter'
    }

    protected ICollectorOutputStrategy getOutputStrategy() {
        new TestDBOutput()
    }

    int getIterationCount() {
        1
    }

    void postSimulationEvaluation() {
        correctPaths()
        correctFields(['totalIncrementalIndexed', 'IBNRIndexed', 'reservesIndexed', 'outstandingIndexed',
                'paidIncrementalIndexed', 'reportedIncrementalIndexed', 'ultimate', 'premiumWritten', 'premiumPaid',
                ClaimCashflowPacket.CHANGES_IN_OUTSTANDING_INDEXED, ClaimCashflowPacket.REPORTED_CUMULATIVE_INDEXED,
                ClaimCashflowPacket.PAID_CUMULATIVE_INDEXED, ClaimCashflowPacket.TOTAL_CUMULATIVE_INDEXED,
                ClaimCashflowPacket.RESERVE_RISK_BASE, ClaimCashflowPacket.PREMIUM_AND_RESERVE_RISK_BASE,
                ClaimCashflowPacket.PREMIUM_RISK_BASE])
        correctUltimateClaimsResults()
        correctCommissionsResults()
        correctPremiumResults()
    }

    void correctPaths() {
        def collectedPaths = PathMapping.list()     //.sort{ it.pathName }
        List<String> paths = [
                'GIRA:segments:outClaimsGross',
                'GIRA:segments:outClaimsCeded',
                'GIRA:segments:outClaimsNet',
                'GIRA:segments:outUnderwritingInfoGross',
                'GIRA:segments:outUnderwritingInfoCeded',
                'GIRA:segments:outUnderwritingInfoNet',
                'GIRA:segments:outDiscountedValues',
                'GIRA:segments:outNetPresentValues',

                'GIRA:segments:subMotorHull:outClaimsGross',
                'GIRA:segments:subMotorHull:outClaimsCeded',
                'GIRA:segments:subMotorHull:outClaimsNet',
                'GIRA:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'GIRA:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'GIRA:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'GIRA:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'GIRA:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsNet',
                'GIRA:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsNet',

                'GIRA:segments:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'GIRA:segments:subMotorHull:outUnderwritingInfoGross',
                'GIRA:segments:subMotorHull:outUnderwritingInfoCeded',
                'GIRA:segments:subMotorHull:outUnderwritingInfoNet',
                'GIRA:segments:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsCeded',
                'GIRA:segments:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsNet',

                'GIRA:segments:subProperty:outClaimsGross',
                'GIRA:segments:subProperty:outClaimsCeded',
                'GIRA:segments:subProperty:outClaimsNet',
                'GIRA:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'GIRA:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'GIRA:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'GIRA:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'GIRA:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsGross',
                'GIRA:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded',
                'GIRA:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'GIRA:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'GIRA:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsNet',
                'GIRA:segments:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'GIRA:segments:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsCeded',
                'GIRA:segments:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsNet',

                'GIRA:segments:subProperty:outUnderwritingInfoGross',
                'GIRA:segments:subProperty:outUnderwritingInfoCeded',
                'GIRA:segments:subProperty:outUnderwritingInfoNet',
                'GIRA:segments:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'GIRA:segments:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',

                'GIRA:reinsuranceContracts:subMotorHullWxl:outClaimsGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:outClaimsCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:outClaimsNet',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outClaimsGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outClaimsCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outClaimsNet',
                'GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsNet',
                'GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsNet',

                'GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoNet',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoNet',

                'GIRA:reinsuranceContracts:subPropertyCxl:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outClaimsNet',

                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsNet',


                'GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoNet',

                'GIRA:reinsuranceContracts:subPropertyQuotaShare:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsNet',

                'GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoNet',

                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsNet',
                'GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsNet',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsNet',
                'GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsNet'
        ]
        // on the KTI branch paths are prepared before simulation starts in a generic way, therefore there are more than on the master
        assertTrue '# of paths correct', paths.size() < collectedPaths.size()

        for (int i = 0; i < collectedPaths.size(); i++) {
            if (collectedPaths[i].pathName.contains("subcomponents")) continue
            def init = paths.contains(collectedPaths[i].pathName)

            if (!paths.remove(collectedPaths[i].pathName)) {
                println "additionally collected path ${collectedPaths[i].pathName}"
            }
        }

        assertTrue "all paths found $paths.size()", paths.size() == 0
    }

    void correctFields(List<String> fields) {
        def collectedFields = FieldMapping.list()
        // on the KTI branch fields are prepared before simulation starts in a generic way, therefore there are more than on the master
        assertTrue '# of fields correct', fields.size() < collectedFields.size()

        for (FieldMapping field : collectedFields) {
            if (!fields.remove(field.fieldName)) {
                println "additionally collected field ${field.fieldName}"
            }
        }
        assertTrue 'all field found', fields.size() == 0
    }

    void correctUltimateClaimsResults() {
        Map<String, Double> resultsPerPath = new LinkedHashMap<String, Double>()
//        resultsPerPath['GIRA:segments:outClaimsGross']=-2700d
//        resultsPerPath['GIRA:segments:outClaimsCeded']=570d
//        resultsPerPath['GIRA:segments:outClaimsNet']=-2130d
        resultsPerPath['GIRA:segments:subMotorHull:outClaimsGross']=-1100d
        resultsPerPath['GIRA:segments:subMotorHull:outClaimsCeded']=200d
        resultsPerPath['GIRA:segments:subMotorHull:outClaimsNet']=-900d
        resultsPerPath['GIRA:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsGross']=-100d
        resultsPerPath['GIRA:segments:subMotorHull:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=0d
        resultsPerPath['GIRA:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsCeded']=200d
        resultsPerPath['GIRA:segments:subMotorHull:claimsGenerators:subMotorHullSingle:outClaimsGross']=-1000d
        resultsPerPath['GIRA:segments:subMotorHull:reinsuranceContracts:subMotorHullWxl:outClaimsCeded']=200d
        resultsPerPath['GIRA:segments:subProperty:outClaimsGross']=-1600d
        resultsPerPath['GIRA:segments:subProperty:outClaimsCeded']=370d
        resultsPerPath['GIRA:segments:subProperty:outClaimsNet']=-1230d
        resultsPerPath['GIRA:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsGross']=-200d
        resultsPerPath['GIRA:segments:subProperty:claimsGenerators:subPropertyAttritional:outClaimsCeded']=40d
        resultsPerPath['GIRA:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsGross']=-500d
        resultsPerPath['GIRA:segments:subProperty:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=150d
        resultsPerPath['GIRA:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsGross']=-900d
        resultsPerPath['GIRA:segments:subProperty:claimsGenerators:subPropertySingle:outClaimsCeded']=180d
        resultsPerPath['GIRA:segments:subProperty:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=320d
        resultsPerPath['GIRA:segments:subProperty:reinsuranceContracts:subPropertyCxl:outClaimsCeded']=50d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outClaimsGross']=-1100d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outClaimsCeded']=200d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outClaimsNet']=-900d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outClaimsGross']=-1100d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outClaimsCeded']=200d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outClaimsNet']=-900d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsGross']=-1000d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsCeded']=200d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullSingle:outClaimsNet']=-800d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsGross']=-100d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsCeded']=0d
        resultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:claimsGenerators:subMotorHullAttritional:outClaimsNet']=-100d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outClaimsGross']=-1280d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outClaimsCeded']=50d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outClaimsNet']=-1230d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outClaimsGross']=-1280d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outClaimsCeded']=50d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outClaimsNet']=-1230d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsGross']=-400d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=50d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyEarthquake:outClaimsNet']=-350d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsGross']=-720d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsCeded']=0d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertySingle:outClaimsNet']=-720d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsGross']=-160d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsCeded']=0d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:claimsGenerators:subPropertyAttritional:outClaimsNet']=-160d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outClaimsGross']=-1600d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outClaimsCeded']=320d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outClaimsNet']=-1280d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outClaimsGross']=-1600d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outClaimsCeded']=320d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outClaimsNet']=-1280d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsGross']=-500d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsCeded']=100d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyEarthquake:outClaimsNet']=-400d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsGross']=-900d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsCeded']=180d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertySingle:outClaimsNet']=-720d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsGross']=-200d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsCeded']=40d
        resultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:claimsGenerators:subPropertyAttritional:outClaimsNet']=-160d


        Map<String, Double> collectedResultsPerPath = new HashMap<String, Double>()
        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "ultimate") {
//                println "${result.path.pathName} = ${result.value}"
                collectedResultsPerPath[result.path.pathName] = result.value
            }
        }
        for (Map.Entry<String, Double> result : resultsPerPath.entrySet()) {
//            re-enable for debugging
//            if (result.value != collectedResultsPerPath.get(result.key) && collectedResultsPerPath.get(result.key) != null) {
//                println "$result.key ultimate claims ${result.value} ${collectedResultsPerPath.get(result.key)}"
//            }
//            else if (collectedResultsPerPath.get(result.key) == null) {
//                println "missing $result.key"
//            }
            assertEquals "$result.key claims ultimate", result.value, collectedResultsPerPath.get(result.key), 1E-8d
        }
    }

    void correctCommissionsResults() {
        Map<String, Double> expectedResultsPerPath = new LinkedHashMap<String, Double>()

        expectedResultsPerPath['GIRA:segments:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:segments:subMotorHull:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:segments:subProperty:outUnderwritingInfoCeded']=120d               //
        expectedResultsPerPath['GIRA:segments:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=120d
        expectedResultsPerPath['GIRA:segments:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoCeded']=0d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=120d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoCeded']=120d

        Map<String, Double> collectedResultsPerPath = new HashMap<String, Double>()
        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "commission") {
                collectedResultsPerPath[result.path.pathName] = result.value
            }
        }

        for (Map.Entry<String, Double> result : expectedResultsPerPath.entrySet()) {
//            re-enable for debugging
            if (result.value != collectedResultsPerPath.get(result.key) && collectedResultsPerPath.get(result.key) != null) {
                println "$result.key commission ${result.value} ${collectedResultsPerPath.get(result.key)}"
            }
            else if (collectedResultsPerPath.get(result.key) == null) {
                println "missing commission $result.key"
            }
//            assertEquals "$result.key commission", result.value, collectedResultsPerPath.get(result.key), 1E-14
        }
    }

    void correctPremiumResults() {
        Map<String, Double> expectedResultsPerPath = new LinkedHashMap<String, Double>()
        expectedResultsPerPath['GIRA:segments:subMotorHull:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=-200d
        expectedResultsPerPath['GIRA:segments:subMotorHull:outUnderwritingInfoGross']=8000d
        expectedResultsPerPath['GIRA:segments:subMotorHull:outUnderwritingInfoCeded']=-200d
        expectedResultsPerPath['GIRA:segments:subMotorHull:outUnderwritingInfoNet']=7800d
        expectedResultsPerPath['GIRA:segments:subProperty:outUnderwritingInfoGross']=6000d
        expectedResultsPerPath['GIRA:segments:subProperty:outUnderwritingInfoCeded']=-1300d
        expectedResultsPerPath['GIRA:segments:subProperty:outUnderwritingInfoNet']=4700d
        expectedResultsPerPath['GIRA:segments:subProperty:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=-1200d
        expectedResultsPerPath['GIRA:segments:subProperty:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=-100d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoCeded']=-200d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoGross']=8000d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:outUnderwritingInfoNet']=7800d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoGross']=8000d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoCeded']=-200d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subMotorHullWxl:segments:subMotorHull:outUnderwritingInfoNet']=7800d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoGross']=4800d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoCeded']=-100d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:outUnderwritingInfoNet']=4700d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoGross']=4800d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoCeded']=-100d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyCxl:segments:subProperty:outUnderwritingInfoNet']=4700d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoGross']=6000d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoCeded']=-1200d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:outUnderwritingInfoNet']=4800d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoGross']=6000d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoCeded']=-1200d
        expectedResultsPerPath['GIRA:reinsuranceContracts:subPropertyQuotaShare:segments:subProperty:outUnderwritingInfoNet']=4800d

        Map<String, Double> collectedResultsPerPath = new HashMap<String, Double>()
        def results = SingleValueResult.list()
        for (SingleValueResult result : results) {
            if (result.field.fieldName == "premiumWritten") {
                collectedResultsPerPath[result.path.pathName] = result.value
            }
        }

        for (Map.Entry<String, Double> result : expectedResultsPerPath.entrySet()) {
//            re-enable for debugging
            if (result.value != collectedResultsPerPath.get(result.key) && collectedResultsPerPath.get(result.key) != null) {
                println "$result.key premiumWritten ${result.value} ${collectedResultsPerPath.get(result.key)}"
            }
            else if (collectedResultsPerPath.get(result.key) == null) {
                println "missing premiumWritten $result.key"
            }
//            assertEquals "$result.key premiumWritten", result.value, collectedResultsPerPath.get(result.key)
        }
    }
}
