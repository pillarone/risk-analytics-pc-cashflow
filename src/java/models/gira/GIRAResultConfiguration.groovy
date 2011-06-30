package models.gira

model = models.gira.GIRAModel
displayName = "Single Gross Claims"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "SINGLE"
            outClaimNumber ="AGGREGATED"
		}
	}
    reservesGenerators {
		subsubcomponents {
			outReserves = "SINGLE"
            outNominalUltimates = "SINGLE"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
    structures {
		subsubcomponents {
			outClaimsGross= "SINGLE"
            outClaimsNet= "SINGLE"
            outClaimsCeded= "SINGLE"
            outUnderwritingInfoGross = "SINGLE"
            outUnderwritingInfoNet = "SINGLE"
            outUnderwritingInfoCeded = "SINGLE"
		}
	}
}
