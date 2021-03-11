package com.kenstevens.stratinit.client.server.rest;


import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SIUnitListJson;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.BeforeEach;

public abstract class TwoPlayerBase extends BaseStratInitControllerTest {
    protected static final String PLAYER_THEM_NAME = "them";
    protected Player playerThem;
    protected Nation nationThem;
    protected int nationThemId;

    @BeforeEach
    public void joinTwoPlayers() {
        setAuthentication(PlayerHelper.PLAYER_ME);
        joinGamePlayerMe();
        playerThem = createPlayer(PLAYER_THEM_NAME);
        joinGame(playerThem);
        nationThem = gameDao.findNation(testGameId, playerThem);
        nationThemId = nationThem.getNationId();
        setAuthentication(PlayerHelper.PLAYER_ME);
    }

    private void setMyRelation(RelationType relationType) {
        Relation relation = relationDao.findRelation(nationMe, nationThem);
        relation.setNextType(relationType);
        relationDaoService.switchRelation(relation);
    }

    private void setTheirRelation(RelationType relationType) {
        Relation relation = relationDao.findRelation(nationThem, nationMe);
        relation.setType(relationType);
        relationDao.save(relation);
    }

    final protected void declareWar() {
        setMyRelation(RelationType.WAR);
    }

    final protected void declareFriendly() {
        setMyRelation(RelationType.FRIENDLY);
    }

    final protected void declareAlliance() {
        setMyRelation(RelationType.ALLIED);
    }

    final protected void warDeclared() {
        setTheirRelation(RelationType.WAR);
    }

    final protected void friendlyDeclared() {
        setTheirRelation(RelationType.FRIENDLY);
    }

    final protected void allianceDeclared() {
        setTheirRelation(RelationType.ALLIED);
    }

    final protected Result<SIRelation> setRelation(int nationThemId, RelationType relationType) {
        return stratInitController.setRelation(new SetRelationJson(nationThemId, relationType));
    }

    final protected Result<SIUpdate> buildCity(Unit unit) {
        SIUnitListJson request = new SIUnitListJson(new SIUnit(unit));
        return stratInitController.buildCity(request);
    }

    final protected Result<SIUpdate> switchTerrain(Unit unit) {
        SIUnitListJson request = new SIUnitListJson(new SIUnit(unit));
        return stratInitController.switchTerrain(request);
    }
}