package utils;

import classifier.Classes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Created by nikita on 13.09.16.
 */
public class Data implements Iterable<DataInstance> {
    public static final int CLASS_NUMBER = 2;

    public ArrayList<DataInstance> instances;
    public Path dir;

    public Data() {
        this.instances = new ArrayList<>();
    }

    public Data(List<DataInstance> instances) {
        this.instances = new ArrayList<>(instances);
    }

    public Data(Path dir) {
        this.dir = dir;
        this.instances = new ArrayList<>();
    }

    public void add(DataInstance instance) {
        instances.add(instance);
    }

    public void add(ArrayList<String> title, ArrayList<String> body, Classes clazz) {
        instances.add(new DataInstance(title, body, clazz));
    }

    public void add(ArrayList<String> title, ArrayList<String> body, Classes clazz, Path file) {
        instances.add(new DataInstance(title, body, clazz, file));
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
