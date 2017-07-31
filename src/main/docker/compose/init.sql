--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: channel; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE channel (
    channel_id bigint NOT NULL,
    name character varying(45) NOT NULL
);


ALTER TABLE channel OWNER TO postgres;

--
-- Name: channel_channel_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE channel_channel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE channel_channel_id_seq OWNER TO postgres;

--
-- Name: channel_channel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE channel_channel_id_seq OWNED BY channel.channel_id;


--
-- Name: transcriber; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE transcriber (
    transcriber_id bigint NOT NULL,
    username character varying(45) NOT NULL,
    channel_id integer NOT NULL
);


ALTER TABLE transcriber OWNER TO postgres;

--
-- Name: user_roles_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE user_roles_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_roles_seq OWNER TO postgres;

--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE user_roles (
    user_role_id integer DEFAULT nextval('user_roles_seq'::regclass) NOT NULL,
    username character varying(45) NOT NULL,
    role character varying(45) NOT NULL
);


ALTER TABLE user_roles OWNER TO postgres;

--
-- Name: user_transcribe_channel_user_transcribe_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE user_transcribe_channel_user_transcribe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_transcribe_channel_user_transcribe_id_seq OWNER TO postgres;

--
-- Name: user_transcribe_channel_user_transcribe_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE user_transcribe_channel_user_transcribe_id_seq OWNED BY transcriber.transcriber_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE users (
    username character varying(45) NOT NULL,
    password character varying(255) NOT NULL,
    enabled smallint DEFAULT 1 NOT NULL
);


ALTER TABLE users OWNER TO postgres;

--
-- Name: channel channel_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY channel ALTER COLUMN channel_id SET DEFAULT nextval('channel_channel_id_seq'::regclass);


--
-- Name: transcriber transcriber_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transcriber ALTER COLUMN transcriber_id SET DEFAULT nextval('user_transcribe_channel_user_transcribe_id_seq'::regclass);


--
-- Data for Name: channel; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY channel (channel_id, name) FROM stdin;
1	msdn
2	traffic
3	lrpu
\.


--
-- Name: channel_channel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('channel_channel_id_seq', 3, true);


--
-- Data for Name: transcriber; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY transcriber (transcriber_id, username, channel_id) FROM stdin;
1	transcriber_lrpu	3
2	transcriber_msdn	1
3	transcriber_traffic	2
4	test	1
5	lfallon	2
7	jcoz	2
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY user_roles (user_role_id, username, role) FROM stdin;
1	transcriber_msdn	ROLE_TRANSCRIBER
2	transcriber_traffic	ROLE_TRANSCRIBER
3	transcriber_lrpu	ROLE_TRANSCRIBER
4	test	ROLE_TRANSCRIBER
5	lfallon	ROLE_TRANSCRIBER
6	jcoz	ROLE_TRANSCRIBER
\.


--
-- Name: user_roles_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('user_roles_seq', 6, true);


--
-- Name: user_transcribe_channel_user_transcribe_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('user_transcribe_channel_user_transcribe_id_seq', 7, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY users (username, password, enabled) FROM stdin;
transcriber_msdn	$2a$10$/BPbiwTq4ljnD0GPBOW7juUwwl8yQSEBbYwsF4cAW1eoOp8yogILC	1
transcriber_traffic	$2a$10$.qXl8BXAb3u9L7TdJDY9u.DEmgLtxmhbzR7NI0J9X.UY3gmUTWxSC	1
transcriber_lrpu	$2a$10$TjykuOlzEP8BJT1Ei1yLu.tV0u61cMVjuuRx4QAMVpjt29ZCHcWJK	1
test	$2a$10$JEU3TgaOTx3fW4xGmRzGgeOJ64WW2A1nru.FDg6CL.FZ9V6hFqgdC	1
lfallon	$2a$10$TUF7.dc5yt5x4B8lzvuaDORquT/4HE1h3PMpRZqT7rR3F7yen265O	1
jcoz	$2a$10$I5q9A5XTNe2UbxklspV4eOD2k6E5HyjZbyUG8LvUai4vmFCBy35NC	1
\.


--
-- Name: channel channel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY channel
    ADD CONSTRAINT channel_pkey PRIMARY KEY (channel_id);


--
-- Name: user_roles uni_username_role; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT uni_username_role UNIQUE (role, username);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_role_id);


--
-- Name: transcriber user_transcribe_channel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transcriber
    ADD CONSTRAINT user_transcribe_channel_pkey PRIMARY KEY (transcriber_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);


--
-- Name: fk_username_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX fk_username_idx ON user_roles USING btree (username);


--
-- Name: transcriber fk_user_transcribe_channel_channel; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transcriber
    ADD CONSTRAINT fk_user_transcribe_channel_channel FOREIGN KEY (channel_id) REFERENCES channel(channel_id);


--
-- Name: transcriber fk_user_transcribe_channel_username; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transcriber
    ADD CONSTRAINT fk_user_transcribe_channel_username FOREIGN KEY (username) REFERENCES users(username);


--
-- Name: user_roles fk_username; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_roles
    ADD CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users(username);


--
-- PostgreSQL database dump complete
--

