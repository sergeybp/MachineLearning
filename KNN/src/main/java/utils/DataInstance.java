package utils;

/**
 * Created by nikita on 16.09.16.
 */
public class DataInstance {
    public PointND point;
    public Integer clazz;

    public DataInstance(PointND point, Integer clazz) {
        this.point = point;
        this.clazz = clazz;
    }

    public PointND getPoint() {
        return point;
    }

    public void setPoint(PointND point) {
        this.point = point;
    }

    public Integer getClazz() {
        return clazz;
    }

    public void setClazz(Integer clazz) {
        this.clazz = clazz;
    }
}
