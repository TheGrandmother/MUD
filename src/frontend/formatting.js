


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

	str = ("<b>"+message.args[0] +"</b><br>");
	str += (message.args[1])+"<br>";
	str += "Exits are: " + message.args[2] + "<br>";
	str += "Users here are: " + message.args[3];
	return str;

}

function formatTime(stamp){
 var date = new Date(parseInt(stamp));
 var hours = date.getHours();
 var minutes = "0" + date.getMinutes();
 var seconds = "0" + date.getSeconds();
 return  "["+hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2)+"] ";

}

function formatSayReply(message){
	str = "<b>"+message.args[0]+": </b>"+message.args[1];
	return str;
}



