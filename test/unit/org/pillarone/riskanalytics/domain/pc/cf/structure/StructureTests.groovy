package org.pillarone.riskanalytics.domain.pc.cf.structure

import org.pillarone.riskanalytics.core.simulation.TestIterationScopeUtilities
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.cf.segment.Segment
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.constant.LogicArguments
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelectionTableConstraints
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot
import org.joda.time.DateTime

/**
 * @author jessika.walter (at) intuitive-collaboration (dot) com
 */
class StructureTests extends GroovyTestCase {

    Structure structure

    DateTime date20110101 = new DateTime(2011, 1, 1, 0, 0, 0, 0)

    Segment segmentMotor = new Segment(name: 'motor')
    Segment segmentMotorHull = new Segment(name: 'motor hull')
    Segment segmentAccident = new Segment(name: 'accident')
    ClaimsGenerator perilMotor = new ClaimsGenerator(name: "motor")
    ClaimsGenerator perilMotorHull = new ClaimsGenerator(name: "motor hull")
    ClaimsGenerator perilAccident = new ClaimsGenerator(name: "accident")
    ClaimRoot baseClaimMotor100 = new ClaimRoot(100d, ClaimType.ATTRITIONAL, null, null)
    ClaimCashflowPacket claimMotor100 = new ClaimCashflowPacket(baseClaimMotor100, 100, 100, 100, 100, 100, 100, 0, 0, 0, null, date20110101, 0)
    ClaimRoot baseClaimMotorHull500 = new ClaimRoot(500d, ClaimType.SINGLE, null, null)
    ClaimCashflowPacket claimMotorHull500 = new ClaimCashflowPacket(baseClaimMotorHull500, 500, 500, 500, 500, 500, 500, 0, 0, 0, null, date20110101, 0)
    ClaimRoot baseClaimAccident = new ClaimRoot(80d, ClaimType.EVENT, null, null)
    ClaimCashflowPacket claimAccident80 = new ClaimCashflowPacket(baseClaimAccident, 80, 80, 80, 80, 80, 80, 0, 0, 0, null, date20110101, 0)
    UnderwritingInfoPacket underwritingInfoMotor50 = new UnderwritingInfoPacket(segment: segmentMotor, premiumWritten: 50)
    UnderwritingInfoPacket underwritingInfoMotorHull40 = new UnderwritingInfoPacket(segment: segmentMotorHull, premiumWritten: 40)
    UnderwritingInfoPacket underwritingInfoAccident30 = new UnderwritingInfoPacket(segment: segmentAccident, premiumWritten: 30)
    PeriodScope periodScope

    void setUp() {
        claimMotor100.setMarker(segmentMotor)
        claimMotor100.setMarker(perilMotor)
        claimMotorHull500.setMarker(segmentMotorHull)
        claimMotorHull500.setMarker(perilMotorHull)
        claimAccident80.setMarker(segmentAccident)
        claimAccident80.setMarker(perilAccident)
        DateTime projectionStart = new DateTime(2010, 1, 1, 0, 0, 0, 0)
        IterationScope iterationScope = TestIterationScopeUtilities.getIterationScope(projectionStart, 2)
        periodScope = iterationScope.periodScope
        ConstraintsFactory.registerConstraint(new ClaimTypeSelectionTableConstraints())
    }


    void testSegments() {

        structure = new Structure(periodScope: periodScope)

        ComboBoxTableMultiDimensionalParameter selectedSegments = new ComboBoxTableMultiDimensionalParameter(["motor", "motor hull"],
                [SegmentsStructuringStrategy.SEGMENT], ISegmentMarker)
        selectedSegments.comboBoxValues.put("motor", segmentMotor)
        selectedSegments.comboBoxValues.put("motor hull", segmentMotorHull)
        structure.parmBasisOfStructures = StructuringType.getStrategy(StructuringType.SEGMENTS, [segments: selectedSegments])

        structure.inClaimsGross << claimAccident80 << claimMotor100 << claimMotorHull500
        structure.inUnderwritingInfoGross << underwritingInfoAccident30 << underwritingInfoMotor50 << underwritingInfoMotorHull40

        structure.doCalculation()

        assertEquals "size", 2, structure.outClaimsGross.size()
        assertEquals "claim motor", 100, structure.outClaimsGross[0].ultimate()
        assertEquals "claim motor hull", 500, structure.outClaimsGross[1].ultimate()
        assertEquals "size", 2, structure.outUnderwritingInfoGross.size()
        assertEquals "premium underwriting info motor", 50, structure.outUnderwritingInfoGross[0].premiumWritten
        assertEquals "premium underwriting info motor hull", 40, structure.outUnderwritingInfoGross[1].premiumWritten
    }

