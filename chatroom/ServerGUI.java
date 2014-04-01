package chatroom;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
/**
 * GUI for Server Class
 * @author Steven Mazzola
 *
 */
public class ServerGUI extends Thread {
	private final int WINDOW_WIDTH = 400;
	private final int WINDOW_HEIGHT = 300;
	JFrame frame = new JFrame("Server");
	JPanel panel = new JPanel();
	JButton button = new JButton("Logs");
	JFileChooser fileChooser = new JFileChooser("user_logs");
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	String command;
	
	public void run() {
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		panel.add(button, BorderLayout.NORTH);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.add(panel, BorderLayout.EAST);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		
		/*
		 * Handles button press and file chooser
		 */
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".dat files", "dat");
			   	fileChooser.setFileFilter(filter);
			    int returnVal = fileChooser.showOpenDialog(button);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	String fileName = fileChooser.getSelectedFile().getName();
			    	String username = fileName.substring(0, fileName.indexOf("."));
			    	ArrayList<String> log = User.readUserLog(username);
			    	textArea.append("=== message log for " + username + "=== \n");
			    	for (int i = 0; i < log.size(); i++) {
			    		textArea.append(log.get(i) + "\n");
			    	}
			    	textArea.append("=== end of message log === \n");
			    }
			}
		});
	}
}
