package chatroom;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class Client {

	private BufferedReader in;
	private PrintWriter out;
	JFrame frame = new JFrame("Client Box");
	JTextField textField = new JTextField(40);
	JTextArea textArea1 = new JTextArea(10, 40);
	JTextArea textArea2 = new JTextArea(10, 40);
	JScrollPane scrollPane1 = new JScrollPane(textArea1);
	JScrollPane scrollPane2 = new JScrollPane(textArea2);
	JTabbedPane tabbedPane = new JTabbedPane();
	JPanel tab1 = new JPanel();
	JPanel tab2 = new JPanel();
	
	public Client() {
		textField.setEditable(false);
		textArea1.setEditable(false);
		textArea2.setEditable(false);
		textArea1.setLineWrap(true);
		textArea2.setLineWrap(true);
		DefaultCaret caret1 = (DefaultCaret)textArea1.getCaret();
		caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		DefaultCaret caret2 = (DefaultCaret)textArea2.getCaret();
		caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);	
		scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tab1.add(scrollPane1, BorderLayout.CENTER);
		tab2.add(scrollPane2, BorderLayout.CENTER);
		tabbedPane.addTab("Room 1", tab1);
		tabbedPane.addTab("Room 2", tab2);
		frame.getContentPane().add(tabbedPane);
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		frame.setResizable(false);
		frame.pack();
		
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String currentTab = Integer.toString(tabbedPane.getSelectedIndex());
				out.println(currentTab + textField.getText());
				textField.setText("");
			}
		});
	}
	
	private String getServerAddress() {
		String ip = JOptionPane.showInputDialog(frame, "Server's IP?", 
				"Welcome to the chatroom.", JOptionPane.PLAIN_MESSAGE);
		if (ip == null) {
			return "IPEXIT";
		}
		else
			return ip;
	}
	
	private String register(String name) {
		JTextField tfusername = new JTextField(name);
		JTextField tfpassword = new JPasswordField();
		JTextField tfconfirmpass = new JPasswordField();
		Object[] message = {"Create Username:", tfusername, 
				"Create Password:", tfpassword, "Confirm Password:", tfconfirmpass};
		int button = JOptionPane.showConfirmDialog(frame, message, 
				"Register", JOptionPane.OK_CANCEL_OPTION);
		String infoReg = tfusername.getText() + "," + tfpassword.getText();
		if (button == 0) {
			if (tfusername.getText().equals("") || tfpassword.getText().equals("")
					|| tfconfirmpass.getText().equals("")) {
				JOptionPane.showMessageDialog(frame, " Please complete all fields.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
				return register(tfusername.getText());
			}
			else if (!tfpassword.getText().equals(tfconfirmpass.getText())) {
				JOptionPane.showMessageDialog(frame, " Passwords do not match.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
				return register(tfusername.getText());
			}
			else {
				return infoReg;
			}
		}
		else {
			return "REGEXIT";
		}
	}
	
	private String login(String name) {
		JTextField tfusername = new JTextField();
		JTextField tfpassword = new JPasswordField();
		Object[] message = {"Username:", tfusername, "Password:", tfpassword};
		UIManager.put("OptionPane.yesButtonText", "Sign In");
		UIManager.put("OptionPane.noButtonText", "Register");
		int button = JOptionPane.showConfirmDialog(frame, message, 
				"Login", JOptionPane.YES_NO_OPTION);
		String info = tfusername.getText() + "," + tfpassword.getText();
		if (button == 0) {
			return info;
		}
		else if (button == 1) {
			return "REGISTER";
		}
		else {
			return "LOGINEXIT";
		}
	}
	
	private void run() throws IOException {
		String serverAddress = getServerAddress();
		if (serverAddress.equals("IPEXIT")) {
			System.exit(0);
		}
		try {
			Socket socket = new Socket(serverAddress, 4000);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(frame, "Could not match IP address to a host.",
    				"Unknown Host", JOptionPane.ERROR_MESSAGE);
			run();
			return;
		}
		while(true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITINFO")) {
				String info = login("");
				out.println(info);
				if (info.equals("LOGINEXIT")) {
					System.exit(0);
				}
			}
			else if (line.startsWith("REGISTERINFO")) {
				String infoReg = register("");
				out.println(infoReg);
			}
			else if (line.startsWith("USERNAMETAKEN")) {
				JOptionPane.showMessageDialog(frame, "      Username is taken!",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("REGCOMPLETE")) {
				JOptionPane.showMessageDialog(frame, "  You are now registered!",
	    				"Congratulations!", JOptionPane.INFORMATION_MESSAGE);
			}
			else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			}
			else if (line.startsWith("0MESSAGE")) {
				textArea1.append(line.substring(9) + "\n");
			}
			else if (line.startsWith("1MESSAGE")) {
				textArea2.append(line.substring(9) + "\n");
			}
			else if (line.startsWith("DUPLICATE")) {
				JOptionPane.showMessageDialog(frame, "This user is already signed in.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("INVALIDPASSWORD")) {
				JOptionPane.showMessageDialog(frame, "     Password is invalid.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("NAMENOTFOUND")) {
				JOptionPane.showMessageDialog(frame, "    Username not found.",
	    				"Warning", JOptionPane.WARNING_MESSAGE);
			}
			else if (line.startsWith("INCOMPLETE")) {
				JOptionPane.showMessageDialog(frame, "      Please fill all fields.",
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
