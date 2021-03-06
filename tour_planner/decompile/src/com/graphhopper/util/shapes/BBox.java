/*
 * Decompiled with CFR 0_121.
 */
package com.graphhopper.util.shapes;

import com.graphhopper.util.Helper;
import com.graphhopper.util.NumHelper;
import com.graphhopper.util.shapes.Circle;
import com.graphhopper.util.shapes.Shape;
import java.util.ArrayList;
import java.util.List;

public class BBox
implements Shape,
Cloneable {
    public double minLon;
    public double maxLon;
    public double minLat;
    public double maxLat;
    public double minEle;
    public double maxEle;
    private final boolean elevation;

    public BBox(double minLon, double maxLon, double minLat, double maxLat) {
        this(minLon, maxLon, minLat, maxLat, Double.NaN, Double.NaN, false);
    }

    public BBox(double minLon, double maxLon, double minLat, double maxLat, double minEle, double maxEle) {
        this(minLon, maxLon, minLat, maxLat, minEle, maxEle, true);
    }

    public BBox(double minLon, double maxLon, double minLat, double maxLat, double minEle, double maxEle, boolean elevation) {
        this.elevation = elevation;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.minLat = minLat;
        this.maxLon = maxLon;
        this.minEle = minEle;
        this.maxEle = maxEle;
    }

    public boolean hasElevation() {
        return this.elevation;
    }

    public static BBox createInverse(boolean elevation) {
        if (elevation) {
            return new BBox(Double.MAX_VALUE, -1.7976931348623157E308, Double.MAX_VALUE, -1.7976931348623157E308, Double.MAX_VALUE, -1.7976931348623157E308, true);
        }
        return new BBox(Double.MAX_VALUE, -1.7976931348623157E308, Double.MAX_VALUE, -1.7976931348623157E308, Double.NaN, Double.NaN, false);
    }

    public void update(double lat, double lon) {
        if (lat > this.maxLat) {
            this.maxLat = lat;
        }
        if (lat < this.minLat) {
            this.minLat = lat;
        }
        if (lon > this.maxLon) {
            this.maxLon = lon;
        }
        if (lon < this.minLon) {
            this.minLon = lon;
        }
    }

    public void update(double lat, double lon, double elev) {
        if (this.elevation) {
            if (elev > this.maxEle) {
                this.maxEle = elev;
            }
            if (elev < this.minEle) {
                this.minEle = elev;
            }
        } else {
            throw new IllegalStateException("No BBox with elevation to update");
        }
        this.update(lat, lon);
    }

    public BBox clone() {
        return new BBox(this.minLon, this.maxLon, this.minLat, this.maxLat, this.minEle, this.maxEle, this.elevation);
    }

    @Override
    public boolean intersect(Shape s) {
        if (s instanceof BBox) {
            return this.intersect((BBox)s);
        }
        if (s instanceof Circle) {
            return ((Circle)s).intersect(this);
        }
        throw new UnsupportedOperationException("unsupported shape");
    }

    @Override
    public boolean contains(Shape s) {
        if (s instanceof BBox) {
            return this.contains((BBox)s);
        }
        if (s instanceof Circle) {
            return this.contains((Circle)s);
        }
        throw new UnsupportedOperationException("unsupported shape");
    }

    public boolean intersect(Circle s) {
        return s.intersect(this);
    }

    public boolean intersect(BBox o) {
        return this.minLon < o.maxLon && this.minLat < o.maxLat && o.minLon < this.maxLon && o.minLat < this.maxLat;
    }

    @Override
    public boolean contains(double lat, double lon) {
        return lat < this.maxLat && lat >= this.minLat && lon < this.maxLon && lon >= this.minLon;
    }

    public boolean contains(BBox b) {
        return this.maxLat >= b.maxLat && this.minLat <= b.minLat && this.maxLon >= b.maxLon && this.minLon <= b.minLon;
    }

    public boolean contains(Circle c) {
        return this.contains(c.getBounds());
    }

    public String toString() {
        String str = "" + this.minLon + "," + this.maxLon + "," + this.minLat + "," + this.maxLat;
        if (this.elevation) {
            str = str + "," + this.minEle + "," + this.maxEle;
        }
        return str;
    }

    public String toLessPrecisionString() {
        return "" + (float)this.minLon + "," + (float)this.maxLon + "," + (float)this.minLat + "," + (float)this.maxLat;
    }

    @Override
    public BBox getBounds() {
        return this;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        BBox b = (BBox)obj;
        return NumHelper.equalsEps(this.minLat, b.minLat) && NumHelper.equalsEps(this.maxLat, b.maxLat) && NumHelper.equalsEps(this.minLon, b.minLon) && NumHelper.equalsEps(this.maxLon, b.maxLon);
    }

    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int)(Double.doubleToLongBits(this.minLon) ^ Double.doubleToLongBits(this.minLon) >>> 32);
        hash = 17 * hash + (int)(Double.doubleToLongBits(this.maxLon) ^ Double.doubleToLongBits(this.maxLon) >>> 32);
        hash = 17 * hash + (int)(Double.doubleToLongBits(this.minLat) ^ Double.doubleToLongBits(this.minLat) >>> 32);
        hash = 17 * hash + (int)(Double.doubleToLongBits(this.maxLat) ^ Double.doubleToLongBits(this.maxLat) >>> 32);
        return hash;
    }

    public boolean isValid() {
        if (this.minLon >= this.maxLon) {
            return false;
        }
        if (this.minLat >= this.maxLat) {
            return false;
        }
        if (this.elevation) {
            if (this.minEle > this.maxEle) {
                return false;
            }
            if (Double.compare(this.maxEle, -1.7976931348623157E308) == 0 || Double.compare(this.minEle, Double.MAX_VALUE) == 0) {
                return false;
            }
        }
        return Double.compare(this.maxLat, -1.7976931348623157E308) != 0 && Double.compare(this.minLat, Double.MAX_VALUE) != 0 && Double.compare(this.maxLon, -1.7976931348623157E308) != 0 && Double.compare(this.minLon, Double.MAX_VALUE) != 0;
    }

    public List<Double> toGeoJson() {
        ArrayList<Double> list = new ArrayList<Double>(4);
        list.add(Helper.round6(this.minLon));
        list.add(Helper.round6(this.minLat));
        if (this.elevation) {
            list.add(Helper.round2(this.minEle));
        }
        list.add(Helper.round6(this.maxLon));
        list.add(Helper.round6(this.maxLat));
        if (this.elevation) {
            list.add(Helper.round2(this.maxEle));
        }
        return list;
    }
}

