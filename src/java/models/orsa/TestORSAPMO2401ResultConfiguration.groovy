package models.orsa

model = models.orsa.ORSAModel
displayName = "PMO-2401 RT"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_PERIOD"
			outClaimsInward = "AGGREGATED"
			outClaimsNet = "AGGREGATE_BY_PERIOD"
//			outCommission = "AGGREGATED"
//			outContractFinancials = "AGGREGATED"
//			outDiscountedValues = "AGGREGATED"
//			outNetPresentValues = "AGGREGATED"
//			outUnderwritingInfoCeded = "AGGREGATED"
//			outUnderwritingInfoGNPI = "AGGREGATED"
//			outUnderwritingInfoGross = "AGGREGATED"
//			outUnderwritingInfoInward = "AGGREGATED"
//			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	retrospectiveReinsurance {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsInward = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
//			outCommission = "AGGREGATED"
//			outContractFinancials = "AGGREGATED"
//			outDiscountedValues = "AGGREGATED"
//			outNetPresentValues = "AGGREGATED"
//			outUnderwritingInfoCeded = "AGGREGATED"
//			outUnderwritingInfoGNPI = "AGGREGATED"
//			outUnderwritingInfoGross = "AGGREGATED"
//			outUnderwritingInfoInward = "AGGREGATED"
//			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	segments {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
//			outDiscountedValues = "AGGREGATED"
//			outFinancials = "AGGREGATED"
//			outNetPresentValues = "AGGREGATED"
//			outUnderwritingInfoCeded = "AGGREGATED"
//			outUnderwritingInfoGross = "AGGREGATED"
//			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outPolicyIndexApplied = "AGGREGATED"
			outPremiumIndexApplied = "AGGREGATED"
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
