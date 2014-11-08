package yolo.ioopm.mud.game;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

import yolo.ioopm.mud.Server;
import yolo.ioopm.mud.communication.Message;
import yolo.ioopm.mud.communication.messages.server.ErrorMessage;
import yolo.ioopm.mud.communication.messages.server.ReplyMessage;
import yolo.ioopm.mud.generalobjects.World;
import yolo.ioopm.mud.generalobjects.World.EntityNotPresent;
import yolo.ioopm.mud.generalobjects.World.EntityNotUnique;

/**
 * This class is responsible for handling the interpreting of the actions passed to the server.
 *
 * @author TheGrandmother
 */
public class GameEngine {


	Server server;
	World world;

	public GameEngine(Server server, World world) {
		// TODO Auto-generated constructor stub
		this.server = server;
		this.world = world;
		
	}
	public void executeAction(Message message) {

		String actor = message.getSender();
		String action = message.getAction();
		String[] arguments = message.getArguments();

		switch(action) {
			case Keywords.MAGIC_MOVE:
				break;
			
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
				
			default:
				server.sendMessage( new ErrorMessage(actor, action + " is not a valid keyword!"));
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
	public static boolean checkUsernamePassword(String username, String password) {
		//TODO returnerar sant om användarnamn och lösen stämmer med sparad data, OBS! måste vara trådsäker
		//TODO denna bör även retunera false om användaren redan är inloggad
		return true;
	}
}
























