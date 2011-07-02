ALTER TABLE player ADD COLUMN emailGameMail bool default true;
ALTER TABLE game ADD COLUMN mapped timestamp;
ALTER TABLE game ADD COLUMN created timestamp;
