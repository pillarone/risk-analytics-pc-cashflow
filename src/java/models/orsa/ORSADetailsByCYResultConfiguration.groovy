package models.orsa

model = models.orsa.ORSAModel
displayName = "Details by Calendar Year"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATE_BY_PERIOD"
		}
	}
	legalEntities {
		subsubcomponents {
			outClaimsCeded = "AGGREGATE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_PERIOD"
			outClaimsNet = "AGGREGATE_BY_PERIOD"
			outClaimsPrimaryInsurer = "AGGREGATE_BY_PERIOD"
			outClaimsReinsurer = "AGGREGATE_BY_PERIOD"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATE_FIN_BY_PERIOD_grossPremiumRisk_grossReserveRisk_grossPremiumReserveRisk"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoGross = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoNet = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoPrimeryInsurer = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoReinsurer = "AGGREGATE_BY_PERIOD"
		}
	}
	reinsuranceContracts {
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
	reservesGenerators {
		subsubcomponents {
			outNominalUltimates = "AGGREGATED"
			outReserves = "AGGREGATE_BY_PERIOD"
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
			outClaimsCeded = "AGGREGATE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_PERIOD"
			outClaimsNet = "AGGREGATE_BY_PERIOD"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATE_FIN_BY_PERIOD_grossPremiumRisk_grossReserveRisk_grossPremiumReserveRisk"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoGross = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoNet = "AGGREGATE_BY_PERIOD"
		}
	}
	structures {
		substructure {
			outClaimsCeded = "AGGREGATE_BY_PERIOD"
			outClaimsGross = "AGGREGATE_BY_PERIOD"
			outClaimsNet = "AGGREGATE_BY_PERIOD"
			outFinancials = "AGGREGATE_FIN_BY_PERIOD_grossPremiumRisk_grossReserveRisk_grossPremiumReserveRisk"
			outUnderwritingInfoCeded = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoGross = "AGGREGATE_BY_PERIOD"
			outUnderwritingInfoNet = "AGGREGATE_BY_PERIOD"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATE_BY_PERIOD"
		}
	}
}
