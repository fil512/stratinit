-- Periodic nation state snapshots (captured each tech tick)
CREATE TABLE game_nation_snapshot (
    id int4 NOT NULL,
    gameId int4 NOT NULL,
    nationName varchar(255) NOT NULL,
    tickTime timestamp NOT NULL,
    tickNumber int4 NOT NULL,
    cities int4 NOT NULL,
    power int4 NOT NULL,
    tech float8 NOT NULL,
    commandPoints int4 NOT NULL,
    capitalPoints int4 NOT NULL,
    PRIMARY KEY (id)
);
CREATE SEQUENCE game_nation_snapshot_id_sequence;
CREATE INDEX idx_gns_game ON game_nation_snapshot(gameId);

-- Discrete event log for troubleshooting
CREATE TABLE game_event_log (
    id int4 NOT NULL,
    gameId int4 NOT NULL,
    nationName varchar(255),
    eventTime timestamp NOT NULL,
    source varchar(10) NOT NULL,
    eventType varchar(50) NOT NULL,
    description varchar(1000) NOT NULL,
    x int4,
    y int4,
    detail varchar(2000),
    PRIMARY KEY (id)
);
CREATE SEQUENCE game_event_log_id_sequence;
CREATE INDEX idx_gel_game ON game_event_log(gameId);
CREATE INDEX idx_gel_game_time ON game_event_log(gameId, eventTime);
