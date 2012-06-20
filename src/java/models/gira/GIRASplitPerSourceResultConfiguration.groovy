package models.gira

model = models.gira.GIRAModel
displayName = "Split Per Source"
components {
	reinsuranceContracts {
		subsubcomponents {
			outClaimsCeded = "SPLIT_PER_SOURCE_REDUCED"
		}
	}
	segments {
		outClaimsGross = "AGGREGATED_ULTIMATE_REPORTED_CLAIM"
		outClaimsNet = "AGGREGATED_ULTIMATE_REPORTED_CLAIM"
		subsubcomponents {
			outClaimsGross = "SPLIT_PER_SOURCE_REDUCED"
			outClaimsNet = "SPLIT_PER_SOURCE_REDUCED"
		}
	}
	structures {
		substructure {
			outClaimsGross = "SPLIT_PER_SOURCE_REDUCED"
			outClaimsNet = "SPLIT_PER_SOURCE_REDUCED"
		}
	}
}
