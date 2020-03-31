package IO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Hermes {

	final static String DEFAULT_IP="igtnet-w.ddns.net";
	final static int DEFAULT_PORT=757;

	/**
	 * Sends a ServerCommand to a SmokeSignal server
	 * @param ClientMSG Message to Send
	 * @param DebugMode Debug Mode (which shows the bynary writer receiving the string)
	 * @param ServerIP IP of the server u want to send to
	 * @param Port Port of the server u want to send to
	 * @return A reply from the server
	 */
	public static String ServerCommand(String ClientMSG, boolean DebugMode, String ServerIP,int Port) {
		//This is all encapsulated in a try, just in case.
		try {
			//This sets up a socket, and connects to the server.
			Socket theSocket=new Socket(ServerIP, Port);

			//The Readers and Writers
			DataOutputStream MercuryOne = new DataOutputStream(theSocket.getOutputStream());
			DataInputStream MercuryTwo = new DataInputStream(theSocket.getInputStream());

			//Formatting for the SentMSG
			String SentMSG;
			
			//For some reason, the Java ByteWriter doesn't send the first character.
			SentMSG=" " + ClientMSG;
			
			//Just to make sure we capture SentMSG's original length
			int S=SentMSG.length();
			
			//For some reason, the Java ByteWriter also only sends if there's 72 characters, so this takes care of that.
			for (int i = S; i < 73; i++) {SentMSG= SentMSG + " ";}

			//We send it
			MercuryOne.writeBytes(SentMSG);
			
			//Initialize some variables
			byte ServerMSG = 0;
			String ReturnMSG="";
			
			//This takes care of the first junk byte
			MercuryTwo.readByte();
			
			//This waits to make sure we have received algo.
			while ((MercuryTwo.available()==0)) {}
			
			//This reads until there's nothing to read. The ByteReader reads one byte at a time, and it's translated on the fly.
			while (!(MercuryTwo.available()==0)) {
				ServerMSG=MercuryTwo.readByte();	
				ReturnMSG=ReturnMSG + new String(new byte[] {ServerMSG});
				if (DebugMode) {System.out.println(MercuryTwo.available() + " " + ReturnMSG);}
			}
			
			//Close and return
			theSocket.close();
			return ReturnMSG;
		}
		catch (UnknownHostException e) {System.out.println("haha silly I couldn't find that host"); e.printStackTrace();} //Couldn't find the host
		catch (ConnectException e) {System.out.println("Woops, the I could not connect"); e.printStackTrace();} //Couldn't connect to the host
		catch (IOException e) {e.printStackTrace();} //Unrelated IO exception
		//If anything happens for whatever reason, return E
		return "E";}

	/**
	 * Sends a ServerCommand to the SmokeSignal server that's listenning on the default IP and Port
	 * @param ClientMSG Message to Send
	 * @return A reply from the server
	 */
	public static String ServerCommand(String ClientMSG) {return ServerCommand(ClientMSG,false,DEFAULT_IP,DEFAULT_PORT);}
	
	/**
	 * Sends a ServerCommand to the SmokeSignal server that's listenning on the default IP and Port
	 * @param ClientMSG Message to Send
	 * @param DebugMode Enable or Disable Debug Mode (Which shows the bynary writer doing it's work.
	 * @return A reply from the server
	 */
	public static String ServerCommand(String ClientMSG,boolean DebugMode) {return ServerCommand(ClientMSG,DebugMode,DEFAULT_IP,DEFAULT_PORT);}
}
