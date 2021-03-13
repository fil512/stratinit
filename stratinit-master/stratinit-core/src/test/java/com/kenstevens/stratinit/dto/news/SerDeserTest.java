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

    // TODO sleep do the rest
}
