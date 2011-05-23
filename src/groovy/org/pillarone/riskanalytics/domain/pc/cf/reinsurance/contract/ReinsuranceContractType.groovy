package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.TableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.LimitStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.PremiumBase
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.PremiumAllocationType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit.ILimitStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.QuotaShareContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.WXLConstractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.allocation.IPremiumAllocationStrategy
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.StopLossPremiumBase
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.TrivialContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractType extends AbstractParameterObjectClassifier {

    public static final ReinsuranceContractType QUOTASHARE = new ReinsuranceContractType("quota share", "QUOTASHARE",
            ["quotaShare": 0d, "limit": LimitStrategyType.getDefault(), 'commission': CommissionStrategyType.getNoCommission()])
    public static final ReinsuranceContractType SURPLUS = new ReinsuranceContractType("surplus", "SURPLUS",
            ["retention": 0d, "lines": 0d, "defaultCededLossShare": 0d])
    public static final ReinsuranceContractType WXL = new ReinsuranceContractType("wxl", "WXL", [
            "aggregateDeductible":0d, "attachmentPoint": 0d, "limit": 0d,
            "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType CXL = new ReinsuranceContractType("cxl", "CXL", [
            "aggregateDeductible":0d, "attachmentPoint": 0d, "limit": 0d,
            "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType WCXL = new ReinsuranceContractType("wcxl", "WCXL", [
            "aggregateDeductible":0d, "attachmentPoint": 0d, "limit": 0d,
            "aggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])
    public static final ReinsuranceContractType STOPLOSS = new ReinsuranceContractType("stop loss", "STOPLOSS",
            ["stopLossContractBase": StopLossPremiumBase.ABSOLUTE, "attachmentPoint": 0d, "limit": 0d, "premium": 0d,
                    "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:])])
    public static final ReinsuranceContractType TRIVIAL = new ReinsuranceContractType("trivial", "TRIVIAL", [:])
    public static final ReinsuranceContractType AGGREGATEXL = new ReinsuranceContractType("aggregate xl", "AggregateXL",
            ["attachmentPoint": 0d, "limit": 0d, "premiumBase": PremiumBase.ABSOLUTE,
                    "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
                    "premium": 0d, "claimClass": ClaimType.AGGREGATED_EVENT])
    public static final ReinsuranceContractType LOSSPORTFOLIOTRANSFER = new ReinsuranceContractType("loss portfolio transfer",
            "LOSSPORTFOLIOTRANSFER", ["quotaShare": 0d, "premiumBase": LPTPremiumBase.ABSOLUTE, "premium": 0d])
    public static final ReinsuranceContractType ADVERSEDEVELOPMENTCOVER = new ReinsuranceContractType("adverse development cover",
            "ADVERSEDEVELOPMENTCOVER",
            ["attachmentPoint": 0d, "limit": 0d, "stopLossContractBase": StopLossPremiumBase.ABSOLUTE, "premium": 0d])
    public static final ReinsuranceContractType GOLDORAK = new ReinsuranceContractType("goldorak", "GOLDORAK",
            ["cxlAttachmentPoint": 0d, "cxlLimit": 0d,
            "cxlAggregateDeductible": 0d, "cxlAggregateLimit": 0d, "premiumBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "slAttachmentPoint": 0d, "slLimit": 0d, "goldorakSlThreshold": 0d,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])

    public static final all = [QUOTASHARE, /*SURPLUS,*/ WXL, /*CXL,
            WCXL, STOPLOSS,*/ TRIVIAL/*, LOSSPORTFOLIOTRANSFER, ADVERSEDEVELOPMENTCOVER, GOLDORAK*/]

    protected static Map types = [:]
    static {
        ReinsuranceContractType.all.each {
            ReinsuranceContractType.types[it.toString()] = it
        }
    }

    private ReinsuranceContractType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ReinsuranceContractType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IReinsuranceContractStrategy getDefault() {
        return new TrivialContractStrategy()
    }

    static IReinsuranceContractStrategy getStrategy(ReinsuranceContractType type, Map parameters) {
        IReinsuranceContractStrategy contract
        switch (type) {
            case ReinsuranceContractType.QUOTASHARE:
                return new QuotaShareContractStrategy(
                        quotaShare: (double) parameters[QuotaShareContractStrategy.QUOTASHARE],
                        limit: (ILimitStrategy) parameters[QuotaShareContractStrategy.LIMIT],
                        commission: (ICommissionStrategy) parameters[QuotaShareContractStrategy.COMMISSION])
                break
            case ReinsuranceContractType.WXL:
                return new WXLConstractStrategy(
                        attachmentPoint: (double) parameters[WXLConstractStrategy.ATTACHMENT_POINT],
                        limit: (double) parameters[WXLConstractStrategy.LIMIT],
                        aggregateLimit: (double) parameters[WXLConstractStrategy.AGGREGATE_LIMIT],
                        aggregateDeductible: (double) parameters[WXLConstractStrategy.AGGREGATE_DEDUCTIBLE],
                        premiumBase: (PremiumBase) parameters[WXLConstractStrategy.PREMIUM_BASE],
                        premium: (double) parameters[WXLConstractStrategy.PREMIUM],
                        premiumAllocation: (IPremiumAllocationStrategy) parameters[WXLConstractStrategy.PREMIUM_ALLOCATION],
                        reinstatementPremiums: (AbstractMultiDimensionalParameter) parameters[WXLConstractStrategy.REINSTATEMENT_PREMIUMS])
                break
//            case ReinsuranceContractType.CXL:
//                contract = getCXL(
//                        (double) parameters["attachmentPoint"],
//                        (double) parameters["limit"],
//                        (double) parameters["aggregateLimit"],
//                        (double) parameters["aggregateDeductible"] == null ? 0 : (double) parameters["aggregateDeductible"],
//                        (PremiumBase) parameters["premiumBase"],
//                        (double) parameters["premium"],
//                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
//                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.WCXL:
//                contract = getWCXL(
//                        (double) parameters["attachmentPoint"],
//                        (double) parameters["limit"],
//                        (double) parameters["aggregateLimit"],
//                        (double) parameters["aggregateDeductible"] == null ? 0 : (double) parameters["aggregateDeductible"],
//                        (PremiumBase) parameters["premiumBase"],
//                        (double) parameters["premium"],
//                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
//                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.STOPLOSS:
//                contract = getStopLoss(
//                        (StopLossPremiumBase) parameters["stopLossContractBase"],
//                        (double) parameters["attachmentPoint"],
//                        (double) parameters["limit"],
//                        (double) parameters["premium"],
//                        (IPremiumAllocationStrategy) parameters["premiumAllocation"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.SURPLUS:
//                contract = getSurplus(
//                        (double) parameters["retention"],
//                        (double) parameters["lines"],
//                        (double) parameters["defaultCededLossShare"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.SURPLUSREVERSE:
//                contract = getComplementarySurplus(
//                        (double) parameters["retention"],
//                        (double) parameters["lines"],
//                        (double) parameters["defaultCededLossShare"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.TRIVIAL:
//                contract = getTrivial()
//                break
//            case ReinsuranceContractType.LOSSPORTFOLIOTRANSFER:
//                contract = getLossPortfolioTransferContractStrategy(
//                        (double) parameters["quotaShare"],
//                        (LPTPremiumBase) parameters["premiumBase"],
//                        (double) parameters["premium"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.ADVERSEDEVELOPMENTCOVER:
//                contract = getAdverseDevelopmentCover(
//                        (StopLossPremiumBase) parameters["stopLossContractBase"],
//                        (double) parameters["attachmentPoint"],
//                        (double) parameters["limit"],
//                        (double) parameters["premium"],
//                        (double) parameters["coveredByReinsurer"])
//                break
//            case ReinsuranceContractType.GOLDORAK:
//                contract = getGoldorak(
//                        (double) parameters["cxlAttachmentPoint"],
//                        (double) parameters["cxlLimit"],
//                        (double) parameters["cxlAggregateDeductible"] == null ? 0 : (double) parameters["cxlAggregateDeductible"],
//                        (double) parameters["cxlAggregateLimit"],
//                        (PremiumBase) parameters["premiumBase"],
//                        (double) parameters["premium"],
//                        (AbstractMultiDimensionalParameter) parameters["reinstatementPremiums"],
//                        (double) parameters["coveredByReinsurer"],
//                        (double) parameters["slAttachmentPoint"],
//                        (double) parameters["slLimit"],
//                        (double) parameters["goldorakSlThreshold"])
//                break
        }
        return contract
    }

    // todo(sku): not sure if this belongs here or rather in its own factory
    static IReinsuranceContract getContract(IReinsuranceContractStrategy contractStrategy) {

    }


}
