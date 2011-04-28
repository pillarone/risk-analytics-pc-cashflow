package org.pillarone.riskanalytics.domain.pc.cf.pattern;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedString;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class PatternUtils {

    public static PatternPacket filterPattern(List<PatternPacket> patterns, ConstrainedString criteria) {
        for (PatternPacket pattern : patterns) {
            if (pattern.getOrigin().equals(criteria.getSelectedComponent())) {
                return pattern;
            }
        }
        return null;
    }

}
