package classifiers.knn.params;

import utils.Data;
import utils.Point;
import utils.PointND;

import java.util.function.Function;

/**
 * Created by nikita on 18.09.16.
 */
public enum SpaceTransformations {
    ID("ID"),
//  SPHERE("SPHERE"),
    PARABOLOID("PARABOLOID");

    private final String name;

    SpaceTransformations(String s) {
        this.name = s;
    }

    public String toString() {
        return this.name;
    }

    public SpaceTransformer get() {
        switch (this) {
            case ID:
                return new SpaceTransformer(Function.identity(), Function.identity());
            /*case SPHERE:
                return new SpaceTransformer(data -> {
                    Data newData = new Data();
                    data.instances.forEach(x -> {
                                double[] newPoint = new double[3];
                                Point old = (Point) x.point;
                                newPoint[0] = old.x;
                                newPoint[1] = old.y;
                                newPoint[2] = Math.sqrt(4 - old.x * old.x - old.y * old.y);
                                newData.add(new PointND(3, newPoint), x.clazz);
                            }
                    );
                    return newData;
                }, data -> {
                    Data newData = new Data();
                    data.instances.forEach(x -> newData.add(new Point(x.point.get(0), x.point.get(1)), x.clazz));
                    return newData;
                });*/
            case PARABOLOID:
                return new SpaceTransformer(data -> {
                    Data newData = new Data();
                    data.instances.forEach(x -> {
                                double[] newPoint = new double[3];
                                Point old = (Point) x.point;
                                newPoint[0] = old.x;
                                newPoint[1] = old.y;
                                newPoint[2] = 4 * old.x * old.x + 4 * old.y * old.y;
                                newData.add(new PointND(3, newPoint), x.clazz);
                            }
                    );
                    return newData;
                }, data -> {
                    Data newData = new Data();
                    data.instances.forEach(x -> newData.add(new Point(x.point.get(0), x.point.get(1)), x.clazz));
                    return newData;
                });
        }
        return new SpaceTransformer(Function.identity(), Function.identity());
    }

}
