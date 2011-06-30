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
            outNominalUltimates = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outPolicyIndexApplied = "AGGREGATED"
			outPremiumIndexApplied = "AGGREGATED"
			outUnderwritingInfo = "AGGREGATED"
		}
	}
    structures {
		subsubcomponents {
			outClaimsGross= "AGGREGATED"
            outClaimsNet= "AGGREGATED"
            outClaimsCeded= "AGGREGATED"
            outUnderwritingInfoGross = "AGGREGATED"
            outUnderwritingInfoNet = "AGGREGATED"
            outUnderwritingInfoCeded = "AGGREGATED"
		}
	}
}
