package models.gira

model = models.gira.GIRAModel
displayName = "Details, Aggregated (Ultimate)"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "AGGREGATED_ULTIMATE_CLAIM"
		}
	}
	legalEntities {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsPrimaryInsurer = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsReinsurer = "AGGREGATED_ULTIMATE_CLAIM"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATED"
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
			outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
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
			outReserves = "AGGREGATED_ULTIMATE_CLAIM"
		}
	}
	retrospectiveReinsurance {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
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
			outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
			outDiscountedValues = "AGGREGATED"
            outFinancials = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	structures {
        substructure {
			outClaimsCeded = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_CLAIM"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
