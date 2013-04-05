package org.pillarone.riskanalytics.life.longevity;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter;
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.life.Gender;

import java.util.Collections;
import java.util.List;

/**
 * author simon.parten @ art-allianz . com
 */
public class PolicyData {

    final List<SinglePoint> policyList;

    public PolicyData(ConstrainedMultiDimensionalParameter mdp, DateTime dateTime) {
        IMultiDimensionalConstraints con = mdp.getConstraints();
        List<SinglePoint> tempList = Lists.newArrayList();
        for (int i = 1; i <= mdp.getValueRowCount(); i++) {
            int age = ((Number) mdp.getValueAt(i, PolicyConstraints.AGE_COLUMN)).intValue();
            int spouseAge = ((Number) mdp.getValueAt(i, PolicyConstraints.SPOUSE_AGE_COLUMN)).intValue();
            int retirementYear = ((Number) mdp.getValueAt(i, PolicyConstraints.RETIREMENT_YEAR_COLUMN)).intValue();
            double spouseAnnuity = ((Number) mdp.getValueAt(i, PolicyConstraints.SPOUSE_ANNUITY_COLUMN)).doubleValue();
            double memberAnnuity = ((Number) mdp.getValueAt(i, PolicyConstraints.MEMBER_ANNUITY_COLUMN)).doubleValue();
            Gender gender = Gender.getStringValue((String) mdp.getValueAtAsObject(i, PolicyConstraints.GENDER_COLUMN));
            Gender spouseGender = Gender.getStringValue((String) mdp.getValueAtAsObject(i, PolicyConstraints.SPOUSE_GENDER_COLUMN));
            SinglePoint aPolicy = new SinglePoint(age, gender, retirementYear, memberAnnuity, spouseAnnuity, spouseAge, spouseGender, dateTime);
            tempList.add(aPolicy);
        }
        policyList = Collections.unmodifiableList(tempList);
    }

    public List<SinglePoint> getPolicyList() {
        return policyList;
    }

    public List<ClaimCashflowPacket> claimsInPeriod(IMortalityTable maleSurvivalRates, IMortalityTable femaleSurvivalRates, PeriodScope periodScope) {
        List<ClaimCashflowPacket> claims = Lists.newArrayList();
        for (SinglePoint singlePoint : policyList) {
            claims.add(singlePoint.indexClaimInPeriod(maleSurvivalRates, femaleSurvivalRates, periodScope));
        }
        return claims;
    }
}