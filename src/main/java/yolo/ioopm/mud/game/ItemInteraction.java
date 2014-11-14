package yolo.ioopm.mud.game;

import java.io.ObjectInputStream.GetField;

import yolo.ioopm.mud.Server;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.communication.messages.server.SeriousErrorMessage;
import yolo.ioopm.mud.generalobjects.*;
import yolo.ioopm.mud.generalobjects.Character.Inventory.InventoryOverflow;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;

public abstract class ItemInteraction {

	
	public static void Take(String actor, String[] arguments, World world, Server server){
		if(arguments.length != 1){
			server.sendMessage(new ErrorMessage(actor, Keywords.TAKE + " takes only one argument!"));
			return;
		}
		
		String item_name = arguments[0];
		
		Pc player = null;
		Room current_room = null; 
		
		try {
			player = world.findPc(actor);
			current_room = player.getLocation();
		} catch (EntityNotPresent e) {
			server.sendMessage(new SeriousErrorMessage(e.getName(), "Player could not be found."));
			return;
		}
		
		Item item = null;
		
		try {
			item = world.findItem(item_name);
		} catch (EntityNotPresent e) {
			server.sendMessage(new ErrorMessage(actor, item_name + " does not exist in the world."));
			return;
		}
		
		if(!current_room.removeItem(item)){
			server.sendMessage(new ErrorMessage(actor, item_name + "is not in the room."));
			return;
		}
		
		try {
			player.getInventory().addItem(item);
		} catch (InventoryOverflow e) {
			server.sendMessage(new ErrorMessage(actor, "Your inventory is full!"));
			return;
		}
		
		server.sendMessage(new ReplyMessage(actor, Keywords.TAKE_REPLY, new String[]{"You picked up a/an " + item_name +"."}));
	}
	
	
	public static void drop(String actor, String[] arguments, World world, Server server){
		if(arguments.length != 1){
			server.sendMessage(new ErrorMessage(actor, Keywords.TAKE + " takes only one argument!"));
			return;
		}
		
		String item_name = arguments[0];
		
		Pc player = null;
		Room current_room = null; 
		
		try {
			player = world.findPc(actor);
			current_room = player.getLocation();
		} catch (EntityNotPresent e) {
			server.sendMessage(new SeriousErrorMessage(e.getName(), "Player could not be found."));
			return;
		}
		
		Item item = null;
		
//		try {
//			item = world.findItem(item_name);
//		} catch (EntityNotPresent e) {
//			server.sendMessage(new ErrorMessage(actor, item_name + " does not exist in the world."));
//			return;
//		}
		
		for (ItemContainer i : player.getInventory().getitems()) {
			if(i.getName().equals(item_name)){
				current_room.addItem(i.getItem());
				player.getInventory().removeItem(item_name);
				server.sendMessage(new ReplyMessage(actor, Keywords.DROP_REPLY, new String[]{"You droped "+ item_name + "."}));
				return;
			}
		}
		
		server.sendMessage(new ErrorMessage(actor, "You dont have that item."));
		
		
	}
	
}


