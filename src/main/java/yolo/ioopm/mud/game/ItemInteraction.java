package yolo.ioopm.mud.game;

import java.io.ObjectInputStream.GetField;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.communication.messages.server.SeriousErrorMessage;
import yolo.ioopm.mud.communication.server.ServerAdapter;
import yolo.ioopm.mud.generalobjects.Character.Inventory;
import yolo.ioopm.mud.generalobjects.Character.Inventory.InventoryOverflow;
import yolo.ioopm.mud.generalobjects.*;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.items.Weapon;

public abstract class ItemInteraction {

	
	public static void Take(String actor, String[] arguments, World world, Adapter adapter){
		if(arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor, Keywords.TAKE + " takes only one argument!"));
			return;
		}
		
		String item_name = arguments[0];
		
		Pc player = null;
		Room current_room = null; 
		
		try {
			player = world.findPc(actor);
			current_room = player.getLocation();
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(e.getName(), "Player could not be found."));
			return;
		}
		
		Item item = null;
		
		try {
			item = world.findItem(item_name);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new ErrorMessage(actor, item_name + " does not exist in the world."));
			return;
		}
		
		if(!current_room.removeItem(item)){
			adapter.sendMessage(new ErrorMessage(actor, item_name + "is not in the room."));
			return;
		}
		
		try {
			player.getInventory().addItem(item);
		} catch (InventoryOverflow e) {
			adapter.sendMessage(new ErrorMessage(actor, "Your inventory is full!"));
			return;
		}
		
		adapter.sendMessage(new ReplyMessage(actor, Keywords.TAKE_REPLY, new String[]{"You picked up a/an " + item_name +"."}));
	}
	
	
	public static void drop(String actor, String[] arguments, World world, Adapter adapter){
		if(arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor, Keywords.TAKE + " takes only one argument!"));
			return;
		}
		
		String item_name = arguments[0];
		
		Pc player = null;
		Room current_room = null; 
		
		try {
			player = world.findPc(actor);
			current_room = player.getLocation();
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(e.getName(), "Player could not be found."));
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
				adapter.sendMessage(new ReplyMessage(actor, Keywords.DROP_REPLY, new String[]{"You droped "+ item_name + "."}));
				return;
			}
		}
		adapter.sendMessage(new ErrorMessage(actor, "You dont have that item."));
	}
	
	
	//TODO this might need to be refactored
	public static void equip(String actor, String[] arguments, World world, Adapter adapter){
		if(arguments.length != 1 || arguments[0].equals("")){
			adapter.sendMessage(new ErrorMessage(actor, "Malformed message. Equip takes 1 argument."));
			return;
		}
		
		String item_name = arguments[0];
		Pc player = null;
		
		try {
			player = world.findPc(actor);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(e.getName(), "Player could not be found."));
			return;
		}
		
		Inventory inv = player.getInventory();
		
		Item i = inv.findItem(item_name);
		if(i == null){
			adapter.sendMessage(new ErrorMessage(actor, "You do not have " + item_name+"."));
			return;
		}
		
		if(!(i instanceof Weapon)){
			adapter.sendMessage(new ErrorMessage(actor, item_name + " is not equippable :/"));
			return;
		}
		
		if(player.getWeapon() != null){
			adapter.sendMessage(new ErrorMessage(actor, "You have already equipped" + player.getWeapon().getName()));
			return;
		}else{
			inv.removeItem(item_name);
			player.setWeapon((Weapon) i);
			adapter.sendMessage(new ReplyMessage(actor, Keywords.EQUIP_REPLY, new String[]{" You have equipped " + item_name + "."}));
			return;
		}
	}
	
	public static void unequip(String actor,String[] arguments, World world, Adapter adapter){
		if(arguments[0].length() >= 1 ){
			adapter.sendMessage(new ErrorMessage(actor, "Malformed message. Unequip takes no arguments. But you sent " + arguments.length +"."));
			return;
		}
		
		Pc player = null;
		
		try {
			player = world.findPc(actor);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(e.getName(), "Player could not be found."));
			return;
		}
		
		Inventory inv = player.getInventory();
		if(player.getWeapon()==null){
			adapter.sendMessage(new ErrorMessage(actor, "You dont have anything equiped"));
			return;
		}else{
			Weapon w = player.getWeapon();
			player.setWeapon(null);
			try {
				player.getInventory().addItem(w);
				adapter.sendMessage(new ReplyMessage(actor, Keywords.UNEQUIP_REPLY, new String[]{"You have unequiped your weapon."}));
			} catch (InventoryOverflow e) {
				adapter.sendMessage(new ErrorMessage(actor, "You dont have enough space in your inventory." + w.getName() + " takes up " + w.getSize() + 
						"units of space but you only have " + (player.getInventory().getMax_volume()-player.getInventory().getVolume()) + " spaces free."));
				return;
			}
			
		}
		
		
	}
	
}


