package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.helper.NationHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SINewsLogsDayTest {

    @Test
    void addNeutralConquest() {

        SINewsLogsDay siNewsLogsDay = new SINewsLogsDay();

        SINewsNeutralConquest conq1 = new SINewsNeutralConquest(NationHelper.nationMe, 3);
        SINewsNeutralConquest conq2 = new SINewsNeutralConquest(NationHelper.nationMe, 4);
        SINewsNeutralConquest conq3 = new SINewsNeutralConquest(NationHelper.nationThem, 8);
        SINewsNeutralConquest conq4 = new SINewsNeutralConquest(NationHelper.nationMe, -2);
        List<SINewsNeutralConquest> list;

        list = siNewsLogsDay.getNeutralConquests();
        assertThat(list, hasSize(0));

        siNewsLogsDay.addNeutralConquest(conq1);
        list = siNewsLogsDay.getNeutralConquests();
        assertThat(list, hasSize(1));
        assertEquals(3, list.get(0).getCount());

        siNewsLogsDay.addNeutralConquest(conq2);
        list = siNewsLogsDay.getNeutralConquests();
        assertThat(list, hasSize(1));
        assertEquals(7, list.get(0).getCount());

        siNewsLogsDay.addNeutralConquest(conq3);
        list = siNewsLogsDay.getNeutralConquests();
        assertThat(list, hasSize(2));
        assertEquals(7, list.get(0).getCount());
        assertEquals(8, list.get(1).getCount());

        siNewsLogsDay.addNeutralConquest(conq4);
        list = siNewsLogsDay.getNeutralConquests();
        assertThat(list, hasSize(2));
        assertEquals(5, list.get(0).getCount());
        assertEquals(8, list.get(1).getCount());
    }

    @Test
    void addOpponentConquest() {
        SINewsLogsDay siNewsLogsDay = new SINewsLogsDay();

        SINewsOpponentConquest conq1 = new SINewsOpponentConquest(NationHelper.nationMe, NationHelper.nationThem, 3); // 3
        SINewsOpponentConquest conq2 = new SINewsOpponentConquest(NationHelper.nationMe, NationHelper.nationThem, 4); // 7
        SINewsOpponentConquest conq3 = new SINewsOpponentConquest(NationHelper.nationThem, NationHelper.nationMe, 5); // 2
        SINewsOpponentConquest conq4 = new SINewsOpponentConquest(NationHelper.nationThem, NationHelper.nationMe, 12); // 10 reversed

        List<SINewsOpponentConquest> list;

        siNewsLogsDay.addOpponentConquest(conq1);
        list = siNewsLogsDay.getOpponentConquest();
        assertConquestList(list, NationHelper.nationMeName, NationHelper.nationThemName, 3);

        siNewsLogsDay.addOpponentConquest(conq2);
        list = siNewsLogsDay.getOpponentConquest();
        assertConquestList(list, NationHelper.nationMeName, NationHelper.nationThemName, 7);

        siNewsLogsDay.addOpponentConquest(conq3);
        list = siNewsLogsDay.getOpponentConquest();
        assertConquestList(list, NationHelper.nationMeName, NationHelper.nationThemName, 2);

        siNewsLogsDay.addOpponentConquest(conq4);
        list = siNewsLogsDay.getOpponentConquest();
        assertConquestList(list, NationHelper.nationThemName, NationHelper.nationMeName, 10);

    }

    private void assertConquestList(List<SINewsOpponentConquest> list, String nationName, String opponentName, int count) {
        assertThat(list, hasSize(1));
        SINewsOpponentConquest conq = list.get(0);
        assertEquals(nationName, conq.nationName);
        assertEquals(opponentName, conq.opponentName);
        assertEquals(count, conq.getCount());
    }
}