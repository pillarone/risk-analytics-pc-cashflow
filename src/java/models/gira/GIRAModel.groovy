package models.gira

import models.AbstractModel
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerators
import org.pillarone.riskanalytics.domain.pc.cf.creditrisk.CreditDefault
import org.pillarone.riskanalytics.domain.pc.cf.dependency.Dependencies
import org.pillarone.riskanalytics.domain.pc.cf.dependency.EventGenerators
import org.pillarone.riskanalytics.domain.pc.cf.discounting.DiscountingYieldCurves
import org.pillarone.riskanalytics.domain.pc.cf.exposure.RiskBands
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingSegments
import org.pillarone.riskanalytics.domain.pc.cf.global.GlobalParameters
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Indices
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntities
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.ReinsuranceContracts
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.RetrospectiveCoverAttributeStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.period.PeriodStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerator
import org.pillarone.riskanalytics.domain.pc.cf.reserve.ReservesGenerators
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segments
import org.pillarone.riskanalytics.domain.pc.cf.structure.Structures
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.domain.pc.cf.pattern.*
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.RetrospectiveReinsuranceContracts

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class GIRAModel extends AbstractModel {

    ReinsuranceContracts reinsuranceContracts
    RetrospectiveReinsuranceContracts retrospectiveReinsurance
    Structures structures

    @Override
    void initComponents() {
        super.initComponents()
        reinsuranceContracts = new ReinsuranceContracts()
        retrospectiveReinsurance = new RetrospectiveReinsuranceContracts()
        structures = new Structures()
    }

    @Override
    void wireComponents() {
        super.wireComponents()
        if (segments.subComponentCount() == 0) {
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
        else {
            segments.inClaims = claimsGenerators.outClaims
            segments.inClaims = reservesGenerators.outReserves
            segments.inUnderwritingInfo = underwritingSegments.outUnderwritingInfo
            segments.inFactors = discounting.outFactors
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
            if (legalEntities.subComponentCount() > 0) {
                legalEntities.inDefaultProbabilities = creditDefault.outDefaultProbabilities
                segments.inLegalEntityDefault = legalEntities.outLegalEntityDefault
                reinsuranceContracts.inLegalEntityDefault = legalEntities.outLegalEntityDefault
                retrospectiveReinsurance.inLegalEntityDefault = legalEntities.outLegalEntityDefault
                legalEntities.inClaims = segments.outClaimsGross
                legalEntities.inUnderwritingInfo = segments.outUnderwritingInfoGross
                legalEntities.inClaimsCeded = reinsuranceContracts.outClaimsCeded
                legalEntities.inClaimsInward = reinsuranceContracts.outClaimsInward
                legalEntities.inUnderwritingInfoCeded = reinsuranceContracts.outUnderwritingInfoCeded
                legalEntities.inUnderwritingInfoInward = reinsuranceContracts.outUnderwritingInfoInward
                legalEntities.inClaimsCeded2 = retrospectiveReinsurance.outClaimsCeded
                legalEntities.inClaimsInward2 = retrospectiveReinsurance.outClaimsInward
                legalEntities.inUnderwritingInfoCeded2 = retrospectiveReinsurance.outUnderwritingInfoCeded
                legalEntities.inUnderwritingInfoInward2 = retrospectiveReinsurance.outUnderwritingInfoInward
            }
        }
    }

    @Override
    List<IParameterObjectClassifier> configureClassifier(String path, List<IParameterObjectClassifier> classifiers) {
        if (path.matches("reinsuranceContracts:(.*):parmCover")) {
            return [
                    CoverAttributeStrategyType.NONE,
                    CoverAttributeStrategyType.ORIGINALCLAIMS,
                    CoverAttributeStrategyType.CONTRACTS,
                    CoverAttributeStrategyType.LEGALENTITIES,
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
