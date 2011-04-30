package models.nonLifeCashflow

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IPolicyIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IPremiumIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ISeverityIndexMarker
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType

model=models.nonLifeCashflow.NonLifeCashflowModel
periodCount=1
displayName='Gross Portfolio'
applicationVersion='1.4-ALPHA-1.3'
periodLabels=["2011-01-01","2012-01-01","2013-01-01","2014-01-01","2015-01-01"]
components {
	claimsGenerators {
		subMotorAttritional {
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, 'sub5Years')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmSeverityIndex[0]=new ConstrainedString(ISeverityIndexMarker, 'subConstantIncrease')
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([[""]]),["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subMotorSingle {
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.NUMBER_OF_POLICIES,"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"frequencyDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1.0]),"produceClaim":FrequencySeverityClaimType.SINGLE,"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.PREMIUM_WRITTEN,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1.0]),])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, 'sub5Years')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmSeverityIndex[0]=new ConstrainedString(ISeverityIndexMarker, 'subConstantIncrease')
			parmUnderwritingInformation[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["motor"]]),["Underwriting Information"], IUnderwritingInfoMarker)
		}
	}
	globalParameters {
		parmGenerateNewClaimsInFirstPeriodOnly[0]=false
		parmProjectionStartDate[0]=new DateTime(2011, 1, 1, 0, 0, 0, 0)
	}
	indices {
		subFrequencyIndices {
			subHighVar {
				parmIndices[0]=IndexStrategyType.getStrategy(IndexStrategyType.STOCHASTIC, ["stDev":0.8,"mean":0.0,"startDate":new DateTime(2011, 1, 1, 0, 0, 0, 0),])
			}
		}
		subPolicyIndices {
			subAggressiveGrowth {
				parmIndices[0]=IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[new DateTime(2010,1,1,0,0,0,0), new DateTime(2011,1,1,0,0,0,0), new DateTime(2012,1,1,0,0,0,0), new DateTime(2013,1,1,0,0,0,0)], [0.06, 0.07, 0.08, 0.1]]),["Date","Change"], ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
		subPremiumIndices {
			subDeflation {
				parmIndices[0]=IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[new DateTime(2010,1,1,0,0,0,0), new DateTime(2011,1,1,0,0,0,0), new DateTime(2012,1,1,0,0,0,0), new DateTime(2013,1,1,0,0,0,0)], [0.0, -0.03, -0.05, -0.08]]),["Date","Change"], ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
		subSeverityIndices {
			subConstantIncrease {
				parmIndices[0]=IndexStrategyType.getStrategy(IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[new DateTime(2010,1,1,0,0,0,0), new DateTime(2011,1,1,0,0,0,0), new DateTime(2012,1,1,0,0,0,0), new DateTime(2013,1,1,0,0,0,0)], [0.02, 0.02, 0.02, 0.02]]),["Date","Change"], ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
	}
	patterns {
		subPayoutPatterns {
			sub5Years {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24, 36, 48], [0.2, 0.4, 0.6, 0.8, 1.0]]),["Months","Cumulated"], ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
	underwritingSegments {
		subMotor {
			parmPolicyIndex[0]=new ConstrainedString(IPolicyIndexMarker, 'subAggressiveGrowth')
			parmPremiumIndex[0]=new ConstrainedString(IPremiumIndexMarker, 'subDeflation')
			parmUnderwritingInformation[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [1000.0], [10.0]]),["maximum sum insured","average sum insured","premium","number of policies"], ConstraintsFactory.getConstraints('DOUBLE'))
		}
	}
}
comments=[]
