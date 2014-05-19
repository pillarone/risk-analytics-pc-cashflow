package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.*
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReinsuranceContractIndexSelectionTableConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class SurplusBoundaryIndexType extends AbstractParameterObjectClassifier {

    public static final SurplusBoundaryIndexType NONE = new SurplusBoundaryIndexType("none", "NONE", [:])
    public static final SurplusBoundaryIndexType INDEXED = new SurplusBoundaryIndexType("indexed", "INDEXED",
        ['index': new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), ReinsuranceContractIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(ReinsuranceContractIndexSelectionTableConstraints.IDENTIFIER)),
         'indexedValues': SurplusBoundaryIndexApplication.RETENTION])



    public static final all = [NONE, INDEXED]


    protected static Map types = [:]
    static {
        SurplusBoundaryIndexType.all.each {
            SurplusBoundaryIndexType.types[it.toString()] = it
        }
    }

    private SurplusBoundaryIndexType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static SurplusBoundaryIndexType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IBoundaryIndexStrategy getDefault() {
        return new SurplusNoBoundaryIndexStrategy()
    }

    public static IBoundaryIndexStrategy getStrategy(SurplusBoundaryIndexType type, Map parameters) {
        IBoundaryIndexStrategy boundaryIndexStrategy;
        switch (type) {
            case SurplusBoundaryIndexType.NONE:
                boundaryIndexStrategy = new SurplusNoBoundaryIndexStrategy()
                break
            case SurplusBoundaryIndexType.INDEXED:
                boundaryIndexStrategy = new SurplusIndexedBoundaryIndexStrategy(
                    index : (ConstrainedMultiDimensionalParameter) parameters['index'],
                    indexedValues: (SurplusBoundaryIndexApplication) parameters['indexedValues'])
                break
            default:
                throw new NotImplementedException(type.toString())
        }
        return boundaryIndexStrategy
    }
}
