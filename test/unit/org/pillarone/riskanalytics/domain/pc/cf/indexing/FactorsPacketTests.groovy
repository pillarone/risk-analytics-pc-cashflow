package org.pillarone.riskanalytics.domain.pc.cf.indexing

import org.joda.time.DateTime

/**
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class FactorsPacketTests extends GroovyTestCase {

    public static final Double EPSILON = 1E-6

    DateTime date20000101 = new DateTime(2000,1,1,0,0,0,0)
    DateTime date20010101 = new DateTime(2001,1,1,0,0,0,0)
    DateTime date20020101 = new DateTime(2002,1,1,0,0,0,0)
    DateTime date20030222 = new DateTime(2003,2,22,0,0,0,0)
    DateTime date20030316 = new DateTime(2003,3,16,0,0,0,0)
    DateTime date20041212 = new DateTime(2004,12,12,0,0,0,0)
    DateTime date20050421 = new DateTime(2005,4,21,0,0,0,0)
    DateTime date20060405 = new DateTime(2006,4,5,0,0,0,0)
    DateTime date20071226 = new DateTime(2007,12,26,0,0,0,0)
    DateTime date20081013 = new DateTime(2008,10,13,0,0,0,0)
    DateTime date20090427 = new DateTime(2009,4,27,0,0,0,0)
    DateTime date20100101 = new DateTime(2010,1,1,0,0,0,0)
    DateTime date20110101 = new DateTime(2011,1,1,0,0,0,0)

    FactorsPacket factors

    void setUp() {
        factors = new FactorsPacket()
        factors.add(date20010101, 100)
        factors.add(date20020101, 103)
        factors.add(date20030222, 104.51)
        factors.add(date20030316, 104.57)
        factors.add(date20041212, 108.02)
        factors.add(date20050421, 109.04)
        factors.add(date20060405, 110.65)
        factors.add(date20071226, 111.53)
        factors.add(date20081013, 113.83)
        factors.add(date20090427, 114.73)
        factors.add(date20100101, 115.76)
    }

    void testGetFactorInterpolated() {
        assertEquals "no interpolation required for 1.1.2001", 100, factors.getFactorInterpolated(date20010101)
        assertEquals "no interpolation required for 1.1.2001 (diff instance)", 100, factors.getFactorInterpolated(new DateTime(date20010101))
        assertEquals "no interpolation required for 1.1.2002", 103, factors.getFactorInterpolated(date20020101)
        assertEquals "interpolation required for 1.7.2001", 101.4765881, factors.getFactorInterpolated(new DateTime(2001,7,1,0,0,0,0)), EPSILON
        assertEquals "interpolation required for 1.1.2003", 104.3205011, factors.getFactorInterpolated(new DateTime(2003,1,1,0,0,0,0)), EPSILON
        assertEquals "no interpolation required for 22.2.2003", 104.51, factors.getFactorInterpolated(new DateTime(2003,2,22,0,0,0,0)), EPSILON
    }

    void testGetFactorAtDate() {
        assertEquals "no interpolation required for 1.1.2001", 100, factors.getFactorAtDate(date20010101)
        assertEquals "no interpolation required for 1.1.2001 (diff instance)", 100, factors.getFactorAtDate(new DateTime(date20010101))
        assertEquals "no interpolation required for 1.1.2002", 103, factors.getFactorAtDate(date20020101)
        assertEquals "default value for 1.7.2001", 1, factors.getFactorAtDate(new DateTime(2001,7,1,0,0,0,0))
        assertEquals "default value for 1.1.2003", 1, factors.getFactorAtDate(new DateTime(2003,1,1,0,0,0,0))
        assertEquals "no interpolation required for 22.2.2003", 104.51, factors.getFactorAtDate(new DateTime(2003,2,22,0,0,0,0)), EPSILON
    }

    void testGetFactorCeiling() {
        assertEquals "no interpolation required for 1.1.2001", 100, factors.getFactorCeiling(date20010101)
        assertEquals "no interpolation required for 1.1.2001 (diff instance)", 100, factors.getFactorCeiling(new DateTime(date20010101))
        assertEquals "no interpolation required for 1.1.2002", 103, factors.getFactorCeiling(date20020101)
        assertEquals "1.1.2002 for 1.7.2001", 103, factors.getFactorCeiling(new DateTime(2001,7,1,0,0,0,0)), EPSILON
        assertEquals "22.2.2003 for 1.1.2003", 104.51, factors.getFactorCeiling(new DateTime(2003,1,1,0,0,0,0)), EPSILON
        assertEquals "no interpolation required for 22.2.2003", 104.51, factors.getFactorCeiling(new DateTime(2003,2,22,0,0,0,0)), EPSILON
    }

    void testGetFactorFloor() {
        assertEquals "no interpolation required for 1.1.2001", 100, factors.getFactorFloor(date20010101)
        assertEquals "no interpolation required for 1.1.2001 (diff instance)", 100, factors.getFactorFloor(new DateTime(date20010101))
        assertEquals "no interpolation required for 1.1.2002", 103, factors.getFactorFloor(date20020101)
        assertEquals "1.1.2001 for 1.7.2001", 100, factors.getFactorFloor(new DateTime(2001,7,1,0,0,0,0)), EPSILON
        assertEquals "1.1.2002 for 1.1.2003", 103, factors.getFactorFloor(new DateTime(2003,1,1,0,0,0,0)), EPSILON
        assertEquals "no interpolation required for 22.2.2003", 104.51, factors.getFactorFloor(new DateTime(2003,2,22,0,0,0,0)), EPSILON
    }

    void testBelowDefinition() {
        assertEquals "first factor, previous", 100, factors.getFactorFloor(date20000101)
        assertEquals "first factor, next", 100, factors.getFactorCeiling(date20000101)
    }

    void testAboveDefinition() {
        assertEquals "first factor, previous", 115.76, factors.getFactorFloor(date20110101)
        assertEquals "first factor, next", 115.76, factors.getFactorCeiling(date20110101)
    }
}
