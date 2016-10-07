package optimizers;

import params.Params;
import utils.Data;
import utils.Utils;
import utils.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by nikita on 08.10.16.
 */
public class GeneticOptimizer extends Optimizer {

    private final static double GENETIC_BOUND = 100_000d;
    private final static int MARK_NUMBER = 2;

    private final Random r = new Random();

    public GeneticOptimizer(Data data, Params params, int maxIterations) {
        super(data, params, maxIterations);
    }

    private Vector crossover(Vector mother, Vector father) {
        Vector son = new Vector(mother.getComponents());
        int n = mother.getDimensions();
        for (int i = 0; i < n; i++) {
            if (r.nextBoolean())
                son.set(i, mother.get(i));
            else son.set(i, father.get(i));
        }
        return son;
    };

    private Vector mutate(Vector a) {
        int n = a.getDimensions();
        for (int i = 0; i < n; i++)
            if (r.nextBoolean())
                a.set(i, Utils.random(-GENETIC_BOUND, GENETIC_BOUND));
        return a;
    };

    private Double fitness(Vector w) {
        return Utils.standardDeviation(data, w);
    }

    private ArrayList<Vector> init(int sizeOfPopulation, int markNumber) {
        ArrayList<Vector> populations = new ArrayList<>();
        for (int k = 0; k < sizeOfPopulation; k++) {
            double[] init = new double[markNumber + 1];
            for (int i = 0; i < markNumber + 1; i++)
                init[i] = Utils.random(-GENETIC_BOUND, GENETIC_BOUND);
            populations.add(new Vector(init));
        }
        return populations;
    }

    private ArrayList<Vector> reproduction(ArrayList<Vector> population) {
        ArrayList<Vector> newPopulation = new ArrayList<>();
        int p = population.size();

        Collections.sort(population, (o1, o2) -> fitness(o1).compareTo(fitness(o2)));

        for (int k = 0; k < p / 2; k++) {
            int m = 0;
            int f = 0;
            while (m == f) {
                m = r.nextInt(p / 2);
                f = r.nextInt(p / 2);
            }
            newPopulation.add(crossover(population.get(m), population.get(f)));
        }

        newPopulation.addAll(population.subList(0, p - newPopulation.size()));

        double averageParentFitness = population.stream().mapToDouble(this::fitness).sum() / p;
        double averageChildFitness = newPopulation.stream().mapToDouble(this::fitness).sum() / p;

        if (averageChildFitness >= averageParentFitness) {
            for (int k = 0; k < p / 2; k++) {
                newPopulation.set(k, mutate(newPopulation.get(k)));
            }
        }

        Collections.sort(population, (o1, o2) -> fitness(o1).compareTo(fitness(o2)));
        return newPopulation;
    }

    @Override
    public Vector optimize() {
        int d = MARK_NUMBER;
        int p = params.sizeOfPopulation;

        ArrayList<Vector> population = init(p, d);

        int i = 0;
        do {
            population = new ArrayList<>(reproduction(population));
            i++;
        } while (i < maxIterations);

        return population.get(0);
    }

}
