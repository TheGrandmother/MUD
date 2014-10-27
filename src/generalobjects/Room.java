package generalobjects;

import java.util.HashSet;

public class Room {

    private final String NAME;

    private HashSet<Room> rooms = null;

    private boolean north_door_unlocked = false;
    private boolean east_door_unlocked  = false;
    private boolean south_door_unlocked = false;
    private boolean west_door_unlocked  = false;

    public Room(String name) {
        this.NAME = name;
    }

    

    public Room setDoorlockNorth() {
        this.north_door_unlocked = !this.north_door_unlocked;
        return this;
    }
}
