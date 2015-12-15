function parseInput(inp){

	inp = 	inp.trim();

	if(inp == ""){
		return null;
	}

	if(inp.charAt(0)==":"){
		inp = inp.substr(1);
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
