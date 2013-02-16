package models.orsa

model = models.orsa.ORSAModel
displayName = "Aggregate Claims, Segments, R/I"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGNPI = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	segments {
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
