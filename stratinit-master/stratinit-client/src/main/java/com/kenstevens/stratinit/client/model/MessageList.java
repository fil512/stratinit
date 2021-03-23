package com.kenstevens.stratinit.client.model;

import com.google.common.collect.Lists;

import java.util.*;

public class MessageList implements Iterable<Message> {
	private final List<Message>list = new ArrayList<Message>();
	private final Map<Integer, Message> map = new Hashtable<Integer, Message>();

	public Message get(int id) {
		return list.get(id);
	}

	private void add(Message message) {
		Integer key = message.getMessageId();
		Message orig = map.get(key);
		if (orig == null) {
			list.add(message);
			map.put(key, message);
		}
	}
	
	public int size() {
		return list.size();
	}

	public void addAll(List<Message> entries) {
		if (list == null) {
			return;
		}
		for (Message entry : entries) {
			add(entry);
		}
		sort();
	}
	
	private void sort() {
		Comparator<Message> byDate = new Comparator<Message>() {
			public int compare(Message e1, Message e2) {
				return e2.getDate().compareTo(e1.getDate());
			}
		};
		Collections.sort(list, byDate);
	}

	@Override
	public Iterator<Message> iterator() {
		return list.iterator();
	}
	
	public List<Message> newMessages(Date date) {
		List<Message> retval = Lists.newArrayList();
		for (Message message : list) {
			if (message.getDate().after(date)) {
				retval.add(message);
			}
		}
		return retval;
	}
}
