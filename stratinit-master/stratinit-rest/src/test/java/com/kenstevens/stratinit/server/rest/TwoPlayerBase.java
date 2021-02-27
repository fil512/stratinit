package com.kenstevens.stratinit.server.rest;


import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.BeforeEach;

public abstract class TwoPlayerBase extends BaseStratInitControllerTest {
    protected static final String PLAYER_THEM_NAME = "them";
    protected Player playerThem;
    protected Nation nationThem;
    protected int nationThemId;

    @BeforeEach
    public void joinTwoPlayers() {
        setAuthentication(PLAYER_ME_NAME);
        joinGamePlayerMe();
        playerThem = createPlayer(PLAYER_THEM_NAME);
        joinGame(playerThem);
        nationThem = gameDao.findNation(testGameId, playerThem);
        nationThemId = nationThem.getNationId();
        setAuthentication(PLAYER_ME_NAME);
    }

    private void setMyRelation(RelationType relationType) {
        Relation relation = gameDao.findRelation(nationMe, nationThem);
        relation.setNextType(relationType);
        gameDaoService.switchRelation(relation);
    }

    private void setTheirRelation(RelationType relationType) {
        Relation relation = gameDao.findRelation(nationThem, nationMe);
        relation.setType(relationType);
        gameDao.save(relation);
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
}