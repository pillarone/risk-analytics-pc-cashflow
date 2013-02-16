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
                "totalCumulative" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:totalCumulativeIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:totalCumulativeIndexed"
                }
                "totalIncremental" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:totalIncrementalIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:totalIncrementalIndexed"
                }
                "reportedCumulativeIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedCumulativeIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reportedCumulativeIndexed"
                }
                "reportedIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reportedIncrementalIndexed"
                }
                "paidCumulativeIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidCumulativeIndexed",{
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:paidCumulativeIndexed"
                }
                "paidIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed",{
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:paidIncrementalIndexed"
                }
                "outstandingIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:outstandingIndexed"
                }
                "changesInOutstandingIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInOutstandingIndexed", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:changesInOutstandingIndexed"
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
                "premiumRiskBase" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reserveRiskBase", {
                    "[%period%]" "GIRA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumAndReserveRiskBase"
            }
        }
        "reservesIndexed" {
            "[%reservesGenerator%]" {
                "ultimate" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:ultimate"
                "totalCumulative" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:totalCumulativeIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:totalCumulativeIndexed"
                }
                "totalIncremental" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:totalIncrementalIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:totalIncrementalIndexed"
                }
                "reportedCumulativeIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:reportedCumulativeIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reportedCumulativeIndexed"
                }
                "reportedIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:reportedIncrementalIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reportedIncrementalIndexed"
                }
                "paidCumulativeIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:paidCumulativeIndexed",{
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:paidCumulativeIndexed"
                }
                "paidIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:paidIncrementalIndexed",{
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:paidIncrementalIndexed"
                }
                "outstandingIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:outstandingIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:outstandingIndexed"
                }
                "changesInOutstandingIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInOutstandingIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:changesInOutstandingIndexed"
                }
                "IBNRIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:IBNRIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:IBNRIndexed"
                }
                "changesInIBNRIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInIBNRIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:changesInIBNRIndexed"
                }
                "reservesIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:reservesIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reservesIndexed"
                }
                "changesInReservesIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInReservesIndexed", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:changesInReservesIndexed"
                }
                "premiumRiskBase" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:reserveRiskBase", {
                    "[%period%]" "GIRA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "GIRA:reservesGenerators:[%reservesGenerator%]:outClaims:premiumAndReserveRiskBase"
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
                "financialsNetCashflow" "GIRA:segments:outFinancials:netCashflow", {
                    "lossRatio" "GIRA:segments:outFinancials:netLossRatioWrittenUltimate"
                    "premium" "GIRA:segments:outFinancials:netPremiumPaid"
                    "commission" "GIRA:segments:outFinancials:commission"
                    "claim" "GIRA:segments:outFinancials:netClaimPaid"
                    "[%period%]" {
                        "financialsNetCashflow" "GIRA:segments:period:[%period%]:outFinancials:netCashflow", {
                            "lossRatio" "GIRA:segments:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                            "premium" "GIRA:segments:period:[%period%]:outFinancials:netPremiumPaid"
                            "commission" "GIRA:segments:period:[%period%]:outFinancials:commission"
                            "claim" "GIRA:segments:period:[%period%]:outFinancials:netClaimPaid"
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:segments:outClaimsNet:ultimate"
                    "totalCumulative" "GIRA:segments:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "GIRA:segments:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "GIRA:segments:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "GIRA:segments:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "GIRA:segments:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:segments:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:segments:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "GIRA:segments:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
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
                    "premiumRiskBase" "GIRA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "GIRA:segments:outClaimsGross:ultimate"
                        "totalCumulative" "GIRA:segments:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:segments:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:segments:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:segments:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:segments:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:segments:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
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
                        "premiumRiskBase" "GIRA:segments:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:segments:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:segments:outClaimsCeded:ultimate"
                        "totalCumulative" "GIRA:segments:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:segments:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:segments:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:segments:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:segments:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:segments:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
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
                    "fixed" "GIRA:segments:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "GIRA:segments:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
            "[%segment%]" "GIRA:segments:[%segment%]:outFinancials:netCashflow", {
                "cashflow" {
                    "cashflowNetDetail" "GIRA:segments:[%segment%]:outFinancials:netCashflow", {
                        "premiumPaid" "GIRA:segments:[%segment%]:outFinancials:netPremiumPaid"
                        "claimPaid" "GIRA:segments:[%segment%]:outFinancials:netClaimPaid"
                        "lossRatioPaidPaid" "GIRA:segments:[%segment%]:outFinancials:netLossRatioPaidPaid"
                        "commission" "GIRA:segments:[%segment%]:outFinancials:commission"
                        "[%period%]" {
                            "netCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netCashflow", {
                                "premiumPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netPremiumPaid"
                                "claimPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netClaimPaid"
                                "lossRatioPaidPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netLossRatioPaidPaid"
                                "commission" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowNetPeriod" "GIRA:segments:[%segment%]:outFinancials:netCashflow", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netCashflow"
                    }
                    "riskNet" {
                        "premiumRiskOnFinancials" "GIRA:segments:[%segment%]:outFinancials:netPremiumRisk"
                        "reserveRiskOnFinancials" "GIRA:segments:[%segment%]:outFinancials:netReserveRisk", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "GIRA:segments:[%segment%]:outFinancials:netPremiumReserveRisk"
                    }

                    "cashflowGrossDetail" "GIRA:segments:[%segment%]:outFinancials:grossCashflow", {
                        "premiumPaid" "GIRA:segments:[%segment%]:outFinancials:grossPremiumPaid"
                        "claimPaid" "GIRA:segments:[%segment%]:outFinancials:grossClaimPaid"
                        "lossRatioPaidPaid" "GIRA:segments:[%segment%]:outFinancials:grossLossRatioPaidPaid"
                        "[%period%]" {
                            "grossCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossCashflow", {
                                "premiumPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossPremiumPaid"
                                "claimPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossClaimPaid"
                                "lossRatioPaidPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossLossRatioPaidPaid"
                            }
                        }
                    }
                    "cashflowGrossPeriod" "GIRA:segments:[%segment%]:outFinancials:grossCashflow", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossCashflow"
                    }
                    "riskGross" {
                        "premiumRiskOnFinancials" "GIRA:segments:[%segment%]:outFinancials:grossPremiumRisk"
                        "reserveRiskOnFinancials" "GIRA:segments:[%segment%]:outFinancials:grossReserveRisk", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "GIRA:segments:[%segment%]:outFinancials:grossPremiumReserveRisk"
                    }

                    "cashflowCededDetail" "GIRA:segments:[%segment%]:outFinancials:cededCashflow", {
                        "premiumPaid" "GIRA:segments:[%segment%]:outFinancials:cededPremiumPaid"
                        "claimPaid" "GIRA:segments:[%segment%]:outFinancials:cededClaimPaid"
                        "lossRatioPaidPaid" "GIRA:segments:[%segment%]:outFinancials:cededLossRatioPaidPaid"
                        "commission" "GIRA:segments:[%segment%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededCashflow", {
                                "premiumPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededPremiumPaid"
                                "claimPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededClaimPaid"
                                "lossRatioPaidPaid" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededLossRatioPaidPaid"
                                "commission" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowCededPeriod" "GIRA:segments:[%segment%]:outFinancials:cededCashflow", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededCashflow"
                    }
                    "riskCeded" {
                        "premiumRiskOnFinancials" "GIRA:segments:[%segment%]:outFinancials:cededPremiumRisk"
                        "reserveRiskOnFinancials" "GIRA:segments:[%segment%]:outFinancials:cededReserveRisk", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "GIRA:segments:[%segment%]:outFinancials:cededPremiumReserveRisk"
                    }
                }
                "bestEstimate" {
                    "bestEstimateNetDetail" "GIRA:segments:[%segment%]:outFinancials:netBestEstimate", {
                        "premiumWritten" "GIRA:segments:[%segment%]:outFinancials:netPremiumWritten"
                        "claimInitial" "GIRA:segments:[%segment%]:outFinancials:netClaimUltimate"
                        "lossRatioWrittenUltimate" "GIRA:segments:[%segment%]:outFinancials:netLossRatioWrittenUltimate"
                        "[%period%]" {
                            "netCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netBestEstimate", {
                                "premiumWritten" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netPremiumWritten"
                                "claimInitial" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netClaimUltimate"
                                "lossRatioWrittenUltimate" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateGrossDetail" "GIRA:segments:[%segment%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "GIRA:segments:[%segment%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "GIRA:segments:[%segment%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "GIRA:segments:[%segment%]:outFinancials:grossLossRatioWrittenUltimate"
                        "[%period%]" {
                            "grossCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossBestEstimate", {
                                "premiumWritten" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossPremiumWritten"
                                "claimInitial" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossClaimUltimate"
                                "lossRatioWrittenUltimate" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:grossLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateCededDetail" "GIRA:segments:[%segment%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "GIRA:segments:[%segment%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "GIRA:segments:[%segment%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "GIRA:segments:[%segment%]:outFinancials:cededLossRatioWrittenUltimate"
                        "commission" "GIRA:segments:[%segment%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededBestEstimate", {
                                "premiumWritten" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededPremiumWritten"
                                "claimInitial" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededClaimUltimate"
                                "lossRatioWrittenUltimate" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:cededLossRatioWrittenUltimate"
                                "commission" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:segments:[%segment%]:outClaimsNet:ultimate"
                    "totalCumulative" "GIRA:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "GIRA:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "GIRA:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "GIRA:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
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
                    "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsGross:ultimate"
                        "totalCumulative" "GIRA:segments:[%segment%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:segments:[%segment%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:segments:[%segment%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
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
                        "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsCeded:ultimate"
                        "totalCumulative" "GIRA:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
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
                    "fixed" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
        "[%legalEntity%]" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netCashflow", {
            "financialsNetCashflow" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netCashflow", {
                "lossRatio" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netLossRatioWrittenUltimate"
                "premium" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netPremiumPaid"
                "commission" "GIRA:legalEntities:[%legalEntity%]:outFinancials:commission"
                "claim" "GIRA:legalEntities:[%legalEntity%]:outFinancials:netClaimPaid"
                "[%period%]" {
                    "financialsNetCashflow" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:netCashflow", {
                        "lossRatio" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                        "premium" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:netPremiumPaid"
                        "commission" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:commission"
                        "claim" "GIRA:legalEntities:[%legalEntity%]:outFinancials:period:[%period%]:netClaimPaid"
                    }
                }
            }
            "claimsNet" {
                "ultimate" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:ultimate"
                "totalCumulative" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:totalCumulativeIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                }
                "totalIncremental" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:totalIncrementalIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                }
                "reportedCumulativeIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reportedCumulativeIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                }
                "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                }
                "paidCumulativeIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:paidCumulativeIndexed",{
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                }
                "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed",{
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                }
                "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                }
                "changesInOutstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:changesInOutstandingIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                }
                "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                }
                "changesInIBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:changesInIBNRIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                }
                "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reservesIndexed"
                }
                "changesInReservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsNet:changesInReservesIndexed", {
                    "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
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
                    "totalCumulative" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:totalCumulativeIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:totalCumulativeIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:totalIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:totalIncrementalIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reportedCumulativeIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:paidCumulativeIndexed",{
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidCumulativeIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:paidCumulativeIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidCumulativeIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:paidCumulativeIndexed"
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
                    "changesInOutstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:changesInOutstandingIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:changesInOutstandingIndexed"
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
                    "changesInIBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:changesInIBNRIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:changesInIBNRIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:changesInIBNRIndexed"
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
                    "changesInReservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsGross:changesInReservesIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInReservesIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:changesInReservesIndexed"
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInReservesIndexed", {
                            "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:changesInReservesIndexed"
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
                    "totalCumulative" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:totalCumulativeIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                    }
                    "totalIncremental" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:totalIncrementalIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedCumulativeIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:paidCumulativeIndexed",{
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed",{
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInOutstandingIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInIBNRIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                    }
                    "changesInReservesIndexed" "GIRA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInReservesIndexed", {
                        "[%period%]" "GIRA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
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
                "cashflow" {
                    "cashflowNetDetail" "GIRA:structures:[%structure%]:outFinancials:netCashflow", {
                        "premiumPaid" "GIRA:structures:[%structure%]:outFinancials:netPremiumPaid"
                        "claimPaid" "GIRA:structures:[%structure%]:outFinancials:netClaimPaid"
                        "lossRatioPaidPaid" "GIRA:structures:[%structure%]:outFinancials:netLossRatioPaidPaid"
                        "commission" "GIRA:structures:[%structure%]:outFinancials:commission"
                        "[%period%]" {
                            "netCashflow" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netCashflow", {
                                "premiumPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netPremiumPaid"
                                "claimPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netClaimPaid"
                                "lossRatioPaidPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netLossRatioPaidPaid"
                                "commission" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowNetPeriod" "GIRA:structures:[%structure%]:outFinancials:netCashflow", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netCashflow"
                    }
                    "riskNet" {
                        "premiumRiskOnFinancials" "GIRA:structures:[%structure%]:outFinancials:netPremiumRisk"
                        "reserveRiskOnFinancials" "GIRA:structures:[%structure%]:outFinancials:netReserveRisk", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "GIRA:structures:[%structure%]:outFinancials:netPremiumReserveRisk"
                    }

                    "cashflowGrossDetail" "GIRA:structures:[%structure%]:outFinancials:grossCashflow", {
                        "premiumPaid" "GIRA:structures:[%structure%]:outFinancials:grossPremiumPaid"
                        "claimPaid" "GIRA:structures:[%structure%]:outFinancials:grossClaimPaid"
                        "lossRatioPaidPaid" "GIRA:structures:[%structure%]:outFinancials:grossLossRatioPaidPaid"
                        "[%period%]" {
                            "grossCashflow" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossCashflow", {
                                "premiumPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossPremiumPaid"
                                "claimPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossClaimPaid"
                                "lossRatioPaidPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossLossRatioPaidPaid"
                            }
                        }
                    }
                    "cashflowGrossPeriod" "GIRA:structures:[%structure%]:outFinancials:grossCashflow", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossCashflow"
                    }
                    "riskGross" {
                        "premiumRiskOnFinancials" "GIRA:structures:[%structure%]:outFinancials:grossPremiumRisk"
                        "reserveRiskOnFinancials" "GIRA:structures:[%structure%]:outFinancials:grossReserveRisk", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "GIRA:structures:[%structure%]:outFinancials:grossPremiumReserveRisk"
                    }

                    "cashflowCededDetail" "GIRA:structures:[%structure%]:outFinancials:cededCashflow", {
                        "premiumPaid" "GIRA:structures:[%structure%]:outFinancials:cededPremiumPaid"
                        "claimPaid" "GIRA:structures:[%structure%]:outFinancials:cededClaimPaid"
                        "lossRatioPaidPaid" "GIRA:structures:[%structure%]:outFinancials:cededLossRatioPaidPaid"
                        "commission" "GIRA:structures:[%structure%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededCashflow", {
                                "premiumPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededPremiumPaid"
                                "claimPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededClaimPaid"
                                "lossRatioPaidPaid" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededLossRatioPaidPaid"
                                "commission" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowCededPeriod" "GIRA:structures:[%structure%]:outFinancials:cededCashflow", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededCashflow"
                    }
                    "riskCeded" {
                        "premiumRiskOnFinancials" "GIRA:structures:[%structure%]:outFinancials:cededPremiumRisk"
                        "reserveRiskOnFinancials" "GIRA:structures:[%structure%]:outFinancials:cededReserveRisk", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "GIRA:structures:[%structure%]:outFinancials:cededPremiumReserveRisk"
                    }
                }
                "bestEstimate" {
                    "bestEstimateNetDetail" "GIRA:structures:[%structure%]:outFinancials:netBestEstimate", {
                        "premiumWritten" "GIRA:structures:[%structure%]:outFinancials:netPremiumWritten"
                        "claimInitial" "GIRA:structures:[%structure%]:outFinancials:netClaimUltimate"
                        "lossRatioWrittenUltimate" "GIRA:structures:[%structure%]:outFinancials:netLossRatioWrittenUltimate"
                        "[%period%]" {
                            "netCashflow" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netBestEstimate", {
                                "premiumWritten" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netPremiumWritten"
                                "claimInitial" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netClaimUltimate"
                                "lossRatioWrittenUltimate" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateGrossDetail" "GIRA:structures:[%structure%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "GIRA:structures:[%structure%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "GIRA:structures:[%structure%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "GIRA:structures:[%structure%]:outFinancials:grossLossRatioWrittenUltimate"
                        "[%period%]" {
                            "grossCashflow" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossBestEstimate", {
                                "premiumWritten" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossPremiumWritten"
                                "claimInitial" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossClaimUltimate"
                                "lossRatioWrittenUltimate" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:grossLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateCededDetail" "GIRA:structures:[%structure%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "GIRA:structures:[%structure%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "GIRA:structures:[%structure%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "GIRA:structures:[%structure%]:outFinancials:cededLossRatioWrittenUltimate"
                        "commission" "GIRA:structures:[%structure%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededBestEstimate", {
                                "premiumWritten" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededPremiumWritten"
                                "claimInitial" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededClaimUltimate"
                                "lossRatioWrittenUltimate" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:cededLossRatioWrittenUltimate"
                                "commission" "GIRA:structures:[%structure%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:structures:[%structure%]:outClaimsNet:ultimate"
                    "totalCumulative" "GIRA:structures:[%structure%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "GIRA:structures:[%structure%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "GIRA:structures:[%structure%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "GIRA:structures:[%structure%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "GIRA:structures:[%structure%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "GIRA:structures:[%structure%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "GIRA:structures:[%structure%]:outClaimsGross:ultimate"
                        "totalCumulative" "GIRA:structures:[%structure%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:structures:[%structure%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:structures:[%structure%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:structures:[%structure%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:structures:[%structure%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:structures:[%structure%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:structures:[%structure%]:outClaimsCeded:ultimate"
                        "totalCumulative" "GIRA:structures:[%structure%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:structures:[%structure%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
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
                    "totalCumulative" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "totalCumulative" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "totalCumulative" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
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
                    "fixed" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "GIRA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
        "retrospectiveReinsurance" {
            "[%contract%]" {
                "Financials" {
                    "result" "GIRA:retrospectiveReinsurance:[%contract%]:outContractFinancials:contractResult", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:contractResult", {
                            "premium" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:cededPremium"
                            "commission" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:cededCommission"
                            "claim" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:cededClaim"
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:ultimate"
                    "totalCumulative" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:ultimate"
                        "totalCumulative" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:ultimate"
                        "totalCumulative" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "GIRA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "GIRA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "GIRA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
    }
}