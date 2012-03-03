    create table ErrorLog (
        id int4 not null,
        date timestamp,
        gameid int4,
        username varchar(255),	  
        stacktrace text,
        primary key (id)
    );

create sequence errorlog_id_sequence;
