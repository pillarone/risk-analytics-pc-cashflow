package models.gira

model = models.gira.GIRAModel
displayName = "Legal Entity, Aggregated (Ultimate, Reported, Paid)"
components {
	legalEntities {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outClaimsPrimaryInsurer = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outClaimsReinsurer = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
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
}
