package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.messages.server.AuthenticationReplyMessage;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.NotifactionMesssage;
import yolo.ioopm.mud.communication.messages.server.RegistrationReplyMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.communication.messages.server.SeriousErrorMessage;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.World.EntityNotUnique;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for handling the interpreting of the actions passed to the server.
 *
 * @author TheGrandmother
 */
public class GameEngine {

	private static final Logger logger = Logger.getLogger(GameEngine.class.getName());

	Adapter adapter;
	World   world;

	public GameEngine(Adapter adapter, World world) {
		this.adapter = adapter;
		this.world = world;
	}
	
	//TODO Implement logging out so that players gets removed from rooms before the log out.
	public void executeAction(Message message) {

		String actor_name = message.getSender();
		MessageType type = message.getType();
		String[] arguments = message.getArguments();

		if(type == MessageType.AUTHENTICATION) {
			//TODO Fix so that players gets added to the lobby when they log in!
			String username = arguments[0];
			String password = arguments[1];
			Pc player = null;
			try {
				player = world.findPc(username);
				if(player.isLogedIn()){
					adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false));
					return;
				}
				if(checkUsernamePassword(username, password)){
					player.setLocation(get_lobby(player,world));
					player.getLocation().addPlayer(player);
					player.setLoggedIn(true);
					adapter.sendMessage(new AuthenticationReplyMessage(actor_name, true));
					return;
				}else{
					adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false));
					return;
				}
				
			} catch (EntityNotPresent e) {
				adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false));
				return;
			}

			//server.sendMessage(new AuthenticationReplyMessage(actor, checkUsernamePassword(username, password)));
		}else if(type == MessageType.REGISTRATION) {
			String username = arguments[0];
			String password = arguments[1];
			
			try {
				world.addCharacter(new Pc(username, "", password, get_lobby(null,world)));
				world.findPc(username).getLocation().addPlayer(world.findPc(username));
				world.findPc(username).setLoggedIn(true);
			} catch (EntityNotUnique e) {
				adapter.sendMessage(new ErrorMessage(actor_name, "The name " + username + " is taken" ));
				adapter.sendMessage(new RegistrationReplyMessage(actor_name, false));
				return;
				
			} catch (EntityNotPresent e) {
				adapter.sendMessage(new SeriousErrorMessage(actor_name, "The lobby does not exist" ));
				adapter.sendMessage(new RegistrationReplyMessage(actor_name, false));
				return;
			}

			adapter.sendMessage(new RegistrationReplyMessage(actor_name, true));
			return;
			//server.sendMessage(new RegistrationReplyMessage(actor, register(username, password)));
		}
		else if(type == MessageType.GENERAL_ACTION) {

			Pc actor = null;
			try {
				if(!world.findPc(actor_name).isLogedIn()){
					adapter.sendMessage(new SeriousErrorMessage(actor_name, "Actor is not logged in!"));
					return;
				}else{
					actor =world.findPc(actor_name);
				}
			} catch (EntityNotPresent e) {
				adapter.sendMessage(new SeriousErrorMessage(actor_name, "Actor does not exist!"));
				return;
			}
			
			
			String action = message.getAction();


			switch(action) {

			
				case Keywords.SAY:
					Talk.say(actor, arguments, world, adapter);
					break;

				case Keywords.WHISPER:
					Talk.whisper(actor, arguments, world, adapter);
					break;

				case Keywords.ECHO:
					adapter.sendMessage( new ReplyMessage(actor.getName(),"echo_reply", arguments));
					break;

				case Keywords.LOOK:
					See.look(actor, world, adapter);
					break;
					
				case Keywords.INVENTORY:
					See.inventory(actor, world, adapter);
					break;

				case Keywords.MOVE:
					Movement.move(actor, arguments, world, adapter);
					break;

				case Keywords.TAKE:
					ItemInteraction.take(actor, arguments, world, adapter);
					break;

				
				case Keywords.DROP:
					ItemInteraction.drop(actor, arguments, world, adapter);
					break;

				
				case Keywords.EQUIP:
					ItemInteraction.equip(actor, arguments, world, adapter);
					break;
					
				case Keywords.UNEQUIP:
					ItemInteraction.unequip(actor, arguments, world, adapter);
					break;
				
				case Keywords.ATTACK:
					Combat.attack(actor, arguments, world, adapter);
					break;
					
				case "drop_players":
					for (Pc p : world.getPlayers()) {
						System.out.println(p.getName());
					}
					break;
				case "drop_players_room":
					try {
						Room room = world.findRoom(arguments[0]);
						for (Pc p : room.getPlayers()) {
							System.out.println(p.getName());
						}
					} catch (EntityNotPresent e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;

				case "am_i_real":
					System.out.println(actor);
					for (Pc p : world.getPlayers()) {
						if(p.getName().equals(actor)){

							System.out.println("indeed i am.");
							break;
						}
					}
					System.out.println("Nobody is in the end.....");
					break;

				default:
					adapter.sendMessage( new ErrorMessage(actor.getName(), action + " is not a valid keyword!"));
			}
		}
	}
	
	/**
	 * 
	 * Broadcasts a ReplyMessage to all the players in the given room
	 * 
	 * @param adapter
	 * @param room
	 * @param type
	 * @param nouns
	 */
	public static void broadcastToRoom(Adapter adapter, Room room, String message){
		for (Pc player : room.getPlayers()) {
			adapter.sendMessage( new NotifactionMesssage(player.getName() , message));
		}
	}
	
	/**
	 * 
	 * Broadcasts a message to all the players in the given room EXCEPT for the sender.
	 * 
	 * @param adapter
	 * @param room	
	 * @param type	The type of the message
	 * @param nouns
	 * @param exludes	The name of the player to which no message is to be sent!
	 */
	public static void broadcastToRoom(Adapter adapter, Room room, String message,String exludes){
		for (Pc player : room.getPlayers()) {
			if(!(player.getName().equals(exludes))){
				adapter.sendMessage( new NotifactionMesssage(player.getName() , message));
			}
		}
	}
	
	
	/**
	 * 
	 * Broadcasts a message to all the players in the given room EXCEPT for all the entries in the exclude array .
	 * 
	 * @param adapter
	 * @param room	
	 * @param type	The type of the message
	 * @param nouns
	 * @param excludes	The name of all the players to which no message is to be sent!
	 */
	public static void broadcastToRoom(Adapter adapter, Room room, String message,String[] excludes){
		
		for (Pc player : room.getPlayers()) {
			boolean skip = false;
			for (String exclude : excludes) {
				if(player.getName().equals(exclude)){
					skip = true;
					break;
				}
				
			}
			if(!skip){
				adapter.sendMessage( new NotifactionMesssage(player.getName() , message));
			}			
		}
	}
	
	
	/**
	 * 
	 * @return A pseudo random number between 1-20
	 */
	public static int d20(){
		return 1+(int)(Math.random()*20);
	}
	
	/**
	 * 
	 * @return A pseudo random number between 1-6
	 */
	public static int d6(){
		return 1+(int)(Math.random()*6);
	}
	
	/**
	 * Gets the lobby to which this player is supposed to go to.
	 * 
	 * As of now this is ugly and it should be done in a more general way.
	 * 
	 * @param player
	 * @return
	 * @throws EntityNotPresent 
	 */
	protected static Room get_lobby(Pc player,World world) throws EntityNotPresent{
		if(player == null){
			return world.findRoom("room1");
		}else{
			//TODO
			//wrtie an actual usefull thing here.
			return world.findRoom("room1");
		}
	}

	/**
	 * Returns true if user exists and has the correct password.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean checkUsernamePassword(String username, String password) {
		try {
			return (world.findPc(username).checkPassword(password));
		} catch (EntityNotPresent e) {
			//Player not present
			return false;
		}
	}
}