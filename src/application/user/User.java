package application.user;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import application.Album.Album;
import application.tags.TagType;
import application.utilities.DataStorage;

/**
 * Represents a user of the Photo Library
 * @author Kaavya Krishna-Kumar	
 * @author Abhishek Kondila
 *
 */
public class User implements Serializable {
	
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -7590333576151190648L;

	/**
	 * The username of this user.
	 */
	private String username;
	
	/**
	 * The data path for this user.
	 */
	private final String dataPath;
	

	/**
	 * The albums this user has created.
	 */
	private transient ArrayList<Album> albums = new ArrayList<>();
	
	/**
	 * The tag types for this user.
	 */
	private ArrayList<TagType> tagTypes = new ArrayList<>();
	
	/**
	 * Gets all the user's corresponding data, such as albums, photos, tags, etc.
	 */
	public void init() {
		File file = new File(dataPath);
		if (file.exists()) {
			for (String s : file.list()) {
				if (s.equals(".DS_Store")) {//MAC users lol..
					continue;
				}
				Album album = new Album(s, dataPath + s + "/");
				album.init();
				albums.add(album);
			}
		} else {
			file.mkdir();
			System.out.println("Creating the dir: " + file);
		}
	}
	
	/**
	 * Checks if an album exists already for this user. 
	 * @param name The name of the album.
	 * @return True if so.
	 */
	public boolean albumExists(String name) {
		for (Album a : albums) {
			if (a.getAlbumName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds an album
	 * @param name the name
	 * @return the album
	 */
	public Album addAlbum(String name) {
		if (albumExists(name)) {
			return null;
		}
		String path = dataPath + name + "/";
		File file = new File(path);
		if (file.exists()) {
			return null;
		}
		file.mkdir();
		System.out.println("Creating the dir for Album: " + file);
		Album album = new Album(name, path);
		albums.add(album);
		return album;
	}
	
	/**
	 * Deletes an album
	 * @param name the name of the album
	 */
	public void deleteAlbum(String name) {
		Album album = null;
		for (Album a : albums) {
			if (a.getAlbumName().equalsIgnoreCase(name)) {
				album = a;
				break;
			}
		}
		if (album == null) {
			return;
		}
		DataStorage.deleteDirectory(new File(album.getPath()));
		albums.remove(album);
	}
	
	/**
	 * Resolves the array list.
	 * @return the object.
	 */ 
	private Object readResolve() {
        this.albums = new ArrayList<>();
        if (tagTypes == null) {
        	tagTypes = new ArrayList<TagType>();
        }
        return this;
    }
	
	
	/**
	 * Constructs a new {@code User} object.
	 * @param username The username.
	 */
	public User(String username) {
		this.username = username;
		this.dataPath = "./userdata/" + username.toLowerCase() + "/";
	}
	
	/**
	 * Gets the username of this user. 
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets this user's albums.
	 * @return the albums The albums.
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}

	/**
	 * @return the tagTypes
	 */
	public ArrayList<TagType> getTagTypes() {
		return tagTypes;
	}
	
}