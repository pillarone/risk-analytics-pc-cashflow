package org.pillarone.riskanalytics.life.longevity

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

/**
*   author simon.parten @ art-allianz . com
 */
class ActualMortalityTableTest extends GroovyTestCase {

    private ConstrainedMultiDimensionalParameter  testTable = new
    ConstrainedMultiDimensionalParameter(
            [[0.5d, 1.5d, 2.5d], [0.2d, 0.3d, 0.4d]],
            MortalityRatesConstraints.columnnHeaders,
            new MortalityRatesConstraints()

    )

     void testActualTableConstruction() {
         Double year = 2010d
         IMortalityTable actualMortalityTable = new ActualMortalityTable(testTable, year, "aName")
         IMortalityTableEntry anEntry = actualMortalityTable.getMortalityObject(0.5d, year)
         assert anEntry.getAge() == 0.5d
         assert anEntry.getYear() == 2010
         assert anEntry.mortalityRate() == 0.2
     }

    void testHistoricTableConstruction(){

    }

    void testIdentityTable(){
        IMortalityTable identity = new IdentityTableStrategy()
        assert 1 == identity.getMortalityObject(25, 2010 ).mortalityRate()
    }
}
