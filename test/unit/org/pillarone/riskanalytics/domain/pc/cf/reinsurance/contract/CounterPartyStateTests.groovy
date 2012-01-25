package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract

import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntity

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class CounterPartyStateTests extends GroovyTestCase {

    DateTime date20100101 = new DateTime(2010, 1, 1, 0, 0, 0, 0)
    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)
    DateTime date20110401 = new DateTime(2011, 4, 1, 0, 0, 0, 0)
    DateTime date20110701 = new DateTime(2011, 7, 1, 0, 0, 0, 0)

    void testUsage() {
        ILegalEntityMarker lunarRe = new LegalEntity()
        ILegalEntityMarker solarRe = new LegalEntity()
        CounterPartyState counterPartyState = new CounterPartyState()
        counterPartyState.addCounterPartyFactor(date20110101, lunarRe, 0.5, true)
        counterPartyState.addCounterPartyFactor(date20110101, solarRe, 0.35, true)

        assertEquals "Covered by Lunar Re at 01.01.2010", 0, counterPartyState.getCoveredByReinsurer(date20100101, lunarRe)
        assertEquals "Covered by Lunar Re at 01.01.2011", 0.5d, counterPartyState.getCoveredByReinsurer(date20110101, lunarRe)
        assertEquals "Covered by Lunar Re at 01.04.2011", 0.5d, counterPartyState.getCoveredByReinsurer(date20110401, lunarRe)

        assertEquals "Covered by reinsurers at 01.01.2010", 0, counterPartyState.getCoveredByReinsurers(date20100101)
        assertEquals "Covered by reinsurers at 01.01.2011", 0.85, counterPartyState.getCoveredByReinsurers(date20110101)
        assertEquals "Covered by reinsurers at 01.04.2011", 0.85, counterPartyState.getCoveredByReinsurers(date20110401)

        assertNull "all counterparties default at 01.01.2011", counterPartyState.allCounterPartiesDefaultAfter()

        assertEquals "Covered by Lunar Re at 01.01.2010", null, counterPartyState.getFactors(date20100101).get(lunarRe)
        assertEquals "Covered by Lunar Re at 01.01.2011", 0.5d/0.85, counterPartyState.getFactors(date20110101).get(lunarRe)
        assertEquals "Covered by Lunar Re at 01.04.2011", 0.5d/0.85, counterPartyState.getFactors(date20110401).get(lunarRe)

        counterPartyState.addCounterPartyFactor(date20110401, lunarRe, 0.2, false)
        assertEquals "After 1st update: Covered by Lunar Re at 01.01.2011", 0.5d, counterPartyState.getCoveredByReinsurer(date20110101, lunarRe)
        assertEquals "After 1st update: Covered by Lunar Re at 01.04.2011", 0.2d, counterPartyState.getCoveredByReinsurer(date20110401, lunarRe)
        assertEquals "After 1st update: Covered by reinsurers at 01.01.2011", 0.85, counterPartyState.getCoveredByReinsurers(date20110101)
        assertEquals "After 1st update: Covered by reinsurers at 01.04.2011", 0.55, counterPartyState.getCoveredByReinsurers(date20110401)
        assertEquals "After 1st update: Covered by Lunar Re at 01.01.2011", 0.2d/0.55, counterPartyState.getFactors(date20110401).get(lunarRe)

        counterPartyState.addCounterPartyFactor(date20110701, lunarRe, 0d, false)
        counterPartyState.addCounterPartyFactor(date20110701, solarRe, 0d, false)
        assertEquals "After 1st update: Covered by Lunar Re at 01.01.2011", 0.5d, counterPartyState.getCoveredByReinsurer(date20110101, lunarRe)
        assertEquals "After 1st update: Covered by Lunar Re at 01.04.2011", 0.2d, counterPartyState.getCoveredByReinsurer(date20110401, lunarRe)
        assertEquals "After 1st update: Covered by Lunar Re at 01.07.2011", 0d, counterPartyState.getCoveredByReinsurer(date20110701, lunarRe)
        assertEquals "After 1st update: Covered by reinsurers at 01.01.2011", 0.85, counterPartyState.getCoveredByReinsurers(date20110101)
        assertEquals "After 1st update: Covered by reinsurers at 01.04.2011", 0.55, counterPartyState.getCoveredByReinsurers(date20110401)
        assertEquals "After 1st update: Covered by reinsurers at 01.07.2011", 0d, counterPartyState.getCoveredByReinsurers(date20110701)
        assertEquals "After 1st update: Covered by Lunar Re at 01.01.2011", null, counterPartyState.getFactors(date20110701).get(lunarRe)

        assertEquals "all counterparties default at 01.07.2011", date20110701, counterPartyState.allCounterPartiesDefaultAfter()
    }
}
