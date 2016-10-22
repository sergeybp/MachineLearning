package utils;

import classifier.Classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Created by nikita on 08.10.16.
 */
public class Utils {

    public final static int SCALE = 500;

    public static ArrayList<Data> getDataFromFile(Path path) {
        ArrayList<Data> data = new ArrayList<>();
        try {
            Files.walkFileTree(path, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    data.add(new Data(dir));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    Classes clazz = file.toString().contains("spmsg") ? Classes.SPAM : Classes.HAM;

                    try (BufferedReader br = Files.newBufferedReader(file)) {
                        String subject = br.readLine();

                        ArrayList<String> title = new ArrayList<>(
                                Arrays.asList(subject.substring("Subject: ".length()).split("\\s")).
                                        stream().
                                        filter(s -> !s.isEmpty()).
                                        collect(Collectors.toList()));

                        String line;
                        ArrayList<String> body = new ArrayList<>();
                        while ((line = br.readLine()) != null) {
                            StringTokenizer st = new StringTokenizer(line);
                            while (st.hasMoreTokens()) {
                                body.add(st.nextToken());
                            }
                        }

                        data.get(data.size() - 1).add(title, body, clazz, file);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    throw exc;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.remove(0);
        return data;
    }

}
