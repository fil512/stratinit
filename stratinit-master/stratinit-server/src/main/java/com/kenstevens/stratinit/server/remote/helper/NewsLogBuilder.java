package com.kenstevens.stratinit.server.remote.helper;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.*;
import com.kenstevens.stratinit.dto.news.*;
import com.kenstevens.stratinit.dto.news.translator.BulletinToSINewsBulletin;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.GameScheduleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NewsLogBuilder {
	private final Comparator<NewsWorthy> byDate = new Comparator<NewsWorthy>() {
		public int compare(NewsWorthy e1, NewsWorthy e2) {
			return e1.getDate().compareTo(e2.getDate());
		}
	};
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private LogDao logDao;
	@Autowired
	private PlayerDao playerDao;

	public List<SINewsLogsDay> getNews(Game game) {
		SINewsLogs newsLogs = new SINewsLogs();
		addBulletins(game, newsLogs);
		addFirsts(game, newsLogs);
		addForeignAffairs(game, newsLogs);
		addBattleLogs(game, newsLogs);
		return newsLogs.getDays();

	}

	private void addBulletins(Game game, SINewsLogs newsLogs) {
		Iterable<Mail> bulletins = messageDao.getBulletins(game);
		NewsWorthySplitter<Mail> splitter = new NewsWorthySplitter<Mail>();
		Map<Integer, List<Mail>> map = splitter.split(game, bulletins);
		for (int day : map.keySet()) {
			List<Mail> dayBulletins = map.get(day);
			List<SINewsBulletin> siBulletins = Lists.transform(dayBulletins, new BulletinToSINewsBulletin());
			newsLogs.get(day).addBulletins(siBulletins);
		}
	}

	private void addFirsts(Game game, SINewsLogs newsLogs) {
		List<UnitBuildAudit> firsts = buildFirsts(game);
		NewsWorthySplitter<UnitBuildAudit> splitter = new NewsWorthySplitter<UnitBuildAudit>();
		Map<Integer, List<UnitBuildAudit>> map = splitter.split(game, firsts);

		for (int day : map.keySet()) {
			List<UnitBuildAudit> dayFirsts = map.get(day);
			List<SINewsFirst> sifirsts = new ArrayList<SINewsFirst>();
			for (UnitBuildAudit first : dayFirsts) {
				Nation nation = gameDao.findNation(game, playerDao.find(first.getUsername()));
				SINewsFirst sifirst = new SINewsFirst(first, nation);
				sifirsts.add(sifirst);
			}
			newsLogs.get(day).addFirsts(sifirsts);
		}
	}

	private void addForeignAffairs(Game game, SINewsLogs newsLogs) {
		Iterable<RelationChangeAudit> relationChanges = messageDao.getRelationChanges(game);
		NewsWorthySplitter<RelationChangeAudit> splitter = new NewsWorthySplitter<RelationChangeAudit>();
		Map<Integer, List<RelationChangeAudit>> map = splitter.split(game, relationChanges);
		for (int day : map.keySet()) {
			List<RelationChangeAudit> dayRelations = map.get(day);
			List<SINewsForeignAffairs> siForeignAffairs = new ArrayList<SINewsForeignAffairs>();

			for (RelationChangeAudit relationChangeAudit : dayRelations) {
				Nation from = gameDao.findNation(game, playerDao.find(relationChangeAudit.getFromUsername()));
				Nation to = gameDao.findNation(game, playerDao.find(relationChangeAudit.getToUsername()));
				SINewsForeignAffairs sifor = new SINewsForeignAffairs(relationChangeAudit, from, to);
				siForeignAffairs.add(sifor);
			}
			newsLogs.get(day).addForeignAffairs(siForeignAffairs);
		}
	}

	private List<UnitBuildAudit> buildFirsts(Game game) {
		List<UnitBuildAudit> unitBuildAudits = unitDao.getBuildAudits(game);
		Map<UnitType, UnitBuildAudit> map = new HashMap<UnitType, UnitBuildAudit>();
		for (UnitBuildAudit unitBuildAudit : unitBuildAudits) {
			UnitType key = unitBuildAudit.getType();
			if (map.get(key) != null) {
				continue;
			}
			map.put(key, unitBuildAudit);
		}
		List<UnitBuildAudit> firsts = new ArrayList<UnitBuildAudit>(map.values());
		Collections.sort(firsts, byDate);
		return firsts;
	}

	private void addBattleLogs(Game game, SINewsLogs siNewsLogs) {
		List<BattleLog> battleLogs = logDao.getBattleLogs(game);
		for (BattleLog battleLog : battleLogs) {
			int day = GameScheduleHelper.dateToDay(game, battleLog.getDate());
			SINewsLogsDay siNewsLogsDay = siNewsLogs.get(day);
			if (battleLog instanceof CityCapturedBattleLog) {
				CityCapturedBattleLog cityCapturedBattleLog = (CityCapturedBattleLog) battleLog;
				if (cityCapturedBattleLog.getDefender() == null) {
					siNewsLogsDay.addNeutralConquest(new SINewsNeutralConquest(cityCapturedBattleLog.getAttacker(), 1));
				} else {
					siNewsLogsDay.addOpponentConquest(new SINewsOpponentConquest(cityCapturedBattleLog.getAttacker(), cityCapturedBattleLog.getDefender(), 1));
				}
			} else if (battleLog instanceof CityNukedBattleLog) {
				CityNukedBattleLog cityNukedBattleLog = (CityNukedBattleLog) battleLog;
				siNewsLogsDay.addNuclearDetonations(new SINewsNuclearDetonations(cityNukedBattleLog.getAttackerUnit(), cityNukedBattleLog.getDefender(), 1));
			} else if (battleLog instanceof FlakBattleLog) {
				FlakBattleLog flakBattleLog = (FlakBattleLog) battleLog;
				siNewsLogsDay.addAirDefense(new SINewsAirDefense(flakBattleLog.getAttackerUnit(), flakBattleLog.getDefender(), 1));
			} else if (battleLog instanceof UnitAttackedBattleLog) {
				UnitAttackedBattleLog unitAttackedBattleLog = (UnitAttackedBattleLog) battleLog;
				siNewsLogsDay.addNewsFromTheFront(new SINewsFromTheFront(unitAttackedBattleLog.getAttackerUnit(), unitAttackedBattleLog.getDefenderUnit(), unitAttackedBattleLog.isDefenderDied(), 1));
			} else {
				throw new IllegalStateException("Unknown Battle Log type: " + battleLog.getClass());
			}
		}
	}


}
