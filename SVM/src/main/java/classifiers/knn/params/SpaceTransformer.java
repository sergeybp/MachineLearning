package classifiers.knn.params;

import utils.Data;

import java.util.function.Function;

/**
 * Created by nikita on 18.09.16.
 */
public class SpaceTransformer {
    public Function<Data, Data> to, from;

    public SpaceTransformer(Function<Data, Data> to, Function<Data, Data> from) {
        this.to = to;
        this.from = from;
    }
}
