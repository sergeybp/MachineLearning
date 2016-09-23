package utils;

import java.util.ArrayList;

/**
 * Created by nikita on 23.09.16.
 */

public class Matrix {

    private double[][] components;

    public double[][] getComponents() {
        double[][] result = new double[getHeight()][];
        for (int i = 0; i < result.length; ++i) {
            result[i] = components[i].clone();
        }
        return result;
    }

    public Matrix(double[]... components) {
        if (components == null || components.length == 0)
            throw new IllegalArgumentException("Matrix components should be non-null and have non-zero length.");
        int width = components[0].length;
        for (double[] component : components) {
            if (component.length != width)
                throw new IllegalArgumentException("All of the matrix rows should have the same length.");
        }
        this.components = components;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix)) {
            return false;
        }
        Matrix m = (Matrix) obj;
        if (getHeight() != m.getHeight() || getWidth() != m.getWidth()) {
            return false;
        }
        for (int i = 0; i < getHeight(); ++i) {
            for (int j = 0; j < getWidth(); ++j) {
                if (get(i, j) != m.get(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if all the components of this and another matrix differ not more than by epsilon.
     *
     * @param that    A matrix to compare with.
     * @param epsilon Permitted difference.
     * @return For all i, j: |this[i, j] - that[i, j]| <= epsilon
     */
    public boolean equals(Matrix that, double epsilon) {
        if (getHeight() != that.getHeight() || getWidth() != that.getWidth()) {
            return false;
        }
        for (int i = 0; i < getHeight(); ++i) {
            for (int j = 0; j < getWidth(); ++j) {
                if (!(Math.abs(get(i, j) - that.get(i, j)) < epsilon))
                    return false;
            }
        }
        return true;
    }

    /**
     * Calculates the matrix m-norm.
     *
     * @return ||A|| = max {1..n} { Sum {1..m} |a_ij| }
     */
    public double norm() {
        double max = 0.;
        for (int i = 0; i < getHeight(); ++i) {
            double sum = 0.;
            for (int j = 0; j < getWidth(); ++j) {
                sum += Math.abs(get(i, j));
            }
            if (sum > max) {
                max = sum;
            }
        }
        return max;
    }

    /**
     * Calculates the matrix conditionality value.
     *
     * @return Cond A = ||A|| * ||A^-1||
     */
    public double conditionality() {
        return norm() * reversed().norm();
    }

    /**
     * Returns an item at i-th row, j-th column
     *
     * @param i Row
     * @param j Column
     * @return Matrix item at i-th row, j-th column
     */
    public double get(int i, int j) {
        return components[i][j];
    }

    public int getWidth() {
        return components[0].length;
    }

    public int getHeight() {
        return components.length;
    }

    /**
     * Adds this matrix to another one.
     *
     * @param m Matrix to add this to.
     * @return A = this + m
     */
    public Matrix add(Matrix m) {
        if (getHeight() != m.getHeight() || getWidth() != m.getWidth())
            throw new IllegalArgumentException("Matrix should have the same size to be added.");
        double[][] newComponents = new double[getWidth()][];
        for (int i = 0; i < getHeight(); ++i) {
            newComponents[i] = new double[getWidth()];
            for (int j = 0; j < newComponents[i].length; ++j) {
                newComponents[i][j] = get(i, j) + m.get(i, j);
            }
        }
        return new Matrix(newComponents);
    }

    public Matrix subtract(Matrix b) {
        return add(b.multiply(-1));
    }

    /**
     * Multiplies this matrix by another one.
     *
     * @param m Matrix to multiply this by.
     * @return A = this * m;
     */
    public Matrix multiply(Matrix m) {
        if (getWidth() != m.getHeight())
            throw new IllegalArgumentException("Matrices cannot be multiplied due to their dimensions.");
        double[][] newComponents = new double[getHeight()][];
        for (int i = 0; i < newComponents.length; ++i) {
            newComponents[i] = new double[m.getWidth()];
            for (int j = 0; j < newComponents[i].length; ++j) {
                for (int k = 0; k < getWidth(); ++k) {
                    newComponents[i][j] += get(i, k) * m.get(k, j);
                }
            }
        }
        return new Matrix(newComponents);
    }

    /**
     * Multiplies this matrix by number.
     *
     * @param a Number to multiply this matrix by.
     * @return A = this * a
     */
    public Matrix multiply(double a) {
        double[][] newComponents = new double[getHeight()][];
        for (int i = 0; i < newComponents.length; ++i) {
            newComponents[i] = new double[getWidth()];
            for (int j = 0; j < newComponents[i].length; ++j) {
                newComponents[i][j] = get(i, j) * a;
            }
        }
        return new Matrix(newComponents);
    }

    /**
     * Multiplies the matrix by vector v as Av.
     *
     * @param v Vector to multiply the matrix by.
     * @return Vector y = Av.
     */
    public Vector multiply(Vector v) {
        if (getWidth() != v.getDimensions())
            throw new IllegalArgumentException("Vector cannot be multiplied due to its dimensions.");
        double[] newComponents = new double[getHeight()];
        for (int i = 0; i < newComponents.length; ++i) {
            for (int j = 0; j < getWidth(); ++j) {
                newComponents[i] += get(i, j) * v.get(j);
            }
        }
        return new Vector(newComponents);
    }

    /**
     * Get an array of the matrix' diagonal items.
     *
     * @return a_i = A_i_i {1..n}
     */
    public double[] getDiagonal() {
        double[] result = new double[getHeight()];
        for (int i = 0; i < getHeight(); ++i) {
            result[i] = get(i, i);
        }
        return result;
    }

    /**
     * Calculates square matrix determinant.
     *
     * @return det(this)
     */
    public double determinant() {
        if (getHeight() != getWidth()) {
            throw new UnsupportedOperationException("Determinants for non-square matrices are not implemented");
        }
        if (getHeight() == 1) {
            return get(0, 0);
        }
        if (getHeight() == 2) {
            return get(0, 0) * get(1, 1) - get(1, 0) * get(0, 1);
        }
        double result = 0;
        for (int i = 0; i < getHeight(); ++i) {
            result += (i % 2 == 0 ? 1 : -1) * get(i, 0) * minor(i, 0).determinant();
        }
        return result;
    }

    public static final double REVERSE_DETERMINANT_EPSILON = 1e-18;

    /**
     * Calculates the reversed matrix A^-1, so that A*A^-1 = E.
     *
     * @return null, if this has determinant approximately equal to zero. this ^ -1 otherwise.
     */
    public Matrix reversed() {
        if (Math.abs(determinant()) < REVERSE_DETERMINANT_EPSILON)
            return null;
        double[][] newComponents = new double[getWidth()][];
        for (int i = 0; i < newComponents.length; ++i) {
            newComponents[i] = new double[getHeight()];
        }
        double det = determinant();
        for (int i = 0; i < getHeight(); ++i) {
            for (int j = 0; j < getWidth(); ++j) {
                newComponents[j][i] = ((i + j) % 2 == 0 ? 1 : -1) * minor(i, j).determinant() / det;
            }
        }
        return new Matrix(newComponents);
    }

    /**
     * Makes a transponed matrix.
     *
     * @return [a_i_j] = [this_j_i]
     */
    public Matrix transponed() {
        double[][] components = new double[getWidth()][];
        for (int i = 0; i < components.length; ++i) {
            components[i] = new double[getHeight()];
            for (int j = 0; j < components[i].length; ++j) {
                components[i][j] = get(j, i);
            }
        }
        return new Matrix(components);
    }

    /**
     * Makes a minor matrix, which is matrix without one row and one column.
     *
     * @param a Row to delete
     * @param b Column to delete
     * @return Matrix of size [n-1 x n-1]
     */
    public Matrix minor(int a, int b) {
        double[][] newComponents = new double[getHeight() - 1][];
        for (int i = 0; i < newComponents.length; ++i) {
            newComponents[i] = new double[getWidth() - 1];
            for (int j = 0; j < newComponents[i].length; ++j) {
                newComponents[i][j] = get(i < a ? i : i + 1, j < b ? j : j + 1);
            }
        }
        return new Matrix(newComponents);
    }

    /**
     * Creates an unit matrix of size [n x n].
     *
     * @param dimensions n -- size of the matrix.
     * @return A of size [n x n] with A[i][j] = "i == j"
     */
    public static Matrix unit(int dimensions) {
        double[][] components = new double[dimensions][];
        for (int i = 0; i < components.length; ++i) {
            components[i] = new double[dimensions];
            for (int j = 0; j < components[i].length; ++j) {
                components[i][j] = i == j ? 1. : 0.;
            }
        }
        return new Matrix(components);
    }

    public static Matrix zero(int dimensions) {
        return zero(dimensions, dimensions);
    }

    /**
     * Creates a matrix filled with zeros.
     *
     * @param height Created matrix height.
     * @param width  Created matrix width.
     * @return Matrix filled with zeros of size [height x width].
     */
    public static Matrix zero(int height, int width) {
        double[][] components = new double[height][];
        for (int i = 0; i < components.length; ++i) {
            components[i] = new double[width];
        }
        return new Matrix(components);
    }

    public static final int TO_STRING_DIGITS = 3;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < getHeight(); ++i) {
            for (int j = 0; j < getWidth(); ++j) {
                result.append(String.format("% " + TO_STRING_DIGITS + "." + TO_STRING_DIGITS + "f ", get(i, j)));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static Matrix byDiagonal(double[] trace) {
        double[][] components = new double[trace.length][];
        for (int i = 0; i < components.length; ++i) {
            components[i] = new double[trace.length];
            components[i][i] = trace[i];
        }
        return new Matrix(components);
    }

    public static Matrix parse(String s) {
        ArrayList<Integer> splitPoints = new ArrayList<>();
        int bracesBalance = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '{') {
                ++bracesBalance;
            } else if (s.charAt(i) == '}') {
                --bracesBalance;
            } else if (s.charAt(i) == ',' && bracesBalance == 0) {
                splitPoints.add(i);
            }
        }
        splitPoints.add(s.length());
        double[][] components = new double[splitPoints.size()][];
        for (int i = 0; i < splitPoints.size(); ++i) {
            String[] line = s.substring(i == 0 ? 0 : splitPoints.get(i - 1) + 1, splitPoints.get(i) - 1).trim()
                    .replaceAll("[\\}\\{,]", " ").split("\\s+");
            components[i] = new double[line.length - 1];
            for (int j = 1; j < line.length; ++j) {
                components[i][j - 1] = Double.parseDouble(line[j]);
            }
        }
        return new Matrix(components);
    }

    /**
     * returns a Vector-representation of specified row
     * for gauss
     *
     * @param i
     * @return
     */
    public Vector getRow(int i) {
        if (i < 0 || i >= components.length)
            throw new IllegalArgumentException("invalid row number\n");
        return new Vector(components[i].clone());
    }

    /**
     * for gauss
     *
     * @param i
     * @return
     */
    public Matrix excludeVariableForward(int i) {
        if (i < 0 || i >= components[0].length)
            throw new IllegalArgumentException("invalid row number\n");

        double[][] newComponents = getComponents();

        Vector fixedRow = getRow(i);
        Vector addition;
        Vector temp;

        for (int j = i + 1; j < components.length; ++j) {
            addition = fixedRow.multiply(-get(j, i) / get(i, i));
            temp = getRow(j).add(addition);
            newComponents[j] = temp.getComponents();
        }

        return new Matrix(newComponents);
    }

    /**
     * for gauss
     *
     * @param i
     * @return
     */
    public Matrix excludeVariableBackward(int i) {
        if (i < 0 || i >= components[0].length)
            throw new IllegalArgumentException("invalid row number\n");

        double[][] newComponents = getComponents();

        double[] last = getRow(i).getComponents();

        Vector fixed = new Vector(last);
        fixed = fixed.multiply(1 / fixed.get(i));

        Vector addition;
        Vector temp;

        for (int j = i - 1; j >= 0; --j) {
            addition = fixed.multiply(-get(j, i));
            temp = getRow(j).add(addition);
            newComponents[j] = temp.getComponents();
        }

        return new Matrix(newComponents);
    }

    /**
     * Creates extended matrix (A|b) from source matrix A and vector b
     *
     * @param a matrix
     * @param b vector
     * @return extended matrix
     */
    public static Matrix getExtended(Matrix a, Vector b) {
        double[][] components = new double[a.getHeight()][a.getWidth() + 1];
        for (int i = 0; i < a.getHeight(); ++i) {
            for (int j = 0; j < a.getWidth(); ++j)
                components[i][j] = a.get(i, j);
            components[i][components[i].length - 1] = b.get(i);
        }
        return new Matrix(components);
    }
}
