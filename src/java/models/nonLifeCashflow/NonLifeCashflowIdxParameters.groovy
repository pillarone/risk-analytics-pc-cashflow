package models.nonLifeCashflow

model=models.nonLifeCashflow.NonLifeCashflowModel
periodCount=1
displayName='Index and Pattern'
applicationVersion='1.4-ALPHA-1.3'
periodLabels=["2011-01-01","2012-01-01","2013-01-01","2014-01-01","2015-01-01"]
components {
	claimsGenerators {
		subMarine {
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subRegular')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmSeveritiesIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["inflation rally"], ["STEPWISE_PREVIOUS"], ["START_OF_PROJECTION"]]),["Index","Mode","Base Date Mode"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.pc.cf.exposure.IUnderwritingInfoMarker)
		}
	}
	globalParameters {
		parmGenerateNewClaimsInFirstPeriodOnly[0]=true
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0)
	}
	indices {
		subSeverityIndices {
			subInflationRally {
				parmIndices[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.DETERMINISTICANNUALCHANGE, ["indices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[new org.joda.time.DateTime(2011,1,1,0,0,0,0), new org.joda.time.DateTime(2012,1,1,0,0,0,0), new org.joda.time.DateTime(2013,1,1,0,0,0,0), new org.joda.time.DateTime(2014,1,1,0,0,0,0), new org.joda.time.DateTime(2015,1,1,0,0,0,0)], [0.04, 0.02, 0.05, 0.03, 0.1]]),["Date","Change"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('INDEX')),])
			}
		}
	}
	patterns {
		subPayoutPatterns {
			subRegular {
				parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternStrategyType.INCREMENTAL, ["incrementalPattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0, 12, 24, 36, 48], [0.2, 0.2, 0.2, 0.2, 0.2]]),["Months","Increments"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
}
comments=[]
