
  ugly_tab = "&nbsp;&nbsp;&nbsp;&nbsp;";	
	time_stamp_offs = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	
	function Message(message){
		
		split_array = message.split(";");
		this.receiver = window.atob(split_array[0]);
		this.sender = window.atob(split_array[1]);
		this.type = split_array[2];
		this.action = split_array[3];
		this.time_stamp = split_array[4];

		this.args = [];
	
		for(i = 5; i < split_array.length; i++){
			this.args.push(window.atob(split_array[i]));
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
		args:args_
	};	
}

function formatToSend(m){

	str = window.btoa(m.receiver) + ";" + window.btoa(m.sender) + ";" + m.type + ";" + m.action + ";" + m.time_stamp + ";";

	console.log(m.args + " mvkf : " + m.args.length);

	//Okay..... why can't i just check for empty array..
	if(m.args == [] || m.args.length == 0){
		return str;
	}else{
		return str + (m.args).map(window.btoa).join(";")+";";
	}

}

//TODO Add all arguments!!
function formatLookReply(message){

	str = ("<b>"+message.args[0] +"</b><br>");
	str +=time_stamp_offs + (message.args[1])+"<br>";
	str +=time_stamp_offs + "Exits are: " + message.args[2] + "<br>";
	str +=time_stamp_offs + "Users here are: " + message.args[3];
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

function formatMoveReply(message){
	str = "<b>"+message.args[0]+" </b>";
	return str;
}

function formatNotification(message){
	str = "<b>"+message.args[0]+" </b>";
	return str;
}

function formatWhisperReply(message){
	str = "<b>"+message.args[0]+": </b><i>"+message.args[1]+"</i>";
	return str;

}






