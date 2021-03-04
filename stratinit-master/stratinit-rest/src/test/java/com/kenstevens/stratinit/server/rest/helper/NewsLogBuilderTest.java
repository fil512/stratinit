package com.kenstevens.stratinit.server.rest.helper;

import com.kenstevens.stratinit.dao.*;
import com.kenstevens.stratinit.dto.news.*;
import com.kenstevens.stratinit.helper.*;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.server.rest.svc.NewsLogBuilder;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsLogBuilderTest {
	private final Game testGame = GameHelper.game;
	private final Unit unit = UnitHelper.unit;
	@Mock
	private MessageDao messageDao;
	@Mock
	private UnitDao unitDao;
	@Mock
	private GameDao gameDao;
	@Mock
	private PlayerDao playerDao;
	@Mock
	private LogDao logDao;
	@InjectMocks
	private NewsLogBuilder newsLogBuilder;

	@Test
	public void getNewsLogBulletins() {
		final List<Mail> bulletins = new ArrayList<>();
		String subject = "subject";
		Mail bulletin = new Mail(testGame, null, null, subject, null);
		bulletins.add(bulletin);

		when(messageDao.getBulletins(testGame)).thenReturn(bulletins);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsBulletin> sibulletins = newsLogs.getBulletins();
		assertEquals(1, sibulletins.size());
		SINewsBulletin sibul = sibulletins.get(0);
		assertEquals(NewsCategory.BULLETINS, sibul.category);
		assertEquals(subject, sibul.message);

		verifyBattleLogs();
	}

	@Test
	public void getFirstsNews() {
		final List<UnitBuildAudit> unitBuilds = new ArrayList<UnitBuildAudit>();
		UnitBuildAudit unitBuildaudit = new UnitBuildAudit(unit);
		unitBuilds.add(unitBuildaudit);

		when(unitDao.getBuildAudits(testGame)).thenReturn(unitBuilds);
		when(gameDao.findNation(testGame, null)).thenReturn(NationHelper.nationMe);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFirst> firsts = newsLogs.getFirsts();
		assertEquals(1, firsts.size());
		SINewsFirst sifront = firsts.get(0);
		assertEquals(NewsCategory.FIRSTS, sifront.category);
		assertEquals(PlayerHelper.me.getUsername(), sifront.nationName);
		assertEquals(UnitType.INFANTRY, sifront.unitType);

		verifyBattleLogs();
	}

	@Test
	public void getNeutralConquests() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, null, SectorHelper.coords);
		battleLogs.add(conquestBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNeutralConquest> neutralConquests = newsLogs.getNeutralConquests();
		assertEquals(1, neutralConquests.size());
		SINewsNeutralConquest sicon = neutralConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PlayerHelper.me.getUsername(), sicon.nationName);
		assertEquals(1, sicon.count);

		verifyBattleLogs();
	}

	@Test
	public void get2NeutralConquests() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, null, SectorHelper.coords);
		battleLogs.add(conquestBattleLog);
		battleLogs.add(conquestBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNeutralConquest> neutralConquests = newsLogs.getNeutralConquests();
		assertEquals(1, neutralConquests.size());
		SINewsNeutralConquest sicon = neutralConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PlayerHelper.me.getUsername(), sicon.nationName);
		assertEquals(2, sicon.count);

		verifyBattleLogs();
	}


	@Test
	public void get3NeutralConquests() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, null, SectorHelper.coords);
		battleLogs.add(conquestBattleLog);
		battleLogs.add(conquestBattleLog);

		Unit unit2 = new Unit(NationHelper.nationThem, UnitType.INFANTRY, SectorHelper.coords);
		CityCapturedBattleLog conquestBattleLog2 = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit2, null, SectorHelper.coords);
		battleLogs.add(conquestBattleLog2);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNeutralConquest> neutralConquests = newsLogs.getNeutralConquests();
		assertEquals(2, neutralConquests.size());
		SINewsNeutralConquest sicon1 = neutralConquests.get(0);
		SINewsNeutralConquest sicon2 = neutralConquests.get(1);
		assertTrue(sicon1.nationName != sicon2.nationName);

		verifyBattleLogs();
	}

	private void verifyBattleLogs() {
		verify(messageDao).getBulletins(testGame);
		verify(messageDao).getRelationChanges(testGame);
		verify(logDao).getBattleLogs(testGame);
		verify(unitDao).getBuildAudits(testGame);
	}

	@Test
	public void getNuclearDetonations() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit nuke = new Unit(NationHelper.nationMe, UnitType.ICBM_1, SectorHelper.coords);
		CityNukedBattleLog nukeBattleLog = new CityNukedBattleLog(nuke, NationHelper.nationThem, SectorHelper.coords);
		battleLogs.add(nukeBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNuclearDetonations> nuclearDetonations = newsLogs.getNuclearDetonations();
		assertEquals(1, nuclearDetonations.size());
		SINewsNuclearDetonations sinuke = nuclearDetonations.get(0);
		assertEquals(NewsCategory.NUCLEAR_DETONATIONS, sinuke.category);
		assertEquals(PlayerHelper.me.getUsername(), sinuke.nationName);
		assertEquals(PlayerHelper.them.getUsername(), sinuke.opponentName);
		assertEquals(UnitType.ICBM_1, sinuke.launchableUnit);
		assertEquals(1, sinuke.count);

		verifyBattleLogs();
	}

	@Test
	public void getAirDefense() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit fighter = new Unit(NationHelper.nationMe, UnitType.FIGHTER, SectorHelper.coords);
		FlakBattleLog flakBattleLog = new FlakBattleLog(AttackType.INITIAL_ATTACK, fighter, NationHelper.nationThem, SectorHelper.coords, 4);
		battleLogs.add(flakBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsAirDefense> airDefenses = newsLogs.getAirDefense();
		assertEquals(1, airDefenses.size());
		SINewsAirDefense siair = airDefenses.get(0);
		assertEquals(NewsCategory.AIR_DEFENCE, siair.category);
		assertEquals(PlayerHelper.me.getUsername(), siair.nationName);
		assertEquals(PlayerHelper.them.getUsername(), siair.opponentName);
		assertEquals(UnitType.FIGHTER, siair.nationUnitType);
		assertEquals(1, siair.count);

		verifyBattleLogs();
	}

	@Test
	public void getForeignAffairs() {
		final List<RelationChangeAudit> relationChanges = new ArrayList<>();
		Relation relation = new Relation(NationHelper.nationMe, NationHelper.nationThem);
		RelationType oldRelation = RelationType.NEUTRAL;
		relation.setType(oldRelation);
		RelationType newRelation = RelationType.WAR;
		relation.setNextType(newRelation);
		relation.setSwitchTime(null);
		RelationChangeAudit relationChange = new RelationChangeAudit(relation);
		relationChanges.add(relationChange);

		when(messageDao.getRelationChanges(testGame)).thenReturn(relationChanges);
		when(gameDao.findNation(any(), any())).thenReturn(NationHelper.nationMe, NationHelper.nationThem);

		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);

		List<SINewsForeignAffairs> sirels = newsLogs.getForeignAffairs();
		assertEquals(1, sirels.size());
		SINewsForeignAffairs sifor = sirels.get(0);
		assertEquals(NewsCategory.FOREIGN_AFFAIRS, sifor.category);
		assertEquals(PlayerHelper.me.getUsername(), sifor.nationName);
		assertEquals(newRelation, sifor.newRelation);
		assertEquals(oldRelation, sifor.oldRelation);
		assertEquals(PlayerHelper.them.getUsername(), sifor.opponentName);

		verifyBattleLogs();
	}

	@Test
	public void getNewsFromTheFront() {
		List<BattleLog> battleLogs = new ArrayList<>();
		Unit def = new Unit(NationHelper.nationThem, UnitType.ZEPPELIN, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, SectorHelper.coords);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(1, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PlayerHelper.me.getUsername(), sifront.nationName);
		assertEquals(UnitType.INFANTRY, sifront.nationUnitType);
		assertEquals(PlayerHelper.them.getUsername(), sifront.opponentName);
		assertEquals(UnitType.ZEPPELIN, sifront.opponentUnitType);
		assertTrue(sifront.killed);
		assertEquals(1, sifront.count);

		verifyBattleLogs();
	}

	@Test
	public void getNewsFromTheFront2() {
		List<BattleLog> battleLogs = new ArrayList<>();
		Unit def = new Unit(NationHelper.nationThem, UnitType.ZEPPELIN, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, SectorHelper.coords);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		battleLogs.add(unitAttackedBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(1, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PlayerHelper.me.getUsername(), sifront.nationName);
		assertEquals(UnitType.INFANTRY, sifront.nationUnitType);
		assertEquals(PlayerHelper.them.getUsername(), sifront.opponentName);
		assertEquals(UnitType.ZEPPELIN, sifront.opponentUnitType);
		assertTrue(sifront.killed);
		assertEquals(2, sifront.count);

		verifyBattleLogs();
	}

	@Test
	public void getNewsFromTheFrontDiftAttUnit() {
		List<BattleLog> battleLogs = new ArrayList<>();
		Unit def = new Unit(NationHelper.nationThem, UnitType.ZEPPELIN, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, SectorHelper.coords);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		Unit att2 = new Unit(NationHelper.nationMe, UnitType.TANK, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog2 = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, att2, def, SectorHelper.coords);
		unitAttackedBattleLog2.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog2);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(2, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PlayerHelper.me.getUsername(), sifront.nationName);
		assertEquals(PlayerHelper.them.getUsername(), sifront.opponentName);
		assertTrue(sifront.killed);
		assertEquals(1, sifront.count);

		verifyBattleLogs();
	}

	@Test
	public void getNewsFromTheFrontDiftDefUnit() {
		List<BattleLog> battleLogs = new ArrayList<>();
		Unit def = new Unit(NationHelper.nationThem, UnitType.ZEPPELIN, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, SectorHelper.coords);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		Unit def2 = new Unit(NationHelper.nationThem, UnitType.TANK, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog2 = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def2, SectorHelper.coords);
		unitAttackedBattleLog2.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog2);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(2, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PlayerHelper.me.getUsername(), sifront.nationName);
		assertEquals(PlayerHelper.them.getUsername(), sifront.opponentName);
		assertTrue(sifront.killed);
		assertEquals(1, sifront.count);

		verifyBattleLogs();
	}

	@Test
	public void getNewsFromTheFrontDiftKillValue() {
		List<BattleLog> battleLogs = new ArrayList<>();
		Unit def = new Unit(NationHelper.nationThem, UnitType.ZEPPELIN, SectorHelper.coords);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, SectorHelper.coords);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		UnitAttackedBattleLog unitAttackedBattleLog2 = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, SectorHelper.coords);
		unitAttackedBattleLog2.setDefenderDied(false);
		battleLogs.add(unitAttackedBattleLog2);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(2, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PlayerHelper.me.getUsername(), sifront.nationName);
		assertEquals(PlayerHelper.them.getUsername(), sifront.opponentName);
		assertEquals(1, sifront.count);

		verifyBattleLogs();
	}

	@Test
	public void getOpponentConquest() {
		List<BattleLog> battleLogs = new ArrayList<>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, NationHelper.nationThem, SectorHelper.coords);
		battleLogs.add(conquestBattleLog);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsOpponentConquest> opponentConquests = newsLogs.getOpponentConquest();
		assertEquals(1, opponentConquests.size());
		SINewsOpponentConquest sicon = opponentConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PlayerHelper.me.getUsername(), sicon.nationName);
		assertEquals(PlayerHelper.them.getUsername(), sicon.opponentName);
		assertEquals(1, sicon.count);

		verifyBattleLogs();
	}

	@Disabled
	// Simplified this when switching from spring-remote to REST
	@Test
	public void getOpponentConquestCancel() {
		List<BattleLog> battleLogs = new ArrayList<>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, NationHelper.nationThem, SectorHelper.coords);
		battleLogs.add(conquestBattleLog);
		Unit unit2 = new Unit(NationHelper.nationThem, UnitType.INFANTRY, SectorHelper.coords);
		CityCapturedBattleLog conquestBattleLog2 = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit2, NationHelper.nationMe, SectorHelper.coords);
		battleLogs.add(conquestBattleLog2);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsOpponentConquest> opponentConquests = newsLogs.getOpponentConquest();
		assertEquals(0, opponentConquests.size());

		verifyBattleLogs();
	}

	@Disabled
	// Simplified this when switching from spring-remote to REST
	@Test
	public void getOpponentConquest1up2down() {
		List<BattleLog> battleLogs = new ArrayList<>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, NationHelper.nationThem, SectorHelper.coords);
		battleLogs.add(conquestBattleLog);
		Unit unit2 = new Unit(NationHelper.nationThem, UnitType.INFANTRY, SectorHelper.coords);
		CityCapturedBattleLog conquestBattleLog2 = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit2, NationHelper.nationMe, SectorHelper.coords);
		battleLogs.add(conquestBattleLog2);
		battleLogs.add(conquestBattleLog2);

		when(logDao.getBattleLogs(testGame)).thenReturn(battleLogs);


		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsOpponentConquest> opponentConquests = newsLogs.getOpponentConquest();
		assertEquals(1, opponentConquests.size());
		SINewsOpponentConquest sicon = opponentConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PlayerHelper.them.getUsername(), sicon.nationName);
		assertEquals(PlayerHelper.me.getUsername(), sicon.opponentName);
		assertEquals(1, sicon.count);

		verifyBattleLogs();
	}

}
