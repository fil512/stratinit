CREATE TABLE unitmove
(
  id integer NOT NULL,
  x integer NOT NULL,
  y integer NOT NULL,
  unit_id integer,
  CONSTRAINT unitmove_pkey PRIMARY KEY (id),
  CONSTRAINT fkf2658b95d695a336 FOREIGN KEY (unit_id)
      REFERENCES unit (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
