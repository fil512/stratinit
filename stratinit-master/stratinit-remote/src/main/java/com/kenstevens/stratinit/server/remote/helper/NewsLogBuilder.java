package com.kenstevens.stratinit.server.remote.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.news.SINewsAirDefense;
import com.kenstevens.stratinit.dto.news.SINewsBulletin;
import com.kenstevens.stratinit.dto.news.SINewsFirst;
import com.kenstevens.stratinit.dto.news.SINewsForeignAffairs;
import com.kenstevens.stratinit.dto.news.SINewsFromTheFront;
import com.kenstevens.stratinit.dto.news.SINewsLogs;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.dto.news.SINewsNeutralConquest;
import com.kenstevens.stratinit.dto.news.SINewsNuclearDetonations;
import com.kenstevens.stratinit.dto.news.SINewsOpponentConquest;
import com.kenstevens.stratinit.dto.news.translator.BulletinToSINewsBulletin;
import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.CityNukedBattleLog;
import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Mail;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.GameScheduleHelper;

@Service
public class NewsLogBuilder {
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

	private Comparator<NewsWorthy> byDate = new Comparator<NewsWorthy>() {
		public int compare(NewsWorthy e1, NewsWorthy e2) {
			return e1.getDate().compareTo(e2.getDate());
		}
	};

	public List<SINewsLogsDay> getNews(Game game) {
		SINewsLogs newsLogs = new SINewsLogs();
		addBulletins(game, newsLogs);
		addFirsts(game, newsLogs);
		addForeignAffairs(game, newsLogs);
		addBattleLogs(game, newsLogs);
		return newsLogs.getDays();

	}

	private void addBulletins(Game game, SINewsLogs newsLogs) {
		List<Mail> bulletins = messageDao.getBulletins(game);
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
		List<RelationChangeAudit> relationChanges = messageDao.getRelationChanges(game);
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
				CityCapturedBattleLog cityCapturedBattleLog = (CityCapturedBattleLog)battleLog;
				if (cityCapturedBattleLog.getDefender() == null) {
					siNewsLogsDay.addNeutralConquest(new SINewsNeutralConquest(cityCapturedBattleLog.getAttacker(), 1));
				} else {
					siNewsLogsDay.addOpponentConquest(new SINewsOpponentConquest(cityCapturedBattleLog.getAttacker(), cityCapturedBattleLog.getDefender(), 1));
				}
			} else if (battleLog instanceof CityNukedBattleLog) {
				CityNukedBattleLog cityNukedBattleLog = (CityNukedBattleLog)battleLog;
				siNewsLogsDay.addNuclearDetonations(new SINewsNuclearDetonations(cityNukedBattleLog.getAttackerUnit(), cityNukedBattleLog.getDefender(), 1));
			} else if (battleLog instanceof FlakBattleLog) {
				FlakBattleLog flakBattleLog = (FlakBattleLog)battleLog;
				siNewsLogsDay.addAirDefense(new SINewsAirDefense(flakBattleLog.getAttackerUnit(), flakBattleLog.getDefender(), 1));
			} else if (battleLog instanceof UnitAttackedBattleLog) {
				UnitAttackedBattleLog unitAttackedBattleLog = (UnitAttackedBattleLog)battleLog;
				siNewsLogsDay.addNewsFromTheFront(new SINewsFromTheFront(unitAttackedBattleLog.getAttackerUnit(), unitAttackedBattleLog.getDefenderUnit(), unitAttackedBattleLog.isDefenderDied(), 1));
			} else {
				throw new IllegalStateException("Unknown Battle Log type: "+battleLog.getClass());
			}
		}
	}


}
