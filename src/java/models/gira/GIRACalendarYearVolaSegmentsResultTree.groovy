package models.gira

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
model = GIRAModel
displayName = "Segments, Calendar Year Volatility"

mappings = {
    GIRA {
        "segments" {
            "totalOfAllSegments" {
//                "financialsNetCashflow" "GIRA:segments:outFinancials:netCashflow", {
//                    "netLossRatioWrittenUltimate" "GIRA:segments:outFinancials:netLossRatioWrittenUltimate"
//                    "premium" "GIRA:segments:outFinancials:netPremiumPaid"
//                    "commission" "GIRA:segments:outFinancials:commission"
//                    "claim" "GIRA:segments:outFinancials:netClaimPaid"
//                    "[%period%]" {
//                        "financialsNetCashflow" "GIRA:segments:period:[%period%]:outFinancials:netCashflow", {
//                            "netLossRatioWrittenUltimate" "GIRA:segments:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
//                            "premium" "GIRA:segments:period:[%period%]:outFinancials:netPremiumPaid"
//                            "commission" "GIRA:segments:period:[%period%]:outFinancials:commission"
//                            "claim" "GIRA:segments:period:[%period%]:outFinancials:netClaimPaid"
//                        }
//                    }
//                }
                "claimsNet" {
                    "premiumRiskBase" "GIRA:segments:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBaseBase" "GIRA:segments:outClaimsNet:premiumAndReserveRiskBaseBase"
                    "claimsGross" {
                        "premiumRiskBase" "GIRA:segments:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBaseBase" "GIRA:segments:outClaimsGross:premiumAndReserveRiskBaseBase"
                    }
                    "claimsCeded" {
                        "premiumRiskBase" "GIRA:segments:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBaseBase" "GIRA:segments:outClaimsCeded:premiumAndReserveRiskBaseBase"
                    }
                }
            }
            "[%segment%]" "GIRA:segments:[%segment%]:outFinancials:netCashflow", {
//                "financialsNetCashflow" "GIRA:segments:[%segment%]:outFinancials:netCashflow", {
//                    "netLossRatioWrittenUltimate" "GIRA:segments:[%segment%]:outFinancials:netLossRatioWrittenUltimate"
//                    "premium" "GIRA:segments:[%segment%]:outFinancials:netPremiumPaid"
//                    "commission" "GIRA:segments:[%segment%]:outFinancials:commission"
//                    "claim" "GIRA:segments:[%segment%]:outFinancials:netClaimPaid"
//                    "[%period%]" {
//                        "financialsNetCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netCashflow", {
//                            "netLossRatioWrittenUltimate" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netLossRatioWrittenUltimate"
//                            "premium" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netPremiumPaid"
//                            "commission" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:commission"
//                            "claim" "GIRA:segments:[%segment%]:period:[%period%]:outFinancials:netClaimPaid"
//                        }
//                    }
//                }
                "claimsNet" {
                    "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumRiskBase"
                    "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsNet:reserveRiskBase", {
                        "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsNet:reserveRiskBase"
                    }
                    "premiumAndReserveRiskBaseBase" "GIRA:segments:[%segment%]:outClaimsNet:premiumAndReserveRiskBaseBase"
                    "claimsGross" {
                        "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsGross:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsGross:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBaseBase" "GIRA:segments:[%segment%]:outClaimsGross:premiumAndReserveRiskBaseBase"
                    }
                    "claimsCeded" {
                        "premiumRiskBase" "GIRA:segments:[%segment%]:outClaimsCeded:premiumRiskBase"
                        "reserveRiskBase" "GIRA:segments:[%segment%]:outClaimsCeded:reserveRiskBase", {
                            "[%period%]" "GIRA:segments:[%segment%]:period:[%period%]:outClaimsCeded:reserveRiskBase"
                        }
                        "premiumAndReserveRiskBaseBase" "GIRA:segments:[%segment%]:outClaimsCeded:premiumAndReserveRiskBaseBase"
                    }
                }

            }
        }
    }
}