package chatterer.kik;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Object representation of message received by the bot
 * 
 * @author Jack Eastman
 *
 */
public class Message implements Serializable{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	public static final String TEXT = "text", FRIEND_PICKER = "friend-picker", STICKER = "sticker",
			DELIVERY_RECEIPT = "delivery-receipt", LINK = "link", IMAGE = "picture", IS_TYPING = "is-typing",
			START_CHATTING = "start-chatting", READ_RECEIPT = "read-receipt", VIDEO = "video", SCAN_DATA = "scan-data";
	public static final int TYPE_TEXT = 0, TYPE_FRIEND_PICKER = 1, TYPE_STICKER = 2, TYPE_DELIVERY_RECEIPT = 3,
			TYPE_LINK = 4, TYPE_IMAGE = 5, TYPE_IS_TYPING = 6, TYPE_START_CHATTING = 7, TYPE_READ_RECEIPT = 8,
			TYPE_VIDEO = 9, TYPE_SCAN_DATA = 10;
	public String chatId, id, type, from, body, timestamp, readReceiptRequested, mention, metadata, picUrl, videoUrl,
			chatType;
	public static final String ICON_URL = ""; //TODO: FILL ME IN
	public String[] participants;
	public Keyboard keyboard;
	public boolean isDirect;
	transient JSONObject incoming;
	transient JSONObject attribution;
	transient ArrayList<JSONObject> responses; // Referred to as "JSON list" below
	ArrayList<Object[]> resps;
	int typeTime = -1;
	public Bot bot;

	/**
	 * Constructs new message given incoming JSON and bot
	 * 
	 * @param incoming
	 *            Incoming json message
	 * @param bot
	 *            Bot the message was sent to
	 */
	public Message(JSONObject incoming, Bot bot) {
		this.bot = bot;
		this.incoming = incoming;
		chatId = getString("chatId");
		id = getString("id");
		type = getString("type");
		body = getString("body");
		from = getString("from");
		timestamp = getString("timestamp");
		readReceiptRequested = getString("readReceiptRequested");
		mention = getString("mention");
		metadata = getString("metadata");
		chatType = getString("chatType");
		if (chatType != null && chatType.equalsIgnoreCase("direct")){
			isDirect = true;
		} else {
			isDirect = false;
		}
		picUrl = getString("picUrl");
		videoUrl = getString("videoUrl");
		getParticipants();
		keyboard = bot.getDefaultKeyboard(from);
		if (body != null && body.equalsIgnoreCase("help")) {
			keyboard.isHidden = false;
		}
		responses = new ArrayList<JSONObject>();
		resps = new ArrayList<Object[]>();
	}

	/**
	 * Gets value for certain key from incoming JSON
	 * 
	 * @param key
	 *            Key to search for
	 * @return String value of key field
	 */
	public String getString(String key) {
		try {
			if (incoming.has(key)) {
				return incoming.getString(key);
			} else {
				return null;
			}
		} catch (JSONException e) {

		}
		return null;
	}

	/**
	 * Generate String array of participants given the JSONArray
	 */
	public void getParticipants() {
		try {
			JSONArray arr = incoming.getJSONArray("participants");
			participants = new String[arr.length()];
			for (int i = 0; i < arr.length(); i++) {
				participants[i] = arr.getString(i);
			}
		} catch (JSONException e) {
		}
	}

