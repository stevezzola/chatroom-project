package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Server side of Instant Messenger Project
 * @author Steven Mazzola
 *
 */
public class Server {
	private static final int PORT = 4000;
	private static ServerSocket listener;
	private static HashSet<String> names = new HashSet<String>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	private static HashMap<String, PrintWriter> hashMap = new HashMap<String, PrintWriter>();
	private static ServerGUI gui;
	
	/*
	 * Main method, runs GUI and listens for Client sockets
	 */
	public static void main(String[] args) {
		gui = new ServerGUI();
		User.createDir();
		gui.start();
		try {
			listener = new ServerSocket(PORT);
			gui.textArea.append("The chatroom is running...\n");
			try {
				while(true) {
					new Handler(listener.accept()).start();
				}
			} 
			catch (Exception e) {
				gui.textArea.append("Problem accepting socket!"); 
			}
			finally {
				listener.close();
			}	
		}
		catch (Exception e) {
			gui.textArea.append("Server address is already in use!");
		}
	}
	
	/**
	 * Handles each Client socket that has connected
	 */
	private static class Handler extends Thread {
		private String info;
		private String name;
		private String password;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private ObjectOutputStream objOut;
		private ObjectInputStream objIn;
		
		public Handler(Socket socket) {
			this.socket = socket;
		}
		
		/*
		 * running the Server thread 
		 */
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				objOut = new ObjectOutputStream(socket.getOutputStream());
				objIn = new ObjectInputStream(socket.getInputStream());
				while(true) {
					/*
					 * giving commands to the Client
					 */
					out.println("SUBMITINFO");
					info = in.readLine();
					if (info.equals("LOGINEXIT")) {
						name = null;
						throw (new Exception());
					}
					else if (info.equals("REGISTER")) {
						while (true) {
							out.println("REGISTERINFO");
							info = in.readLine();
							if (info.equals("REGEXIT")) {
								break;
							}
							else {	
								name = info.split(",")[0];
								password = info.split(",")[1];
								int trigger = User.addUser(name, password);
								if(trigger == 0) {
									out.println("USERNAMETAKEN");
									name = null;
									continue;
								}
								else {
									out.println("REGCOMPLETE");
									break;
								}
							}
						}
						continue;
					}
					else if (info.equals("REGCANCEL")) {
						continue;
					}
					
					try {
						name = info.split(",")[0];
						password = info.split(",")[1];
					}
					catch (ArrayIndexOutOfBoundsException e) {
						out.println("INCOMPLETE");
						continue;
					}
					if (name == null) {
						continue;
					}
					else if (names.contains(name)) {
						out.println("DUPLICATENAME");
						continue;
					}
					int trigger = User.getUser(name, password);
					if (trigger == 1) {
						names.add(name);
						gui.textArea.append(name + " has logged on.\n");
						break;
					}
					else if (trigger == -1) {
						out.println("INVALIDPASSWORD");
						continue;
					}
					else {
						out.println("NAMENOTFOUND");
						continue;
					}
				}
				
				out.println("NAMEACCEPTED" + name);
				writers.add(out);
				hashMap.put(name, out);
			
				while(true) {
					/*
					 * reads User input after login 
					 */
					try {
						String tabInput = in.readLine();
						if (tabInput.startsWith("PRIVATE")) {
							String nameInput = tabInput.substring(7);
							String roomName = nameInput.split("%")[0];
							String input = "";
							try { 
								input = nameInput.split("%")[1];
								User.writeUserLog(name, input);
							}
							catch (ArrayIndexOutOfBoundsException e) {}
							for (PrintWriter writer: writers) {
								writer.println("PMESSAGE" + roomName + "%" + name + ": " + input);
							}
						}
						else if (tabInput.startsWith("INVITE")) {
							String nameInput = tabInput.substring(6);
							@SuppressWarnings("unchecked")
							ArrayList<String> usernames = (ArrayList<String>)objIn.readObject();
							gui.textArea.append(usernames.toString() + "\n");
							String roomName = nameInput.split("%")[0];
							String inviteName = "";
							try { 
								inviteName = nameInput.split("%")[1];
							}
							catch (ArrayIndexOutOfBoundsException e) {}
							if (hashMap.get(inviteName) == null) {
								objOut.writeObject(usernames);
								objOut.flush();
								out.println("NAMENOTFOUND");
							}
							else if (usernames.contains(name)){
								objOut.writeObject(usernames);
								objOut.flush();
							}
							else {
								usernames.add(inviteName);
								objOut.writeObject(usernames);
								objOut.flush();
								hashMap.get(inviteName).println("PINVITE" + roomName + "%" + name);
								gui.textArea.append(inviteName + " has joined " + name + "'s room: " + roomName + "\n");
							}
							objOut.close();
							objIn.close();
						}
						
						else {
							char tab = tabInput.charAt(0);
							String input = tabInput.substring(1);
							User.writeUserLog(name, input);
							for (PrintWriter writer: writers) {
								writer.println("MESSAGE" + tab + name + ": " + input);
							}
						}
					}
					catch (SocketException e) {
						e.printStackTrace();
						return;
					}
				}
				
			} catch (Exception e) {
				gui.textArea.append("Client closed: " + socket.getInetAddress() + "\n");
			} finally {
				if(name != null) {
					names.remove(name);
					gui.textArea.append(name + " has disconnected.\n");
				}
				if(out != null) {
					writers.remove(out);	
				}
				try {
					socket.close();
				} catch (IOException e) {
					gui.textArea.append("Problem closing socket!\n");
				}
			}
		}
	}
}
