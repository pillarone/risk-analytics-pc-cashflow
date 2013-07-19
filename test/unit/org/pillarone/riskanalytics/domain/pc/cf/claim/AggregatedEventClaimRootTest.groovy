package org.pillarone.riskanalytics.domain.pc.cf.claim

import com.google.common.collect.Lists
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket
import org.pillarone.riskanalytics.domain.pc.cf.event.IEvent

/**
*   author simon.parten @ art-allianz . com
 */
class AggregatedEventClaimRootTest extends GroovyTestCase {

    void testUltimate() {

        IEvent anEvent = new EventPacket(new DateTime(2012, 1, 1, 1, 0, 0, 0))
        IEvent failEvent = new EventPacket(new DateTime(2012, 1, 1, 2, 0, 0, 0))

        IClaimRoot claimRoot = new ClaimRoot(10d, ClaimType.AGGREGATED, null, null, anEvent)
        IClaimRoot claimRoot1 = new ClaimRoot(10d, ClaimType.AGGREGATED, null, null, anEvent)
        IClaimRoot claimRoot2 = new ClaimRoot(10d, ClaimType.AGGREGATED, null, null, anEvent)
        IClaimRoot failRoot = new ClaimRoot(10d, ClaimType.AGGREGATED, null, null, null)
        IClaimRoot fail2Root = new ClaimRoot(10d, ClaimType.AGGREGATED, null, null, failEvent)

        List<IClaimRoot> roots = Lists.newArrayList()
        roots.add(claimRoot)
        roots.add(claimRoot1)
        roots.add(claimRoot2)

        AggregatedEventClaimRoot aggClaimRoot = new AggregatedEventClaimRoot(roots, anEvent)
        assert aggClaimRoot.getUltimate() == 30d

        roots.add(failRoot)

        shouldFail {
            AggregatedEventClaimRoot aggClaimRoot1 = new AggregatedEventClaimRoot(roots, anEvent)
        }

        shouldFail {
            AggregatedEventClaimRoot aggClaimRoot2 = new AggregatedEventClaimRoot([fail2Root], anEvent)
        }

    }

}
