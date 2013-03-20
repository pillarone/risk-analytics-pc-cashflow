package models.gira

model = models.gira.GIRAModel
displayName = "Legal Entity, Drill Down"
components {
	legalEntities {
		subsubcomponents {
			outClaimsCeded = "SPLIT_PER_SOURCE"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outClaimsPrimaryInsurer = "SPLIT_PER_SOURCE"
			outClaimsReinsurer = "SPLIT_PER_SOURCE"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "SPLIT_PER_SOURCE"
			outUnderwritingInfoGross = "SPLIT_PER_SOURCE"
			outUnderwritingInfoNet = "AGGREGATED"
			outUnderwritingInfoPrimeryInsurer = "AGGREGATED"
			outUnderwritingInfoReinsurer = "AGGREGATED"
		}
	}
	structures {
        substructure {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
}
