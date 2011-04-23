package org.pillarone.riskanalytics.domain.pc.cf.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.NotInProjectionHorizon;


import java.util.*;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class ClaimCashflowPacket extends MultiValuePacket {

    private static Log LOG = LogFactory.getLog(ClaimCashflowPacket.class);

    // todo(sku): ask msp, should be final but setting it final is not possible with default c'tor
    private ClaimRoot baseClaim;

    private double paidIncremental;
    private double paidCumulated;
    private double reportedIncremental;
    private double reportedCumulated;
    private double reserves;

    /** the updateDate might be the incurred, paidIncremental, reportedIncremental date, depending on the updated double properties above */
    private DateTime updateDate;
    private Integer updatePeriod;

    private boolean first;

    // c'tor required for DefaultResultStructureBuilder L124
    public ClaimCashflowPacket() {
    }

    /** todo(sku): safer c'tor required, currently used for ultimate modelling */
    public ClaimCashflowPacket(ClaimRoot baseClaim) {
        this.baseClaim = baseClaim;
        updateDate = baseClaim.getOccurrenceDate();
        setDate(updateDate);
        first = true;
    }

    public ClaimCashflowPacket(ClaimRoot baseClaim, double paidIncremental, double paidCumulated,
                               double reportedIncremental, double reportedCumulated, double reserves,
                               DateTime updateDate, IPeriodCounter periodCounter, int number) {
        this(baseClaim);
        this.paidCumulated = paidCumulated;
        this.paidIncremental = paidIncremental;
        this.reportedCumulated = reportedCumulated;
        this.reportedIncremental = reportedIncremental;
        this.reserves = reserves;
        this.updateDate = updateDate;
        updatePeriod(periodCounter);
        setDate(updateDate);
        first = number == 1;
    }

    /**
     * Used to modify the packet date property according the persistence date. 
     * @param claim
     * @param persistenceDate
     */
    // todo(sku): ask msp about copy/clone constructor
    // todo(sku): fix!
    private ClaimCashflowPacket(ClaimCashflowPacket claim, DateTime persistenceDate) {
        this(claim.baseClaim);
        paidIncremental = claim.paidIncremental;
        paidCumulated = claim.paidCumulated;
        reportedCumulated = claim.reportedCumulated;
        reportedIncremental = claim.reportedIncremental;
        reserves = claim.reserves;
        updateDate = claim.updateDate;
        setDate(persistenceDate);
    }

    public double ibnr() {
        return reserves - outstanding();
    }

    public double outstanding() {
        return reportedCumulated - paidCumulated;
    }

    public double reserved() {
        return reserves;
    }

    /**
     * @return developed ultimate at time t_{i} and final ultimate if i = last paidIncremental date of claim
     */
    public double developedUltimate() {
        return reserved() + paidCumulated;
    }

    public double  developmentResult() {
        return developedUltimate() - baseClaim.getUltimate();
    }

    /**
     * @param periodCounter
     * @return update period in the context of the simulation engine
     */
    public int updatePeriod(IPeriodCounter periodCounter) {
        if (updatePeriod == null) {
            try {
                updatePeriod = periodCounter.belongsToPeriod(updateDate);
            }
            catch (NotInProjectionHorizon ex) {
                LOG.debug(updateDate + " is not in projection horizon");
            }
        }
        return updatePeriod;
    }

    public ClaimCashflowPacket getClaimCashflow() {
        return this;
    }

    /**
     * Helper method to fill underwriting period channels
     * @return
     */
    public ClaimCashflowPacket getClaimUnderwritingPeriod() {
        return new ClaimCashflowPacket(this, baseClaim.getOccurrenceDate());
    }

    public double getPaidIncremental() {
        return paidIncremental;
    }

    public double getReportedIncremental() {
        return reportedIncremental;
    }

    /**
     * @return 0 except for the occurrence period, nominal ultimate without any index applied
     */
    public double ultimate() {
        return first ? baseClaim.getUltimate() : 0d;
    }

    /**
     * Add 'properties' calculated on the fly
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> valuesToSave = new HashMap<String, Number>();
        valuesToSave.put(ULTIMATE, baseClaim.getUltimate());    // todo(sku): leads to failure during result tree building
        if (!baseClaim.hasTrivialPayout()) {
            valuesToSave.put(PAID, paidIncremental);
            valuesToSave.put(RESERVES, reserved());
            if (baseClaim.hasIBNR()) {
                valuesToSave.put(REPORTED, reportedIncremental);
                valuesToSave.put(IBNR, ibnr());
                valuesToSave.put(OUTSTANDING, outstanding());
            }
        }
        valuesToSave.put(DEVELOPED_RESULT, developmentResult());
        return valuesToSave;
    }

    /**
     * Add 'properties' calculated on the fly
     * @return
     */
    @Override
    public List<String> getFieldNames() {
        if (baseClaim.hasTrivialPayout()) {
            return TRIVIAL_PAYOUT;
        }
        else if (baseClaim.hasIBNR()) {
            return NON_TRIVIAL_PAYOUT_IBNR;
        }
        else {
            return NON_TRIVIAL_PAYOUT;
        }
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(baseClaim.getUltimate());
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

    public final static List<String> TRIVIAL_PAYOUT = Arrays.asList(ULTIMATE, DEVELOPED_RESULT);
    public final static List<String> NON_TRIVIAL_PAYOUT = Arrays.asList(ULTIMATE, PAID, RESERVES, DEVELOPED_RESULT);
    public final static List<String> NON_TRIVIAL_PAYOUT_IBNR = Arrays.asList(ULTIMATE, PAID, RESERVES, REPORTED, IBNR, OUTSTANDING, DEVELOPED_RESULT);
}
