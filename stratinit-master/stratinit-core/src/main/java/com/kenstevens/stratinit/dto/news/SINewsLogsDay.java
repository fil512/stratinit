package com.kenstevens.stratinit.dto.news;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class SINewsLogsDay implements Serializable {
	private static final long serialVersionUID = 1L;
	private final int day;
	private final List<SINewsBulletin> bulletins = new ArrayList<SINewsBulletin>();
	private final List<SINewsFirst> firsts = new ArrayList<SINewsFirst>();
	private final Multiset<SINewsNeutralConquest> neutralConquests = HashMultiset.create();
	private final Multiset<SINewsNuclearDetonations> nuclearDetonations = HashMultiset.create();
	private final Multiset<SINewsAirDefense> airDefense = HashMultiset.create();
	private final List<SINewsForeignAffairs> foreignAffairs = new ArrayList<SINewsForeignAffairs>();
	private final Multiset<SINewsFromTheFront> newsFromTheFront = HashMultiset.create();
	private final Multiset<SINewsOpponentConquest> opponentConquest = HashMultiset.create();

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
		return Lists.transform(Lists.newArrayList(neutralConquests.entrySet()), new Function<Multiset.Entry<SINewsNeutralConquest>, SINewsNeutralConquest>() {
			public SINewsNeutralConquest apply(Entry<SINewsNeutralConquest> entry) {
				return new SINewsNeutralConquest(entry.getElement().nationName, entry.getCount());
			}
		});
	}

	public void addNeutralConquest(SINewsNeutralConquest siNewsNeutralConquest) {
		neutralConquests.add(siNewsNeutralConquest, siNewsNeutralConquest.count);
	}

	public List<SINewsNuclearDetonations> getNuclearDetonations() {
		return Lists.transform(Lists.newArrayList(nuclearDetonations.entrySet()), new Function<Multiset.Entry<SINewsNuclearDetonations>, SINewsNuclearDetonations>() {
			public SINewsNuclearDetonations apply(Entry<SINewsNuclearDetonations> entry) {
				SINewsNuclearDetonations element = entry.getElement();
				return new SINewsNuclearDetonations(element.nationName, element.launchableUnit, element.opponentName, entry.getCount());
			}
		});
	}

	public void addNuclearDetonations(
			SINewsNuclearDetonations siNewsNuclearDetonations) {
		nuclearDetonations.add(siNewsNuclearDetonations, siNewsNuclearDetonations.count);
	}

	public List<SINewsAirDefense> getAirDefense() {
		return Lists.transform(Lists.newArrayList(airDefense.entrySet()), new Function<Multiset.Entry<SINewsAirDefense>, SINewsAirDefense>() {
			public SINewsAirDefense apply(Entry<SINewsAirDefense> entry) {
				SINewsAirDefense element = entry.getElement();
				return new SINewsAirDefense(element.nationName, element.nationUnitType, element.opponentName, entry.getCount());
			}
		});
	}

	public void addAirDefense(SINewsAirDefense siNewsAirDefense) {
		airDefense.add(siNewsAirDefense, siNewsAirDefense.count);
	}

	public List<SINewsForeignAffairs> getForeignAffairs() {
		return foreignAffairs;
	}

	public void addForeignAffairs(List<SINewsForeignAffairs> foreignAffairs) {
		this.foreignAffairs.addAll(foreignAffairs);
	}

	public List<SINewsFromTheFront> getNewsFromTheFront() {
		return Lists.transform(Lists.newArrayList(newsFromTheFront.entrySet()), new Function<Multiset.Entry<SINewsFromTheFront>, SINewsFromTheFront>() {
			public SINewsFromTheFront apply(Entry<SINewsFromTheFront> entry) {
				SINewsFromTheFront element = entry.getElement();
				return new SINewsFromTheFront(element.nationName, element.nationUnitType, element.opponentName, element.opponentUnitType, element.killed, entry.getCount());
			}
		});
	}

	public void addNewsFromTheFront(SINewsFromTheFront siNewsFromTheFront) {
		newsFromTheFront.add(siNewsFromTheFront, siNewsFromTheFront.count);
	}

	public List<SINewsOpponentConquest> getOpponentConquest() {
		return Lists.transform(Lists.newArrayList(opponentConquest.entrySet()), new Function<Multiset.Entry<SINewsOpponentConquest>, SINewsOpponentConquest>() {
			public SINewsOpponentConquest apply(Entry<SINewsOpponentConquest> entry) {
				SINewsOpponentConquest element = entry.getElement();
				return new SINewsOpponentConquest(element.nationName, element.opponentName, entry.getCount());
			}
		});
	}

	public void addOpponentConquest(
			SINewsOpponentConquest siNewsOpponentConquest) {
		SINewsOpponentConquest reverseConquest = siNewsOpponentConquest.reverse();
		if (opponentConquest.count(reverseConquest) > 0) {
			// TODO REF should really compare reverse to count and flip it
			opponentConquest.remove(reverseConquest);
		} else {
			opponentConquest.add(siNewsOpponentConquest, siNewsOpponentConquest.count);
		}
	}

	public int getDay() {
		return day;
	}
}
