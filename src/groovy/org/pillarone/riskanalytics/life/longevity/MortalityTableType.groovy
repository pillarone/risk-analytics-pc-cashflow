package org.pillarone.riskanalytics.life.longevity

import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.cf.indexing.IIndexMarker
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class MortalityTableType extends AbstractParameterObjectClassifier {


    public static final MortalityTableType IDENTITY = new MortalityTableType("identity", "IDENTITY", [:])
    public static final MortalityTableType REFERENCE = new MortalityTableType("reference", "REFERENCE", [
        startYear: 2013d,
        startTable: new ConstrainedString(IMortalityTableMarker, '')
    ])
    public static final MortalityTableType REFERENCEINDEX = new MortalityTableType("referenceindex", "REFERENCEINDEX", [
        startYear: 2013d,
        startTable: new ConstrainedString(IMortalityTableMarker, ''),
        index: new ConstrainedString(IIndexMarker, '')
    ])

    public static final all = [IDENTITY, REFERENCE, REFERENCEINDEX]

    protected static Map types = [:]
    static {
        MortalityTableType.all.each {
            MortalityTableType.types[it.toString()] = it
        }
    }

    private MortalityTableType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static MortalityTableType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return MortalityTableType.getStrategy(this, parameters)
    }

    static IMortalityTable getStrategy(MortalityTableType type, Map parameters) {
        switch (type) {
            case IDENTITY:
                return new IdentityTableStrategy()
            case REFERENCE:
                return new SingleMortalityTableTransformStrategy(
                    (ConstrainedString) parameters.get('startTable'),
                    (Double) parameters.get('startYear'))
            case REFERENCEINDEX:
                return new FixedMortalityTableStrategy(
                    (ConstrainedString) parameters.get('startTable'),
                    (Double) parameters.get('startYear'),
                    (ConstrainedString) parameters.get('index'))
            default:
                throw new NotImplementedException("$type is not implemented")
        }
    }
}