package application.Album;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import application.photoEdit.Photo;
import application.utilities.DataStorage;

/**
 * Represents a photo album.
 * @author kaavya1698
 *
 */
public class Album implements Serializable {
	

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 4502169424215450270L;

	/**
	 * The name of the album.
	 */
	private String albumName;
	
	/**
	 * The path to this album.
	 */
	private String path;
	
	/**
	 * The photos in this album
	 */
	private ArrayList<Photo> photos = new ArrayList<Photo>();
	
	/**
	 * The start date.
	 */
	private Date start;
	
	/**
	 * The end date.
	 */
	private Date end;
	
	/**
	 * Constructs a new {@code Album} object.
	 * @param albumName The album name.
	 * @param path the path.
	 */
	public Album(String albumName, String path) {
		this.setAlbumName(albumName);
		this.path = path;
	}

	/**
	 * Initializes the file path.
	 */
	public void init() {
		DataStorage.parsePhotos(this);
	}
	
	/**
	 * Renames the album
	 * @param name The name.
	 */
	public void rename(String name) {
		albumName = name;
		File dir = new File(path);
		String renamedPath = dir.getParent() + "/" + name;
		File rename = new File(renamedPath);
		dir.renameTo(rename);
		path = renamedPath;
	}
	
	/**
	 * Gets the album's name.
	 * @return the albumName
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * Sets the album's name.
	 * @param albumName the albumName to set
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	/**
	 * Gets the file path to this album.
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Gets the pictures from this album
	 * @return ArrayList of photos for this album, null if none
	 */
	public ArrayList<Photo> getPhotos(){
		return photos;
	}
	
	/**
	 * Adds a photo to this album
	 * @param a is a photo to add to this album
	 */
	public void addPhoto(Photo a) {
		this.photos.add(a);
		setDates();
	}
	
	/**
	 * Removes the photo.
	 * @param remove The photo to remove.
	 */
	public void remove(Photo remove) {
		this.photos.remove(remove);
		setDates();
	}
	
	/**
	 * Sets the dates.
	 */
	public void setDates() {
		for (Photo p : photos) {
			if (start == null) {
				start = p.getDate();
			}
			if (end == null) {
				end = p.getDate();
			}
			if (p.getDate().getTime() < start.getTime()) {
				start = p.getDate();
			}
			if (p.getDate().getTime() > end.getTime()) {
				end = p.getDate();
			}
		}
	}
	
	/**
	 * Gets the start date.
	 * @return The start date.
	 */
	public String getStartDate() {
		if (start == null) {
			return "N/A";
		}
		String[] data = start.toString().split(" ");
		return data[1] + " " + data[2] + ", " + data[5];
	}
	
	/**
	 * Gets the end date.
	 * @return The end date.
	 */
	public String getEndDate() {
		if (end == null) {
			return "N/A";
		}
		String[] data = end.toString().split(" ");
		return data[1] + " " + data[2] + ", " + data[5];
	}
	
	/**
	 * Removes a photo from this album
	 * @param a is a photo to remove from this album
	 */
	public void removePhoto(String a) {
		for(Photo b: this.photos) {
			if(b.getName().equals(a)){
				this.photos.remove(b);
				break;
			}
		}
	}
}