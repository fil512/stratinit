package com.kenstevens.stratinit.main;

import com.kenstevens.stratinit.type.Constants;



public final class ClientConstants {

	public static final String CLIENT_NAME = "Strategic Initiative";
	public static final String CLIENT_VERSION = "1.1.47";
	public static final int SCREEN_WIDTH = 40;
	public static final String FAILED_PAGE = "failed_to_parse.html";
	public static final String HELP_URL = "http://www.strategicinitiative.org/";
	public static final String SUPPORT_URL = "http://groups.google.com/group/stratinit";
	public static final String IMAGES_SUBFOLDER_NAME = "images";
	public static final String WAV_SUBFOLDER_NAME = "wav";
	public static final int IMG_PIXELS = 20;
	public static final int IMG_PIXELS_SMALL = 8;

	//	This is just a guess
	public static final int VMARGIN = 64;
	public static final int HMARGIN = 32;
	public static final String DATA_DIR = "stratinit";
	public static final String ACCOUNT_FILENAME = DATA_DIR+"/account.xml";
	public static final String KEY = "modicum of snuff";
	public static final boolean DEBUG = true;
	public static final String NO_CHANGE = "(no change)";
	public static final int ACTION_QUEUE_SIZE = 100;
	public static final String BATTLE_TAB_ITEM_TITLE = "Log";
	public static final String PLAYER_TAB_ITEM_TITLE = "Players";
	public static final String SUPPLY_TAB_ITEM_TITLE = "Supply";
	public static final String SECTOR_TAB_ITEM_TITLE = "Sector";
	public static final String UNIT_TAB_TITLE = "Units";
	public static final String CITY_TITLE = "Cities";
	public static final String HISTORY_TITLE = "Cmds";
	public static final String FUTURE_TITLE = "Plan";
	public static final int MAX_RANGE = 24;
	public static final int DEFAULT_BOARD_SIZE = 100;
	public static final int BOARD_EDGE_SQUARES = 3;
	public static final int REPAIR_CYCLE = 4 * 60 *60;
	public static final int SECONDS_SEEN_1 = REPAIR_CYCLE;
	public static final int SECONDS_SEEN_2 = 2 * REPAIR_CYCLE;
	public static final int SECONDS_SEEN_3 = 3 * REPAIR_CYCLE;
	public static final String MESSAGE_BOARD = Constants.MESSAGE_BOARD;
	public static final String ALLY_PREFIX = "@ ";
	public static final String INBOX = "Inbox";
	public static final String SENT_ITEMS = "Sent Items";
	public static final String DISBAND = "Disband";
	public static final String NEW_CITY = "New City";
	public static final int COMMAND_POINT_WARN = 32;

	private ClientConstants() {}
}
