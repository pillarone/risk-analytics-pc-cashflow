package models.gira

model = models.gira.GIRAModel
displayName = "Premium Reserve Risk Triangle"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "SPLIT_PER_SOURCE"
			outClaimsGross = "SPLIT_PER_SOURCE"
			outClaimsNet = "SPLIT_PER_SOURCE"
		}
	}
	segments {
		outClaimsCeded = "PREMIUM_RESERVE_RISK_TRIANGLE"
		outClaimsGross = "PREMIUM_RESERVE_RISK_TRIANGLE"
		outClaimsNet = "PREMIUM_RESERVE_RISK_TRIANGLE"
		subsubcomponents {
			outClaimsCeded = "PREMIUM_RESERVE_RISK_TRIANGLE"
			outClaimsGross = "PREMIUM_RESERVE_RISK_TRIANGLE"
			outClaimsNet = "PREMIUM_RESERVE_RISK_TRIANGLE"
		}
	}
}
