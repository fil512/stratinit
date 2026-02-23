INSERT INTO player VALUES (2, '2010-04-07 22:23:53.433', 'ken.stevens@sympatico.ca', true, '$2a$10$YIF1UCBaeGUsw05r8DkN2eR61w4roW/yW1zLT74KWci5BluioSZi6', 0, 'test1', 0, true);
INSERT INTO playerrole VALUES (2, 'ROLE_USER', 2);
INSERT INTO player VALUES (3, '2010-04-09 16:01:38.994', 'ken.stevens@sympatico.ca', true, '$2a$10$YIF1UCBaeGUsw05r8DkN2eR61w4roW/yW1zLT74KWci5BluioSZi6', 0, 'test2', 0, true);
INSERT INTO playerrole VALUES (3, 'ROLE_USER', 3);
INSERT INTO player VALUES (4, '2010-04-09 16:05:51.565', 'ken.stevens@sympatico.ca', true, '$2a$10$YIF1UCBaeGUsw05r8DkN2eR61w4roW/yW1zLT74KWci5BluioSZi6', 0, 'test3', 0, true);
INSERT INTO playerrole VALUES (4, 'ROLE_USER', 4);
INSERT INTO player VALUES (5, '2010-04-10 00:28:30.56', 'ken.stevens@sympatico.ca', true, '$2a$10$YIF1UCBaeGUsw05r8DkN2eR61w4roW/yW1zLT74KWci5BluioSZi6', 0, 'test4', 0, true);
INSERT INTO playerrole VALUES (5, 'ROLE_USER', 5);

ALTER sequence role_id_sequence RESTART WITH 100;
ALTER sequence player_id_sequence RESTART WITH 100;
ALTER sequence game_id_sequence RESTART WITH 100;
