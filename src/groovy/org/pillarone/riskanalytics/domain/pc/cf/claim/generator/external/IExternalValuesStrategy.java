package org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimRoot;
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.ExposureBase;
import org.pillarone.riskanalytics.domain.pc.cf.exposure.UnderwritingInfoPacket;
import org.pillarone.riskanalytics.domain.pc.cf.indexing.Factors;
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator;

import java.util.List;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IExternalValuesStrategy extends IParameterObject {

    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
                                          PeriodScope periodScope, ClaimType claimType, ExposureBase claimsSizeBase,
                                          IRandomNumberGenerator dateGenerator, int iteration);
}
