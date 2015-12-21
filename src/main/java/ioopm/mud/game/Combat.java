package ioopm.mud.game;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.Item;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;

/**
 * This class contains all of the functions associated with the combat mechanics.
 * These methods are only to be called trough the executeAction method in {@link GameEngine}
 *
 * @author TheGrandmother
 */
public abstract class Combat {

	/**
	 * How much of the max healt will be respawned uppon death
	 */
	private final static double HEALTH_RESET_FACTOR = 0.75;
	/**
	 * How much of the taken uc will be removed from the victim uppon death.
	 */
	private final static double UC_REDUCTION_FACTOR = 2;


	/**
	 * This method is called when an attack action gets issued.
	 * It tries to attack the target given by the first element in the arguments
	 * array.
	 * <p>
	 * If the attack was completed a {@link ioopm.mud.communication.messages.server.NotificationMessage} gets broadcast to all of the other players in the room except for the attacker and the target.
	 * <p>
	 * If the attack where to be unsuccessful only the attacker and target will receive a {@link ioopm.mud.communication.messages.server.ReplyMessage}.
	 * <p>
	 * If the target dies a {@link ioopm.mud.communication.messages.server.NotificationMessage} will be broadcast to all the other players in the room notifying the of the players departure from the human condition.
	 * The attacker will also receive a portion of the targets university credits and another part  of "new" university credits.<p>
	 * Upon death the the target will be respawned in the appropriate lobby(see {@link ioopm.mud.generalobjects.World#getLobby(int)}) with some health restored.
	 * All the players in the lobby will be notified about the targets arrival.
	 * <p>
	 * Upon the death of the target and after the attacker receives his new university credits a level-up check will be performed (see {@link ioopm.mud.generalobjects.Character.CharacterSheet#levelUp()}).
	 * <p>
	 * The mechanics of the combat are defined in specs/Mechanics.txt (<b>NOTE:</b> this file is as of now a bit incomplete.)
	 *
	 * @param actor     Who is attacking
	 * @param arguments First entry is the name of the target
	 * @param world
	 * @param adapter
	 * @date
	 */
	public static void attack(Player actor, String[] arguments, World world, Adapter adapter) {
		if(arguments == null || arguments.length != 1) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Attack takes only one argument."));
			return;
		}

		String target_name = arguments[0];
		Player target = null;


		if(target_name.equals(actor.getName())) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Dude... why are you attacking your self?"));
			return;
		}

		if(!actor.getLocation().isPVP()) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "PVP is not allowed in this room."));
			return;
		}

		try {
			target = world.findPlayer(target_name);
		} catch(EntityNotPresent e) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Target " + target_name + " does not exist."));
			return;
		}

		if(!actor.getLocation().playerPresent(target)) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "Target " + target_name + " is not in the room!."));
			return;
		}

		if(actor.getWeapon() == null) {
			adapter.sendMessage(new ErrorMessage(actor.getName(), "You don't have a weapon equipped."));
			return;
		}

		int damage = -1;

		try {
			damage = actor.getWeapon().attack(actor, target);
		} catch(Item.UseFailedException e) {
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, "Your attack failed. " + e.getReason()));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, actor.getName() + " tried to attack you but failed. " + e.getReason()));
			return;
		}


		if(!dealDamage(actor, target, damage, world, adapter)){

			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, "You attacked " + target_name + " and dealt " + damage + " damage."));
			adapter.sendMessage(new ReplyMessage(target_name, Keywords.ATTACK_REPLY, "You were attacked by " + actor.getName() + " and suffered " + damage + " damage."));

			GameEngine.broadcastToRoom(adapter, actor.getLocation(), target_name + " was attacked by " + actor.getName() + "!", actor.getName(), target_name);
		
		}
		return;
	}


	/**
	 * Deals damage to the target.
	 * If death occurs the target will be killed and the attacker will absorb university credits.
	 *
	 *@param actor The Player who is attacking
	 *@param target The target being attacked
	 *@param damage The ammount of damage dealth	
	 *@param world The world in which the attack takes place
	 *@param adapter The adapter trough which the messages get sent
	 *@return True if the target was killed and false otherwise
	 **/
	private static boolean dealDamage(Player actor, Player target, int damage, World world, Adapter adapter){
	
		target.getCs().addHealth(-1 * damage);

		if(target.getCs().getHealth() == 0) {
			kill(actor,target,world,adapter);
			return true;
		}
		
		return false;
	
	}


	/** Kills the target :D
	 *
	 * University credits will be redistributed and the victim will be respawned in the lobby.
	 *
	 *@param actor Who kills the target
	 *@param target The poor sod who gets murdered
	 *@param world Where the horror happens
	 *@param adapter Trough wich the message of the ensuing horror gets transmitted.
	 */
	public static void kill(Player actor, Player target, World world, Adapter adapter){
 		
			target.getCs().setHealth((int) (target.getCs().getMaxHealth() * HEALTH_RESET_FACTOR));
			try {
				target.getLocation().removePlayer(target);
			} catch(EntityNotPresent e) {
				adapter.sendMessage(new ErrorMessage(actor.getName(), "Something went absurdly wrong. This is our fault."));
				adapter.sendMessage(new ErrorMessage(target.getName(), "Something went absurdly wrong. This is our fault."));
			}
			target.setLocation(world.getLobby(target.getCs().getLevel()));
			target.getLocation().addPlayer(target);
			GameEngine.broadcastToRoom(adapter, target.getLocation(), target.getName() + " returned from the dead.", target.getName());


			int uc_taken = computeTakenHp(actor, target); 
			target.getCs().addUc(-1 * (int)(uc_taken / UC_REDUCTION_FACTOR));
			actor.getCs().addUc(uc_taken);
			adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, "You killed " + target.getName() + " and got " + uc_taken + " university credits :D"));
			adapter.sendMessage(new ReplyMessage(target.getName(), Keywords.ATTACK_REPLY, "You were killed by " + actor.getName() + " and he stole " + (int)(uc_taken / UC_REDUCTION_FACTOR) + " university credits!. "
				+ "You respawned in " + target.getLocation().getName()));


			GameEngine.broadcastToRoom(adapter, actor.getLocation(), target.getName() + " was killed by " + actor.getName() + "!", actor.getName(), target.getName());
			if(actor.getCs().levelUp()) {
				adapter.sendMessage(new ReplyMessage(actor.getName(), Keywords.ATTACK_REPLY, "You leveled up and are now lvl: " + actor.getCs().getLevel() +
					". Your max health is now " + actor.getCs().getMaxHealth() + "!"));
			}
 	}


	/**Computes how many university credits are to be stolen.
	 */
	private static int computeTakenHp(Player actor, Player target){
		double dice_factor = 0.1;
		return (int) (target.getCs().getUc() * ((double) GameEngine.d6() * dice_factor));
	}

}
