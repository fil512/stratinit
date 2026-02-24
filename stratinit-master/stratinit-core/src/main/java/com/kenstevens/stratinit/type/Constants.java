package com.kenstevens.stratinit.type;

public final class Constants {
	private Constants() {
	}

	public static final int UNASSIGNED = -1;
	public static final int HOURS_BETWEEN_UNIT_UPDATES = 4;
	public static final int TECH_UPDATE_INTERVAL_SECONDS = 15 * 60;
	public static final double[] TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES = {0,
			0.8, 1.2, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 2.0, 2.1, 2.2, 2.3, 2.4,
            2.5};
    public static final int MAX_RANGE = 24;
    public static final int NO_FUEL_REQUIRED = -1;
    public static final int START_INFANTRY = 4;
    public static final int START_ZEPPELINS = 1;
	public static final int SUPPLY_RADIUS = 6;
	public static final int INTERCEPTION_RADIUS = 6;
	public static final int BATTLE_GROUP_RADIUS = 6;
	public static final int SECONDS_PER_DAY = 24 * 60 * 60;
	public static final int UNIT_VISIBLE_INTERVAL = SECONDS_PER_DAY; // 24 hours
	public static final int SUB_VISIBLE_INTERVAL = 8 * 60 * 60; // 8 hours
	public static final int CITY_VIEW_DISTANCE = 1;
	public static final int ALLIED_FRIENDLY_CANCEL_TIME_HOURS = 24;
	public static final int BASE_FLAK = 1;
	public static final int BASE_CANNONS = 1;
	public static final int FORT_FLAK = 2;
	public static final int FORT_CANNONS = 2;
	public static final int AIRPORT_FLAK = 4;
	public static final int PORT_CANNONS = 4;
	public static final int SATELLITE_SIGHT = 6;
	public static final int AIR_IN_CITY_MULTIPLIER = 2;
	public static final int SHIP_IN_CITY_MULTIPLIER = 3;
	public static final int TECH_NEXT_BUILD = 2;
	public static final int MIN_ATTACK = 2;
	public static final int MAP_EXTRA_SLOTS = 2;

    public static final String DEFAULT_BATTLELOG_FILENAME = "battles.txt";
    public static final int AIRPORT_VIEW_RADIUS = 3;
    public static final double MAX_TECH = 16.0;
    public static final double OTHER_TECH_BLEED = 4.0;
    public static final double ALLY_TECH_BLEED = 2.0;
    public static final int RADAR_BASE = 2;
    public static final int MAX_MOBILITY_MULTIPLIER = 3;
    public static final int MAX_PLAYERS_PER_GAME = 10;
	public static final int MAX_ALLIES = 1; // Maximum number of allies per
											// player
	public static final int RADAR_FACTOR = 3;
	public static final String SERVER_MESSAGE = "HQ";
	public static final int CITY_CAPTURE_DAMAGE = 4;

	public static final int COMMAND_COST = 1;
	public static final int COMMAND_COST_ATTACK = 2;
	public static final int COMMAND_COST_TAKE_CITY = 4;
	public static final int COMMAND_COST_LAUNCH_SATELLITE = 8;
	public static final int COMMAND_COST_SWITCH_TERRAIN = 16;
	public static final int COMMAND_COST_LAUNCH_ICBM = 32;
	public static final int COMMAND_COST_BUILD_CITY = 128;
	public static final int COMMAND_POINT_FACTOR = 4;
	public static final int START_COMMAND_POINTS = 512;
	public static final int MAX_COMMAND_POINTS = 1024;
	// Note a tick is 15 minutes
	public static final int MIN_COMMAND_POINTS_GAINED_PER_TICK = COMMAND_COST;
	public static final int MAX_COMMAND_POINTS_GAINED_PER_TICK = 15;
	public static final String MESSAGE_BOARD = "Message Board";
	public static final int CITY_HEAL_PERCENT = 40;
	public static final int SUPPLY_HEAL_PERCENT = 20;
	public static final int MOB_COST_TO_CREATE_CITY = 9; // == Engineer max
															// mobility
	public static final int MOB_COST_TO_SWITCH_TERRAIN = 6;
	public static final int SHIP_COUNTERFIRE_RANGE = 1;
	public static final int ESCORT_RADIUS = 2;

	public static final int BOT_TURN_INTERVAL_SECONDS = 5 * 60;

}
