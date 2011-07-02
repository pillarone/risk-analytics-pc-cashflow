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

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.TrivialContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.ICommissionStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission.param.CommissionStrategyType
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.StopLossBase

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.CXLConstractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.WCXLConstractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional.StopLossConstractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.SurplusContractStrategy

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
            ["stopLossContractBase": StopLossBase.ABSOLUTE, "attachmentPoint": 0d, "limit": 0d, "premium": 0d,
                    "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:])])
    public static final ReinsuranceContractType TRIVIAL = new ReinsuranceContractType("trivial", "TRIVIAL", [:])
    public static final ReinsuranceContractType AGGREGATEXL = new ReinsuranceContractType("aggregate xl", "AggregateXL",
            ["attachmentPoint": 0d, "limit": 0d, "contractBase": PremiumBase.ABSOLUTE,
                    "premiumAllocation": PremiumAllocationType.getStrategy(PremiumAllocationType.PREMIUM_SHARES, [:]),
                    "premium": 0d, "claimClass": ClaimType.AGGREGATED_EVENT])
    public static final ReinsuranceContractType LOSSPORTFOLIOTRANSFER = new ReinsuranceContractType("loss portfolio transfer",
            "LOSSPORTFOLIOTRANSFER", ["quotaShare": 0d, "contractBase": LPTPremiumBase.ABSOLUTE, "premium": 0d])
    public static final ReinsuranceContractType ADVERSEDEVELOPMENTCOVER = new ReinsuranceContractType("adverse development cover",
            "ADVERSEDEVELOPMENTCOVER",
            ["attachmentPoint": 0d, "limit": 0d, "stopLossContractBase": StopLossBase.ABSOLUTE, "premium": 0d])
    public static final ReinsuranceContractType GOLDORAK = new ReinsuranceContractType("goldorak", "GOLDORAK",
            ["cxlAttachmentPoint": 0d, "cxlLimit": 0d,
            "cxlAggregateDeductible": 0d, "cxlAggregateLimit": 0d, "contractBase": PremiumBase.ABSOLUTE, "premium": 0d,
            "slAttachmentPoint": 0d, "slLimit": 0d, "goldorakSlThreshold": 0d,
            "reinstatementPremiums": new TableMultiDimensionalParameter([0.0], ['Reinstatement Premium'])])

    public static final all = [QUOTASHARE, SURPLUS, WXL, CXL,
            WCXL, STOPLOSS, TRIVIAL/*, LOSSPORTFOLIOTRANSFER, ADVERSEDEVELOPMENTCOVER, GOLDORAK*/]

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
            case ReinsuranceContractType.CXL:
                return new CXLConstractStrategy(
                        attachmentPoint: (double) parameters[CXLConstractStrategy.ATTACHMENT_POINT],
                        limit: (double) parameters[CXLConstractStrategy.LIMIT],
                        aggregateLimit: (double) parameters[CXLConstractStrategy.AGGREGATE_LIMIT],
                        aggregateDeductible: (double) parameters[CXLConstractStrategy.AGGREGATE_DEDUCTIBLE],
                        premiumBase: (PremiumBase) parameters[CXLConstractStrategy.PREMIUM_BASE],
                        premium: (double) parameters[CXLConstractStrategy.PREMIUM],
                        premiumAllocation: (IPremiumAllocationStrategy) parameters[CXLConstractStrategy.PREMIUM_ALLOCATION],
                        reinstatementPremiums: (AbstractMultiDimensionalParameter) parameters[CXLConstractStrategy.REINSTATEMENT_PREMIUMS])
                break
            case ReinsuranceContractType.WCXL:
                return new WCXLConstractStrategy(
                        attachmentPoint: (double) parameters[WCXLConstractStrategy.ATTACHMENT_POINT],
                        limit: (double) parameters[WCXLConstractStrategy.LIMIT],
                        aggregateLimit: (double) parameters[WCXLConstractStrategy.AGGREGATE_LIMIT],
                        aggregateDeductible: (double) parameters[WCXLConstractStrategy.AGGREGATE_DEDUCTIBLE],
                        premiumBase: (PremiumBase) parameters[WCXLConstractStrategy.PREMIUM_BASE],
                        premium: (double) parameters[WCXLConstractStrategy.PREMIUM],
                        premiumAllocation: (IPremiumAllocationStrategy) parameters[WCXLConstractStrategy.PREMIUM_ALLOCATION],
                        reinstatementPremiums: (AbstractMultiDimensionalParameter) parameters[WCXLConstractStrategy.REINSTATEMENT_PREMIUMS])
                break
            case ReinsuranceContractType.STOPLOSS:
                return new StopLossConstractStrategy(
                        contractBase: (StopLossBase) parameters[StopLossConstractStrategy.CONTRACT_BASE],
                        attachmentPoint: (double) parameters[StopLossConstractStrategy.ATTACHMENT_POINT],
                        limit: (double) parameters[StopLossConstractStrategy.LIMIT],
                        premium: (double) parameters[StopLossConstractStrategy.PREMIUM],
                        premiumAllocation: (IPremiumAllocationStrategy) parameters[StopLossConstractStrategy.PREMIUM_ALLOCATION])
                break
            case ReinsuranceContractType.SURPLUS:
                return new SurplusContractStrategy(
                        retention: (double) parameters[SurplusContractStrategy.RETENTION],
                        lines: (double) parameters[SurplusContractStrategy.LINES],
                        defaultCededLossShare: (double) parameters[SurplusContractStrategy.DEFAULTCEDEDLOSSSHARE],
                        commission: (ICommissionStrategy) parameters[SurplusContractStrategy.COMMISSION])
                break
            case ReinsuranceContractType.TRIVIAL:
                return new TrivialContractStrategy()
                break
//            case ReinsuranceContractType.LOSSPORTFOLIOTRANSFER:
//                contract = getLossPortfolioTransferContractStrategy(
//                        (double) parameters["quotaShare"],
//                        (LPTPremiumBase) parameters["contractBase"],
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
//                        (PremiumBase) parameters["contractBase"],
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
}
