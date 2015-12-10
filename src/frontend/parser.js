function parseInput(inp){

	trim = 	inp.trim();
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

		default:
			appendToOut("Pointless input");
			return null;
	}

}
