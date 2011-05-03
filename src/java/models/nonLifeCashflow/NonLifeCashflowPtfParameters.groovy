package models.nonLifeCashflow

model=models.nonLifeCashflow.NonLifeCashflowModel
periodCount=1
displayName='Gross Portfolio'
applicationVersion='1.4-ALPHA-1.3'
periodLabels=["2011-01-01","2012-01-01","2013-01-01","2014-01-01","2015-01-01"]
components {
	claimsGenerators {
		subMotorAttritional {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'sub5yConstantPayments')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmSeveritiesIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["constant increase"], ["STEPWISE_PREVIOUS"], ["START_OF_PROJECTION"]]),["Index","Mode","Base Date Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker)
		}
		subMotorSingle {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase.NUMBER_OF_POLICIES,"frequencyModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1.0]),"produceClaim":org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType.SINGLE,"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.PREMIUM_WRITTEN,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1.0]),"frequencyIndices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["growth"], ["CONTINUOUS"], ["START_OF_PROJECTION"]]),["Index","Mode","Base Date Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'sub5yConstantPayments')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmSeveritiesIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["constant increase"], ["STEPWISE_PREVIOUS"], ["START_OF_PROJECTION"]]),["Index","Mode","Base Date Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker)
		}
	}
	globalParameters {
		parmGenerateNewClaimsInFirstPeriodOnly[0]=false
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0)
	}
	indices {
//		subFrequencyIndices {
//			subCATIncrease {
//				parmIndices[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.STOCHASTIC, ["startDate":new org.joda.time.DateTime(2010, 1, 1, 0, 0, 0, 0),"distribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.LOGNORMAL, [mean:0.03, stDev:0.02]),])
//			}
//		}
		subPolicyIndices {
			subAggressiveGrowth {
				parmIndices[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2010,1,1,0,0,0,0), new org.joda.time.DateTime(2011,1,1,0,0,0,0), new org.joda.time.DateTime(2012,1,1,0,0,0,0), new org.joda.time.DateTime(2013,1,1,0,0,0,0)], [0.06, 0.07, 0.08, 0.1]]),["Date","Change"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
		subPremiumIndices {
			subDeflation {
				parmIndices[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2010,1,1,0,0,0,0), new org.joda.time.DateTime(2011,1,1,0,0,0,0), new org.joda.time.DateTime(2012,1,1,0,0,0,0), new org.joda.time.DateTime(2013,1,1,0,0,0,0)], [0.0, -0.03, -0.05, -0.08]]),["Date","Change"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
		subSeverityIndices {
			subConstantIncrease {
				parmIndices[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2010,1,1,0,0,0,0), new org.joda.time.DateTime(2011,1,1,0,0,0,0), new org.joda.time.DateTime(2012,1,1,0,0,0,0), new org.joda.time.DateTime(2013,1,1,0,0,0,0)], [0.02, 0.02, 0.02, 0.02]]),["Date","Change"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
	}
	patterns {
		subPayoutPatterns {
			sub5Years {
				parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 12, 24, 36, 48], [0.2, 0.4, 0.6, 0.8, 1.0]]),["Months","Cumulated"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
	underwritingSegments {
		subMotor {
			parmPoliciesIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["aggressive growth"], ["STEPWISE_PREVIOUS"], ["START_OF_PROJECTION"]]),["Index","Mode","Base Date Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["deflation"], ["STEPWISE_PREVIOUS"], ["START_OF_PROJECTION"]]),["Index","Mode","Base Date Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [1000.0], [10.0]]),["maximum sum insured","average sum insured","premium","number of policies"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DOUBLE'))
		}
	}
}
comments=[]
