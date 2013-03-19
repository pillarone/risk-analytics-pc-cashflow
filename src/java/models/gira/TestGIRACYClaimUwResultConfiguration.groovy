package models.gira

model = models.gira.GIRAModel
displayName = "CY Split for Claims and Underwriting"
components {
	claimsGenerators {
		subsubcomponents {
			outClaims = "AGGREGATE_BY_PERIOD"
		}
	}
	underwritingSegments {
		subsubcomponents {
			outUnderwritingInfo = "AGGREGATE_BY_PERIOD"
		}
	}
}
