package com.kenstevens.stratinit.server.remote.helper;

import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.dao.MessageDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.news.*;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.model.audit.RelationChangeAudit;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.UnitType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewsLogBuilderTest extends TwoPlayerBase {
	private final Mockery context = new Mockery();

	private MessageDao messageDao;
	private UnitDao unitDao;
	private LogDao logDao;

	@Autowired
	private MessageDao origMessageDao;
	@Autowired
	private UnitDao origUnitDao;
	@Autowired
	private LogDao origLogDao;

	@Autowired
	private NewsLogBuilder newsLogBuilder;

	private Unit unit;


	@BeforeEach
	public void setupMocks() {

		messageDao = context.mock(MessageDao.class);
		unitDao = context.mock(UnitDao.class);
		logDao = context.mock(LogDao.class);
		ReflectionTestUtils
				.setField(newsLogBuilder, "messageDao", messageDao);
		ReflectionTestUtils
				.setField(newsLogBuilder, "unitDao", unitDao);
		ReflectionTestUtils
				.setField(newsLogBuilder, "logDao", logDao);

		unit = new Unit(nationMe, UnitType.INFANTRY, TEST_COORDS);
	}

	@AfterEach
	public void undoMocks() {
		ReflectionTestUtils.setField(newsLogBuilder, "messageDao",
				origMessageDao);
		ReflectionTestUtils.setField(newsLogBuilder, "unitDao",
				origUnitDao);
		ReflectionTestUtils.setField(newsLogBuilder, "logDao",
				origLogDao);
	}

	@Test
	public void getNewsLogBulletins() {
		final List<Mail> bulletins = new ArrayList<Mail>();
		String subject = "subject";
		Mail bulletin = new Mail(testGame, null, null, subject, null);
		bulletins.add(bulletin);
		context.checking(new Expectations() {
			{
				oneOf(logDao).getBattleLogs(with(same(testGame)));
				oneOf(messageDao).getRelationChanges(with(same(testGame)));
				oneOf(unitDao).getBuildAudits(with(same(testGame)));
				oneOf(messageDao).getBulletins(with(same(testGame)));
				will(returnValue(bulletins));
			}
		});
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsBulletin> sibulletins = newsLogs.getBulletins();
		assertEquals(1, sibulletins.size());
		SINewsBulletin sibul = sibulletins.get(0);
		assertEquals(NewsCategory.BULLETINS, sibul.category);
		assertEquals(subject, sibul.message);
		context.assertIsSatisfied();
	}


	@Test
	public void getFirstsNews() {
		final List<UnitBuildAudit> unitBuilds = new ArrayList<UnitBuildAudit>();
		UnitBuildAudit unitBuildaudit = new UnitBuildAudit(unit);
		unitBuilds.add(unitBuildaudit);
		context.checking(new Expectations() {
			{
				oneOf(messageDao).getBulletins(with(same(testGame)));
				oneOf(messageDao).getRelationChanges(with(same(testGame)));
				oneOf(logDao).getBattleLogs(with(same(testGame)));
				oneOf(unitDao).getBuildAudits(with(same(testGame)));
				will(returnValue(unitBuilds));
			}
		});
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFirst> firsts = newsLogs.getFirsts();
		assertEquals(1, firsts.size());
		SINewsFirst sifront = firsts.get(0);
		assertEquals(NewsCategory.FIRSTS, sifront.category);
		assertEquals(PLAYER_ME_NAME, sifront.nationName);
		assertEquals(UnitType.INFANTRY, sifront.unitType);
		context.assertIsSatisfied();
	}

	@Test
	public void getNeutralConquests() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, null, TEST_COORDS);
		battleLogs.add(conquestBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNeutralConquest> neutralConquests = newsLogs.getNeutralConquests();
		assertEquals(1, neutralConquests.size());
		SINewsNeutralConquest sicon = neutralConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PLAYER_ME_NAME, sicon.nationName);
		assertEquals(1, sicon.count);
		context.assertIsSatisfied();
	}

	@Test
	public void get2NeutralConquests() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, null, TEST_COORDS);
		battleLogs.add(conquestBattleLog);
		battleLogs.add(conquestBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNeutralConquest> neutralConquests = newsLogs.getNeutralConquests();
		assertEquals(1, neutralConquests.size());
		SINewsNeutralConquest sicon = neutralConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PLAYER_ME_NAME, sicon.nationName);
		assertEquals(2, sicon.count);
		context.assertIsSatisfied();
	}

	@Test
	public void get3NeutralConquests() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, null, TEST_COORDS);
		battleLogs.add(conquestBattleLog);
		battleLogs.add(conquestBattleLog);
		Unit unit2 = new Unit(nationThem, UnitType.INFANTRY, TEST_COORDS);
		CityCapturedBattleLog conquestBattleLog2 = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit2, null, TEST_COORDS);
		battleLogs.add(conquestBattleLog2);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNeutralConquest> neutralConquests = newsLogs.getNeutralConquests();
		assertEquals(2, neutralConquests.size());
		SINewsNeutralConquest sicon1 = neutralConquests.get(0);
		SINewsNeutralConquest sicon2 = neutralConquests.get(1);
		assertTrue(sicon1.nationName != sicon2.nationName);
		context.assertIsSatisfied();
	}

	private void checkingBattleLogs(final List<BattleLog> battleLogs) {
		context.checking(new Expectations() {
			{
				oneOf(messageDao).getBulletins(with(same(testGame)));
				oneOf(messageDao).getRelationChanges(with(same(testGame)));
				oneOf(unitDao).getBuildAudits(with(same(testGame)));
				oneOf(logDao).getBattleLogs(with(same(testGame)));
				will(returnValue(battleLogs));
			}
		});
	}

	@Test
	public void getNuclearDetonations() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit nuke = new Unit(nationMe, UnitType.ICBM_1, TEST_COORDS);
		CityNukedBattleLog nukeBattleLog = new CityNukedBattleLog(nuke, nationThem, TEST_COORDS);
		battleLogs.add(nukeBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsNuclearDetonations> nuclearDetonations = newsLogs.getNuclearDetonations();
		assertEquals(1, nuclearDetonations.size());
		SINewsNuclearDetonations sinuke = nuclearDetonations.get(0);
		assertEquals(NewsCategory.NUCLEAR_DETONATIONS, sinuke.category);
		assertEquals(PLAYER_ME_NAME, sinuke.nationName);
		assertEquals(PLAYER_THEM_NAME, sinuke.opponentName);
		assertEquals(UnitType.ICBM_1, sinuke.launchableUnit);
		assertEquals(1, sinuke.count);
		context.assertIsSatisfied();
	}

	@Test
	public void getAirDefense() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit fighter = new Unit(nationMe, UnitType.FIGHTER, TEST_COORDS);
		FlakBattleLog flakBattleLog = new FlakBattleLog(AttackType.INITIAL_ATTACK, fighter, nationThem, TEST_COORDS, 4);
		battleLogs.add(flakBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsAirDefense> airDefenses = newsLogs.getAirDefense();
		assertEquals(1, airDefenses.size());
		SINewsAirDefense siair = airDefenses.get(0);
		assertEquals(NewsCategory.AIR_DEFENCE, siair.category);
		assertEquals(PLAYER_ME_NAME, siair.nationName);
		assertEquals(PLAYER_THEM_NAME, siair.opponentName);
		assertEquals(UnitType.FIGHTER, siair.nationUnitType);
		assertEquals(1, siair.count);
		context.assertIsSatisfied();
	}

	@Test
	public void getForeignAffairs() {
		final List<RelationChangeAudit> relationChanges = new ArrayList<RelationChangeAudit>();
		Relation relation = new Relation(nationMe, nationThem);
		RelationType oldRelation = RelationType.NEUTRAL;
		relation.setType(oldRelation);
		RelationType newRelation = RelationType.WAR;
		relation.setNextType(newRelation);
		relation.setSwitchTime(null);
		RelationChangeAudit relationChange = new RelationChangeAudit(relation);
		relationChanges.add(relationChange);
		context.checking(new Expectations() {
			{
				oneOf(messageDao).getBulletins(with(same(testGame)));
				oneOf(unitDao).getBuildAudits(with(same(testGame)));
				oneOf(logDao).getBattleLogs(with(same(testGame)));
				oneOf(messageDao).getRelationChanges(with(same(testGame)));
				will(returnValue(relationChanges));
			}
		});
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsForeignAffairs> sirels = newsLogs.getForeignAffairs();
		assertEquals(1, sirels.size());
		SINewsForeignAffairs sifor = sirels.get(0);
		assertEquals(NewsCategory.FOREIGN_AFFAIRS, sifor.category);
		assertEquals(PLAYER_ME_NAME, sifor.nationName);
		assertEquals(newRelation, sifor.newRelation);
		assertEquals(oldRelation, sifor.oldRelation);
		assertEquals(PLAYER_THEM_NAME, sifor.opponentName);
		context.assertIsSatisfied();
	}

	@Test
	public void getNewsFromTheFront() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit def = new Unit(nationThem, UnitType.ZEPPELIN, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, TEST_COORDS);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(1, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PLAYER_ME_NAME, sifront.nationName);
		assertEquals(UnitType.INFANTRY, sifront.nationUnitType);
		assertEquals(PLAYER_THEM_NAME, sifront.opponentName);
		assertEquals(UnitType.ZEPPELIN, sifront.opponentUnitType);
		assertTrue(sifront.killed);
		assertEquals(1, sifront.count);
		context.assertIsSatisfied();
	}

	@Test
	public void getNewsFromTheFront2() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit def = new Unit(nationThem, UnitType.ZEPPELIN, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, TEST_COORDS);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		battleLogs.add(unitAttackedBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(1, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PLAYER_ME_NAME, sifront.nationName);
		assertEquals(UnitType.INFANTRY, sifront.nationUnitType);
		assertEquals(PLAYER_THEM_NAME, sifront.opponentName);
		assertEquals(UnitType.ZEPPELIN, sifront.opponentUnitType);
		assertTrue(sifront.killed);
		assertEquals(2, sifront.count);
		context.assertIsSatisfied();
	}

	@Test
	public void getNewsFromTheFrontDiftAttUnit() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit def = new Unit(nationThem, UnitType.ZEPPELIN, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, TEST_COORDS);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		Unit att2 = new Unit(nationMe, UnitType.TANK, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog2 = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, att2, def, TEST_COORDS);
		unitAttackedBattleLog2.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog2);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(2, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PLAYER_ME_NAME, sifront.nationName);
		assertEquals(PLAYER_THEM_NAME, sifront.opponentName);
		assertTrue(sifront.killed);
		assertEquals(1, sifront.count);
		context.assertIsSatisfied();
	}

	@Test
	public void getNewsFromTheFrontDiftDefUnit() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit def = new Unit(nationThem, UnitType.ZEPPELIN, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, TEST_COORDS);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		Unit def2 = new Unit(nationThem, UnitType.TANK, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog2 = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def2, TEST_COORDS);
		unitAttackedBattleLog2.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog2);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(2, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PLAYER_ME_NAME, sifront.nationName);
		assertEquals(PLAYER_THEM_NAME, sifront.opponentName);
		assertTrue(sifront.killed);
		assertEquals(1, sifront.count);
		context.assertIsSatisfied();
	}

	@Test
	public void getNewsFromTheFrontDiftKillValue() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		Unit def = new Unit(nationThem, UnitType.ZEPPELIN, TEST_COORDS);
		UnitAttackedBattleLog unitAttackedBattleLog = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, TEST_COORDS);
		unitAttackedBattleLog.setDefenderDied(true);
		battleLogs.add(unitAttackedBattleLog);
		UnitAttackedBattleLog unitAttackedBattleLog2 = new UnitAttackedBattleLog(AttackType.INITIAL_ATTACK, unit, def, TEST_COORDS);
		unitAttackedBattleLog2.setDefenderDied(false);
		battleLogs.add(unitAttackedBattleLog2);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsFromTheFront> newsFromTheFront = newsLogs.getNewsFromTheFront();
		assertEquals(2, newsFromTheFront.size());
		SINewsFromTheFront sifront = newsFromTheFront.get(0);
		assertEquals(NewsCategory.NEWS_FROM_THE_FRONT, sifront.category);
		assertEquals(PLAYER_ME_NAME, sifront.nationName);
		assertEquals(PLAYER_THEM_NAME, sifront.opponentName);
		assertEquals(1, sifront.count);
		context.assertIsSatisfied();
	}
	@Test
	public void getOpponentConquest() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, nationThem, TEST_COORDS);
		battleLogs.add(conquestBattleLog);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsOpponentConquest> opponentConquests = newsLogs.getOpponentConquest();
		assertEquals(1, opponentConquests.size());
		SINewsOpponentConquest sicon = opponentConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PLAYER_ME_NAME, sicon.nationName);
		assertEquals(PLAYER_THEM_NAME, sicon.opponentName);
		assertEquals(1, sicon.count);
		context.assertIsSatisfied();
	}
	@Test
	public void getOpponentConquestCancel() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, nationThem, TEST_COORDS);
		battleLogs.add(conquestBattleLog);
		Unit unit2 = new Unit(nationThem, UnitType.INFANTRY, TEST_COORDS);
		CityCapturedBattleLog conquestBattleLog2 = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit2, nationMe, TEST_COORDS);
		battleLogs.add(conquestBattleLog2);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsOpponentConquest> opponentConquests = newsLogs.getOpponentConquest();
		assertEquals(0, opponentConquests.size());
		context.assertIsSatisfied();
	}

	@Test
	public void getOpponentConquest1up2down() {
		List<BattleLog> battleLogs = new ArrayList<BattleLog>();
		CityCapturedBattleLog conquestBattleLog = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit, nationThem, TEST_COORDS);
		battleLogs.add(conquestBattleLog);
		Unit unit2 = new Unit(nationThem, UnitType.INFANTRY, TEST_COORDS);
		CityCapturedBattleLog conquestBattleLog2 = new CityCapturedBattleLog(AttackType.INITIAL_ATTACK, unit2, nationMe, TEST_COORDS);
		battleLogs.add(conquestBattleLog2);
		battleLogs.add(conquestBattleLog2);
		checkingBattleLogs(battleLogs);
		SINewsLogsDay newsLogs = newsLogBuilder.getNews(testGame).get(0);
		List<SINewsOpponentConquest> opponentConquests = newsLogs.getOpponentConquest();
		assertEquals(1, opponentConquests.size());
		SINewsOpponentConquest sicon = opponentConquests.get(0);
		assertEquals(NewsCategory.CONQUEST, sicon.category);
		assertEquals(PLAYER_THEM_NAME, sicon.nationName);
		assertEquals(PLAYER_ME_NAME, sicon.opponentName);
		assertEquals(1, sicon.count);
		context.assertIsSatisfied();
	}

}
