package org.pillarone.riskanalytics.domain.pc.cf.exposure

import org.pillarone.riskanalytics.core.components.IterationStore
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class RiskBandTests extends GroovyTestCase {

    static UnderwritingInfoPacket getUnderwritingInfo0() {
        return new UnderwritingInfoPacket(
                numberOfPolicies: 1000d,
                sumInsured: 80d,
                maxSumInsured: 100d,
                premiumWritten: 5000d,
                premiumPaid: 5000d)
    }
    // second band: ceded = (200-100)/200 = 0.5
    static UnderwritingInfoPacket getUnderwritingInfo1() {
        return new UnderwritingInfoPacket(
                numberOfPolicies: 100d,
                sumInsured: 200d,
                maxSumInsured: 400d,
                premiumWritten: 2000d,
                premiumPaid: 2000d)
    }
    // third band: ceded = (400-100)/500 = 0.6
    static UnderwritingInfoPacket getUnderwritingInfo2() {
        return new UnderwritingInfoPacket(
                numberOfPolicies: 50d,
                sumInsured: 500d,
                maxSumInsured: 800d,
                premiumWritten: 4000d,
                premiumPaid: 4000d)
    }

    static List<UnderwritingInfoPacket> getUnderwritingInfos() {
        return [getUnderwritingInfo0(), getUnderwritingInfo1(), getUnderwritingInfo2()]
    }

    void testUsage() {
        ConstrainedMultiDimensionalParameter UnderwritingInfoPacketrmation1 = new ConstrainedMultiDimensionalParameter(
                [[100d, 400d, 800d], [80d, 200d, 500d], [5000d, 2000d, 4000d], [1000d, 100d, 50d]],
                RiskBands.TABLE_COLUMN_TITLES, ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))

        ConstrainedMultiDimensionalParameter UnderwritingInfoPacketrmation2 = new ConstrainedMultiDimensionalParameter(
                [[100d, 400d, 800d], [80d, 200d, 500d], [5000d, 2000d, 4000d], [1000d, 100d, 50d]],
                RiskBands.TABLE_COLUMN_TITLES, ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))

        IterationScope iterationScope = new IterationScope(periodScope: new PeriodScope())
        iterationScope.prepareNextIteration()
        RiskBands bands = new RiskBands(
                parmUnderwritingInformation: UnderwritingInfoPacketrmation1,
                iterationScope: iterationScope,
                iterationStore: new IterationStore(iterationScope)
        )
        bands.doCalculation()

        bands.outUnderwritingInfo.eachWithIndex {UnderwritingInfoPacket underwritingInfo, idx ->
            UnderwritingInfoPacket uwInfo = underwritingInfos[idx]
            uwInfo.origin = bands
            assertTrue "max sum insured info$idx", underwritingInfo.sameContent(uwInfo)
        }

    }
}
