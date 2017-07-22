CREATE  TABLE users (
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(255) NOT NULL ,
  enabled SMALLINT NOT NULL DEFAULT 1 ,
  PRIMARY KEY (username));
CREATE SEQUENCE user_roles_seq;

CREATE TABLE user_roles (
  user_role_id int NOT NULL DEFAULT NEXTVAL ('user_roles_seq'),
  username varchar(45) NOT NULL,
  role varchar(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  CONSTRAINT uni_username_role UNIQUE (role,username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username));

CREATE TABLE channel (
  channel_id int NOT NULL DEFAULT NEXTVAL ('channel_seq'),
  name varchar(45) NOT NULL,
  PRIMARY KEY (channel_id));

CREATE TABLE user_transcribe_channel (
  user_transcribe_id int NOT NULL DEFAULT NEXTVAL ('user_transcribe_seq'),
  username varchar(45) NOT NULL,
  channel int NOT NULL,
  PRIMARY KEY (user_transcribe_id),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES channel (username));

CREATE INDEX fk_username_idx ON user_roles (username);

INSERT INTO users(username,password,enabled)
VALUES ('transcriber_msdn','$2a$10$/BPbiwTq4ljnD0GPBOW7juUwwl8yQSEBbYwsF4cAW1eoOp8yogILC', 1);
INSERT INTO users(username,password,enabled)
VALUES ('transcriber_traffic','$2a$10$.qXl8BXAb3u9L7TdJDY9u.DEmgLtxmhbzR7NI0J9X.UY3gmUTWxSC', 1);
INSERT INTO users(username,password,enabled)
VALUES ('transcriber_lrpu','$2a$10$TjykuOlzEP8BJT1Ei1yLu.tV0u61cMVjuuRx4QAMVpjt29ZCHcWJK', 1);

INSERT INTO user_roles (username, role)
VALUES ('transcriber_msdn', 'ROLE_TRANSCRIBER');
INSERT INTO user_roles (username, role)
VALUES ('transcriber_traffic', 'ROLE_TRANSCRIBER');
INSERT INTO user_roles (username, role)
VALUES ('transcriber_lrpu', 'ROLE_TRANSCRIBER');