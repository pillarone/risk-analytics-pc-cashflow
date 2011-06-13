package models.gira

model=models.gira.GIRAModel
periodCount=1
displayName='segment'
applicationVersion='1.4-ALPHA-1.5'
periodLabels=["2011-01-01"]
components {
	claimsGenerators {
		subMarineAttritional {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subMarineSingle {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase.ABSOLUTE,"frequencyModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"produceClaim":org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType.SINGLE,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType.CONSTANT, [constant:5.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:100.0]),"frequencyIndices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
	}
	globalParameters {
		parmGenerateNewClaimsInFirstPeriodOnly[0]=true
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0)
	}
	reinsuranceContracts {
		subQuote {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"quotaShare":0.2,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.ALL, ["reserves":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.IncludeType.NOTINCLUDED,])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.MONTHS, ["startCover":new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0),"numberOfMonths":12,])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
	}
	segments {
		subMarine {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["marine attritional", "marine single"], [0.5, 0.7]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["marine"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
	}
	underwritingSegments {
		subMarine {
			parmPolicyIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmPremiumPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker, '')
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[100000.0], [10000.0], [50000.0], [500.0]]),["maximum sum insured","average sum insured","premium","number of policies"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DOUBLE'))
		}
	}
}
comments=[]
