
    create table City (
        lastUpdated timestamp,
        build int4,
        nextBuild int4,
        type int4,
        sector_x int4,
        sector_y int4,
        sector_game_id int4,
        nation_game_id int4,
        nation_player_username varchar(255),
        primary key (sector_x, sector_y, sector_game_id)
    );

    create table CityCapturedBattleLog (
        battleLogId int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attacker_game_id int4,
        attacker_player_username varchar(255),
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_username varchar(255),
        primary key (battleLogId)
    );

    create table CityNukedBattleLog (
        battleLogId int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attacker_game_id int4,
        attacker_player_username varchar(255),
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_username varchar(255),
        primary key (battleLogId)
    );

    create table FlakBattleLog (
        battleLogId int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attacker_game_id int4,
        attacker_player_username varchar(255),
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_username varchar(255),
        primary key (battleLogId)
    );

    create table Game (
        id int4 not null,
        duration int4 not null,
        enabled bool,
        islands int4 not null,
        name varchar(255) not null,
        players int4 not null,
        size int4 not null,
        started timestamp,
        primary key (id)
    );

    create table LaunchedSatellite (
        satelliteId int4 not null,
        x int4 not null,
        y int4 not null,
        nation_game_id int4,
        nation_player_username varchar(255),
        primary key (satelliteId)
    );

    create table Mail (
        messageId int4 not null,
        date timestamp,
        read bool not null,
        subject varchar(255),
        body text,
        from_game_id int4,
        from_player_username varchar(255),
        to_game_id int4,
        to_player_username varchar(255),
        primary key (messageId)
    );

    create table Nation (
        lastAction timestamp,
        nationId int4 not null,
        tech float8 not null,
        game_id int4,
        player_username varchar(255),
        primary key (game_id, player_username)
    );

    create table Player (
        username varchar(255) not null,
        email varchar(255),
        enabled bool not null,
        password varchar(255),
        played int4 not null,
        wins int4 not null,
        primary key (username)
    );

    create table PlayerRole (
        playerRoleId int4 not null,
        roleName varchar(255),
        player_username varchar(255),
        primary key (playerRoleId)
    );

    create table Relation (
        nextType int4,
        switchTime timestamp,
        type int4,
        to_game_id int4 not null,
        to_player_username varchar(255) not null,
        from_game_id int4 not null,
        from_player_username varchar(255) not null,
        primary key (from_game_id, from_player_username, to_game_id, to_player_username)
    );

    create table Sector (
        x int4 not null,
        y int4 not null,
        island int4 not null,
        type int4 not null,
        game_id int4 not null,
        primary key (x, y, game_id)
    );

    create table SectorSeen (
        x int4 not null,
        y int4 not null,
        lastSeen timestamp,
        nation_game_id int4 not null,
        nation_player_username varchar(255) not null,
        primary key (x, y, nation_game_id, nation_player_username)
    );

    create table Unit (
        id int4 not null,
        lastUpdated timestamp,
        alive bool not null,
        ammo int4 not null,
        canSeeSubs bool not null,
        x int4 not null,
        y int4 not null,
        created timestamp,
        fuel int4 not null,
        hp int4 not null,
        mobility int4 not null,
        sight int4 not null,
        type int4,
        nation_game_id int4,
        nation_player_username varchar(255),
        unitBuildAudit_id int4,
        primary key (id)
    );

    create table UnitAttackedBattleLog (
        battleLogId int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attackerCollateralUnitsSunk int4 not null,
        attackerDied bool not null,
        damage int4 not null,
        defenderCannotAttack bool not null,
        defenderCollateralUnitsSunk int4 not null,
        defenderDied bool not null,
        defenderOutOfAmmo bool not null,
        returnDamage int4 not null,
        attacker_game_id int4,
        attacker_player_username varchar(255),
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_username varchar(255),
        defenderUnit_id int4,
        primary key (battleLogId)
    );

    create table UnitBuildAudit (
        id int4 not null,
        date timestamp,
        gameId int4 not null,
        type int4,
        username varchar(255),
        x int4 not null,
        y int4 not null,
        primary key (id)
    );

    create table UnitSeen (
        expiry timestamp,
        unit_id int4,
        nation_game_id int4,
        nation_player_username varchar(255),
        primary key (nation_game_id, nation_player_username, unit_id)
    );

    alter table City 
        add constraint FK200D8B4A521B4F 
        foreign key (nation_game_id, nation_player_username) 
        references Nation;

    alter table City 
        add constraint FK200D8B85DFDCD0 
        foreign key (sector_x, sector_y, sector_game_id) 
        references Sector;

    alter table CityCapturedBattleLog 
        add constraint FK990E50E3CC66C87 
        foreign key (defender_game_id, defender_player_username) 
        references Nation;

    alter table CityCapturedBattleLog 
        add constraint FK990E50E3A9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table CityCapturedBattleLog 
        add constraint FK990E50E34323E4AB 
        foreign key (attacker_game_id, attacker_player_username) 
        references Nation;

    alter table CityNukedBattleLog 
        add constraint FKC6A4D9F4CC66C87 
        foreign key (defender_game_id, defender_player_username) 
        references Nation;

    alter table CityNukedBattleLog 
        add constraint FKC6A4D9F4A9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table CityNukedBattleLog 
        add constraint FKC6A4D9F44323E4AB 
        foreign key (attacker_game_id, attacker_player_username) 
        references Nation;

    alter table FlakBattleLog 
        add constraint FK567C2DBCCC66C87 
        foreign key (defender_game_id, defender_player_username) 
        references Nation;

    alter table FlakBattleLog 
        add constraint FK567C2DBCA9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table FlakBattleLog 
        add constraint FK567C2DBC4323E4AB 
        foreign key (attacker_game_id, attacker_player_username) 
        references Nation;

    alter table LaunchedSatellite 
        add constraint FK2A5B7DA94A521B4F 
        foreign key (nation_game_id, nation_player_username) 
        references Nation;

    alter table Mail 
        add constraint FK2479D7C57D7315 
        foreign key (from_game_id, from_player_username) 
        references Nation;

    alter table Mail 
        add constraint FK2479D7ECA83DB7 
        foreign key (to_game_id, to_player_username) 
        references Nation;

    alter table Nation 
        add constraint FK8AA73B67B3E72851 
        foreign key (player_username) 
        references Player;

    alter table Nation 
        add constraint FK8AA73B67DBFA6476 
        foreign key (game_id) 
        references Game;

    alter table PlayerRole 
        add constraint FKF3BD1317B3E72851 
        foreign key (player_username) 
        references Player;

    alter table Relation 
        add constraint FKE2CE5E1CC57D7315 
        foreign key (from_game_id, from_player_username) 
        references Nation;

    alter table Relation 
        add constraint FKE2CE5E1CECA83DB7 
        foreign key (to_game_id, to_player_username) 
        references Nation;

    alter table Sector 
        add constraint FK93604386DBFA6476 
        foreign key (game_id) 
        references Game;

    alter table SectorSeen 
        add constraint FK9B0E53214A521B4F 
        foreign key (nation_game_id, nation_player_username) 
        references Nation;

    alter table Unit 
        add constraint FK284DA42DEE89 
        foreign key (unitBuildAudit_id) 
        references UnitBuildAudit;

    alter table Unit 
        add constraint FK284DA44A521B4F 
        foreign key (nation_game_id, nation_player_username) 
        references Nation;

    alter table UnitAttackedBattleLog 
        add constraint FK1A329901CC66C87 
        foreign key (defender_game_id, defender_player_username) 
        references Nation;

    alter table UnitAttackedBattleLog 
        add constraint FK1A329901A9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table UnitAttackedBattleLog 
        add constraint FK1A32990146514BF3 
        foreign key (defenderUnit_id) 
        references Unit;

    alter table UnitAttackedBattleLog 
        add constraint FK1A3299014323E4AB 
        foreign key (attacker_game_id, attacker_player_username) 
        references Nation;

    alter table UnitSeen 
        add constraint FKF2681E3FD695A336 
        foreign key (unit_id) 
        references Unit;

    alter table UnitSeen 
        add constraint FKF2681E3F4A521B4F 
        foreign key (nation_game_id, nation_player_username) 
        references Nation;

    create sequence hibernate_sequence;
