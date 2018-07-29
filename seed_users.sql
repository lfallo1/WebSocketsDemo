create sequence user_roles_seq
;

create sequence channel_seq
;

create sequence user_transcribe_seq
;

create table users
(
  username varchar(45) not null
    constraint users_pkey
    primary key,
  password varchar(255) not null,
  enabled smallint default 1 not null
)
;

create table user_roles
(
  user_role_id integer default nextval('user_roles_seq'::regclass) not null
    constraint user_roles_pkey
    primary key,
  username varchar(45) not null
    constraint fk_username
    references users,
  role varchar(45) not null,
  constraint uni_username_role
  unique (role, username)
)
;

create index fk_username_idx
  on user_roles (username)
;

create table channel
(
  channel_id integer default nextval('channel_seq'::regclass) not null
    constraint channel_pkey
    primary key,
  name varchar(45) not null
)
;

create table transcriber
(
  transcriber_id integer default nextval('user_transcribe_seq'::regclass) not null
    constraint user_transcribe_channel_pkey
    primary key,
  username varchar(45) not null
    constraint fk_username
    references users,
  channel_id integer not null
)
;

INSERT INTO public.users (username, password, enabled) VALUES ('wa3rqd', '$2a$12$48PnOLcqjQxRvAU4P1K7MuS9BBKRtXrm8Nvn/R66YZY8v8bt04fEC', 1);
INSERT INTO public.user_roles (user_role_id, username, role) VALUES (1, 'wa3rqd', 'ROLE_TRANSCRIBER');
INSERT INTO public.channel (channel_id, name) VALUES (1, 'MSN');
INSERT INTO public.transcriber (transcriber_id, username, channel_id) VALUES (1, 'wa3rqd', 1);