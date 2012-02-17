package models.gira

model = models.gira.GIRAModel
displayName = "Single Gross Claims, Reserves"
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
}
