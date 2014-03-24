package chatroom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String password;
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public static int getUser(String name, String password) {
		ArrayList<User> users = loadUserArrayList();
		User user = new User(name, password);
		if (users == null) {
			users = new ArrayList<User>();
			users.add(new User("admin", "12345"));
			writeFile(users);
		}
		int index = searchUserIndex(user.getName());
		if (index == -1) {
			//System.out.println(users.toString());
			return 0; //user not found
		}
		else if (!password.equals(users.get(index).getPassword())) {
			return -1; //incorrect password
		}
		else {
			return 1; //user/pass accepted
		}
	}
	
	public static int addUser(String name, String password) {
		ArrayList<User> users = loadUserArrayList();
		if (users == null) {
			users = new ArrayList<User>();
			users.add(new User("admin", "12345"));
			writeFile(users);
		}
		int index = searchUserIndex(name);	
		if (index != -1) {
			return 0; //username already exists
		}
		else {
			users.add(new User(name, password));
			writeFile(users);
			return 1;
		}
	}
	
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