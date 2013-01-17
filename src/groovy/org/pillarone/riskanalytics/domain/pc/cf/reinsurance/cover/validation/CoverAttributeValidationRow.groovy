package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.validation

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap

class CoverAttributeValidationRow {
    final String netContractName
    final String cededContractName
    final String legalEntityName
    final String segmentName
    final String perilName
    final String lossKind

    CoverAttributeValidationRow(int rowIndex, ConstrainedMultiDimensionalParameter flexibleCover) {
        netContractName = flexibleCover.getValueAt(rowIndex, CoverMap.CONTRACT_NET_OF_COLUMN_INDEX);
        cededContractName = flexibleCover.getValueAt(rowIndex, CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX);
        legalEntityName = flexibleCover.getValueAt(rowIndex, CoverMap.LEGAL_ENTITY_OF_COLUMN_INDEX);
        segmentName = flexibleCover.getValueAt(rowIndex, CoverMap.SEGMENTS_OF_COLUMN_INDEX);
        perilName = flexibleCover.getValueAt(rowIndex, CoverMap.GENERATORS_OF_COLUMN_INDEX);
        lossKind = flexibleCover.getValueAt(rowIndex, CoverMap.LOSS_KIND_OF_OF_COLUMN_INDEX);
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        CoverAttributeValidationRow that = (CoverAttributeValidationRow) o

        if (cededContractName != that.cededContractName) return false
        if (legalEntityName != that.legalEntityName) return false
        if (lossKind != that.lossKind) return false
        if (netContractName != that.netContractName) return false
        if (perilName != that.perilName) return false
        if (segmentName != that.segmentName) return false

        return true
    }

    int hashCode() {
        int result
        result = (netContractName != null ? netContractName.hashCode() : 0)
        result = 31 * result + (cededContractName != null ? cededContractName.hashCode() : 0)
        result = 31 * result + (legalEntityName != null ? legalEntityName.hashCode() : 0)
        result = 31 * result + (segmentName != null ? segmentName.hashCode() : 0)
        result = 31 * result + (perilName != null ? perilName.hashCode() : 0)
        result = 31 * result + (lossKind != null ? lossKind.hashCode() : 0)
        return result
    }
}
