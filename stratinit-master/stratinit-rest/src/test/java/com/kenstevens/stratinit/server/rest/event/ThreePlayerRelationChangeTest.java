package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ThreePlayerRelationChangeTest extends ThreeRelationManagerTest {
    @Test
    public void themThirdAlliedIdeclareWarOnThemThirdShouldSwitchToNeutral() {
        declareFriendlyToThird();
        friendlyDeclaredByThird();
        themThirdAlly();
        changedTo(RelationType.WAR);
        assertRelationChanged(nationThird, RelationType.NEUTRAL);
        assertRelationChanged(nationThird, nationMe, RelationType.NEUTRAL);
    }

    @Test
    public void meThirdWarthemThirdAlliedIdeclareWarOnThemThirdShouldSwitchToNeutral() {
        declareWarOnThird();
        warDeclaredByThird();
        themThirdAlly();
        changedTo(RelationType.WAR);

        assertRelationChanged(nationThird, RelationType.WAR);
        assertRelationChanged(nationThird, nationMe, RelationType.WAR);
    }

    @Test
    public void thirdThemWarThirdMeFriendIAllyThemShouldSwitchThirdNeutralToMe() {
        themThirdWar();
        friendlyDeclaredByThird();
        changedTo(RelationType.ALLIED);
        assertRelationChanged(nationThird, nationMe, RelationType.NEUTRAL);
    }

    @Test
    public void noDoubleAlly() {
        declareAlliance();
        allianceDeclared();
        Result<Relation> result = relationService.setRelation(nationMe, nationThird, RelationType.ALLIED, false);
        assertFalseResult(result);
        assertEquals("You already have an ally", result.toString());
    }


    @Test
    public void cannotAllyWithPlayerWhoAlreadyHasAlly_issue72() {
        // nationThem and nationThird are mutual allies
        themThirdAlly();
        // nationMe tries to ally with nationThem, who already has an ally
        Result<Relation> result = relationService.setRelation(nationMe, nationThem, RelationType.ALLIED, false);
        assertFalseResult(result);
        assertEquals(nationThem + " already has an ally.", result.toString());
    }

    @Test
    public void noDoubleUnrequiredAlly() {
        declareAlliance();
        Result<Relation> result = relationService.setRelation(nationMe, nationThird, RelationType.ALLIED, false);
        assertFalseResult(result);
        assertEquals("You already have an ally", result.toString());
    }
}
