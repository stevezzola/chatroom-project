package chatroom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class User {
	private String name;
	private String password;
	
	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}
	
	public int getUser(String name, String password) {
		ArrayList<User> users = loadUserArrayList();
		if (users == null) {
			users = new ArrayList<User>();
		}
		User foundUser = new User(name, password);
		int index = searchUserIndex(name);
		if (index == -1) {
			return 0; //user not found
		}
		else if (password != users.get(index).getPassword()) {
			return -1; //incorrect password
		}
		else {
			
			return 1; //user/pass accepted
		}
			
		
		
	}
	
	public void addUser(String name, String password) {
		ArrayList<User> users = loadUserArrayList();
		if (users == null) {
			users = new ArrayList<User>();
		}
		User newUser = new User(name, password);
		int index = searchUserIndex(name);	
		if (index != -1) {
			//says user name already exists
		}
		else {
			users.add(newUser);
		}
		writeFile(users);
	}
	
	public void writeFile(ArrayList<User> users) {
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
	
	public ArrayList<User> loadUserArrayList() {
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
	
	public int searchUserIndex(String name) {
		ArrayList<User> temp = loadUserArrayList();
		if (temp == null) return -1;
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).getName().equals(name)) {
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