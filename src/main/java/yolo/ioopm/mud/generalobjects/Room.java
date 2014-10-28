package yolo.ioopm.mud.generalobjects;

import java.util.HashSet;

public class Room {

    class Door {
        private final Room    otherside;
        private       boolean is_locked;

        public Door(Room r, boolean lock_status) {
            otherside = r;
            is_locked = lock_status;
        }

        public boolean isLocked() {
            return is_locked;
        }

        public void setLocked() {
            is_locked = true;
        }

        public void setUnlocked() {
            is_locked = false;
        }

        public Room getOtherSide() {
            return otherside;
        }

        public String getName() {
            return otherside.getName();
        }
    }

    private final String NAME;
    private final String DESCRIPTION;

    private HashSet<Door> exits   = new HashSet<Door>();
    private HashSet<Pc>   players = new HashSet<Pc>();
    private HashSet<NPC>  npcs    = new HashSet<NPC>();
    private HashSet<Item> items   = new HashSet<Item>();

    public Room(String name, String description) {
        this.NAME = name;
        this.DESCRIPTION = description;
    }

    public String getName() {
        return NAME;
    }

    public boolean addExit(Room r, boolean locked) {
        return exits.add(new Door(r, locked));
    }

    public boolean addPlayer(Pc p) {
        return players.add(p);
    }

    public boolean addNPC(NPC n) {
        return npcs.add(n);
    }

    public boolean addItem(Item i) {
        return items.add(i);
    }

    public String[] getNameOfExits() {
        String[] names = new String[exits.size()];

        int i = 0;
        for(Door d : exits) {
            names[i++] = d.getName();
        }

        return names;
    }

    public Pc[] getPlayers() {
        return players.toArray(new Pc[players.size()]);
    }

    public NPC[] getNPCs() {
        return npcs.toArray(new NPC[npcs.size()]);
    }

    public Item[] getItems() {
        return items.toArray(new Item[items.size()]);
    }

    public boolean removePlayer(Pc p) {
        return players.remove(p);
    }

    public boolean removeNPC(NPC n) {
        return npcs.remove(n);
    }

    public boolean removeItem(Item i) {
        return items.remove(i);
    }
}
