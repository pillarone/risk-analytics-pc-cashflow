package models.orsa

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = ORSAModel
displayName = "Split by Source"

mappings = {
    ORSA {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "totalCumulative" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:totalCumulativeIndexed"
                "totalIncremental" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:totalIncrementalIndexed"
                "reportedCumulativeIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedCumulativeIndexed"
                "reportedIncrementalIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed"
                "paidCumulativeIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:paidCumulativeIndexed"
                "paidIncrementalIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed"
                "outstandingIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed"
                "changesInOutstandingIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInOutstandingIndexed"
                "IBNRIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNRIndexed"
                "changesInIBNRIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInIBNRIndexed"
                "reservesIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed"
                "changesInReservesIndexed" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:changesInReservesIndexed"
                "premiumRiskBase" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:reserveRiskBase"
                "premiumAndReserveRiskBase" "ORSA:claimsGenerators:[%claimsGenerator%]:outClaims:premiumAndReserveRiskBase"
            }
        }
        "reservesIndexed" {
            "[%reservesGenerator%]" {
                "ultimate" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:ultimate"
                "totalCumulative" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:totalCumulativeIndexed"
                "totalIncremental" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:totalIncrementalIndexed"
                "reportedCumulativeIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reportedCumulativeIndexed"
                "reportedIncrementalIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reportedIncrementalIndexed"
                "paidCumulativeIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:paidCumulativeIndexed"
                "paidIncrementalIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:paidIncrementalIndexed"
                "outstandingIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:outstandingIndexed"
                "changesInOutstandingIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInOutstandingIndexed"
                "IBNRIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:IBNRIndexed"
                "changesInIBNRIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInIBNRIndexed"
                "reservesIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reservesIndexed"
                "changesInReservesIndexed" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:changesInReservesIndexed"
                "premiumRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:premiumRiskBase"
                "reserveRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:reserveRiskBase"
                "premiumAndReserveRiskBase" "ORSA:reservesGenerators:[%reservesGenerator%]:outClaims:premiumAndReserveRiskBase"
            }
        }
        "grossUnderwritingBySegment" {
            "[%underwritingSegment%]" {
                "premiumWritten" "ORSA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumWritten"
                "premiumPaid" "ORSA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumPaid"
            }
        }
        "segments" {
            "totalOfAllSegments" {
                "financialsNetCashflow" "ORSA:segments:outFinancials:netCashflow", {
                    "lossRatio" "ORSA:segments:outFinancials:netLossRatioWrittenUltimate"
                    "premium" "ORSA:segments:outFinancials:netPremiumPaid"
                    "commission" "ORSA:segments:outFinancials:commission"
                    "claim" "ORSA:segments:outFinancials:netClaimPaid"
                }
                "claimsNet" {
                    "ultimate" "ORSA:segments:outClaimsNet:ultimate"
                    "totalCumulative" "ORSA:segments:outClaimsNet:totalCumulativeIndexed"
                    "totalIncremental" "ORSA:segments:outClaimsNet:totalIncrementalIndexed"
                    "reportedCumulativeIndexed" "ORSA:segments:outClaimsNet:reportedCumulativeIndexed"
                    "reportedIncrementalIndexed" "ORSA:segments:outClaimsNet:reportedIncrementalIndexed"
                    "paidCumulativeIndexed" "ORSA:segments:outClaimsNet:paidCumulativeIndexed"
                    "paidIncrementalIndexed" "ORSA:segments:outClaimsNet:paidIncrementalIndexed"
                    "outstandingIndexed" "ORSA:segments:outClaimsNet:outstandingIndexed"
                    "changesInOutstandingIndexed" "ORSA:segments:outClaimsNet:changesInOutstandingIndexed"
                    "IBNRIndexed" "ORSA:segments:outClaimsNet:IBNRIndexed"
                    "changesInIBNRIndexed" "ORSA:segments:outClaimsNet:changesInIBNRIndexed"
                    "reservesIndexed" "ORSA:segments:outClaimsNet:reservesIndexed"
                    "changesInReservesIndexed" "ORSA:segments:outClaimsNet:changesInReservesIndexed"
                    "premiumRiskBase" "ORSA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "ORSA:segments:outClaimsNet:reserveRiskBase"
                    "premiumAndReserveRiskBase" "ORSA:segments:outClaimsNet:premiumAndReserveRiskBase"
                    "claimsGross" {
                        "ultimate" "ORSA:segments:outClaimsGross:ultimate"
                        "totalCumulative" "ORSA:segments:outClaimsGross:totalCumulativeIndexed"
                        "totalIncremental" "ORSA:segments:outClaimsGross:totalIncrementalIndexed"
                        "reportedCumulativeIndexed" "ORSA:segments:outClaimsGross:reportedCumulativeIndexed"
                        "reportedIncrementalIndexed" "ORSA:segments:outClaimsGross:reportedIncrementalIndexed"
                        "paidCumulativeIndexed" "ORSA:segments:outClaimsGross:paidCumulativeIndexed"
                        "paidIncrementalIndexed" "ORSA:segments:outClaimsGross:paidIncrementalIndexed"
                        "outstandingIndexed" "ORSA:segments:outClaimsGross:outstandingIndexed"
                        "changesInOutstandingIndexed" "ORSA:segments:outClaimsGross:changesInOutstandingIndexed"
                        "IBNRIndexed" "ORSA:segments:outClaimsGross:IBNRIndexed"
                        "changesInIBNRIndexed" "ORSA:segments:outClaimsGross:changesInIBNRIndexed"
                        "reservesIndexed" "ORSA:segments:outClaimsGross:reservesIndexed"
                        "changesInReservesIndexed" "ORSA:segments:outClaimsGross:changesInReservesIndexed"
                        "premiumRiskBase" "ORSA:segments:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:outClaimsGross:reserveRiskBase"
                        "premiumAndReserveRiskBase" "ORSA:segments:outClaimsGross:premiumAndReserveRiskBase"
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:segments:outClaimsCeded:ultimate"
                        "totalCumulative" "ORSA:segments:outClaimsCeded:totalCumulativeIndexed"
                        "totalIncremental" "ORSA:segments:outClaimsCeded:totalIncrementalIndexed"
                        "reportedCumulativeIndexed" "ORSA:segments:outClaimsCeded:reportedCumulativeIndexed"
                        "reportedIncrementalIndexed" "ORSA:segments:outClaimsCeded:reportedIncrementalIndexed"
                        "paidCumulativeIndexed" "ORSA:segments:outClaimsCeded:paidCumulativeIndexed"
                        "paidIncrementalIndexed" "ORSA:segments:outClaimsCeded:paidIncrementalIndexed"
                        "outstandingIndexed" "ORSA:segments:outClaimsCeded:outstandingIndexed"
                        "changesInOutstandingIndexed" "ORSA:segments:outClaimsCeded:changesInOutstandingIndexed"
                        "IBNRIndexed" "ORSA:segments:outClaimsCeded:IBNRIndexed"
                        "changesInIBNRIndexed" "ORSA:segments:outClaimsCeded:changesInIBNRIndexed"
                        "reservesIndexed" "ORSA:segments:outClaimsCeded:reservesIndexed"
                        "changesInReservesIndexed" "ORSA:segments:outClaimsCeded:changesInReservesIndexed"
                        "premiumRiskBase" "ORSA:segments:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "ORSA:segments:outClaimsCeded:reserveRiskBase"
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
                        "gross" "ORSA:segments:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "ORSA:segments:outUnderwritingInfoCeded:premiumPaid", {
                            "fixed" "ORSA:segments:outUnderwritingInfoCeded:premiumPaidFixed"
                            "variable" "ORSA:segments:outUnderwritingInfoCeded:premiumPaidVariable"
                        }
                    }
                }
                "commission" "ORSA:segments:outUnderwritingInfoCeded:commission", {
                    "fixed" "ORSA:segments:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "ORSA:segments:outUnderwritingInfoCeded:commissionVariable"
                }
            }
            "[%segment%]" "ORSA:segments:[%segment%]:outFinancials:netCashflow", {
                "cashflow" {
                    "cashflowNetDetail" "ORSA:segments:[%segment%]:outFinancials:netCashflow", {
                        "premiumPaid" "ORSA:segments:[%segment%]:outFinancials:netPremiumPaid"
                        "claimPaid" "ORSA:segments:[%segment%]:outFinancials:netClaimPaid"
                        "lossRatioPaidPaid" "ORSA:segments:[%segment%]:outFinancials:netLossRatioPaidPaid"
                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
                    }
                    "cashflowNetPeriod" "ORSA:segments:[%segment%]:outFinancials:netCashflow"
                    "riskNet" {
                        "premiumRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:netPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:netReserveRisk"
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:segments:[%segment%]:outFinancials:netPremiumReserveRisk"
                    }

                    "cashflowGrossDetail" "ORSA:segments:[%segment%]:outFinancials:grossCashflow", {
                        "premiumPaid" "ORSA:segments:[%segment%]:outFinancials:grossPremiumPaid"
                        "claimPaid" "ORSA:segments:[%segment%]:outFinancials:grossClaimPaid"
                        "lossRatioPaidPaid" "ORSA:segments:[%segment%]:outFinancials:grossLossRatioPaidPaid"
                    }
                    "cashflowGrossPeriod" "ORSA:segments:[%segment%]:outFinancials:grossCashflow"
                    "riskGross" {
                        "premiumRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:grossPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:grossReserveRisk"
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:segments:[%segment%]:outFinancials:grossPremiumReserveRisk"
                    }

                    "cashflowCededDetail" "ORSA:segments:[%segment%]:outFinancials:cededCashflow", {
                        "premiumPaid" "ORSA:segments:[%segment%]:outFinancials:cededPremiumPaid"
                        "claimPaid" "ORSA:segments:[%segment%]:outFinancials:cededClaimPaid"
                        "lossRatioPaidPaid" "ORSA:segments:[%segment%]:outFinancials:cededLossRatioPaidPaid"
                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
                    }
                    "cashflowCededPeriod" "ORSA:segments:[%segment%]:outFinancials:cededCashflow"
                    "riskCeded" {
                        "premiumRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:cededPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:segments:[%segment%]:outFinancials:cededReserveRisk"
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:segments:[%segment%]:outFinancials:cededPremiumReserveRisk"
                    }
                }
                "bestEstimate" {
                    "bestEstimateNetDetail" "ORSA:segments:[%segment%]:outFinancials:netBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:netPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:netClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:netLossRatioWrittenUltimate"
                    }
                    "bestEstimateGrossDetail" "ORSA:segments:[%segment%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:grossLossRatioWrittenUltimate"
                    }
                    "bestEstimateCededDetail" "ORSA:segments:[%segment%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "ORSA:segments:[%segment%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "ORSA:segments:[%segment%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:segments:[%segment%]:outFinancials:cededLossRatioWrittenUltimate"
                        "commission" "ORSA:segments:[%segment%]:outFinancials:commission"
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:segments:[%segment%]:outClaimsNet:ultimate", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                    }
                    "totalCumulative" "ORSA:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "ORSA:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsNet:outstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "ORSA:segments:[%segment%]:outClaimsNet:IBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:segments:[%segment%]:outClaimsNet:changesInIBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                    }
                    "reservesIndexed" "ORSA:segments:[%segment%]:outClaimsNet:reservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                    }
                    "changesInReservesIndexed" "ORSA:segments:[%segment%]:outClaimsNet:changesInReservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                    }
                    "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:premiumRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                    }
                    "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                    }
                    "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                    }
                    "claimsGross" {
                        "ultimate" "ORSA:segments:[%segment%]:outClaimsGross:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:segments:[%segment%]:outClaimsGross:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsGross:paidCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsGross:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:segments:[%segment%]:outClaimsGross:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:segments:[%segment%]:outClaimsGross:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:segments:[%segment%]:outClaimsGross:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:segments:[%segment%]:outClaimsGross:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:segments:[%segment%]:outClaimsGross:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:segments:[%segment%]:outClaimsCeded:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:segments:[%segment%]:outClaimsCeded:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:segments:[%segment%]:outClaimsCeded:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:segments:[%segment%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                        }
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
                        "ceded" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten", {
                            "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                        }
                    }
                    "premiumPaidNet" "ORSA:segments:[%segment%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "ORSA:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid", {
                            "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid"
                            "fixed" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed", {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                            }
                            "variable" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable", {
                                "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                            }
                        }
                    }
                }
                "commission" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:commission", {
                    "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission"
                    "fixed" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:commissionFixed", {
                        "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "ORSA:segments:[%segment%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%contract%]" "ORSA:segments:[%segment%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
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
            }
            "claimsNet" {
                "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:ultimate", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:ultimate"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:ultimate"
                    }
                }
                "totalCumulative" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:totalCumulativeIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed"
                    }
                }
                "totalIncremental" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:totalIncrementalIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed"
                    }
                }
                "reportedCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reportedCumulativeIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed"
                    }
                }
                "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reportedIncrementalIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                }
                "paidCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:paidCumulativeIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed"
                    }
                }
                "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:paidIncrementalIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed"
                    }
                }
                "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:outstandingIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:outstandingIndexed"
                    }
                }
                "changesInOutstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:changesInOutstandingIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed"
                    }
                }
                "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:IBNRIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:IBNRIndexed"
                    }
                }
                "changesInIBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:changesInIBNRIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:ultimate"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:ultimate"
                    }
                }
                "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reservesIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:reservesIndexed"
                    }
                }
                "changesInReservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:changesInReservesIndexed", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:changesInReservesIndexed"
                    }
                }
                "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:premiumRiskBase", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    }
                }
                "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:reserveRiskBase", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:reserveRiskBase"
                    }
                }
                "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsNet:premiumAndReserveRiskBase", {
                    "byPeril" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                    }
                    "byReserve" {
                        "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                    }
                    "bySegment" {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                    }
                }
                "claimsGross" {
                    "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:ultimate", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:ultimate", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:ultimate"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:ultimate", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:ultimate"
                            }
                        }
                    }
                    "totalCumulative" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:totalCumulativeIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:totalCumulativeIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:totalCumulativeIndexed"
                            }
                        }
                    }
                    "totalIncremental" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:totalIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:totalIncrementalIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:totalIncrementalIndexed"
                            }
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reportedCumulativeIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:reportedCumulativeIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:reportedCumulativeIndexed"
                            }
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reportedIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:reportedIncrementalIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:reportedIncrementalIndexed"
                            }
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:paidCumulativeIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:paidCumulativeIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:paidCumulativeIndexed"
                            }
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:paidIncrementalIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:paidIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:paidIncrementalIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:paidIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:paidIncrementalIndexed"
                            }
                        }
                    }
                    "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:outstandingIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:outstandingIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:outstandingIndexed"
                            }
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:changesInOutstandingIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:changesInOutstandingIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:changesInOutstandingIndexed"
                            }
                        }
                    }
                    "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:IBNRIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:IBNRIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:IBNRIndexed"
                            }
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:changesInIBNRIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:changesInIBNRIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:changesInIBNRIndexed"
                            }
                        }
                    }
                    "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reservesIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:reservesIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:reservesIndexed"
                            }
                        }
                    }
                    "changesInReservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:changesInReservesIndexed", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:changesInReservesIndexed"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:changesInReservesIndexed"
                            }
                        }
                    }
                    "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:premiumRiskBase", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:premiumRiskBase"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:premiumRiskBase"
                            }
                        }
                    }
                    "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:reserveRiskBase", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:reserveRiskBase"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:reserveRiskBase"
                            }
                        }
                    }
                    "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsGross:premiumAndReserveRiskBase", {
                        "primaryInsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsPrimaryInsurer:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsPrimaryInsurer:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsPrimaryInsurer:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsPrimaryInsurer:premiumAndReserveRiskBase"
                            }
                        }
                        "reinsurer" "GIRA:legalEntities:[%legalEntity%]:outClaimsReinsurer:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsReinsurer:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "GIRA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsReinsurer:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "GIRA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsReinsurer:premiumAndReserveRiskBase"
                            }
                        }
                    }
                }
                "claimsCeded" {
                    "ultimate" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:ultimate", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:ultimate"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:ultimate"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        }
                    }
                    "totalCumulative" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:totalCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:totalIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reportedIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:paidCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidCumulativeIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:paidIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:outstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:outstandingIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed"
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInOutstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInOutstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:IBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:IBNRIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed"
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInIBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:ultimate"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:ultimate"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        }
                    }
                    "reservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:reservesIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed"
                        }
                    }
                    "changesInReservesIndexed" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:changesInReservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInReservesIndexed"
                        }
                    }
                    "premiumRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:premiumRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRiskBase"
                        }
                    }
                    "reserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:reserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:reserveRiskBase"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRiskBase"
                        }
                    }
                    "premiumAndReserveRiskBase" "ORSA:legalEntities:[%legalEntity%]:outClaimsCeded:premiumAndReserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:legalEntities:[%legalEntity%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
                        }
                    }
                }
            }
            "premium" {
                "premiumWrittenNet" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumWritten", {
                    "gross" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumWritten", {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                    }
                    "ceded" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumWritten", {
                        "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                }
                "premiumPaidNet" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoNet:premiumPaid", {
                    "gross" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoGross:premiumPaid", {
                        "[%segment%]" "ORSA:legalEntities:[%legalEntity%]:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                    }
                    "ceded" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaid", {
                        "fixed" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidFixed"
                        "variable" "ORSA:legalEntities:[%legalEntity%]:outUnderwritingInfoCeded:premiumPaidVariable"
                        "byContract" {
                            "[%contract%]" "ORSA:legalEntities:[%legalEntity%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid"
                        }
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
                    }
                    "cashflowNetPeriod" "ORSA:structures:[%structure%]:outFinancials:netCashflow"
                    "riskNet" {
                        "premiumRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:netPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:netReserveRisk"
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:structures:[%structure%]:outFinancials:netPremiumReserveRisk"
                    }

                    "cashflowGrossDetail" "ORSA:structures:[%structure%]:outFinancials:grossCashflow", {
                        "premiumPaid" "ORSA:structures:[%structure%]:outFinancials:grossPremiumPaid"
                        "claimPaid" "ORSA:structures:[%structure%]:outFinancials:grossClaimPaid"
                        "lossRatioPaidPaid" "ORSA:structures:[%structure%]:outFinancials:grossLossRatioPaidPaid"
                    }
                    "cashflowGrossPeriod" "ORSA:structures:[%structure%]:outFinancials:grossCashflow"
                    "riskGross" {
                        "premiumRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:grossPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:grossReserveRisk"
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:structures:[%structure%]:outFinancials:grossPremiumReserveRisk"
                    }

                    "cashflowCededDetail" "ORSA:structures:[%structure%]:outFinancials:cededCashflow", {
                        "premiumPaid" "ORSA:structures:[%structure%]:outFinancials:cededPremiumPaid"
                        "claimPaid" "ORSA:structures:[%structure%]:outFinancials:cededClaimPaid"
                        "lossRatioPaidPaid" "ORSA:structures:[%structure%]:outFinancials:cededLossRatioPaidPaid"
                        "commission" "ORSA:structures:[%structure%]:outFinancials:commission"
                    }
                    "cashflowCededPeriod" "ORSA:structures:[%structure%]:outFinancials:cededCashflow"
                    "riskCeded" {
                        "premiumRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:cededPremiumRisk"
                        "reserveRiskOnFinancials" "ORSA:structures:[%structure%]:outFinancials:cededReserveRisk"
                        "premiumAndReserveRiskBasedOnFinancials" "ORSA:structures:[%structure%]:outFinancials:cededPremiumReserveRisk"
                    }
                }
                "bestEstimate" {
                    "bestEstimateNetDetail" "ORSA:structures:[%structure%]:outFinancials:netBestEstimate", {
                        "premiumWritten" "ORSA:structures:[%structure%]:outFinancials:netPremiumWritten"
                        "claimInitial" "ORSA:structures:[%structure%]:outFinancials:netClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:outFinancials:netLossRatioWrittenUltimate"
                    }
                    "bestEstimateGrossDetail" "ORSA:structures:[%structure%]:outFinancials:grossBestEstimate", {
                        "premiumWritten" "ORSA:structures:[%structure%]:outFinancials:grossPremiumWritten"
                        "claimInitial" "ORSA:structures:[%structure%]:outFinancials:grossClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:outFinancials:grossLossRatioWrittenUltimate"
                    }
                    "bestEstimateCededDetail" "ORSA:structures:[%structure%]:outFinancials:cededBestEstimate", {
                        "premiumWritten" "ORSA:structures:[%structure%]:outFinancials:cededPremiumWritten"
                        "claimInitial" "ORSA:structures:[%structure%]:outFinancials:cededClaimUltimate"
                        "lossRatioWrittenUltimate" "ORSA:structures:[%structure%]:outFinancials:cededLossRatioWrittenUltimate"
                        "commission" "ORSA:structures:[%structure%]:outFinancials:commission"
                    }
                }
                "claimsNet" {
                    "ultimate" "ORSA:structures:[%structure%]:outClaimsNet:ultimate", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:ultimate"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate"
                        }
                    }
                    "totalCumulative" "ORSA:structures:[%structure%]:outClaimsNet:totalCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "ORSA:structures:[%structure%]:outClaimsNet:totalIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reportedCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reportedIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsNet:paidCumulativeIndexed",{
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:paidCumulativeIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsNet:paidIncrementalIndexed",{
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsNet:outstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:outstandingIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed"
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:structures:[%structure%]:outClaimsNet:changesInOutstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsNet:IBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:IBNRIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed"
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:structures:[%structure%]:outClaimsNet:changesInIBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:changesInIBNRIndexed"
                        }
                    }
                    "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsNet:reservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:reservesIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed"
                        }
                    }
                    "changesInReservesIndexed" "ORSA:structures:[%structure%]:outClaimsNet:changesInReservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:changesInReservesIndexed"
                        }
                    }
                    "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:premiumRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRiskBase"
                        }
                    }
                    "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:reserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:reserveRiskBase"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRiskBase"
                        }
                    }
                    "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsNet:premiumAndReserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "byContract" {
                            "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                    }
                    "claimsGross" {
                        "ultimate" "ORSA:structures:[%structure%]:outClaimsGross:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:ultimate"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:structures:[%structure%]:outClaimsGross:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:structures:[%structure%]:outClaimsGross:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsGross:paidCumulativeIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsGross:paidIncrementalIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsGross:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:outstandingIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:structures:[%structure%]:outClaimsGross:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsGross:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:IBNRIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:structures:[%structure%]:outClaimsGross:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsGross:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:reservesIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:structures:[%structure%]:outClaimsGross:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:reserveRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsGross:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:structures:[%structure%]:outClaimsCeded:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:ultimate"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:structures:[%structure%]:outClaimsCeded:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:structures:[%structure%]:outClaimsCeded:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:paidCumulativeIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:paidIncrementalIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:outstandingIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:IBNRIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:reservesIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:structures:[%structure%]:outClaimsCeded:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:structures:[%structure%]:outClaimsCeded:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:structures:[%structure%]:outClaimsCeded:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:reserveRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:structures:[%structure%]:outClaimsCeded:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:structures:[%structure%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                        }
                    }
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:structures:[%structure%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:structures:[%structure%]:outUnderwritingInfoGross:premiumWritten", {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        }
                        "ceded" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumWritten", {
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"   
                            }
                        }
                    }
                    "premiumPaidNet" "ORSA:structures:[%structure%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "ORSA:structures:[%structure%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaid", {
                            "bySegment" {
                                "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "byContract" {
                                "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaidFixed", {
                                "bySegment" {
                                    "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                                }
                                "byContract" {
                                    "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed"
                                }
                            }
                            "variable" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaidVariable", {
                                "bySegment" {
                                    "[%segment%]" "ORSA:structures:[%structure%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                                }
                                "byContract" {
                                    "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable"
                                }
                            }
                        }
                    }
                }
                "commission" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:commission", {
                    "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission"
                    "fixed" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:commissionFixed",  {
                        "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                    }
                    "variable" "ORSA:structures:[%structure%]:outUnderwritingInfoCeded:commissionVariable", {
                        "[%contract%]" "ORSA:structures:[%structure%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
                    }
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "Financials" {
                    "result" "ORSA:reinsuranceContracts:[%contract%]:outContractFinancials:contractResult"
                }
                "claimsNet" {
                    "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:ultimate", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:ultimate"
                        }
                    }
                    "totalCumulative" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:totalCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:totalIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:paidCumulativeIndexed",{
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed",{
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:outstandingIndexed"
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInOutstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:IBNRIndexed"
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInIBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:changesInIBNRIndexed"
                        }
                    }
                    "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:reservesIndexed"
                        }
                    }
                    "changesInReservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:changesInReservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:changesInReservesIndexed"
                        }
                    }
                    "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                        }
                    }
                    "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:reserveRiskBase"
                        }
                    }
                    "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsNet:premiumAndReserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                    }
                    "claimsGross" {
                        "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:paidCumulativeIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsGross:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidCumulativeIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:reinsuranceContracts:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:reinsuranceContracts:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                        }
                    }
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten", {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        }
                        "ceded" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten", {
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                            }
                        }
                    }
                    "premiumPaidNet" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "bySegment" {
                                "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed", {
                                "bySegment" {
                                    "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                                }
                            }
                            "variable" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable", {
                                "bySegment" {
                                    "[%segment%]" "ORSA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                                }
                            }
                        }
                    }
                }
                "commission" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "[%contract%]" "ORSA:reinsuranceContracts:[%contract%]:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commission"
                    "fixed" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "ORSA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
        "retrospectiveReinsurance" {
            "[%contract%]" {
                "Financials" {
                    "result" "ORSA:retrospectiveReinsurance:[%contract%]:outContractFinancials:contractResult"
                }
                "claimsNet" {
                    "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:ultimate", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:ultimate"
                        }
                    }
                    "totalCumulative" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:totalCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:totalCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:totalCumulativeIndexed"
                        }
                    }
                    "totalIncremental" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:totalIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:totalIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:totalIncrementalIndexed"
                        }
                    }
                    "reportedCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedCumulativeIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:reportedCumulativeIndexed"
                        }
                    }
                    "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reportedIncrementalIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                    }
                    "paidCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidCumulativeIndexed",{
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:paidCumulativeIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:paidCumulativeIndexed"
                        }
                    }
                    "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:paidIncrementalIndexed",{
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:paidIncrementalIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed"
                        }
                    }
                    "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:outstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:outstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:outstandingIndexed"
                        }
                    }
                    "changesInOutstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInOutstandingIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:changesInOutstandingIndexed"
                        }
                    }
                    "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:IBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:IBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:IBNRIndexed"
                        }
                    }
                    "changesInIBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInIBNRIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:changesInIBNRIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:changesInIBNRIndexed"
                        }
                    }
                    "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:reservesIndexed"
                        }
                    }
                    "changesInReservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:changesInReservesIndexed", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:changesInReservesIndexed"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:changesInReservesIndexed"
                        }
                    }
                    "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:premiumRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                        }
                    }
                    "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:reserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:reserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:reserveRiskBase"
                        }
                    }
                    "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsNet:premiumAndReserveRiskBase", {
                        "byPeril" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "byReserve" {
                            "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                        "bySegment" {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                        }
                    }
                    "claimsGross" {
                        "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidCumulativeIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:paidIncrementalIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsGross:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                            }
                        }
                    }
                    "claimsCeded" {
                        "ultimate" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:ultimate", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:ultimate"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:ultimate"
                            }
                        }
                        "totalCumulative" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:totalCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:totalCumulativeIndexed"
                            }
                        }
                        "totalIncremental" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:totalIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:totalIncrementalIndexed"
                            }
                        }
                        "reportedCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedCumulativeIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:reportedCumulativeIndexed"
                            }
                        }
                        "reportedIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                            }
                        }
                        "paidCumulativeIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidCumulativeIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:paidCumulativeIndexed"
                            }
                        }
                        "paidIncrementalIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:paidIncrementalIndexed",{
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                            }
                        }
                        "outstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:outstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:outstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:outstandingIndexed"
                            }
                        }
                        "changesInOutstandingIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInOutstandingIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:changesInOutstandingIndexed"
                            }
                        }
                        "IBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:IBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:IBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:IBNRIndexed"
                            }
                        }
                        "changesInIBNRIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInIBNRIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:changesInIBNRIndexed"
                            }
                        }
                        "reservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:reservesIndexed"
                            }
                        }
                        "changesInReservesIndexed" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:changesInReservesIndexed", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:changesInReservesIndexed"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:changesInReservesIndexed"
                            }
                        }
                        "premiumRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:premiumRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                            }
                        }
                        "reserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:reserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:reserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:reserveRiskBase"
                            }
                        }
                        "premiumAndReserveRiskBase" "ORSA:retrospectiveReinsurance:[%contract%]:outClaimsCeded:premiumAndReserveRiskBase", {
                            "byPeril" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "byReserve" {
                                "[%peril%]" "ORSA:retrospectiveReinsurance:[%contract%]:reservesGenerators:[%peril%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBase"
                            }
                        }
                    }
                }
                "premium" {
                    "premiumWrittenNet" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoGross:premiumWritten", {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outUnderwritingInfoGross:premiumWritten"
                        }
                        "ceded" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumWritten", {
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumWritten"
                            }
                        }
                    }
                    "premiumPaidNet" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoGross:premiumPaid", {
                            "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        }
                        "ceded" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
                            "bySegment" {
                                "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid"
                            }
                            "fixed" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaidFixed", {
                                "bySegment" {
                                    "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidFixed"
                                }
                            }
                            "variable" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:premiumPaidVariable", {
                                "bySegment" {
                                    "[%segment%]" "ORSA:retrospectiveReinsurance:[%contract%]:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaidVariable"
                                }
                            }
                        }
                    }
                }
                "commission" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commission", {
                    "[%contract%]" "ORSA:retrospectiveReinsurance:[%contract%]:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commission"
                    "fixed" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commissionFixed"
                    "variable" "ORSA:retrospectiveReinsurance:[%contract%]:outUnderwritingInfoCeded:commissionVariable"
                }
            }
        }
    }
}