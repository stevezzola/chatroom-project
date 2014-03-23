package chatroom;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Server {
	private static final int PORT = 4000;
	private static HashSet<String> names = new HashSet<String>();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	
	public static void main(String[] args) throws Exception {
		System.out.println("The chatroom is running...");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while(true) {
				new Handler(listener.accept()).start();
			}
		} 
		catch (Exception e) {
			System.err.println(e); 
		}
		finally {
			listener.close();
		}	
	}

	private static class Handler extends Thread {
		private String info;
		private String name;
		private String password;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		
		public Handler(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				while(true) {
					out.println("SUBMITINFO");
					info = in.readLine();
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
					User user = new User(name, password);
					int trigger = user.getUser(user);
					if (trigger == 1) {
						names.add(name);
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
				
				out.println("NAMEACCEPTED");
				writers.add(out);
			
				while(true) {
					String input = in.readLine();
					if (input == null) {
						return;
					}
					for (PrintWriter writer: writers) {
						writer.println("MESSAGE " + name + ": " + input);
					}
				}
				
			} catch (Exception ex) {
				System.err.println(ex);
			} finally {
				if(name != null) {
					names.remove(name);
				}
				if(out != null) {
					writers.remove(out);	
				}
				try {
					socket.close();
				} catch (IOException e) {
					System.err.println("Problem closing socket!");
				}
			}
		}
	}
}
