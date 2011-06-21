package models.gira

model = models.gira.GIRAModel
displayName = "Aggregate Gross Claims"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "AGGREGATED"
			outSeverityIndexApplied = "AGGREGATED"
		}
	}
    reservesGenerators {
		subsubcomponents {
			outReserves = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outPolicyIndexApplied = "AGGREGATED"
			outPremiumIndexApplied = "AGGREGATED"
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
