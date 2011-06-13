package models.gira

model = models.gira.GIRAModel
displayName = "Aggregate Gross Claims without Index Collection"
components {
	claimsGenerators {
		subsubcomponents {
			outClaimNumber = "AGGREGATED"
			outClaims = "AGGREGATED"
		}
	}
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "AGGREGATED"
			outClaimsGross = "AGGREGATED"
			outClaimsNet = "AGGREGATED"
			outCommission = "AGGREGATED"
			outContractFinancials = "AGGREGATED"
			outUnderwritingInfoCeded = "AGGREGATED"
			outUnderwritingInfoGross = "AGGREGATED"
			outUnderwritingInfoNet = "AGGREGATED"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATED"
		}
	}
}