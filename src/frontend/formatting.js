


	function Message(message){
		
		split_array = message.split(";");
		this.receiver = split_array[0];
		this.sender = split_array[1];
		this.type = split_array[2];
		this.action = split_array[3];
		this.time_stamp = split_array[4];

		this.args = [];
	
		for(i = 5; i < split_array.length; i++){
			this.args.push(split_array[i]);
		}

	}

	function basicMessageFormat(message){
		str = "Message:<br>";
		str += ugly_tab+"Type: " + message.type + "<br>";
		str += ugly_tab+"Action: " + message.action + "<br>";
		str += ugly_tab+"Time Stamp: " + message.time_stamp + "<br>";
		str += ugly_tab+"Arguments: " + "<br>";
		if(message.args.length == 0){
			str += ugly_tab+ugly_tab+"NONE" + "<br>";
		}else{
			for(i = 0; i < message.args.length; i++){
				str += ugly_tab+ugly_tab+message.args[i] + "<br>";
			}
		
		}

		return str;
		
	}


function makeRequest(action_,args_,sender_){
	return message = {
		receiver:"server",
		sender:sender_,
		type:"GENERAL_ACTION",
		action:action_,
		time_stamp:""+ new Date().getTime(),
		args:args_.join(";")
	};	
}

function formatToSend(m){

	str = m.receiver + ";" + m.sender + ";" + m.type + ";" + m.action + ";" + m.time_stamp + ";";
	if(m.args == []){
		return str;
	}else{
		return str + m.args + ";";
	}

}


function formatLookReply(message){

	appendToOut("<b>"+message.args[0] +"</b>");
	appendToOut(message.args[1]);
	exits = message.args[2];
	appendToOut("Exits are: " + exits);
	appendToOut("Users here are: " + message.args[3]);

}





