package utils;

/**
 * Created by nikita on 16.09.16.
 */
public class DataInstance {
    public double area;
    public double rooms;
    public double prices;

    public DataInstance(double area, double rooms, double prices) {
        this.area = area;
        this.rooms = rooms;
        this.prices = prices;
    }

    public double getArea() {
        return area;
    }

    public double getByName(String name) {
        switch (name) {
            case "area":
                return area;
            case "rooms":
                return rooms;
            case "prices":
                return prices;
        }
        return 0d;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getRooms() {
        return rooms;
    }

    public void setRooms(double rooms) {
        this.rooms = rooms;
    }

    public double getPrices() {
        return prices;
    }

    public void setPrices(double prices) {
        this.prices = prices;
    }
}
