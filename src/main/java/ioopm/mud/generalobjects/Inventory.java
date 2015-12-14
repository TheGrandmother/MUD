package ioopm.mud.generalobjects;

import java.util.HashSet;

/**
 * The inventory contains and manages all of the items possessed by the Character.
 *
 * @author TheGrandmother
 */
public class Inventory {

	private HashSet<ItemContainer> items;
	private int                    volume;
	private int max_volume = 10;

	/**
	 * Creates a new empty inventory with max_volume set to 10.
	 */
	public Inventory() {
		volume = 0;
		items = new HashSet<ItemContainer>();
	}

	/**
	 * returns the set of ItemContainers
	 *
	 * @return The set of item containers.
	 */
	public HashSet<ItemContainer> getitems() {
		return this.items;
	}

	/**
	 * Tries to retrieve an item from the items set.
	 * NOTE: this does not remove the item!
	 *
	 * @param name the name of the item
	 * @return Returns null if no silly item was found
	 */
	public Item findItem(String name) {
		for(ItemContainer i : items) {
			if(i.getName().equals(name)) {
				return i.getItem();
			}
		}
		return null;

	}

	/**
	 * Tries to remove the item. Either removes the {@link ItemContainer} completely or just decreases the amount.
	 *
	 * @param name The name of the item.
	 * @return false if item does not exist
	 */
	public boolean removeItem(String name) {

		for(ItemContainer i : items) {
			if(i.getName().equals(name)) {
				if(i.getAmount() == 1) {
					volume -= i.getItem().getSize();
					items.remove(i);
					return true;
				} else {
					volume -= i.getItem().getSize();
					i.setAmount(i.getAmount() - 1);
					return true;
				}
			}
		}

		return false;
	}


	/**
	 * Ads an item to the inventory. Creates a new {@link ItemContainer} or just dereases the amount.
	 *
	 * @param item item to be added
	 * @throws InventoryOverflow gets thrown if item does not fit.
	 */
	public void addItem(Item item) throws InventoryOverflow {
		for(ItemContainer i : items) {
			if(item.getName().equals(i.getName())) {
				if(item.getSize() + volume > max_volume) {
					throw new InventoryOverflow();
				} else {
					volume += item.getSize();
					i.setAmount(i.getAmount() + 1);
					return;
				}
			}
		}
		if(item.getSize() + volume > max_volume) {
			throw new InventoryOverflow();
		} else {
			volume += item.getSize();
			items.add(new ItemContainer(item));
			return;
		}

	}

	/**
	 * @return the maximum volume of the inventory
	 */
	public int getMax_volume() {
		return max_volume;
	}

	/**
	 * increases the capacity of the inventory
	 *
	 * @param max_volume new max volume
	 */
	public void setMax_volume(int max_volume) {
		this.max_volume = max_volume;
	}

	/**
	 * How much space is occupied
	 *
	 * @return the current volume of the inventory
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * sets the current volume
	 *
	 * @param volume
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}


	@SuppressWarnings("serial")
	/**
	 *
	 * This exception is to be thrown when an item does not fit into the inventory.
	 *
	 * @author TheGrandmother
	 */
	public class InventoryOverflow extends Exception {
		public InventoryOverflow() {
			super();
		}
	}


}
