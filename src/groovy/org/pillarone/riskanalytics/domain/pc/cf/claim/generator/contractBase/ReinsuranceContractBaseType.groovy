package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase

import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class ReinsuranceContractBaseType extends AbstractParameterObjectClassifier {

    public static final ReinsuranceContractBaseType PLEASESELECT = new ReinsuranceContractBaseType(
            'please select', 'PLEASESELECT', [:])
    public static final ReinsuranceContractBaseType LOSSESOCCURRING = new ReinsuranceContractBaseType(
            'losses occuring', 'LOSSESOCCURRING', [:])
    public static final ReinsuranceContractBaseType RISKATTACHING = new ReinsuranceContractBaseType(
            'risk attaching', 'RISKATTACHING', [underlyingContractLength: 12])

    public static final all = [PLEASESELECT, LOSSESOCCURRING, RISKATTACHING]

    protected static Map types = [:]
    static {
        ReinsuranceContractBaseType.all.each {
            ReinsuranceContractBaseType.types[it.toString()] = it
        }
    }

    private ReinsuranceContractBaseType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ReinsuranceContractBaseType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return ReinsuranceContractBaseType.getStrategy(this, parameters)
    }

    static IReinsuranceContractBaseStrategy getStrategy(ReinsuranceContractBaseType type, Map parameters) {
        switch (type) {
            case ReinsuranceContractBaseType.PLEASESELECT:
                return new DefaultContractBase()
            case ReinsuranceContractBaseType.LOSSESOCCURRING:
                return new LossesOccurringContractBase()
            case ReinsuranceContractBaseType.RISKATTACHING:
                return new RiskAttachingContractBase(
                        underlyingContractLength: (Integer) parameters.get('underlyingContractLength'))
        }
    }
}