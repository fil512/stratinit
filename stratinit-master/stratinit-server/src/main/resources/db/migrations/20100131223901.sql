INSERT INTO player VALUES (1, '01-Jan-2010', 'admin@admin.com', true, 'e48d046fa16ca3950a684bc79e30e134962a3f2e', 0, 'admin', 0);
INSERT INTO playerrole VALUES (1, 'ROLE_ADMIN', 1);
ALTER sequence role_id_sequence INCREMENT 1;
ALTER sequence player_id_sequence INCREMENT 1;

create index game_enabled on game(enabled);
create index sector_game_x on sector(game_id, x);
create index sector_game_y on sector(game_id, y);
create index unit_alive on unit(alive);
create index unit_game_coords on unit(nation_game_id, x, y, alive);
create index unit_game_x on unit(nation_game_id, x, alive);
create index unit_game_y on unit(nation_game_id, y, alive);

CREATE VIEW GameBuildAudit AS select (trim(to_char(gameid, '99999')) || '_' || trim(to_char(type, '99999'))) as id, gameid, type, count(id) from unitbuildaudit group by gameid, type order by gameid, type;
