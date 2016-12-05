package net;

/**
 * Created by nikita on 03.12.16.
 */
public class Label implements Comparable<Label> {
    public int label;

    public Label(int label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "" + label;
    }


    @Override
    public int compareTo(Label o) {
        return Integer.compare(label, o.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label1 = (Label) o;

        return label == label1.label;

    }

    @Override
    public int hashCode() {
        return label;
    }
}
