package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.Character.Inventory;
import yolo.ioopm.mud.generalobjects.Character.Inventory.InventoryOverflow;
import yolo.ioopm.mud.generalobjects.*;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.items.Weapon;

public abstract class ItemInteraction {

	
	public static void take(Pc actor, String[] arguments, World world, Adapter adapter){
		if(arguments == null || arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor.getName(), Keywords.TAKE + " takes only one argument!"));
			return;
		}
		
		String item_name = arguments[0];
		
		
		Room current_room = actor.getLocation(); 

		
		Item item = null;
		
		try {
			item = world.findItem(item_name);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), item_name + " does not exist in the world."));
			return;
		}
		
		if(!current_room.removeItem(item)){
			adapter.sendMessage(new ErrorMessage(actor.getName(), item_name + "is not in the room."));
			return;
		}
		
		try {
			actor.getInventory().addItem(item);
		} catch (InventoryOverflow e) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Your inventory is full!"));
			return;
		}
		
		adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.TAKE_REPLY, new String[]{"You picked up a/an " + item_name +"."}));
		GameEngine.broadcastToRoom(adapter, current_room, actor.getName() +" picked up " + item_name, actor.getName()+".");
	}
	
	
	public static void drop(Pc actor, String[] arguments, World world, Adapter adapter){
		if(arguments  == null ||arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor.getName(), Keywords.TAKE + " takes only one argument!"));
			return;
		}
		
		String item_name = arguments[0];
		

		Room current_room = actor.getLocation();
		
		Item item = null;
		
		for (ItemContainer i : actor.getInventory().getitems()) {
			if(i.getName().equals(item_name)){
				current_room.addItem(i.getItem());
				actor.getInventory().removeItem(item_name);
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.DROP_REPLY, new String[]{"You droped "+ item_name + "."}));
				GameEngine.broadcastToRoom(adapter, current_room, actor.getName() +" dropped " + item_name, actor.getName()+".");
				return;
			}
		}
		adapter.sendMessage(new ErrorMessage(actor.getName(), "You dont have that item."));
	}
	
	
	//TODO this might need to be refactored
	public static void equip(Pc actor, String[] arguments, World world, Adapter adapter){
		if(arguments == null || arguments.length != 1 || arguments[0].equals("")){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Malformed message. Equip takes 1 argument."));
			return;
		}
		
		String item_name = arguments[0];
		
		
		Inventory inv = actor.getInventory();
		
		Item i = inv.findItem(item_name);
		if(i == null){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You do not have " + item_name+"."));
			return;
		}
		
		if(!(i instanceof Weapon)){
			adapter.sendMessage(new ErrorMessage(actor.getName(), item_name + " is not equippable :/"));
			return;
		}
		
		if(actor.getWeapon() != null){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You have already equipped" + actor.getWeapon().getName()));
			return;
		}else{
			inv.removeItem(item_name);
			actor.setWeapon((Weapon) i);
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.EQUIP_REPLY, new String[]{" You have equipped " + item_name + "."}));
			return;
		}
	}
	
	public static void unequip(Pc actor, World world, Adapter adapter){

		if(actor.getWeapon()==null){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You dont have anything equiped"));
			return;
		}else{
			Weapon w = actor.getWeapon();
			actor.setWeapon(null);
			try {
				actor.getInventory().addItem(w);
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.UNEQUIP_REPLY, new String[]{"You have unequiped your weapon."}));
			} catch (InventoryOverflow e) {
				adapter.sendMessage(new ErrorMessage(actor.getName(), "You dont have enough space in your inventory." + w.getName() + " takes up " + w.getSize() + 
						"units of space but you only have " + (actor.getInventory().getMax_volume()-actor.getInventory().getVolume()) + " spaces free."));
				return;
			}
			
		}
		
		
	}
	
}


