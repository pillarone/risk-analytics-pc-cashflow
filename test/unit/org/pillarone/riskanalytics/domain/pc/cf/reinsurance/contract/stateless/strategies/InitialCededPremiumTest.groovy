package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.strategies

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.TestPeriodScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternPacketTests
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.YearLayerIdentifier

/**
*   author simon.parten @ art-allianz . com
 */
class InitialCededPremiumTest extends GroovyTestCase {

    void testWrittenInitialPremium() {

        ContractLayer contractLayer = new ContractLayer(new YearLayerIdentifier(1d, 1d), 0.5d, 0d, 0d, 0d, 0d, 500, null, null, null, 0d)
        PatternPacket aPattern = PatternPacketTests.getPattern([0, 12], [0d, 1d], false)

        InitialCededPremium cededPremium = new InitialCededPremium(contractLayer, aPattern)

        assert cededPremium.initialWrittenPremium() == 0.5 * 500

    }

    void testPaidInitialPremium() {
        DateTime start2012 = new DateTime(2012, 1, 1, 1,0, 0, 0)

        ContractLayer contractLayer = new ContractLayer(new YearLayerIdentifier(1d, 1d), 1d, 0d, 0d, 0d, 0d, 500, null, null, null, 0d)
        PatternPacket aPattern = PatternPacketTests.getPattern([0, 12], [0d, 1d], false)
        InitialCededPremium cededPremium = new InitialCededPremium(contractLayer, aPattern)

        PeriodScope periodScope = TestPeriodScopeUtilities.getPeriodScope(start2012, 1)

        assert cededPremium.cumulativePremiumPaidAtDate(start2012.plusYears(1).minusDays(1), periodScope) == 500


    }


}
