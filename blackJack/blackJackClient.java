package blackJack;

import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class blackJackClient
{
	public static boolean myTurn;
	
	public static void main(String[] args)
	{
		//normal host is 127.0.0.1
		Scanner myScanner = new Scanner(System.in);
		System.out.println("Input host to connect to");
		String host = myScanner.nextLine();
		System.out.println("Input port to connect to");
		int port = myScanner.nextInt();
		try(Socket socket = new Socket(host, port))
		{
			Scanner secondScanner = new Scanner(System.in);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;
			String clientName = "empty";
			
			ClientThread clientThread = new ClientThread(socket);
			new Thread(clientThread).start();
			System.out.print("Enter your name");
			clientName = secondScanner.nextLine();
			//out.println("Welcome " + clientName + " to the server!");
			out.println(clientName);
			
			while(true)
			{
				String message = ("(" + clientName + ")" + ":");
				//System.out.print(message);
				line = secondScanner.nextLine();
				out.println(message + " " + line);
				if(line.contains("Bye"))
				{
					in.close();
		            out.close();
		            socket.close();
					out.flush();
					myScanner.close();
					return;
				}
			}
					
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}


	private static class ClientThread implements Runnable
	{
		private Socket socket;
		private BufferedReader in;
		
		public ClientThread(Socket s) throws IOException
		{
			this.socket = s;
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		
		
		@Override
		public void run()
		{
			try
			{
				while(true)
				{
					String chatMessage = in.readLine();
					//This prints what the user just said back to them
					System.out.println(chatMessage);
					
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
			try
		    {
		    	if(in != null)
		    	{
		    	    in.close();
		    	}
			    socket.close();
		    }
		    catch(Exception e)
		    {
		    	//e.printStackTrace();
		    }
		}

	}
}