package models.gira

import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stabilization.StabilizationStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.FilterStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.XLPremiumBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.cf.exposure.FrequencyBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.FrequencySeverityClaimType
import org.pillarone.riskanalytics.domain.utils.math.distribution.FrequencyDistributionType
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase

model=models.gira.GIRAModel
periodCount=1
displayName='Split per Source Test Parameter'
applicationVersion='1.4-BETA-8'
periodLabels=["2011-01-01"]
components {
	claimsGenerators {
		subMotorHullAttritional {
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:100.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, '')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmRunOffIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"]]),["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subMotorHullSingle {
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.SINGLE,"frequencyDistribution":FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant:1.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:1000.0]),"frequencyIndices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, '')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmRunOffIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"]]),["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subPropertyAttritional {
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:200.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, '')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmRunOffIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subPropertyEarthquake {
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.AGGREGATED_EVENT,"frequencyDistribution":FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant:1.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:500.0]),"frequencyIndices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, '')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmRunOffIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
		}
		subPropertySingle {
			parmAssociateExposureInfo[0]=RiskAllocatorType.getStrategy(RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=ClaimsGeneratorType.getStrategy(ClaimsGeneratorType.FREQUENCY_SEVERITY, ["frequencyBase":FrequencyBase.ABSOLUTE,"frequencyModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"produceClaim":FrequencySeverityClaimType.SINGLE,"frequencyDistribution":FrequencyDistributionType.getStrategy(FrequencyDistributionType.CONSTANT, [constant:1.0]),"claimsSizeModification":DistributionModifier.getStrategy(DistributionModifier.NONE, [:]),"claimsSizeBase":ExposureBase.ABSOLUTE,"claimsSizeDistribution":DistributionType.getStrategy(DistributionType.CONSTANT, [constant:900.0]),"frequencyIndices":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('FREQUENCY_INDEX_SELECTION')),])
			parmPayoutPattern[0]=new ConstrainedString(IPayoutPatternMarker, '')
			parmReportingPattern[0]=new ConstrainedString(IReportingPatternMarker, '')
			parmRunOffIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Underwriting Information"], IUnderwritingInfoMarker)
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
		parmProjection[0]=ProjectionPeriodType.getStrategy(ProjectionPeriodType.COMPLETEROLLOUT, [:])
		parmProjectionStartDate[0]=new DateTime(2011, 1, 1, 0, 0, 0, 0)
		parmRunOffAfterFirstPeriod[0]=true
	}
	reinsuranceContracts {
		subMotorHullWxl {
			parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.WXL, ["limit":200.0,"stabilization":StabilizationStrategyType.getStrategy(StabilizationStrategyType.NONE, [:]),"reinstatementPremiums":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reinstatement Premium"],ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER)),"riPremiumSplit":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"aggregateLimit":200.0,"premium":200.0,"premiumBase":XLPremiumBase.ABSOLUTE,"aggregateDeductible":0.0,"attachmentPoint":500.0,])
			parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS, ["segments":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"]]),["Segments"], ISegmentMarker),]),])
			parmCoveredPeriod[0]=PeriodStrategyType.getStrategy(PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
		subPropertyCxl {
			parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.CXL, ["limit":50.0,"stabilization":StabilizationStrategyType.getStrategy(StabilizationStrategyType.NONE, [:]),"reinstatementPremiums":new TableMultiDimensionalParameter(GroovyUtils.toList([[0.0]]),["Reinstatement Premium"]),"riPremiumSplit":PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),"aggregateLimit":50.0,"premium":100.0,"premiumBase":XLPremiumBase.ABSOLUTE,"aggregateDeductible":0.0,"attachmentPoint":300.0,])
			parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.CONTRACTS, ["contracts":new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subPropertyQuotaShare"], ["NET"]]),["Covered Contracts","Based On"], ConstraintsFactory.getConstraints('RI_CONTRACT_BASEDON')),"filter":FilterStrategyType.getStrategy(FilterStrategyType.ALL, [:]),])
			parmCoveredPeriod[0]=PeriodStrategyType.getStrategy(PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
		subPropertyQuotaShare {
			parmContractStrategy[0]=ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, ["commission":CommissionStrategyType.getStrategy(CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.1,]),"limit":LimitStrategyType.getStrategy(LimitStrategyType.NONE, [:]),"quotaShare":0.2,])
			parmCover[0]=CoverAttributeStrategyType.getStrategy(CoverAttributeStrategyType.ORIGINALCLAIMS, ["filter":FilterStrategyType.getStrategy(FilterStrategyType.SEGMENTS, ["segments":new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([["subProperty"]]),["Segments"], ISegmentMarker),]),])
			parmCoveredPeriod[0]=PeriodStrategyType.getStrategy(PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reinsurer","Covered Portion"], ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
		}
	}
	segments {
		subMotorHull {
			parmClaimsPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subMotorHullAttritional", "subMotorHullSingle"], [1.0, 1.0]]),["Claims Generator","Portion"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new ConstrainedString(ILegalEntityMarker, '')
			parmDiscounting[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reserves Generator","Portion"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subMotorHull"], [1.0]]),["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
		subProperty {
			parmClaimsPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subPropertyAttritional", "subPropertyEarthquake", "subPropertySingle"], [1.0, 1.0, 1.0]]),["Claims Generator","Portion"], ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new ConstrainedString(ILegalEntityMarker, '')
			parmDiscounting[0]=new ComboBoxTableMultiDimensionalParameter(GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Reserves Generator","Portion"], ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([["subProperty"], [1.0]]),["Underwriting","Portion"], ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
	}
	underwritingSegments {
		subMotorHull {
			parmPolicyIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmPremiumPattern[0]=new ConstrainedString(IPremiumPatternMarker, '')
			parmUnderwritingInformation[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [8000.0], [1000.0]]),["maximum sum insured","average sum insured","premium","number of policies"], ConstraintsFactory.getConstraints('DOUBLE'))
		}
		subProperty {
			parmPolicyIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmPremiumPattern[0]=new ConstrainedString(IPremiumPatternMarker, '')
			parmUnderwritingInformation[0]=new ConstrainedMultiDimensionalParameter(GroovyUtils.toList([[0.0], [0.0], [6000.0], [10.0]]),["maximum sum insured","average sum insured","premium","number of policies"], ConstraintsFactory.getConstraints('DOUBLE'))
		}
	}
}
comments=[]
tags=["LOCKED"]
