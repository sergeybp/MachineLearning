package classifiers.params;

/**
 * Created by nikita on 28.10.16.
 */
public enum Classes {
    FIRST("FIRST"),
    SECOND("SECOND");

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

    public int value() {
        switch (this) {
            case FIRST:
                return -1;
            case SECOND:
                return 1;
        }
        return 0;
    }

    public int index() {
        switch (this) {
            case FIRST:
                return 0;
            case SECOND:
                return 1;
        }
        return -1;
    }


}