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
                "changesInIBNRIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInIBNRIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:changesInIBNRIndexed"
                }
                "reservesIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reservesIndexed"
                }
                "changesInReservesIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInReservesIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:changesInReservesIndexed"
                }
                "increaseDueToIndex" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResultIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:developedResultIndexed"
                }
                "premiumRiskBase" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reserveRiskBase", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumAndReserveRiskBase"
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
                "premiumRiskBase" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:premiumRiskBase"
                "reserveRiskBase" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reserveRiskBase", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:premiumAndReserveRiskBase"
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
            "totalOfAllSegments" {
                "financialsNetCashflow" "GIRA:segments:outNetFinancials:netCashflow", {
                    "lossRatio" "GIRA:segments:outNetFinancials:lossRatio"
                    "premium" "GIRA:segments:outNetFinancials:netPremiumPaid"
                    "commission" "GIRA:segments:outNetFinancials:commission"
                    "claim" "GIRA:segments:outNetFinancials:netClaimPaid"
                    "[%period%]" {
                        "financialsNetCashflow" "GIRA:segments:period:[%period%]:outNetFinancials:netCashflow", {
                            "lossRatio" "GIRA:segments:period:[%period%]:outNetFinancials:lossRatio"
                            "premium" "GIRA:segments:period:[%period%]:outNetFinancials:netPremiumPaid"
                            "commission" "GIRA:segments:period:[%period%]:outNetFinancials:commission"
                            "claim" "GIRA:segments:period:[%period%]:outNetFinancials:netClaimPaid"
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:segments:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:segments:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:segments:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:segments:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:segments:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "GIRA:segments:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:segments:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "GIRA:segments:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "increaseDueToIndex" "GIRA:segments:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "GIRA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "GIRA:segments:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:segments:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:segments:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:segments:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:segments:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:segments:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:segments:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "GIRA:segments:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:segments:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:segments:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:segments:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:segments:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:segments:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:segments:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:segments:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:segments:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:developedResultIndexed"
                        }
                        "premiumRiskBase" "GIRA:segments:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:segments:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "discountedValues" {
                    "paidIncrementalGross" "GIRA:segments:outDiscountedValues:discountedPaidIncrementalGross"
                    "paidIncrementalNet" "GIRA:segments:outDiscountedValues:discountedPaidIncrementalNet"
                    "paidIncrementalCeded" "GIRA:segments:outDiscountedValues:discountedPaidIncrementalCeded"
                    "reservesGross" "GIRA:segments:outDiscountedValues:discountedReservedGross"
                    "reservesNet" "GIRA:segments:outDiscountedValues:discountedReservedNet"
                    "reservesCeded" "GIRA:segments:outDiscountedValues:discountedReservedCeded"
                    "netPresentValuePaidGross" "GIRA:segments:outNetPresentValues:netPresentValueGross"
                    "netPresentValuePaidNet" "GIRA:segments:outNetPresentValues:netPresentValueNet"
                    "netPresentValuePaidCeded" "GIRA:segments:outNetPresentValues:netPresentValueCeded"
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:segments:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:segments:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:segments:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:segments:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "GIRA:segments:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "GIRA:segments:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "GIRA:segments:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "GIRA:segments:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "GIRA:segments:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:segments:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:segments:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:segments:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "GIRA:segments:outUnderwritingInfoCeded:commissionVariable"
                }
            }
            "[%segment%]" "GIRA:segments:[%segment%]:outNetFinancials:netCashflow", {
                "financialsNetCashflow" "GIRA:segments:[%segment%]:outNetFinancials:netCashflow", {
                    "lossRatio" "GIRA:segments:[%segment%]:outNetFinancials:lossRatio"
                    "premium" "GIRA:segments:[%segment%]:outNetFinancials:netPremiumPaid"
                    "commission" "GIRA:segments:[%segment%]:outNetFinancials:commission"
                    "claim" "GIRA:segments:[%segment%]:outNetFinancials:netClaimPaid"
                    "[%period%]" {
                        "financialsNetCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:netCashflow", {
                            "lossRatio" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:lossRatio"
                            "premium" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:netPremiumPaid"
                            "commission" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:commission"
                            "claim" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:netClaimPaid"
                        }
                    }
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
                    "changesInIBNRIndexed" "GIRA:segments:[%segment%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "GIRA:segments:[%segment%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
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
                        "changesInIBNRIndexed" "GIRA:segments:[%segment%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:segments:[%segment%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
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
                        "changesInIBNRIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
                        }
                        "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase"
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
                "[%period%]" {
                    "financialsNetCashflow" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outNetFinancials:netCashflow", {
                        "lossRatio" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outNetFinancials:lossRatio"
                        "premium" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outNetFinancials:netPremiumPaid"
                        "commission" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outNetFinancials:commission"
                        "claim" "GIRA:legalEntities:[%legalEntity%]:outNetFinancials:period:[%period%]:netClaimPaid"
                    }
                }
            }
            "claimsNet" {
                "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:ultimate"
                "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                }
                "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                }
                "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                }
                "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                }
                "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reservesIndexed"
                }
                "premiumRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:premiumRiskBase"
                "reserveRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reserveRiskBase", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:premiumAndReserveRiskBase"
                "claimsGross" {
                    "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:ultimate", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:ultimate"
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:ultimate"
                    }
                    "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reportedIncrementalIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:paidIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:outstandingIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:outstandingIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:outstandingIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:outstandingIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:outstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:IBNRIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:IBNRIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:IBNRIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:IBNRIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:IBNRIndexed"
                        }
                    }
                    "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reservesIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reservesIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reservesIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reservesIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reservesIndexed"
                        }
                    }
                    "premiumRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:premiumRiskBase"
                    "reserveRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reserveRiskBase", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:premiumAndReserveRiskBase"
                }
                "claimsCeded" {
                    "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:ultimate"
                    "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                    }
                    "premiumRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:premiumRiskBase"
                    "reserveRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reserveRiskBase", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:premiumAndReserveRiskBase"
                }
            }
            "premium" {
                "premiumWrittenNet" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumWritten", {
                    "gross" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumWritten", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoGross:premiumWritten"
                    }
                    "ceded" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumWritten", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                }
                "premiumPaidNet" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumPaid", {
                    "gross" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumPaid", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                    }
                    "ceded" "GIRA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaid", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
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
                    "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:premiumAndReserveRiskBase"
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
                        "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:premiumAndReserveRiskBase"
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
                        "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsCeded:premiumAndReserveRiskBase"
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
                "Financials" {
                    "result" "GIRA:reinsuranceContracts:[%contract%]:outContractFinancials:contractResult", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:contractResult", {
                            "premium" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:cededPremium"
                            "commission" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:cededCommission"
                            "claim" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:cededClaim"
                        }
                    }
                }
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
                    "premiumRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
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
                        "premiumRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
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
                        "premiumRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
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