package ioopm.mud.game;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.Item;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**Class for handling admin actions
 *
 */
public abstract class Admin {

				/**Main admin action handler.
				 *
				 *Dispatches actions to the appropriate sub methods if neededd.
				 *
				 *@param actor Who wants to do adminy thing
				 *@param action What kind of adminny things are to be done
				 *@param arguments The arguments to the admin action
				 *@param time_stamp the timestamp of the message. Needed for hashing.
				 *@param world Wher the admin does his adminy things
				 *@param adapter Trough wich adminy things gets told
				 *@param logger For logging adminny activity
				 */
				public static  void action(Player actor, String action, String[] arguments, long time_stamp, World world, Adapter adapter, Logger logger){
					
					if(action == null || action.equals("") || arguments.length < 1){
						adapter.sendMessage(new ErrorMessage(actor.getName(),"Malformed admin command."));
						return;
					}

					if(action.equals("make_admin")){
						
						if(actor.isAdmin()){
							adapter.sendMessage(new ErrorMessage(actor.getName(),"You are already admin."));
							return;
						}
						validatePrivelegeEscalation(actor, arguments[0], time_stamp, adapter, logger);

					}else{
						adapter.sendMessage(new ErrorMessage(actor.getName(),"Unrecognised admin action."));
						return;
						
					}
				
				}

				
				/**Validates an attempt to become admin.
				 *
				 *Will compare the hash recieved with the hash in the  in the adminpass file. It will
				 *salt the hash in the adminpass file with the timestamp of the message and then hash it again!
				 *
				 *@param actor Who is to be admin
				 *@param hash The hash sent from the client
				 *@param time_stamp The timestamp of the message with the make_admin request
				 */
				private static void validatePrivelegeEscalation(Player actor, String hash, long time_stamp, Adapter adapter, Logger logger){

					String local_hash;

					try {
						BufferedReader br = new BufferedReader(new FileReader("adminpass"));
						StringBuilder sb = new StringBuilder();
						local_hash = br.readLine();

				    br.close();

					}catch (FileNotFoundException e){
						adapter.sendMessage(new ErrorMessage(actor.getName(),"No admin password found on the server."));
						logger.severe("No adminpassword file found on the server!");
						return;
					
					}catch (IOException e){
						adapter.sendMessage(new ErrorMessage(actor.getName(),"Exception while reading password file."));
						logger.severe("IOException while trying to read adminpass file!");
						return;
					} 
					
          String local_hash_salted = local_hash + time_stamp;
					byte[] digest;
					try{
					MessageDigest md = MessageDigest.getInstance("SHA-256");

					md.update(local_hash_salted.getBytes("UTF-8")); // Change this to "UTF-16" if needed
					digest = md.digest();
					}catch(NoSuchAlgorithmException e){
						
						adapter.sendMessage(new ErrorMessage(actor.getName(),"No such algorithm error occured on the server."));
						logger.severe("No such alogrithm exception occured when hasing stuff!");
						return;
					
					}catch (UnsupportedEncodingException e){
					
						adapter.sendMessage(new ErrorMessage(actor.getName(),"UnsupportedEncodingException occured on the server side."));
						logger.severe("UnsupportedEncodingException occured!");
						return;
						
					}

					String mathching_hash = String.format("%064x", new java.math.BigInteger(1, digest));
					logger.fine("Hashed local to: " + mathching_hash );
					logger.fine("Matching with remote hash: " + hash);

					if(hash.equals(mathching_hash)){
						logger.fine("Hashes match. " + actor.getName() + " is now admin.");
						adapter.sendMessage(new ReplyMessage(actor.getName(), "admin_reply", "You are now admin!"));
						actor.setAdmin(true);
						return;
					}else{
						adapter.sendMessage(new ErrorMessage(actor.getName(),"Wrong admin password!"));
						logger.severe("Wrong admin password!");
						return;
					
					}
				

				}	

}

