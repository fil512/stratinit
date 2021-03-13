package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.helper.NationHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SINewsOpponentsTest {
    @Test
    public void testReverse() {
        int count = 2401;
        SINewsOpponentConquest news = new SINewsOpponentConquest(NationHelper.nationMe, NationHelper.nationThem, count);
        SINewsOpponentConquest reverse = news.reverse();
        assertEquals(news.nationName, reverse.opponentName);
        assertEquals(news.opponentName, reverse.nationName);
        assertEquals(news.count, reverse.count);
    }
}
