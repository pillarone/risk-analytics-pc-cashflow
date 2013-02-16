package models.orsa

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = ORSAModel
displayName = "Calendar Year"

mappings = {
    ORSA {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "totalCumulative" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:totalCumulativeIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:totalCumulativeIndexed"
                }
                "totalIncremental" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:totalIncrementalIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:totalIncrementalIndexed"
                }
                "reportedCumulativeIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedCumulativeIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reportedCumulativeIndexed"
                }
                "reportedIncrementalIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reportedIncrementalIndexed"
                }
                "paidCumulativeIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:paidCumulativeIndexed",{
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:paidCumulativeIndexed"
                }
                "paidIncrementalIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed",{
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:paidIncrementalIndexed"
                }
                "outstandingIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:outstandingIndexed"
                }
                "changesInOutstandingIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInOutstandingIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:changesInOutstandingIndexed"
                }
                "IBNRIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNRIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:IBNRIndexed"
                }
                "changesInIBNRIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInIBNRIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:changesInIBNRIndexed"
                }
                "reservesIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reservesIndexed"
                }
                "changesInReservesIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInReservesIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:changesInReservesIndexed"
                }
                "premiumRiskBase" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reserveRiskBase", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumAndReserveRiskBase"
            }
        }
        "reservesIndexed" {
            "[%reservesGenerator%]" {
                "ultimate" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:ultimate"
                "totalCumulative" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:totalCumulativeIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:totalCumulativeIndexed"
                }
                "totalIncremental" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:totalIncrementalIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:totalIncrementalIndexed"
                }
                "reportedCumulativeIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reportedCumulativeIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reportedCumulativeIndexed"
                }
                "reportedIncrementalIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reportedIncrementalIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reportedIncrementalIndexed"
                }
                "paidCumulativeIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:paidCumulativeIndexed",{
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:paidCumulativeIndexed"
                }
                "paidIncrementalIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:paidIncrementalIndexed",{
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:paidIncrementalIndexed"
                }
                "outstandingIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:outstandingIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:outstandingIndexed"
                }
                "changesInOutstandingIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInOutstandingIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:changesInOutstandingIndexed"
                }
                "IBNRIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:IBNRIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:IBNRIndexed"
                }
                "changesInIBNRIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInIBNRIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:changesInIBNRIndexed"
                }
                "reservesIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reservesIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reservesIndexed"
                }
                "changesInReservesIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInReservesIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:changesInReservesIndexed"
                }
                "premiumRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reserveRiskBase", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outClaims:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:premiumAndReserveRiskBase"
            }
        }
        "grossUnderwritingBySegment" {
            "[%underwritingSegment%]" {
                "premiumWritten" "ORSA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumWritten"
                "premiumPaid" "ORSA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumPaid", {
                    "[%period%]" "ORSA:underwritingSegments:[%underwritingSegment%]:period:[%period%]:outUnderwritingInfo:premiumPaid"
                }
            }
        }
        "segments" {
            "totalOfAllSegments" {
                "financialsNetCashflow" "ORSA:segments:outFinancials:netCashflow", {
                    "lossRatio" "ORSA:segments:outFinancials:netLossRatioWrittenUltimate"
                    "premium" "ORSA:segments:outFinancials:netPremiumPaid"
                    "commission" "ORSA:segments:outFinancials:commission"
                    "claim" "ORSA:segments:outFinancials:netClaimPaid"
                    "[%period%]" {
                        "financialsNetCashflow" "ORSA:segments:period:[%period%]:outFinancials:netCashflow", {
                            "lossRatio" "ORSA:segments:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                            "premium" "ORSA:segments:period:[%period%]:outFinancials:netPremiumPaid"
                            "commission" "ORSA:segments:period:[%period%]:outFinancials:commission"
                            "claim" "ORSA:segments:period:[%period%]:outFinancials:netClaimPaid"
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:segments:outClaimsNet:ultimate"
                    "totalCumulative" "ORSA:segments:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "ORSA:segments:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "ORSA:segments:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "ORSA:segments:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "ORSA:segments:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:segments:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:segments:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "ORSA:segments:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:segments:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "ORSA:segments:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:segments:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "ORSA:segments:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "ORSA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:segments:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:segments:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:segments:outClaimsGross:ultimate"
                        "totalCumulative" "ORSA:segments:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:segments:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:segments:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:segments:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:segments:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:segments:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:segments:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:segments:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:segments:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:segments:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:segments:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:segments:outClaimsCeded:ultimate"
                        "totalCumulative" "ORSA:segments:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:segments:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:segments:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:segments:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:segments:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:segments:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:segments:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:segments:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:segments:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:segments:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:segments:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "discountedValues" {
                    "paidIncrementalGross" "ORSA:segments:outDiscountedValues:discountedPaidIncrementalGross"
                    "paidIncrementalNet" "ORSA:segments:outDiscountedValues:discountedPaidIncrementalNet"
                    "paidIncrementalCeded" "ORSA:segments:outDiscountedValues:discountedPaidIncrementalCeded"
                    "reservesGross" "ORSA:segments:outDiscountedValues:discountedReservedGross"
                    "reservesNet" "ORSA:segments:outDiscountedValues:discountedReservedNet"
                    "reservesCeded" "ORSA:segments:outDiscountedValues:discountedReservedCeded"
                    "netPresentValuePaidGross" "ORSA:segments:outNetPresentValues:netPresentValueGross"
                    "netPresentValuePaidNet" "ORSA:segments:outNetPresentValues:netPresentValueNet"
                    "netPresentValuePaidCeded" "ORSA:segments:outNetPresentValues:netPresentValueCeded"
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:segments:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:segments:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "ORSA:segments:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "ORSA:segments:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "ORSA:segments:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "ORSA:segments:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:segments:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "ORSA:segments:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:segments:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "ORSA:segments:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "ORSA:segments:outUnderwritingInfoCeded:commission", {
                    "fixed" "ORSA:segments:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "ORSA:segments:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
            "[%segment%]" "ORSA:segments:[%segment%]:outFinancials:netCashflow", {
                "cashflow" {
                    "cashflowNetDetail" "ORSA:segments:[%segment%]:outFinancials:netCashflow", {
                        "premiumPaid" "ORSA:segments:[%segment%]:outFinancials:netPremiumPaid"
                        "claimPaid" "ORSA:segments:[%segment%]:outFinancials:netClaimPaid"
                        "lossRatioPaidPaid" "ORSA:segments:[%segment%]:outFinancials:netLossRatioPaidPaid"
                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
                        "[%period%]" {
                            "netCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netCashflow", {
                                "premiumPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netPremiumPaid"
                                "claimPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netClaimPaid"
                                "lossRatioPaidPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netLossRatioPaidPaid"
                                "commission" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowNetPeriod" "ORSA:segments:[%segment%]:outFinancials:netCashflow", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netCashflow"
                    }
                    "riskNet" {
                        "premiumRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:netPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:netReserveRisk", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:segments:[%segment%]:outFinancials:netPremiumReserveRisk"
                    }

                    "cashflowGrossDetail" "ORSA:segments:[%segment%]:outFinancials:grossCashflow", {
                        "premiumPaid" "ORSA:segments:[%segment%]:outFinancials:grossPremiumPaid"
                        "claimPaid" "ORSA:segments:[%segment%]:outFinancials:grossClaimPaid"
                        "lossRatioPaidPaid" "ORSA:segments:[%segment%]:outFinancials:grossLossRatioPaidPaid"
                        "[%period%]" {
                            "grossCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossCashflow", {
                                "premiumPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossPremiumPaid"
                                "claimPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossClaimPaid"
                                "lossRatioPaidPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossLossRatioPaidPaid"
                            }
                        }
                    }
                    "cashflowGrossPeriod" "ORSA:segments:[%segment%]:outFinancials:grossCashflow", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossCashflow"
                    }
                    "riskGross" {
                        "premiumRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:grossPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:grossReserveRisk", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:segments:[%segment%]:outFinancials:grossPremiumReserveRisk"
                    }

                    "cashflowCededDetail" "ORSA:segments:[%segment%]:outFinancials:cededCashflow", {
                        "premiumPaid" "ORSA:segments:[%segment%]:outFinancials:cededPremiumPaid"
                        "claimPaid" "ORSA:segments:[%segment%]:outFinancials:cededClaimPaid"
                        "lossRatioPaidPaid" "ORSA:segments:[%segment%]:outFinancials:cededLossRatioPaidPaid"
                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededCashflow", {
                                "premiumPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededPremiumPaid"
                                "claimPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededClaimPaid"
                                "lossRatioPaidPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededLossRatioPaidPaid"
                                "commission" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowCededPeriod" "ORSA:segments:[%segment%]:outFinancials:cededCashflow", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededCashflow"
                    }
                    "riskCeded" {
                        "premiumRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:cededPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:cededReserveRisk", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:segments:[%segment%]:outFinancials:cededPremiumReserveRisk"
                    }
                }
                "bestEstimate" {
                    "bestEstimateNetDetail" "ORSA:segments:[%segment%]:outFinancials:netBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:netPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:netClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:netLossRatioWrittenUltimate"
                        "[%period%]" {
                            "netCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netBestEstimate", {
                                "premiumWritten" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netPremiumWritten"
                                "claimInitial" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netClaimUltimate"
                                "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateGrossDetail" "ORSA:segments:[%segment%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:grossLossRatioWrittenUltimate"
                        "[%period%]" {
                            "grossCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossBestEstimate", {
                                "premiumWritten" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossPremiumWritten"
                                "claimInitial" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossClaimUltimate"
                                "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateCededDetail" "ORSA:segments:[%segment%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:cededLossRatioWrittenUltimate"
                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededBestEstimate", {
                                "premiumWritten" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededPremiumWritten"
                                "claimInitial" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededClaimUltimate"
                                "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededLossRatioWrittenUltimate"
                                "commission" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:segments:[%segment%]:outClaimsNet:ultimate"
                    "totalCumulative" "ORSA:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "ORSA:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "ORSA:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:segments:[%segment%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "ORSA:segments:[%segment%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "ORSA:segments:[%segment%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:segments:[%segment%]:outClaimsGross:ultimate"
                        "totalCumulative" "ORSA:segments:[%segment%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:segments:[%segment%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:segments:[%segment%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:segments:[%segment%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:segments:[%segment%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:segments:[%segment%]:outClaimsCeded:ultimate"
                        "totalCumulative" "ORSA:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "discountedValues" {
                    "paidIncrementalGross" "ORSA:segments:[%segment%]:outDiscountedValues:discountedPaidIncrementalGross"
                    "paidIncrementalNet" "ORSA:segments:[%segment%]:outDiscountedValues:discountedPaidIncrementalNet"
                    "paidIncrementalCeded" "ORSA:segments:[%segment%]:outDiscountedValues:discountedPaidIncrementalCeded"
                    "reservesGross" "ORSA:segments:[%segment%]:outDiscountedValues:discountedReservedGross"
                    "reservesNet" "ORSA:segments:[%segment%]:outDiscountedValues:discountedReservedNet"
                    "reservesCeded" "ORSA:segments:[%segment%]:outDiscountedValues:discountedReservedCeded"
                    "netPresentValuePaidGross" "ORSA:segments:[%segment%]:outNetPresentValues:netPresentValueGross"
                    "netPresentValuePaidNet" "ORSA:segments:[%segment%]:outNetPresentValues:netPresentValueNet"
                    "netPresentValuePaidCeded" "ORSA:segments:[%segment%]:outNetPresentValues:netPresentValueCeded"
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:segments:[%segment%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "ORSA:segments:[%segment%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "ORSA:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
        "[%legalEntity%]" "ORSA:legalEntities:[%legalEntity%]:outFinancials:netCashflow", {
            "financialsNetCashflow" "ORSA:legalEntities:[%legalEntity%]:outFinancials:netCashflow", {
                "lossRatio" "ORSA:legalEntities:[%legalEntity%]:outFinancials:netLossRatioWrittenUltimate"
                "premium" "ORSA:legalEntities:[%legalEntity%]:outFinancials:netPremiumPaid"
                "commission" "ORSA:legalEntities:[%legalEntity%]:outFinancials:commission"
                "claim" "ORSA:legalEntities:[%legalEntity%]:outFinancials:netClaimPaid"
                "[%period%]" {
                    "financialsNetCashflow" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:netCashflow", {
                        "lossRatio" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                        "premium" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:netPremiumPaid"
                        "commission" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outFinancials:commission"
                        "claim" "ORSA:legalEntities:[%legalEntity%]:outFinancials:period:[%period%]:netClaimPaid"
                    }
                }
            }
            "claimsNet" {
                "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:ultimate"
                "totalCumulative" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:totalCumulativeIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                }
                "totalIncremental" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:totalIncrementalIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                }
                "reportedCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reportedCumulativeIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                }
                "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                }
                "paidCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:paidCumulativeIndexed",{
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                }
                "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed",{
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                }
                "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                }
                "changesInOutstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:changesInOutstandingIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                }
                "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                }
                "changesInIBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:changesInIBNRIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                }
                "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reservesIndexed"
                }
                "changesInReservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:changesInReservesIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                }
                "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:premiumRiskBase"
                "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reserveRiskBase", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:premiumAndReserveRiskBase"
                "claimsGross" {
                    "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:ultimate", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:ultimate"
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:ultimate"
                    }
                    "totalCumulative" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:totalCumulativeIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:totalCumulativeIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:totalIncrementalIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:totalIncrementalIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reportedCumulativeIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:paidCumulativeIndexed",{
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidCumulativeIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:paidCumulativeIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidCumulativeIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:paidCumulativeIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:paidIncrementalIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:outstandingIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:outstandingIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:outstandingIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:outstandingIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:outstandingIndexed"
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:changesInOutstandingIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:changesInOutstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:IBNRIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:IBNRIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:IBNRIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:IBNRIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:IBNRIndexed"
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:changesInIBNRIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:changesInIBNRIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:changesInIBNRIndexed"
                        }
                    }
                    "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reservesIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reservesIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reservesIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reservesIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reservesIndexed"
                        }
                    }
                    "changesInReservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:changesInReservesIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInReservesIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:changesInReservesIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInReservesIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:changesInReservesIndexed"
                        }
                    }
                    "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:premiumRiskBase"
                    "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reserveRiskBase", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:premiumAndReserveRiskBase"
                }
                "claimsCeded" {
                    "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:ultimate"
                    "totalCumulative" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:totalCumulativeIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                    }
                    "totalIncremental" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:totalIncrementalIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedCumulativeIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:paidCumulativeIndexed",{
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed",{
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInOutstandingIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInIBNRIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                    }
                    "changesInReservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInReservesIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:premiumRiskBase"
                    "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reserveRiskBase", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:premiumAndReserveRiskBase"
                }
            }
            "premium" {
                "premiumWrittenNet" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumWritten", {
                    "gross" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumWritten", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoGross:premiumWritten"
                    }
                    "ceded" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumWritten", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                }
                "premiumPaidNet" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumPaid", {
                    "gross" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumPaid", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                    }
                    "ceded" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaid", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                        "fixed" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidFixed"
                        "variable" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidVariable"
                    }
                }
            }
            "commission" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commission", {
                "fixed" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commissionFixed"
                "variable" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:commissionVariable"
            }
        }
        "structures" {
            "[%structure%]" {
                "cashflow" {
                    "cashflowNetDetail" "ORSA:structures:[%structure%]:outFinancials:netCashflow", {
                        "premiumPaid" "ORSA:structures:[%structure%]:outFinancials:netPremiumPaid"
                        "claimPaid" "ORSA:structures:[%structure%]:outFinancials:netClaimPaid"
                        "lossRatioPaidPaid" "ORSA:structures:[%structure%]:outFinancials:netLossRatioPaidPaid"
                        "commission" "ORSA:structures:[%structure%]:outFinancials:commission"
                        "[%period%]" {
                            "netCashflow" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netCashflow", {
                                "premiumPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netPremiumPaid"
                                "claimPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netClaimPaid"
                                "lossRatioPaidPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netLossRatioPaidPaid"
                                "commission" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowNetPeriod" "ORSA:structures:[%structure%]:outFinancials:netCashflow", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netCashflow"
                    }
                    "riskNet" {
                        "premiumRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:netPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:netReserveRisk", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:structures:[%structure%]:outFinancials:netPremiumReserveRisk"
                    }

                    "cashflowGrossDetail" "ORSA:structures:[%structure%]:outFinancials:grossCashflow", {
                        "premiumPaid" "ORSA:structures:[%structure%]:outFinancials:grossPremiumPaid"
                        "claimPaid" "ORSA:structures:[%structure%]:outFinancials:grossClaimPaid"
                        "lossRatioPaidPaid" "ORSA:structures:[%structure%]:outFinancials:grossLossRatioPaidPaid"
                        "[%period%]" {
                            "grossCashflow" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossCashflow", {
                                "premiumPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossPremiumPaid"
                                "claimPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossClaimPaid"
                                "lossRatioPaidPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossLossRatioPaidPaid"
                            }
                        }
                    }
                    "cashflowGrossPeriod" "ORSA:structures:[%structure%]:outFinancials:grossCashflow", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossCashflow"
                    }
                    "riskGross" {
                        "premiumRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:grossPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:grossReserveRisk", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:structures:[%structure%]:outFinancials:grossPremiumReserveRisk"
                    }

                    "cashflowCededDetail" "ORSA:structures:[%structure%]:outFinancials:cededCashflow", {
                        "premiumPaid" "ORSA:structures:[%structure%]:outFinancials:cededPremiumPaid"
                        "claimPaid" "ORSA:structures:[%structure%]:outFinancials:cededClaimPaid"
                        "lossRatioPaidPaid" "ORSA:structures:[%structure%]:outFinancials:cededLossRatioPaidPaid"
                        "commission" "ORSA:structures:[%structure%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededCashflow", {
                                "premiumPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededPremiumPaid"
                                "claimPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededClaimPaid"
                                "lossRatioPaidPaid" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededLossRatioPaidPaid"
                                "commission" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                    "cashflowCededPeriod" "ORSA:structures:[%structure%]:outFinancials:cededCashflow", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededCashflow"
                    }
                    "riskCeded" {
                        "premiumRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:cededPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:cededReserveRisk", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededReserveRisk"
                        }
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:structures:[%structure%]:outFinancials:cededPremiumReserveRisk"
                    }
                }
                "bestEstimate" {
                    "bestEstimateNetDetail" "ORSA:structures:[%structure%]:outFinancials:netBestEstimate", {
                        "premiumWritten" "ORSA:structures:[%structure%]:outFinancials:netPremiumWritten"
                        "claimInitial" "ORSA:structures:[%structure%]:outFinancials:netClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:outFinancials:netLossRatioWrittenUltimate"
                        "[%period%]" {
                            "netCashflow" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netBestEstimate", {
                                "premiumWritten" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netPremiumWritten"
                                "claimInitial" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netClaimUltimate"
                                "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateGrossDetail" "ORSA:structures:[%structure%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "ORSA:structures:[%structure%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "ORSA:structures:[%structure%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:outFinancials:grossLossRatioWrittenUltimate"
                        "[%period%]" {
                            "grossCashflow" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossBestEstimate", {
                                "premiumWritten" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossPremiumWritten"
                                "claimInitial" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossClaimUltimate"
                                "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:grossLossRatioWrittenUltimate"
                            }
                        }
                    }
                    "bestEstimateCededDetail" "ORSA:structures:[%structure%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "ORSA:structures:[%structure%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "ORSA:structures:[%structure%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:outFinancials:cededLossRatioWrittenUltimate"
                        "commission" "ORSA:structures:[%structure%]:outFinancials:commission"
                        "[%period%]" {
                            "cededCashflow" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededBestEstimate", {
                                "premiumWritten" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededPremiumWritten"
                                "claimInitial" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededClaimUltimate"
                                "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:cededLossRatioWrittenUltimate"
                                "commission" "ORSA:structures:[%structure%]:period:[%period%]:outFinancials:commission"
                            }
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:structures:[%structure%]:outClaimsNet:ultimate"
                    "totalCumulative" "ORSA:structures:[%structure%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "ORSA:structures:[%structure%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "ORSA:structures:[%structure%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "ORSA:structures:[%structure%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "ORSA:structures:[%structure%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:structures:[%structure%]:outClaimsGross:ultimate"
                        "totalCumulative" "ORSA:structures:[%structure%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:structures:[%structure%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:structures:[%structure%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:structures:[%structure%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:structures:[%structure%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:structures:[%structure%]:outClaimsCeded:ultimate"
                        "totalCumulative" "ORSA:structures:[%structure%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:structures:[%structure%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:structures:[%structure%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:structures:[%structure%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "ORSA:structures:[%structure%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "ORSA:structures:[%structure%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "Financials" {
                    "result" "ORSA:reinsuranceContracts:[%contract%]:outContractFinancials:contractResult", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:contractResult", {
                            "premium" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:cededPremium"
                            "commission" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:cededCommission"
                            "claim" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outContractFinancials:cededClaim"
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate"
                    "totalCumulative" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "totalCumulative" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "totalCumulative" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
        "retrospectiveReinsurance" {
            "[%contract%]" {
                "Financials" {
                    "result" "ORSA:retrospectiveReinsurance:[%contract%]:outContractFinancials:contractResult", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:contractResult", {
                            "premium" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:cededPremium"
                            "commission" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:cededCommission"
                            "claim" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outContractFinancials:cededClaim"
                        }
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:ultimate"
                    "totalCumulative" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:totalCumulativeIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "totalIncremental" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:totalIncrementalIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "reportedCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedCumulativeIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidCumulativeIndexed",{
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidIncrementalIndexed",{
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "changesInOutstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInOutstandingIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "changesInIBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInIBNRIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:changesInIBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "changesInReservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInReservesIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:ultimate"
                        "totalCumulative" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInReservesIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:ultimate"
                        "totalCumulative" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:totalCumulativeIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "totalIncremental" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "reportedCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidCumulativeIndexed",{
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidIncrementalIndexed",{
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "changesInOutstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "changesInIBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInIBNRIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:changesInIBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "changesInReservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInReservesIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "netByUnderwritingYear" {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoNet:premiumPaid"
                        }
                        "gross" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "cededByUnderwritingYear" {
                                "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "fixed" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
    }
}