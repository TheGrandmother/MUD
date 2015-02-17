# MUD
This is the worlds most pointless MUD.

##Building

Bulding the MUD requires Maven.
Bulding it is then done by running `mvn` in the projects root folder.
The runnable file will then be `target/MUD.jar`.

##Running

The program can be started as a client by running the following command in a terminal:
`java -jar MUD.jar client`

It can be started as a server by running this command:
`java -jar MUD.jar server`

###Client
As soon as the client has loaded it will ask for a server address to connect to and a username to use for
said server. When these two has been given to the client it attempts to connect to the given server 
address.

After a successful connection has been established you will be able play the game to its full extent by
entering the commands given in the menu bar.

###Server
When starting the server it will attempt to bind to port 1337, if this port is already in use it will not launch
and display an error message.

##Goals completed using this program
 - 