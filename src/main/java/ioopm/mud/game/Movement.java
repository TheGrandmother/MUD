package ioopm.mud.game;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.communication.messages.server.SeriousErrorMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.*;
import ioopm.mud.generalobjects.items.Key;

/**
 * Class containing all methods related to moving around in the world.
 *
 * @author TheGrandmother
 */
public abstract class Movement {

	/**
	 * This method is called when the actor wants to move from one room to another.
	 * It will send the actor a {@link ReplyMessage} with the action {@literal Keywords#MOVE_REPLY}} if the move was successful.<p>
	 * A {@link ErrorMessage} will be sent if<p>
	 * The argument is malformed<p>
	 * The destination room does not exist<p>
	 * The current room has no exit to destination<p>
	 * The destination room is locked and the player has no key to that room or the player does not have a sufficient level to use the key.<p>
	 *
	 * @param actor     The player who wants to move.
	 * @param arguments Name to the room.
	 * @param world     Where all the stuff is
	 * @param adapter   Through which the messages will be sent.
	 */
	public static void move(Player actor, String[] arguments, World world, Adapter adapter) {
		if(arguments == null || arguments.length != 1) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Wrong number of arguments for move operation."));
			return;
		}

		String destination_name = arguments[0];
		Room current_room = actor.getLocation();

		Room destination_room = getDestinationRoom(destination_name,current_room,actor,world,adapter);
		
		if(destination_room == null){return;}

		Room.Exit door = current_room.getExit(destination_name);
		if(!door.isLocked() || hasKey(door,actor,world,adapter)) {

			try {
				current_room.removePlayer(actor);
			} catch(EntityNotPresent e) {
				adapter.sendMessage(new SeriousErrorMessage(actor.getName(), "You are not in the room you are trying to leave!"));
				return;
			}
			destination_room.addPlayer(actor);
			actor.setLocation(destination_room);

			GameEngine.broadcastToRoom(adapter, current_room, actor.getName() + " has gone to " + destination_room.getName() + ".", actor.getName());

			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.MOVE_REPLY, "You are now in " + destination_room.getName() + "."));
			GameEngine.broadcastToRoom(adapter, destination_room, actor.getName() + " entered from " + current_room.getName() + ".", actor.getName());

		} else {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have the key to " + destination_name + "."));
		}

	}

	
	/**Checks if the player has the key to the current door.
	 *
	 *@param door The actual door!
	 *@param actor Who is trying to open the door
	 *@param world The world in which everything is
	 *@param adapter take aguess
	 *@return True if the player has the key and false otherwise.
	 */
	private static boolean hasKey(Room.Exit door, Player actor, World world, Adapter adapter){
	
		if(door.isLocked()) {
			Inventory inventory = actor.getInventory();
			for(ItemContainer i : inventory.getitems()) {
				if(i.getItem() instanceof Key && door.getNameOfOtherside().equals(((Key) i.getItem()).getTargetRoom())) {
					if(i.getItem().getLevel() > actor.getCs().getLevel()) {
						adapter.sendMessage(new ErrorMessage(actor.getName(), "Key requires level " + i.getItem().getLevel() + " but you are only level " + actor.getCs().getLevel() + "."));
						return false;
					} else {
						 return true;
					}
				}
			}
		}
		return false;

	}


	/**Gets the room wich has the destination name if such a room exists and there is an exit to it.
	 *
	 *
	 *@param destination_name The name of the room to which the player thinks he can go
	 *@param current_room The room that the player is in
	 *@param actor The player who is apparently moving around.
	 *@param world Where everuthing is
	 *@param adapter The adapter trough which messages are sent.
	 *@return The room to which destination_name points. If thigs go south null is returned.
	 */
	private static Room getDestinationRoom(String destination_name, Room current_room,Player actor, World world, Adapter adapter){
	
		Room destination_room = null;
		
		try {

			destination_room = world.findRoom(destination_name);

			if(actor.getLocation() == destination_room) {
				adapter.sendMessage(new ErrorMessage(actor.getName(), "Dude you are already in that room"));
				return null;

			}

			if(current_room.getExit(destination_name) == null) {
				adapter.sendMessage(new ErrorMessage(actor.getName(), current_room.getName() + " has no exit to " + destination_name + "."));
				return null;
			}
			
		} catch(EntityNotPresent e1) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), destination_name + " isn't even a real room!"));
			return null;
		}

		return destination_room;

	}
}
