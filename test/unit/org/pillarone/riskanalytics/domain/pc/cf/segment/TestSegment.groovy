package org.pillarone.riskanalytics.domain.pc.cf.segment

import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.parameterization.ComboBoxTableMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.engine.IterationScope
import org.pillarone.riskanalytics.domain.pc.cf.discounting.IDiscountMarker
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker
import org.pillarone.riskanalytics.domain.utils.marker.IUnderwritingInfoMarker

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class TestSegment {

    static Segment get(String name, IterationScope iterationScope,
                Map<IPerilMarker, Double> perilPortions,
                Map<IUnderwritingInfoMarker, Double> uwInfoPortions,
                Map<IReserveMarker, Double> reservePortions,
                IDiscountMarker discountCurve,
                ILegalEntityMarker company = null
    ) {
        Segment segment = new Segment(name: name)
        segment.iterationScope = iterationScope
        segment.periodStore = new PeriodStore(segment.iterationScope.periodScope)

        if (perilPortions?.size() > 0) {
            segment.parmClaimsPortions = new ConstrainedMultiDimensionalParameter(
                [perilPortions.keySet()*.name.toList(), perilPortions.values().toList()], [Segment.PERIL, Segment.PORTION],
                ConstraintsFactory.getConstraints(PerilPortion.IDENTIFIER))
        }
        if (uwInfoPortions?.size() > 0) {
            segment.parmUnderwritingPortions = new ConstrainedMultiDimensionalParameter(
                [uwInfoPortions.keySet()*.name.toList(), uwInfoPortions.values().toList()], [Segment.UNDERWRITING, Segment.PORTION],
                ConstraintsFactory.getConstraints(UnderwritingPortion.IDENTIFIER))
        }
        if (reservePortions?.size() > 0) {
            segment.parmReservesPortions = new ConstrainedMultiDimensionalParameter(
                [reservePortions.keySet()*.name.toList(), reservePortions.values().toList()], [Segment.RESERVE, Segment.PORTION],
                ConstraintsFactory.getConstraints(ReservePortion.IDENTIFIER))
        }
        ComboBoxTableMultiDimensionalParameter discountComboBox = new ComboBoxTableMultiDimensionalParameter(
                ["subDiscountIndex"], ["Discount Index"], IDiscountMarker)
        discountComboBox.comboBoxValues.put('subDiscountIndex', discountCurve)
        segment.parmDiscounting = discountComboBox
        if (company) {
            segment.parmCompany = new ConstrainedString(ILegalEntityMarker, company.name)
            segment.parmCompany.selectedComponent = company
        }
        return segment
    }
}
