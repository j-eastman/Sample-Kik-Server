package chatterer.kik;

import java.io.Serializable;
import java.util.Arrays;

import org.json.JSONObject;

/**
 * Keyboard object representation
 * 
 * @author Jack Eastman
 *
 */
public class Keyboard implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] responses;
	String[] metadata;
	public boolean isHidden = true;
	boolean hasMeta = false;

	public Keyboard(String[] responses) {
		this.responses = responses;
	}

	/**
	 * Generate keyboard with plain text options
	 * 
	 * @param responses
	 *            Text options
	 * @param isHidden
	 *            Whether or not the keyboard is hidden
	 */
	public Keyboard(String[] responses, boolean isHidden) {
		this.responses = responses;
		this.isHidden = isHidden;
	}

	/**
	 * Generate keyboard with metadata
	 * 
	 * @param responses
	 *            2D array containing {text,metadata}
	 * @param isHidden
	 *            Whether or not the keyboard is hiddden
	 */
	public Keyboard(String[][] responses, boolean isHidden) {
		hasMeta = true;
		this.responses = new String[responses.length];
		this.metadata = new String[responses.length];
		for (int i = 0; i < responses.length; i++) {
			this.responses[i] = responses[i][0];
			this.metadata[i] = responses[i][1];
		}
		this.isHidden = isHidden;
	}
	
	public static Keyboard getKeyboardWithMeta(String[] responses, boolean isHidden){
		String[] metadata = new String[responses.length];
		for (int i = 0; i < responses.length; i++){
			metadata[i] = String.valueOf(i);
		}
		return new Keyboard(new String[][] {responses,metadata},isHidden);
	}

	/**
	 * Format keyboard into JSONObject
	 * 
	 * @return Formatted JSON
	 */
	public JSONObject[] getKeyboard() {
		JSONObject retVal = new JSONObject();
		if (responses != null) {
			JSONObject[] keys = new JSONObject[responses.length];
			if (hasMeta) {
				for (int i = 0; i < responses.length; i++) {
					JSONObject temp = new JSONObject();
					temp.put("type", "text");
					temp.put("body", responses[i]);
					temp.put("metadata", metadata[i]);
					keys[i] = temp;
				}
			} else {
				for (int i = 0; i < responses.length; i++) {
					JSONObject temp = new JSONObject();
					temp.put("type", "text");
					temp.put("body", responses[i]);
					keys[i] = temp;
				}
			}
			retVal.put("type", "suggested");
			retVal.put("hidden", isHidden);
			retVal.put("responses", keys);
			return new JSONObject[] { retVal };
		} else {
			retVal.put("type", "suggested");
			retVal.put("hidden", true);
			retVal.put("responses", new JSONObject[1]);
			return new JSONObject[] { retVal };
		}

	}

	public String toString() {
		return Arrays.toString(responses);
	}
}