package org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.filterUtilities;


/**
 * author simon.parten @ art-allianz . com
 */
public class YearLayerIdentifier {

    private final double year;
    private final double layer;

    public YearLayerIdentifier(final double year, final double layer) {
        this.year = year;
        this.layer = layer;
    }

    public double getYear() {
        return year;
    }

    public int intYear() {
        return (int) year;
    }

    public int intLayer() {
        return (int) layer;
    }

    public double getLayer() {
        return layer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof YearLayerIdentifier)) return false;

        final YearLayerIdentifier that = (YearLayerIdentifier) o;

        if (Double.compare(that.layer, layer) != 0) return false;
        if (Double.compare(that.year, year) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(year);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(layer);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "year=" + year +
                ", layer=" + layer +"||"
                ;
    }
}
