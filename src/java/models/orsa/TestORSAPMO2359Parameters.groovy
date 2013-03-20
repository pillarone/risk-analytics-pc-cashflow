package models.orsa

model=models.orsa.ORSAModel
periodCount=1
displayName='PMO-2359 Five Contracts'
applicationVersion='1.6-RC-3-kti'
periodLabels=["2012-01-01"]
components {
	claimsGenerators {
		subMarine {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"frequencyBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase.ABSOLUTE,"produceClaim":org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType.AGGREGATED_EVENT,"frequencyDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType.CONSTANT, [constant:1.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,"claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1000.0]),"frequencyIndices":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, 'subMarine')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, 'subMarine')
			parmRunOffIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
	}
	creditDefault {
		parmDefaultA[0]=5.0E-4
		parmDefaultAA[0]=1.0E-4
		parmDefaultAAA[0]=2.0E-5
		parmDefaultB[0]=0.04175
		parmDefaultBB[0]=0.012
		parmDefaultBBB[0]=0.0024
		parmDefaultC[0]=0.04175
		parmDefaultCC[0]=0.04175
		parmDefaultCCC[0]=0.04175
	}
	globalParameters {
		parmProjection[0]=org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.COMPLETEROLLOUT, [:])
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0)
		parmRunOffAfterFirstPeriod[0]=false
	}
	legalEntities {
		subA {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
			parmRecoveryPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker, '')
		}
		subB {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
			parmRecoveryPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker, '')
		}
		subC {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
			parmRecoveryPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker, '')
		}
		subD {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
			parmRecoveryPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker, '')
		}
		subE {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
			parmRecoveryPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IRecoveryPatternMarker, '')
		}
	}
	patterns {
		subPayoutAndReportingPatterns {
			subMarine {
				parmPattern[0]=org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.pattern.PayoutReportingCombinedPatternStrategyType.CUMULATIVE, ["cumulativePattern":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0], [1.0], [1.0]]),["Months","Cumulative Payout","Cumulative Reported"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PATTERN')),])
			}
		}
	}
	reinsuranceContracts {
		subCatXL {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.CXL, ["limit":1700.0,"stabilization":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType.NONE, [:]),"reinstatementPremiums":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reinstatement Premium"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DOUBLE')),"riPremiumSplit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType.PREMIUM_SHARES, [:]),"aggregateLimit":1700.0,"premium":0.0,"premiumBase":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.XLPremiumBase.ABSOLUTE,"aggregateDeductible":0.0,"attachmentPoint":300.0,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),"flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["", ""], ["subQuote3", "subQuote4"], ["", ""], ["", ""], ["", ""], ["ANY", "ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subE"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=false
		}
		subQuote1 {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"lossParticipation":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.NOPARTICIPATION, [:]),"commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"quotaShare":0.7,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),"flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""], [""], ["subA"], [""], [""], ["ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subB"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=false
		}
		subQuote2 {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"lossParticipation":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.NOPARTICIPATION, [:]),"commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"quotaShare":0.3,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),"flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""], [""], ["subA"], [""], [""], ["ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subC"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=false
		}
		subQuote3 {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"lossParticipation":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.NOPARTICIPATION, [:]),"commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"quotaShare":0.4,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),"flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""], ["subQuote1"], [""], [""], [""], ["ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subD"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=false
		}
		subQuote4 {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"lossParticipation":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.NOPARTICIPATION, [:]),"commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"quotaShare":0.1,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),"flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""], ["subQuote2"], [""], [""], [""], ["ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subD"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=false
		}
	}
	segments {
		subA {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMarine"], [1.0]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, 'subA')
			parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMarine"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
	}
	underwritingSegments {
		subMarine {
			parmPolicyIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmPremiumPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker, '')
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [1000.0], [0.0]]),["maximum sum insured","average sum insured","premium","number of policies"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DOUBLE'))
		}
	}
}
comments=["""[path:'ORSA', period:-1, lastChange:new org.joda.time.DateTime(1360315941371),user:null, comment: \"\"\"v2: null\"\"\", tags:(['version'] as Set)]"""]
tags=[]
