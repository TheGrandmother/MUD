package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.messages.server.*;
import yolo.ioopm.mud.generalobjects.Character;
import yolo.ioopm.mud.generalobjects.Item.UseFailedException;
import yolo.ioopm.mud.generalobjects.Player;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.*;

/**
 * This class contains all of the functions associated with the combat mechanics.
 * These methods are only to be called trough the executeAction method in {@link GameEngine} 
 * @author TheGrandmother
 *
 */
public abstract class Combat {
	
	
	/**
	 * 
	 * This method is called when an attack action gets issued.
	 * It tries to attack the target given by the first element in the arguments
	 * array.
	 * <p>
	 * If the attack was completed a {@link NotifactionMesssage} gets broadcasted to all of the other players in the room except for the attacker and the target.
	 * <p>
	 * If the attack where to be unsuccessful only the attacker and target will receive a {@link ReplyMessage}.
	 * <p>
	 * If the target dies a {@link NotifactionMesssage} will be broadcasted to all the other players in the room notifying the of the players departure from the human condition.
	 * The attacker will also receive a portion of the targets university credits and another part  of "new" university credits.<p>
	 * Upon death the the target will be respawned in the appropriate lobby(see {@link World#getLobby(int)}) with some health restored. 
	 * All the players in the lobby will be notified about the targets arrival.
	 * <p>
	 * Upon the death of the target and after the attacker receives his new university credits a level-up check will be performed (see {@link Character.CharacterSheet#levelUp()}).
	 * <p>
	 * The mechanics of the combat are defined in specs/Mechanics.txt (<b>NOTE:</b> this file is as of now a bit incomplete.)
	 * 
	 * 
	 * 
	 * @param actor Who is attacking
	 * @param arguments First entry is the name of the target
	 * @param world
	 * @param adapter
	 * @date 
	 */
	public static void attack(Player actor, String[] arguments, World world, Adapter adapter){
		if(arguments == null || arguments.length != 1){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Attack takes only one argument."));
			return;
		}
		
		String target_name = arguments[0];
		Player target = null;
		
		
		if(target_name.equals(actor.getName())){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Dude... why are you attacking your self?"));
			return;
		}
		
		if(!actor.getLocation().isPVP()){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "PVP is not allowed in this room."));
			return;
		}
		
		try {
			target = world.findPc(target_name);
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Target " + target_name + "does not exist."));
			return;
		}
		
		if(actor.getWeapon() == null){
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have a weapon equiped."));
			return;
		}
		
		int damage = -1;
		
		try {
			damage = actor.getWeapon().attack(actor, target);
		} catch (UseFailedException e) {
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"Your attack failed. " + e.getReason()}));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{actor.getName()+" tried to attack you but failed. " + e.getReason()}));
			return;
		}
		
		target.getCs().addHealth(-1*damage);
		
		if(target.getCs().getHealth()==0){
			target.getCs().setHealth((int)(target.getCs().getMaxHealth()*0.75));
		
			target.setLocation(world.getLobby(target.getCs().getLevel()));
			target.getLocation().addPlayer(target);
			GameEngine.broadcastToRoom(adapter, target.getLocation(), target_name+" returned from the dead.", target_name);
			

			
			int hp_taken = (int) (target.getCs().getHp()*((double)GameEngine.d6()*0.1));
			target.getCs().addHp(-1*(hp_taken/2));
			actor.getCs().addHp(hp_taken);
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"You killed " + target_name + " and got " + hp_taken + " university credists :D"}));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{"You was killed by " + actor.getName() + " and he stole " + hp_taken/2 + " university credists!. "
					+ "You respawned in " + target.getLocation().getName()}));
			
			
			GameEngine.broadcastToRoom(adapter, actor.getLocation(), target_name+" was killed by "+ actor.getName() +"!",new String[]{actor.getName(),target_name});
			if(actor.getCs().levelUp()){
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"Yo leveld up and are now lvl: " + actor.getCs().getLevel()+
					". Your max health is now " + actor.getCs().getMaxHealth() + "!"}));
			}
			return;
		}
		
		adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, new String[]{"You attacked " + target_name + " and dealt " + damage + " damage."}));
		adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, new String[]{"You was attacked by " + actor.getName() + " and suffered " + damage + " damage."}));
		
		GameEngine.broadcastToRoom(adapter, actor.getLocation(), target_name+" was attacked by "+ actor.getName() +"!",new String[]{actor.getName(),target_name});
		return;
		

	}
	
	
}
