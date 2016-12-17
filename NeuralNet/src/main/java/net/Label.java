package net;

/**
 * Created by nikita on 03.12.16.
 */
public class Label implements Comparable<Label> {
    public int value;

    public Label(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "" + value;
    }


    @Override
    public int compareTo(Label o) {
        return Integer.compare(value, o.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label1 = (Label) o;

        return value == label1.value;

    }

    @Override
    public int hashCode() {
        return value;
    }
}
