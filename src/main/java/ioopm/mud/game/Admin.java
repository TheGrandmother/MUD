package ioopm.mud.game;

import ioopm.mud.communication.Adapter;
import ioopm.mud.communication.messages.server.ErrorMessage;
import ioopm.mud.communication.messages.server.SeriousErrorMessage;
import ioopm.mud.communication.messages.server.ReplyMessage;
import ioopm.mud.communication.messages.server.NotificationMessage;
import ioopm.mud.exceptions.EntityNotPresent;
import ioopm.mud.generalobjects.Item;
import ioopm.mud.generalobjects.Player;
import ioopm.mud.generalobjects.World;
import ioopm.mud.generalobjects.Room;
import ioopm.mud.game.GameEngine;

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

    }

    if(!actor.isAdmin()){
      adapter.sendMessage(new ErrorMessage(actor.getName(),"You are not admin! Please go away!"));
      return;

    } else if(action.equals("ban")){
      banPlayer(actor, arguments[0], logger, world,adapter);
      return;

    }else if(action.equals("un_ban")){
      unbanPlayer(actor, arguments[0], logger, world,adapter);
      return;

    }else if(action.equals("broadcast")){
      broadcastToAll(actor, String.join(" ", arguments), logger, world, adapter);
      return;

    }else if(action.equals("mute")){
      mutePlayer(actor, arguments[0], logger, world, adapter);
      return;

    }else if(action.equals("un_mute")){
      unMutePlayer(actor, arguments[0], logger, world, adapter);
      return;
    }else if(action.equals("kick")){
      kickPlayer(actor, arguments[0], logger, world, adapter);
      return;
    }else if(action.equals("teleport")){
      teleportPlayer(actor, arguments[0],arguments[1], logger, world, adapter);
      return;
    }
  }


	private static void teleportPlayer(Player actor, String teleportee_name, String destination_name, Logger logger, World world, Adapter adapter){
		Player teleportee = null;
		try{
			teleportee = world.findPlayer(teleportee_name);
		}catch (EntityNotPresent e){
			adapter.sendMessage(new ErrorMessage(actor.getName(),
						"You are trying to teleport a player that does not exist!"))	;
			return;
		}

		Room destination = null;
		try{
			destination = world.findRoom(destination_name);
		}catch (EntityNotPresent e){
			adapter.sendMessage(new ErrorMessage(actor.getName(),
						"You are trying to teleport a player into a room that does not exist!"))	;
			return;
		}
		
		if (destination != teleportee.getLocation()){
			boolean self = teleportee == actor;
			Room current_room = teleportee.getLocation();
			try{

				current_room.removePlayer(teleportee);
				destination.addPlayer(teleportee);
				teleportee.setLocation(destination);
				adapter.sendMessage(new NotificationMessage(teleportee.getName(),self ?
							"With your great administrative powers you tear a hole in the spacetime continuum and "+
							"teleport yourself into " + destination.getName()+".":
							"You feel a tingling sensation in your ears, the world goes all sparkly and you find "+
							"yourself in " + destination.getName()+"."));
				GameEngine.broadcastToRoom(adapter,destination,teleportee.getName() + 
							" appeared in the room from nowhere. You suspect that the great administrative powers has"+
							" something to do with this....",teleportee.getName());
			if (!self){
				adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,
							"You successfully teleported " +teleportee.getName()+" to " +destination.getName() +"."));
			}

			}catch (EntityNotPresent e){
				adapter.sendMessage(new SeriousErrorMessage(actor.getName(),
							"The teleportee has dissapeared from the room he was in!"));
				return;
			}

		}else{
		
			adapter.sendMessage(new ErrorMessage(actor.getName(),
						"No point teleporting a player to the room he is currently in."))	;
			return;
		}

	}

  private static void broadcastToAll(Player actor, String message, Logger logger, World world, Adapter adapter){
    for (Room room: world.getRooms()){
    	GameEngine.broadcastToRoom(adapter, room, message);
    }
  }
  
  private static void mutePlayer(Player actor, String mutee_name, Logger logger,World world, Adapter adapter){

      Player mutee = null;
      try{
        mutee = world.findPlayer(mutee_name);

        if(mutee.isMuted()){

        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to mute a player that is already muted!"));
        return;

        }else{
          mutee.setMuted(true);
          adapter.sendMessage(new ReplyMessage(mutee.getName(),Keywords.ADMIN_REPLY,"Congratulations, you have been muted. Just like in the matrix where agent Smith goes all like \"What good is a phone call if you are unable to speak.\" and Neos mouth gets like all strange and stuff."));
        
          adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,mutee.getName() + " has been muted"));
          logger.fine(actor.getName() + "has muted" + mutee.getName());
          return;

        }

      }catch (EntityNotPresent e){
            
        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to mute a player which does not exist!"));
        return;

      }
  }

  private static void unMutePlayer(Player actor, String mutee_name, Logger logger,World world, Adapter adapter){

      Player mutee = null;
      try{
        mutee = world.findPlayer(mutee_name);

        if(!mutee.isMuted()){

        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to un mute a player that not muted!"));
        return;

        }else{
          mutee.setMuted(false);
        
          adapter.sendMessage(new ReplyMessage(mutee.getName(),Keywords.ADMIN_REPLY,"Congratulations. You are no longer muted. Unfortunately for the rest of us, you can chat openly now."));
          adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,mutee.getName() + " has been muted"));
          logger.fine(actor.getName() + "has un muted" + mutee.getName());
          return;

        }

      }catch (EntityNotPresent e){
            
        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to mute a player which does not exist!"));
        return;

      }
  }
  private static void unbanPlayer(Player actor, String bannee_name, Logger logger,World world, Adapter adapter){

      Player bannee = null;
      try{
        bannee = world.findPlayer(bannee_name);

        if(!bannee.isBanned()){

        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to un ban a player that is not banned!"));
        return;

        }else{
          bannee.setBanned(false);
        
          adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,bannee.getName() + " is no longer banned!"));
          logger.fine(actor.getName() + "has un banned" + bannee.getName());
          return;

        }

      }catch (EntityNotPresent e){
            
        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to un ban a player which does not exist!"));
        return;

      }
  }

	private static void kickPlayer(Player actor, String kicked_name, Logger logger, World world, Adapter adapter){
		Player kicked = null;
		try{
			kicked  = world.findPlayer(kicked_name);
		}catch (EntityNotPresent e){
			adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to kick a player that does not exist."));
			return;
		}
		
		if(kicked.isLoggedIn()){
			adapter.sendMessage(new ReplyMessage(kicked.getName(),"You are being kicked :)"));

			logger.fine(actor.getName() + " kicked " + kicked.getName() + " like a boss!");
			GameEngine.logoutPlayer(kicked.getName(),world,adapter);
			
			adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,kicked.getName() + " is now banned!"));
			return;

		}else{
			adapter.sendMessage(new ErrorMessage(actor.getName(),kicked.getName() + " is already loged out."));
		}

	}

  private static void banPlayer(Player actor, String bannee_name, Logger logger,World world, Adapter adapter){
      
      Player bannee = null;
      try{
        bannee = world.findPlayer(bannee_name);
      }catch (EntityNotPresent e){
            
        adapter.sendMessage(new ErrorMessage(actor.getName(),"You are trying to ban a player which does not exist!"));
        return;

      }

      if(bannee.isBanned()){

        adapter.sendMessage(new ErrorMessage(actor.getName(),bannee.getName() + " is allready banned!"));
        return;

      }

      if(bannee.isLoggedIn()){
        adapter.sendMessage(new ReplyMessage(bannee.getName(),Keywords.ADMIN_LOGOUT,"Congratulations you are being banned :D Please go home and reconsider your life choises."));
        bannee.setBanned(true);
        logger.fine(actor.getName() + " banned " + bannee.getName() + " like a boss!");
        GameEngine.logoutPlayer(bannee.getName(),world,adapter);
        
        adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,bannee.getName() + " is now banned!"));
        return;

      }else{
        bannee.setBanned(true);

        adapter.sendMessage(new ReplyMessage(actor.getName(),Keywords.ADMIN_REPLY,bannee.getName() + " is now banned!"));
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
