package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Legal Entity"

mappings = {
    GIRA {
        "legalEntities" {
            "[%legalEntity%]" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netCashflow", {
                "financialsNetCashflow" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netCashflow", {
                    "lossRatio" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netLossRatioWrittenUltimate"
                    "premium" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netPremiumPaid"
                    "commission" "GIRA:legalEntities:[%legalEntity%]:outFinancials:commission"
                    "claim" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netClaimPaid"
                }
                "claimsNet" {
                    "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed"
                    "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed"
                    "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed"
                    "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed"
                    "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed"
                    "claimsGross" {
                        "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:ultimate", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:ultimate"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:ultimate"
                        }
                        "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:paidIncrementalIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:outstandingIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:outstandingIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:IBNRIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:IBNRIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reservesIndexed", {
                            "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reservesIndexed"
                            "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reservesIndexed"
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed"
                        "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed"
                        "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
    }
}