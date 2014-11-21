package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.messages.server.AuthenticationReplyMessage;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
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

	Adapter server;
	World   world;

	public GameEngine(Adapter server, World world) {
		this.server = server;
		this.world = world;
	}
	
	//TODO Implement logging out so that players gets removed from rooms before the log out.
	public void executeAction(Message message) {

		String actor = message.getSender();
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
					server.sendMessage(new AuthenticationReplyMessage(actor, false));
					return;
				}
				if(checkUsernamePassword(username, password)){
					player.setLocation(get_lobby(player));
					player.getLocation().addPlayer(player);
					player.setLoggedIn(true);
					server.sendMessage(new AuthenticationReplyMessage(actor, true));
				}else{
					server.sendMessage(new AuthenticationReplyMessage(actor, false));
					return;
				}
				
			} catch (EntityNotPresent e) {
				server.sendMessage(new AuthenticationReplyMessage(actor, false));
				return;
			}
		}else if(type == MessageType.REGISTRATION) {
			String username = arguments[0];
			String password = arguments[1];
			
			try {
				world.addCharacter(new Pc(username, "", password, get_lobby(null)));
				world.findPc(username).getLocation().addPlayer(world.findPc(username));
				world.findPc(username).isLogedIn();
			} catch (EntityNotUnique e) {
				server.sendMessage(new ErrorMessage(actor, "The name " + username + " is taken" ));
				server.sendMessage(new RegistrationReplyMessage(actor, false));
				return;
				
			} catch (EntityNotPresent e) {
				server.sendMessage(new SeriousErrorMessage(actor, "The lobby does not exist" ));
				server.sendMessage(new RegistrationReplyMessage(actor, false));
				return;
			}

			server.sendMessage(new RegistrationReplyMessage(actor, true));
			return;
			//server.sendMessage(new RegistrationReplyMessage(actor, register(username, password)));
		}
		else if(type == MessageType.GENERAL_ACTION) {
			try {
				if(!world.findPc(actor).isLogedIn()){
					server.sendMessage(new SeriousErrorMessage(actor, "Actor is not logged in!"));
					return;
				}
			} catch (EntityNotPresent e) {
				server.sendMessage(new SeriousErrorMessage(actor, "Actor does not exist!"));
				return;
			}
			
			
			String action = message.getAction();

			switch(action) {

				case Keywords.SAY:
					if(arguments.length != 1){
						server.sendMessage( new ErrorMessage(actor, "Mallformed message :("));
						break;
					}
					Talk.say(actor, arguments[0], world, server);
					break;

				case Keywords.WHISPER:
					if(arguments.length != 2){
						server.sendMessage( new ErrorMessage(actor, "Mallformed message :("));
						break;
					}
					Talk.whisper(actor, arguments[0], arguments[1], world, server);
					break;

				case Keywords.ECHO:
					server.sendMessage( new ReplyMessage(actor,"echo_reply", arguments));
					break;

				case Keywords.LOOK:
					See.look(actor, world, server);
					break;

				case Keywords.MOVE:
					Movement.move(actor, arguments, world, server);
					break;

				case Keywords.TAKE:
					ItemInteraction.Take(actor, arguments, world, server);
					break;

				//TODO implement drop action
				case Keywords.DROP:
					ItemInteraction.drop(actor, arguments, world, server);
					break;

				//TODO implement inspect inventory action

				case "drop_players":
					for (Pc p : world.getPlayers()) {
						System.out.println(p.getName());
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
					server.sendMessage( new ErrorMessage(actor, action + " is not a valid keyword!"));
			}
		}
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
	private Room get_lobby(Pc player) throws EntityNotPresent{
		if(player == null){
			return world.findRoom("room1");
		}else{
			//TODO
			//wrtie an actual usefull thing here.
			return world.findRoom("room1");
		}
	}

	/**
	 * Returns true if user exists and has the correct passowrd..
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