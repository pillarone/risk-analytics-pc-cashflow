package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.limit;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.domain.pc.cf.claim.BasedOnClaimProperty;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimCashflowPacket;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ILimitStrategy extends IParameterObject {
    double getAAD();
    double getAAL();
    double appliedLimit(ClaimCashflowPacket claim, BasedOnClaimProperty claimProperty);
    double appliedLimit(double value);
}
