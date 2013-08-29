package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelector;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimValidator;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoUtils;
import org.pillarone.riskanalytics.domain.utils.constant.ReinsuranceContractBase;
import org.pillarone.riskanalytics.domain.utils.marker.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatrixCoverAttributeRow {

    IReinsuranceContractMarker netContract;
    IReinsuranceContractMarker cededContract;
    ILegalEntityMarker legalEntity;
    ISegmentMarker segment;
    Set<ISegmentMarker> implicitSegments = new HashSet<ISegmentMarker>();
    IClaimMarker peril;
    ClaimTypeSelector claimTypeSelector;
    boolean isStructure;

    private ClaimValidator claimValidator;

    public MatrixCoverAttributeRow(int rowIndex, boolean isStructure, ConstrainedMultiDimensionalParameter flexibleCover) {
        this.isStructure = isStructure;
        if (!isStructure) {
            netContract = (IReinsuranceContractMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.CONTRACT_NET_OF_COLUMN_INDEX);
            cededContract = (IReinsuranceContractMarker) flexibleCover.getValueAtAsObject(rowIndex, CoverMap.CONTRACT_CEDED_OF_COLUMN_INDEX);
        }
        legalEntity = (ILegalEntityMarker) flexibleCover.getValueAtAsObject(rowIndex, isStructure ? MatrixStructureContraints.LEGAL_ENTITY_OF_COLUMN_INDEX : CoverMap.LEGAL_ENTITY_OF_COLUMN_INDEX);
        segment = (ISegmentMarker) flexibleCover.getValueAtAsObject(rowIndex, isStructure ? MatrixStructureContraints.SEGMENTS_OF_COLUMN_INDEX : CoverMap.SEGMENTS_OF_COLUMN_INDEX);
        peril = (IClaimMarker) flexibleCover.getValueAtAsObject(rowIndex, isStructure ? MatrixStructureContraints.GENERATORS_OF_COLUMN_INDEX : CoverMap.GENERATORS_OF_COLUMN_INDEX);
        claimTypeSelector = ClaimTypeSelector.valueOf((String) flexibleCover.getValueAtAsObject(rowIndex, isStructure ? MatrixStructureContraints.LOSS_KIND_OF_OF_COLUMN_INDEX : CoverMap.LOSS_KIND_OF_OF_COLUMN_INDEX));
    }

    public List<ClaimCashflowPacket> filter(List<ClaimCashflowPacket> source, boolean checkedSign) {
        if (claimValidator == null) { claimValidator = new ClaimValidator(); }
        List<ClaimCashflowPacket> result = new ArrayList<ClaimCashflowPacket>();
        for (ClaimCashflowPacket claim : source) {
            if (!isStructure && netContract == null && cededContract == null && claim.reinsuranceContract() != null) {
                // covers gross claims only.
            }
            else if ((netContract == null || netContract == claim.reinsuranceContract()) &&
                    (cededContract == null || cededContract == claim.reinsuranceContract()) &&
                    (legalEntity == null || legalEntity == claim.legalEntity()) &&
                    (segment == null || segment == claim.segment()) &&
                    (peril == null || peril == claim.peril() || peril == claim.reserve()) &&
                    claimTypeMatches(claimTypeSelector, claim)) {
                if (checkedSign && !isStructure) {
                    result.add(ClaimValidator.positiveNominalUltimate(claim));
                }
                else {
                    result.add(claim);
                }
                if (claim.segment() != null) {
                    implicitSegments.add(claim.segment());
                }
            }
        }
        source.removeAll(result);
        return result;
    }

    public List filterUnderwritingInfos(List source) {
        List result = new ArrayList();
        for (Object underwritingInfo : source) {
            ISegmentMarker uwSegment = ((UnderwritingInfoPacket) underwritingInfo).segment();
            if ((legalEntity == null || legalEntity == ((UnderwritingInfoPacket) underwritingInfo).legalEntity()) &&
                    ((segment == uwSegment) || (segment == null && implicitSegments.contains(uwSegment)))) {
                result.add(underwritingInfo);
            }
        }
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

    public ISegmentMarker getSegment() {
        return segment;
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
