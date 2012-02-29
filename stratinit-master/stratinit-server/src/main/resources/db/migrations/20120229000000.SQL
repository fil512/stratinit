ALTER TABLE Nation ADD COLUMN noAlliances bool not null default false;
ALTER TABLE Game ADD COLUMN noAlliancesVote int4;
ALTER TABLE Game ADD COLUMN noAlliances bool not null default false;
