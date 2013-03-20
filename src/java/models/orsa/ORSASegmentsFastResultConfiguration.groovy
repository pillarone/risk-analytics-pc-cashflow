package models.orsa

model = models.orsa.ORSAModel
displayName = "Segments (fast)"
components {
	segments {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outClaimsNet = "AGGREGATED_ULTIMATE_REPORTED_PAID_CLAIM"
			outDiscountedValues = "AGGREGATED"
			outFinancials = "AGGREGATED"
			outNetPresentValues = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
}
