package models.gira

model = models.gira.GIRAModel
displayName = "Split per Source"
components {
//	legalEntities {
//		subsubcomponents {
//			outClaimsCeded = "SPLIT_PER_SOURCE"
//			outClaimsGross = "SPLIT_PER_SOURCE"
//			outClaimsNet = "SPLIT_PER_SOURCE"
//			outClaimsPrimaryInsurer = "SPLIT_PER_SOURCE"
//			outClaimsReinsurer = "SPLIT_PER_SOURCE"
//			outUnderwritingInfoCeded = "SPLIT_PER_SOURCE"
//			outUnderwritingInfoGross = "SPLIT_PER_SOURCE"
//			outUnderwritingInfoNet = "SPLIT_PER_SOURCE"
//			outUnderwritingInfoPrimeryInsurer = "SPLIT_PER_SOURCE"
//			outUnderwritingInfoReinsurer = "SPLIT_PER_SOURCE"
//		}
//	}
	reinsuranceContracts {
		subcomponents {
			outClaimsCeded = "SPLIT_PER_SOURCE"
			outClaimsGross = "SPLIT_PER_SOURCE"
			outClaimsNet = "SPLIT_PER_SOURCE"
			outCommission = "SPLIT_PER_SOURCE"
			outContractFinancials = "SPLIT_PER_SOURCE"
			outUnderwritingInfoCeded = "SPLIT_PER_SOURCE"
			outUnderwritingInfoGross = "SPLIT_PER_SOURCE"
			outUnderwritingInfoNet = "SPLIT_PER_SOURCE"
		}
	}
	segments {
		outClaimsCeded = "AGGREGATED"
		outClaimsGross = "AGGREGATED"
		outClaimsNet = "AGGREGATED"
		outDiscountedValues = "AGGREGATED"
		outNetPresentValues = "AGGREGATED"
		outUnderwritingInfoCeded = "AGGREGATED"
		outUnderwritingInfoGross = "AGGREGATED"
		outUnderwritingInfoNet = "AGGREGATED"
		subcomponents {
			outClaimsCeded = "SPLIT_PER_SOURCE"
			outClaimsGross = "SPLIT_PER_SOURCE"
			outClaimsNet = "SPLIT_PER_SOURCE"
			outDiscountedValues = "SPLIT_PER_SOURCE"
			outNetPresentValues = "SPLIT_PER_SOURCE"
			outUnderwritingInfoCeded = "SPLIT_PER_SOURCE"
			outUnderwritingInfoGross = "SPLIT_PER_SOURCE"
			outUnderwritingInfoNet = "SPLIT_PER_SOURCE"
		}
	}
//	structures {
//		subsubcomponents {
//			outClaimsCeded = "AGGREGATED"
//			outClaimsGross = "AGGREGATED"
//			outClaimsNet = "AGGREGATED"
//			outUnderwritingInfoCeded = "AGGREGATED"
//			outUnderwritingInfoGross = "AGGREGATED"
//			outUnderwritingInfoNet = "AGGREGATED"
//		}
//	}
}
