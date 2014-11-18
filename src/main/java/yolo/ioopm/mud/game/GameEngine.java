package yolo.ioopm.mud.game;

import yolo.ioopm.mud.communication.Adapter;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.MessageType;
import yolo.ioopm.mud.communication.messages.server.AuthenticationReplyMessage;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.RegistrationReplyMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.Pc;
import yolo.ioopm.mud.generalobjects.World;

/**
 * This class is responsible for handling the interpreting of the actions passed to the server.
 *
 * @author TheGrandmother
 */
public class GameEngine {

	Adapter server;
	World   world;

	public GameEngine(Adapter server, World world) {
		this.server = server;
		this.world = world;
	}

	public void executeAction(Message message) {

		String actor = message.getSender();
		MessageType type = message.getType();
		String[] arguments = message.getArguments();

		if(type == MessageType.AUTHENTICATION) {
			String username = arguments[0];
			String password = arguments[1];

			server.sendMessage(new AuthenticationReplyMessage(actor, checkUsernamePassword(username, password)));
		}
		else if(type == MessageType.REGISTRATION) {
			String username = arguments[0];
			String password = arguments[1];

			server.sendMessage(new RegistrationReplyMessage(actor, register(username, password)));
		}
		else if(type == MessageType.GENERAL_ACTION) {
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
	 * 
	 * Returns true if user exists, password is correct and user is still logged in.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean checkUsernamePassword(String username, String password) {
		//TODO returnerar sant om användarnamn och lösen stämmer med sparad data
		//TODO denna bör även retunera false om användaren redan är inloggad
		for (Pc pc : world.players) {
			if(pc.getName().equals(username) && pc.checkPassword(password)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Registers the user at the server.
	 *
	 * @param username
	 * @param password
	 * @return true if successfull, else false
	 */
	public boolean register(String username, String password) {
		// TODO registrerar användaren på servern, returnerar sant om det gick, annars falskt
		return false;
	}
}
























