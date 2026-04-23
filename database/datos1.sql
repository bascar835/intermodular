--
-- PostgreSQL database dump
--



-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: categorias; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.categorias VALUES (8, 'Aventura', 'Aventura', '/uploads/categorias/317a254e-7c1a-44c7-bb5d-411ab9d4ab9c.png');


--
-- Data for Name: experiencias; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.experiencias VALUES (15, 'sa', 'dsfa', 435.00, 'gfd', 5.00, NULL, '2026-04-20 22:12:22.765079');
INSERT INTO public.experiencias VALUES (16, 'fsad', 'asdf', 24.00, 'asdf', 2.00, NULL, '2026-04-21 23:20:56.836043');
INSERT INTO public.experiencias VALUES (17, 'adsdsa', 'asd', 53.00, 'gsdr', 34.00, 8, '2026-04-21 23:33:22.360723');


--
-- Data for Name: experiencia_imagenes; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.experiencia_imagenes VALUES (2, 15, '/uploads/experiencias/c1597d8b-fe0e-4b2a-8f02-c667e38f59f5.png');
INSERT INTO public.experiencia_imagenes VALUES (3, 16, '/uploads/experiencias/1eada6c6-9e3d-4f63-b813-abf901651ace.png');
INSERT INTO public.experiencia_imagenes VALUES (4, 16, '/uploads/experiencias/16adf204-91f3-4afa-bb88-3bec02cd45bd.png');
INSERT INTO public.experiencia_imagenes VALUES (5, 16, '/uploads/experiencias/1faed7ef-5d98-4fda-ab66-0912fd1b77d9.png');
INSERT INTO public.experiencia_imagenes VALUES (6, 16, '/uploads/experiencias/d3ab560f-a947-4d89-86d7-760427022405.png');
INSERT INTO public.experiencia_imagenes VALUES (9, 17, '/uploads/experiencias/dc751dfc-cd6b-493b-bdb8-bb8ac5addb18.png');


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.usuarios VALUES (9, 'Valido', 'valido@gmail.com', '$2a$10$.PAtikABot7k0WvUH4FOyuXGBqmFjr6OHTiFnfoBuosLdQY/PG39y', 'ROLE_ADMIN', '2026-04-04 15:00:37.161063', false);
INSERT INTO public.usuarios VALUES (10, 'Juan', 'juan@gmail.com', '', 'ROLE_ADMIN', NULL, false);
INSERT INTO public.usuarios VALUES (11, 'Juan2', 'daniel@gmail.com', '', 'ROLE_USER', NULL, false);
INSERT INTO public.usuarios VALUES (13, 'Usuario1', 'juan1@gmail.com', '$2a$10$P1saMHhOvt3r3talEnGRhe.VicYeKNcZfJhQvUG0VFd8rAj889WFi', 'ROLE_USER', '2026-04-21 17:50:35.93156', false);
INSERT INTO public.usuarios VALUES (14, 'jd', 'jd@gmail.com', '$2a$10$NUmrWp0CHYN6PrdE3w/dbeVtmsr81mkEavF.cvvGAghnGpmResC.6', 'ROLE_USER', '2026-04-21 20:41:19.718349', false);
INSERT INTO public.usuarios VALUES (15, 'jd1', 'jd1@gmail.com', '$2a$10$4h0owl2OlEZIE6g8V2GeS.VZPLpBN7WPdGHRtMKnB3EI/UmJwnb0K', 'ROLE_USER', '2026-04-21 20:46:09.245476', false);
INSERT INTO public.usuarios VALUES (16, 'lau', 'lau@gmail.com', '$2a$10$uisFQ9OjVKT736IKZlqZ7ewrgyj1j5/hCIcaqDVi5Lv/R5YBtjLJ2', 'ROLE_USER', '2026-04-21 21:00:28.251291', false);


--
-- Data for Name: reservas; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.reservas VALUES (11, 9, 17, '1111-11-11 11:11:00', '2026-04-23 01:09:46.879552', 1, 11.00, 'pendiente');


--
-- Name: categorias_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.categorias_id_seq', 10, true);


--
-- Name: experiencia_imagenes_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.experiencia_imagenes_id_seq', 9, true);


--
-- Name: experiencias_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.experiencias_id_seq', 17, true);


--
-- Name: reservas_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.reservas_id_seq', 11, true);


--
-- Name: usuarios_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usuarios_id_seq', 16, true);


--
-- PostgreSQL database dump complete
--


