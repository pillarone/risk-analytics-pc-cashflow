package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.paidImpl;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.claim.IClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.ContractCoverBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.IncurredClaimBase;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.PeriodLayerParameters;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.GRIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities.RIUtilities;
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.incurredImpl.TermIncurredCalculation;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sparten
 * Date: 31.08.12
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class AllocatePaidEqualIncurredByClaim {

    public List<ClaimCashflowPacket> allocatePaid(double paidInPeriod, List<ClaimCashflowPacket> allCashflows,
                                                  List<IClaimRoot> allCededClaims, PeriodScope periodScope,
                                                  double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase) {

        Map<Integer, Double> cededIncurredByPeriod = cededIncurredsByPeriods(allCashflows, periodScope, termExcess, termLimit, layerParameters, coverageBase);

        TermPaidRespectIncurredByClaim iRiPaidCalculation = new TermPaidRespectIncurredByClaim();

        for (int modelPeriod = 0; modelPeriod < periodScope.getCurrentPeriod(); modelPeriod++) {
            double cededAmountInModelPeriod = cededIncurredByPeriod.get(modelPeriod);
            IPeriodCounter counter = periodScope.getPeriodCounter();
            DateTime startOfPriorPeriod = counter.startOfPeriod(periodScope.getCurrentPeriod() - 1);
            Map<Integer, Double> paidUpToPriorPeriodStartDateForModelPeriod = iRiPaidCalculation.cededAmountByCashflowsInPeriodsOccuringBeforeDate(periodScope, allCashflows, layerParameters,
                    startOfPriorPeriod, ContractCoverBase.LOSSES_OCCURING, periodScope.getCurrentPeriod() - 1, periodScope.getCurrentPeriod());

            Map<Integer, Double> paidToDateForThisPeriod = iRiPaidCalculation.cededAmountByCashflowsInPeriodsOccuringBeforeDate(periodScope, allCashflows, layerParameters,
                    periodScope.getCurrentPeriodStartDate(), ContractCoverBase.LOSSES_OCCURING, periodScope.getCurrentPeriod() - 1, periodScope.getCurrentPeriod());


/*            double paidToThisPeriodRespectTerm = Math.min(cededAmountInModelPeriod, paidToDateForThisPeriod);
            double paidPriorStartDateRespectTerm = Math.min(cededAmountInModelPeriod, paidUpToPriorPeriodStartDateForModelPeriod);
            double paidAmountAllocatedToPeriod = paidToThisPeriodRespectTerm - paidPriorStartDateRespectTerm;

            Set<IClaimRoot> cededClaimsInPeriod = RIUtilities.incurredClaimsByDate(counter.startOfPeriod(modelPeriod), counter.startOfPeriod(modelPeriod + 1), new HashSet<IClaimRoot>(allCededClaims), coverageBase);
            List<ClaimCashflowPacket> cashflowsForModelPeriod = GRIUtilities.cashflowsRelatedToRoots(cededClaimsInPeriod, allCashflows);
            List<ClaimCashflowPacket> cashflowsForModelPeriodDateThisSimPeriod = RIUtilities.cashflowClaimsByOccurenceDate(periodScope.getCurrentPeriodStartDate(), periodScope.getNextPeriodStartDate().minusMillis(1), cashflowsForModelPeriod);

            double grossIncurredThisPeriod = GRIUtilities.ultimateSum(new ArrayList<IClaimRoot>( cededClaimsInPeriod ));
            for (IClaimRoot iClaimRoot : cededClaimsInPeriod) {
                Set<IClaimRoot> tempRootList = new HashSet<IClaimRoot>();
                tempRootList.add(iClaimRoot);
                List<ClaimCashflowPacket> cashflowsToThisRoot = GRIUtilities.cashflowsRelatedToRoots(tempRootList, cashflowsForModelPeriodDateThisSimPeriod);
                double allocatedToThisClaim = paidAmountAllocatedToPeriod * iClaimRoot.getUltimate() / grossIncurredThisPeriod;
                double paidByClaimInThisPeriod = GRIUtilities.incrementalCashflowSum(cashflowsToThisRoot);
                for (ClaimCashflowPacket claimCashflowPacket : cashflowsToThisRoot) {
                    double packetAllocation = allocatedToThisClaim  *  claimCashflowPacket.getPaidIncrementalIndexed() / paidByClaimInThisPeriod;
//                    ClaimCashflowPacket paymentAllocated = new ClaimCashflowPacket(claimCashflowPacket, packetAllocation, );
                }


            }
*/

        }





            return new ArrayList<ClaimCashflowPacket>();
    }


    private Map<Integer, Double> cededIncurredsByPeriods(List<ClaimCashflowPacket> allCashflows, PeriodScope periodScope,
                                                         double termExcess, double termLimit, PeriodLayerParameters layerParameters, ContractCoverBase coverageBase) {
        TermIncurredCalculation iRiIncurredCalculation = new TermIncurredCalculation();
        Map<Integer, Double> incurredCededAmountByPeriod = new TreeMap<Integer, Double>();
        for (int contractPeriod = 0; contractPeriod <= periodScope.getCurrentPeriod(); contractPeriod++) {
            List<IClaimRoot> incurredClaims = new ArrayList<IClaimRoot>(RIUtilities.incurredClaims(allCashflows, IncurredClaimBase.KEY));
            double incurredInPeriod = iRiIncurredCalculation.cededIncurredToPeriod(incurredClaims, layerParameters, periodScope, termExcess, termLimit, coverageBase, contractPeriod);
            incurredCededAmountByPeriod.put(contractPeriod, incurredInPeriod);
        }

        return incurredCededAmountByPeriod;
    }


}
