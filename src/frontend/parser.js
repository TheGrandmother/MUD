function parseInput(inp){

	inp = 	inp.trim();
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

			default:
				appendToOut("Pointless input");
				return null;
		}
	}else{
		return makeRequest("say",[inp],user);
	}

}
