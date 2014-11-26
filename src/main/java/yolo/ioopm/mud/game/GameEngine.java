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
import yolo.ioopm.mud.generalobjects.Player;
import yolo.ioopm.mud.generalobjects.Room;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.World.EntityNotUnique;

import java.util.logging.Logger;

import com.sun.accessibility.internal.resources.accessibility;

/**
 *This is the main class for the actual game part of the mud. This class is responsible for interpreting the messages
 *and taking appropriate action in accordance to the spec/Messages.txt file.<p>
 *

 *
 *
 * @author TheGrandmother
 */
public class GameEngine {

	private static final Logger logger = Logger.getLogger(GameEngine.class.getName());

	private final Adapter adapter;
	private final World   world;

		/**
		 * Returns a new Game engine
		 * @param adapter This is the adapter trough which all of the communication is to be handled
		 * @param world		This is the world where all of the in-game entities live.
		 */
	public GameEngine(Adapter adapter, World world) {
		this.adapter = adapter;
		this.world = world;
	}
	
	
	
	/**
	 * 
	 * This function is takes the appropriate action for the given 
	 * <p>
	 * <b>NOTE</b>: <p>
	 *Since this project is still in the development phase and changes are currently being made it is very likely that there exists discrepancies
	 *between the spec/Messages.txt files and how this function operates.
	 *
	 * @param message The message to be handled.
	 */
	//TODO Implement logging out so that players gets removed from rooms when they log out.
	public void handleMessage(Message message) {
		MessageType type = message.getType();
		
		if(type == MessageType.AUTHENTICATION) {
			handleLoginRequest(message);
		}else if(type == MessageType.REGISTRATION) {
			handleRegistrationRequest(message);
		}else if(type == MessageType.LOGOUT){
			handleLogoutRequest(message);
		}else if(type == MessageType.GENERAL_ACTION) {
			executeAction(message);
		}
	}
	
	
	private void handleLogoutRequest(Message message){
		String actor_name = message.getSender();
		//MessageType type = message.getType();
		String[] arguments = message.getArguments();
		try {
			Player player = world.findPc(actor_name);
			player.getLocation().removePlayer(player);
			player.setLoggedIn(false);
			GameEngine.broadcastToRoom(adapter, player.getLocation(), actor_name + " has left the game.");
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(actor_name, "Tried to log out a non exsisting player."));
		}
		
	}
	
	private void handleRegistrationRequest(Message message){
		String actor_name = message.getSender();
		//MessageType type = message.getType();
		String[] arguments = message.getArguments();
		
		String username = arguments[0];
		String password = arguments[1];
		
		try {
			world.addCharacter(new Player(username, "", password, world.getLobby(0)));
			//The below methods must be celled in this order... which is horrible
			world.findPc(username).setLoggedIn(true);
			world.findPc(username).getLocation().addPlayer(world.findPc(username));
			GameEngine.broadcastToRoom(adapter, world.findPc(username).getLocation(), username + " joined the fun :D!", username);
			
		} catch (EntityNotUnique e) {
			adapter.sendMessage(new ErrorMessage(actor_name, "The name " + username + " is taken" ));
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, false, "Username is already taken!"));
			return;
			
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(actor_name, "The lobby does not exist" ));
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, false, "That lobby does not exist!"));
			return;
		}
		
		adapter.sendMessage(new RegistrationReplyMessage(actor_name, true, null));
		return;
	}
	
	/**
	 * This method handles an authentication request(login).<p>
	 * <b>NOTE:</b> this function must send {@link AuthenticationReplyMessage}. 
	 * 
	 * @param message Must be a message of {@literal MessageType#AUTHENTICATION}
	 */
	private void handleLoginRequest(Message message){
		String actor_name = message.getSender();
		String[] arguments = message.getArguments();
		//TODO Fix so that players gets added to the lobby when they log in!
		String username = arguments[0];
		String password = arguments[1];
		Player player = null;
		try {
			player = world.findPc(username);
			if(player.isLogedIn()){
				adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false, "That player is already logged in!"));
				return;
			}
			if(checkUsernamePassword(username, password)){
				player.setLocation(world.getLobby(player.getCs().getLevel()));
				//The below methods must be celled in this order... which is horrible
				player.setLoggedIn(true);
				player.getLocation().addPlayer(player);
				GameEngine.broadcastToRoom(adapter, world.findPc(username).getLocation(), username + " has returned :D!", username);
				adapter.sendMessage(new AuthenticationReplyMessage(actor_name, true, null));
				return;
			}else{
				adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false, "Incorrect username/password!"));
				return;
			}
			
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false, "That username is not registered!"));
			return;
		}
	}
	
	/**
	 * 
	 * Executes the action in the given message type.
	 * <p>
	 * <b>NOTE:</b> This method must always send back at least one message to the sender of the given message.
	 * 
	 * @param message Must be a message of  {@literal MessageType#GENERAL_ACTION} type.
	 */
	private void executeAction(Message message){
		String actor_name = message.getSender();
		String[] arguments = message.getArguments();
		
		Player actor = null;
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
		if(!actor.getLocation().playerPresent(actor)){
			adapter.sendMessage(new SeriousErrorMessage(actor_name, "Actor is not present in his own room!"));
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
				
			case Keywords.EXAMINE:
				See.examine(actor, arguments, world, adapter);
				break;
				
			case Keywords.CS:
				See.cs(actor, world, adapter);
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
				ItemInteraction.unequip(actor, world, adapter);
				break;
			
			case Keywords.ATTACK:
				Combat.attack(actor, arguments, world, adapter);
				break;
				
			case "drop_players":
				for (Player p : world.getPlayers()) {
					System.out.println(p.getName());
				}
				break;
			case "drop_players_room":
				try {
					Room room = world.findRoom(arguments[0]);
					for (Player p : room.getPlayers()) {
						System.out.println(p.getName());
					}
				} catch (EntityNotPresent e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			case "am_i_real":
				System.out.println(actor);
				for (Player p : world.getPlayers()) {
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
	
	/**
	 * 
	 * Broadcasts a {@link NotifactionMesssage} to all the players in the given room
	 * 
	 * @param adapter
	 * @param room
	 * @param type
	 * @param nouns
	 */
	public static void broadcastToRoom(Adapter adapter, Room room, String message){
		for (Player player : room.getPlayers()) {
			adapter.sendMessage( new NotifactionMesssage(player.getName() , message));
		}
	}

	/**
	 * 
	 * This function broadcasts a a {@link NotifactionMesssage} too the players in the given room except for the arguments gilen to the exludes parameter.
	 * 
	 * @param adapter The adapter trough wich the notification is to be sent.
	 * @param room Room in wich the notification is to be broadcasted. 
	 * @param message The message to be sent.
	 * @param excludes The name of all the players to which no message is to be sent!
	 */
	public static void broadcastToRoom(Adapter adapter, Room room, String message,String... excludes){
		
		for (Player player : room.getPlayers()) {
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
	 * A roll of a twenty sided dice.
	 * 
	 * @return A pseudo random number between 1-20
	 */
	public static int d20(){
		return 1+(int)(Math.random()*20);
	}
	
	/**
	 * A roll of a six sided dice
	 * 
	 * @return A pseudo random number between 1-6
	 */
	public static int d6(){
		return 1+(int)(Math.random()*6);
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