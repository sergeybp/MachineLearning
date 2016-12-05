package net;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nikita on 05.12.16.
 */
public class Data {
    public ArrayList<DataInstance> instances;

    public Data() {
        this.instances = new ArrayList<>();
    }

    public Data(ArrayList<DataInstance> instances) {
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
}
