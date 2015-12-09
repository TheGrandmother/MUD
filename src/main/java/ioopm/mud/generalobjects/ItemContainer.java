package ioopm.mud.generalobjects;

/**
 * This class is a simple clas used to indicate the precens of items in a room or an inventory
 *
 * @author TheGrandmother
 */
public class ItemContainer {
	/**
	 * The item in the container
	 */
	Item item;
	/**
	 * How many of the item are in the container. May never be less than 1.
	 */
	int amount;

	/**
	 * Creates a new item container with amount 1.
	 *
	 * @param item The item contained in the item container
	 */
	public ItemContainer(Item item) {
		this.item = item;
		amount = 1;
	}

	/**
	 * Returns the item in the container.
	 *
	 * @return the item in the container
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Returns the name of the item in the container
	 *
	 * @return The name of the item in the container
	 */
	public String getName() {
		return item.getName();
	}

	/**
	 * Returns how many of the item are in the container
	 *
	 * @return The amount of the item in the container
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the amount of the item in the container
	 *
	 * @param amount How many of the item are in the container
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * Adds items to the container
	 *
	 * @param add How many items to add to the container. Must be larger than 0
	 */
	public void addAmount(int add) {
		if(add > 0) {
			this.amount += add;
		}
	}

}