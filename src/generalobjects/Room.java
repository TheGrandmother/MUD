package generalobjects;

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

        public void toggleLock() {
            is_locked = !is_locked;
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

    private HashSet<Door>   exits   = new HashSet<Door>();
    private HashSet<Player> players = new HashSet<Player>();
    private HashSet<NPC>    npcs    = new HashSet<NPC>();
    private HashSet<Item>   items   = new HashSet<Item>();

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

    public boolean addPlayer(Player p) {
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

    public Player[] getPlayers() {
        return players.toArray(new Player[players.size()]);
    }

    public NPC[] getNPCs() {
        return npcs.toArray(new NPC[npcs.size()]);
    }

    public Item[] getItems() {
        return items.toArray(new Item[items.size()]);
    }

    public boolean removePlayer(Player p) {
        return players.remove(p);
    }

    public boolean removeNPC(NPC n) {
        return npcs.remove(n);
    }

    public boolean removeItem(Item i) {
        return items.remove(i);
    }
}
