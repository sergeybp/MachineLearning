package utils;

import classifiers.params.Classes;

/**
 * Created by nikita on 16.09.16.
 */
public class DataInstance {
    public PointND point;
    public Classes clazz;

    public DataInstance(PointND point, Classes clazz) {
        this.point = point;
        this.clazz = clazz;
    }
}
