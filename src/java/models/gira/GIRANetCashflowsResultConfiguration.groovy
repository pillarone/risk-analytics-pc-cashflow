package models.gira

model = models.gira.GIRAModel
displayName = "Net Cashflows"
components {
	legalEntities {
		subsubcomponents {
			outNetFinancials = "AGGREGATED"
		}
	}
	segments {
		subsubcomponents {
			outNetFinancials = "AGGREGATED"
		}
	}
}
