package classifier;

/**
 * Created by nikita on 14.10.16.
 */
public enum Classes {
    HAM("HAM"),
    SPAM("SPAM");

    private final String name;

    Classes(String s) {
        this.name = s;
    }

    public boolean equalsName(String otherName) {
        return otherName != null && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public int get() {
        switch (this) {
            case HAM: return 0;
            case SPAM: return 1;
        }
        return -1;
    }

    public double weight() {
        switch (this) {
            case HAM: return 1d;
            case SPAM: return 1d;
        }
        return 0d;
    }
}
