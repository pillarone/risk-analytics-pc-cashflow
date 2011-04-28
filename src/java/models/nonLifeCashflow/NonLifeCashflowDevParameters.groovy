package models.nonLifeCashflow

import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

model=models.nonLifeCashflow.NonLifeCashflowModel
periodCount=1
displayName='Developed Claims'
applicationVersion='1.4-ALPHA-1.3'
periodLabels=["2011-01-01","2012-01-01","2013-01-01","2014-01-01","2015-01-01","2016-01-01"]
components {
	claimsGenerators {
		subAviation {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:1000.0, stDev:250.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subAviation')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subTrivial')
		}
		subMarine {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:800.0, stDev:200.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subMarine')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subMarine')
		}
		subMotorSingle {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase.ABSOLUTE,"frequencyModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.POISSON, [lambda:5.0]),"produceClaim":org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType.SINGLE,"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.PARETO, [alpha:2.5, beta:50000.0]),])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subMotorShort')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subMotorShort')
		}
		subProperty {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionType.LOGNORMAL, [mean:100.0, stDev:20.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subTrivial')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subTrivial')
		}
	}
	globalParameters {
		parmGenerateNewClaimsInFirstPeriodOnly[0]=true
	}
	patterns {
		subPayoutPatterns {
			subAviation {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.INCREMENTAL,
                        ["incrementalPattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24, 36, 48, 60], [0.2, 0.6, 0.1, 0.05, 0.025, 0.025]]),["Months","Increments"],
                                ConstraintsFactory.getConstraints('PATTERN')),])
			}
			subMarine {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE,
                        ["cumulativePattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24, 36, 48], [0.1, 0.7, 0.9, 0.98, 1.0]]),["Months","Cumulated"],
                                ConstraintsFactory.getConstraints('PATTERN')),])
			}
			subMotorShort {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.INCREMENTAL, ["incrementalPattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24], [0.0, 0.8, 0.2]]),["Months","Increments"], ConstraintsFactory.getConstraints('PATTERN')),])
			}
			subTrivial {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
			}
		}
		subReportingPatterns {
			subMarine {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.CUMULATIVE, ["cumulativePattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24, 36, 48], [0.5, 0.8, 0.9, 1.0, 1.0]]),["Months","Cumulated"], ConstraintsFactory.getConstraints('PATTERN')),])
			}
			subMotorShort {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.INCREMENTAL, ["incrementalPattern":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0, 12, 24], [0.4, 0.5, 0.1]]),["Months","Increments"], ConstraintsFactory.getConstraints('PATTERN')),])
			}
			subTrivial {
				parmPattern[0]=PatternStrategyType.getStrategy(PatternStrategyType.NONE, [:])
			}
		}
	}
}
comments=[]
