package chatterer.kik;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Base64;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

/**
 * Basic construction blocks for a kik bot
 * 
 * @author Jack Eastman
 *
 */
public abstract class Bot implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String ENDPOINT = "https://api.kik.com/v1/";
	public static final String ENDPOINT_MSG = ENDPOINT + "message";
	public static final String ENDPOINT_CONFIG = ENDPOINT + "config";
	public static final String ENDPOINT_BROADCAST = ENDPOINT + "broadcast";
	public static final String ENDPOINT_USER = "https://api.kik.com/v1/user/";
	public static final int FEATURE_READ_RECEIPT = 0, FEATURE_IS_TYPING = 1, FEATURE_SEND_READ = 2,
			FEATURE_SEND_DELIVERY = 3;
	private String username;
	private String apikey;
	private String webhook;

	/**
	 * Construct a bot using username and password
	 * 
	 * @param username
	 *            Username of the bot being created
	 * @param apikey
	 *            API Key used for verification
	 */
	public Bot(String username, String apikey) {
		this.username = username;
		this.apikey = apikey;
	}

	/**
	 * Construct a bot using username, password, and webhook
	 * 
	 * @param username
	 *            Username of the bot being created
	 * @param apikey
	 *            API Key used for verification
	 * @param webhook
	 *            Webhook that the bot receives messages from
	 */
	public Bot(String username, String apikey, String webhook) {
		this.username = username;
		this.apikey = apikey;
		this.webhook = webhook;
		setDefaultConfig();
	}

	/**
	 * Returns username
	 * 
	 * @return bot's username
	 */
	public String getUser() {
		return username;
	}

	/**
	 * Returns API Key
	 * 
	 * @return bot's API Key
	 */
	public String getApi() {
		return apikey;
	}

	/**
	 * Constructs encoded authorization token
	 * 
	 * @return Bot's auth token
	 */
	public String getAuthToken() {
		try {
			return "Basic " + Base64.getEncoder().encodeToString(((username + ":" + apikey).getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding!");
			return null;
		}
	}

	/**
	 * Sets bot's default configuration
	 */
	public void setConfig() {
		JSONObject configs = new JSONObject();
		JSONObject features = new JSONObject();
		configs.put("webhook", webhook);
		features.put("receiveReadReceipts", false).put("receiveIsTyping", false).put("manuallySendReadReceipts", false)
				.put("receiveDeliveryReceipts", false);
		configs.put("features", features);
		try {
			post(ENDPOINT_CONFIG, configs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets bot's default configuration
	 */
	public void setDefaultConfig() {
		try {
			setConfig(new boolean[] { true, false, false, false });
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * Sets bot's configuration equal to values
	 * 
	 * @param values
	 *            Values to be passed through to configuration
	 */
	public void setConfig(boolean[] values) {
		JSONObject configs = new JSONObject();
		JSONObject features = new JSONObject();
		configs.put("webhook", webhook);
		features.put("receiveReadReceipts", values[FEATURE_READ_RECEIPT])
				.put("receiveIsTyping", values[FEATURE_IS_TYPING])
				.put("manuallySendReadReceipts", values[FEATURE_SEND_READ])
				.put("receiveDeliveryReceipts", values[FEATURE_SEND_DELIVERY]);
		configs.put("features", features);
		try {
			post(ENDPOINT_CONFIG, configs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send a simple text message to a specific user
	 * 
	 * @param message
	 *            Text to send to user
	 * @param username
	 *            Username of intended recipient
	 * @return HTTP status code
	 */
	public String send(String message, String username) {
		try {
			return post(ENDPOINT_MSG, getJSON(message, username));
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
	}

	/**
	 * Send message using prebuilt JSON
	 * 
	 * @param json
	 *            Prebuilt JSON
	 * @return HTTP status code
	 * @throws IOException server responds with error code
	 */
	public String send(JSONObject json) throws IOException {
		return post(ENDPOINT_MSG, json);
	}

	/**
	 * Send post request to kik server with JSON payload
	 * 
	 * @param endpoint
	 *            Kik endpoint to send to
	 * @param json
	 *            JSON payload
	 * @return HTTP status code
	 * @throws IOException server responds with error code
	 */
	public String post(String endpoint, JSONObject json) throws IOException {
		URL obj = null;
		obj = new URL(endpoint);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		String out = json.toString();
		con.setRequestProperty("Authorization", getAuthToken());
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.write(out.getBytes("utf-8"));
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		if (responseCode != 200) {
			System.out.println("Server responded with: " + responseCode);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append("\n");
		}
		in.close();
		return response.toString();
	}

	/**
	 * Sends broadcast message to up to 100 users in a single batch
	 * 
	 * @param message
	 *            Message being sent out
	 * @param usernames
	 *            Array of all the usernames of the intended recipients
	 */
	public void broadcast(String message, String[] usernames) {
		System.out.println("Sending broadcast message.");
		JSONObject[] messages = new JSONObject[usernames.length];
		for (int i = 0; i < messages.length; i++) {
			JSONObject temp = new JSONObject();
			temp.put("body", message).put("to", usernames[i]).put("type", "text");
			messages[i] = temp;
		}
		JSONObject out = new JSONObject();
		out.put("messages", messages);
		try {
			post(ENDPOINT_BROADCAST, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches a user's data from kik servers
	 * 
	 * @param username
	 *            User to fetch
	 * @return JSON object containing all information on user
	 * @throws IOException
	 *             IOException
	 */
	public JSONObject getUserData(String username) throws IOException {
		URL obj = null;
		obj = new URL(ENDPOINT_USER + username);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		con.setRequestProperty("Authorization", getAuthToken());
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		int responseCode = con.getResponseCode();
		if (responseCode != 200) {
			System.out.println("Server responded with: " + responseCode);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append("\n");
		}
		in.close();
		return new JSONObject(response.toString());
	}

	public String getPfpLink(String username) {
		try {
			JSONObject userdata = getUserData(username);
			return userdata.getString("profilePicUrl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return final JSON for use with post(JSONObject)
	 * 
	 * @param message
	 *            String to send to user
	 * @param username
	 *            Username of intended recipient
	 * @return Properly formatted JSONObject
	 */
	public static JSONObject getJSON(String message, String username) {
		JSONObject retVal = new JSONObject();
		JSONObject mes = new JSONObject();
		mes.put("body", message).put("to", username).put("type", "text");
		JSONObject[] arr = { mes };
		retVal.put("messages", arr);
		return retVal;
	}

	/**
	 * Return JSON to be added with other JSONObjects
	 * 
	 * @param message
	 *            String to send to user
	 * @param username
	 *            Username of intended recipient
	 * @return Properly formatted JSONObject
	 */
	public static JSONObject getSingleJSON(String message, String username) {
		JSONObject mes = new JSONObject();
		mes.put("body", message).put("to", username).put("type", "text");
		return mes;
	}

	public abstract Keyboard getDefaultKeyboard(String from);

	public abstract void onTextMessage(Message message);

	public abstract void onStartChattingMessage(Message message);

	public abstract void onPictureMessage(Message message);

	public abstract void onVideoMessage(Message message);

	public abstract void onLinkMessage(Message message);

	public abstract void onFriendPickerMessage(Message message);

	public abstract void onStickerMessage(Message message);

	public abstract void onDeliveryReceiptMessage(Message message);

	public abstract void onIsTypingMessage(Message message);

	public abstract void onScanDataMessage(Message message);

	public abstract void onReadReceiptMessage(Message message);

	public String getUsername() {
		return username;
	}

	public boolean equals(String username) {
		return this.username.equalsIgnoreCase(username);
	}

}
