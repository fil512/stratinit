
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
        add constraint FK200D8BDBFA6476 
        foreign key (game_id) 
        references Game;

    alter table City 
        add constraint FK200D8B61AB1414 
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table CityCapturedBattleLog 
        add constraint FK990E50E3A9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table CityCapturedBattleLog 
        add constraint FK990E50E356A68A4C 
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table CityCapturedBattleLog 
        add constraint FK990E50E37F5837F0 
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table CityNukedBattleLog 
        add constraint FKC6A4D9F4A9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table CityNukedBattleLog 
        add constraint FKC6A4D9F456A68A4C 
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table CityNukedBattleLog 
        add constraint FKC6A4D9F47F5837F0 
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table FlakBattleLog 
        add constraint FK567C2DBCA9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table FlakBattleLog 
        add constraint FK567C2DBC56A68A4C 
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table FlakBattleLog 
        add constraint FK567C2DBC7F5837F0 
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table LaunchedSatellite 
        add constraint FK2A5B7DA961AB1414 
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table Mail 
        add constraint FK2479D7566FAD7C 
        foreign key (to_game_id, to_player_id) 
        references Nation;

    alter table Mail 
        add constraint FK2479D7DBFA6476 
        foreign key (game_id) 
        references Game;

    alter table Mail 
        add constraint FK2479D7AB8CB21A 
        foreign key (from_game_id, from_player_id) 
        references Nation;

    alter table Nation 
        add constraint FK8AA73B67DBFA6476 
        foreign key (game_id) 
        references Game;

    alter table Nation 
        add constraint FK8AA73B672BA83B16 
        foreign key (player_id) 
        references Player;

    alter table PlayerRole 
        add constraint FKF3BD13172BA83B16 
        foreign key (player_id) 
        references Player;

    alter table Relation 
        add constraint FKE2CE5E1C566FAD7C 
        foreign key (to_game_id, to_player_id) 
        references Nation;

    alter table Relation 
        add constraint FKE2CE5E1CAB8CB21A 
        foreign key (from_game_id, from_player_id) 
        references Nation;

    alter table Sector 
        add constraint FK93604386DBFA6476 
        foreign key (game_id) 
        references Game;

    alter table SectorSeen 
        add constraint FK9B0E532161AB1414 
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table Unit 
        add constraint FK284DA461AB1414 
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table UnitAttackedBattleLog 
        add constraint FK1A329901A9500CA1 
        foreign key (attackerUnit_id) 
        references Unit;

    alter table UnitAttackedBattleLog 
        add constraint FK1A32990156A68A4C 
        foreign key (defender_game_id, defender_player_id) 
        references Nation;

    alter table UnitAttackedBattleLog 
        add constraint FK1A32990146514BF3 
        foreign key (defenderUnit_id) 
        references Unit;

    alter table UnitAttackedBattleLog 
        add constraint FK1A3299017F5837F0 
        foreign key (attacker_game_id, attacker_player_id) 
        references Nation;

    alter table UnitSeen 
        add constraint FKF2681E3F61AB1414 
        foreign key (nation_game_id, nation_player_id) 
        references Nation;

    alter table UnitSeen 
        add constraint FKF2681E3FD695A336 
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
