package utils;

import classifier.Classes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nikita on 16.09.16.
 */
public class DataInstance {
    public ArrayList<String> title;
    public ArrayList<String> body;
    public ArrayList<String> all;
    public Classes clazz;
    public Path file;

    public DataInstance(ArrayList<String> title, ArrayList<String> body, Classes clazz) {
        this.title = title;
        this.body = body;
        this.clazz = clazz;
        this.all = new ArrayList<>(Stream.concat(title.stream(), body.stream()).collect(Collectors.toList()));
    }

    public DataInstance(ArrayList<String> title, ArrayList<String> body, Classes clazz, Path file) {
        this.title = title;
        this.body = body;
        this.clazz = clazz;
        this.file = file;
        this.all = new ArrayList<>(Stream.concat(title.stream(), body.stream()).collect(Collectors.toList()));
    }
}
