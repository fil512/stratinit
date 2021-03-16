package com.kenstevens.stratinit.ui.model;

import com.kenstevens.stratinit.client.model.BattleLogEntry;
import com.kenstevens.stratinit.type.NewsCategory;
import org.eclipse.swt.SWT;

public class SwtBattleLogEntry {
    private final BattleLogEntry entry;
    private int color;
    private String event;

    public SwtBattleLogEntry(BattleLogEntry entry) {
        this.entry = entry;
        setEvent();
        setColor();
    }

    public int getColor() {
        return color;
    }

    public String getEvent() {
        return event;
    }

    private void setColor() {
        if (entry.isAttackerDied()) {
            if (entry.iAmAttacker()) {
                color = SWT.COLOR_RED;
            } else {
                color = SWT.COLOR_GREEN;
            }
        } else if (entry.isDefenderDied()) {
            if (entry.iAmAttacker()) {
                color = SWT.COLOR_GREEN;
            } else {
                color = SWT.COLOR_RED;
            }
        }
    }

    private void setEvent() {
        if (entry.getType() == NewsCategory.CONQUEST) {
            if (entry.iAmAttacker()) {
                color = SWT.COLOR_DARK_GREEN;
                event = "lost";
            } else {
                event = "took";
                color = SWT.COLOR_RED;
            }
        } else if (entry.getType() == NewsCategory.AIR_DEFENCE) {
            if (entry.iAmAttacker()) {
                color = SWT.COLOR_DARK_RED;
                event = "flak";
            } else {
                color = SWT.COLOR_DARK_GREEN;
                event = "flak";
            }
        } else if (entry.getType() == NewsCategory.NEWS_FROM_THE_FRONT) {
            if (entry.isAttackerDied() || entry.isDefenderDied()) {
                event = "killed";
            } else {
                event = "hit";
            }
            if (entry.iAmAttacker()) {
                event = event + " by";
                color = SWT.COLOR_DARK_GREEN;
            } else {
                color = SWT.COLOR_DARK_RED;
            }
        } else {
            color = SWT.COLOR_BLACK;
            event = "unknown";
        }
        if (!entry.iAmAttacker()) {
            event = "* " + event;
        }
    }
}
