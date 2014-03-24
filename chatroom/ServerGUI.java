package chatroom;

import java.awt.BorderLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;




import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
//import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

public class ServerGUI extends Thread {
	private final int WINDOW_WIDTH = 400;
	private final int WINDOW_HEIGHT = 300;
	JFrame frame = new JFrame("Server");
	//static JPanel buttonPanel = new JPanel();
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	JButton bStart = new JButton("Start");
	JButton bStop = new JButton("Stop");
	String command;
	
	public void run() {
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		textArea.setEditable(false);
		DefaultCaret caret1 = (DefaultCaret)textArea.getCaret();
		caret1.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		//buttonPanel.add(bStart);
		//buttonPanel.add(bStop);
		frame.add(scrollPane, BorderLayout.CENTER);
		//frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
			
		/*
		bStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.append("The chatroom is running...\n");
			}
		});
		
		bStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.append("The server has been stopped.\n");
			}
		});
		*/
	}
}
