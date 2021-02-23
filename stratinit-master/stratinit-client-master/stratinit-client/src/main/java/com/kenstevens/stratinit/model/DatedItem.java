package com.kenstevens.stratinit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatedItem {
	protected String dateString;
	@JsonIgnore
	private Date date;

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd HH:mm");

	public DatedItem() {
		super();
	}

	public String getDateString() {
		if (date == null) {
			if (dateString == null) {
				return "";
			} else {
				return dateString;
			}
		}
		return FORMAT.format(date);
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	protected void setDate(Date date) {
		this.date = date;
		if (dateString == null) {
			dateString = FORMAT.format(date);
		}
	}

	public Date getDate() {
		return date;
	}

}