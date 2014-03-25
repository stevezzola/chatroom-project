package chatroom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * User Class for managing user info
 * @author Steven Mazzola
 *
 */

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String password;
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	/*
	 * gets user info from file
	 */
	public static int getUser(String name, String password) {
		ArrayList<User> users = loadUserArrayList();
		User user = new User(name, password);
		if (users == null) {
			users = new ArrayList<User>();
			users.add(new User("admin", "12345"));
			writeFile(users);
		}
		int index = searchUserIndex(user.getName());
		//System.out.println(users.toString());
		if (index == -1) {
			return 0; //user not found
		}
		else if (!password.equals(users.get(index).getPassword())) {
			return -1; //incorrect password
		}
		else {
			return 1; //name and password accepted
		}
	}
	
	/*
	 * adds user info to file
	 */
	public static int addUser(String name, String password) {
		ArrayList<User> users = loadUserArrayList();
		if (users == null) {
			users = new ArrayList<User>();
			users.add(new User("admin", "12345"));
			writeFile(users);
		}
		int index = searchUserIndex(name);	
		if (index != -1) {
			return 0; //name already exists
		}
		else {
			users.add(new User(name, password));
			writeFile(users);
			return 1; //name registered
		}
	}
	
	/*
	 * writes users to file
	 */
	public static void writeFile(ArrayList<User> users) {
		try {
			FileOutputStream fos = new FileOutputStream(new File("users.dat"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(users);
			oos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * loads binary file of user info
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<User> loadUserArrayList() {
		try {
			FileInputStream fis = new FileInputStream(new File("users.dat"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<User> users = (ArrayList<User>) ois.readObject();
			ois.close();
			return users;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/*
	 * searches file for specified user
	 */
	public static int searchUserIndex(String name) {
		ArrayList<User> temp = loadUserArrayList();
		if (temp == null) return -1;
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String toString() {
		return "\n=================" + "\nName: " + name + "\nPassword: " + password;
	}
}