
    create table City (
        x int4 not null,
        y int4 not null,
        lastUpdated timestamp,
        build int4,
        nextBuild int4,
        type int4,
        game_id int4 not null,
        nation_game_id int4,
        nation_player_id int4,
        primary key (x, y, game_id)
    );

    create table CityCapturedBattleLog (
        id int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attacker_game_id int4,
        attacker_player_id int4,
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_id int4,
        primary key (id)
    );

    create table CityNukedBattleLog (
        id int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attacker_game_id int4,
        attacker_player_id int4,
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_id int4,
        primary key (id)
    );

    create table FlakBattleLog (
        id int4 not null,
        attackType int4,
        x int4 not null,
        y int4 not null,
        date timestamp,
        flakDamage int4 not null,
        attacker_game_id int4,
        attacker_player_id int4,
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_id int4,
        primary key (id)
    );

    create table Game (
        id int4 not null,
        lastUpdated timestamp,
        blitz bool not null,
        duration int4 not null,
        enabled bool,
        islands int4 not null,
        gamename varchar(255) not null,
        players int4 not null,
        gamesize int4 not null,
        started timestamp,
        primary key (id)
    );

    create table LaunchedSatellite (
        satelliteId int4 not null,
        x int4 not null,
        y int4 not null,
        nation_game_id int4,
        nation_player_id int4,
        primary key (satelliteId)
    );

    create table Mail (
        messageId int4 not null,
        date timestamp,
        read bool not null,
        subject varchar(255),
        body text,
        from_game_id int4,
        from_player_id int4,
        game_id int4,
        to_game_id int4,
        to_player_id int4,
        primary key (messageId)
    );

    create table Nation (
        commandPoints int4 not null,
        dailyTechBleed float8 not null,
        dailyTechGain float8 not null,
        lastAction timestamp,
        nationId int4 not null,
        newBattle bool not null,
        newMail bool not null,
        x int4 not null,
        y int4 not null,
        tech float8 not null,
        game_id int4,
        player_id int4,
        primary key (game_id, player_id)
    );

    create table Player (
        id int4 not null,
        created timestamp,
        email varchar(255),
        enabled bool not null,
        password varchar(255),
        played int4 not null,
        username varchar(255) unique,
        wins int4 not null,
        primary key (id)
    );

    create table PlayerRole (
        playerRoleId int4 not null,
        roleName varchar(255),
        player_id int4,
        primary key (playerRoleId)
    );

    create table Relation (
        nextType int4,
        switchTime timestamp,
        type int4,
        to_game_id int4 not null,
        to_player_id int4 not null,
        from_game_id int4 not null,
        from_player_id int4 not null,
        primary key (from_game_id, from_player_id, to_game_id, to_player_id)
    );

    create table RelationChangeAudit (
        id int4 not null,
        date timestamp,
        effective timestamp,
        fromUsername varchar(255),
        gameId int4 not null,
        nextType int4,
        toUsername varchar(255),
        type int4,
        primary key (id)
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
        nation_player_id int4 not null,
        primary key (x, y, nation_game_id, nation_player_id)
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
        nation_player_id int4,
        primary key (id)
    );

    create table UnitAttackedBattleLog (
        id int4 not null,
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
        attacker_player_id int4,
        attackerUnit_id int4,
        defender_game_id int4,
        defender_player_id int4,
        defenderUnit_id int4,
        primary key (id)
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
        enabled bool not null,
        expiry timestamp,
        unit_id int4,
        nation_game_id int4,
        nation_player_id int4,
        primary key (nation_game_id, nation_player_id, unit_id)
    );

    alter table City 
        add constraint FK_CITY_GAME
        foreign key (game_id) 
        references Game;

    alter table City 
        add constraint FK_CITY_NATION
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table CityCapturedBattleLog 
        add constraint FK_LOG_AUNIT
        foreign key (attackerUnit_id) 
        references Unit;

    alter table CityCapturedBattleLog 
        add constraint FK_LOG_DNATION
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table CityCapturedBattleLog 
        add constraint FK_LOG_ANATION
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table CityNukedBattleLog 
        add constraint FK_NUKE_LOG_AUNIT
        foreign key (attackerUnit_id) 
        references Unit;

    alter table CityNukedBattleLog 
        add constraint FK_NUKE_LOG_DUNIT
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table CityNukedBattleLog 
        add constraint FK_NUKE_LOG_ANATION
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table FlakBattleLog 
        add constraint FK_FLAK_LOG_AUNIT
        foreign key (attackerUnit_id) 
        references Unit;

    alter table FlakBattleLog 
        add constraint FK_FLAK_LOG_DNATION
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table FlakBattleLog 
        add constraint FK_FLAK_LOG_ANATION
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table LaunchedSatellite 
        add constraint FK_SAT_LOG_NAT
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table Mail 
        add constraint FK_MAIL_TO_NAT
        foreign key (to_game_id, to_player_id) 
        references Nation;

    alter table Mail 
        add constraint FK_MAIL_GAME
        foreign key (game_id) 
        references Game;

    alter table Mail 
        add constraint FK_MAIL_FROM_NAT
        foreign key (from_game_id, from_player_id) 
        references Nation;

    alter table Nation 
        add constraint FK8AA73B67DBFA6476 
        foreign key (game_id) 
        references Game;

    alter table Nation 
        add constraint FK_NATION_PLAYER
        foreign key (player_id) 
        references Player;

    alter table PlayerRole 
        add constraint FK_PLAYERROLE_PLAYER
        foreign key (player_id) 
        references Player;

    alter table Relation 
        add constraint FK_RELATION_TO_NATION
        foreign key (to_game_id, to_player_id) 
        references Nation;

    alter table Relation 
        add constraint FK_RELATION_FROM_NATION
        foreign key (from_game_id, from_player_id) 
        references Nation;

    alter table Sector 
        add constraint FK_SECTOR_GAME
        foreign key (game_id) 
        references Game;

    alter table SectorSeen 
        add constraint FK_SECTORSEEN_NATION
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table Unit 
        add constraint FK_UNIT_NATION
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table UnitAttackedBattleLog
        add constraint FK_UNIT_LOG_AUNIT
        foreign key (attackerUnit_id) 
        references Unit;

    alter table UnitAttackedBattleLog 
        add constraint FK_UNIT_LOG_DNATION
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table UnitAttackedBattleLog 
        add constraint FK_UNIT_LOG_DUNIT
        foreign key (defenderUnit_id) 
        references Unit;

    alter table UnitAttackedBattleLog 
        add constraint FK_UNIT_LOG_ANATION
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table UnitSeen 
        add constraint FK_UNITSEEN_NATION
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table UnitSeen 
        add constraint FK_UNITSEEN_UNIT
        foreign key (unit_id) 
        references Unit;

    create sequence battle_id_sequence;

    create sequence game_id_sequence;

    create sequence launched_id_sequence;

    create sequence message_id_sequence;

    create sequence player_id_sequence;

    create sequence relationchange_id_sequence;

    create sequence role_id_sequence;

    create sequence unit_id_sequence;

    create sequence unitbuild_id_sequence;
