package dividers;

import javafx.util.Pair;
import utils.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by nikita on 11.10.16.
 */
public class CVDivider extends Divider {
    private ArrayList<Division> divided;

    public CVDivider(Data data, int cvSize) {
        super(data);
        divided = divide(cvSize);
    }

    public int size() {
        return divided.size();
    }

    public ArrayList<Division> divide(int cvSize) {
        Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> cv = crossValidation(data.size(), cvSize);
        ArrayList<ArrayList<Integer>> trainCV = cv.getKey();
        ArrayList<ArrayList<Integer>> testCV = cv.getValue();

        ArrayList<Division> result = new ArrayList<>();

        for (int i = 0; i < trainCV.size(); i++) {
            result.add(new Division(
                            new Data(trainCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList())),
                            new Data(testCV.get(i).stream().map(j -> data.get(j)).collect(Collectors.toList()))
                    )
            );
        }

        return result;
    }

    public static Pair<ArrayList<ArrayList<Integer>>, ArrayList<ArrayList<Integer>>> crossValidation(int dataSize, int cvSize) {
        int count = (int) Math.ceil(((double) (dataSize)) / ((double) (cvSize)));

        ArrayList<Integer> index = new ArrayList<>(IntStream.range(0, dataSize).
                                                            boxed().
                                                            collect(Collectors.toList()));
        Collections.shuffle(index);

        ArrayList<ArrayList<Integer>> trainIndies = new ArrayList<>();
        ArrayList<ArrayList<Integer>> testIndices = new ArrayList<>();

        for (int i = 0; i < dataSize; i += count) {
            ArrayList<Integer> tmpTrain = new ArrayList<>();
            ArrayList<Integer> tmpTest = new ArrayList<>();

            tmpTrain.addAll(index.subList(0, i));
            if (i + count < dataSize) {
                tmpTrain.addAll(index.subList(i + count, index.size()));
                tmpTest.addAll(index.subList(i, i + count));
            } else
                tmpTest.addAll(index.subList(i, index.size()));

            trainIndies.add(tmpTrain);
            testIndices.add(tmpTest);
        }
        return new Pair<>(trainIndies, testIndices);
    }


    @Override
    public Iterator<Division> iterator() {
        return divided.iterator();
    }

    @Override
    public void forEach(Consumer<? super Division> action) {
        divided.forEach(action);
    }

    @Override
    public Spliterator<Division> spliterator() {
        return divided.spliterator();
    }
}
