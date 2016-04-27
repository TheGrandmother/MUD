jQuery(window).bind('beforeunload', function(){
  return 'IF YOU NAVIGATE AWAY FROM THE GAME YOU WILL BE LOGGED OUT!';
});

var heart = setInterval(function (){
  if(!connection_established){return;}
  socket.send(formatToSend(heartbeatMessage()));
},5000)

user = "";
socket = new WebSocket("ws://46.101.109.56:1337");

handshake_complete = false;
connection_established = false;

attempting_registration = false;
attempting_login = false;

username_entered = false;
password_enetered = false;

is_banned = false;

socket_open = false;

input_mode = "NORMAL"

socket.onopen= function (){
  console.log("started connection");
  appendToOut("Connected to server.");
  socket_open = true;

}

socket.onerror = function (ev){
  console.log("Connection error!" + ev);
  appendToOut("Connetion error!!");
}


socket.onclose = function (){
  console.log("Connection Terminatd!");
  appendToOut("Connetion terminated!!");
}

socket.onmessage = function (env){
  data = env.data;
  msg = new Message(data);
  console.log(msg.args);
  switch(msg.type){
    case "HANDSHAKE_REPLY":
      console.log("recieved handshake reply");
      console.log(msg.args);
      if(msg.args[0]=="true"){
        handshake_complete = true;
        console.log("Handshake reply confirmed.")
      }else{
        console.log("handshake failed. attempting login");
      }
      break;
    case "REGISTRATION_REPLY":
      console.log("Received registration reply");
      if(msg.args[0]=="true"){
        connection_established = true;
        console.log("Registartion complete");
        appendToOut("Welcome to the game :D")

      }else{
        console.log("Registration failed");
        appendToOut("Registration failed: " + msg.args[1]);
        appendToOut("Please type register to register a new account or login to log in with an existing accout.");
        attempting_registration = false;
        username_entered = false;
        password_enetered = false;

      }
      break;
    case "AUTHENTICATION_REPLY":
      console.log("Received registration reply");
      if(msg.args[0]=="true"){
        connection_established = true;
        console.log("Login complete");
        appendToOut("Welcome to the game :D")

      }else{
        console.log("Login failed");
        appendToOut("Login failed: " + msg.args[1]);
        appendToOut("Please type register to register a new account or login to log in with an existing accout.");
        attempting_login = false;
        username_entered = false;
        password_enetered = false;
      }
      break;
    case "GENERAL_REPLY":
      switch (msg.action){
          case "look_reply":
            appendToOut( formatTime(msg.time_stamp)+ formatLookReply(msg));
            break;
          case "move_reply":
            appendToOut( formatTime(msg.time_stamp)+ formatMoveReply(msg));
            break;
          case "say":
            appendToOut(formatTime(msg.time_stamp)+formatSayReply(msg));
            break;
          case "whisper_reply":
            appendToOut(formatTime(msg.time_stamp)+formatWhisperReply(msg));
            break;
          case "inventory_reply":
            appendToOut(formatTime(msg.time_stamp)+formatInventoryReply(msg));
            break;
          case "take_reply":
            appendToOut(formatTime(msg.time_stamp)+formatTakeReply(msg));
            break;
          case "examine_reply":
            appendToOut(formatTime(msg.time_stamp)+formatExamineReply(msg));
            break;
          case "cs_reply":
            appendToOut(formatTime(msg.time_stamp)+formatCsReply(msg));
            break;
          case "equip_reply":
            appendToOut(formatTime(msg.time_stamp)+formatEquipReply(msg));
            break;
          case "unequip_reply":
            appendToOut(formatTime(msg.time_stamp)+formatUnequipReply(msg));
            break;
          case "attack_reply":
            appendToOut(formatTime(msg.time_stamp)+formatAttackReply(msg));
            break;
          case "drop_reply":
            appendToOut(formatTime(msg.time_stamp)+formatDropReply(msg));
            break;
          case "admin_reply":
            appendToOut(formatTime(msg.time_stamp)+formatAdminReply(msg));
            break;
          case "admin_logout":
            is_banned = true;
            appendToOut(formatTime(msg.time_stamp)+formatAdminLogout(msg));
            break;
          default:
            appendToOut(basicMessageFormat(msg));
            break;
      }
      break;
    case "NOTIFICATION":
      appendToOut(formatTime(msg.time_stamp)+formatNotification(msg));
      break;
    case "GENERAL_ERROR":
      appendToOut(formatTime(msg.time_stamp)+formatError(msg));
      break;
    case "HEARTBEAT_REPLY":
      //console.log("recieved heartbeat reply");
      break;
    default:
      appendToOut(basicMessageFormat(msg));

  }
}

