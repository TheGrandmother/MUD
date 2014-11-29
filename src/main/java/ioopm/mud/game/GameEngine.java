package ioopm.mud.game;

import ioopm.mud.communication.messages.server.AuthenticationReplyMessage;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.Message;
import ioopm.mud.communication.MessageType;
import ioopm.mud.communication.messages.server.NotificationMessage;
import ioopm.mud.communication.messages.server.RegistrationReplyMessage;
import ioopm.mud.communication.messages.server.SeriousErrorMessage;
import ioopm.mud.exceptions.EntityNotUnique;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.Room;
import ioopm.mud.generalobjects.World;

import java.util.logging.Logger;

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
	
	/**
	 * Handles a logout request.<p>
	 * <b>NOTE:</b> This method does not necessarily send a message back to the sender.
	 * 
	 * 
	 * @param message Must be of type {@literal MessageType#LOGOUT}
	 */
	private void handleLogoutRequest(Message message){
		String actor_name = message.getSender();
		//MessageType type = message.getType();
		String[] arguments = message.getArguments();
		try {
			Player player = world.findPlayer(actor_name);
			player.getLocation().removePlayer(player);
			player.setLoggedIn(false);
			GameEngine.broadcastToRoom(adapter, player.getLocation(), actor_name + " has left the game.");
		} catch (EntityNotPresent e) {
			logger.warning("Unknown player tried to log out! Username: \"" + actor_name + "\"");
			adapter.sendMessage(new SeriousErrorMessage(actor_name, "Tried to log out a non existing player."));
		}
		
	}
	/**
	 * Handles a registration request.<p>
	 * <b>NOTE:</b> This method must send a {@link RegistrationReplyMessage} to the sender.
	 * 
	 * @param message Must be a message of {@literal MessageType#REGISTRATION}
	 */
	private void handleRegistrationRequest(Message message){
		String actor_name = message.getSender();
		//MessageType type = message.getType();
		String[] arguments = message.getArguments();
		if(arguments.length != 2 || arguments == null){
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, false, "Needs both username and a password!"));
			return;
		}
		String username = arguments[0];
		String password = arguments[1];
		
		try {
			world.addCharacter(new Player(username, "", password, world.getLobby(0)));
			//The below methods must be celled in this order... which is horrible
			world.findPlayer(username).setLoggedIn(true);
			world.findPlayer(username).getLocation().addPlayer(world.findPlayer(username));
			GameEngine.broadcastToRoom(adapter, world.findPlayer(username).getLocation(), username + " joined the fun :D!", username);
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, true, null));
			return;
			
		} catch (EntityNotUnique e) {
			adapter.sendMessage(new ErrorMessage(actor_name, "The name " + username + " is taken" ));
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, false, "Username is already taken!"));
			return;
			
		} catch (EntityNotPresent e) {
			adapter.sendMessage(new SeriousErrorMessage(actor_name, "The lobby does not exist" ));
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, false, "That lobby does not exist!"));
			return;
		}

	}
	
	/**
	 * This method handles an authentication request(login).<p>
	 * <b>NOTE:</b> this method must send {@link ioopm.mud.communication.messages.server.AuthenticationReplyMessage} to the sender.
	 * 
	 * @param message Must be a message of {@literal MessageType#AUTHENTICATION}
	 */
	private void handleLoginRequest(Message message){
		String actor_name = message.getSender();
		String[] arguments = message.getArguments();
	
		if(arguments.length != 2|| arguments == null){
			adapter.sendMessage(new RegistrationReplyMessage(actor_name, false, "Needs both username and a password!"));
			return;
		}
		String username = arguments[0];
		String password = arguments[1];
		Player player = null;
		try {
			player = world.findPlayer(username);
			if(player.isLoggedIn()){
				adapter.sendMessage(new AuthenticationReplyMessage(actor_name, false, "That player is already logged in!"));
				return;
			}
			if(checkUsernamePassword(username, password)){
				player.setLocation(world.getLobby(player.getCs().getLevel()));
				//The below methods must be celled in this order... which is horrible
				player.setLoggedIn(true);
				player.getLocation().addPlayer(player);
				GameEngine.broadcastToRoom(adapter, world.findPlayer(username).getLocation(), username + " has returned :D!", username);
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
			if(!world.findPlayer(actor_name).isLoggedIn()){
				adapter.sendMessage(new SeriousErrorMessage(actor_name, "Actor is not logged in!"));
				return;
			}else{
				actor =world.findPlayer(actor_name);
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

			default:
				adapter.sendMessage( new ErrorMessage(actor.getName(), action + " is not a valid keyword!"));
		}
	}
	
	/**
	 * 
	 * Broadcasts a {@link ioopm.mud.communication.messages.server.NotificationMessage} to all the players in the given room
	 * 
	 * @param adapter
	 * @param room
	 * @param type
	 * @param nouns
	 */
	public static void broadcastToRoom(Adapter adapter, Room room, String message){
		for (Player player : room.getPlayers()) {
			adapter.sendMessage( new NotificationMessage(player.getName() , message));
		}
	}

	/**
	 * 
	 * This function broadcasts a a {@link ioopm.mud.communication.messages.server.NotificationMessage} too the players in the given room except for the arguments given to the excludes parameter.
	 * 
	 * @param adapter The adapter trough which the notification is to be sent.
	 * @param room Room in which the notification is to be broadcast.
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
				adapter.sendMessage( new NotificationMessage(player.getName() , message));
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
			return (world.findPlayer(username).checkPassword(password));
		} catch (EntityNotPresent e) {
			//Player not present
			return false;
		}
	}
}