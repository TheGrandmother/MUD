function parseInput(inp){

  inp =   inp.trim();

  if(inp == ""){
    return null;
  }

  if(is_banned){
     appendToOut("You are banned... Deal with it!");  
    return null;
  }


  if(input_mode=="NORMAL"){
    action = inp.split(" ")[0].toLowerCase();
    switch(action){
    
      case "look":
        return makeRequest("look",[],user);
        
        break;

      case "say":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to say: " + arg);
        return makeRequest("say",[arg],user);
        break;
      
      case "move":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to move: " + arg);
        return makeRequest("move",[arg],user);
        break;

      case "whisper":
        recipient = inp.split(" ")[1];
        msg = inp.split(" ").slice(2).join(" ");
        console.log("Trying to wisper: \""+ msg + "\" to: " + recipient );
        return makeRequest("whisper",[recipient,msg],user);

      case "take":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to take: " + arg);
        return makeRequest("take",[arg],user);
        break;

      case "drop":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to drop: " + arg);
        return makeRequest("drop",[arg],user);
        break;
      
      case "attack":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to attack: " + arg);
        return makeRequest("attack",[arg],user);
        break;
      
      case "equip":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to equip: " + arg);
        return makeRequest("equip",[arg],user);
        break;

      case "examine":
        arg = inp.split(" ").slice(1).join(" ");
        console.log("trying to examine: " + arg);
        return makeRequest("examine",[arg],user);
        break;
      
      case "_make_admin_":
        arg = inp.split(" ").splice(1);
        hashed_pass = forge_sha256(arg[0]);
        time_stamp = new Date().getTime();
        salted_hash = forge_sha256(hashed_pass + "" + time_stamp);
        arg[0] = salted_hash;
        return makeAdminAction("make_admin",[arg[0]],user,time_stamp);
        
        break;

      case "_ban_":
        
        arg = inp.split(" ").splice(1);
        console.log("Trying to ban " + arg[0]);
        
        return makeAdminAction("ban",[arg[0]],user, new Date().getTime());
        break;
      
      case "_mute_":
        
        arg = inp.split(" ").splice(1);
        console.log("Trying to mute " + arg[0]);
        
        return makeAdminAction("mute",[arg[0]],user, new Date().getTime());
        break;

      case "_un_mute_":
        
        arg = inp.split(" ").splice(1);
        console.log("Trying to un mute " + arg[0]);
        
        return makeAdminAction("un_mute",[arg[0]],user,new Date().getTime());
        break;
        
      case "_kick_":
        
        arg = inp.split(" ").splice(1);
        console.log("Trying to kick " + arg[0]);
        
        return makeAdminAction("kick",[arg[0]],user,new Date().getTime());
        break;

      case "_teleport_":
        
        arg = inp.split(" ").splice(1);
        console.log(arg);
        arg= arg.join("").split("_to_")
        console.log(arg);
        console.log("Trying to teleport " + arg[0] + " to " + arg[1]);
        
        return makeAdminAction("kick",[arg[0]],user,new Date().getTime());
        break;

      case "_broadcast_":
        
        arg = inp.split(" ").splice(1).join(" ");
        console.log("broadcasting");
        
        return makeAdminAction("broadcast",[arg],user,new Date().getTime());
        break;

      case "cs":
        return makeRequest("cs",[],user);
        
        break;

      
      case "inventory":
        return makeRequest("inventory",[],user);
        
        break;
      
      case "unequip":
        return makeRequest("unequip",[],user);
        
        break;

      case "quit":
      case "logout":
        appendToOut("You have left the game :/");
        return logoutMessage();

        break;
      
      default:
        appendToOut("Pointless input");
        return null;
    }
  }else{
    return makeRequest("say",[inp],user);
  }

}
