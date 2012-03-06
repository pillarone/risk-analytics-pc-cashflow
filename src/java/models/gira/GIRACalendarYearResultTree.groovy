package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Calendar Year"

mappings = {
    GIRA {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "reportedIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reportedIncrementalIndexed"
                }
                "paidIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed",{
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:paidIncrementalIndexed"
                }
                "outstandingIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:outstandingIndexed"
                }
                "IBNRIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNRIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:IBNRIndexed"
                }
                "reservesIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reservesIndexed"
                }
                "increaseDueToIndex" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResultIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:developedResultIndexed"
                }
                "premiumRisk" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumRisk"
                "reserveRisk" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reserveRisk", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reserveRisk"
                }
            }
        }
        "reservesIndexed" {
            "[%reservesGenerator%]" {
                "ultimateFromInceptionPeriod" "GIRA:reservesGenerators:[%reservesGenerator%]:outNominalUltimates:value"
                "reportedIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reportedIncrementalIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reportedIncrementalIndexed"
                }
                "paidIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:paidIncrementalIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:paidIncrementalIndexed"
                }
                "outstandingIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:outstandingIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:outstandingIndexed"
                }
                "IBNRIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:IBNRIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:IBNRIndexed"
                }
                "reservesIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reservesIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reservesIndexed"
                }
                "increaseDueToIndex" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:developedResultIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:developedResultIndexed"
                }
                "premiumRisk" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:premiumRisk"
                "reserveRisk" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reserveRisk", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reserveRisk"
                }
            }
        }
        "grossUnderwritingBySegment" {
            "[%underwritingSegment%]" {
                "premiumWritten" "GIRA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumWritten"
                "premiumPaid" "GIRA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumPaid", {
                    "[%period%]" "GIRA:underwritingSegments:[%underwritingSegment%]:period:[%period%]:outUnderwritingInfo:premiumPaid"
                }
            }
        }
        "segments" {
            "[%segment%]" "GIRA:segments:[%segment%]:outNetFinancials:netCashflow", {
                "financialsNetCashflow" "GIRA:segments:[%segment%]:outNetFinancials:netCashflow", {
                    "lossRatio" "GIRA:segments:[%segment%]:outNetFinancials:lossRatio"
                    "premium" "GIRA:segments:[%segment%]:outNetFinancials:netPremiumPaid"
                    "commission" "GIRA:segments:[%segment%]:outNetFinancials:commission"
                    "claim" "GIRA:segments:[%segment%]:outNetFinancials:netClaimPaid"
                }
                "claimsNet" {
                    "ultimate" "GIRA:segments:[%segment%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:segments:[%segment%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRisk" "GIRA:segments:[%segment%]:outClaimsNet:premiumRisk"
                    "reserveRisk" "GIRA:segments:[%segment%]:outClaimsNet:reserveRisk", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRisk"
                    }
                    "claimsGross" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:segments:[%segment%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRisk" "GIRA:segments:[%segment%]:outClaimsGross:premiumRisk"
                        "reserveRisk" "GIRA:segments:[%segment%]:outClaimsGross:reserveRisk", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRisk"
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
                        }
                        "premiumRisk" "GIRA:segments:[%segment%]:outClaimsCeded:premiumRisk"
                        "reserveRisk" "GIRA:segments:[%segment%]:outClaimsCeded:reserveRisk", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reserveRisk"
                        }
                    }
                }
                "discountedValues" {
                    "paidIncrementalGross" "GIRA:segments:[%segment%]:outDiscountedValues:discountedPaidIncrementalGross"
                    "paidIncrementalNet" "GIRA:segments:[%segment%]:outDiscountedValues:discountedPaidIncrementalNet"
                    "paidIncrementalCeded" "GIRA:segments:[%segment%]:outDiscountedValues:discountedPaidIncrementalCeded"
                    "reservesGross" "GIRA:segments:[%segment%]:outDiscountedValues:discountedReservedGross"
                    "reservesNet" "GIRA:segments:[%segment%]:outDiscountedValues:discountedReservedNet"
                    "reservesCeded" "GIRA:segments:[%segment%]:outDiscountedValues:discountedReservedCeded"
                    "netPresentValuePaidGross" "GIRA:segments:[%segment%]:outNetPresentValues:netPresentValueGross"
                    "netPresentValuePaidNet" "GIRA:segments:[%segment%]:outNetPresentValues:netPresentValueNet"
                    "netPresentValuePaidCeded" "GIRA:segments:[%segment%]:outNetPresentValues:netPresentValueCeded"
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:segments:[%segment%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:segments:[%segment%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "GIRA:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "[%legalEntity%]" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netCashflow", {
            "financialsNetCashflow" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netCashflow", {
                "lossRatio" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:lossRatio"
                "premium" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netPremiumPaid"
                "commission" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:commission"
                "claim" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:netClaimPaid"
            }
        }
        "structures" {
            "[%structure%]" {
                "claimsNet" {
                    "ultimate" "GIRA:structures:[%structure%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "increaseDueToIndex" "GIRA:structures:[%structure%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRisk" "GIRA:structures:[%structure%]:outClaimsNet:premiumRisk"
                    "reserveRisk" "GIRA:structures:[%structure%]:outClaimsNet:reserveRisk", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reserveRisk"
                    }
                    "claimsGross" {
                        "ultimate" "GIRA:structures:[%structure%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:structures:[%structure%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRisk" "GIRA:structures:[%structure%]:outClaimsGross:premiumRisk"
                        "reserveRisk" "GIRA:structures:[%structure%]:outClaimsGross:reserveRisk", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reserveRisk"
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:structures:[%structure%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:structures:[%structure%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
                        }
                        "premiumRisk" "GIRA:structures:[%structure%]:outClaimsCeded:premiumRisk"
                        "reserveRisk" "GIRA:structures:[%structure%]:outClaimsCeded:reserveRisk", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reserveRisk"
                        }
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:structures:[%structure%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:structures:[%structure%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:structures:[%structure%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "GIRA:structures:[%structure%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "claimsNet" {
                    "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRisk" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRisk"
                    "reserveRisk" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRisk", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reserveRisk"
                    }
                    "claimsGross" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRisk" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRisk"
                        "reserveRisk" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRisk", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reserveRisk"
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
                        }
                        "premiumRisk" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRisk"
                        "reserveRisk" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRisk", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reserveRisk"
                        }
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
    }
}