function onInput(key_code){
  switch (key_code){
    case 13:
      val =  document.getElementById("input").value;

      if(!socket_open){ console.log("connection not established"); return;}

      if(!connection_established){
        authenticate();
      }else{
        msg =  parseInput(val);
        if(msg != null){
          console.log("Raw message: "  +  msg);
          msg = formatToSend(msg);
          console.log("Sending message: " + msg);
          socket.send(msg);
          document.getElementById("input").value = "";

        }
      }
      break;
    case 27:
      if (input_mode == "TALK"){
        input_mode = "NORMAL";
        document.getElementById("mode").innerHTML = "NORMAL";
      }else{
        input_mode = "TALK";
        document.getElementById("mode").innerHTML = "TALK";
      }
  }
}

function authenticate(){
  if(!attempting_registration && !attempting_login){
    switch (val){
      case "register":
        appendToOut("Type in your username.");
        console.log("started registration");
        attempting_registration = true;
        document.getElementById("input").value = "";
        return;
      case "login":
        appendToOut("Type in your username.");
        console.log("started login");
        attempting_login = true;
        document.getElementById("input").value = "";
        return;
    }
  }

  if(attempting_registration || attempting_login){
    if(!username_entered){
      if(val.length < 3){
        appendToOut("Username needs to be 3 characters or longer.");
        return;
      }

      console.log("Entered username")
      user = val;
      username_entered = true;
      document.getElementById("input").value = "";
      socket.send(window.btoa("server")+";"+window.btoa(user)+";HANDSHAKE;null;"+new Date().getTime()+";");
      appendToOut("Please enter password");
      $("#input").attr("type", "password");
      return;
    }

    if(username_entered && handshake_complete && !password_enetered){
      if(val.length < 4){
        appendToOut("Password needs to be 4 characters or longer.");
        return;
      }

      console.log("Entered password");
      $("#input").attr("type", "text");
      if(attempting_registration){
        socket.send(window.btoa("server")+";"+window.btoa(user)+";REGISTRATION;null;"+new Date().getTime()+";"+window.btoa(user)+";"+window.btoa(val)+";");
      }else if(attempting_login){
        socket.send(window.btoa("server")+";"+window.btoa(user)+";AUTHENTICATION;null;"+new Date().getTime()+";"+window.btoa(user)+";"+window.btoa(val)+";");
      }else{
        appendToOut("Serious oddness during registration/login");
      }
      password_enetered = true;
      document.getElementById("input").value = "";
      return;
    }

    console.log("registration caused oddness");

  }
}

function look(){
  msg = formatToSend(new makeRequest("look",[],user));
  console.log("sending message: " + msg);
  socket.send(msg);
}

function appendToOut(text){
  out = document.getElementById("output");
  var isScrolledToBottom = out.scrollHeight - out.clientHeight <= out.scrollTop + 1;
  //Anny additions to the output must happen below this line and before the if statement.
  //otherwise the autoscrolling wont work.
  out.innerHTML += unescape(text) + "<br>";

  if(isScrolledToBottom){
    out.scrollTop = out.scrollHeight - out.clientHeight;
  }
}

function sleepFor( sleepDuration ){
    var now = new Date().getTime();
    while(new Date().getTime() < now + sleepDuration){ /* do nothing */ }
}





