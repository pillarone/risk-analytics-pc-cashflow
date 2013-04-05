package org.pillarone.riskanalytics.life.longevity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.SimulationException;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.event.EventPacket;
import org.pillarone.riskanalytics.life.Gender;

/**
 * author simon.parten @ art-allianz . com
 */
public class SinglePoint implements IClaimRoot  {

    private final int memberAge;
    private final Gender gender;
    private final int retirementYear;
    private final double memberAnnuity;
    private final double spouseAnnuity;
    private final int spouseAge;
    private final Gender spouseGender;
    private final DateTime dateTime;

    public SinglePoint(int memberAge, Gender gender, int retirementYear, double memberAnnuity, double spouseAnnuity, int spouseAge, Gender spouseGender, DateTime dateTime) {
        this.memberAge = memberAge;
        this.gender = gender;
        this.retirementYear = retirementYear;
        this.memberAnnuity = memberAnnuity;
        this.spouseAnnuity = spouseAnnuity;
        this.spouseAge = spouseAge;
        this.spouseGender = spouseGender;
        this.dateTime = dateTime;
    }

    public ClaimCashflowPacket indexClaimInPeriod(IMortalityTable maleSurvivalRates, IMortalityTable femaleSurvivalRates, PeriodScope periodScope) {
        DateTime currentPeriodStart = periodScope.getCurrentPeriodStartDate();
        int currentYear = currentPeriodStart.get(DateTimeFieldType.year());
        if (retirementYear <= currentYear) {
            double memberSurvival = gender.survivalProbability(maleSurvivalRates, femaleSurvivalRates, memberAge, currentYear);
            double spouseSurvival = spouseGender.survivalProbability(maleSurvivalRates, femaleSurvivalRates, spouseAge, currentYear);

            double memberContribution = memberAnnuity * memberSurvival;
            double spouseContribution = spouseAnnuity * (1 - memberSurvival) * spouseSurvival;

            double claim = spouseContribution + memberContribution;
            ClaimCashflowPacket claimCashflowPacket = new ClaimCashflowPacket(this, claim, 0, claim, claim, 0d, 0d, 0d, null, currentPeriodStart, periodScope.getPeriodCounter());
            return claimCashflowPacket;
        }
        return new ClaimCashflowPacket(this, 0, 0d, 0d, 0d, 0d, 0d, 0d, null, currentPeriodStart, periodScope.getPeriodCounter());
    }

    public double getUltimate() {
        return memberAnnuity;
    }

    public boolean hasEvent() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public EventPacket getEvent() {
        return null;
    }

    public ClaimType getClaimType() {
        return ClaimType.SINGLE;
    }

    public DateTime getExposureStartDate() {
        return dateTime;
    }

    public DateTime getOccurrenceDate() {
        return dateTime;
    }

    public Integer getOccurrencePeriod(IPeriodCounter periodCounter) {
        return 0;
    }

    public boolean occurrenceInCurrentPeriod(PeriodScope periodScope) {
        if(periodScope.getCurrentPeriod() == 0) {
            return true;
        }
        return false;
    }

    public Integer getInceptionPeriod(IPeriodCounter periodCounter) {
        throw new SimulationException("");
    }

    public boolean hasSynchronizedPatterns() {
        throw new SimulationException("");
    }

    public boolean hasTrivialPayout() {
        throw new SimulationException("");
    }

    public boolean hasIBNR() {
        throw new SimulationException("");
    }

    public IClaimRoot withScale(double scaleFactor) {
        throw new SimulationException("");
    }

    @Override
    public String toString() {
        return "SinglePoint{" +
                "memberAge=" + memberAge +
                ", gender=" + gender +
                ", retirementYear=" + retirementYear +
                ", memberAnnuity=" + memberAnnuity +
                ", spouseAnnuity=" + spouseAnnuity +
                ", spouseAge=" + spouseAge +
                ", spouseGender=" + spouseGender +
                '}';
    }
}
