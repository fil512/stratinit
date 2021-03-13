package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.dto.StratInitDTO;

import java.util.ArrayList;
import java.util.List;

public class SINewsLogsDay implements StratInitDTO {
	private static final long serialVersionUID = 1L;
	private final int day;
	private final List<SINewsBulletin> bulletins = new ArrayList<>();
	private final List<SINewsFirst> firsts = new ArrayList<>();
	private final List<SINewsForeignAffairs> foreignAffairs = new ArrayList<>();

	private final List<SINewsNeutralConquest> neutralConquests = new ArrayList<>();
	private final List<SINewsNuclearDetonations> nuclearDetonations = new ArrayList<>();
	private final List<SINewsAirDefense> airDefense = new ArrayList<>();
	private final List<SINewsFromTheFront> newsFromTheFront = new ArrayList<>();
	private final List<SINewsOpponentConquest> opponentConquest = new ArrayList<>();

	public SINewsLogsDay(int day) {
		this.day = day;
	}

	public SINewsLogsDay() {
		this.day = -1;
	}

	public List<SINewsBulletin> getBulletins() {
		return bulletins;
	}

	public void addBulletins(List<SINewsBulletin> bulletins) {
		this.bulletins.addAll(bulletins);
	}

	public List<SINewsFirst> getFirsts() {
		return firsts;
	}

	public void addFirsts(List<SINewsFirst> firsts) {
		this.firsts.addAll(firsts);
	}

	public List<SINewsNeutralConquest> getNeutralConquests() {
		return neutralConquests;
	}

	public void addNeutralConquest(SINewsNeutralConquest siNewsNeutralConquest) {
		add(neutralConquests, siNewsNeutralConquest);
	}

	private <T extends SINewsCountable> void add(List<T> list, T item) {
		int index = list.indexOf(item);
		if (index == -1) {
			list.add(item);
		} else {
			list.get(index).increment(item.getCount());
		}
	}

	public List<SINewsNuclearDetonations> getNuclearDetonations() {
		return nuclearDetonations;
	}

	public void addNuclearDetonations(SINewsNuclearDetonations siNewsNuclearDetonations) {
		add(nuclearDetonations, siNewsNuclearDetonations);
	}

	public List<SINewsAirDefense> getAirDefense() {
		return airDefense;
	}

	public void addAirDefense(SINewsAirDefense siNewsAirDefense) {
		add(airDefense, siNewsAirDefense);
	}

	public List<SINewsForeignAffairs> getForeignAffairs() {
		return foreignAffairs;
	}

	public void addForeignAffairs(List<SINewsForeignAffairs> foreignAffairs) {
		this.foreignAffairs.addAll(foreignAffairs);
	}

	public List<SINewsFromTheFront> getNewsFromTheFront() {
		return newsFromTheFront;
	}

	public void addNewsFromTheFront(SINewsFromTheFront siNewsFromTheFront) {
		add(newsFromTheFront, siNewsFromTheFront);
	}

	public List<SINewsOpponentConquest> getOpponentConquest() {
		return opponentConquest;
	}

	public void addOpponentConquest(SINewsOpponentConquest siNewsOpponentConquest) {
		add(opponentConquest, siNewsOpponentConquest);
	}

	public int getDay() {
		return day;
	}
}
