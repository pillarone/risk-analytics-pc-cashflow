package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints

import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public abstract class MDPConstraintsHelper implements IMultiDimensionalConstraints {

//    public static final String IDENTIFIER = "Example"
//    public static final Map<Integer, Class> classMapper = [ : ]


    public boolean matches(int row, int column, Object value) {
        Integer tempColumn = column.toInteger()
        if (!(classMapper.get(tempColumn) == null)) {
            if(classMapper.get(tempColumn).isEnum()){
//                 The enums used in constraints must have the method getStringValue. We then rely on the Groovy truth to get a true result.
//                 The enum itself should throw an exception if this value is the wrong string.
                return classMapper.get(tempColumn).getStringValue(value)
            }
            return classMapper[tempColumn].isAssignableFrom(value.class)
        }
        throw new IllegalAccessError("Column not in column map" + this.toString())
    }

    /**
     * Each individual set of MDP contraints should have a public static string called identifier which describes what the class
     * is being used to constrain.
     * @return
     */
    public String getName() {
        return IDENTIFIER
    }

    /**
     *
     * @param column
     * @return
     */
    public Class getColumnType(int column) {
        if (!(classMapper.get(column) == null)) {
            return classMapper.get(column)
        }
        throw new IllegalAccessError("Column not in column map: Column " + column.toString() + " :::  "+ this.toString())
    }

    public Integer getColumnIndex(Class marker) {
        null
    }

    boolean emptyComponentSelectionAllowed(int column) {
        return false;
    }
}

