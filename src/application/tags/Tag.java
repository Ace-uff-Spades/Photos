package application.tags;

import java.io.Serializable;

/**
 * This represents a tag.
 * @author Kaavya Krishna-Kumar	
 * @author Abhishek Kondila
 *
 */
public class Tag implements Serializable {

	/**
	 * The serial UID.
	 */
	private static final long serialVersionUID = 4289541410515978108L;

	/**
	 * They key for a tag.
	 */
	private String key;
	
	/**
	 * The value of a tag.
	 */
	private String value;
	
	/**
	 * Constructs a new {@code Tag} object.
	 * @param key The key of the tag.
	 * @param value The value of the tag.
	 */
	public Tag(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}