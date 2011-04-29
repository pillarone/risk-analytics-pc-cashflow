package org.pillarone.riskanalytics.domain.pc.cf.exposure;

import org.pillarone.riskanalytics.core.packets.MultiValuePacket;
import org.pillarone.riskanalytics.domain.utils.PacketUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class UnderwritingInfoPacket extends MultiValuePacket {
    
    private double premium;
    private double numberOfPolicies;
    private double sumInsured;
    private double maxSumInsured;

    private ExposureInfo exposure;

    private UnderwritingInfoPacket original;

    public UnderwritingInfoPacket() {
        super();
    }

    /**
     * 
     * @param policyFactor
     * @param premiumFactor
     * @return creates a cloned instance with numberOfPolicies and premium according to parameters
     */
    public UnderwritingInfoPacket withFactorsApplied(double policyFactor, double premiumFactor) {
        UnderwritingInfoPacket modified = (UnderwritingInfoPacket) this.clone();
        modified.numberOfPolicies *= policyFactor;
        modified.premium *= premiumFactor;
        return modified;
    }

    /**
     * @param base
     * @return return the packet value according the base, 1 for absolute
     */
    public double scaleValue(ExposureBase base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case PREMIUM_WRITTEN:
                return premium;
            case NUMBER_OF_POLICIES:
                return numberOfPolicies;
        }
        return 0;
    }

    /**
     * @param base
     * @return return the packet value according the base, 1 for absolute
     */
    public double scaleValue(FrequencyBase base) {
        switch (base) {
            case ABSOLUTE:
                return 1d;
            case NUMBER_OF_POLICIES:
                return numberOfPolicies;
        }
        return 1;
    }

    public boolean sameContent(UnderwritingInfoPacket other) {
        return (PacketUtilities.sameContent(this, other)
                && numberOfPolicies == other.getNumberOfPolicies()
                && sumInsured == other.getSumInsured()
                && maxSumInsured == other.getMaxSumInsured()
                && premium == other.getPremium())
                && (exposure == null && other.getExposure() == null
                   || exposure.equals(other.getExposure()));
    }

    private static final String PREMIUM = "premium";
    /**
     * Warning: if the number or names of values is modified, UnderwritingBatchInsertDBCollector
     * has to be corrected accordingly.
     *
     * @return
     * @throws IllegalAccessException
     */
    @Override
    public Map<String, Number> getValuesToSave() throws IllegalAccessException {
        Map<String, Number> map = new HashMap<String, Number>();
        map.put(PREMIUM, premium);
        return map;
    }

    public final static List<String> FIELD_NAMES = Arrays.asList(PREMIUM);

    @Override
    public List<String> getFieldNames() {
        return FIELD_NAMES;
    }

    @Override
    public String toString() {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(premium);
        result.append(separator);
        result.append(numberOfPolicies);
        return result.toString();
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public double getNumberOfPolicies() {
        return numberOfPolicies;
    }

    public void setNumberOfPolicies(double numberOfPolicies) {
        this.numberOfPolicies = numberOfPolicies;
    }

    public double getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(double sumInsured) {
        this.sumInsured = sumInsured;
    }

    public double getMaxSumInsured() {
        return maxSumInsured;
    }

    public void setMaxSumInsured(double maxSumInsured) {
        this.maxSumInsured = maxSumInsured;
    }

    public ExposureInfo getExposure() {
        return exposure;
    }

    public void setExposure(ExposureInfo exposure) {
        this.exposure = exposure;
    }

    public UnderwritingInfoPacket getOriginal() {
        return original;
    }

    public void setOriginal(UnderwritingInfoPacket original) {
        this.original = original;
    }
}
