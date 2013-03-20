package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective.AdverseDevelopmentCoverConstractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective.LossPortfolioTransferContractStrategy
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.retrospective.TrivialContractStrategy

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RetrospectiveReinsuranceContractType extends AbstractParameterObjectClassifier {

    public static final RetrospectiveReinsuranceContractType LOSSPORTFOLIOTRANSFER = new RetrospectiveReinsuranceContractType("loss portfolio transfer", "LOSSPORTFOLIOTRANSFER",
            ["cededShare": 0d, "limit": 0d, "reinsurancePremium": 0d])
    public static final RetrospectiveReinsuranceContractType ADVERSEDEVELOPMENTCOVER = new RetrospectiveReinsuranceContractType("adverse development cover", "ADVERSEDEVELOPMENTCOVER", [
            "attachmentPoint": 0d, "limit": 0d, "reinsurancePremium": 0d])
    public static final RetrospectiveReinsuranceContractType TRIVIAL = new RetrospectiveReinsuranceContractType("trivial", "TRIVIAL", [:])

    public static final all = [LOSSPORTFOLIOTRANSFER, ADVERSEDEVELOPMENTCOVER, TRIVIAL]

    protected static Map types = [:]
    static {
        RetrospectiveReinsuranceContractType.all.each {
            RetrospectiveReinsuranceContractType.types[it.toString()] = it
        }
    }

    private RetrospectiveReinsuranceContractType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static RetrospectiveReinsuranceContractType valueOf(String type) {
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

    static IReinsuranceContractStrategy getStrategy(RetrospectiveReinsuranceContractType type, Map parameters) {
        IReinsuranceContractStrategy contract
        switch (type) {
            case RetrospectiveReinsuranceContractType.LOSSPORTFOLIOTRANSFER:
                return new LossPortfolioTransferContractStrategy(
                        cededShare: (double) parameters[LossPortfolioTransferContractStrategy.CEDEDSHARE],
                        limit: (double) parameters[LossPortfolioTransferContractStrategy.LIMIT],
                        reinsurancePremium: (double) parameters[LossPortfolioTransferContractStrategy.REINSURANCEPREMIUM])
                break
            case RetrospectiveReinsuranceContractType.ADVERSEDEVELOPMENTCOVER:
                return new AdverseDevelopmentCoverConstractStrategy(
                        attachmentPoint: (double) parameters[AdverseDevelopmentCoverConstractStrategy.ATTACHMENT_POINT],
                        limit: (double) parameters[AdverseDevelopmentCoverConstractStrategy.LIMIT],
                        reinsurancePremium: (double) parameters[AdverseDevelopmentCoverConstractStrategy.REINSURANCEPREMIUM])
                break
            case RetrospectiveReinsuranceContractType.TRIVIAL:
                return new TrivialContractStrategy()
                break
        }
        return contract
    }
}
