package utils;

/**
 * Created by nikita on 16.09.16.
 */
public class DataInstance {
    public long userID;
    public long itemID;
    public int rate;

    public DataInstance(long userID, long itemID, int rate) {
        this.userID = userID;
        this.itemID = itemID;
        this.rate = rate;
    }
}
