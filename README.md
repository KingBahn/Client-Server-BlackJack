# Client-Server-BlackJack
BlackJack that utilizes the client server model to allow multiple players to play blackjack together.

This project includes 3 .java files. blackJackSingleplayer is as the name implies only for one person. I created this file before working on the client server version to make
my life easier when I added the networking portion.

blackJackServer is the server that the clients will connect to. When it is run you must provide it with a port for the server to begin listening for clients on.

blackJackClient is the clients that will connect to the server. Multiple people can be simulated by running the client file multiple times. When the client file is run
it will require the host(the IP of the host), the port, and finally a name to differentiate the players.
