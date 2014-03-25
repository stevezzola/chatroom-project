package chatroom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

public class Client {
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket = new Socket();
	JFrame frame = new JFrame("Messenger");
	JTextField textField = new JTextField(40);
	JTextArea textArea1 = new JTextArea(10, 40);
	JTextArea textArea2 = new JTextArea(10, 40);
	JScrollPane scrollPane1 = new JScrollPane(textArea1);
	JScrollPane scrollPane2 = new JScrollPane(textArea2);
	JTabbedPane tabbedPane = new JTabbedPane();
	JPanel tab1 = new JPanel();
	JPanel tab2 = new JPanel();
	JPanel tabPlus = new JPanel();
	JButton tabPlusButton = new JButton("+");
	
	public Client() {
		textField.setEditable(false);
		textArea1.setEditable(false);
		textArea2.setEditable(false);
		textArea1.setLineWrap(true);
		textArea2.setLineWrap(true);
		textArea1.setWrapStyleWord(true);
		textArea2.setWrapStyleWord(true);
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
		tabPlusButton.setContentAreaFilled(false);
		tabPlusButton.setBorderPainted(false);
		tabbedPane.add(tabPlus);
		tabbedPane.setEnabledAt(2, false);
		tabbedPane.setTabComponentAt(2, tabPlusButton);
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.getContentPane().add(tabbedPane);
		frame.getContentPane().add(textField, BorderLayout.SOUTH);
		frame.setResizable(false);
		frame.pack();
		
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int currentTab = tabbedPane.getSelectedIndex();
				if (currentTab > 1) {
					out.println("PRIVATE" + Chatroom.selected.name + "%" + textField.getText());
				}
				else {
				Integer.toString(currentTab);
				out.println(currentTab + textField.getText());
				}
				textField.setText("");
			}
		});
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JPanel temp = (JPanel)tabbedPane.getSelectedComponent();
				Chatroom.searchTab(temp);
			}
		});
		
		tabPlusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newTabName = JOptionPane.showInputDialog(frame, "          Name of new Private Chatroom?", 
						"New Private Room.", JOptionPane.PLAIN_MESSAGE);
				if (newTabName != null) {
					
					Chatroom room = new Chatroom();
					room.name = newTabName;
					room.tab = new JPanel();
					room.textArea = new JTextArea(10, 40);
					room.scrollPane = new JScrollPane(room.textArea);
					room.tab.add(room.scrollPane, BorderLayout.CENTER);
					room.textArea.setEditable(false);
					Chatroom.chatrooms.add(room);
					tabbedPane.setSelectedComponent(tabPlus);
					tabbedPane.insertTab(room.name, null, room.tab, "private room", tabbedPane.getSelectedIndex());
					tabbedPane.setSelectedComponent(room.tab);
					
					
				}
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
			socket = new Socket(serverAddress, 4000);
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
			try {
				String line = in.readLine();
				if (line.startsWith("SUBMITINFO")) {
					String info = login("");
					out.println(info);
					if (info.equals("LOGINEXIT")) {
						throw (new Exception());
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
				else if (line.startsWith("MESSAGE0")) {
					textArea1.append(line.substring(8) + "\n");
				}
				else if (line.startsWith("MESSAGE1")) {
					textArea2.append(line.substring(8) + "\n");
				}
				else if (line.startsWith("PMESSAGE")) {
					String nameText = line.substring(8);
					String roomName = nameText.split("%")[0];
					String text = nameText.split("%")[1];
					if (Chatroom.searchTab(roomName) != null)
						Chatroom.searchTab(roomName).textArea.append(text + "\n");
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
			catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Lost connection to the server!",
						"Warning", JOptionPane.ERROR_MESSAGE);
				socket.close();
				System.exit(0);
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
	
	public static class Chatroom {
		private static ArrayList<Chatroom> chatrooms = new ArrayList<Chatroom>();
		private static Chatroom selected;
		public JPanel tab;
		public JTextArea textArea;
		public JScrollPane scrollPane;
		public String name;
		public HashSet<String> names;
	
		public Chatroom(){
			
		}
		
		private static void searchTab(JPanel tab) {
			for (int i = 0; i < chatrooms.size(); i++) {
				if (tab == chatrooms.get(i).tab) {
					selected = chatrooms.get(i);
					return;
				}
			}
		}
		
		private static Chatroom searchTab(String name) {
			for (int i = 0; i < chatrooms.size(); i++) {
				if (name.equals(chatrooms.get(i).name)) {
					return chatrooms.get(i);
				}
			}
			return null;
		}
	}
}
