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
                "reportedIncrementalIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:reportedIncrementalIndexed"
                }
                "paidIncrementalIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed",{
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:paidIncrementalIndexed"
                }
                "outstandingIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:outstandingIndexed"
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
                "increaseDueToIndex" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResultIndexed", {
                    "[%period%]" "ORSA:claimsGenerators:[%claimsGenerator%]:period:[%period%]:outClaims:developedResultIndexed"
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
                "ultimateFromInceptionPeriod" "ORSA:reservesGenerators:[%reservesGenerator%]:outNominalUltimates:value"
                "reportedIncrementalIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:reportedIncrementalIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reportedIncrementalIndexed"
                }
                "paidIncrementalIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:paidIncrementalIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:paidIncrementalIndexed"
                }
                "outstandingIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:outstandingIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:outstandingIndexed"
                }
                "IBNRIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:IBNRIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:IBNRIndexed"
                }
                "reservesIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:reservesIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reservesIndexed"
                }
                "increaseDueToIndex" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:developedResultIndexed", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:developedResultIndexed"
                }
                "premiumRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:premiumRiskBase"
                "reserveRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:reserveRiskBase", {
                    "[%period%]" "ORSA:reservesGenerators:[%reservesGenerator%]:period:[%period%]:outReserves:reserveRiskBase"
                }
                "premiumAndReserveRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outReserves:premiumAndReserveRiskBase"
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
                    "reportedIncrementalIndexed" "ORSA:segments:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:segments:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:segments:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:outstandingIndexed"
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
                    "increaseDueToIndex" "ORSA:segments:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "ORSA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:segments:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:segments:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:segments:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:segments:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "ORSA:segments:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:outstandingIndexed"
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
                        "increaseDueToIndex" "ORSA:segments:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "ORSA:segments:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:segments:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "ORSA:segments:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:outstandingIndexed"
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
                        "increaseDueToIndex" "ORSA:segments:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "ORSA:segments:period:[%period%]:outClaimsCeded:developedResultIndexed"
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
//                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
//                        "[%period%]" {
//                            "netCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netCashflow", {
//                                "premiumPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netPremiumPaid"
//                                "claimPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netClaimPaid"
//                                "lossRatioPaidPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:netLossRatioPaidPaid"
//                                "commission" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
//                            }
//                       }
                    }
                    "bestEstimateGrossDetail" "ORSA:segments:[%segment%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:grossLossRatioWrittenUltimate"
//                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
//                        "[%period%]" {
//                            "grossCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossCashflow", {
//                                "premiumPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossPremiumPaid"
//                                "claimPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossClaimPaid"
//                                "lossRatioPaidPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:grossLossRatioPaidPaid"
//                                "commission" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
//                            }
//                       }
                    }
                    "bestEstimateCededDetail" "ORSA:segments:[%segment%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:cededLossRatioWrittenUltimate"
//                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
//                        "[%period%]" {
//                            "cededCashflow" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededCashflow", {
//                                "premiumPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededPremiumPaid"
//                                "claimPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededClaimPaid"
//                                "lossRatioPaidPaid" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:cededLossRatioPaidPaid"
//                                "commission" "ORSA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
//                            }
//                       }
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:segments:[%segment%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:outstandingIndexed"
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
                    "increaseDueToIndex" "ORSA:segments:[%segment%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:segments:[%segment%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:outstandingIndexed"
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
                        "increaseDueToIndex" "ORSA:segments:[%segment%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:segments:[%segment%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
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
                        "increaseDueToIndex" "ORSA:segments:[%segment%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
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
                        "[%period%]" "ORSA:segments:[%segment%]:period:[%period%]::outUnderwritingInfoCeded:commissionVariable"
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
                "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                }
                "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                }
                "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                }
                "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                }
                "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed", {
                    "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsNet:reservesIndexed"
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
                    "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:reportedIncrementalIndexed"
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
                    "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:IBNRIndexed", {
                        "primaryInsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:IBNRIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsPrimaryInsurer:IBNRIndexed"
                        }
                        "reinsurer" "ORSA:legalEntities:[%legalEntity%]:outClaimsReinsurer:IBNRIndexed", {
                            "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsReinsurer:IBNRIndexed"
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
                    "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:premiumRiskBase"
                    "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reserveRiskBase", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:premiumAndReserveRiskBase"
                }
                "claimsCeded" {
                    "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:ultimate"
                    "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed", {
                        "[%period%]" "ORSA:legalEntities:[%legalEntity%]:period:[%period%]:outClaimsCeded:reservesIndexed"
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
                "claimsNet" {
                    "ultimate" "ORSA:structures:[%structure%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "increaseDueToIndex" "ORSA:structures:[%structure%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:structures:[%structure%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "increaseDueToIndex" "ORSA:structures:[%structure%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:structures:[%structure%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "increaseDueToIndex" "ORSA:structures:[%structure%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "ORSA:structures:[%structure%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
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
                    "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "increaseDueToIndex" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "increaseDueToIndex" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "increaseDueToIndex" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "ORSA:reinsuranceContracts:[%contract%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
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
        "retroactive" {
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
                    "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidIncrementalIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:outstandingIndexed"
                    }
                    "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:IBNRIndexed"
                    }
                    "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reservesIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reservesIndexed"
                    }
                    "increaseDueToIndex" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:developedResultIndexed", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:developedResultIndexed"
                    }
                    "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:outstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:IBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reservesIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reservesIndexed"
                        }
                        "increaseDueToIndex" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:developedResultIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:developedResultIndexed"
                        }
                        "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidIncrementalIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:outstandingIndexed"
                        }
                        "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:IBNRIndexed"
                        }
                        "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:reservesIndexed"
                        }
                        "increaseDueToIndex" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:developedResultIndexed", {
                            "[%period%]" "ORSA:retrospectiveReinsurance:[%contract%]:period:[%period%]:outClaimsCeded:developedResultIndexed"
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