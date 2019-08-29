package application.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import application.Album.Album;
import application.photoEdit.Photo;
import application.user.User;

/**
 * This class will handle all the data storage for the Photo Library application.
 * 
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila
 *
 */
public class DataStorage {
	
	/**
	 * The filename where we will save all the users too.
	 */
	private static String USER_FILENAME = "photolibrary.users";
	
	/**
	 * Saves all the users.
	 * @param users The users.
	 */
	public static void saveAllUsers(ArrayList<User> users) {
		try {
			FileOutputStream file = new FileOutputStream(USER_FILENAME);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeInt(users.size());
			for (User u : users) {
				savePhotos(u);
				out.writeObject(u);
			}
			out.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the users of the PhotoLibrary application and returns them in an array list.
	 * @return An ArrayList of users.
	 */
	public static ArrayList<User> parseUsers() {
		ArrayList<User> users = new ArrayList<>();
        try {
        	FileInputStream file = new FileInputStream(USER_FILENAME); 
			ObjectInputStream in = new ObjectInputStream(file);
			int length = in.readInt();
			for (int i = 0; i < length; i++) {
				User u = (User) in.readObject();
				u.init();
				users.add(u);
			}
			in.close();
			file.close();
		} catch (IOException | ClassNotFoundException e) {
			if (e instanceof FileNotFoundException) {
				return users;
			}
			e.printStackTrace();
		} 
		return users;
	}
	

	/**
	 * Saves a user's photos.
	 * @param user The user.
	 */
	public static void savePhotos(User user) {
		for (Album a : user.getAlbums()) {
			try {
				File file = new File(a.getPath() + "photos.data");
				file.createNewFile();
				FileOutputStream file1 = new FileOutputStream(a.getPath() + "photos.data");
				ObjectOutputStream out = new ObjectOutputStream(file1);
				int length = a.getPhotos().size();
				out.writeInt(length);
				for (Photo p : a.getPhotos()) {
					out.writeObject(p);
				}
				out.close();
				file1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Parses the photos for this album.
	 * @param album The album.
	 */
	public static void parsePhotos(Album album) {
		try {
			FileInputStream file = new FileInputStream(album.getPath() + "photos.data");
			ObjectInputStream in = new ObjectInputStream(file);
			int length = in.readInt();
			for (int i = 0; i < length; i++) {
				Photo p = (Photo) in.readObject();
				album.addPhoto(p);
			}
			in.close();
			file.close();
		} catch (IOException | ClassNotFoundException e) {
			if (e instanceof FileNotFoundException) {
				return;
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * This deletes a directory and its contents.
	 * @param directory The directory to delete.
	 */
	public static void deleteDirectory(File directory) {
		File[] files = directory.listFiles();
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteDirectory(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    directory.delete();
	}


}