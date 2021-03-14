package com.kenstevens.stratinit.dto.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerDeserTest {
    private static final NewsCategory NEWS_CATEGORY = NewsCategory.FIRSTS;
    private static final String NATION_NAME = "children";
    private static final UnitType UNIT_TYPE = UnitType.CARRIER;
    private static final String OPPONENT_NAME = "billy";
    private static final int COUNT = 2401;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void newsAirDefense() throws JsonProcessingException {
        SINewsAirDefense orig = new SINewsAirDefense(NATION_NAME, UNIT_TYPE, OPPONENT_NAME, COUNT);
        String json = mapper.writeValueAsString(orig);
        SINewsAirDefense deser = mapper.readValue(json, SINewsAirDefense.class);
        assertEquals(NewsCategory.AIR_DEFENCE, deser.category);
        assertEquals(NATION_NAME, deser.nationName);
        assertEquals(UNIT_TYPE, deser.nationUnitType);
        assertEquals(OPPONENT_NAME, deser.opponentName);
    }

    @Test
    public void newsBulletin() throws JsonProcessingException {
        SINewsBulletin orig = new SINewsBulletin();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsBulletin.class)));
    }


    @Test
    public void newsFirst() throws JsonProcessingException {
        SINewsFirst orig = new SINewsFirst(NEWS_CATEGORY, NATION_NAME, UNIT_TYPE);
        String json = mapper.writeValueAsString(orig);
        SINewsFirst deser = mapper.readValue(json, SINewsFirst.class);
        assertEquals(NEWS_CATEGORY, deser.category);
        assertEquals(NATION_NAME, deser.nationName);
        assertEquals(UNIT_TYPE, deser.unitType);
    }

    @Test
    public void foreignAffairs() throws JsonProcessingException {
        SINewsForeignAffairs orig = new SINewsForeignAffairs();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsForeignAffairs.class)));
    }

    @Test
    public void newsFromTheFront() throws JsonProcessingException {
        SINewsFromTheFront orig = new SINewsFromTheFront();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsFromTheFront.class)));
    }


    @Test
    public void newsLogs() throws JsonProcessingException {
        SINewsLogs orig = new SINewsLogs();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsLogs.class)));
    }

    @Test
    public void newsLog() throws JsonProcessingException {
        SINewsLog orig = new SINewsLog();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsLog.class)));
    }

    @Test
    public void newsLogsDay() throws JsonProcessingException {
        SINewsLogsDay orig = new SINewsLogsDay();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsLogsDay.class)));
    }

    @Test
    public void newsNation() throws JsonProcessingException {
        SINewsNation orig = new SINewsNation();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsNation.class)));
    }

    @Test
    public void newsNeutralConquest() throws JsonProcessingException {
        SINewsNeutralConquest orig = new SINewsNeutralConquest();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsNeutralConquest.class)));
    }

    @Test
    public void newsNuclear() throws JsonProcessingException {
        SINewsNuclearDetonations orig = new SINewsNuclearDetonations();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsNuclearDetonations.class)));
    }

    @Test
    public void newsOpponentConquest() throws JsonProcessingException {
        SINewsOpponentConquest orig = new SINewsOpponentConquest();
        String json = mapper.writeValueAsString(orig);
        assertEquals(json, mapper.writeValueAsString(mapper.readValue(json, SINewsOpponentConquest.class)));
    }
}
