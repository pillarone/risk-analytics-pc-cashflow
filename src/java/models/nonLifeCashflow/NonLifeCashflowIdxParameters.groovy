package models.nonLifeCashflow

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ISeverityIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType

model=models.nonLifeCashflow.NonLifeCashflowModel
periodCount=1
displayName='Index and Pattern'
applicationVersion='1.4-ALPHA-1.3'
periodLabels=["2011-01-01","2012-01-01","2013-01-01","2014-01-01","2015-01-01"]
components {
	claimsGenerators {
		subMarine {
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, 'subRegular')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmSeverityIndex[0]=new ConstrainedString(ISeverityIndexMarker, 'subInflationRally')
		}
	}
	globalParameters {
		parmGenerateNewClaimsInFirstPeriodOnly[0]=true
	}
	indices {
		subSeverityIndices {
			subInflationRally {
				parmIndices[0]=IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[new DateTime(2011,1,1,0,0,0,0), new DateTime(2012,1,1,0,0,0,0), new DateTime(2013,1,1,0,0,0,0), new DateTime(2014,1,1,0,0,0,0), new DateTime(2015,1,1,0,0,0,0)], [0.04, 0.02, 0.05, 0.03, 0.1]]),["Date","Change"], ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
	}
	patterns {
		subPayoutPatterns {
			subRegular {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.INCREMENTAL, ["incrementalPattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24, 36, 48], [0.2, 0.2, 0.2, 0.2, 0.2]]),["Months","Increments"], ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
}
comments=[]
