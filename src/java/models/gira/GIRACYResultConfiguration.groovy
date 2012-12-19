package models.gira

model = models.gira.GIRAModel
displayName = "Details, Aggregated, CY Split"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "SPLIT_BY_INCEPTION_DATE"
		}
	}
	legalEntities {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outClaimsPrimaryInsurer = "AGGREGATED"
			outClaimsReinsurer = "AGGREGATED"
			outDiscountedValues = "AGGREGATED"
			outNetFinancials = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
			outUnderwritingInfoPrimeryInsurer = "AGGREGATED"
			outUnderwritingInfoReinsurer = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outCommission = "AGGREGATED"
			outContractFinancials = "AGGREGATED"
			outDiscountedValues = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	reservesGenerators {
		subsubcomponents {
			outNominalUltimates = "AGGREGATED"
			outReserves = "SPLIT_BY_INCEPTION_DATE"
		}
	}
	retrospectiveReinsurance {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outContractFinancials = "AGGREGATED"
			outDiscountedValues = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	segments {
		subsubcomponents {
			outClaimsCeded = "SPLIT_BY_INCEPTION_DATE"
			outClaimsGross = "SPLIT_BY_INCEPTION_DATE"
			outClaimsNet = "SPLIT_BY_INCEPTION_DATE"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	structures {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "SPLIT_BY_INCEPTION_DATE"
		}
	}
}
