package com.kenstevens.stratinit.ui.messages;

import com.kenstevens.stratinit.model.Mail;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageBoxer {
	private final Mail message;
	private final boolean isSent;

	public MessageBoxer(Mail message, boolean isSent) {
		this.message = message;
		this.isSent = isSent;
	}

	public Mail forward() {
		Mail forwardedMessage = new Mail();
		forwardedMessage.setBody(encapsulateBody("Forwarded"));
		String subject = message == null ? "" : message.getSubject();
		if (subject.startsWith("FW: ")) {
			forwardedMessage.setSubject(subject);
		} else {
			forwardedMessage.setSubject("FW: "+subject);
		}
		return forwardedMessage;
	}

	private static final Pattern WRAP_REGEXP = Pattern.compile(".{0,79}([ $|\\s$]|$)");

	private static String[] wordWrap(String str) {
		List<String> list = new LinkedList<String>();
		Matcher m = WRAP_REGEXP.matcher(str);
		while (m.find()) {
			list.add(m.group());
		}
		return list.toArray(new String[list.size()]);
	}

	private String encapsulateBody(String prefix) {
		if (message == null) {
			return "";
		}
		String body = message.getBody();
		if (body == null || body.isEmpty()
				|| message.getDateString() == null) {
			return "";
		}
		String direction;
		String player;
		if (isSent) {
			direction = "to";
			player = message.getRecipient();
		} else {
			direction = "from";
			player = message.getAuthor();
		}
		String retval = prefix + " Message " + direction + " "
				+ player + " sent on " + message.getDateString()
				+ ":\n";
		String wrappedBody = StringUtils.join(wordWrap(body), "\r\n");
		String[] lines = StringUtils.split(wrappedBody, "\r\n");
		for (String line : lines) {
			retval += "> " + line + "\n";
		}
		retval += "\n";
		return retval;
	}

	public Mail reply() {
		if (message == null) {
			return null;
		}
		Mail repliedMessage = new Mail();
		repliedMessage.setBody(encapsulateBody("Reply to "));
		String subject = message.getSubject();
		if (subject.startsWith("RE: ")) {
			repliedMessage.setSubject(subject);
		} else {
			repliedMessage.setSubject("RE: "+subject);
		}
		repliedMessage.setTo(message.getFrom());
		return repliedMessage;
	}

	public Mail replyAsPost() {
		Mail repliedMessage = reply();
		if (repliedMessage == null) {
			return null;
		}
		repliedMessage.setTo(null);
		repliedMessage.setPost(true);
		return repliedMessage;
	}

	public static Mail newPost() {
		Mail newMessage = new Mail();
		newMessage.setPost(true);
		return newMessage;
	}
}
