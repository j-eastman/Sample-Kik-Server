package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import chatterer.kik.Message;
import kikBot.KikBot;
import launch.Main;

@WebServlet("/kik")
public class KikServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServletOutputStream out = resp.getOutputStream();
		out.println("There is nothing for you here.");
		out.flush();
		out.close();
	}

	/**
	 * This will receive post requests to www.<your domain>.com/kik
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
		// String header = req.getHeader("X-Kik-Username"); // retrieves
		// username of bot if you want to have more than one per servlet
		KikBot bot = Main.bot;
		String json = "";
		String line;
		while ((line = br.readLine()) != null && !line.equals("")) {
			json += line;
		}
		JSONObject first = null;
		JSONArray messages = null;
		try {
			first = new JSONObject(json);
			messages = first.getJSONArray("messages");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < messages.length(); i++) {

			Message message = null;
			try {
				message = new Message(messages.getJSONObject(i), bot);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			message.setTypeTime(1000);
			int type = message.getType();
			//This switch statement triggers the various ways of dealing with incoming messages
			switch (type) {
			case Message.TYPE_TEXT:
				try {
					bot.onTextMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
					message.reply("Error while attempting to respond: " + e.getMessage());
				}
				break;
			case Message.TYPE_FRIEND_PICKER:
				bot.onFriendPickerMessage(message);
				break;
			case Message.TYPE_STICKER:
				bot.onStickerMessage(message);
				break;
			case Message.TYPE_DELIVERY_RECEIPT:
				bot.onDeliveryReceiptMessage(message);
				break;
			case Message.TYPE_IMAGE:
				bot.onPictureMessage(message);
				break;
			case Message.TYPE_IS_TYPING:
				bot.onIsTypingMessage(message);
				break;
			case Message.TYPE_START_CHATTING:
				bot.onStartChattingMessage(message);
				break;
			case Message.TYPE_READ_RECEIPT:
				bot.onReadReceiptMessage(message);
				break;
			case Message.TYPE_VIDEO:
				bot.onVideoMessage(message);
				break;
			case Message.TYPE_SCAN_DATA:
				bot.onScanDataMessage(message);
				break;
			case Message.TYPE_LINK:
				bot.onLinkMessage(message);
				break;
			default:
				bot.onTextMessage(message);
				break;
			}
		}
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
}
