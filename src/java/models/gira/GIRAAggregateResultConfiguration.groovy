package models.gira

model = models.gira.GIRAModel
displayName = "Aggregate Gross Claims"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "SINGLE"
			outSeverityIndicesApplied = "SINGLE"
		}
	}
	reservesGenerators {
		subsubcomponents {
			outNominalUltimates = "AGGREGATED"
			outReserves = "AGGREGATED"
		}
	}
	retrospectiveReinsurance {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
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
	underwritingSegments {
		subsubcomponents {
			outPolicyIndexApplied = "AGGREGATED"
			outPremiumIndexApplied = "AGGREGATED"
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}
