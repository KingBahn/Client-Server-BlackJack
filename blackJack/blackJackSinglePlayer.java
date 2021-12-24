package blackJack;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.*;
public class blackJackSinglePlayer
{
	
	public static void main(String[] args)
	{
		HashMap<Integer, String> deck = new HashMap<Integer, String>(); 
		//Switched from arraylist to a Hashmap in order to easily remove and add the same values to and from deck
		HashMap<Integer, String> playerHand = new HashMap<Integer, String>();
		HashMap<Integer, String> dealerHand = new HashMap<Integer, String>();
		boolean roundOver;
		int totalWins = 0;
		int totalLosses = 0;
		Scanner input = new Scanner(System.in);
		
		String playerInput = input.next();
		while(playerInput != "end")
		{
			roundOver = false;
			//new turn
			deck = createDeck();
			System.out.printf("%n%n%s%n","(New Round)");
			//Whenever cards are added to the player's hand or dealer's hand the deck must be pruned of those cards
			playerHand = startTurn(deck);
			deck = pruneDeck(playerHand, deck);
			dealerHand = startTurn(deck);
			deck = pruneDeck(dealerHand, deck);
			
			System.out.print("Dealer is showing an ");
			ArrayList<Integer> keysAsArray = new ArrayList<Integer>(dealerHand.keySet());
			Random r = new Random();
			int cardToPick = keysAsArray.get(r.nextInt(keysAsArray.size()));
			System.out.print(dealerHand.get(cardToPick));
			
			//Prints out the different values in the playerHand hashmap using a for each loop
			System.out.printf("%n%s","Your hand is: ");
			for (Integer key: playerHand.keySet())
			{
	            System.out.print(playerHand.get(key) + " ");
	        }
			while(roundOver == false)
			{
				System.out.printf("%n%s","Player Total = " + checkTotal(playerHand));
				System.out.printf("%n%s", "Would you like to hit or stay?");
				playerInput = input.next();
				if(playerInput.toLowerCase().contains("hit"))
				{
					playerHand = hit(playerHand, deck);
					deck = pruneDeck(playerHand, deck);
					System.out.print("Your hand is now: ");
					for (Integer key: playerHand.keySet())
					{
			            System.out.print(playerHand.get(key) + " ");
			        }
					if(checkTotal(playerHand) > 21)
					{
						System.out.printf("%n%s","Player Total = " + checkTotal(playerHand));
						System.out.printf("%n%s","You Busted!");
						totalLosses += 1;
						roundOver = true;
					}
				}
				else if(playerInput.toLowerCase().contains("stay"))
				{
					System.out.printf("%s","The dealer's hand was: " );
					for (Integer key: dealerHand.keySet())
					{
			            System.out.print(dealerHand.get(key) + " ");
			        }
					System.out.printf("%n%s","Dealer Total = "  + checkTotal(dealerHand));
					while(checkTotal(dealerHand) <= 16 )
					{	
						System.out.printf("%n%s","The dealer has to hit");
						dealerHand = hit(dealerHand, deck);
						deck = pruneDeck(dealerHand, deck);
						System.out.printf("%n%s","The dealer's hand is now: ");
						for (Integer key: dealerHand.keySet())
						{
				            System.out.print(dealerHand.get(key) + " ");
				        }
						System.out.printf("%n%s","Dealer Total = " + checkTotal(dealerHand));
					}
					if(checkTotal(dealerHand) > 21)
					{
						System.out.printf("%n%s","THE DEALER BUSTS SO THE PLAYER WINS!");
						totalWins += 1;
					}
					else if (checkTotal(playerHand) > checkTotal(dealerHand) )
					{
						System.out.printf("%n%s","THE PLAYER HAD A HIGHER NUMBER SO THEY WIN!");
						totalWins += 1;
					}
					else if (checkTotal(playerHand) < checkTotal(dealerHand) )
					{
						System.out.printf("%n%s","THE PLAYER HAD A LOWER NUMBER SO THEY LOSE!");
						totalLosses += 1;
					}
					else
					{
						System.out.printf("%n%s","THE PLAYER AND DEALER HAD THE SAME NUMBER SO IT'S A DRAW");
					}
					roundOver = true;
				}
			}
		}
		System.out.printf("%n%s%n",playerHand.get(0));
		System.out.print(playerHand.get(1));
	}
	
	public static HashMap<Integer, String> startTurn(HashMap<Integer, String> currentDeck)
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
	
	//Call when you take cards out of the deck to create a players hand
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
	
	public static HashMap<Integer, String> hit(HashMap<Integer, String> currentHand, HashMap<Integer, String> currentDeck)
	{
		ArrayList<Integer> keysAsArray = new ArrayList<Integer>(currentDeck.keySet());
		Random r = new Random();
		int cardToPick = keysAsArray.get(r.nextInt(keysAsArray.size()));
		currentHand.put(cardToPick, currentDeck.get(cardToPick));
		return currentHand;
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
	
	//This method converts the string version of the numbers to actual numbers and counts them up to give the total
	//value of the players hand
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
	
}