package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover

import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.core.parameterization.*

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FilterStrategyType extends AbstractParameterObjectClassifier {

    public static final FilterStrategyType ALL = new FilterStrategyType("all", "ALL", [:])
    public static final FilterStrategyType PERILS = new FilterStrategyType(
            'perils', 'PERILS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Perils'], IPerilMarker)])
    public static final FilterStrategyType SEGMENTS = new FilterStrategyType(
            'segments', 'SEGMENTS',
            ['segments':new ComboBoxTableMultiDimensionalParameter([], ['Segments'], ISegmentMarker)])
    public static final FilterStrategyType PERILSSEGMENTS = new FilterStrategyType(
            'perils, segments', 'PERILSSEGMENTS',
            ['perils':new ComboBoxTableMultiDimensionalParameter([], ['Perils'], IPerilMarker),
             'segments':new ComboBoxTableMultiDimensionalParameter([], ['Segments'], ISegmentMarker),
             'connection':LogicArguments.AND])
//    public static final FilterStrategyType LEGALENTITIES = new FilterStrategyType(
//            'legal entities', 'LEGALENTITIES',
//            ['legalEntities':new ComboBoxTableMultiDimensionalParameter([], ['Legal Entities'], ILegalEntityMarker)])

    public static final all = [ALL, PERILS, SEGMENTS, PERILSSEGMENTS] //, LEGALENTITIES]

    protected static Map types = [:]
    static {
        FilterStrategyType.all.each {
            FilterStrategyType.types[it.toString()] = it
        }
    }

    private FilterStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static FilterStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }


    public static ICoverAttributeStrategy getDefault() {
        return new AllFilterStrategy()
    }

    public static ICoverAttributeStrategy getStrategy(FilterStrategyType type, Map parameters) {
        ICoverAttributeStrategy coverStrategy ;
        switch (type) {
            case FilterStrategyType.ALL:
                coverStrategy = new AllFilterStrategy()
                break
            case FilterStrategyType.PERILS:
                coverStrategy = new PerilsFilterStrategy(perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'])
                break
            case FilterStrategyType.SEGMENTS:
                coverStrategy = new SegmentsFilterStrategy(segments: (ComboBoxTableMultiDimensionalParameter) parameters['segments'])
                break
            case FilterStrategyType.PERILSSEGMENTS:
                coverStrategy = new PerilsSegmentsFilterStrategy(
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters['perils'],
                        segments: (ComboBoxTableMultiDimensionalParameter) parameters['segments'],
                        connection: (LogicArguments) parameters['connection'])
                break
//            case FilterStrategyType.LEGALENTITIES:
//                coverStrategy = new LegalEntitiesFilterStrategy(
//                        legalEntities: (ComboBoxTableMultiDimensionalParameter) parameters['legalEntities'])
//                break
        }
        return coverStrategy;
    }
}
