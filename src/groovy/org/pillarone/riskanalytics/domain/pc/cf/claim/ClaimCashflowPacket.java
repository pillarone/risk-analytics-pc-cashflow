package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.Component;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureInfo;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IReserveMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;


import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimCashflowPacket extends MultiValuePacket {

    private static Log LOG = LogFactory.getLog(ClaimCashflowPacket.class);

    /**
     *  This property is used for the calculation of derived figures and has to be individually set for every gross, ceded
     *  and net claim.
     */
    private final IClaimRoot baseClaim;
    /**
     *  This property is used as key and should be the same for claims derived of a common original claim. Don't use
     *  it for any calculations!
     */
    private final IClaimRoot keyClaim;
    private ExposureInfo exposureInfo;

    /** is different from 0 only in the occurrence period */
    private double ultimate;
    /** contains the ultimate value of the occurrence period in every period */
    private double nominalUltimate;
    private double paidIncrementalIndexed;
    private double paidCumulatedIndexed;
    private double reportedIncrementalIndexed;
    private double reportedCumulatedIndexed;
    private double reservesIndexed;
    private double appliedIndexValue;

    private DateTime updateDate;
    private Integer updatePeriod;

    /**
     * true only if this packet belongs to the occurrence period of the claim
     */
    @Deprecated
    private boolean hasUltimate;

    private IPerilMarker peril;
    private ISegmentMarker segment;
    private IReinsuranceContractMarker reinsuranceContract;
    private ILegalEntityMarker legalEntity;
    private IReserveMarker reserve;

    private List<Factors> discountFactors;

    /**
     * Used for 'zero' claims
     */
    public ClaimCashflowPacket() {
        this(new ClaimRoot(0, ClaimType.ATTRITIONAL, null, null));
    }

    // todo(sku): safer c'tor required, currently used for ultimate modelling
    private ClaimCashflowPacket(IClaimRoot baseClaim) {
        this(baseClaim, baseClaim);
    }

    private ClaimCashflowPacket(IClaimRoot baseClaim, IClaimRoot keyClaim) {
        this.baseClaim = baseClaim;
        this.keyClaim = keyClaim;
        hasUltimate = true;
        ultimate = baseClaim.getUltimate();
        nominalUltimate = ultimate;
        this.paidCumulatedIndexed = ultimate();
        this.paidIncrementalIndexed = ultimate();
        this.reportedCumulatedIndexed = ultimate();
        this.reportedIncrementalIndexed = ultimate();
        this.reservesIndexed = 0;
        updateDate = baseClaim.getOccurrenceDate();
        setDate(updateDate);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double paidIncrementalIndexed, double paidCumulatedIndexed,
                               double reservesIndexed, ExposureInfo exposureInfo, DateTime updateDate, IPeriodCounter periodCounter) {
        this(baseClaim);
        this.ultimate = ultimate;
        this.paidCumulatedIndexed = paidCumulatedIndexed;
        this.paidIncrementalIndexed = paidIncrementalIndexed;
        this.reportedCumulatedIndexed = baseClaim.getUltimate();
        this.reportedIncrementalIndexed = ultimate();
        this.reservesIndexed = reservesIndexed;
        this.updateDate = updateDate;
        this.exposureInfo = exposureInfo;
        updatePeriod(periodCounter);
        setDate(updateDate);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double paidIncrementalIndexed, double paidCumulatedIndexed,
                               double reportedIncrementalIndexed, double reportedCumulatedIndexed, double reservesIndexed,
                               ExposureInfo exposureInfo, DateTime updateDate, IPeriodCounter periodCounter) {
        this(baseClaim);
        this.ultimate = ultimate;
        this.paidCumulatedIndexed = paidCumulatedIndexed;
        this.paidIncrementalIndexed = paidIncrementalIndexed;
        this.reportedCumulatedIndexed = reportedCumulatedIndexed;
        this.reportedIncrementalIndexed = reportedIncrementalIndexed;
        this.reservesIndexed = reservesIndexed;
        this.updateDate = updateDate;
        this.exposureInfo = exposureInfo;
        updatePeriod(periodCounter);
        setDate(updateDate);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double nominalUltimate, double paidIncrementalIndexed,
                               double paidCumulatedIndexed, double reportedIncrementalIndexed, double reportedCumulatedIndexed,
                               double reservesIndexed, ExposureInfo exposureInfo, DateTime updateDate, int updatePeriod) {
        this(baseClaim, ultimate, paidIncrementalIndexed, paidCumulatedIndexed, reportedIncrementalIndexed, reportedCumulatedIndexed, reservesIndexed,
                exposureInfo, updateDate, updatePeriod);
        this.nominalUltimate = nominalUltimate;
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, IClaimRoot keyClaim, double ultimate, double nominalUltimate, double paidIncrementalIndexed,
                               double paidCumulatedIndexed, double reportedIncrementalIndexed, double reportedCumulatedIndexed,
                               double reservesIndexed, ExposureInfo exposureInfo, DateTime updateDate, int updatePeriod) {
        this(baseClaim, keyClaim, ultimate, paidIncrementalIndexed, paidCumulatedIndexed, reportedIncrementalIndexed,
                reportedCumulatedIndexed, reservesIndexed, exposureInfo, updateDate, updatePeriod);
        this.nominalUltimate = nominalUltimate;
    }


    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double paidIncrementalIndexed, double paidCumulatedIndexed,
                               double reportedIncrementalIndexed, double reportedCumulatedIndexed, double reservesIndexed,
                               ExposureInfo exposureInfo, DateTime updateDate, int updatePeriod) {
        this(baseClaim);
        this.ultimate = ultimate;
        if (ultimate != 0) { nominalUltimate = ultimate; }
        this.paidCumulatedIndexed = paidCumulatedIndexed;
        this.paidIncrementalIndexed = paidIncrementalIndexed;
        this.reportedCumulatedIndexed = reportedCumulatedIndexed;
        this.reportedIncrementalIndexed = reportedIncrementalIndexed;
        this.reservesIndexed = reservesIndexed;
        this.updateDate = updateDate;
        this.updatePeriod = updatePeriod;
        this.exposureInfo = exposureInfo;
        setDate(updateDate);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, IClaimRoot keyClaim, double ultimate, double paidIncrementalIndexed, double paidCumulatedIndexed,
                               double reportedIncrementalIndexed, double reportedCumulatedIndexed, double reservesIndexed,
                               ExposureInfo exposureInfo, DateTime updateDate, int updatePeriod) {
        this(baseClaim, keyClaim);
        this.ultimate = ultimate;
        if (ultimate != 0) { nominalUltimate = ultimate; }
        this.paidCumulatedIndexed = paidCumulatedIndexed;
        this.paidIncrementalIndexed = paidIncrementalIndexed;
        this.reportedCumulatedIndexed = reportedCumulatedIndexed;
        this.reportedIncrementalIndexed = reportedIncrementalIndexed;
        this.reservesIndexed = reservesIndexed;
        this.updateDate = updateDate;
        this.updatePeriod = updatePeriod;
        this.exposureInfo = exposureInfo;
        setDate(updateDate);
    }

    /**
     * Used to modify the packet date property according the persistence date on a cloned instance.
     *
     * @param persistenceDate
     */
    private ClaimCashflowPacket withDate(DateTime persistenceDate) {
        ClaimCashflowPacket packet = (ClaimCashflowPacket) super.clone();
        packet.setDate(persistenceDate);
        return packet;
    }

    /**
     * @return reserves - outstandingIndexed
     */
    public double ibnrIndexed() {
        return reservedIndexed() - outstandingIndexed();
    }

    /** reported = cumulated paid + outstandingIndexed */

    /**
     * @return reported cumulated - paid cumulated
     */
    public double outstandingIndexed() {
        return reportedCumulatedIndexed - paidCumulatedIndexed;
    }

    /**
     * @return ultimate * (1 - cumulated payout factor)
     */
    public double reservedIndexed() {
        return reservesIndexed;
    }

    /**
     * @return developed ultimate at time t_{i} and final ultimate if i = last paidIncrementalIndexed date of claim
     */
    public double developedUltimate() {
        return reservedIndexed() + paidCumulatedIndexed;
    }

    /**
     * @return 0 except for the occurrence period, nominal ultimate without any index applied
     */
    public double ultimate() {
        return ultimate;
    }

    public double nominalUltimate() {
        return nominalUltimate;
    }

    public double developmentResultCumulative() {
        return baseClaim.hasTrivialPayout() ? 0 : developedUltimate() - nominalUltimate;
    }

    public double premiumRisk() {
        return ultimate() == 0d ? 0 : reportedIncrementalIndexed + ibnrIndexed();
    }

    public double reserveRisk() {
        return ultimate() != 0d ? 0 : reportedIncrementalIndexed + ibnrIndexed();
    }

    /**
     * @param periodCounter
     * @return update period in the context of the simulation engine
     */
    public int updatePeriod(IPeriodCounter periodCounter) {
        if (updatePeriod == null) {
            try {
                updatePeriod = periodCounter.belongsToPeriod(updateDate);
            } catch (NotInProjectionHorizon ex) {
                LOG.debug(updateDate + " is not in projection horizon");
            }
        }
        return updatePeriod;
    }

    public int occurrencePeriod(IPeriodCounter periodCounter) {
        return baseClaim.getOccurrencePeriod(periodCounter);
    }

    /**
     * Helper method to fill underwriting period channels
     *
     * @return a clone of the current instance with the date equal to the occurrence date
     */
    public ClaimCashflowPacket getClaimUnderwritingPeriod() {
        return withDate(baseClaim.getOccurrenceDate());
    }

    public double getPaidIncrementalIndexed() {
        return paidIncrementalIndexed;
    }

    public double getReportedIncrementalIndexed() {
        return reportedIncrementalIndexed;
    }

    public IPerilMarker peril() { return peril; }
    public IReserveMarker reserve() { return reserve; }
    public ISegmentMarker segment() { return segment; }
    public IReinsuranceContractMarker reinsuranceContract() { return reinsuranceContract; }
    public ILegalEntityMarker legalEntity() { return legalEntity; }

    public void setMarker(IComponentMarker marker) {
        if (marker == null) return;
        if (IPerilMarker.class.isAssignableFrom(marker.getClass())) {
            peril = (IPerilMarker) marker;
        }
        else if (ISegmentMarker.class.isAssignableFrom(marker.getClass())) {
            segment = (ISegmentMarker) marker;
        }
        else if (IReinsuranceContractMarker.class.isAssignableFrom(marker.getClass())) {
            reinsuranceContract = (IReinsuranceContractMarker) marker;
        }
        else if (ILegalEntityMarker.class.isAssignableFrom(marker.getClass())) {
            legalEntity = (ILegalEntityMarker) marker;
        }
        else if (IReserveMarker.class.isAssignableFrom(marker.getClass())) {
            reserve = (IReserveMarker) marker;
        }
    }

    public void removeMarker(Class<? extends IComponentMarker> marker) {
        if (marker == null) return;
        if (IPerilMarker.class.isAssignableFrom(marker)) {
            peril = null;
        }
        else if (ISegmentMarker.class.isAssignableFrom(marker)) {
            segment = null;
        }
        else if (IReinsuranceContractMarker.class.isAssignableFrom(marker)) {
            reinsuranceContract = null;
        }
        else if (ILegalEntityMarker.class.isAssignableFrom(marker)) {
            legalEntity = null;
        }
        else if (IReserveMarker.class.isAssignableFrom(marker)) {
            reserve = null;
        }
    }

    public void removeMarkers() {
        peril = null;
        segment = null;
        reinsuranceContract = null;
        legalEntity = null;
        reserve = null;
    }

    public ClaimType getClaimType() {
        return baseClaim.getClaimType();
    }
    public ExposureInfo getExposureInfo() {
        return exposureInfo;
    }
    public boolean hasExposureInfo() {
        return exposureInfo != null;
    }

    /**
     * Add 'properties' calculated on the fly
     *
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>();
        valuesToSave.put(ULTIMATE, ultimate);    // this and missing default c'tor (final!) leads to failure during result tree building
        valuesToSave.put(PAID_INDEXED, paidIncrementalIndexed);
        valuesToSave.put(RESERVES_INDEXED, reservedIndexed());
        valuesToSave.put(REPORTED_INDEXED, reportedIncrementalIndexed);
        valuesToSave.put(IBNR_INDEXED, ibnrIndexed());
        valuesToSave.put(OUTSTANDING_INDEXED, outstandingIndexed());
        valuesToSave.put(DEVELOPED_RESULT_INDEXED, developmentResultCumulative());
        valuesToSave.put(APPLIED_INDEX_VALUE, appliedIndexValue);
        return valuesToSave;
    }

    /**
     * Add 'properties' calculated on the fly
     *
     * @return
     */
    @Override
    public List<String> getFieldNames() {
        return NON_TRIVIAL_PAYOUT_IBNR;
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(ultimate);
        result.append(separator);
        result.append(reportedIncrementalIndexed);
        result.append(separator);
        result.append(paidIncrementalIndexed);
        result.append(separator);
        result.append(updateDate);
        result.append(separator);
        result.append(baseClaim.getClaimType());
        if (peril() != null) {
            result.append(separator);
            result.append(((Component) peril()).getNormalizedName());
        }
        if (segment() != null) {
            result.append(separator);
            result.append(((Component) segment()).getNormalizedName());
        }
        return result.toString();
    }

    public final static String ULTIMATE = "ultimate";
    public final static String REPORTED_INDEXED = "reportedIncrementalIndexed";
    public final static String PAID_INDEXED = "paidIncrementalIndexed";
    public final static String IBNR_INDEXED = "IBNRIndexed";
    public final static String RESERVES_INDEXED = "reservesIndexed";
    public final static String OUTSTANDING_INDEXED = "outstandingIndexed";
    public final static String DEVELOPED_ULTIMATE = "developedUltimateIndexed";
    public final static String DEVELOPED_RESULT_INDEXED = "developedResultIndexed";
    public final static String APPLIED_INDEX_VALUE = "appliedIndexValue";

    public final static List<String> NON_TRIVIAL_PAYOUT_IBNR = Arrays.asList(ULTIMATE, PAID_INDEXED, RESERVES_INDEXED,
            REPORTED_INDEXED, IBNR_INDEXED, OUTSTANDING_INDEXED, DEVELOPED_RESULT_INDEXED, APPLIED_INDEX_VALUE);

    public IClaimRoot getBaseClaim() {
        return baseClaim;
    }

    /**
     * the updateDate might be the incurred, paidIncrementalIndexed, reportedIncrementalIndexed date, depending on the updated double properties above
     *
     * @return
     */
    public DateTime getUpdateDate() {
        return updateDate;
    }

    public DateTime getOccurrenceDate() {
        return baseClaim.getOccurrenceDate();
    }

    public double getPaidCumulatedIndexed() {
        return paidCumulatedIndexed;
    }

    public double getReportedCumulatedIndexed() {
        return reportedCumulatedIndexed;
    }

    public Integer getUpdatePeriod() {
        return updatePeriod;
    }

    @Deprecated
    public boolean hasUltimate() { return hasUltimate; }

    public double getNominalUltimate() {
        return nominalUltimate;
    }

    public boolean hasEvent() {
        return getBaseClaim().getClaimType().equals(ClaimType.EVENT) || getBaseClaim().getClaimType().equals(ClaimType.AGGREGATED_EVENT);
    }

    public EventPacket getEvent() {
        return getBaseClaim().getEvent();
    }

    public double getAppliedIndexValue() {
        return appliedIndexValue;
    }

    public void setAppliedIndexValue(double appliedIndexValue) {
        this.appliedIndexValue = appliedIndexValue;
    }

    public IClaimRoot getKeyClaim() {
        return keyClaim;
    }

    /** property is normally set in the gross phase of the segment */
    public List<Factors> getDiscountFactors() {
        return discountFactors;
    }

    public void setDiscountFactors(List<Factors> discountFactors) {
        this.discountFactors = discountFactors;
    }
}
