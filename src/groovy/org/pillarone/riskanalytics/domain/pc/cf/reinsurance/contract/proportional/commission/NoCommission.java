package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.proportional.commission;

import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.CededUnderwritingInfoPacket;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public class NoCommission implements ICommission {

    public NoCommission() {
    }

    public void calculateCommission(List<ClaimCashflowPacket> grossClaims, List<CededUnderwritingInfoPacket> cededUnderwritingInfos,
                                    boolean isAdditive, Integer occurrencePeriod) {
    }
}
