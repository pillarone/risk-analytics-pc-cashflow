package models.gira

/**
 * @author shartmann (at) munichre (dot) com
 */
model = GIRAModel
displayName = "Risk View, simplified"

mappings = {
    GIRA {
        "grossClaims" {
            "[%claimsGenerator%]" {
                "ultimate" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:ultimate"
                "reportedIncrementalIndexed" "GIRA:claimsGenerators:[%claimsGenerator%]:outClaims:reportedIncrementalIndexed"
            }
        }
        "reservesIndexed" {
            "[%reservesGenerator%]" {
                "ultimateFromInceptionPeriod" "GIRA:reservesGenerators:[%reservesGenerator%]:outNominalUltimates:value"
                "reportedIncrementalIndexed" "GIRA:reservesGenerators:[%reservesGenerator%]:outReserves:reportedIncrementalIndexed"
            }
        }
        "grossUnderwriting" {
            "premiumWritten" "GIRA:underwritingSegments:outUnderwritingInfo:premiumWritten"
            "premiumPaid" "GIRA:underwritingSegments:outUnderwritingInfo:premiumPaid"
        }
        "segments" {
            "totalOfAllSegments" {
                "claimsGross" {
                    "ultimate" "GIRA:segments:outClaimsGross:ultimate", {
                        "[%peril%]" "GIRA:segments:claimsGenerators:[%peril%]:outClaimsGross:ultimate"      // todo
                    }
                    "reportedIncrementalIndexed" "GIRA:segments:outClaimsGross:reportedIncrementalIndexed", {
                        "[%peril%]" "GIRA:segments:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"    // todo
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:outClaimsGross:premiumAndReserveRiskBase"
                    "premiumRiskBase" "GIRA:segments:outClaimsGross:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:outClaimsGross:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reserveRiskBase"
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:segments:outClaimsNet:ultimate", {
                        "[%peril%]" "GIRA:segments:claimsGenerators:[%peril%]:outClaimsNet:ultimate"    // todo
                    }
                    "reportedIncrementalIndexed" "GIRA:segments:outClaimsNet:reportedIncrementalIndexed", {
                        "[%peril%]" "GIRA:segments:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"  // todo
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:outClaimsNet:premiumAndReserveRiskBase"
                    "premiumRiskBase" "GIRA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                }
            }
            "[%segment%]" {
                "claimsGross" {
                    "ultimate" "GIRA:segments:[%segment%]:outClaimsGross:ultimate", {
                        "[%peril%]" "GIRA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                    }
                    "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed", {
                        "[%peril%]" "GIRA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBase"
                    "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:segments:[%segment%]:outClaimsNet:ultimate", {
                            "[%peril%]" "GIRA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                    }
                    "reportedIncrementalIndexed" "GIRA:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed", {
                            "[%peril%]" "GIRA:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                    }
                    "premiumAndReserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBase"
                    "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                }
            }
        }
        "structures" {
            "[%structure%]" {
                "claimsGross" {
                    "ultimate" "GIRA:structures:[%structure%]:outClaimsGross:ultimate", {
                        "bySegment" {
                            "[%segment%]" "GIRA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:ultimate"
                        }
                        "byPeril" {
                            "[%peril%]" "GIRA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:ultimate"
                        }
                    }
                    "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                        "bySegment" {
                            "[%segment%]" "GIRA:structures:[%structure%]:segments:[%segment%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                        "byPeril" {
                            "[%peril%]" "GIRA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsGross:reportedIncrementalIndexed"
                        }
                    }
                    "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:premiumAndReserveRiskBase"
                    "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:premiumRiskBase"
                    "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsGross:reserveRiskBase", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                    }
                }
                "claimsNet" {
                    "ultimate" "GIRA:structures:[%structure%]:outClaimsGross:ultimate", {
                        "bySegment" {
                            "[%segment%]" "GIRA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:ultimate"
                        }
                        "byPeril" {
                            "[%peril%]" "GIRA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:ultimate"
                        }
//                        "byContract" {
//                            // todo
//                        }
                    }
                    "reportedIncrementalIndexed" "GIRA:structures:[%structure%]:outClaimsGross:reportedIncrementalIndexed", {
                        "bySegment" {
                            "[%segment%]" "GIRA:structures:[%structure%]:segments:[%segment%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                        "byPeril" {
                            "[%peril%]" "GIRA:structures:[%structure%]:claimsGenerators:[%peril%]:outClaimsNet:reportedIncrementalIndexed"
                        }
                    }
                    "premiumAndReserveRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:premiumAndReserveRiskBase"
                    "premiumRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:structures:[%structure%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:structures:[%structure%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                }
            }
        }
        "reinsurance" {
            "[%contract%]" {
                "claimsCeded" {
                    "ultimate" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:ultimate", {
                        "bySegment" {
                            "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:ultimate", {
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"    // todo
                                }
                            }
                        }
                        "byPerils" {
                            "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:ultimate"
                        }
                    }
                    "reportedIncrementalIndexed" "GIRA:reinsuranceContracts:[%contract%]:outClaimsCeded:reportedIncrementalIndexed", {
                        "bySegment" {
                            "[%segment%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:outClaimsCeded:reportedIncrementalIndexed", {
                                "byPerils" {
                                    "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:segments:[%segment%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"  // todo
                                }
                            }
                        }
                        "byPerils" {
                            "[%peril%]" "GIRA:reinsuranceContracts:[%contract%]:claimsGenerators:[%peril%]:outClaimsCeded:reportedIncrementalIndexed"
                        }
                    }
                }
            }
        }
//        // todo : add retrospective paths
    }
}

