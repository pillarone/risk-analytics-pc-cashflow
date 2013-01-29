package models.orsa

import models.MultiPeriodPCModel
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.MatrixReinsuranceContracts
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.RetroactiveReinsuranceContracts
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.structure.Structures

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ORSAModel extends MultiPeriodPCModel {

    MatrixReinsuranceContracts reinsuranceContracts
    RetroactiveReinsuranceContracts retrospectiveReinsurance
    Structures structures

    @Override
    void initComponents() {
        super.initComponents()
        reinsuranceContracts = new MatrixReinsuranceContracts()
        retrospectiveReinsurance = new RetroactiveReinsuranceContracts()
        structures = new Structures()
    }

    @Override
    void wireWithoutSegments() {
        reinsuranceContracts.inClaims = claimsGenerators.outClaims
        reinsuranceContracts.inClaims = reservesGenerators.outReserves
        reinsuranceContracts.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        reinsuranceContracts.inFactors = indices.outFactors
        retrospectiveReinsurance.inClaims = claimsGenerators.outClaims
        retrospectiveReinsurance.inClaims = reservesGenerators.outReserves
        retrospectiveReinsurance.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
        if (structures.subComponentCount() > 0) {
            structures.inClaimsGross = claimsGenerators.outClaims
            structures.inClaimsCeded = reinsuranceContracts.outClaimsCeded
            structures.inClaimsCeded = retrospectiveReinsurance.outClaimsCeded
            structures.inUnderwritingInfoGross = underwritingSegments.outUnderwritingInfo
            structures.inUnderwritingInfoCeded = reinsuranceContracts.outUnderwritingInfoCeded
            structures.inUnderwritingInfoCeded = retrospectiveReinsurance.outUnderwritingInfoCeded
        }
    }

    @Override
    void wireWithSegments() {
        reinsuranceContracts.inClaims = segments.outClaimsGross
        reinsuranceContracts.inUnderwritingInfo = segments.outUnderwritingInfoGross
        segments.inClaimsCeded = reinsuranceContracts.outClaimsCeded
        segments.inUnderwritingInfoCeded = reinsuranceContracts.outUnderwritingInfoCeded
        retrospectiveReinsurance.inClaims = segments.outClaimsGross
        retrospectiveReinsurance.inUnderwritingInfo = segments.outUnderwritingInfoGross
        segments.inClaimsCeded = retrospectiveReinsurance.outClaimsCeded
        segments.inUnderwritingInfoCeded = retrospectiveReinsurance.outUnderwritingInfoCeded
        if (structures.subComponentCount() > 0) {
            structures.inClaimsGross = segments.outClaimsGross
            structures.inClaimsCeded = segments.outClaimsCeded
            structures.inUnderwritingInfoGross = segments.outUnderwritingInfoGross
            structures.inUnderwritingInfoCeded = segments.outUnderwritingInfoCeded
        }
    }

    @Override
    void wireWithLegalEntities() {
        reinsuranceContracts.inLegalEntityDefault = legalEntities.outLegalEntityDefault
        retrospectiveReinsurance.inLegalEntityDefault = legalEntities.outLegalEntityDefault
        legalEntities.inClaimsCeded = reinsuranceContracts.outClaimsCeded
        legalEntities.inClaimsInward = reinsuranceContracts.outClaimsInward
        legalEntities.inUnderwritingInfoCeded = reinsuranceContracts.outUnderwritingInfoCeded
        legalEntities.inUnderwritingInfoInward = reinsuranceContracts.outUnderwritingInfoInward
        legalEntities.inClaimsCeded2 = retrospectiveReinsurance.outClaimsCeded
        legalEntities.inClaimsInward2 = retrospectiveReinsurance.outClaimsInward
        legalEntities.inUnderwritingInfoCeded2 = retrospectiveReinsurance.outUnderwritingInfoCeded
        legalEntities.inUnderwritingInfoInward2 = retrospectiveReinsurance.outUnderwritingInfoInward
    }


    @Override
    List<IParameterObjectClassifier> configureClassifier(String path, List<IParameterObjectClassifier> classifiers) {
        if (path.matches("reinsuranceContracts:(.*):parmCover") || path.matches("retrospectiveReinsurance:(.*):parmCover")) {
            return [
                    CoverAttributeStrategyType.NONE,
                    CoverAttributeStrategyType.MATRIX,
            ]
        }
        if (path.matches("reinsuranceContracts:(.*):parmCoveredPeriod")) {
            return [
                    PeriodStrategyType.ANNUAL,
                    PeriodStrategyType.CUSTOM,
                    PeriodStrategyType.MONTHS,
                    PeriodStrategyType.ONEYEAR,
            ]
        }
        if (path.matches("reinsuranceContracts:(.*):parmContractStrategy")) {
            return [
                    ReinsuranceContractType.CXL,
                    ReinsuranceContractType.WXL,
                    ReinsuranceContractType.QUOTASHARE,
                    ReinsuranceContractType.SURPLUS,
                    ReinsuranceContractType.STOPLOSS,
                    ReinsuranceContractType.TRIVIAL,
            ]
        }
        if (path.matches("retrospectiveReinsurance:(.*):parmCoveredPeriod")) {
            return [
                    PeriodStrategyType.RETROACTIVE,
            ]
        }
        if (path.matches("retrospectiveReinsurance:(.*):parmContractStrategy")) {
            return [
                    ReinsuranceContractType.TRIVIAL,
                    ReinsuranceContractType.UNIFIEDADCLPT,
            ]
        }
        return classifiers
    }

    @Override
    Closure createResultNavigatorMapping() {
        return {
            entities {
                enclosedBy(prefix: ["legalEntities:sub"], suffix: [":"])
            }
            lob {
                or {
                    enclosedBy(prefix: ['segments:sub'], suffix: [':'])
                    conditionedOn(value: 'Aggregate') {
                        matching(toMatch: ["segments:(?!sub)"])
                    }
                }
            }
            peril {
                enclosedBy(prefix: ["claimsGenerators:sub", "reservesGenerators:sub"], suffix: [":"])
            }
            reinsuranceContractType {
                enclosedBy(prefix: ["reinsuranceContracts:sub", "retrospectiveReinsurance:sub"], suffix: [":"])
            }
            accountBasis {
                matching(toMatch: ["Gross", "Ceded", "Net"])
            }
            keyfigure {
                synonymousTo(category: "Field")
            }
        }
    }
}
