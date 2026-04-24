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
-- Name: estado_reserva; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.estado_reserva AS ENUM (
    'pendiente',
    'confirmada',
    'cancelada'
);


ALTER TYPE public.estado_reserva OWNER TO postgres;

--
-- Name: rol_usuario; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.rol_usuario AS ENUM (
    'ROLE_ADMIN',
    'ROLE_USER'
);


ALTER TYPE public.rol_usuario OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: categorias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categorias (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion text,
    imagen_url character varying(500)
);


ALTER TABLE public.categorias OWNER TO postgres;

--
-- Name: categorias_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.categorias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categorias_id_seq OWNER TO postgres;

--
-- Name: categorias_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.categorias_id_seq OWNED BY public.categorias.id;


--
-- Name: experiencia_imagenes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experiencia_imagenes (
    id bigint NOT NULL,
    experiencia_id bigint NOT NULL,
    url character varying(500) NOT NULL
);


ALTER TABLE public.experiencia_imagenes OWNER TO postgres;

--
-- Name: experiencia_imagenes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.experiencia_imagenes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.experiencia_imagenes_id_seq OWNER TO postgres;

--
-- Name: experiencia_imagenes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.experiencia_imagenes_id_seq OWNED BY public.experiencia_imagenes.id;


--
-- Name: experiencias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experiencias (
    id bigint NOT NULL,
    titulo character varying(200) NOT NULL,
    descripcion text,
    precio numeric(10,2) NOT NULL,
    ubicacion character varying(200),
    duracion_horas numeric(4,2),
    categoria_id bigint,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_duracion_positiva CHECK (((duracion_horas IS NULL) OR (duracion_horas > (0)::numeric))),
    CONSTRAINT chk_precio_positivo CHECK ((precio > (0)::numeric))
);


ALTER TABLE public.experiencias OWNER TO postgres;

--
-- Name: experiencias_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.experiencias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.experiencias_id_seq OWNER TO postgres;

--
-- Name: experiencias_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.experiencias_id_seq OWNED BY public.experiencias.id;


--
-- Name: reservas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reservas (
    id bigint NOT NULL,
    usuario_id bigint NOT NULL,
    experiencia_id bigint NOT NULL,
    fecha_reserva timestamp without time zone NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    numero_personas integer NOT NULL,
    precio_total numeric(10,2) NOT NULL,
    estado public.estado_reserva DEFAULT 'pendiente'::public.estado_reserva NOT NULL,
    CONSTRAINT chk_personas CHECK ((numero_personas > 0)),
    CONSTRAINT chk_precio_total CHECK ((precio_total >= (0)::numeric))
);


ALTER TABLE public.reservas OWNER TO postgres;

--
-- Name: reservas_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reservas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reservas_id_seq OWNER TO postgres;

--
-- Name: reservas_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reservas_id_seq OWNED BY public.reservas.id;


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    email character varying(150) NOT NULL,
    password character varying(255) NOT NULL,
    rol public.rol_usuario DEFAULT 'ROLE_USER'::public.rol_usuario NOT NULL,
    fecha_registro timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.usuarios OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usuarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.usuarios_id_seq OWNER TO postgres;

--
-- Name: usuarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usuarios_id_seq OWNED BY public.usuarios.id;


--
-- Name: categorias id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias ALTER COLUMN id SET DEFAULT nextval('public.categorias_id_seq'::regclass);


--
-- Name: experiencia_imagenes id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experiencia_imagenes ALTER COLUMN id SET DEFAULT nextval('public.experiencia_imagenes_id_seq'::regclass);


--
-- Name: experiencias id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experiencias ALTER COLUMN id SET DEFAULT nextval('public.experiencias_id_seq'::regclass);


--
-- Name: reservas id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reservas ALTER COLUMN id SET DEFAULT nextval('public.reservas_id_seq'::regclass);


--
-- Name: usuarios id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq'::regclass);


--
-- Name: categorias categorias_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_nombre_key UNIQUE (nombre);


--
-- Name: categorias categorias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categorias
    ADD CONSTRAINT categorias_pkey PRIMARY KEY (id);


--
-- Name: experiencia_imagenes experiencia_imagenes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experiencia_imagenes
    ADD CONSTRAINT experiencia_imagenes_pkey PRIMARY KEY (id);


--
-- Name: experiencias experiencias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experiencias
    ADD CONSTRAINT experiencias_pkey PRIMARY KEY (id);


--
-- Name: reservas reservas_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reservas
    ADD CONSTRAINT reservas_pkey PRIMARY KEY (id);


--
-- Name: usuarios usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (id);


--
-- Name: idx_exp_imagenes_experiencia; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_exp_imagenes_experiencia ON public.experiencia_imagenes USING btree (experiencia_id);


--
-- Name: idx_experiencias_categoria; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_experiencias_categoria ON public.experiencias USING btree (categoria_id);


--
-- Name: idx_reservas_experiencia; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reservas_experiencia ON public.reservas USING btree (experiencia_id);


--
-- Name: idx_reservas_usuario; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reservas_usuario ON public.reservas USING btree (usuario_id);


--
-- Name: ux_usuarios_email_lower; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX ux_usuarios_email_lower ON public.usuarios USING btree (lower((email)::text));


--
-- Name: experiencia_imagenes experiencia_imagenes_experiencia_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experiencia_imagenes
    ADD CONSTRAINT experiencia_imagenes_experiencia_id_fkey FOREIGN KEY (experiencia_id) REFERENCES public.experiencias(id) ON DELETE CASCADE;


--
-- Name: experiencias fk_experiencia_categoria; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experiencias
    ADD CONSTRAINT fk_experiencia_categoria FOREIGN KEY (categoria_id) REFERENCES public.categorias(id) ON DELETE SET NULL;


--
-- Name: reservas fk_reserva_experiencia; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reservas
    ADD CONSTRAINT fk_reserva_experiencia FOREIGN KEY (experiencia_id) REFERENCES public.experiencias(id) ON DELETE CASCADE;


--
-- Name: reservas fk_reserva_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reservas
    ADD CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id) ON DELETE CASCADE;


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: pg_database_owner
--

GRANT USAGE ON SCHEMA public TO postgres;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: postgres
--

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA public GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES TO postgres;


--
-- PostgreSQL database dump complete
--


