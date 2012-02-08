package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.nonproportional

import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class EqualUsagePerPeriodThresholdStoreTests extends GroovyTestCase {
    
    void testUsage() {
        EqualUsagePerPeriodThresholdStore thresholdStore = new EqualUsagePerPeriodThresholdStore(1000);
        thresholdStore.initPeriod(0)
        
        assertEquals 'P0 correct init of ultimate', 1000, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 0)
        assertEquals 'P0 correct init of reported', 1000, thresholdStore.get(BasedOnClaimProperty.REPORTED, 0)
        assertEquals 'P0 correct init of paid', 1000, thresholdStore.get(BasedOnClaimProperty.PAID, 0)

        thresholdStore.plus(-300, BasedOnClaimProperty.ULTIMATE, 0)
        assertEquals 'P0 correct update of ultimate', 700, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 0)
        thresholdStore.plus(-200, BasedOnClaimProperty.REPORTED, 0)
        assertEquals 'P0 correct update of reported', 800, thresholdStore.get(BasedOnClaimProperty.REPORTED, 0)
        thresholdStore.plus(-100, BasedOnClaimProperty.PAID, 0)
        assertEquals 'P0 correct update of paid', 900, thresholdStore.get(BasedOnClaimProperty.PAID, 0)


        thresholdStore.initPeriod(1)

        assertEquals 'P1 correct reduction for ultimate P0', 0, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 0)
        assertEquals 'P1 correct reduction for reported P0', 100, thresholdStore.get(BasedOnClaimProperty.REPORTED, 0)
        assertEquals 'P1 correct reduction for paid P0', 200, thresholdStore.get(BasedOnClaimProperty.PAID, 0)
        assertEquals 'P1 correct init of ultimate P1', 700, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 1)
        assertEquals 'P1 correct init of reported P1', 700, thresholdStore.get(BasedOnClaimProperty.REPORTED, 1)
        assertEquals 'P1 correct init of paid P1', 700, thresholdStore.get(BasedOnClaimProperty.PAID, 1)

        thresholdStore.plus(-300, BasedOnClaimProperty.ULTIMATE, 1)
        assertEquals 'P1 correct update of ultimate', 400, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 1)
        thresholdStore.plus(-100, BasedOnClaimProperty.REPORTED, 0)
        assertEquals 'P1.0 correct update of reported', 0, thresholdStore.get(BasedOnClaimProperty.REPORTED, 0)
        thresholdStore.plus(-300, BasedOnClaimProperty.REPORTED, 1)
        assertEquals 'P1.1 correct update of reported', 400, thresholdStore.get(BasedOnClaimProperty.REPORTED, 1)
        thresholdStore.plus(-100, BasedOnClaimProperty.PAID, 0)
        assertEquals 'P1.0 correct update of paid', 100, thresholdStore.get(BasedOnClaimProperty.PAID, 0)
        thresholdStore.plus(-150, BasedOnClaimProperty.PAID, 1)
        assertEquals 'P1.1 correct update of paid', 550, thresholdStore.get(BasedOnClaimProperty.PAID, 1)

        thresholdStore.initPeriod(2)

        assertEquals 'P2 correct reduction for ultimate P0', 0, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 0)
        assertEquals 'P2 correct reduction for reported P0', 0, thresholdStore.get(BasedOnClaimProperty.REPORTED, 0)
        assertEquals 'P2 correct reduction for paid P0', 100, thresholdStore.get(BasedOnClaimProperty.PAID, 0)
        assertEquals 'P2 correct reduction for ultimate P1', 0, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 1)
        assertEquals 'P2 correct reduction for reported P1', 0, thresholdStore.get(BasedOnClaimProperty.REPORTED, 1)
        assertEquals 'P2 correct reduction for paid P1', 150, thresholdStore.get(BasedOnClaimProperty.PAID, 1)
        assertEquals 'P2 correct init of ultimate P2', 400, thresholdStore.get(BasedOnClaimProperty.ULTIMATE, 2)
        assertEquals 'P2 correct init of reported P2', 400, thresholdStore.get(BasedOnClaimProperty.REPORTED, 2)
        assertEquals 'P2 correct init of paid P2', 400, thresholdStore.get(BasedOnClaimProperty.PAID, 2)
    }
}
