CREATE TABLE GAMEHISTORY (
	ID INTEGER NOT NULL,
	BLITZ BOOLEAN NOT NULL,
	DURATION INTEGER NOT NULL,
	ENDS TIMESTAMP,
	GAMEID INTEGER NOT NULL,
	GAMENAME VARCHAR(255) NOT NULL,
	GAMESIZE INTEGER NOT NULL,
	STARTTIME TIMESTAMP,
	CONSTRAINT GAMEHISTORY_PKEY PRIMARY KEY (ID)
);

CREATE TABLE gamehistoryteam
(
  id integer NOT NULL,
  gamehistory_id integer,
  CONSTRAINT gamehistoryteam_pkey PRIMARY KEY (id)
  );
  alter table gamehistoryteam
  add constraint FK_GAMEHISTORY_TEAM_GAMEHISTORY
  foreign key (gamehistory_id)
  references gamehistory;

CREATE TABLE gamehistorynation
(
  id integer NOT NULL,
  cities integer NOT NULL,
  GAMENAME character varying(255),
  power integer NOT NULL,
  gamehistoryteam_id integer,
  CONSTRAINT gamehistorynation_pkey PRIMARY KEY (id),
  CONSTRAINT FK_GAMEHISTORYNATION_GAMEHISTORYTEAM FOREIGN KEY (gamehistoryteam_id)
      REFERENCES gamehistoryteam
);

create sequence game_history_id_sequence;
create sequence game_history_team_id_sequence;
create sequence game_history_nation_id_sequence;


