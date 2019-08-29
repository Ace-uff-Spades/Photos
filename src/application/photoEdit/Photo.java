package application.photoEdit;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import application.Photos;
import application.tags.Tag;
import application.tags.TagType;

/**
 * Represents a photo.
 * @author Abhishek Kondila
 *
 */
public class Photo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2250112614096569637L;

	/**
	 * The path to this photo.
	 */
	private String path;
	
	/**
	 * The caption of this photo.
	 */
	private String caption = "";
	
	/**
	 * The date of this photo.
	 */
	private final Date date;
	
	/**
	 *  The photo location. 
	 */
	private String location = "";
	
	/**
	 *  The photo name.
	 */
	private String name;
	
	/**
	 * The tag arraylist.
	 */
	private ArrayList<Tag> tags = new ArrayList<>();
	
	/**
	 * Constructs a new {@code Album} object.
	 * @param name The album name.
	 * @param path the Path.
	 * @param date the date.
	 */
	public Photo(String name, String path, long date) {
		this.name = name;
		this.path = path;
		this.date = new Date(date);
	}
	
	/**
	 * Gets the photo's date.
	 * @return the photo's date
	 */
	public String getPhotoDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		return month + "/" + day + "/" + year;
	}

	/**
	 * Gets the file path to this photo.
	 * @return the path of this photo
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Sets the photo's location.
	 * @param location the location for this photo.
	 */
	public void setPhotoLocation(String location) {
		this.location = location;
	}
	/**
	 * Gets the location of the photo.
	 * @return the location of this photo
	 */
	public String getLocation() {
		return location;
	}
	
	/**
	 * Gets the caption of the photo
	 * @return the caption of this photo
	 */
	public String getCaption() {
		return caption;
	}
	
	/**
	 * Sets the caption of this photo
	 * @param a caption for this photo
	 */
	public void setCaption(String a) {
		this.caption = a;
	}
	
	/**
	 * Gets the name of the photo
	 * @return the name of this photo
	 */
	public String getName() {
		if (name.length() > 6) {
			return name.substring(0, 7) + "...";
		}
		return name;
	}
	
	/**
	 * Sets the name of this photo
	 * @param a the name for this photo
	 */
	public void setName(String a) {
		this.name = a;
	}
	
	/**
	 * Checks if we can add a tag
	 * @param check The check.
	 * @return True if so.
	 */
	public boolean canAddTag(Tag check) {
		for (Tag t : tags) {
			if (t.getKey().equalsIgnoreCase(check.getKey())) {
				for (TagType tt : Photos.currentUser.getTagTypes()) {
					if (tt.getName().equalsIgnoreCase(t.getKey()) && tt.isLimited()) {
						return false;
					}
				}
				if (t.getValue().equalsIgnoreCase(check.getValue())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return the tags
	 */
	public ArrayList<Tag> getTags() {
		return tags;
	}
	
	/**
	 * @return the date.
	 */
	public Date getDate() {
		return date;
	}
}