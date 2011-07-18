package org.pillarone.riskanalytics.domain.pc.cf.structure

import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelectionTableConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class StructuringType extends AbstractParameterObjectClassifier {

    public static final StructuringType SEGMENTS = new StructuringType("segments", "GROSSSEGMENTS",
            [segments: new ComboBoxTableMultiDimensionalParameter([], [SegmentsStructuringStrategy.SEGMENT], ISegmentMarker)])
    public static final StructuringType SEGMENTSPERILS = new StructuringType("segments, perils", "SEGMENTSPERILS",
            [segments: new ComboBoxTableMultiDimensionalParameter([], [SegmentsPerilsStructuringStrategy.SEGMENT], ISegmentMarker),
                    perils: new ComboBoxTableMultiDimensionalParameter([], [SegmentsPerilsStructuringStrategy.PERIL], IPerilMarker),
                    connection: LogicArguments.AND])
    public static final StructuringType CLAIMTYPES = new StructuringType("claim types", "CLAIMTYPES",
            [claimTypes: new ConstrainedMultiDimensionalParameter([], ClaimTypeSelectionTableConstraints.COLUMN_TITLES,
                    ConstraintsFactory.getConstraints(ClaimTypeSelectionTableConstraints.IDENTIFIER))])

    public static final all = [SEGMENTS, SEGMENTSPERILS, CLAIMTYPES]

    protected static Map types = [:]
    static {
        StructuringType.all.each {
            StructuringType.types[it.toString()] = it
        }
    }

    private StructuringType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static StructuringType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IStructuringStrategy getDefault() {
        return new SegmentsStructuringStrategy(
                segments: new ComboBoxTableMultiDimensionalParameter([], [SegmentsStructuringStrategy.SEGMENT], ISegmentMarker))
    }


    static IStructuringStrategy getStrategy(StructuringType type, Map parameters) {
        IStructuringStrategy strategy;
        switch (type) {
            case StructuringType.SEGMENTS:
                strategy = new SegmentsStructuringStrategy(
                        segments: (ComboBoxTableMultiDimensionalParameter) parameters["segments"])
                break;
            case StructuringType.SEGMENTSPERILS:
                strategy = new SegmentsPerilsStructuringStrategy(
                        segments: (ComboBoxTableMultiDimensionalParameter) parameters["segments"],
                        perils: (ComboBoxTableMultiDimensionalParameter) parameters["perils"],
                        connection: (LogicArguments) parameters["connection"])
                break;
            case StructuringType.CLAIMTYPES:
                strategy = new ClaimTypesStructuringStrategy(
                        claimTypes: (ConstrainedMultiDimensionalParameter) parameters["claimTypes"])
                break;
        }
        return strategy;
    }
}

