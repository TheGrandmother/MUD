package ioopm.mud.game;

import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.*;
import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.Message;
import ioopm.mud.communication.messages.server.SeriousErrorMessage;
import ioopm.mud.generalobjects.Character.Inventory;


/**
 * 
 * This class contains all of the methods used for looking at things.
 * 
 * @author TheGrandmother
 *
 */
public final class See {

	/**
	 * 
	 * This method will look at the room. It will send the actor a {@link ioopm.mud.communication.messages.server.ReplyMessage} with the action {@literal Keywords#LOOK_REPLY}} action with a description of what is in the room.
	 * 
	 * @param actor
	 * @param world The world in which everything is
	 * @param adapter The adapter trough which the {@link Message}s are sent.
	 */
	public static void look(Player actor,World world, Adapter server){

		Room current_room  = actor.getLocation();
		String[] observation = new String[6];
		observation[0] = "You are in room " + current_room.getName() + "."; //NAME
		observation[1] = current_room.getDescription();						//DESCRIPTION
		observation[2] = "";												//EXITS
		observation[3] = "";												//PLAYERS
		observation[4] = "";												//NPCS
		observation[5] = "";												//ITEMS
		
		if(current_room.getExits().isEmpty()){
			observation[2] = " ";
		}else{
			for (Room.Exit door : current_room.getExits()) {
				
				if(door.isLocked()){
					observation[2] += door.getNameOfOtherside() + " locked, ";
				}else{
					observation[2] += door.getNameOfOtherside() + ", ";
				}
			}
			observation[2] = observation[2].substring(0, observation[2].length()-2);
		}
		
		if(current_room.getPlayers().isEmpty()){
			observation[3] = " ";
		}else{
			for (Player pc : current_room.getPlayers()) {
				observation[3] += pc.getName()+", ";
			}
			observation[3] = observation[3].substring(0, observation[3].length()-2);
		}
		
		if(current_room.getNpcs().isEmpty()){
			observation[4] = " ";
		}else{
			for (Npc npc : current_room.getNpcs()) {
				observation[4] += npc.getName()+", ";
			}
			observation[4] = observation[4].substring(0, observation[4].length()-2);
		}
		
		if(current_room.getItems().isEmpty()){
			observation[5] = " ";
		}else{
			for (ItemContainer item : current_room.getItems()) {
				observation[5] += "( "+ item.getAmount() + " "+item.getName()+
						"), ";
			}
			observation[5] = observation[5].substring(0, observation[5].length()-2);
		}
		
		server.sendMessage(new ReplyMessage(actor.getName(), Keywords.LOOK_REPLY, observation));
				

	}
	
	
	/**
	 *
	 * Sends back a {@link ReplyMessage} with a action type {@literal Keywords#INVENTORY_REPLY} with a message describing what is in the actors inventory.
	 * 
	 * @param actor The actor whose inventory is to be inspected.
	 * @param world The world in which everything is
	 * @param adapter The adapter trough which the {@link Message}s are sent.
	 */
	public static void inventory(Player actor, World world, Adapter adapter){
		Inventory inventory = null;
		

		inventory = actor.getInventory();
		if(inventory.getitems().isEmpty()){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Your inventory is empty! You have " + inventory.getMax_volume() + " units of space left."));
			return;
		}
		
		String[] args = new String[3];

		args[0] = ""+(inventory.getMax_volume() - inventory.getVolume());
		args[1] = ""+inventory.getMax_volume();
		args[2] ="";
		
		for (ItemContainer i : inventory.getitems()) {
			if(i.getAmount() == 1){
				args[2] += i.getName()+",";
			}else{
				args[2] += "("+i.getAmount()+" "+i.getName()+"),";
			}
		}
		
		if(args[2].length() > 1){
			args[2] = args[2].substring(0, args[2].length()-1);
		}
		
		adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.INVENTORY_REPLY, args));
		
	}
	
	/**
	 * 
	 * This method is used to examine something in the room. As of now one can only examine {@link ioopm.mud.generalobjects.Item}s and {@link Player}s.
	 * Upon a successful examination a {@link ReplyMessage} with the action {@literal Keywords#EXAMINE_REPLY} will be sent to the actor
	 * containing a message of describing the thing examined.<p>
	 * <p>
	 * An {@link ErrorMessage} will be sent if the thing looked for can't be found. 
	 * 
	 * @param actor The player who is doing the examine
	 * @param arguments what is to be examined (must be length 1)
	 * @param world Where everything is
	 * @param adapter What the {@link Message}s will be sent trough
	 */
	public static void examine(Player actor, String[] arguments, World world, Adapter adapter){
		if(arguments == null || arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Examine takes but one and only one argument."));
			return;
		}
		
		String query_name = arguments[0];
		
		if(World.assertExistence(query_name, world.getItems())){
			//query is item.
			//First check the room for the item.
			for(ItemContainer ic : actor.getLocation().getItems()){
				if(ic.getName().equals(query_name)){
					adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EXAMINE_REPLY, ic.getItem().inspect()));
					return;
				}
			}
			
			//check the players inventory
			for(ItemContainer ic : actor.getInventory().getitems()){
				if(ic.getName().equals(query_name)){
					adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EXAMINE_REPLY, ic.getItem().inspect()+" You own this item."));
					return;
				}
			}
			
			// check if its a mounted weapon.
			if(actor.getWeapon() != null && actor.getWeapon().getName().equals(query_name)){
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EXAMINE_REPLY,actor.getWeapon().inspect()+" You have equipped this weapon."));
				return;
			}
			
		}else if(World.assertExistence(query_name, world.getPlayers())){
			//thing is a player
			Player p = null;
			try {
				p = world.findPlayer(query_name);
			} catch (EntityNotPresent e) {
				adapter.sendMessage(new SeriousErrorMessage(actor.getName(), "Player could not be found after an existence check had ben successful."));
				return;
			}
			if(actor.getLocation().playerPresent(p)){
				if(p.getDescription().equals("")){
					adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EXAMINE_REPLY, p.getName() + " is level " + p.getCs().getLevel()+"."));
					return;
				}else{
					adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EXAMINE_REPLY, p.getName() + " is level " + p.getCs().getLevel()+". Description: " + p.getDescription()));
					return;
				}
			}
		}
		
		adapter.sendMessage(new ErrorMessage(actor.getName(), query_name + " could not be found."));
		return;
		
	}
	
	/**
	 * 
	 * This function returns a {@link ReplyMessage} with the action {@literal Keywords#CS_REPLY} with a description of the actors stats.
	 * 
	 * @param actor The players who want to see their stats.
	 * @param world Where everything is.
	 * @param adapter Adapter trough which all {@link Message}are to be sent.
	 */
	public static void cs(Player actor, World world, Adapter adapter){
		adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.CS_REPLY, "You are level " + actor.getCs().getLevel()+". You have "+
		actor.getCs().getHealth() +" health points out of "+	actor.getCs().getMaxHealth()+".You have " + actor.getCs().getHp() + " university credits and and need "+
				 actor.getCs().hpToNextLevel()+ " more to level up."));
	}
	
}
