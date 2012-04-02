CREATE TABLE gamehistory
(
  id integer NOT NULL,
  blitz boolean NOT NULL,
  duration integer NOT NULL,
  ends timestamp without time zone,
  gameid integer NOT NULL,
  "name" character varying(255) NOT NULL,
  size integer NOT NULL,
  starttime timestamp without time zone,
  CONSTRAINT gamehistory_pkey PRIMARY KEY (id)
);

CREATE TABLE gamehistoryteam
(
  id integer NOT NULL,
  gamehistory_id integer,
  CONSTRAINT gamehistoryteam_pkey PRIMARY KEY (id),
  CONSTRAINT fk9028b99f719292de FOREIGN KEY (gamehistory_id)
      REFERENCES gamehistory (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE gamehistorynation
(
  id integer NOT NULL,
  cities integer NOT NULL,
  "name" character varying(255),
  power integer NOT NULL,
  gamehistoryteam_id integer,
  CONSTRAINT gamehistorynation_pkey PRIMARY KEY (id),
  CONSTRAINT fk1e73fe09be71dbde FOREIGN KEY (gamehistoryteam_id)
      REFERENCES gamehistoryteam (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

create sequence game_history_id_sequence;
create sequence game_history_team_id_sequence;
create sequence game_history_nation_id_sequence;


