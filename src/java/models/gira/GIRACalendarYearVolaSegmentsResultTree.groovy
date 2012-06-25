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
//                "financialsNetCashflow" "GIRA:segments:outNetFinancials:netCashflow", {
//                    "lossRatio" "GIRA:segments:outNetFinancials:lossRatio"
//                    "premium" "GIRA:segments:outNetFinancials:netPremiumPaid"
//                    "commission" "GIRA:segments:outNetFinancials:commission"
//                    "claim" "GIRA:segments:outNetFinancials:netClaimPaid"
//                    "[%period%]" {
//                        "financialsNetCashflow" "GIRA:segments:period:[%period%]:outNetFinancials:netCashflow", {
//                            "lossRatio" "GIRA:segments:period:[%period%]:outNetFinancials:lossRatio"
//                            "premium" "GIRA:segments:period:[%period%]:outNetFinancials:netPremiumPaid"
//                            "commission" "GIRA:segments:period:[%period%]:outNetFinancials:commission"
//                            "claim" "GIRA:segments:period:[%period%]:outNetFinancials:netClaimPaid"
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
            "[%segment%]" "GIRA:segments:[%segment%]:outNetFinancials:netCashflow", {
//                "financialsNetCashflow" "GIRA:segments:[%segment%]:outNetFinancials:netCashflow", {
//                    "lossRatio" "GIRA:segments:[%segment%]:outNetFinancials:lossRatio"
//                    "premium" "GIRA:segments:[%segment%]:outNetFinancials:netPremiumPaid"
//                    "commission" "GIRA:segments:[%segment%]:outNetFinancials:commission"
//                    "claim" "GIRA:segments:[%segment%]:outNetFinancials:netClaimPaid"
//                    "[%period%]" {
//                        "financialsNetCashflow" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:netCashflow", {
//                            "lossRatio" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:lossRatio"
//                            "premium" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:netPremiumPaid"
//                            "commission" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:commission"
//                            "claim" "GIRA:segments:[%segment%]:period:[%period%]:outNetFinancials:netClaimPaid"
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