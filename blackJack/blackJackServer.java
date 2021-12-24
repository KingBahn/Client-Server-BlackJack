package blackJack;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class blackJackServer
{
	//Keeping track of which thread is pertaining to what player and how many there are
	int totalPlayers = 0;
	int playerNumber = 1;
	private Socket client = null;
    private ServerSocket server = null;
    //Create a list of client threads so we can communicate with them
	ArrayList<ThreadHandler> threadList = new ArrayList<>();
	static ArrayList<String> userList = new ArrayList<>();
	
	static HashMap<Integer, String> deck = new HashMap<Integer, String>(); 
	static HashMap<Integer, String> dealerHand = new HashMap<Integer, String>();
	static boolean roundOver = false;
	static int activePlayer = 1;
	
	
    public blackJackServer(int port)
    {
    	try 
    	{
    		//Creating the main deck that all the threads will modify
    		deck = createDeck();
    		//Creating the dealers hand
    		dealerHand = createHand(deck);
    		//Pruning out the cards we just added to the dealer's hand so the cards are actually removed from the deck
    		deck = pruneDeck(dealerHand, deck);
	    	server = new ServerSocket(port);
	    	//Closes the port at the end of the program so it can be reused immediately
	    	//otherwise the used port might be busy the next time the program is run
	    	server.setReuseAddress(true);
	    	System.out.println("Server created on port " + port);
	    	while(true)
	    	{
	    		//for now 3 players in blackjack for testing purposes
	    		if(totalPlayers < 5)
	    		{
		    		client = server.accept();
		    		//System.out.printf("%s%n",deck.get(1));
		    		ThreadHandler individualThread = new ThreadHandler(client, threadList, playerNumber);
		    		//Adding the current client thread to our list so we can keep track of it
		    		threadList.add(individualThread);
		    		//Creating an individual thread to handle the client that just connected
		    		new Thread(individualThread).start();
		    		//System.out.print(java.lang.Thread.activeCount());
		    		//Ups the player number after a connection was formed 
		    		playerNumber += 1;
		    	}

	    	}
    	}
    	
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		try 
    		{	
	    		if(server != null)
	    		{
	            	server.close();
	    		}
    		}
    		catch(Exception e)
    		{
    			//e.printStackTrace();
    		}
    	}
    	
    }
    
	public static void main(String args[])
	{	
		//We use a scanner to get a custom port at runtime
		Scanner myScanner = new Scanner(System.in);
		System.out.print("Enter your port");
		int currentPort = myScanner.nextInt();
		//We then pass that user inputed port to the ThreadedServer method
		blackJackServer server = new blackJackServer(currentPort);
	}

	
	private static class ThreadHandler implements Runnable
	{
		private final Socket clientSocket;
		private ArrayList<ThreadHandler> threadList;
		private int playerNum;
		//we declare the printwriter outside of run so we can reference it in
		//our printToEveryClient method
		private PrintWriter out;
	    
		public ThreadHandler(Socket socket, ArrayList<ThreadHandler> threads, int playerNumber)
		{
			this.clientSocket = socket;
			this.threadList = threads;
			this.playerNum = playerNumber;
		}
		
		@Override
		public void run()
		{
			boolean myTurn = false;
			int roundTotal = 0;
			String clientOutput;
			HashMap<Integer, String> playerHand = new HashMap<Integer, String>();
		    BufferedReader in = null;
		    
		    out = null;
		    int firstText = 0;
		    String userName = "empty";
		    try
		    {
		    	in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    	out = new PrintWriter(clientSocket.getOutputStream(), true);
		    	//As long as the result is not bye continue listening for client input
		    	out.printf("%n%s%n","Welcome to the server, you are player " + playerNum);
		    	while(true)
		    	{
	    			if(roundOver == true)
   					{
   						out.printf("%s","The dealer's hand was: " );
   						for (Integer key: dealerHand.keySet())
   						{
   				            out.print(dealerHand.get(key) + " ");
   				        }
   						out.printf("%n%s","Dealer Total = "  + checkTotal(dealerHand));
   						while(checkTotal(dealerHand) <= 16 )
   						{	
   							out.printf("%n%s","The dealer has to hit");
   							dealerHand = hit(dealerHand, deck);
   							deck = pruneDeck(dealerHand, deck);
   							out.printf("%n%s","The dealer's hand is now: ");
   							for (Integer key: dealerHand.keySet())
   							{
   					            out.print(dealerHand.get(key) + " ");
   					        }
   							out.printf("%n%s","Dealer Total = " + checkTotal(dealerHand));
   						}
   						if(checkTotal(dealerHand) > 21)
   						{
   							out.printf("%n%s%n%n","THE DEALER BUSTS SO PLAYER " + playerNum + " WINS!");
   						}
   						else if (checkTotal(playerHand) > checkTotal(dealerHand) )
   						{
   							out.printf("%n%s%n%n","PLAYER " + playerNum + " HAD A HIGHER NUMBER SO THEY WIN!");
   						}
   						else if (checkTotal(playerHand) < checkTotal(dealerHand) )
   						{
   							out.printf("%n%s%n%n","PLAYER " + playerNum + " HAD A LOWER NUMBER SO THEY LOSE!");
   						}
   						else
   						{
   							out.printf("%n%s%n%n","PLAYER " + playerNum + " AND DEALER HAD THE SAME NUMBER SO IT'S A DRAW");
   						}
   						if(playerNum == 1)
   						{
   							roundOver = false;
   						}
   					
	    			}
		    		if(playerNum == activePlayer)
		    		{
		    			myTurn = true;
		    		if(firstText != 1)
		    		{
		    			userName = in.readLine();
		    			userList.add(userName);
		    			if(userName != "empty")
		    			{
		    				firstText = 1;
		    			}
		    			System.out.println(userName + " has connected to the server");
		    			enterAndExitServer(userName, 0);
		    		}
			    		
			 	    	System.out.printf("%s%n","It is Player number " + playerNum + "'s turn");
			        
				    	if(myTurn == true)
				    	{
				   			//Picks one of the dealer's cards to show to the player
				   			out.print("Dealer is showing an ");
				   			//printToEveryClient("Dealer is showing an");
				   			ArrayList<Integer> keysAsArray = new ArrayList<Integer>(dealerHand.keySet());
			    			Random r = new Random();
			    			int cardToPick = keysAsArray.get(r.nextInt(keysAsArray.size()));
			    			//System.out.print(dealerHand.get(cardToPick));
				    		out.print(dealerHand.get(cardToPick));
				    		
				   			//Create's the player's hand and tells the player what they have
				   			playerHand = createHand(deck);
				   			deck = pruneDeck(playerHand, deck);
				   			out.printf("%n%s","Your hand is: ");
				   			for (Integer key: playerHand.keySet())
			    			{
			    	            out.print(playerHand.get(key) + " ");
			    	        }
				    		while(myTurn == true)
				    		{
				    			out.print("| Player Total = " + checkTotal(playerHand));;
				    			//When this is above in.readLine a newline character is required after the
					   			//text or the program will ignore this statement entirely
					   			out.printf("%n%s%n", "Would you like to hit or stay?");
					   			clientOutput = in.readLine();
					   			if(clientOutput.toLowerCase().contains("hit"))
				    			{
				    				playerHand = hit(playerHand, deck);
				    				deck = pruneDeck(playerHand, deck);
				    				out.print("Your hand is now: ");
				    				for (Integer key: playerHand.keySet())
					    			{
					    	            out.print(playerHand.get(key) + " ");
					   		        }
					   				if(checkTotal(playerHand) > 21)
					 				{
					   					out.printf("%n%s","Player Total = " + checkTotal(playerHand));
				    					out.printf("%n%s","You Busted!");
				    					//Player busted so they lose no matter what
				    					roundTotal = 0;
					   					if(activePlayer == threadList.size())
					   					{
					   		    			//If the round is over the deck is recreated and a new dealerHand is chosen
					   		        		deck = createDeck();
					   		        		dealerHand = createHand(deck);
					   		        		deck = pruneDeck(dealerHand, deck);
					   		        		//Setting the active player back to player 1 to start a new round
					   		        		activePlayer = 1;
					   						roundOver = true;
						   				}
						   				else
						   				{
						   					activePlayer += 1;
						   					System.out.print(activePlayer + " " + threadList.size());
											System.out.print("Next Player");
						   				}
						   				myTurn = false;
						   				out.printf("%n%s%n","Player " + playerNum + "'s turn is over");
						    		}
					   					
					    		}
					    		else if(clientOutput.toLowerCase().contains("stay"))
					    		{
					    			roundTotal = checkTotal(playerHand);
					   				if(activePlayer == threadList.size())
					   				{
				   		    			//If the round is over the deck is recreated and a new dealerHand is chosen
				   		        		deck = createDeck();
				   		        		dealerHand = createHand(deck);
				   		        		deck = pruneDeck(dealerHand, deck);
				   		        		activePlayer = 1;
				   						roundOver = true;
					   				}
					   				else
					   				{
					   					activePlayer += 1;
					   					System.out.print(activePlayer + " " + threadList.size());
					   					System.out.print("Next Player");
					   				}
					   				myTurn = false;
					   				out.printf("%n%s%n","Player " + playerNum + "'s turn is over");
					   			}
				    		}
				    	}
				    		
		    		}
		    		//Telling the threads to sleep when they are not active seems to be crucial in the game working
		    		//For a long time it would not switch back and forth between the players until I added this
		    		else
		    		{
		    			Thread.sleep(100);
		    		}
		    	}
		    
		    }
			catch(Exception e)
			{
				//e.printStackTrace();
			}
		    //This try catch is needed to check if the streams are active and close them
		    //if they are, basically cleaning up after yourself
		    try
		    {
		    	if(out != null)
		    	{
		    	    out.close();
		    	}
		    	if(in != null)
		    	{
		    	    in.close();
		    	}
			    clientSocket.close();
		    }
		    catch(Exception e)
		    {
		    	//e.printStackTrace();
		    }
	
		}
		
		private void enterAndExitServer(String name, int welcomeOrLeave)
		{
			for(ThreadHandler current: threadList)
			{
				if(welcomeOrLeave == 0)
				{
					current.out.println("(Server): Welcome " + name);
				}
				else if(welcomeOrLeave == 1)
				{
					current.out.println("(Server): Goodbye " + name);
				}
			}
			if(welcomeOrLeave == 1)
			{
				System.out.println(name + " has disconnected from the server");
			}
		}
		
		//Uses the list of threads to print the same thing to every client
		private void printToEveryClient(String outputString)
		{
			for(ThreadHandler current: threadList)
			{
				current.out.println(outputString);
			}
		}
		
		private void printEveryUser()
		{
			for(String name: userList)
			{
				out.println(name);
			}
		}
		
	}
	
	public static HashMap<Integer, String> createDeck()
	{
		HashMap<Integer, String> deck = new HashMap<Integer, String>(); 
		for(int i = 0; i < 52; i++)
		{
			if(i < 4)
			{
				deck.put(i, "Ace");
			}
			else if(i >= 4 && i < 8)
			{
				deck.put(i, "Two");
			}
			else if(i >= 8 && i < 12)
			{
				deck.put(i, "Three");
			}
			else if(i >= 12 && i < 16)
			{
				deck.put(i, "Four");
			}
			else if(i >= 16 && i < 20)
			{
				deck.put(i, "Five");
			}
			else if(i >= 20 && i < 24)
			{
				deck.put(i, "Six");
			}
			else if(i >= 24 && i < 28)
			{
				deck.put(i, "Seven");
			}
			else if(i >= 28 && i < 32)
			{
				deck.put(i, "Eight");
			}
			else if(i >= 32 && i < 36)
			{
				deck.put(i, "Nine");
			}
			else if(i >= 36 && i < 40)
			{
				deck.put(i, "Ten");
			}
			else if(i >= 40 && i < 44)
			{
				deck.put(i, "Jack");
			}
			else if(i >= 44 && i < 48)
			{
				deck.put(i, "Queen");
			}
			else if(i >= 48 && i < 52)
			{
				deck.put(i, "King");
			}
			//Checking to make sure we have the correct amount of cards
			//System.out.printf("%s%n",deck.get(i));
		}
		return deck;
	}
	
	public static int checkTotal(HashMap<Integer, String> cards)
	{
		int aceAmount = 0;
		int total = 0;
		for (Integer key: cards.keySet())
		{
            String currentCard = cards.get(key);
            if(currentCard.toLowerCase().contains("king") || currentCard.toLowerCase().contains("queen") 
            		|| currentCard.toLowerCase().contains("jack") || currentCard.toLowerCase().contains("ten"))
            {
            	total += 10;
            }
            else if(currentCard.toLowerCase().contains("nine"))
            {
            	total += 9;
            }
            else if(currentCard.toLowerCase().contains("eight"))
            {
            	total += 8;
            }
            else if(currentCard.toLowerCase().contains("seven"))
            {
            	total += 7;
            }
            else if(currentCard.toLowerCase().contains("six"))
            {
            	total += 6;
            }
            else if(currentCard.toLowerCase().contains("five"))
            {
            	total += 5;
            }
            else if(currentCard.toLowerCase().contains("four"))
            {
            	total += 4;
            }
            else if(currentCard.toLowerCase().contains("three"))
            {
            	total += 3;
            }
            else if(currentCard.toLowerCase().contains("two"))
            {
            	total += 2;
            }
            //Aces are treated differently depending on the total of the other cards. They can be 11 or 1
            else if(currentCard.toLowerCase().contains("ace"))
            {
            	aceAmount += 1;

            }
        }
		while(aceAmount != 0)
		{
			if(total <= 10)
        	{
        		total += 11;
        	}
        	else
        	{
        		total += 1;
        	}
			aceAmount -= 1;
		}
		return total;
	}
	
	public static HashMap<Integer, String> hit(HashMap<Integer, String> currentHand, HashMap<Integer, String> currentDeck)
	{
		ArrayList<Integer> keysAsArray = new ArrayList<Integer>(currentDeck.keySet());
		Random r = new Random();
		int cardToPick = keysAsArray.get(r.nextInt(keysAsArray.size()));
		currentHand.put(cardToPick, currentDeck.get(cardToPick));
		return currentHand;
	}
	
	public static HashMap<Integer, String> createHand(HashMap<Integer, String> currentDeck)
	{
		HashMap<Integer, String> newPlayerHand = new HashMap<Integer, String>();
		for(int i = 0; i < 2; i++)
		{
			//Generates a random number between 0 and 51
			ArrayList<Integer> keysAsArray = new ArrayList<Integer>(currentDeck.keySet());
			Random r = new Random();
			int cardToPick = keysAsArray.get(r.nextInt(keysAsArray.size()));
			newPlayerHand.put(cardToPick ,currentDeck.get(cardToPick));
		}
		return newPlayerHand;
	}
	
	public static HashMap<Integer, String> pruneDeck(HashMap<Integer, String> currentHand, HashMap<Integer, String> currentDeck)
	{
		//Using this method we can get all the keys from the currenthand and use them to remove the appropriate
		//values from the deck which we will then return
		for(int strKey : currentHand.keySet())
		{
			if(currentDeck.containsKey(strKey))
			{
				currentDeck.remove(strKey);
			}
		}
		return currentDeck;
	}
	
}