package com.kenstevens.stratinit.server.rest;


import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.BeforeEach;

public abstract class ThreePlayerBase extends TwoPlayerBase {
    protected static final String PLAYER_THREE_NAME = "three";
    protected Player playerThree;
    protected Nation nationThird;
    protected int nationThreeId;

    @Override
    protected int getNumIslands() {
        return 3;
    }

    @BeforeEach
    public void joinThreePlayers() {
        playerThree = createPlayer(PLAYER_THREE_NAME);
        Result<SINation> retval = joinGame(playerThree);
        nationThird = nationDao.findNation(testGameId, playerThree);
        setAuthentication(PlayerHelper.PLAYER_ME);
    }

    private void setMyRelationToThird(RelationType relationType) {
        Relation relation = relationDao.findRelation(nationMe, nationThird);
        relation.setNextType(relationType);
        relationService.switchRelation(relation);
    }

    private void setThirdRelationToMe(RelationType relationType) {
        Relation relation = relationDao.findRelation(nationThird, nationMe);
        relation.setType(relationType);
        relationDao.save(relation);
    }

    private void setThemRelationToThird(RelationType relationType) {
        Relation relation = relationDao.findRelation(nationThem, nationThird);
        relation.setNextType(relationType);
        relationService.switchRelation(relation);
    }

    private void setThirdRelationToThem(RelationType relationType) {
        Relation relation = relationDao.findRelation(nationThird, nationThem);
        relation.setType(relationType);
        relationDao.save(relation);
    }

    protected void declareWarOnThird() {
        setMyRelationToThird(RelationType.WAR);
    }

    protected void declareFriendlyToThird() {
        setMyRelationToThird(RelationType.FRIENDLY);
    }

    protected void themThirdAlly() {
        setThemRelationToThird(RelationType.ALLIED);
        setThirdRelationToThem(RelationType.ALLIED);
    }

    protected void themThirdWar() {
        setThemRelationToThird(RelationType.WAR);
        setThirdRelationToThem(RelationType.WAR);
    }

    protected void warDeclaredByThird() {
        setThirdRelationToMe(RelationType.WAR);
    }

    protected void friendlyDeclaredByThird() {
        setThirdRelationToMe(RelationType.FRIENDLY);
    }
}