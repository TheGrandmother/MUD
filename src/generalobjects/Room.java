package generalobjects;

public class Room {

    private final String NAME;

    private Room north = null;
    private Room east = null;
    private Room south = null;
    private Room west = null;

    private boolean north_door_unlocked = false;
    private boolean east_door_unlocked = false;
    private boolean south_door_unlocked = false;
    private boolean west_door_unlocked = false;

    public Room(String name) {
        this.NAME = name;
    }

    public Room setNorth(Room r) {
        this.north = r;
        return this;
    }

    public Room setEast(Room r) {
        this.east = r;
        return this;
    }

    public Room setSouth(Room r) {
        this.south = r;
        return this;
    }

    public Room setWest(Room r) {
        this.west = r;
        return this;
    }

    public Room setDoorlockNorth() {
        this.north_door_unlocked = !this.north_door_unlocked;
        return this;
    }
}
