package client;

/**
 * Mutable boolean object to allow tracking flags to be passed by reference
 * @author Josh
 *
 */
public class Tracker {
	private boolean value;
	
	/**
	 * Initializes flag to value
	 * @param value
	 */
	public Tracker(boolean value) {
		this.value = value;
	}
	
	/**
	 * Setter
	 * @param value
	 */
	public void setValue(boolean value){
		this.value = value;
	}
	
	/**
	 * Getter
	 * @return
	 */
	public boolean getValue() {
		return value;
	}
}
