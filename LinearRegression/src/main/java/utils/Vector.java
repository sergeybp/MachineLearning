package utils;

/**
 * Created by nikita on 23.09.16.
 */
public class Vector implements Cloneable {

    private double[] components;

    public double[] getComponents() {
        return components.clone();
    }

    public void set(int i, double value) {
        components[i] = value;
    }

    public Vector(Vector that) {
        this.components = new double[that.getDimensions()];
        for (int i = 0; i < getDimensions(); i++)
            this.components[i] = that.get(i);
    }

    public Vector(double... components) {
        if (components == null)
            throw new IllegalArgumentException("Vector components cannot be null.");
        this.components = components;
    }

    public int getDimensions() {
        return components.length;
    }

    public double get(int component) {
        return components[component];
    }

    public Vector add(Vector that) {
        double[] newComponents = new double[getDimensions()];
        for (int i = 0; i < newComponents.length; ++i) {
            newComponents[i] = get(i) + that.get(i);
        }
        return new Vector(newComponents);
    }

    public Vector subtract(Vector that) {
        return add(that.multiply(-1));
    }

    public Vector multiply(double a) {
        double[] newComponents = new double[getDimensions()];
        for (int i = 0; i < newComponents.length; ++i) {
            newComponents[i] = get(i) * a;
        }
        return new Vector(newComponents);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Vector result = (Vector) super.clone();
        result.components = result.components.clone();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }
        Vector v = (Vector) obj;
        if (getDimensions() != v.getDimensions()) return false;
        for (int i = 0; i < getDimensions(); ++i) {
            if (get(i) != v.get(i))
                return false;
        }
        return true;
    }

    /**
     * Calculates scalar product with another vector.
     *
     * @param v Another vector.
     * @return A number -- (this, v), which is scalar product.
     */
    public double product(Vector v) {
        if (getDimensions() != v.getDimensions())
            throw new IllegalArgumentException();
        double result = 0;
        for (int i = 0; i < getDimensions(); ++i) {
            result += get(i) * v.get(i);
        }
        return result;
    }

    /**
     * Checks if all the components of this and another vector differ not more than by epsilon.
     *
     * @param that    A vector to compare with;
     * @param epsilon Permitted difference.
     * @return For all i: |this[i] - that[i]| <= epsilon
     */
    public boolean equals(Vector that, double epsilon) {
        if (getDimensions() != that.getDimensions())
            return false;
        for (int i = 0; i < getDimensions(); ++i) {
            if (!(Math.abs(get(i) - that.get(i)) < epsilon))
                return false;
        }
        return true;
    }

    public boolean hasNaN() {
        for (int i = 0; i < getDimensions(); ++i) {
            if (get(i) == Double.NaN) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int factor = 1;
        for (int i = 0; i < getDimensions(); ++i) {
            hash += Double.hashCode(get(i)) * factor;
            factor *= 31;
        }
        return hash;
    }

    /**
     * Calculates the Euclidean norm of vector.
     *
     * @return |a| = sqrt(Sum {1..n} a_i^2)
     */
    public double norm() {
        return Vector.distance(this, Vector.zero(getDimensions()));
    }

    /**
     * Calculates the distance between two vectors, |a - b|.
     *
     * @return |a - b| = sqrt(Sum {1..n} (b_i - a_i)^2)
     */
    public static double distance(Vector a, Vector b) {
        if (a.getDimensions() != b.getDimensions()) {
            throw new IllegalArgumentException("a and b should be of the same dimension.");
        }
        double quadsSum = 0.;
        for (int i = 0; i < a.getDimensions(); ++i) {
            double componentDiff = b.get(i) - a.get(i);
            quadsSum += componentDiff * componentDiff;
        }
        return Math.sqrt(quadsSum);
    }

    /**
     * Creates an n-dimensional zero vector
     *
     * @param dimensions n - number of components
     * @return (0, 0, ..., 0) { n components }
     */
    public static Vector zero(int dimensions) {
        return new Vector(new double[dimensions]);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("(");
        for (int i = 0; i < getDimensions(); ++i) {
            result.append(get(i));
            if (i != getDimensions() - 1)
                result.append(", ");
        }
        result.append(")");
        return result.toString();
    }
}