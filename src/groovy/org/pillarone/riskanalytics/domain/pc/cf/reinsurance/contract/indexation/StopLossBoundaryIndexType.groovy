package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReinsuranceContractIndexSelectionTableConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class StopLossBoundaryIndexType extends AbstractParameterObjectClassifier {

    public static final StopLossBoundaryIndexType NONE = new StopLossBoundaryIndexType("none", "NONE", [:])
    public static final StopLossBoundaryIndexType INDEXED = new StopLossBoundaryIndexType("indexed", "INDEXED",
        ['index': new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), ReinsuranceContractIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(ReinsuranceContractIndexSelectionTableConstraints.IDENTIFIER)),
         'indexedValues': StopLossBoundaryIndexApplication.LIMIT])



    public static final all = [NONE, INDEXED]


    protected static Map types = [:]
    static {
        StopLossBoundaryIndexType.all.each {
            StopLossBoundaryIndexType.types[it.toString()] = it
        }
    }

    private StopLossBoundaryIndexType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static StopLossBoundaryIndexType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IBoundaryIndexStrategy getDefault() {
        return new StopLossNoBoundaryIndexStrategy()
    }

    public static IBoundaryIndexStrategy getStrategy(StopLossBoundaryIndexType type, Map parameters) {
        IBoundaryIndexStrategy boundaryIndexStrategy;
        switch (type) {
            case StopLossBoundaryIndexType.NONE:
                boundaryIndexStrategy = new StopLossNoBoundaryIndexStrategy()
                break
            case StopLossBoundaryIndexType.INDEXED:
                boundaryIndexStrategy = new StopLossIndexedBoundaryIndexStrategy(
                    index : (ConstrainedMultiDimensionalParameter) parameters['index'],
                    indexedValues: (StopLossBoundaryIndexApplication) parameters['indexedValues'])
                break
            default:
                throw new NotImplementedException(type.toString())
        }
        return boundaryIndexStrategy
    }
}
