package com.kenstevens.stratinit.rest;

public class SIRestPaths {
    public static final String VERSION = "/version";
    public static final String SERVER_CONFIG = "/serverConfig";
    // FIXME collapse into one /game call with a parameter
    public static final String GAME_JOINED = "/joinedGames";
    public static final String GAME_UNJOINED = "/unjoinedGames";
    public static final String SET_GAME = "/setGame";
    public static final String JOIN_GAME = "/joinGame";
    public static final String SECTOR = "/sector";
    public static final String UNIT_BASE = "/unitbase";
    // FIXME consolidate
    public static final String UNIT = "/unit";
    public static final String UNIT_SEEN = "/unit-seen";
    // FIXME consolidate
    public static final String NATION = "/nation";
    public static final String NATION_ME = "/nation-me";
    // FIXME consolidate
    public static final String CITY = "/city";
    public static final String CITY_SEEN = "/city-seen";
    public static final String BATTLE_LOG = "/battlelog";
    public static final String UPDATE = "/update";
    public static final String RELATION = "/relation";
    // FIXME consolidate with params
    public static final String MESSAGE = "/message";
    public static final String MESSAGE_MAIL = "/message-mail";
    public static final String MESSAGE_SENT = "/message-sent";
    public static final String MESSAGE_ANNOUNCEMENT = "/message-announcement";
    public static final String NEWS_LOG = "/newslog";
    public static final String UNIT_BUILT = "/unitbuilt";
    public static final String TEAM = "/team";
    public static final String UPDATE_CITY = "/update-city";
}
