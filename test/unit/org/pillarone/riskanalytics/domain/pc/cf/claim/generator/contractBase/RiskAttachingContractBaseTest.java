package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.contractBase;

import groovy.util.GroovyTestCase;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.GrossClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author simon.parten @ art-allianz . com
 */
public class RiskAttachingContractBaseTest extends GroovyTestCase {

    //    Check that we preserve the start date for patterns throughout the splitting process.
    public void testSplitClaims() throws Exception {
//      Setup objects
        DateTime start2010 = new DateTime(2010, 1, 1, 1, 1, 0, 0);
        DateTime startDateForPatterns = new DateTime(2010, 6, 1, 0, 0, 0, 0);

        PatternPacket patternPacket = PatternPacketTests.getPattern(Arrays.asList(12), Arrays.asList(12d));
        GrossClaimRoot grossClaimRoot = new GrossClaimRoot(100d, ClaimType.AGGREGATED, start2010,
                start2010, patternPacket, patternPacket,
//                Check this date remains the same for each claim root that pops out the other side.
                startDateForPatterns
                );

        Map map = new HashMap();
        map.put("underlyingContractLength", 12);
        IReinsuranceContractBaseStrategy riskAttachingContractBase = (IReinsuranceContractBaseStrategy) ReinsuranceContractBaseType.getStrategy(ReinsuranceContractBaseType.RISKATTACHING, map );

        List<GrossClaimRoot> grossClaimRootList = riskAttachingContractBase.splitClaims(Arrays.asList(grossClaimRoot), TestPeriodScopeUtilities.getPeriodScope(start2010, 1));

        for (GrossClaimRoot claimRoot : grossClaimRootList) {
            assert startDateForPatterns.equals(claimRoot.getStartDateForPatterns());
        }

//        Check we have the right number of claims.
        assertEquals("Check 12 claims", riskAttachingContractBase.splittedClaimsNumber(), grossClaimRootList.size());


    }


}
