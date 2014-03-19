package chatroom;

import javax.swing.JFrame;
import javax.swing.*;

public class test {
	
	public static void main(String[]  args) {

		JTextField username = new JTextField();
		JTextField password = new JPasswordField();
		Object[] message = {
				"Username:", username,
				"Password:", password
		};

		int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if (username.getText().equals("h") && password.getText().equals("h")) {
				System.out.println("Login successful");
			} else {
				System.out.println("login failed");
			}
		} else {
			System.out.println("Login canceled");
		}
	}
}
