package org.pillarone.riskanalytics.life.longevity

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.utils.constraints.MDPConstraintsHelper

/**
 *   author simon.parten @ art-allianz . com
 */
class HistoricMortalityRatesConstraints extends MDPConstraintsHelper {

    public static final String IDENTIFIER = "Historic Mortality Constraints"

    public static final Integer AGE_COLUMN = 0

    public static final Class AGE = Integer

    private static final Integer YEAR_START = 1950
    private static final Integer YEAR_END = 2010

    public static final Map<Integer, Class> classMapper = createMap()

    public static List<String> columnnHeaders = makeHeaders()

    private static Map<Integer, Class> createMap() {
        Map<Integer, Class> aMap = Maps.newHashMap()
        aMap.put(AGE_COLUMN, AGE)
        for (int i = YEAR_START; i <= YEAR_END; i++) {
            aMap.put(i - (YEAR_START-1), Double)
        }
        return aMap
    }

    private static List<String> makeHeaders(){
        List<String> headers = Lists.newArrayList()
        headers << "Age"
        for (int i = YEAR_START; i <= YEAR_END; i++) {
            headers << i.toString()
        }
        return headers
    }

    public static List<List<Double>> initialList(){
        List<List<Double>> headers = Lists.newArrayList()
        headers << []
        for (int i = YEAR_START; i <= YEAR_END; i++) {
            headers << []
        }
        return headers
    }

}
