package models.orsa

model = models.orsa.ORSAModel
displayName = "PMO2233 Test 2"
components {
	legalEntities {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_PAID_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_PAID_CLAIM"
			outClaimsPrimaryInsurer = "AGGREGATED_ULTIMATE_PAID_CLAIM"
			outClaimsReinsurer = "AGGREGATED_ULTIMATE_PAID_CLAIM"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED_ULTIMATE_PAID_CLAIM"
			outClaimsGross = "AGGREGATED_ULTIMATE_PAID_CLAIM"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGNPI = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
		}
	}
}
