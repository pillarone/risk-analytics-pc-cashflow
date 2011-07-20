package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Details"

mappings = {
    GIRA {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "reportedIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed"
                "paidIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed"
                "outstandingIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed"
                "IBNRIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNRIndexed"
                "reservesIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed"
                "increaseDueToIndex" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResultIndexed"
                "numberOfClaims" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
                "severityIndices" {
                    "continuous" "GIRA:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:continuous"
                    "stepwise, previous" "GIRA:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:stepwisePrevious"
                    "stepwise, next" "GIRA:claimsGenerators:[%claimsGenerator%]:outSeverityIndexApplied:stepwiseNext"
                }
            }
        }
        "reservesIndexed" {
            "[%reservesGenerator%]" {
                "ultimateFromInceptionPeriod" "GIRA:reservesGenerators:[%reservesGenerator%]:outNominalUltimates:value"
                "reportedIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reportedIncrementalIndexed"
                "paidIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:paidIncrementalIndexed"
                "outstandingIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:outstandingIndexed"
                "IBNRIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:IBNRIndexed"
                "reservesIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reservesIndexed"
                "increaseDueToIndex" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:developedResultIndexed"
            }
        }
        "grossUnderwritingWritten" "GIRA:underwritingSegments:outUnderwritingInfo:premiumWritten"
        "grossUnderwritingPaid" "GIRA:underwritingSegments:outUnderwritingInfo:premiumPaid"
        "grossUnderwritingBySegment" {
            "[%underwritingSegment%]" {
                "premiumWritten" "GIRA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumWritten"
                "premiumPaid" "GIRA:underwritingSegments:[%underwritingSegment%]:outUnderwritingInfo:premiumPaid"
                "policyIndex" "GIRA:underwritingSegments:[%underwritingSegment%]:outPolicyIndexApplied:value"
                "premiumIndex" "GIRA:underwritingSegments:[%underwritingSegment%]:outPremiumIndexApplied:value"
            }
        }
        "segments" {
            "[%segment%]" {
                "claimsNet" {
                    "ultimate" "GIRA:segments:[%segment%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed"
                    "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:paidIncrementalIndexed"
                    "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsNet:outstandingIndexed"
                    "IBNRIndexed" "GIRA:segments:[%segment%]:outClaimsNet:IBNRIndexed"
                    "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reservesIndexed"
                    "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsNet:developedResultIndexed"
                    "claimsGross" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsGross:outstandingIndexed"
                        "IBNRIndexed" "GIRA:segments:[%segment%]:outClaimsGross:IBNRIndexed"
                        "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reservesIndexed"
                        "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsGross:developedResultIndexed"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:segments:[%segment%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:outstandingIndexed"
                        "IBNRIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:IBNRIndexed"
                        "reservesIndexed" "GIRA:segments:[%segment%]:outClaimsCeded:reservesIndexed"
                        "increaseDueToIndex" "GIRA:segments:[%segment%]:outClaimsCeded:developedResultIndexed"
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
                        "gross" "GIRA:segments:[%segment%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "GIRA:segments:[%segment%]:outUnderwritingInfoCeded:premiumPaid", {
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
        "structures" {
            "[%structure%]" {
                "claimsNet" {
                    "ultimate" "GIRA:structures:[%structure%]:outClaimsNet:ultimate"
                    "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reportedIncrementalIndexed"
                    "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsNet:paidIncrementalIndexed"
                    "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsNet:outstandingIndexed"
                    "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsNet:IBNRIndexed"
                    "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsNet:reservesIndexed"
                    "increaseDueToIndex" "GIRA:structures:[%structure%]:outClaimsNet:developedResultIndexed"
                    "claimsGross" {
                        "ultimate" "GIRA:structures:[%structure%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsGross:outstandingIndexed"
                        "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsGross:IBNRIndexed"
                        "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reservesIndexed"
                        "increaseDueToIndex" "GIRA:structures:[%structure%]:outClaimsGross:developedResultIndexed"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:structures:[%structure%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:outstandingIndexed"
                        "IBNRIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:IBNRIndexed"
                        "reservesIndexed" "GIRA:structures:[%structure%]:outClaimsCeded:reservesIndexed"
                        "increaseDueToIndex" "GIRA:structures:[%structure%]:outClaimsCeded:developedResultIndexed"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:structures:[%structure%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:structures:[%structure%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:structures:[%structure%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "GIRA:structures:[%structure%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "GIRA:structures:[%structure%]:outUnderwritingInfoCeded:premiumPaid", {
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
                    "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reportedIncrementalIndexed"
                    "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:paidIncrementalIndexed"
                    "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:outstandingIndexed"
                    "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:IBNRIndexed"
                    "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:reservesIndexed"
                    "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsNet:developedResultIndexed"
                    "claimsGross" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:ultimate"
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:outstandingIndexed"
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:IBNRIndexed"
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:reservesIndexed"
                        "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsGross:developedResultIndexed"
                    }
                    "claimsCeded" {
                        "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate"
                        "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed"
                        "paidIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:paidIncrementalIndexed"
                        "outstandingIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:outstandingIndexed"
                        "IBNRIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:IBNRIndexed"
                        "reservesIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reservesIndexed"
                        "increaseDueToIndex" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:developedResultIndexed"
                    }
                }
                "premium" {
                    "premiumWrittenNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumWritten", {
                        "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumWritten"
                        "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumWritten"
                    }
                    "premiumPaidNet" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoNet:premiumPaid", {
                        "gross" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoGross:premiumPaid"
                        "ceded" "GIRA:reinsuranceContracts:[%contract%]:outUnderwritingInfoCeded:premiumPaid", {
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
        "claims" {
            "ultimate" "GIRA:claimsGenerators:outClaims:ultimate", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
            }
            "reportedIncrementalIndexed" "GIRA:claimsGenerators:outClaims:reportedIncrementalIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed"
            }
            "paidIncrementalIndexed" "GIRA:claimsGenerators:outClaims:paidIncrementalIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:paidIncrementalIndexed"
            }
            "outstandingIndexed" "GIRA:claimsGenerators:outClaims:outstandingIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:outstandingIndexed"
            }
            "IBNRIndexed" "GIRA:claimsGenerators:outClaims:IBNRIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:IBNRIndexed"
            }
            "reservesIndexed" "GIRA:claimsGenerators:outClaims:reservesIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reservesIndexed"
            }
            "increaseDueToIndex" "GIRA:claimsGenerators:outClaims:developedResultIndexed", {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:developedResultIndexed"
            }
            "numberOfClaims" {
                "[%claimsGenerator%]" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaimNumber:value"
            }
        }

    }
}