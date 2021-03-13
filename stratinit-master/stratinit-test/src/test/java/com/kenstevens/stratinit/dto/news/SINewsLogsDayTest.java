package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.helper.NationHelper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SINewsLogsDayTest {

    private final SINewsLogsDay siNewsLogsDay = new SINewsLogsDay();

    @Test
    void addNeutralConquest() {
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
}