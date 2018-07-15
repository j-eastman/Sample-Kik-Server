package kikBot;

import chatterer.kik.Bot;
import chatterer.kik.Keyboard;
import chatterer.kik.Message;

public class KikBot extends Bot{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public KikBot(String username, String apikey, String webhook) {
		super(username, apikey, webhook);
	}

	@Override
	public Keyboard getDefaultKeyboard(String message) {
		return new Keyboard(new String[]{"Hello"},false);
	}

	@Override
	public void onDeliveryReceiptMessage(Message message) {
		// TODO: Handle Delivery Receipts
		
	}

	@Override
	public void onFriendPickerMessage(Message message) {
		// TODO: Handle Friend Picker
		
	}

	@Override
	public void onIsTypingMessage(Message message) {
		// TODO: handle is typing messages
		
	}

	@Override
	public void onLinkMessage(Message message) {
		// TODO: handle link messages
		
	}

	@Override
	public void onPictureMessage(Message message) {
		message.reply("Cool picture!");
		
	}

	@Override
	public void onReadReceiptMessage(Message message) {
		// TODO Handle Read Receipt
		
	}

	@Override
	public void onScanDataMessage(Message message) {
		// TODO Handle Scan Data
		
	}

	@Override
	public void onStartChattingMessage(Message message) {
		message.reply("Hey, new user!");
		
	}

	@Override
	public void onStickerMessage(Message message) {
		message.reply("Nice Sticker");
		
	}

	@Override
	public void onTextMessage(Message message) {
		message.reply("I haven't been programmed yet, check back later!");
		
	}

	@Override
	public void onVideoMessage(Message message) {
		message.reply("Cool video!");
		
	}

}
