--
-- PostgreSQL database dump
--

-- Dumped from database version 13.1 (Debian 13.1-1.pgdg100+1)
-- Dumped by pg_dump version 13.1 (Debian 13.1-1.pgdg100+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: battle_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.battle_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.battle_id_sequence OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: city; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.city (
    x integer NOT NULL,
    y integer NOT NULL,
    lastupdated timestamp without time zone,
    build integer,
    nextbuild integer,
    type integer,
    game_id integer NOT NULL,
    nation_game_id integer,
    nation_player_id integer,
    switchontechchange boolean DEFAULT false
);


ALTER TABLE public.city OWNER TO postgres;

--
-- Name: citycapturedbattlelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.citycapturedbattlelog (
    id integer NOT NULL,
    attacktype integer,
    x integer NOT NULL,
    y integer NOT NULL,
    date timestamp without time zone,
    flakdamage integer NOT NULL,
    attacker_game_id integer,
    attacker_player_id integer,
    attackerunit_id integer,
    defender_game_id integer,
    defender_player_id integer,
    attackerdied boolean DEFAULT true NOT NULL
);


ALTER TABLE public.citycapturedbattlelog OWNER TO postgres;

--
-- Name: citymove; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.citymove (
    id integer NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    city_x integer,
    city_y integer,
    city_game_id integer
);


ALTER TABLE public.citymove OWNER TO postgres;

--
-- Name: citymove_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.citymove_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.citymove_id_sequence OWNER TO postgres;

--
-- Name: citynukedbattlelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.citynukedbattlelog (
    id integer NOT NULL,
    attacktype integer,
    x integer NOT NULL,
    y integer NOT NULL,
    date timestamp without time zone,
    flakdamage integer NOT NULL,
    attacker_game_id integer,
    attacker_player_id integer,
    attackerunit_id integer,
    defender_game_id integer,
    defender_player_id integer
);


ALTER TABLE public.citynukedbattlelog OWNER TO postgres;

--
-- Name: errorlog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.errorlog (
    id integer NOT NULL,
    date timestamp without time zone,
    gameid integer,
    username character varying(255),
    stacktrace text
);


ALTER TABLE public.errorlog OWNER TO postgres;

--
-- Name: errorlog_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.errorlog_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.errorlog_id_sequence OWNER TO postgres;

--
-- Name: flakbattlelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flakbattlelog (
    id integer NOT NULL,
    attacktype integer,
    x integer NOT NULL,
    y integer NOT NULL,
    date timestamp without time zone,
    flakdamage integer NOT NULL,
    attacker_game_id integer,
    attacker_player_id integer,
    attackerunit_id integer,
    defender_game_id integer,
    defender_player_id integer
);


ALTER TABLE public.flakbattlelog OWNER TO postgres;

--
-- Name: game; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.game (
    id integer NOT NULL,
    lastupdated timestamp without time zone,
    blitz boolean NOT NULL,
    duration integer NOT NULL,
    enabled boolean,
    islands integer NOT NULL,
    name character varying(255) NOT NULL,
    players integer NOT NULL,
    size integer NOT NULL,
    starttime timestamp without time zone,
    mapped timestamp without time zone,
    created timestamp without time zone,
    ends timestamp without time zone,
    noalliancesvote integer DEFAULT 0,
    noalliances boolean DEFAULT false NOT NULL
);


ALTER TABLE public.game OWNER TO postgres;

--
-- Name: game_history_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.game_history_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.game_history_id_sequence OWNER TO postgres;

--
-- Name: game_history_nation_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.game_history_nation_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.game_history_nation_id_sequence OWNER TO postgres;

--
-- Name: game_history_team_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.game_history_team_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.game_history_team_id_sequence OWNER TO postgres;

--
-- Name: game_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.game_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.game_id_sequence OWNER TO postgres;

--
-- Name: unitbuildaudit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.unitbuildaudit (
    id integer NOT NULL,
    date timestamp without time zone,
    gameid integer NOT NULL,
    type integer,
    username character varying(255),
    x integer NOT NULL,
    y integer NOT NULL
);


ALTER TABLE public.unitbuildaudit OWNER TO postgres;

--
-- Name: gamebuildaudit; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.gamebuildaudit AS
 SELECT ((btrim(to_char(unitbuildaudit.gameid, '99999'::text)) || '_'::text) || btrim(to_char(unitbuildaudit.type, '99999'::text))) AS id,
    unitbuildaudit.gameid,
    unitbuildaudit.type,
    count(unitbuildaudit.id) AS count
   FROM public.unitbuildaudit
  GROUP BY unitbuildaudit.gameid, unitbuildaudit.type
  ORDER BY unitbuildaudit.gameid, unitbuildaudit.type;


ALTER TABLE public.gamebuildaudit OWNER TO postgres;

--
-- Name: gamehistory; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gamehistory (
    id integer NOT NULL,
    blitz boolean NOT NULL,
    duration integer NOT NULL,
    ends timestamp without time zone,
    gameid integer NOT NULL,
    name character varying(255) NOT NULL,
    size integer NOT NULL,
    starttime timestamp without time zone
);


ALTER TABLE public.gamehistory OWNER TO postgres;

--
-- Name: gamehistorynation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gamehistorynation (
    id integer NOT NULL,
    cities integer NOT NULL,
    name character varying(255),
    power integer NOT NULL,
    gamehistoryteam_id integer
);


ALTER TABLE public.gamehistorynation OWNER TO postgres;

--
-- Name: gamehistoryteam; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gamehistoryteam (
    id integer NOT NULL,
    gamehistory_id integer
);


ALTER TABLE public.gamehistoryteam OWNER TO postgres;

--
-- Name: launched_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.launched_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.launched_id_sequence OWNER TO postgres;

--
-- Name: launchedsatellite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.launchedsatellite (
    satelliteid integer NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    nation_game_id integer,
    nation_player_id integer
);


ALTER TABLE public.launchedsatellite OWNER TO postgres;

--
-- Name: mail; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mail (
    messageid integer NOT NULL,
    date timestamp without time zone,
    read boolean NOT NULL,
    subject character varying(255),
    body text,
    from_game_id integer,
    from_player_id integer,
    game_id integer,
    to_game_id integer,
    to_player_id integer
);


ALTER TABLE public.mail OWNER TO postgres;

--
-- Name: message_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.message_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.message_id_sequence OWNER TO postgres;

--
-- Name: nation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nation (
    commandpoints integer NOT NULL,
    dailytechbleed double precision NOT NULL,
    dailytechgain double precision NOT NULL,
    lastaction timestamp without time zone,
    nationid integer NOT NULL,
    newbattle boolean NOT NULL,
    newmail boolean NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    tech double precision NOT NULL,
    game_id integer NOT NULL,
    player_id integer NOT NULL,
    hourlycpgain integer DEFAULT 0,
    noalliances boolean DEFAULT false NOT NULL
);


ALTER TABLE public.nation OWNER TO postgres;

--
-- Name: player; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.player (
    id integer NOT NULL,
    created timestamp without time zone,
    email character varying(255),
    enabled boolean NOT NULL,
    password character varying(255),
    played integer NOT NULL,
    username character varying(255),
    wins integer NOT NULL,
    emailgamemail boolean DEFAULT true,
    lastlogin timestamp without time zone,
    useragent character varying(255)
);


ALTER TABLE public.player OWNER TO postgres;

--
-- Name: player_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.player_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.player_id_sequence OWNER TO postgres;

--
-- Name: playerrole; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.playerrole (
    playerroleid integer NOT NULL,
    rolename character varying(255),
    player_id integer
);


ALTER TABLE public.playerrole OWNER TO postgres;

--
-- Name: relation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.relation (
    nexttype integer,
    switchtime timestamp without time zone,
    type integer,
    to_game_id integer NOT NULL,
    to_player_id integer NOT NULL,
    from_game_id integer NOT NULL,
    from_player_id integer NOT NULL
);


ALTER TABLE public.relation OWNER TO postgres;

--
-- Name: relationchange_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.relationchange_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.relationchange_id_sequence OWNER TO postgres;

--
-- Name: relationchangeaudit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.relationchangeaudit (
    id integer NOT NULL,
    date timestamp without time zone,
    effective timestamp without time zone,
    fromusername character varying(255),
    gameid integer NOT NULL,
    nexttype integer,
    tousername character varying(255),
    type integer
);


ALTER TABLE public.relationchangeaudit OWNER TO postgres;

--
-- Name: role_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_sequence OWNER TO postgres;

--
-- Name: schema_version; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schema_version (
    version character varying(32) NOT NULL,
    applied_on timestamp without time zone NOT NULL,
    duration integer NOT NULL
);


ALTER TABLE public.schema_version OWNER TO postgres;

--
-- Name: sector; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sector (
    x integer NOT NULL,
    y integer NOT NULL,
    island integer NOT NULL,
    type integer NOT NULL,
    game_id integer NOT NULL
);


ALTER TABLE public.sector OWNER TO postgres;

--
-- Name: sectorseen; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sectorseen (
    x integer NOT NULL,
    y integer NOT NULL,
    lastseen timestamp without time zone,
    nation_game_id integer NOT NULL,
    nation_player_id integer NOT NULL
);


ALTER TABLE public.sectorseen OWNER TO postgres;

--
-- Name: unit; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.unit (
    id integer NOT NULL,
    lastupdated timestamp without time zone,
    alive boolean NOT NULL,
    ammo integer NOT NULL,
    canseesubs boolean NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    created timestamp without time zone,
    fuel integer NOT NULL,
    hp integer NOT NULL,
    mobility integer NOT NULL,
    sight integer NOT NULL,
    type integer,
    nation_game_id integer,
    nation_player_id integer
);


ALTER TABLE public.unit OWNER TO postgres;

--
-- Name: unit_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.unit_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.unit_id_sequence OWNER TO postgres;

--
-- Name: unitattackedbattlelog; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.unitattackedbattlelog (
    id integer NOT NULL,
    attacktype integer,
    x integer NOT NULL,
    y integer NOT NULL,
    date timestamp without time zone,
    flakdamage integer NOT NULL,
    attackercollateralunitssunk integer NOT NULL,
    attackerdied boolean NOT NULL,
    damage integer NOT NULL,
    defendercannotattack boolean NOT NULL,
    defendercollateralunitssunk integer NOT NULL,
    defenderdied boolean NOT NULL,
    defenderoutofammo boolean NOT NULL,
    returndamage integer NOT NULL,
    attacker_game_id integer,
    attacker_player_id integer,
    attackerunit_id integer,
    defender_game_id integer,
    defender_player_id integer,
    defenderunit_id integer
);


ALTER TABLE public.unitattackedbattlelog OWNER TO postgres;

--
-- Name: unitbuild_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.unitbuild_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.unitbuild_id_sequence OWNER TO postgres;

--
-- Name: unitmove; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.unitmove (
    id integer NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    unit_id integer
);


ALTER TABLE public.unitmove OWNER TO postgres;

--
-- Name: unitmove_id_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.unitmove_id_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.unitmove_id_sequence OWNER TO postgres;

--
-- Name: unitseen; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.unitseen (
    enabled boolean NOT NULL,
    expiry timestamp without time zone,
    unit_id integer NOT NULL,
    nation_game_id integer NOT NULL,
    nation_player_id integer NOT NULL
);


ALTER TABLE public.unitseen OWNER TO postgres;

--
-- Name: city city_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (x, y, game_id);


--
-- Name: citycapturedbattlelog citycapturedbattlelog_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citycapturedbattlelog
    ADD CONSTRAINT citycapturedbattlelog_pkey PRIMARY KEY (id);


--
-- Name: citymove citymove_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citymove
    ADD CONSTRAINT citymove_pkey PRIMARY KEY (id);


--
-- Name: citynukedbattlelog citynukedbattlelog_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citynukedbattlelog
    ADD CONSTRAINT citynukedbattlelog_pkey PRIMARY KEY (id);


--
-- Name: errorlog errorlog_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.errorlog
    ADD CONSTRAINT errorlog_pkey PRIMARY KEY (id);


--
-- Name: flakbattlelog flakbattlelog_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flakbattlelog
    ADD CONSTRAINT flakbattlelog_pkey PRIMARY KEY (id);


--
-- Name: game game_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.game
    ADD CONSTRAINT game_pkey PRIMARY KEY (id);


--
-- Name: gamehistory gamehistory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gamehistory
    ADD CONSTRAINT gamehistory_pkey PRIMARY KEY (id);


--
-- Name: gamehistorynation gamehistorynation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gamehistorynation
    ADD CONSTRAINT gamehistorynation_pkey PRIMARY KEY (id);


--
-- Name: gamehistoryteam gamehistoryteam_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gamehistoryteam
    ADD CONSTRAINT gamehistoryteam_pkey PRIMARY KEY (id);


--
-- Name: launchedsatellite launchedsatellite_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.launchedsatellite
    ADD CONSTRAINT launchedsatellite_pkey PRIMARY KEY (satelliteid);


--
-- Name: mail mail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mail
    ADD CONSTRAINT mail_pkey PRIMARY KEY (messageid);


--
-- Name: nation nation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT nation_pkey PRIMARY KEY (game_id, player_id);


--
-- Name: player player_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_pkey PRIMARY KEY (id);


--
-- Name: player player_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_username_key UNIQUE (username);


--
-- Name: playerrole playerrole_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.playerrole
    ADD CONSTRAINT playerrole_pkey PRIMARY KEY (playerroleid);


--
-- Name: relation relation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relation
    ADD CONSTRAINT relation_pkey PRIMARY KEY (from_game_id, from_player_id, to_game_id, to_player_id);


--
-- Name: relationchangeaudit relationchangeaudit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relationchangeaudit
    ADD CONSTRAINT relationchangeaudit_pkey PRIMARY KEY (id);


--
-- Name: schema_version schema_version_version_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schema_version
    ADD CONSTRAINT schema_version_version_key UNIQUE (version);


--
-- Name: sector sector_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT sector_pkey PRIMARY KEY (x, y, game_id);


--
-- Name: sectorseen sectorseen_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sectorseen
    ADD CONSTRAINT sectorseen_pkey PRIMARY KEY (x, y, nation_game_id, nation_player_id);


--
-- Name: unit unit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (id);


--
-- Name: unitattackedbattlelog unitattackedbattlelog_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitattackedbattlelog
    ADD CONSTRAINT unitattackedbattlelog_pkey PRIMARY KEY (id);


--
-- Name: unitbuildaudit unitbuildaudit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitbuildaudit
    ADD CONSTRAINT unitbuildaudit_pkey PRIMARY KEY (id);


--
-- Name: unitmove unitmove_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitmove
    ADD CONSTRAINT unitmove_pkey PRIMARY KEY (id);


--
-- Name: unitseen unitseen_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitseen
    ADD CONSTRAINT unitseen_pkey PRIMARY KEY (nation_game_id, nation_player_id, unit_id);


--
-- Name: game_enabled; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX game_enabled ON public.game USING btree (enabled);


--
-- Name: sector_game_x; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sector_game_x ON public.sector USING btree (game_id, x);


--
-- Name: sector_game_y; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX sector_game_y ON public.sector USING btree (game_id, y);


--
-- Name: unit_alive; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX unit_alive ON public.unit USING btree (alive);


--
-- Name: unit_game_coords; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX unit_game_coords ON public.unit USING btree (nation_game_id, x, y, alive);


--
-- Name: unit_game_x; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX unit_game_x ON public.unit USING btree (nation_game_id, x, alive);


--
-- Name: unit_game_y; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX unit_game_y ON public.unit USING btree (nation_game_id, y, alive);


--
-- Name: citymove citymove_city_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citymove
    ADD CONSTRAINT citymove_city_id FOREIGN KEY (city_x, city_y, city_game_id) REFERENCES public.city(x, y, game_id);


--
-- Name: unitattackedbattlelog fk1a32990146514bf3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitattackedbattlelog
    ADD CONSTRAINT fk1a32990146514bf3 FOREIGN KEY (defenderunit_id) REFERENCES public.unit(id);


--
-- Name: unitattackedbattlelog fk1a32990156a68a4c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitattackedbattlelog
    ADD CONSTRAINT fk1a32990156a68a4c FOREIGN KEY (defender_game_id, defender_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: unitattackedbattlelog fk1a3299017f5837f0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitattackedbattlelog
    ADD CONSTRAINT fk1a3299017f5837f0 FOREIGN KEY (attacker_game_id, attacker_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: unitattackedbattlelog fk1a329901a9500ca1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitattackedbattlelog
    ADD CONSTRAINT fk1a329901a9500ca1 FOREIGN KEY (attackerunit_id) REFERENCES public.unit(id);


--
-- Name: gamehistorynation fk1e73fe09be71dbde; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gamehistorynation
    ADD CONSTRAINT fk1e73fe09be71dbde FOREIGN KEY (gamehistoryteam_id) REFERENCES public.gamehistoryteam(id);


--
-- Name: city fk200d8b61ab1414; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT fk200d8b61ab1414 FOREIGN KEY (nation_game_id, nation_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: city fk200d8bdbfa6476; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT fk200d8bdbfa6476 FOREIGN KEY (game_id) REFERENCES public.game(id);


--
-- Name: mail fk2479d7566fad7c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mail
    ADD CONSTRAINT fk2479d7566fad7c FOREIGN KEY (to_game_id, to_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: mail fk2479d7ab8cb21a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mail
    ADD CONSTRAINT fk2479d7ab8cb21a FOREIGN KEY (from_game_id, from_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: mail fk2479d7dbfa6476; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mail
    ADD CONSTRAINT fk2479d7dbfa6476 FOREIGN KEY (game_id) REFERENCES public.game(id);


--
-- Name: unit fk284da461ab1414; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unit
    ADD CONSTRAINT fk284da461ab1414 FOREIGN KEY (nation_game_id, nation_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: launchedsatellite fk2a5b7da961ab1414; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.launchedsatellite
    ADD CONSTRAINT fk2a5b7da961ab1414 FOREIGN KEY (nation_game_id, nation_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: flakbattlelog fk567c2dbc56a68a4c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flakbattlelog
    ADD CONSTRAINT fk567c2dbc56a68a4c FOREIGN KEY (defender_game_id, defender_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: flakbattlelog fk567c2dbc7f5837f0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flakbattlelog
    ADD CONSTRAINT fk567c2dbc7f5837f0 FOREIGN KEY (attacker_game_id, attacker_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: flakbattlelog fk567c2dbca9500ca1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flakbattlelog
    ADD CONSTRAINT fk567c2dbca9500ca1 FOREIGN KEY (attackerunit_id) REFERENCES public.unit(id);


--
-- Name: nation fk8aa73b672ba83b16; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT fk8aa73b672ba83b16 FOREIGN KEY (player_id) REFERENCES public.player(id);


--
-- Name: nation fk8aa73b67dbfa6476; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT fk8aa73b67dbfa6476 FOREIGN KEY (game_id) REFERENCES public.game(id);


--
-- Name: gamehistoryteam fk9028b99f719292de; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gamehistoryteam
    ADD CONSTRAINT fk9028b99f719292de FOREIGN KEY (gamehistory_id) REFERENCES public.gamehistory(id);


--
-- Name: sector fk93604386dbfa6476; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sector
    ADD CONSTRAINT fk93604386dbfa6476 FOREIGN KEY (game_id) REFERENCES public.game(id);


--
-- Name: citycapturedbattlelog fk990e50e356a68a4c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citycapturedbattlelog
    ADD CONSTRAINT fk990e50e356a68a4c FOREIGN KEY (defender_game_id, defender_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: citycapturedbattlelog fk990e50e37f5837f0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citycapturedbattlelog
    ADD CONSTRAINT fk990e50e37f5837f0 FOREIGN KEY (attacker_game_id, attacker_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: citycapturedbattlelog fk990e50e3a9500ca1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citycapturedbattlelog
    ADD CONSTRAINT fk990e50e3a9500ca1 FOREIGN KEY (attackerunit_id) REFERENCES public.unit(id);


--
-- Name: sectorseen fk9b0e532161ab1414; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sectorseen
    ADD CONSTRAINT fk9b0e532161ab1414 FOREIGN KEY (nation_game_id, nation_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: citynukedbattlelog fkc6a4d9f456a68a4c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citynukedbattlelog
    ADD CONSTRAINT fkc6a4d9f456a68a4c FOREIGN KEY (defender_game_id, defender_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: citynukedbattlelog fkc6a4d9f47f5837f0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citynukedbattlelog
    ADD CONSTRAINT fkc6a4d9f47f5837f0 FOREIGN KEY (attacker_game_id, attacker_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: citynukedbattlelog fkc6a4d9f4a9500ca1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.citynukedbattlelog
    ADD CONSTRAINT fkc6a4d9f4a9500ca1 FOREIGN KEY (attackerunit_id) REFERENCES public.unit(id);


--
-- Name: relation fke2ce5e1c566fad7c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relation
    ADD CONSTRAINT fke2ce5e1c566fad7c FOREIGN KEY (to_game_id, to_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: relation fke2ce5e1cab8cb21a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.relation
    ADD CONSTRAINT fke2ce5e1cab8cb21a FOREIGN KEY (from_game_id, from_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: unitmove fkf2658b95d695a336; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitmove
    ADD CONSTRAINT fkf2658b95d695a336 FOREIGN KEY (unit_id) REFERENCES public.unit(id);


--
-- Name: unitseen fkf2681e3f61ab1414; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitseen
    ADD CONSTRAINT fkf2681e3f61ab1414 FOREIGN KEY (nation_game_id, nation_player_id) REFERENCES public.nation(game_id, player_id);


--
-- Name: unitseen fkf2681e3fd695a336; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.unitseen
    ADD CONSTRAINT fkf2681e3fd695a336 FOREIGN KEY (unit_id) REFERENCES public.unit(id);


--
-- Name: playerrole fkf3bd13172ba83b16; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.playerrole
    ADD CONSTRAINT fkf3bd13172ba83b16 FOREIGN KEY (player_id) REFERENCES public.player(id);


--
-- PostgreSQL database dump complete
--

