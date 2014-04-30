package models.orsa

model=models.orsa.ORSAModel
periodCount=1
displayName='PMO233 Test 2 - SL Abs Param'
applicationVersion='1.6.1'
periodLabels=["2012-01-01"]
components {
	claimsGenerators {
		subFire2B {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:0.9]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.PREMIUM_WRITTEN,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmRunOffIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subB"], ["CONTINUOUS"], ["START_OF_PROJECTION"], [new org.joda.time.DateTime(2013,1,1,0,0,0,0)]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subFire"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
		}
		subMotor1A {
			parmAssociateExposureInfo[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.allocation.RiskAllocatorType.NONE, [:])
			parmClaimsModel[0]=org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGeneratorType.ATTRITIONAL, ["claimsSizeDistribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.CONSTANT, [constant:0.9]),"claimsSizeModification":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier.NONE, [:]),"claimsSizeBase":org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase.PREMIUM_WRITTEN,])
			parmPayoutPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPayoutPatternMarker, '')
			parmReportingPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IReportingPatternMarker, '')
			parmRunOffIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RUN_OFF_INDEX_SELECTION'))
			parmSeverityIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subA"], ["CONTINUOUS"], ["START_OF_PROJECTION"], [new org.joda.time.DateTime(2013,1,1,0,0,0,0)]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('SEVERITY_INDEX_SELECTION'))
			parmUnderwritingSegments[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotor"]]),["Underwriting Information"], org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker)
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
	dependencies {
		subIndexCorrelation {
			parmCopulaStrategy[0]=org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.copula.CopulaType.NORMAL, ["dependencyMatrix":new org.pillarone.riskanalytics.core.parameterization.ComboBoxMatrixMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[1.0, 0.2], [0.2, 1.0]]),["subA","subB"],org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker),])
		}
	}
	globalParameters {
		parmProjection[0]=org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.global.ProjectionPeriodType.COMPLETEROLLOUT, [:])
		parmProjectionStartDate[0]=new org.joda.time.DateTime(2012, 1, 1, 0, 0, 0, 0)
		parmRunOffAfterFirstPeriod[0]=true
	}
	indices {
		subSeverityIndices {
			subA {
				parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.STOCHASTIC, ["startDate":new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0),"distribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.NORMAL, [mean:0.0, stDev:0.05]),])
			}
			subB {
				parmIndex[0]=org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.indexing.IndexStrategyType.STOCHASTIC, ["startDate":new org.joda.time.DateTime(2011, 1, 1, 0, 0, 0, 0),"distribution":org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.getStrategy(org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType.NORMAL, [mean:0.0, stDev:0.05]),])
			}
		}
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
	}
	reinsuranceContracts {
		subQuota60AToB {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.QUOTASHARE, ["limit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType.NONE, [:]),"lossParticipation":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.lossparticipation.LossParticipationStrategyType.NOPARTICIPATION, [:]),"commission":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType.FIXEDCOMMISSION, ["commission":0.05,]),"quotaShare":0.6,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""], [""], ["subA"], [""], [""], ["ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),"benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subB"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.VirtualizationMode.NO
		}
		subSL50Xs75BToC {
			parmContractStrategy[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType.STOPLOSS, ["limit":80.0,"riPremiumSplit":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType.PREMIUM_SHARES, [:]),"stopLossContractBase":org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.StopLossBase.ABSOLUTE,"attachmentPoint":120.0,"premium":112.0,])
			parmCover[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType.MATRIX, ["flexibleCover":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["", ""], ["", "subQuota60AToB"], ["subB", ""], ["subFire", ""], ["", ""], ["ANY", "ANY"]]),["Filtered by Net of Contract","Filtered by Ceded of Contract","Legal Entity","Segments","Generators","Kind of Loss"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('COVER_MAP')),"benefitContracts":new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Benefit Contract"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RI_CONTRACT')),])
			parmCoveredPeriod[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.getStrategy(org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType.ONEYEAR, [:])
			parmReinsurers[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subC"], [1.0]]),["Reinsurer","Covered Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('LEGAL_ENTITY_PORTION'))
			parmVirtual[0]=org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.VirtualizationMode.NO
		}
	}
	segments {
		subFire {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subFire2B"], [1.0]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, 'subB')
			parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subFire"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
		subMotor {
			parmClaimsPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotor1A"], [1.0]]),["Claims Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PERIL_PORTION'))
			parmCompany[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker, 'subA')
			parmDiscounting[0]=new org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[""]]),["Discount Index"], org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker)
			parmReservesPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Reserves Generator","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('RESERVE_PORTION'))
			parmUnderwritingPortions[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([["subMotor"], [1.0]]),["Underwriting","Portion"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('UNDERWRITING_PORTION'))
		}
	}
	underwritingSegments {
		subFire {
			parmPolicyIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmPremiumPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker, '')
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [100.0], [1.0]]),["maximum sum insured","average sum insured","premium","number of policies"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DOUBLE'))
		}
		subMotor {
			parmPolicyIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('POLICY_INDEX_SELECTION'))
			parmPremiumIndices[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[]]),["Index","Index Mode","Base Date Mode","Date"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('PREMIUM_INDEX_SELECTION'))
			parmPremiumPattern[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedString(org.pillarone.riskanalytics.domain.pc.cf.pattern.IPremiumPatternMarker, '')
			parmUnderwritingInformation[0]=new org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter(org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.0], [0.0], [100.0], [1.0]]),["maximum sum insured","average sum insured","premium","number of policies"], org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory.getConstraints('DOUBLE'))
		}
	}
}
comments=["""[path:'ORSA', period:-1, lastChange:new org.joda.time.DateTime(1371830248655),user:null, comment: \"\"\"v4: absolute premium for stoploss\"\"\", tags:(['version'] as Set)]""","""[path:'ORSA', period:-1, lastChange:new org.joda.time.DateTime(1371830248655),user:null, comment: \"\"\"v2: remove LE in ceded SL contract cover\"\"\", tags:(['version'] as Set)]""","""[path:'ORSA', period:-1, lastChange:new org.joda.time.DateTime(1371830248655),user:null, comment: \"\"\"v3: cover ceded only from A\"\"\", tags:(['version'] as Set)]""","""[path:'ORSA', period:-1, lastChange:new org.joda.time.DateTime(1371830248655),user:null, comment: \"\"\"v2: remove LE in ceded SL contract cover\"\"\", tags:(['version'] as Set)]""","""[path:'ORSA', period:-1, lastChange:new org.joda.time.DateTime(1371830249137),user:null, comment: \"\"\"v3: segment added in SL cover\"\"\", tags:(['version'] as Set)]"""]
tags=[]
