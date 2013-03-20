package org.pillarone.riskanalytics.domain.pc.cf.accounting.experienceAccounting;

/**
 * @author simon.parten (at) art-allianz (dot) com
 */
public enum CommutationBehaviour {
    DEFAULT {
    };

    public static CommutationBehaviour getStringValue(String value){
        CommutationBehaviour[] values = CommutationBehaviour.values();
        for (CommutationBehaviour basedOnCommutationCondition : values) {
            if(value.equals(basedOnCommutationCondition.toString()))
                return basedOnCommutationCondition;
        }
        throw new IllegalArgumentException("Enum not found for " + value);
    }
}
