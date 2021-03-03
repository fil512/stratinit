package com.kenstevens.stratinit.config;

import org.apache.commons.lang3.time.DateUtils;

public class ServerConfig {
    private static final int MIN_PLAYERS_TO_SCHEDULE = 4; // hopefully this will increase over time

    private static final long SCHEDULED_TO_MAPPED_MILLIS = 16 * DateUtils.MILLIS_PER_HOUR;
    private static final long MAPPED_TO_STARTED_MILLIS = 8 * DateUtils.MILLIS_PER_HOUR;

    private static final long FLUSH_CACHE_MILLIS = 15 * DateUtils.MILLIS_PER_MINUTE;

    // TEST values

    private static final long SCHEDULED_TO_MAPPED_MILLIS_TEST = 10 * DateUtils.MILLIS_PER_SECOND;
    private static final long MAPPED_TO_STARTED_MILLIS_TEST = 10 * DateUtils.MILLIS_PER_SECOND;
    private static final long FLUSH_CACHE_MILLIS_TEST = 15 * DateUtils.MILLIS_PER_SECOND;

    private final RunModeEnum runModeEnum;

    public ServerConfig(RunModeEnum runModeEnum) {
        // FIXME pull this back with server config
        // FIXME in stratinit-core we added a bunch of long parameters.  Change those back to server config parameters.  Ideally move those out of those classes,
        this.runModeEnum = runModeEnum;
    }

    public long getScheduledToMappedMillis() {
        if (runModeEnum == RunModeEnum.TEST) {
            return SCHEDULED_TO_MAPPED_MILLIS_TEST;
        } else {
            return SCHEDULED_TO_MAPPED_MILLIS;
        }
    }

    public long getMappedToStartedMillis() {
        if (runModeEnum == RunModeEnum.TEST) {
            return MAPPED_TO_STARTED_MILLIS_TEST;
        } else {
            return MAPPED_TO_STARTED_MILLIS;
        }
    }

    public long getScheduledToStartedMillis() {
        if (runModeEnum == RunModeEnum.TEST) {
            return SCHEDULED_TO_MAPPED_MILLIS_TEST
                    + MAPPED_TO_STARTED_MILLIS_TEST;
        } else {
            return SCHEDULED_TO_MAPPED_MILLIS
                    + MAPPED_TO_STARTED_MILLIS;
        }
    }

    public long getFlushCacheMillis() {
        if (runModeEnum == RunModeEnum.TEST) {
            return FLUSH_CACHE_MILLIS_TEST;
        } else {
            return FLUSH_CACHE_MILLIS;
        }
    }

    public int getMinPlayersToSchedule() {
        if (runModeEnum == RunModeEnum.TEST) {
            return 1;
        } else {
            return MIN_PLAYERS_TO_SCHEDULE;
        }
    }
}
