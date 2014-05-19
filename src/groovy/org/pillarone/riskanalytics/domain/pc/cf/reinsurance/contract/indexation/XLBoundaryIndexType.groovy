package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.indexation

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReinsuranceContractIndexSelectionTableConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class XLBoundaryIndexType extends AbstractParameterObjectClassifier {

    public static final XLBoundaryIndexType NONE = new XLBoundaryIndexType("none", "NONE", [:])
    public static final XLBoundaryIndexType INDEXED = new XLBoundaryIndexType("indexed", "INDEXED",
        ['index': new ConstrainedMultiDimensionalParameter(
            Collections.emptyList(), ReinsuranceContractIndexSelectionTableConstraints.COLUMN_TITLES,
            ConstraintsFactory.getConstraints(ReinsuranceContractIndexSelectionTableConstraints.IDENTIFIER)),
         'indexedValues': XLBoundaryIndexApplication.LIMIT_AGGREGATE_LIMIT])



    public static final all = [NONE, INDEXED]


    protected static Map types = [:]
    static {
        XLBoundaryIndexType.all.each {
            XLBoundaryIndexType.types[it.toString()] = it
        }
    }

    private XLBoundaryIndexType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static XLBoundaryIndexType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    public static IBoundaryIndexStrategy getDefault() {
        return new XLNoBoundaryIndexStrategy()
    }

    public static IBoundaryIndexStrategy getStrategy(XLBoundaryIndexType type, Map parameters) {
        IBoundaryIndexStrategy boundaryIndexStrategy;
        switch (type) {
            case XLBoundaryIndexType.NONE:
                boundaryIndexStrategy = new XLNoBoundaryIndexStrategy()
                break
            case XLBoundaryIndexType.INDEXED:
                boundaryIndexStrategy = new XLIndexedBoundaryIndexStrategy(
                    index : (ConstrainedMultiDimensionalParameter) parameters['index'],
                    indexedValues: (XLBoundaryIndexApplication) parameters['indexedValues'])
                break
            default:
                throw new NotImplementedException(type.toString())
        }
        return boundaryIndexStrategy
    }
}
