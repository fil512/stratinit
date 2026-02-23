CREATE TABLE unitmove
(
  id integer NOT NULL,
  x integer NOT NULL,
  y integer NOT NULL,
  unit_id integer,
  CONSTRAINT unitmove_pkey PRIMARY KEY (id),
  CONSTRAINT FK_UNITMOVE_UNIT FOREIGN KEY (unit_id)
      REFERENCES unit
);
