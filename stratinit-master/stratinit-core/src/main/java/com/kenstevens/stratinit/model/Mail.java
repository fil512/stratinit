package com.kenstevens.stratinit.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import com.kenstevens.stratinit.type.NewsCategory;

@Entity
public class Mail extends Message {
	private String body = "";
	@Transient
	private boolean post = false;

	public Mail() {}

	public Mail(Game game, Nation from, Nation to, String subject, String body) {
		super(game, from, to, subject);
		this.body = body;
	}

	public Mail(Date date, String subject) {
		this.setDate(date);
		this.setSubject(subject);
	}

	@Override
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.BULLETINS;
	}

	public void setPost(boolean post) {
		this.post = post;
	}

	public boolean isPost() {
		return post;
	}
}
