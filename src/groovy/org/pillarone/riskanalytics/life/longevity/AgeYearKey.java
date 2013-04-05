package org.pillarone.riskanalytics.life.longevity;

/**
 * author simon.parten @ art-allianz . com
 */
public class AgeYearKey {

    final double age;
    final double year;

    public AgeYearKey(double age, double year) {
        this.age = age;
        this.year = year;
    }

    public double getAge() {
        return age;
    }

    public double getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AgeYearKey that = (AgeYearKey) o;

        if (Double.compare(that.age, age) != 0) return false;
        if (Double.compare(that.year, year) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = age != +0.0d ? Double.doubleToLongBits(age) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = year != +0.0d ? Double.doubleToLongBits(year) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "AgeYearKey{" +
                "age=" + age +
                ", year=" + year +
                '}';
    }
}
