package application.tags;

import java.io.Serializable;

/**
 * Represents a tag type.
 * @author Kaavya Krishna-Kumar
 * @author Abhishek Kondila
 *
 */
public class TagType implements Serializable {
	
	/**
	 * The serialversion UID.
	 */
	private static final long serialVersionUID = 2652839806523440904L;

	/**
	 * The name of the tag type.
	 */
	private final String name;
	
	/**
	 * Whether this tag type is limited.
	 */
	private final boolean limited;
	
	/**
	 * Constructs a new {@code TagType} object.
	 * @param name The name of the tag type object.
	 * @param limited If true, a photo can only have one of this tag type.
	 */
	public TagType(String name, boolean limited) {
		this.name = name;
		this.limited = limited;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Whether the tagtype is limited.
	 * @return True if so.
	 */
	public boolean isLimited() {
		return this.limited;
	}

	@Override
	public String toString() {
		return "Name: " + name + " Limited: " + limited;
	}
}