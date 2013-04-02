package org.pillarone.riskanalytics.life.longevity

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
*   author simon.parten @ art-allianz . com
 */
class ActualMortalityTableTest extends GroovyTestCase {

    private ConstrainedMultiDimensionalParameter  testTable = new
    ConstrainedMultiDimensionalParameter(
            org.pillarone.riskanalytics.core.util.GroovyUtils.toList([[0.5, 1.5, 2.5], [0.2, 0.3, 0.4]]),
            MortalityRatesConstraints.columnnHeaders,
            MortalityRatesConstraints.class,

    )

     private void testHistoricTableConstruction() {
         Double year = 2010d
         IMortalityTable actualMortalityTable = new ActualMortalityTable(testTable, year)
         IMortalityTableEntry anEntry = actualMortalityTable.getMortalityObject(0.5d, year)
         assert anEntry.getAge() == 0.5d
         assert anEntry.getYear() == 2010
         assert anEntry.mortalityRate() == 0.2

     }



}
