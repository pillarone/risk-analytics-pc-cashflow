package org.pillarone.riskanalytics.domain.pc.cf.exposure

import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.ReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class UnderwritingInfoUtilsTests extends GroovyTestCase {

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

    void testAggregateBySegment() {
        Segment segmentFire = new Segment(name: 'fire')
        Segment segmentMotor = new Segment(name: 'motor')
        ReinsuranceContract quoteFire = new ReinsuranceContract(name: 'quote fire')

        UnderwritingInfoPacket segmentFireUwInfo = new UnderwritingInfoPacket(numberOfPolicies: 1000d, sumInsured: 100d,
                maxSumInsured: 200d, premiumWritten: 5000d, premiumPaid: 5000d, segment: segmentFire)
        UnderwritingInfoPacket segmentMotorUwInfo = new UnderwritingInfoPacket(numberOfPolicies: 800d, sumInsured: 200d,
                maxSumInsured: 1000d, premiumWritten: 7500d, premiumPaid: 7500d, segment: segmentMotor)
        CededUnderwritingInfoPacket quoteFireUwInfo = new CededUnderwritingInfoPacket(numberOfPolicies: 1000d, sumInsured: 50d,
                maxSumInsured: 100d, premiumWritten: -2500d, premiumPaid: -2500d, segment: segmentFire, reinsuranceContract: quoteFire)
        List<UnderwritingInfoPacket> originalUnderwritingInfoPackets = [segmentFireUwInfo, segmentMotorUwInfo, quoteFireUwInfo]

        List<UnderwritingInfoPacket> aggregateBySegment = UnderwritingInfoUtils.aggregateBySegment(originalUnderwritingInfoPackets)
        assert segmentMotorUwInfo, aggregateBySegment[1]            // 'segment motor unmodified'
        assert segmentFire, aggregateBySegment[0].segment
        assert 2500d, aggregateBySegment[0].premiumWritten
        assert 2500d, aggregateBySegment[0].premiumPaid
    }
}
