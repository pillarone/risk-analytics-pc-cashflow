package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.IReinsuranceContractMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;
import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;


import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): implement pattern shifts (asynchron patterns)
public class ClaimCashflowPacket extends MultiValuePacket {

    private static Log LOG = LogFactory.getLog(ClaimCashflowPacket.class);

    private final IClaimRoot baseClaim;

    /** is different from 0 only in the occurrence period */
    private double ultimate;
    /** contains the ultimate value of the occurrence period in every period */
    private double nominalUltimate;
    private double paidIncremental;
    private double paidCumulated;
    private double reportedIncremental;
    private double reportedCumulated;
    private double reserves;

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

    public ClaimCashflowPacket() {
        this(new ClaimRoot(0, ClaimType.ATTRITIONAL, null, null));
    }

    // todo(sku): safer c'tor required, currently used for ultimate modelling
    public ClaimCashflowPacket(IClaimRoot baseClaim) {
        this.baseClaim = baseClaim;
        hasUltimate = true;
        ultimate = baseClaim.getUltimate();
        nominalUltimate = ultimate;
        this.paidCumulated = ultimate();
        this.paidIncremental = ultimate();
        this.reportedCumulated = ultimate();
        this.reportedIncremental = ultimate();
        this.reserves = 0;
        updateDate = baseClaim.getOccurrenceDate();
        setDate(updateDate);
    }


    // todo(sku): safer c'tor required, currently used for ultimate modelling
    public ClaimCashflowPacket(IClaimRoot baseClaim, IPeriodCounter periodCounter) {
        this(baseClaim);
        updatePeriod(periodCounter);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double paidIncremental, double paidCumulated,
                               double reserves, DateTime updateDate, IPeriodCounter periodCounter) {
        this(baseClaim);
        this.ultimate = ultimate;
        this.paidCumulated = paidCumulated;
        this.paidIncremental = paidIncremental;
        this.reportedCumulated = baseClaim.getUltimate();
        this.reportedIncremental = ultimate();
        this.reserves = reserves;
        this.updateDate = updateDate;
        updatePeriod(periodCounter);
        setDate(updateDate);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double paidIncremental, double paidCumulated,
                               double reportedIncremental, double reportedCumulated, double reserves, DateTime updateDate,
                               IPeriodCounter periodCounter) {
        this(baseClaim);
        this.ultimate = ultimate;
        this.paidCumulated = paidCumulated;
        this.paidIncremental = paidIncremental;
        this.reportedCumulated = reportedCumulated;
        this.reportedIncremental = reportedIncremental;
        this.reserves = reserves;
        this.updateDate = updateDate;
        updatePeriod(periodCounter);
        setDate(updateDate);
    }

    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double nominalUltimate, double paidIncremental,
                               double paidCumulated, double reportedIncremental, double reportedCumulated,
                               double reserves, DateTime updateDate, int updatePeriod) {
        this(baseClaim, ultimate, paidIncremental, paidCumulated, reportedIncremental, reportedCumulated, reserves, updateDate, updatePeriod);
        this.nominalUltimate = nominalUltimate;
     }


    public ClaimCashflowPacket(IClaimRoot baseClaim, double ultimate, double paidIncremental, double paidCumulated,
                               double reportedIncremental, double reportedCumulated, double reserves,
                               DateTime updateDate, int updatePeriod) {
        this(baseClaim);
        this.ultimate = ultimate;
        if (ultimate != 0) { nominalUltimate = ultimate; }
        this.paidCumulated = paidCumulated;
        this.paidIncremental = paidIncremental;
        this.reportedCumulated = reportedCumulated;
        this.reportedIncremental = reportedIncremental;
        this.reserves = reserves;
        this.updateDate = updateDate;
        this.updatePeriod = updatePeriod;
        setDate(updateDate);
    }

    public ClaimCashflowPacket withScale(double scaleFactor) {
        ClaimCashflowPacket packet = (ClaimCashflowPacket) super.clone();
        packet.paidCumulated = paidCumulated * scaleFactor;
        packet.paidIncremental = paidIncremental * scaleFactor;
        packet.reportedCumulated = reportedCumulated * scaleFactor;
        packet.reportedIncremental = reportedIncremental * scaleFactor;
        packet.reserves = reserves * scaleFactor;
        return packet;
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
     * @return reserves - outstanding
     */
    public double ibnr() {
        return reserved() - outstanding();
    }

    /** reported = cumulated paid + outstanding */

    /**
     * @return reported cumulated - paid cumulated
     */
    public double outstanding() {
        return reportedCumulated - paidCumulated;
    }

    /**
     * @return ultimate * (1 - cumulated payout factor)
     */
    public double reserved() {
        return reserves;
    }

    /**
     * @return developed ultimate at time t_{i} and final ultimate if i = last paidIncremental date of claim
     */
    public double developedUltimate() {
        return reserved() + paidCumulated;
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

    public double developmentResult() {
        return baseClaim.hasTrivialPayout() ? 0 : developedUltimate() - nominalUltimate;
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

    public double getPaidIncremental() {
        return paidIncremental;
    }

    public double getReportedIncremental() {
        return reportedIncremental;
    }

    public IPerilMarker peril() { return peril; }
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
        valuesToSave.put(PAID, paidIncremental);
        valuesToSave.put(RESERVES, reserved());
        valuesToSave.put(REPORTED, reportedIncremental);
        valuesToSave.put(IBNR, ibnr());
        valuesToSave.put(OUTSTANDING, outstanding());
        valuesToSave.put(DEVELOPED_RESULT, developmentResult());
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
        result.append(reportedIncremental);
        result.append(separator);
        result.append(paidIncremental);
        result.append(separator);
        result.append(updateDate);
        return result.toString();
    }

    public final static String ULTIMATE = "ultimate";
    public final static String REPORTED = "reportedIncremental";
    public final static String PAID = "paidIncremental";
    public final static String IBNR = "IBNR";
    public final static String RESERVES = "reserves";
    public final static String OUTSTANDING = "outstanding";
    public final static String DEVELOPED_ULTIMATE = "developedUltimate";
    public final static String DEVELOPED_RESULT = "developedResult";

    public final static List<String> NON_TRIVIAL_PAYOUT_IBNR = Arrays.asList(ULTIMATE, PAID, RESERVES, REPORTED, IBNR, OUTSTANDING, DEVELOPED_RESULT);

    public IClaimRoot getBaseClaim() {
        return baseClaim;
    }

    /**
     * the updateDate might be the incurred, paidIncremental, reportedIncremental date, depending on the updated double properties above
     *
     * @return
     */
    public DateTime getUpdateDate() {
        return updateDate;
    }

    public DateTime getOccurrenceDate() {
        return baseClaim.getOccurrenceDate();
    }

    public double getPaidCumulated() {
        return paidCumulated;
    }

    public double getReportedCumulated() {
        return reportedCumulated;
    }

    public Integer getUpdatePeriod() {
        return updatePeriod;
    }

    @Deprecated
    public boolean hasUltimate() { return hasUltimate; }

    public double getNominalUltimate() {
        return nominalUltimate;
    }
}
