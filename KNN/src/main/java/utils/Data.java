package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Created by nikita on 13.09.16.
 */
public class Data implements Iterable<DataInstance> {
    public ArrayList<DataInstance> instances;
    public int numberOfClasses = 2;

    public Data() {
        this.instances = new ArrayList<>();
    }

    public Data(List<DataInstance> instances) {
        this.instances = new ArrayList<>(instances);
    }

    public void add(DataInstance instance) {
        instances.add(instance);
    }

    public void add(PointND point, Integer clazz) {
        instances.add(new DataInstance(point, clazz));
    }

    public int size() {
        return instances.size();
    }

    public DataInstance get(int i) {
        return instances.get(i);
    }

    @Override
    public Iterator<DataInstance> iterator() {
        return instances.iterator();
    }

    @Override
    public void forEach(Consumer<? super DataInstance> action) {
        instances.forEach(action);
    }

    @Override
    public Spliterator<DataInstance> spliterator() {
        return instances.spliterator();
    }
}
