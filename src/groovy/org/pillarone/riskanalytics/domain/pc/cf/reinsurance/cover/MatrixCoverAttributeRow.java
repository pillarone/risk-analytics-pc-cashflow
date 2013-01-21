package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;

import java.util.ArrayList;
import java.util.List;

public class MatrixCoverAttributeRow {

    IReinsuranceContractMarker netContract;
    IReinsuranceContractMarker cededContract;
    ILegalEntityMarker legalEntity;
    ISegmentMarker segment;
    IPerilMarker peril;
    ClaimTypeSelector claimTypeSelector;

    public MatrixCoverAttributeRow(int rowIndex, ConstrainedMultiDimensionalParameter flexibleCover) {
        netContract = (IReinsuranceContractMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.CONTRACT_NET_OF_COLUMN_INDEX);
        cededContract = (IReinsuranceContractMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX);
        legalEntity = (ILegalEntityMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.LEGAL_ENTITY_OF_COLUMN_INDEX);
        segment = (ISegmentMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.SEGMENTS_OF_COLUMN_INDEX);
        peril = (IPerilMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.GENERATORS_OF_COLUMN_INDEX);
        claimTypeSelector = ClaimTypeSelector.valueOf((String) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.LOSS_KIND_OF_OF_COLUMN_INDEX));
    }

    public List<ClaimCashflowPacket> filter(List<ClaimCashflowPacket> source) {
        List<ClaimCashflowPacket> result = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket claim : source) {
            if (netContract == null && cededContract == null && claim.reinsuranceContract() != null){
                // covers gross claims only.
            }else if ((netContract == null || netContract == claim.reinsuranceContract()) &&
                    (cededContract == null || cededContract == claim.reinsuranceContract()) &&
                    (legalEntity == null || legalEntity == claim.legalEntity()) &&
                    (segment == null || segment == claim.segment()) &&
                    (peril == null || peril == claim.peril()) &&
                    claimTypeMatches(claimTypeSelector, claim)) {
                result.add(claim);
            }
        }
        source.removeAll(result);
        return result;
    }

    private boolean claimTypeMatches(ClaimTypeSelector claimTypeSelector, ClaimCashflowPacket claim) {
        return claimTypeSelector == ClaimTypeSelector.ANY || ClaimType.valueOf(claimTypeSelector.name()) == claim.getClaimType();
    }

    public boolean coverGrossClaimsOnly() {
        return netContract == null && cededContract == null;
    }

    public IReinsuranceContractMarker getNetContract() {
        return netContract;
    }

    public IReinsuranceContractMarker getCededContract() {
        return cededContract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatrixCoverAttributeRow that = (MatrixCoverAttributeRow) o;

        if (cededContract != null ? !cededContract.equals(that.cededContract) : that.cededContract != null)
            return false;
        if (claimTypeSelector != that.claimTypeSelector) return false;
        if (legalEntity != null ? !legalEntity.equals(that.legalEntity) : that.legalEntity != null) return false;
        if (netContract != null ? !netContract.equals(that.netContract) : that.netContract != null) return false;
        if (peril != null ? !peril.equals(that.peril) : that.peril != null) return false;
        if (segment != null ? !segment.equals(that.segment) : that.segment != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = netContract != null ? netContract.hashCode() : 0;
        result = 31 * result + (cededContract != null ? cededContract.hashCode() : 0);
        result = 31 * result + (legalEntity != null ? legalEntity.hashCode() : 0);
        result = 31 * result + (segment != null ? segment.hashCode() : 0);
        result = 31 * result + (peril != null ? peril.hashCode() : 0);
        result = 31 * result + (claimTypeSelector != null ? claimTypeSelector.hashCode() : 0);
        return result;
    }
}