	/**
	 * Check whether chat participants include a given username
	 * 
	 * @param username
	 *            Username to search for
	 * @return Whether or not that user is in the chat
	 */
	public boolean includes(String username) {
		if (participants != null) {
			for (String name : participants) {
				if (name.equalsIgnoreCase(username)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Add text reply to waiting JSON list
	 * 
	 * @param message
	 *            Message to add
	 */
	public void addReply(String message) {
		Object[] temp = new Object[2];
		temp[0] = "string";
		temp[1] = message;
		resps.add(temp);
		responses.add(getSingleJSON(message));
	}

	public void addPicReply(String url) {
		Object[] temp = new Object[2];
		temp[0] = "pic";
		temp[1] = new String[] { url, null, null };
		resps.add(temp);
		responses.add(getPicMsgJSON(url, null, null));
	}
	
	/**
	 * Add picture reply to waiting JSON list
	 * 
	 * @param url
	 *            URL of picture to send
	 * @param attrib
	 *            Desired attribution of image
	 */
	public void addPicReply(String url, String attrib) {
		Object[] temp = new Object[2];
		temp[0] = "pic";
		temp[1] = new String[] { url, attrib, ICON_URL };
		resps.add(temp);
		responses.add(getPicMsgJSON(url, attrib, ICON_URL));
	}

	public void addPicReply(String url, String attrib, String iconUrl) {
		Object[] temp = new Object[2];
		temp[0] = "pic";
		temp[1] = new String[] { url, attrib, iconUrl };
		resps.add(temp);
		responses.add(getPicMsgJSON(url, attrib, iconUrl));
	}

	public void addVideoReply(String url) {
		Object[] temp = new Object[2];
		temp[0] = "video";
		temp[1] = new String[] { url, null, null };
		resps.add(temp);
		responses.add(getVideoMsgJSON(url, null, null));
	}

	public void addVideoReply(String url, String attrib) {
		Object[] temp = new Object[2];
		temp[0] = "video";
		temp[1] = new String[] { url, attrib, ICON_URL };
		resps.add(temp);
		responses.add(getVideoMsgJSON(url, attrib, ICON_URL));
	}

	public void addVideoReply(String url, String attrib, String iconUrl) {
		Object[] temp = new Object[2];
		temp[0] = "video";
		temp[1] = new String[] { url, attrib, iconUrl };
		resps.add(temp);
		responses.add(getVideoMsgJSON(url, attrib, iconUrl));
	}

	public void addLinkReply(String url) {
		Object[] temp = new Object[2];
		temp[0] = "link";
		temp[1] = new Object[] { url, null, null, null, null };
		resps.add(temp);
		responses.add(getLinkMsgJSON(url, null, null, null, null));
	}

	public void addLinkReply(String url, String title) {
		Object[] temp = new Object[2];
		temp[0] = "link";
		temp[1] = new Object[] { url, title, null, null, null };
		resps.add(temp);
		responses.add(getLinkMsgJSON(url, title, null, null, null));
	}

	public void addLinkReply(String url, String title, String text) {
		Object[] temp = new Object[2];
		temp[0] = "link";
		temp[1] = new Object[] { url, title, text, null, null };
		resps.add(temp);
		responses.add(getLinkMsgJSON(url, title, text, null, null));
	}

	public void addLinkReply(String url, String title, String text, String picUrl) {
		Object[] temp = new Object[2];
		temp[0] = "link";
		temp[1] = new Object[] { url, title, text, picUrl, null };
		resps.add(temp);
		responses.add(getLinkMsgJSON(url, title, text, picUrl, null));
	}

	public void addLinkReply(String url, String title, String text, String picUrl, String[] attrib) {
		Object[] temp = new Object[2];
		temp[0] = "link";
		temp[1] = new Object[] { url, title, text, picUrl, attrib };
		resps.add(temp);
		responses.add(getLinkMsgJSON(url, title, text, picUrl, attrib));
	}

	public void addLinkReply(String url, String title, String text, String picUrl, String attrib) {
		String[] attribution = { attrib, ICON_URL };
		Object[] temp = new Object[2];
		temp[0] = "link";
		temp[1] = new Object[] { url, title, text, picUrl, attribution };
		resps.add(temp);
		responses.add(getLinkMsgJSON(url, title, text, picUrl, attribution));
	}

	/**
	 * Add already formatted JSON to waiting JSON list
	 * 
	 * @param reply
	 *            Formatted JSON to add
	 */
	public void addReply(JSONObject reply) {
		responses.add(reply);
	}

	/**
	 * Check whether the JSON list contains messages waiting to be sent
	 * 
	 * @return Whether the JSON list contains messages to be sent
	 */
	public boolean hasMessage() {
		if (responses.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Send all messages waiting in JSON list
	 */
	public void reply() {
		JSONObject out = new JSONObject();
		if (responses.size() > 0) {
			try {
				JSONObject[] msgs = new JSONObject[Math.min(25, responses.size())];
				for (int i = 0; i < Math.min(25, responses.size()); i++) {
					msgs[i] = responses.get(i);
				}
				out.put("messages", msgs);
				bot.send(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reply with a single text response
	 * 
	 * @param message
	 *            Desired text response
	 */
	public void reply(String message) {
		try {
			bot.send(getJSON(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines type of incoming message
	 * 
	 * @return Integer representation of type of incoming message
	 */
	public int getType() {
		if (type.equals(TEXT)) {
			return TYPE_TEXT;
		}
		if (type.equals(FRIEND_PICKER)) {
			return TYPE_FRIEND_PICKER;
		}
		if (type.equals(STICKER)) {
			return TYPE_STICKER;
		}
		if (type.equals(DELIVERY_RECEIPT)) {
			return TYPE_DELIVERY_RECEIPT;
		}
		if (type.equals(LINK)) {
			return TYPE_LINK;
		}
		if (type.equals(IMAGE)) {
			return TYPE_IMAGE;
		}
		if (type.equals(IS_TYPING)) {
			return TYPE_IS_TYPING;
		}
		if (type.equals(START_CHATTING)) {
			return TYPE_START_CHATTING;
		}
		if (type.equals(READ_RECEIPT)) {
			return TYPE_READ_RECEIPT;
		}
		if (type.equals(VIDEO)) {
			return TYPE_VIDEO;
		}
		if (type.equals(SCAN_DATA)) {
			return TYPE_SCAN_DATA;
		}
		return TYPE_TEXT;
	}

	/**
	 * Set amount of time the bot will appear to be typing before sending the
	 * message
	 * 
	 * @param typeTime
	 *            Time (in milliseconds) the bot will appear to be typing
	 */
	public void setTypeTime(int typeTime) {
		this.typeTime = typeTime;
	}

	/**
	 * Adds suggested response keyboard to message (defaults to hidden keyboard)
	 * 
	 * @param responses
	 *            Array of suggested responses
	 */
	public void addKeyboard(String[] responses) {
		keyboard = new Keyboard(responses);
	}

	/**
	 * Adds suggested response keyboard to message with given value for isHidden
	 * 
	 * @param responses
	 *            Array of suggested responses
	 * @param isHidden
	 *            Whether or not the keyboard should appear as hidden
	 */
	public void addKeyboard(String[] responses, boolean isHidden) {
		keyboard = new Keyboard(responses, isHidden);
	}

	/**
	 * Add prebuilt keyboard to message
	 * 
	 * @param keyboard
	 *            Keyboard to add
	 */
	public void addKeyboard(Keyboard keyboard) {
		this.keyboard = keyboard;
	}

	/**
	 * Add attribution to outgoing picture message
	 * 
	 * @param name
	 *            Attribution name
	 * @param iconUrl
	 *            Image Icon URL
	 */
	public void addAttribution(String name, String iconUrl) {
		attribution = new JSONObject();
		attribution.put("name", name);
		attribution.put("iconUrl", iconUrl);
	}

	/**
	 * Format JSON given a response
	 * 
	 * @param response
	 *            Bot's response to message
	 * @return Formatted JSON
	 */
	public JSONObject getJSON(String response) {
		// body, to, type, chatId
		JSONObject retVal = new JSONObject();
		JSONObject message = new JSONObject();
		response = response.substring(0, Math.min(2000, response.length()));
		message.put("body", response).put("to", from).put("type", "text").put("chatId", chatId);
		if (typeTime > 0) {
			message.put("typeTime", typeTime);
		}
		if (keyboard != null) {
			message.put("keyboards", keyboard.getKeyboard());
		}
		JSONObject[] arr = { message };
		retVal.put("messages", arr);
		return retVal;
	}

	/**
	 * Generate JSON for outgoing picture messages
	 * 
	 * @param imgUrl
	 *            Picture URL
	 * @param attrib
	 *            Attribution name
	 * @param iconUrl
	 *            Icon URL
	 * @return Formatted JSON
	 */
	public JSONObject getPicMsgJSON(String imgUrl, String attrib, String iconUrl) {
		JSONObject message = new JSONObject();
		JSONObject attribution = new JSONObject();
		if (attrib != null) {
			attribution.put("name", attrib);
			if (iconUrl != null) {
				attribution.put("iconUrl", iconUrl);
			}
		}
		message.put("type", Message.IMAGE).put("picUrl", imgUrl);
		message.put("to", from).put("chatId", chatId).put("attribution", attribution);
		if (typeTime > 0) {
			message.put("typeTime", typeTime);
		}
		if (keyboard != null) {
			message.put("keyboards", keyboard.getKeyboard());
		}
		return message;
	}

	public JSONObject getVideoMsgJSON(String videoUrl, String attrib, String iconUrl) {
		JSONObject message = new JSONObject();
		JSONObject attribution = new JSONObject();
		if (attrib != null) {
			attribution.put("name", attrib);
			if (iconUrl != null) {
				attribution.put("iconUrl", iconUrl);
			}
		}
		message.put("type", Message.VIDEO).put("videoUrl", videoUrl);
		message.put("to", from).put("chatId", chatId).put("attribution", attribution);
		if (typeTime > 0) {
			message.put("typeTime", typeTime);
		}
		if (keyboard != null) {
			message.put("keyboards", keyboard.getKeyboard());
		}
		return message;
	}

	public Keyboard getDefaultKeyboard() {
		return bot.getDefaultKeyboard(from);
	}

	public JSONObject getLinkMsgJSON(String url, String title, String text, String picUrl, String[] attrib) {
		JSONObject message = new JSONObject();
		if (attrib != null) {
			JSONObject attribution = new JSONObject();
			attribution.put("name", attrib[0]);
			attribution.put("iconUrl", attrib[1]);
			message.put("attribution", attribution);
		}

		message.put("type", "link");
		message.put("url", url);
		if (title != null) {
			message.put("title", title);
		}
		if (text != null) {
			message.put("text", text);
		}
		if (picUrl != null) {
			message.put("picUrl", picUrl);
		}
		message.put("to", from).put("chatId", chatId);
		return message;
	}

	/**
	 * Create single JSON object for text message to be added to waiting list
	 * 
	 * @param response
	 *            Text response
	 * @return Formatted JSON
	 */
	public JSONObject getSingleJSON(String response) {
		if (response.equals("") || response.equals(" ") || response == null) {
			response = "What?";
		}
		JSONObject message = new JSONObject();
		message.put("body", response).put("to", from).put("type", "text").put("chatId", chatId);
		if (typeTime > 0) {
			message.put("typeTime", typeTime);
		}
		if (keyboard != null) {
			message.put("keyboards", keyboard.getKeyboard());
		}
		return message;
	}

	public boolean isPrivate(){
		return chatType.equalsIgnoreCase("private");
	}
	public boolean isPublic(){
		return chatType.equalsIgnoreCase("public");
	}
	public boolean isDirect(){
		return chatType.equalsIgnoreCase("direct");
	}

}
