package models.gira

model=models.gira.GIRAModel
periodCount=1
displayName='Multi Company'
applicationVersion='1.4-ALPHA-6.2'
periodLabels=["2011-01-01"]
components {
	claimsGenerators {
		subMarine {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:1000.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subMotor {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:800.0]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.ABSOLUTE,])
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
	legalEntities {
		subMixedCesar {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
		}
		subPrimeAlpha {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
		}
		subPrimeBeta {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
		}
		subReDelta {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
		}
		subReEcho {
			parmRating[0]=org.pillarone.riskanalytics.domain.utils.constant.Rating.NO_DEFAULT
		}
	}
	reinsuranceContracts {
		subQuoteMarine {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"quotaShare":0.25,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.GROSSPERILS, ["perils":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["marine"]]),["Covered Perils"], org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker),])
			parmCoveredByReinsurers[0]=1.0
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["mixed cesar"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
		subQuoteMotor {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.NOCOMMISSION, [:]),"limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"quotaShare":0.4,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.GROSSLEGALENTITIES, ["legalEntities":new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["mixed cesar"]]),["Covered Legal Entities"], org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker),])
			parmCoveredByReinsurers[0]=1.0
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["re delta", "re echo"], [0.7, 0.3]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
	}
	segments {
		subMarineAlpha {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["marine"], [0.6]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, 'subMixedCesar')
			parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
		subMarineBeta {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["marine"], [0.4]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, 'subMixedCesar')
			parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
		subMotorCesar {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["motor"], [1.0]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, 'subMixedCesar')
			parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
	}
}
comments=[]
tags=["LOCKED"]