    void testSegmentsPerils() {

        structure = new Structure(periodScope: periodScope)

        ComboBoxTableMultiDimensionalParameter selectedSegments = new ComboBoxTableMultiDimensionalParameter(["motor", "motor hull"],
                [SegmentsStructuringStrategy.SEGMENT], ISegmentMarker)
        selectedSegments.comboBoxValues.put("motor", segmentMotor)
        selectedSegments.comboBoxValues.put("motor hull", segmentMotorHull)
        ComboBoxTableMultiDimensionalParameter selectedPerils = new ComboBoxTableMultiDimensionalParameter(["motor", "accident"],
                [SegmentsPerilsStructuringStrategy.PERIL], IPerilMarker)
        selectedPerils.comboBoxValues.put("motor", perilMotor)
        selectedPerils.comboBoxValues.put("accident", perilAccident)
        structure.parmBasisOfStructures = StructuringType.getStrategy(StructuringType.SEGMENTSPERILS,
                [segments: selectedSegments, perils: selectedPerils, connection: LogicArguments.AND])

        structure.inClaimsGross << claimAccident80 << claimMotor100 << claimMotorHull500
        structure.inUnderwritingInfoGross << underwritingInfoAccident30 << underwritingInfoMotor50 << underwritingInfoMotorHull40

        structure.doCalculation()

        assertEquals "size", 1, structure.outClaimsGross.size()
        assertEquals "claim motor", 100, structure.outClaimsGross[0].ultimate()
        assertEquals "size", 2, structure.outUnderwritingInfoGross.size()
        assertEquals "premium underwriting info motor", 50, structure.outUnderwritingInfoGross[0].premiumWritten
        assertEquals "premium underwriting info motor hull", 40, structure.outUnderwritingInfoGross[1].premiumWritten

        structure.parmBasisOfStructures = StructuringType.getStrategy(StructuringType.SEGMENTSPERILS,
                [segments: selectedSegments, perils: selectedPerils, connection: LogicArguments.OR])

        structure.reset()
        structure.inClaimsGross << claimAccident80 << claimMotor100 << claimMotorHull500
        structure.inUnderwritingInfoGross << underwritingInfoAccident30 << underwritingInfoMotor50 << underwritingInfoMotorHull40

        structure.doCalculation()

        assertEquals "size", 3, structure.outClaimsGross.size()
        assertEquals "claim accident", 80, structure.outClaimsGross[0].ultimate()
        assertEquals "claim motor", 100, structure.outClaimsGross[1].ultimate()
        assertEquals "claim motor hull", 500, structure.outClaimsGross[2].ultimate()
        assertEquals "size", 2, structure.outUnderwritingInfoGross.size()
        assertEquals "premium underwriting info motor", 50, structure.outUnderwritingInfoGross[0].premiumWritten
        assertEquals "premium underwriting info motor hull", 40, structure.outUnderwritingInfoGross[1].premiumWritten
    }

    void testClaimTypes() {

        structure = new Structure(periodScope: periodScope)

        ConstrainedMultiDimensionalParameter selectedTypes = new ConstrainedMultiDimensionalParameter([[ClaimType.ATTRITIONAL.toString(), ClaimType.SINGLE.toString()]],
                ClaimTypeSelectionTableConstraints.COLUMN_TITLES, ConstraintsFactory.getConstraints(ClaimTypeSelectionTableConstraints.IDENTIFIER))
        structure.parmBasisOfStructures = StructuringType.getStrategy(StructuringType.CLAIMTYPES, [claimTypes: selectedTypes])

        structure.inClaimsGross << claimAccident80 << claimMotor100 << claimMotorHull500
        structure.inUnderwritingInfoGross << underwritingInfoAccident30 << underwritingInfoMotor50 << underwritingInfoMotorHull40

        structure.doCalculation()

        assertEquals "size", 2, structure.outClaimsGross.size()
        assertEquals "claim motor", 100, structure.outClaimsGross[0].ultimate()
        assertEquals "claim motor hull", 500, structure.outClaimsGross[1].ultimate()
        assertEquals "size", 0, structure.outUnderwritingInfoGross.size()
    }
}
