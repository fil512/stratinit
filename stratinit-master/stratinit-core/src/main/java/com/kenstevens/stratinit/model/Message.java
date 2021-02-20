package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.Constants;
import com.querydsl.core.annotations.QueryInit;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@MappedSuperclass
public abstract class Message implements NewsWorthy {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd HH:mm");

	@Id
	@SequenceGenerator(name="message_id_seq", sequenceName="message_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="message_id_seq")
	private Integer messageId;
	@ManyToOne
	private Game game;
	@ManyToOne(optional = true)
	@QueryInit("nationPK.player")
	private Nation from;
	@ManyToOne(optional = true)
	@QueryInit("nationPK.player")
	private Nation to;
	private String subject = "";
	private Date date = new Date();
	// TODO * server manages read
	private boolean read = true;

	public Message() {
	}

	public Message(Game game, Nation from, Nation to, String subject) {
		this.game = game;
		this.from = from;
		this.to = to;
		this.subject = subject;
	}

	public abstract String getBody();

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		if (date == null) {
			return "";
		}
		return FORMAT.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setFrom(Nation from) {
		this.from = from;
	}

	public Nation getFrom() {
		return from;
	}

	public void setTo(Nation to) {
		this.to = to;
	}

	public Nation getTo() {
		return to;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isRead() {
		return read;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public String getAuthor() {
		if (from != null) {
			return from.toString();
		} else {
			return Constants.SERVER_MESSAGE;
		}
	}

	public String getRecipient() {
		if (to != null) {
			return to.toString();
		} else {
			return Constants.MESSAGE_BOARD;
		}
	}
}
