package classifiers.params;

import utils.Data;

/**
 * Created by nikita on 28.10.16.
 */
public class ConfusionMatrix {
    public int[][] matrix;
    private int P, N;

    public ConfusionMatrix(Data real, Data answer) {
        this.matrix = new int[2][2];
        for (int i = 0; i < real.size(); i++) {
            matrix[answer.get(i).clazz.index()][real.get(i).clazz.index()]++;
            if (real.get(i).clazz == Classes.FIRST) P++;
            else N++;
        }
    }

    public int TP() { return matrix[1][1]; }
    public int FN() { return matrix[0][1]; }
    public int TN() { return matrix[0][0]; }
    public int FP() { return matrix[1][0]; }
    public int P() { return this.P; }
    public int N() { return this.N; }

}
