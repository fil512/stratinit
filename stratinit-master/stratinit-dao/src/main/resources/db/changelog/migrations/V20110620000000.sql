CREATE TABLE citymove
(
  id integer NOT NULL,
  x integer NOT NULL,
  y integer NOT NULL,
  city_x integer,
  city_y integer,
  city_game_id integer,
  CONSTRAINT citymove_pkey PRIMARY KEY (id),
  CONSTRAINT citymove_city_id FOREIGN KEY (city_x, city_y, city_game_id)
      REFERENCES city (x, y, game_id)
);

create sequence citymove_id_sequence;
