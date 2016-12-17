package net;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by nikita on 05.12.16.
 */
public class Data implements Iterable<DataInstance> {
    public List<DataInstance> instances;

    public Data() {
        this.instances = new ArrayList<>();
    }

    public Data(ArrayList<Feature> features, ArrayList<Label> labels) {
        this.instances = new ArrayList<>(
                IntStream.range(0, features.size()).
                boxed().
                map(i -> new DataInstance(features.get(i), labels.get(i))).
                collect(Collectors.toList()));
    }

    public Data(List<DataInstance> instances) {
        this.instances = instances;
    }

    public void add(DataInstance instance) {
        this.instances.add(instance);
    }

    public void add(Feature feature, Label label) {
        add(new DataInstance(feature, label));
    }

    public int size() {
        return instances.size();
    }

    public void shuffle() {
        Collections.shuffle(instances);
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
