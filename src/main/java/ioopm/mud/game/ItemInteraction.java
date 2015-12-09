package ioopm.mud.game;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.Character.Inventory;
import ioopm.mud.generalobjects.Character.Inventory.InventoryOverflow;
import ioopm.mud.generalobjects.*;
import ioopm.mud.generalobjects.items.Weapon;

public abstract class ItemInteraction {

	/**
	 * This functions tries to take the item given as an argument from the room. If the actor succeeds in taking the object a {@link ioopm.mud.communication.messages.server.ReplyMessage} with action
	 * {@literal Keywords#TAKE_REPLY} informing him of the successful take. All other players in the room will receive a  {@link ioopm.mud.communication.messages.server.NotificationMessage} notifying them that the
	 * item was picked up by the actor.
	 * <p>
	 * A {@link ioopm.mud.communication.messages.server.ErrorMessage} will be sent to the actor if:<p>
	 * The item is not present in the room.<p>
	 * The players inventory is full.<p>
	 * An incorrect argument is given<p>
	 *
	 * @param actor     The player who does things
	 * @param arguments Should be of only length one and contain the name of the item to be picked up.
	 * @param world     The world in which everything lives.
	 * @param adapter   The adapter trough which the messages are to be sent.
	 */
	public static void take(Player actor, String[] arguments, World world, Adapter adapter) {
		if(arguments == null || arguments.length != 1) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), Keywords.TAKE + " takes only one argument!"));
			return;
		}

		String item_name = arguments[0];


		Room current_room = actor.getLocation();


		Item item = null;

		try {
			item = world.findItem(item_name);
		} catch(EntityNotPresent e) {
			//Players should not care if it's a real item or not
			adapter.sendMessage(new ErrorMessage(actor.getName(), "There is no " + item_name + " here."));
			return;
		}

		if(current_room.hasItem(item_name)) {
			try {
				actor.getInventory().addItem(item);
			} catch(InventoryOverflow e) {
				adapter.sendMessage(new ErrorMessage(actor.getName(), "Your inventory is full!"));
				return;
			}

			current_room.removeItem(item);

			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.TAKE_REPLY, "You picked up a/an " + item_name + "."));
			GameEngine.broadcastToRoom(adapter, current_room, actor.getName() + " picked up " + item_name + ".", actor.getName());
			return;

		} else {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Item " + item_name + " is not in the room!"));
			return;
		}


		//adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.TAKE_REPLY, "You picked up a/an " + item_name +"."));
		//GameEngine.broadcastToRoom(adapter, current_room, actor.getName() +" picked up " + item_name+".", actor.getName());
	}

	/**
	 * In this function the actor tries to drop an item . If the drop is successful the player will receive a {@link ReplyMessage} with action
	 * {@literal Keywords#DROP_REPLY} and all other players in the room will receive a {@link ioopm.mud.communication.messages.server.NotificationMessage} notifying them of the dropped item.
	 * <p>
	 * An {@link ErrorMessage} will be sent if the player does not have the item.
	 *
	 * @param actor     The dude dropping stuff everywhere
	 * @param arguments The item to be dropped (must be at index 0)
	 * @param world     Where everything is
	 * @param adapter   The adapter trough which the messages are to be sent.
	 */
	public static void drop(Player actor, String[] arguments, World world, Adapter adapter) {
		if(arguments == null || arguments.length != 1) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), Keywords.TAKE + " takes only one argument!"));
			return;
		}

		String item_name = arguments[0];


		Room current_room = actor.getLocation();

		Item item = null;

		for(ItemContainer i : actor.getInventory().getitems()) {
			if(i.getName().equals(item_name)) {
				current_room.addItem(i.getItem());
				actor.getInventory().removeItem(item_name);
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.DROP_REPLY, "You dropped " + item_name + "."));
				GameEngine.broadcastToRoom(adapter, current_room, actor.getName() + " dropped " + item_name, actor.getName());
				return;
			}
		}
		adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have that item."));
	}

	/**
	 * With this method the actor tries to equip a item. An {@link ReplyMessage} will be sent if the desired item was not equipped.
	 * As of now only equipping weapons is supported.<p>
	 * An {@link ErrorMessage} will be sent if:
	 * The arguments are malformed
	 * The item is not equippable.
	 * The actor does not have the item.
	 * The actor has no free equipping slots.
	 *
	 * @param actor     The player who wants to equip something
	 * @param arguments The item weapon to be equipped.
	 * @param world     The world in which everything is.
	 * @param adapter   The adapter trough which messages will be sent.
	 */
	//TODO this might need to be refactored
	public static void equip(Player actor, String[] arguments, World world, Adapter adapter) {
		if(arguments == null || arguments.length != 1 || arguments[0].equals("")) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Malformed message. Equip takes 1 argument."));
			return;
		}

		String item_name = arguments[0];


		Inventory inv = actor.getInventory();

		Item i = inv.findItem(item_name);
		if(i == null) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You do not have " + item_name + "."));
			return;
		}

		if(!(i instanceof Weapon)) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), item_name + " is not equippable :/"));
			return;
		}

		if(actor.getWeapon() != null) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You have already equipped " + actor.getWeapon().getName()));
			return;
		} else {
			inv.removeItem(item_name);
			actor.setWeapon((Weapon) i);
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EQUIP_REPLY, "You have equipped " + item_name + "."));
			return;
		}
	}

	/**
	 * Attempts to unequip an item. Will return a {@link ReplyMessage} with action {@literal Keywords#UNEQUIP_REPLY} if the unequip was unsuccessful.<p>
	 * Will send an {@link ErrorMessage} if the item is not equipped or there is not enough room for the item in the inventory.
	 *
	 * @param actor   The player that tries to equip stuff.
	 * @param world   Where everything is
	 * @param adapter Through which the the messages are to be sent.
	 */
	public static void unequip(Player actor, World world, Adapter adapter) {

		if(actor.getWeapon() == null) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have anything equipped"));
			return;
		} else {
			Weapon w = actor.getWeapon();

			try {
				actor.getInventory().addItem(w);
				actor.setWeapon(null);
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.UNEQUIP_REPLY, "You have unequipped your weapon."));
				return;
			} catch(InventoryOverflow e) {
				adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have enough space in your inventory. " + w.getName() + " takes up " + w.getSize() +
					" units of space but you only have " + (actor.getInventory().getMax_volume() - actor.getInventory().getVolume()) + " free."));
				return;
			}

		}


	}

}


