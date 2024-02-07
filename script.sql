create table users
(
    id          varchar default nextval('users_id_seq'::regclass) not null,
    username    varchar(255),
    email       varchar(255),
    "firstName" varchar(255),
    "lastName"  varchar(255),
    hash_pwd    varchar,
    roles       varchar
);

alter table users
    owner to postgres;


