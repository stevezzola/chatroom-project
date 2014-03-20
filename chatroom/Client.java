package chatroom;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

public class Client {

	private BufferedReader in;
	private PrintWriter out;
	JFrame frame = new JFrame("Client Box");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);
	
	public Client() {
		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, BorderLayout.NORTH);
		frame.getContentPane().add(messageArea, BorderLayout.CENTER);
		frame.pack();
		
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}
	
	private String getServerAddress() {
		return JOptionPane.showInputDialog(frame, "Server's IP?", 
				"Welcome to the chatroom.", JOptionPane.PLAIN_MESSAGE);
	}
	
	private String getInfo() {
		JTextField tfusername = new JTextField();
		JTextField tfpassword = new JPasswordField();
		Object[] message = {"Username:", tfusername, "Password:", tfpassword};
		
		JOptionPane.showConfirmDialog(frame, message, 
				"Login", JOptionPane.OK_CANCEL_OPTION);
		String info = tfusername.getText() + "," + tfpassword.getText();
		return info;
	}
	
	private void run() throws IOException {
		String serverAddress = getServerAddress();
		Socket socket = new Socket(serverAddress, 4000);
		in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		
		while(true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITINFO")) {
				out.println(getInfo());
			}
			else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			}
			else if (line.startsWith("MESSAGE")) {
				messageArea.append(line.substring(8) + "\n");
			}
			else if (line.startsWith("DUPLICATE")) {
				JOptionPane.showMessageDialog(frame, "This user is already signed in.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("INVALIDPASSWORD")) {
				JOptionPane.showMessageDialog(frame, "Password is invalid.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("NAMENOTFOUND")) {
				JOptionPane.showMessageDialog(frame, "Username not found.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("INCOMPLETE")) {
				JOptionPane.showMessageDialog(frame, "Please fill all fields.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setLocationRelativeTo(null);
		client.frame.setVisible(true);
		client.run();
	}
}
