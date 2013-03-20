package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover;

import org.pillarone.riskanalytics.domain.utils.marker.ILegalEntityMarker;

import java.util.List;

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface ILegalEntityCover {
    List<ILegalEntityMarker> getCoveredLegalEntities();
}
