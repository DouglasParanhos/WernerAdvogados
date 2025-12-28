--
-- PostgreSQL database dump
--

-- Dumped from database version 16.1
-- Dumped by pg_dump version 16.1

-- Started on 2025-05-11 12:51:31

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 219 (class 1259 OID 60235)
-- Name: address; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.address (
    address_id bigint NOT NULL,
    logradouro character varying(255) NOT NULL,
    cidade character varying(255) NOT NULL,
    estado character varying(255) NOT NULL,
    cep character varying(255) NOT NULL,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.address OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 60234)
-- Name: address_address_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.address_address_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.address_address_id_seq OWNER TO postgres;

--
-- TOC entry 4927 (class 0 OID 0)
-- Dependencies: 218
-- Name: address_address_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.address_address_id_seq OWNED BY public.address.address_id;


--
-- TOC entry 215 (class 1259 OID 60212)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 60275)
-- Name: matriculation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.matriculation (
    matriculation_id bigint NOT NULL,
    numero character varying(32) NOT NULL,
    inicio_erj timestamp(6) without time zone,
    cargo character varying(255) NOT NULL,
    carga_horaria character varying(5),
    data_aposentadoria timestamp(6) without time zone,
    nivel_atual character varying(5) NOT NULL,
    trienio_atual character varying(5),
    referencia character varying(1) NOT NULL,
    observacoes character varying(4000),
    person_id bigint,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.matriculation OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 60274)
-- Name: matriculation_matriculation_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.matriculation_matriculation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.matriculation_matriculation_id_seq OWNER TO postgres;

--
-- TOC entry 4928 (class 0 OID 0)
-- Dependencies: 222
-- Name: matriculation_matriculation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.matriculation_matriculation_id_seq OWNED BY public.matriculation.matriculation_id;


--
-- TOC entry 227 (class 1259 OID 60307)
-- Name: moviment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.moviment (
    moviment_id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    date timestamp(6) without time zone NOT NULL,
    process_id bigint,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.moviment OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 60306)
-- Name: moviment_moviment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.moviment_moviment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.moviment_moviment_id_seq OWNER TO postgres;

--
-- TOC entry 4929 (class 0 OID 0)
-- Dependencies: 226
-- Name: moviment_moviment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.moviment_moviment_id_seq OWNED BY public.moviment.moviment_id;


--
-- TOC entry 221 (class 1259 OID 60246)
-- Name: person; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.person (
    person_id bigint NOT NULL,
    nome_completo character varying(50) NOT NULL,
    email character varying(255),
    cpf character varying(16) NOT NULL,
    rg character varying(16) NOT NULL,
    estado_civil character varying(16) NOT NULL,
    data_nascimento timestamp(6) without time zone NOT NULL,
    profissao character varying(255) NOT NULL,
    telefone character varying(16),
    vivo boolean NOT NULL,
    representante character varying(255),
    id_funcional character varying(16),
    nacionalidade character varying(32),
    observacoes character varying(4000),
    user_id bigint,
    address_id bigint,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.person OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 60245)
-- Name: person_person_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.person_person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.person_person_id_seq OWNER TO postgres;

--
-- TOC entry 4930 (class 0 OID 0)
-- Dependencies: 220
-- Name: person_person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.person_person_id_seq OWNED BY public.person.person_id;


--
-- TOC entry 225 (class 1259 OID 60291)
-- Name: process; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.process (
    process_id bigint NOT NULL,
    numero character varying(255) NOT NULL,
    comarca character varying(255) NOT NULL,
    vara character varying(255) NOT NULL,
    sistema character varying(255) NOT NULL,
    valor double precision,
    previsao_honorarios_contratuais double precision,
    previsao_honorarios_sucumbenciais double precision,
    distribuido_em timestamp(6) without time zone,
    tipo_processo character varying(50) NOT NULL,
    status character varying(50),
    matriculation_id bigint,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.process OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 60290)
-- Name: process_process_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.process_process_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.process_process_id_seq OWNER TO postgres;

--
-- TOC entry 4931 (class 0 OID 0)
-- Dependencies: 224
-- Name: process_process_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.process_process_id_seq OWNED BY public.process.process_id;


--
-- TOC entry 217 (class 1259 OID 60222)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(512) NOT NULL,
    user_role character varying(16) NOT NULL,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_login timestamp(6) without time zone
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 60221)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 4932 (class 0 OID 0)
-- Dependencies: 216
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4721 (class 2604 OID 60320)
-- Name: address address_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address ALTER COLUMN address_id SET DEFAULT nextval('public.address_address_id_seq'::regclass);


--
-- TOC entry 4727 (class 2604 OID 60334)
-- Name: matriculation matriculation_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matriculation ALTER COLUMN matriculation_id SET DEFAULT nextval('public.matriculation_matriculation_id_seq'::regclass);


--
-- TOC entry 4733 (class 2604 OID 60371)
-- Name: moviment moviment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.moviment ALTER COLUMN moviment_id SET DEFAULT nextval('public.moviment_moviment_id_seq'::regclass);


--
-- TOC entry 4724 (class 2604 OID 60391)
-- Name: person person_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person ALTER COLUMN person_id SET DEFAULT nextval('public.person_person_id_seq'::regclass);


--
-- TOC entry 4730 (class 2604 OID 60449)
-- Name: process process_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.process ALTER COLUMN process_id SET DEFAULT nextval('public.process_process_id_seq'::regclass);


--
-- TOC entry 4718 (class 2604 OID 60498)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 4913 (class 0 OID 60235)
-- Dependencies: 219
-- Data for Name: address; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.address (address_id, logradouro, cidade, estado, cep, created_on, modified_on) FROM stdin;
1	Rua Di Cavalcanti 155 casa 19 Sape	Niterói	RJ	24000-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
2	Rua Franco Montoro nº31 Ap 301-31 Jardim Atlântico	Maricá	RJ	24935-595	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
3	Rua Prefeito Joao Augusto De Andrade Lote 120 Bloco 01 Apt 301 São Joaquim	Itaboraí	RJ	248000-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
4	Alameda São Boaventura nº 419 apto 204 Fonseca	Niterói	RJ	24130-005	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
5	Rua Augusto Calheiros nº 157 Raul Veiga	São Gonçalo	RJ	24730-030	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
6	Estrada Caetano Monteiro nº 391 apto 306 bloco 08 Badu	Niterói	RJ	24320-570	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
7	Rua Cartunista Samuel nº 10 centro hípico	Cabo Frio	RJ	28927-000	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
8	Rua Coronel Moreira César (Rua Paulo Gustavo) nº 300/901 Icaraí	Niterói	RJ	24230-063	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
9	Rua Joaquim P de Almeida nº 05 C/1 sob Raul Veiga	São Gonçalo	RJ	24740-260	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
10	Rua do Sol Lote 31 Qd 12 Jardim Atlântico Oeste Itaipuaçu	Maricá	RJ	24935-475	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
11	Avenida Atlântica 181 apt 202 Floresta das Gaivotas	Rio das Ostras	RJ	28897-322	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
12	Rua Dr. Mário Viana nº734 Bl. 03 apto. 803 Santa Rosa	Niterói	RJ	24241-002	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
13	Rua Mem de Sá nº 168 apto. 401 Icaraí	Niterói	RJ	24220-261	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
14	Rua Nicolau Policarpo nº 1115 casa 01	São Gonçalo	RJ	24738-765	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
15	Avenida Sete de Setembro nº 76 ap 603 Icaraí	Niterói	RJ	24220-000	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
16	Rua Jornalista Carlos Vilhena 1 Santo Antonio	Niterói	RJ	24355-160	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
17	Estrada Itaitindiba nº 376 fundos Santa Isabel Alcântara	São Gonçalo	RJ	24735-790	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
18	Rua Guarapari nº 150 Trindade	São Gonçalo	RJ	24456-130	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
19	Rua Dr. Alberto Torres nº555 BL 08C apt 104 Neves	São Gonçalo	RJ	24426-271	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
20	Rua Miranda Mendes nº 24 Casa 21 Santa Isabel	São Gonçalo	RJ	24738-775	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
21	Rua Noronha Torrezão nº 335 Bl 03 Apto 1506 Santa Rosa	Niterói	RJ	24240-181	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
22	Travessa Alberto Fortes nº 35 Barreto	Niterói	RJ	24110-064	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
23	Rua Tabajaras nº 148 São Francisco	Niterói	RJ	24360-220	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
24	Rua Deodorino de Azevedo Alv. s/n lote 04 casa D Santa Isabel	São Gonçalo	RJ	24738-791	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
25	Rua Araruama 328 Centro	Rio das Ostras	RJ	28893-066	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
26	Rua J S Oliveira lote 39 QD 3 Santa Izabel	São Gonçalo	RJ	24738-830	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
27	Rua dos Mariscos L2A Qd 45 Piratininga	Niterói	RJ	24000-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
28	Rua Des. Ayres Itabaiana de Oliveira 72/201 Vital Brasil	Niterói	RJ	24230-135	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
29	Rua Araruama 328 Centro	Rio das Ostras	RJ	28893-066	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
30	Rua Portugal nº 10 apto 401 centro	Nova Friburgo	RJ	28610-135	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
31	Rua Aguiar 38 cobertura 02 Horto	Campos do Goytacazes	RJ	28080-760	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
32	Rua General Bruce 158 bloco 04 apto 1.006 São Cristóvão	Rio de Janeiro	RJ	20921-030	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
33	Rua Lopes Trovão 394/201 Icaraí	Niterói	RJ	24220-071	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
34	Rua Santos Dumont 36/102 Icaraí	Niterói	RJ	24220-280	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
35	Rua A Backer 579 Bloco A 7 apto. 201 Alcântara	São Gonçalo	RJ	24400-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
36	Rua Cinco 2 Quadra 15 Lote 11 Itaipuaçu	Maricá	RJ	24935-490	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
37	Rua General Silvestre Rocha nº 17 apto 403 Icaraí	Niterói	RJ	24220-170	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
38	Rua Mariz e Barros 470 apt 814 Maracanã	Rio de Janeiro	RJ	20270-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
39	Rua Presidente Pedreira 17/401 Ingá	Niterói	RJ	24240-470	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
40	Rua Gavião Peixoto 71/801 Icaraí	Niterói	RJ	24230-090	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
41	Alameda São Boa Ventura 955 Casa 04	Niterói	RJ	24130-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
42	Avenida Francisco Azeredo Coutinho nº 1 Sitio 1 Ipiíba	São Gonçalo	RJ	24752-427	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
43	Rua Antonio Silva 70/204 Fonseca	Niterói	RJ	24130-175	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
44	Rua A Silva 29/201 Fonseca	Niterói	RJ	24130-175	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
45	Rua Frei Orlando nº 53 Mutondo	São Gonçalo	RJ	24452-150	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
46	Rua Frei Orlando nº 53 Mutondo	São Gonçalo	RJ	24452-150	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
47	Estrada do Pacheco 94 Lagoinha	São Gonçalo	RJ	24732-570	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
48	Rua Nóbrega nº 01 apto 1205 Icaraí	Niterói	RJ	24220-320	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
49	Rua Recife nº 131 Trindade	São Gonçalo	RJ	24457-550	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
50	Travessa José Bagueira Leal nº 50 C1 Barreto	Niterói	RJ	24000-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
51	Rua Leila Diniz 429 São Francisco	Niterói	RJ	24360-110	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
52	Rua Luiz Mota nº 105 LT 05 A Sobrado Raul Veiga	São Gonçalo	RJ	24710-720	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
53	Rua Ruth de Oliveira Ferreira nº 336 Casa 01 São Francisco	Niterói	RJ	24365-278	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
54	Rua Clodomiro Antunes Costa nº 45 Arsenal	São Gonçalo	RJ	24751-360	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
55	Rua Itaporanga nº 100 Monjolos	São Gonçalo	RJ	24724-080	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
56	Rua Clodomiro Antunes da Costa nº 86 Arsenal	São Gonçalo	RJ	24751-360	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
57	Verificar	Verificar	Verificar	Verificar	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
58	Avenida Maracanã nº 667 B01 Apto 605 Maracanã	Rio de Janeiro	RJ	20550-144	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
59	Rua Ator Paulo Gustavo 211 BB 401 Icaraí	Niterói	RJ	24230-052	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
60	Rua Lara Vilela 210/801 São Domingos	Niterói	RJ	24210-590	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
61	Rua Alexandre Barbosa 553 Costazul	Rio das Ostras	RJ	28895-306	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
62	Rua Comandante Abelardo Mata nº 384 Boacu	São Gonçalo	RJ	24465-100	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
63	Av. Sete de Setembro nº 91 apto 1.404 Icaraí	Niterói	RJ	24230-250	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
64	Estrada do Itaitindiba nº 442 Casa Santa Isabel	São Gonçalo	RJ	24738-795	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
65	Rua Blemira 50 ap 101 Piedade	Rio de Janeiro	RJ	20756-200	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
66	Estrada Ipiiba 750 Santa Isabel	São Gonçalo	RJ	24738-415	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
67	Rua Joaquim Torres nº 530 bloco 04 apto 304 Jardim Miriambi	São Gonçalo	RJ	24731-130	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
68	Estrada Ipiiba 750 Santa Isabel	São Gonçalo	RJ	24738-415	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
69	Praia de Icaraí nº 75 apto 401 bloco 02 Icaraí	Niterói	RJ	24230-000	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
70	Rua Inocêncio Dias André s/n Lt 4 Q6 Pacheco	São Gonçalo	RJ	24732-420	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
71	Avenida Roberto Silveira nº 25 Apto 101 Icaraí	Niterói	RJ	24230-150	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
72	Rua Pedro Vieira da Silva 595 b 01 apto 34 Santa Genebra	Campinas	SP	13080-570	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
73	Alameda São Boaventura 1025/1103 Fonseca	Niterói	RJ	24130-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
74	Alameda São Boaventura 369 apto 204 Fonseca	Niterói	RJ	24130-001	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
75	Av São Pedro 170 Centro	São Pedro da Aldeia	RJ	28941-176	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
76	Rodovia Amaral Peixoto lt 04 qd 28 km 103 Praia Linda	São Pedro da Aldeia	RJ	28949-746	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
77	Rua Coronel José Carlos Moreira nº 98	Miracema	RJ	28460-000	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
78	Estrada dos Passageiros nº 1570 Campo Redondo	São Pedro da Aldeia	RJ	28942-444	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
79	Travessa Luiz Azeredo nº 07 A 2 São Francisco	Niterói	RJ	24360-510	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
80	Avenida Professor João Brasil nº 366 bloco 02 apto 304 Fonseca	Niterói	RJ	24130-082	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
81	Avenida Campeões nº 535/203 Ramos	Rio de Janeiro	RJ	21040-016	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
82	Estrada Raul Veiga 315 B2 Ap 401 Raul Veiga	São Gonçalo	RJ	24710-480	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
83	Alameda Geninho nº 37 Casa	São Pedro da Aldeia	RJ	28941-326	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
84	Rua Lopes Trovão nº 134 Ap 1301 Bl 01 Icaraí	Niterói	RJ	24220-070	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
85	Rua Nazaro Machado nº 260 1 Amendoeira	São Gonçalo	RJ	24730-160	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
86	Rua S. José 67/204 Fonseca	Niterói	RJ	24120-325	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
87	Rua Noronha Torrezão 524 apto. 204 bloco 01	Niterói	RJ	24240-183	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
88	Rua Vicente de Lima Cleto 291 casa 01 Nova Cidade	São Gonçalo	RJ	24455-000	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
89	Rua Dr. Alfredo Backer 132 bloco 03 apto 1008 Alcântara	São Gonçalo	RJ	24452-005	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
90	Rua Niterói 231 Jardim Bela Vista	Rio das Ostras	RJ	28895-544	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
91	Rua Alvares de Azevedo 100/602 Icaraí	Niterói	RJ	24220-021	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
92	Rua Presidente Domiciano 157/402	Niterói	RJ	24210-271	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
93	Avenida Jornalista Alberto Francisco Torres 291/1304 Icaraí	Niterói	RJ	24230-004	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
94	Rua Visconde de Moraes 159/1404 Ingá	Niterói	RJ	24210-145	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
\.


--
-- TOC entry 4909 (class 0 OID 60212)
-- Dependencies: 215
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	create basic tables	SQL	V1__create_basic_tables.sql	-152581693	postgres	2024-02-06 11:55:53.937416	31	t
2	2	adding default users	SQL	V2__adding_default_users.sql	-1247101459	postgres	2024-02-06 11:55:53.984597	2	t
\.


--
-- TOC entry 4917 (class 0 OID 60275)
-- Dependencies: 223
-- Data for Name: matriculation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.matriculation (matriculation_id, numero, inicio_erj, cargo, carga_horaria, data_aposentadoria, nivel_atual, trienio_atual, referencia, observacoes, person_id, created_on, modified_on) FROM stdin;
1	3066065-8	2014-06-17 00:00:00	Docente I	30h	\N	5	15	D	\N	1	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
2	0915636-5	2007-02-23 00:00:00	Docente I	18h	\N	7	30	D	\N	1	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
3	0250475-1	1986-08-20 00:00:00	Docente II	22h	2017-01-24 00:00:00	8	55	C	\N	2	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
4	074983-8	1966-05-26 00:00:00	Docente I	16h	1993-07-01 00:00:00	8	45	C	\N	3	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
5	0157513-3	1977-06-04 00:00:00	Docente I	16h	2010-09-11 00:00:00	9	60	D	\N	4	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
6	0153266-2	1976-05-20 00:00:00	Docente II	22h	2004-11-29 00:00:00	8	50	C	\N	5	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
7	0804258-2	1990-12-01 00:00:00	Docente I	16h	2014-06-17 00:00:00	7	40	C	\N	5	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
8	0934042-3	2007-05-23 00:00:00	Docente I	18h	\N	5	20	C	\N	6	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
9	0915378-4	2005-08-25 00:00:00	Docente I	18h	\N	4	15	C	\N	6	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
10	0153180-5	1976-05-25 00:00:00	Prof. Assist II	22h	2004-05-27 00:00:00	9	50	D	\N	7	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
11	503421-0	1972-09-08 00:00:00	Docente II	22h	2010-08-11 00:00:00	7	60	B	\N	8	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
12	0088688-7	1972-03-01 00:00:00	Docente II	22h	1997-06-16 00:00:00	6	45	B	\N	9	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
13	157229-6	1977-04-05 00:00:00	Docente II	22h	2003-02-07 00:00:00	6	45	B	\N	9	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
14	243286-2	1985-05-21 00:00:00	Docente II	22h	2018-02-08 00:00:00	9	55	D	\N	10	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
15	0956417-0	2009-10-29 00:00:00	Prof. Inspetor	25h	\N	5	25	C	\N	10	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
16	0243957-8	1985-05-22 00:00:00	Docente I	16h	2013-05-21 00:00:00	9	50	D	\N	11	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
17	0235167-4	1984-05-02 00:00:00	Docente I	16h	2011-07-12 00:00:00	8	50	C	\N	12	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
18	0024124-0	1967-04-14 00:00:00	Docente I	16	1995-06-26 00:00:00	8	50	C	\N	13	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
19	0252459-3	1986-10-29 00:00:00	Docente II	22h	1999-05-11 00:00:00	5	25	C	\N	14	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
20	0072979-8	1969-09-18 00:00:00	Prof. Assist II	22h	1998-08-17 00:00:00	7	45	B	\N	15	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
21	0135880-3	1970-05-26 00:00:00	Docente II	22h	1998-08-17 00:00:00	7	50	B	\N	16	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
22	0234868-8	1984-05-02 00:00:00	Docente II	22h	2008-04-18 00:00:00	6	45	B	\N	17	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
23	0240540-5	1985-05-16 00:00:00	Docente II	22h	2017-03-17 00:00:00	9	55	D	\N	18	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
24	0515008-1	1968-08-01 00:00:00	Docente I	16h	2012-10-09 00:00:00	8	60	C	\N	19	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
25	0234961-1	1984-05-02 00:00:00	Docente II	22h	2010-05-28 00:00:00	9	45	D	\N	20	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
26	0515025-5	1973-03-12 00:00:00	Docente I	16h	2007-07-20 00:00:00	8	55	C	\N	21	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
27	0913739-9	2005-02-01 00:00:00	Docente I	18h	\N	6	30	D	\N	22	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
28	841662-0	2001-02-12 00:00:00	Docente I	18h	\N	7	35	D	\N	22	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
29	1514936-2	1971-03-09 00:00:00	Docente I	16h	1997-11-04 00:00:00	9	50	D	\N	23	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
30	50786-3	1962-05-02 00:00:00	Docente I	16h	1990-05-31 00:00:00	8	50	C	\N	23	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
31	0234736-3	1984-05-02 00:00:00	Docente II	22h	2017-06-01 00:00:00	8	60	C	\N	24	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
32	0021666-3	1985-10-27 00:00:00	Docente I	16h	1985-10-28 00:00:00	8	45	C	\N	25	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
33	0234693-0	1984-05-02 00:00:00	Docente II	22h	2009-05-19 00:00:00	7	45	B	\N	26	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
34	0252454-4	1986-10-29 00:00:00	Docente II	22h	2019-11-01 00:00:00	9	50	D	\N	27	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
35	0248139-8	1985-09-24 00:00:00	Docente I	16h	2011-11-26 00:00:00	8	45	C	\N	28	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
36	0082828-5	1972-05-30 00:00:00	Docente I	16h	2002-04-05 00:00:00	8	55	C	\N	29	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
37	0110651-7	1973-03-12 00:00:00	Docente I	16h	2003-03-06 00:00:00	8	55	C	\N	29	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
38	2806933	1988-03-10 00:00:00	Docente II	22h	2017-12-11 00:00:00	9	50	D	\N	30	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
39	8061970	1991-02-01 00:00:00	Docente II	22h	2018-08-23 00:00:00	9	45	D	\N	30	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
40	3083142-4	2015-10-06 00:00:00	Docente I	30h	\N	4	10	D	\N	31	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
41	8331142	1998-08-27 00:00:00	Docente I	18h	\N	3	10	C	\N	32	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
42	8409161	2000-05-24 00:00:00	Docente I	18h	\N	3	10	C	\N	32	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
43	0237991-5	1984-05-15 00:00:00	Docente I	16h	2012-01-31 00:00:00	8	50	C	\N	33	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
44	0249523-2	1986-08-08 00:00:00	Docente I	16h	2012-01-31 00:00:00	8	45	C	\N	33	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
45	0241737-6	1985-05-17 00:00:00	Docente I	16h	2016-09-01 00:00:00	9	55	D	\N	34	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
46	0231548-9	1983-09-12 00:00:00	Docente II	22h	2010-05-11 00:00:00	8	45	C	\N	35	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
47	0152616-9	1976-05-18 00:00:00	Docente I	16h	2004-12-08 00:00:00	8	50	C	\N	36	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
48	0240773-2	1985-05-16 00:00:00	Docente I	16h	2004-12-08 00:00:00	6	45	C	\N	36	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
49	0402292A	\N	Docente I	16h	2008-11-12 00:00:00	6	\N	C	\N	37	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
50	0093655-9	1985-12-29 00:00:00	Docente II	22h	1985-12-30 00:00:00	7	50	B	\N	38	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
51	0137901-5	1973-07-23 00:00:00	Docente I	18h	\N	8	60	C	\N	39	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
52	0514190-8	1973-05-11 00:00:00	Docente II	22h	2001-09-04 00:00:00	08	45	C	\N	40	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
53	0234485-1	1984-05-02 00:00:00	Docente II	22h	\N	6	55	A	\N	41	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
54	1208032-1	1982-09-20 00:00:00	Docente II	22h	\N	8	45	C	\N	42	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
55	0515221-0	1968-03-27 00:00:00	Docente II	22h	\N	7	50	B	\N	43	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
56	0515246-7	1974-04-22 00:00:00	Docente II	22h	\N	7	60	B	\N	44	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
57	515255-8	1974-04-22 00:00:00	Docente II	22h	\N	7	55	B	\N	45	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
58	0234864-7	1984-05-02 00:00:00	Docente II	22h	\N	6	45	A	\N	46	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
59	0281011-7	1988-03-11 00:00:00	Docente I	16h	\N	9	50	D	\N	47	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
60	0955228-2	2009-09-25 00:00:00	Docente I	18h	\N	6	25	D	\N	47	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
61	2449825	1985-05-23 00:00:00	Docente II	22h	2008-03-03 00:00:00	8	35	D	\N	48	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
62	11625266	1978-04-17 00:00:00	Docente II	22h	2008-02-26 00:00:00	9	50	D	\N	48	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
63	291405-9	1990-08-15 00:00:00	Docente II	22h	2016-02-04 00:00:00	9	45	D	\N	49	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
64	515282-2	1974-02-11 00:00:00	Docente I	16h	2007-04-13 00:00:00	8	55	C	\N	50	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
65	37714-3	1986-09-01 00:00:00	Docente I	16h	1992-01-30 00:00:00	8	50	C	\N	51	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
66	0515280-6	1972-05-29 00:00:00	Docente I	16h	1998-05-06 00:00:00	7	45	C	\N	52	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
67	0957554-9	2009-12-02 00:00:00	Inspetor Escolar	25h	\N	6	50	D	\N	53	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
68	0234930-6	1984-05-02 00:00:00	Docente II	22h	2015-07-14 00:00:00	6	55	A	\N	54	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
69	0138804-0	1974-08-06 00:00:00	Docente I	16h	2003-06-27 00:00:00	9	45	D	\N	55	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
70	0111794-4	1973-04-26 00:00:00	Docente I	16h	1997-11-07 00:00:00	8	40	D	\N	55	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
71	0234871-2	1984-05-02 00:00:00	Docente II	22h	2009-09-02 00:00:00	8	45	C	\N	56	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
72	0937707-8	2008-02-11 00:00:00	Docente I	18h	\N	6	25	D	\N	57	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
73	5012654-9	1994-03-07 00:00:00	Docente I	\N	2019-05-30 00:00:00	9	45	D	\N	57	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
74	0515313-5	1974-05-20 00:00:00	Docente I	16h	2001-11-21 00:00:00	8	50	C	\N	58	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
75	1167047-8	1976-05-18 00:00:00	Docente I	16h	2006-03-30 00:00:00	8	50	C	\N	58	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
76	0514142-9	1972-04-14 00:00:00	Docente I	16h	2011-05-24 00:00:00	8	60	C	\N	59	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
77	2437812	1985-05-21 00:00:00	Docente II	22h	2009-11-10 00:00:00	8	45	D	\N	60	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
78	1954759	1982-04-01 00:00:00	Docente II	22h	2009-10-23 00:00:00	9	50	D	\N	60	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
79	0807222-5	1991-02-01 00:00:00	Docente I	16h	\N	8	45	C	\N	61	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
80	0515355-6	1974-04-22 00:00:00	Docente II	22h	2005-06-13 00:00:00	8	50	C	\N	62	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
81	0160671-4	1977-07-29 00:00:00	Docente II	22h	2009-07-28 00:00:00	8	55	C	\N	62	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
82	968552	1972-03-06 00:00:00	Docente I	16h	2010-09-29 00:00:00	9	60	D	\N	63	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
83	742775	1970-03-23 00:00:00	Inspetora	25h	2003-05-26 00:00:00	9	60	D	\N	63	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
84	1208034-7	1982-09-20 00:00:00	Docente II	22h	2009-01-28 00:00:00	9	45	D	\N	64	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
85	0917832-8	2005-02-01 00:00:00	Docente I	18h	\N	6	30	D	\N	65	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
86	5005912-0	1994-03-07 00:00:00	Docente II	40h	2021-09-23 00:00:00	9	50	D	\N	66	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
87	0234869-6	1984-05-02 00:00:00	Docente II	22h	2012-05-04 00:00:00	7	50	B	\N	67	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
88	0234694-8	1984-05-02 00:00:00	Docente II	22h	2011-11-29 00:00:00	9	50	D	\N	68	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
89	0073669-4	1970-08-11 00:00:00	Assistente de Adm Educ II	22h	1999-07-16 00:00:00	7	45	B	\N	69	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
90	0107865-8	1972-03-27 00:00:00	Docente II	22h	2006-08-29 00:00:00	7	55	B	\N	70	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
91	035024-9	1967-04-14 00:00:00	Docente I	16h	1993-09-28 00:00:00	8	50	C	\N	71	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
92	243214-4	1985-05-21 00:00:00	Docente I	16h	2010-07-20 00:00:00	8	45	C	\N	71	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
93	0832523-5	1998-06-02 00:00:00	Docente I	16h	2015-07-22 00:00:00	6	NA	C	\N	72	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
94	0250470-2	1986-08-20 00:00:00	Docente II	22h	2012-02-27 00:00:00	9	45	D	\N	73	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
95	0515415-8	1973-04-16 00:00:00	Docente II	22h	2002-03-25 00:00:00	7	50	B	\N	74	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
96	0255778-3	1987-02-16 00:00:00	Docente I	16h	2008-04-16 00:00:00	7	40	C	\N	75	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
97	292435-5	1990-08-15 00:00:00	Docente II	22h	2010-10-27 00:00:00	7	60	C	\N	76	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
98	1162659-5	1978-05-05 00:00:00	Docente II	22h	2007-08-30 00:00:00	8	55	C	\N	76	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
99	283287	1988-06-23 00:00:00	Docente II	22h	2018-05-08 00:00:00	9	50	D	\N	77	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
100	251102	1986-10-03 00:00:00	Docente II	22h	2017-02-15 00:00:00	9	50	D	\N	77	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
101	0194233-3	1982-04-01 00:00:00	Docente II	22h	2012-08-01 00:00:00	9	55	D	\N	78	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
102	0080935-0	1972-03-24 00:00:00	Assistente de Adm Educ I	16h	2003-07-14 00:00:00	9	60	D	\N	79	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
103	0106417-9	1970-04-29 00:00:00	Docente I	16h	2008-09-25 00:00:00	9	60	D	\N	80	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
104	0942715-4	2008-03-17 00:00:00	Inspetora	25h	\N	7	30	D	\N	80	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
105	161870-1	1977-04-14 00:00:00	Docente II	22h	2008-06-06 00:00:00	6	55	A	\N	81	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
106	156296-6	1977-03-30 00:00:00	Docente II	22h	2007-07-04 00:00:00	9	55	D	\N	82	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
107	162647-2	1978-05-05 00:00:00	Docente II	22h	2021-05-12 00:00:00	9	60	D	\N	82	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
108	167433 2	1974-10-17 00:00:00	Docente I	16h	1974-10-17 00:00:00	8	60	C	\N	83	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
109	0103100-4	1969-04-17 00:00:00	Docente I	16h	1998-09-23 00:00:00	8	50	C	\N	83	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
110	0024086-1	1969-03-11 00:00:00	Assistente de Adm Educ I	16h	1993-02-02 00:00:00	8	55	C	\N	84	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
111	0234888-6	1984-05-02 00:00:00	Docente II	22h	2006-03-22 00:00:00	7	40	C	\N	85	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
112	0283392-9	1988-06-24 00:00:00	Docente II	22h	2013-12-26 00:00:00	7	45	B	\N	86	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
113	0230782-5	1983-03-01 00:00:00	Docente II	22h	2013-11-26 00:00:00	7	55	B	\N	86	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
114	0162537-5	1978-05-05 00:00:00	Docente II	22h	2013-05-28 00:00:00	7	60	B	\N	87	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
115	0940061-5	2008-02-27 00:00:00	Docente I	18h	\N	6	25	D	\N	88	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
116	0942767-5	2008-03-17 00:00:00	Inspetor Escolar	25h	\N	6	25	D	\N	88	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
117	3057640-9	2014-02-27 00:00:00	Docente I	18h	\N	4	15	C	\N	89	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
118	0942721-2	2008-03-17 00:00:00	Inspetor Escolar	25h	2022-09-23 00:00:00	6	25	D	\N	90	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
119	0285312-5	1989-10-03 00:00:00	Docente I	16h	2020-02-06 00:00:00	8	55	C	\N	91	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
120	0103556-7	1972-03-27 00:00:00	Docente II	22h	1997-12-05 00:00:00	7	45	C	\N	92	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
121	0283640-1	1988-06-27 00:00:00	Docente I	16h	2015-09-09 00:00:00	09	50	D	\N	93	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
122	0249494-6	1986-08-08 00:00:00	Docente I	16h	2013-08-27 00:00:00	09	50	D	\N	93	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
123	0255900-3	1987-02-25 00:00:00	Docente I	16h	2012-11-09 00:00:00	9	45	D	\N	94	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
124	0154802-3	1977-03-31 00:00:00	Docente II	22h	2001-09-27 00:00:00	8	45	D	\N	94	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
125	g	2024-04-22 19:36:09.876	af	fds	2024-04-18 01:00:00	s	f	z		\N	2024-04-22 19:37:02.006831	2024-04-22 19:37:02.006831
126	3	2024-04-22 20:07:16.577	g	fg	2024-04-24 01:00:00	g	c	f		\N	2024-04-22 20:08:27.895136	2024-04-22 20:08:27.895136
127	sd	2024-04-22 20:08:46.073	xdf	df	2024-04-18 01:00:00	v	v	c		\N	2024-04-22 20:09:21.989717	2024-04-22 20:09:21.989717
128	c	2024-04-22 20:10:19.78	c	c	2024-04-10 01:00:00	c	c	c		\N	2024-04-22 20:10:57.451489	2024-04-22 20:10:57.451489
129	t	2024-04-22 20:12:28.462	t	t	2024-04-23 01:00:00	t	t	t		\N	2024-04-22 20:13:04.023939	2024-04-22 20:13:04.023939
130	r	2024-04-22 20:12:28.462	r	r	2024-04-11 01:00:00	r	r	r		\N	2024-04-22 20:13:04.023939	2024-04-22 20:13:04.023939
131	sd	2024-04-26 18:38:55.549	sd	d	2024-05-01 01:00:00	s	s	s		\N	2024-04-26 18:39:50.577136	2024-04-26 18:39:50.577136
132	ds	2024-04-26 18:38:55.549	d	d	2024-04-25 01:00:00	a	a	a		\N	2024-04-26 18:39:50.577136	2024-04-26 18:39:50.577136
133	fdf	2024-04-26 18:40:20.873	ddf	df	2024-05-01 01:00:00	f	f	f		\N	2024-04-26 18:41:17.219866	2024-04-26 18:41:17.219866
134	sfd	2024-04-26 18:40:20.873	d	d	2024-04-27 01:00:00	d	d	d		\N	2024-04-26 18:41:17.219866	2024-04-26 18:41:17.219866
\.


--
-- TOC entry 4921 (class 0 OID 60307)
-- Dependencies: 227
-- Data for Name: moviment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.moviment (moviment_id, descricao, date, process_id, created_on, modified_on) FROM stdin;
1	Juntada de Cálculos do Contador	2023-10-26 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
2	Juntada Contrarrazões ERJ	2022-11-20 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
3	Juntada Petição Autora requerendo prosseguimento em razãod e trânsito do Agravo	2023-04-24 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
4	Determinada livre distribuição	2022-01-14 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
5	Ação Distribuída	2022-01-11 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
6	Juntada Petição Autora em resposta à Impugnação	2022-05-03 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
7	Juntada Petição ERJ com avaliação de 2003	2023-07-19 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
8	Sentença negando EDs	2022-11-30 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
9	Juntada EDs Autora sobre avaliação de 2003	2022-06-09 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
10	Juntada Petição Autora concordando com cálculos	2023-11-07 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
11	Decisão - Deferida Jg | Determinada intimação ERJ	2022-03-07 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
12	Juntada Impugnação ERJ	2022-04-28 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
13	Determinada a manifestação das partes sobre os cálculos	2023-10-26 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
14	Despacho - Ao ERJ | Ao Contador	2023-07-13 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
15	Juntada Petição Estado discordando dos cálculos	2023-11-13 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
16	Despacho ao Embargado	2022-07-07 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
17	Decisão determinando parâmetros	2022-05-13 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
18	Juntada Petição ERJ informando Agravo (0044240-06.2022.8.19.0000)	2022-06-19 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
19	Petição do Município de Niterói em razão de erro do Cartório	2022-08-03 00:00:00	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
20	Decisão Determinando Intimação ERJ	2023-05-09 00:00:00	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
21	Juntada Impugnação ERJ	2023-08-01 00:00:00	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
22	Envio dos Autos para Contador	2023-08-02 00:00:00	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
23	Intimação ERJ Enviada	2023-05-30 00:00:00	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
24	Ação Distribuída	2023-01-19 00:00:00	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
25	Juntada Petição Autora em resposta à Impugnação	2023-08-01 00:00:00	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
26	Juntada Petição Autora pedindo homologação	2023-07-19 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
27	Intimação ERJ Enviada	2023-05-30 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
28	Verificado pagamento	2024-01-08 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
29	Decisão Homologando Cálculos	2023-07-20 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
30	RPV Autora 	2023-11-06 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
31	Enviada intimação sobre Decisão	2023-10-30 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
32	Decisão Determinando Intimação ERJ	2023-05-09 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
33	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
34	Ação Distribuída	2023-01-19 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
35	Envio RPV	2023-11-07 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
36	Protocolada Petição pedindo expedição de RPV com valores	2023-08-10 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
37	Juntada Concordancia ERJ	2023-07-19 00:00:00	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
38	Juntada Petição Autora em resposta à Impugnação	2023-07-19 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
39	Intimação ERJ Enviada	2023-05-30 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
40	Envio dos Autos para Contador	2023-08-16 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
41	Juntada Impugnação ERJ	2023-07-19 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
42	Ação Distribuída	2023-01-19 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
43	Contato com setor da contadoria - informou ainda não ter previsão -ficamos de retornar	2023-11-05 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
44	Decisão Determinando Intimação ERJ	2023-05-09 00:00:00	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
45	Juntada Petição Autora pedindo homologação	2023-07-27 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
46	Decisão Determinando Intimação ERJ	2023-05-11 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
47	Ação Distribuída	2023-01-24 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
48	Intimação ERJ Enviada	2023-05-31 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
49	Decisão Homologando Cálculos	2023-08-07 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
50	Juntada Concordancia ERJ	2023-07-27 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
51	digitada RPV 	2023-11-17 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
52	Protocolada Petição pedindo expedição de RPV com valores	2023-08-10 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
53	enviada RPV 	2023-11-17 00:00:00	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
54	Decisão Homologando Cálculos	2023-08-15 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
55	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
56	Juntada Concordancia ERJ	2023-07-27 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
57	Protocolada Petição pedindo expedição de RPV com valores	2023-08-18 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
58	Decisão Determinando Intimação ERJ	2023-05-11 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
59	Verificado pagamento	2024-01-08 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
60	Petição Autora sobre RPV	2023-11-29 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
61	RPV Autora 	2023-11-24 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
62	Juntada Petição Autora pedindo homologação	2023-07-27 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
63	Intimação ERJ Enviada	2023-05-31 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
64	Ação Distribuída	2023-01-24 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
65	Envio RPV	2023-11-27 00:00:00	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
66	Protocolada Petição pedindo expedição de RPV com valores	2023-08-22 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
67	Decisão Determinando Intimação ERJ	2023-05-11 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
68	Ação Distribuída	2023-01-24 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
69	Juntada Concordancia ERJ	2023-08-05 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
70	Juntada Petição Autora pedindo homologação	2023-08-05 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
71	Petição atualizando teto 40 salários para 2024	2023-12-20 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
72	Intimação ERJ Enviada	2023-05-31 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
73	Decisão Homologando Cálculos	2023-08-21 00:00:00	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
74	Verificado Pagamento	2024-01-10 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
75	RPV Autora digitado	2023-11-12 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
76	Juntada de Petição requerendo levantamento	2024-01-11 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
77	Ação Distribuída	2023-01-24 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
78	Envio RPV	2023-11-14 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
79	Protocolada Petição pedindo expedição de RPV com valores	2023-08-10 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
80	Juntada Petição Autora pedindo homologação	2023-07-27 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
81	Decisão Determinando Intimação ERJ	2023-05-12 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
82	Juntada Concordancia ERJ	2023-07-27 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
83	Intimação ERJ Enviada	2023-05-31 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
84	Decisão Homologando Cálculos	2023-08-07 00:00:00	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
85	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
86	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
87	Intimação ERJ Enviada	2023-07-06 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
88	Ação Distribuída	2023-01-31 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
89	Envio RPV	2023-11-14 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
90	Petição Autora sobre RPV	2023-11-16 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
91	Juntada Petição Autora pedindo homologação	2023-09-02 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
92	RPV Autora 	2023-11-10 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
93	Juntada Concordancia ERJ	2023-09-02 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
94	Decisão Homologando Cálculos	2023-09-26 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
95	Verificado pagamento	2024-01-08 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
96	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
97	Ação Distribuída	2023-01-31 00:00:00	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
98	Juntada Impugnação ERJ	2023-09-01 00:00:00	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
99	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
100	Envio dos Autos para Contador	2023-09-05 00:00:00	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
101	Intimação ERJ Enviada	2023-07-06 00:00:00	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
102	Juntada Petição Autora em resposta à Impugnação	2023-09-01 00:00:00	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
103	Juntada Concordancia ERJ	2023-09-02 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
104	Ação Distribuída	2023-01-31 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
105	Juntada Petição Autora pedindo homologação	2023-09-02 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
106	Decisão Homologando Cálculos	2023-09-26 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
107	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
108	Intimação ERJ Enviada	2023-07-06 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
109	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
110	RPV Autora digitada	2023-12-15 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
111	Juntada Petição Autora pedindo homologação	2023-09-02 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
112	Ação Distribuída	2023-01-31 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
113	Envio RPV 	2023-12-15 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
114	Decisão Homologando Cálculos	2023-09-21 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
115	Juntada Concordancia ERJ	2023-09-02 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
116	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
117	Intimação ERJ Enviada	2023-07-06 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
118	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
119	RPV Autora 	2023-11-13 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
120	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
121	Verificado pagamento	2024-01-08 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
122	Decisão Homologando Cálculos	2023-09-26 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
123	Ação Distribuída	2023-01-31 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
124	Envio RPV	2023-11-14 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
125	Intimação ERJ Enviada	2023-07-06 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
126	Petição sobre RPV	2023-11-16 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
127	Juntada Concordancia ERJ	2023-09-02 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
128	Juntada Petição Autora pedindo homologação	2023-09-02 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
129	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
130	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
131	RPV enviado	2024-01-10 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
132	Juntada Petição Autora pedindo RPV	2023-09-27 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
133	Autos conclusos	2023-09-07 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
134	Intimação ERJ Enviada	2023-07-13 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
135	Juntada Concordancia ERJ	2023-09-07 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
136	Ação Distribuída	2023-03-15 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
137	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
138	Juntada Petição Autora pedindo homologação	2023-09-07 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
139	Decisão homologando cálculos	2023-09-21 00:00:00	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
140	Juntada Concordancia ERJ	2023-09-01 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
141	Ação Distribuída	2023-01-31 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
142	Envio RPV 	2023-12-15 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
143	Decisão Homologando Cálculos	2023-09-21 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
144	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
145	Intimação ERJ Enviada	2023-07-06 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
146	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
147	Juntada Petição Autora pedindo homologação	2023-09-01 00:00:00	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
148	Petição Autora sobre RPV	2023-11-13 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
149	Verificado pagamento	2024-01-08 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
150	Envio RPV	2023-11-13 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
151	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
152	RPV Autora 	2023-11-10 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
153	Intimação ERJ Enviada	2023-07-06 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
154	Ação Distribuída	2023-01-31 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
155	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
156	Decisão Homologando Cálculos	2023-09-26 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
157	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
158	Juntada Concordancia ERJ	2023-09-01 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
159	Juntada Petição Autora pedindo homologação	2023-09-01 00:00:00	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
160	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
161	Juntada Petição Autora pedindo homologação	2023-09-04 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
162	Juntada Concordancia ERJ	2023-09-04 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
163	Ação Distribuída	2023-01-31 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
164	Verificado pagamento	2024-01-04 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
165	Decisão Homologando Cálculos	2023-09-21 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
166	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
167	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
168	Digitada RPV	2023-11-13 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
169	Enviada RPV	2023-11-13 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
170	Intimação ERJ Enviada	2023-07-06 00:00:00	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
171	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
172	Enviada RPV	2023-11-17 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
173	Intimação ERJ Enviada	2023-07-06 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
174	Juntada Petição Autora pedindo homologação	2023-09-04 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
175	Digitada RPV	2023-11-13 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
176	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
177	Decisão Homologando Cálculos	2023-09-21 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
178	Ação Distribuída	2023-01-31 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
179	Juntada Concordancia ERJ	2023-09-04 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
180	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
181	Verificado pagamento	2024-01-04 00:00:00	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
182	Decisão Determinando Intimação ERJ	2023-05-16 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
183	Processo com juiz para análise	2024-01-08 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
184	Enviado novo E-mail sobre falta de movimentação 	2024-01-08 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
185	Intimação ERJ Enviada	2023-06-15 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
186	Enviado E-mail sobre falta de movimentação	2023-12-06 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
187	Juntada Petição Autora pedindo homologação	2023-08-12 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
188	Ação Distribuída	2023-02-02 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
189	Juntada Concordancia ERJ	2023-08-12 00:00:00	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
190	Ação Distribuída	2023-02-02 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
191	Juntada Concordancia ERJ	2023-08-15 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
192	Verificado pagamento	2024-01-10 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
193	Petição Autora sobre RPV	2023-11-16 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
194	RPV enviado	2023-11-14 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
195	Intimação ERJ Enviada	2023-06-19 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
196	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
197	Decisão Homologando Cálculos	2023-08-23 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
198	RPV Autora digitado	2023-11-10 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
199	Juntada de Petição requerendo levantamento	2024-01-11 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
200	Protocolada Petição pedindo expedição de RPV com valores	2023-08-25 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
201	Juntada Petição Autora pedindo homologação	2023-08-15 00:00:00	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
202	Digitado RPV 	2023-11-09 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
203	Decisão Determinando Intimação ERJ	2023-05-16 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
204	Decisão Homologando Cálculos 	2023-08-23 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
205	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
206	Pagamento Verificado	2024-01-04 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
207	Intimação ERJ Enviada	2023-06-15 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
208	Juntada Impugnação ERJ	2023-08-12 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
209	Protocolados EDs Autora sobre sucumbência mínima	2023-08-25 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
210	Juntada Petição Autora pedindo homologação (Diferença mínima)	2023-08-12 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
211	Enviado RPV	2023-11-10 00:00:00	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
212	Juntada Petição Autora pedindo homologação	2023-08-22 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
213	Protocolada Petição pedindo expedição de RPV com valores	2023-09-28 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
214	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
215	Juntada Concordancia ERJ	2023-08-22 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
216	Intimação ERJ Enviada	2023-06-19 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
217	Digitada RPV	2024-01-16 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
218	Decisão Homologando Cálculos	2023-09-26 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
219	Ação Distribuída	2023-02-09 00:00:00	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
220	Juntada Petição Autora pedindo homologação	2023-08-15 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
221	Intimação ERJ Enviada	2023-06-19 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
222	Enviado novo E-mail sobre falta de movimentação do cartório	2024-01-08 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
223	Enviado E-mail sobre falta de movimentação	2023-12-06 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
224	Processo com juiz para análise	2024-01-08 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
225	Ação Distribuída	2023-02-08 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
226	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
227	Juntada Concordancia ERJ	2023-08-15 00:00:00	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
228	Ação Distribuída	2023-02-09 00:00:00	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
229	Juntada Petição Autora pedindo homologação	2023-08-27 00:00:00	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
230	Autos Conclusos	2023-08-27 00:00:00	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
231	Juntada Concordancia ERJ	2023-08-27 00:00:00	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
232	Intimação ERJ Enviada	2023-06-30 00:00:00	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
233	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
234	Enviada intimação ERJ	2023-10-23 00:00:00	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
235	Decisão Determinando Intimação ERJ	2023-07-04 00:00:00	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
236	Juntada Petição Autora solicitando a homologação dos cálculos	2023-12-12 00:00:00	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
237	Ação Distribuída	2023-02-08 00:00:00	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
238	Juntada petição ERJ concordando com cálculos	2023-12-07 00:00:00	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
239	Decisão homologando Cálculos	2023-08-17 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
240	Digitada RPV	2023-11-08 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
241	Decisão determinando Intimação ERJ	2023-05-16 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
242	Intimação ERJ Enviada	2023-06-15 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
243	Juntada Petição Autora solicitando Mandado de Pagamento	2024-01-10 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
244	Juntada Concordancia ERJ	2023-08-14 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
245	Juntada Petição Autora pedindo homologação - com renúncia	2023-08-14 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
246	Ação Distribuída	2023-02-09 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
247	Verificado Pagamento	2024-01-04 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
248	Enviada RPV	2023-11-10 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
249	Protocolada Petição pedindo expedição RPV em razão de renúncia	2023-08-18 00:00:00	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
250	Digitado Mandado de Pagamento	2023-12-07 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
251	Ato ordinatório: às partes	2023-04-27 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
252	Juntada Petição ERJ reiterando Impugnação	2022-06-08 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
253	Ato ordinatório à Autora sobre quitação	2023-12-15 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
254	Decisão ao Contador	2022-07-18 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
255	Decisão rejeitando impugnação	2022-03-11 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
256	Trânsito em julgado	2023-09-01 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
257	Ação Distribuída	2021-10-28 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
258	Juntada Cálculos	2022-10-19 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
259	Juntada Impugnação ERJ	2022-02-09 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
260	Juntada Petição ERJ Reiterando impugnação	2023-05-03 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
261	Juntada Petição Autora informando concordancia	2022-10-27 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
262	Sentença rejeitando impugnação	2023-07-05 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
263	Juntada Petição Autora com Documentos para cálculo	2022-04-11 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
264	Juntada Petição Autora pedindo levantamento valor depositado	2023-10-16 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
265	Digitação RPV	2023-09-06 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
266	Juntada de novos cálculos 	2023-04-27 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
267	Juntada Petição Autora informando parâmetros incorretos do cálculo	2022-06-13 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
268	Juntada Petição ERJ reiterando impugnação	2022-12-06 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
269	Juntada Petição ERJ com informação de pedido de pagamento	2023-09-19 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
270	Juntada Petição Autora com taxa 	2023-09-13 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
271	Decisão - Deferida JG | Determinada intimação ERJ	2021-10-29 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
272	Juntada Cálculos Contador 	2022-06-06 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
273	Petição autora requerendo a expedição do Mandado	2023-12-20 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
274	Juntada Petição Autora concordando com cálculos	2023-05-02 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
275	Juntada Petição Autora em resposta à Impugnação	2022-02-15 00:00:00	27	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
276	Autos remetidos à primeira instância	2023-03-21 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
277	Juntada Petição Autora em resposta à Impugnação	2021-11-22 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
278	Juntada EDs ERJ	2022-08-08 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
279	Decisão de não admissão	2022-10-18 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
280	Juntada Impugnação ERJ	2021-11-19 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
281	Juntada REsp	2022-10-04 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
282	Autos conclusos	2023-10-19 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
283	Juntada Contrarrazões ao AResp	2022-10-31 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
284	Despacho deferindo JG e determinando intimação ERJ	2021-10-27 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
285	Protocolada Petição Autora requerendo RPV	2023-04-24 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
286	Determinada intimação do Estado sobre Petição Autora	2023-11-13 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
287	Juntada Contrarrazões ao EDs	2022-08-22 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
288	Negado AResp no STJ	2023-01-11 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
289	Ação Distribuída	2021-10-14 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
290	Juntada AResp ERJ	2022-10-26 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
291	Juntada Contrarrazões ERJ	2022-04-28 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
292	Juntada Apelação Autora	2022-02-15 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
293	Expedida intimação do Estado	2023-12-12 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
294	Decisão declinando competência - SG	2021-10-17 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
295	Sentença extinguindo por Prescrição	2022-01-24 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
296	Julgado provido Recurso	2022-08-02 00:00:00	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
297	Juntada Petição Autora sobre desnecessidade de ofício	2022-03-03 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
298	Ação Distribuída	2021-09-27 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
299	Juntada Petição Autora reiterando pedido de prosseguimento	2023-04-24 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
300	Envio de Ofício	2022-08-25 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
301	Decisão determinando envio de ofício à 8ª Vara	2022-02-17 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
302	Juntada Petição Autora em resposta à Impugnação	2021-11-22 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
303	Despacho determinando que se aguarde resposta de Ofício	2023-02-28 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
304	Despacho pedindo que cartório certifique se houve resposta do ofício	2023-07-28 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
305	Juntada Impugnação ERJ	2021-11-12 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
306	Despacho deferindo JG e determinando intimação ERJ	2021-09-28 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
307	Manifestação Mp - sem interesse no feito	2023-10-31 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
308	Enviada intimação MP	2023-10-18 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
383	Digitado RPV 	2023-11-17 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
309	Despacho - Vista ao MP para que se manifeste em relação ao feito. Após, cls	2023-10-04 00:00:00	29	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
310	Despacho Ao Embargado	2023-04-25 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
311	Decisão rejeitando a impugnação	2022-10-11 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
312	Ação Distribuída	2021-10-05 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
313	Juntada Impugnação ERJ	2022-07-27 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
314	Juntada Petição ERJ pedindo sobrestamento	2023-08-19 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
315	Juntada Petição Autora em resposta à Impugnação	2022-08-01 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
316	Juntada Petição ERJ informando Agravo (0092245-59.2022.8.19.0000)	2022-11-30 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
317	Negado Provimento aos EDs	2023-07-24 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
318	Decisão - Deferida JG | Determinada Intimação ERJ	2021-10-14 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
319	Juntada Petição solicitando julgamento EDs	2023-02-01 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
320	Expedida Nova intimação ERJ	2022-06-22 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
321	Juntado EDs Autora sobre honorários	2022-10-31 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
322	Despacho - Cumpra-se decisão de superior instância. Anote-se o sobrestamento do feito.	2023-12-04 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
323	Juntada Petição Autora sobre inercia ERJ	2022-01-07 00:00:00	30	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
324	Despacho - As partes sobre desconto previdenciário	2023-08-15 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
325	Juntada Petição Autora concordando com cálculos	2022-10-27 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
326	Digitado Mandado de Pagamento	2023-12-07 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
327	Ação Distribuída	2021-12-06 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
328	Envio de RPV	2023-09-06 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
329	Sentença rejeitando impugnação	2023-02-24 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
330	Despacho - Ao contador	2022-07-18 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
331	Juntada Petição ERJ informando pedido de pagamento	2023-09-19 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
332	Juntada Petição Autora em resposta à Impugnação	2022-02-16 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
333	Juntada de Cálculos 	2022-06-06 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
334	Trânsito em julgado	2023-04-28 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
335	Juntada Petição Autora com documentos para cálculo	2022-04-11 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
336	Juntada Petição ERJ reiterando impugnação	2022-10-27 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
337	Juntada Cálculos 	2022-10-19 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
338	Juntada Impugnação ERJ	2022-02-15 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
339	Decisão - deferida JG | Determinada intimação ERJ	2021-12-06 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
340	Juntada Petição Autora Sobre valores - Taxa e sucumbencia	2023-08-18 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
341	Juntada Petição ERJ reiterando impugnação	2022-06-08 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
342	Petição autora requerendo a expedição do Mandado	2023-12-20 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
343	Juntada Petição Autora pedindo levantamento valor depositado	2023-10-16 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
344	Ato ordinatório à Autora sobre quitação	2023-12-15 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
345	Decisão rejeitando Impugnação	2022-03-17 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
346	Juntada Petição Autora sobre cálculos	2022-06-13 00:00:00	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
347	Juntada Concordancia ERJ	2023-09-27 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
348	Intimação ERJ Enviada	2023-09-12 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
349	EDS Autora sobre JG	2023-12-04 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
350	Decisão Determinando Intimação ERJ	2023-05-24 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
351	Ação Distribuída	2023-02-14 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
352	Juntada Petição Autora pedindo homologação	2023-10-04 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
353	Decisão homologando - com erro material	2023-11-27 00:00:00	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
354	Decisão extinguindo o processo	2023-08-11 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
355	Juntada Petição Autora de reconsideração	2022-04-20 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
356	Despacho - Expeçam-se os Ofícios Requisitórios dos valores constantes do cálculo	2023-05-09 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
357	Juntada Petição ERJ com impugnação	2022-09-06 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
358	Juntada EDs Autores	2023-08-17 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
359	Decisão homologando valor (R$ 53.979,71)	2023-02-03 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
360	Juntada Petição Autora sobre inercia ERJ	2022-08-24 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
361	Decisão de declínio (Juizado)	2022-04-13 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
362	Juntada Petição Autora requerendo expedição RPV	2023-02-14 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
363	Juntada Petição Autora reiterando expedição RPV	2023-06-14 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
364	Ação Distribuída	2022-04-04 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
365	Juntada Petição Autora em resposta à Impugnação	2022-09-27 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
366	Juntada Petição ERJ sobre competência	2023-05-11 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
367	Decisão reconsiderando e determinando intimação ERJ	2022-05-26 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
368	Juntada Petição Autora em resposta	2023-05-15 00:00:00	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
369	Juntada Concordancia ERJ	2023-08-31 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
370	Decisão homologando	2023-11-28 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
371	Intimação ERJ Enviada	2023-07-31 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
372	Ação Distribuída	2023-02-15 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
373	EDs sobre sucumbencia e RPV	2023-12-04 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
374	Juntada Petição Autora pedindo homologação	2023-09-05 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
375	Decisão Determinando Intimação ERJ	2023-05-22 00:00:00	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
376	Juntada Petição Autora pedindo homologação	2023-09-05 00:00:00	35	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
377	Ação Distribuída	2023-02-15 00:00:00	35	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
378	Juntada Concordancia ERJ	2023-08-31 00:00:00	35	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
379	Decisão Determinando Intimação ERJ	2023-05-22 00:00:00	35	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
380	Conclusão	2023-11-01 00:00:00	35	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
381	Intimação ERJ Enviada	2023-07-31 00:00:00	35	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
382	Protocolada Petição pedindo expedição de RPV com valores	2023-08-14 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
384	Ação Distribuída	2023-02-16 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
385	Juntada Petição Autora pedindo homologação	2023-08-07 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
386	Intimação ERJ Enviada	2023-06-06 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
387	Juntada de petição pelo cartório	2023-10-30 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
388	Juntada Concordancia ERJ	2023-08-07 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
389	Decisão Determinando Intimação ERJ	2023-05-15 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
390	RPV Enviada	2023-11-23 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
391	Decisão Homologando Cálculos	2023-08-11 00:00:00	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
392	Intimação ERJ Enviada	2023-07-31 00:00:00	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
393	Juntada Petição Autora em resposta à Impugnação	2023-09-18 00:00:00	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
394	Decisão Determinando Intimação ERJ	2023-05-23 00:00:00	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
395	Ação Distribuída	2023-03-01 00:00:00	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
396	Determinado o envio ao Contador Judicial	2023-12-15 00:00:00	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
397	Juntada Impugnação ERJ (utilizou nível incorreto da Autora)	2023-09-13 00:00:00	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
398	Decisão determinando intimação do Estado	2023-05-23 00:00:00	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
399	Distribuída Ação	2023-03-01 00:00:00	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
400	Juntada Petição do Estado impugnando o valor - não considerou nível correto	2023-08-11 00:00:00	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
401	Envio de intimação ao Estado	2023-07-31 00:00:00	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
402	Petição Autora em resposta à Impugnação	2023-08-17 00:00:00	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
403	Autos com o Juiz para análise das petições	2023-11-01 00:00:00	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
404	Decisão determinando intimação ERJ	2023-05-23 00:00:00	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
405	Ação Distribuída	2023-03-02 00:00:00	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
406	Conclusão ao juiz para análise das petições	2023-11-01 00:00:00	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
407	ERJ concordou com Cálculos	2023-08-21 00:00:00	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
408	ERJ intimado	2023-07-31 00:00:00	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
409	Petição Autora pedindo homologação	2023-08-22 00:00:00	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
410	Conclusão ao juiz para análise das petições	2023-11-01 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
411	ERJ concordou com cálculos	2023-09-16 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
412	Decisão determinando intimação ERJ	2023-05-23 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
413	ERJ Intimado	2023-07-31 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
414	Petição com valores para RPV	2024-01-12 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
415	Petição Autora pedindo homologação	2023-09-18 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
416	Ação Distribuída	2023-03-02 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
417	Homologados cálculos Autora - determinado envio de RPV	2023-12-11 00:00:00	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
418	Com o juiz para análise	2023-12-18 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
419	Ação Distribuída	2023-03-03 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
420	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
421	Autos conclusos	2023-08-23 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
422	Autos conclusos	2023-12-11 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
423	Juntada Concordancia ERJ	2023-08-23 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
424	Intimação ERJ Enviada	2023-06-19 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
425	Petição com valores para RPV	2024-01-12 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
426	Juntada Petição Autora pedindo homologação	2023-08-23 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
427	Cálculos homologado	2024-01-11 00:00:00	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
428	Intimação ERJ Enviada	2023-06-19 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
429	Verificado Pagamento	2024-01-10 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
430	Protocolada Petição pedindo expedição de RPV com valores	2023-08-18 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
431	Juntada Concordancia ERJ	2023-08-15 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
432	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
433	Decisão Homologando Cálculos	2023-08-17 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
434	RPV Autora digitada	2023-12-07 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
435	RPVs enviadas	2023-12-12 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
436	Ação Distribuída	2023-03-03 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
437	Juntada Petição Autora pedindo homologação	2023-08-15 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
438	Juntada de Petição requerendo levantamento	2024-01-11 00:00:00	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
439	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
440	Autos conclusos	2023-08-23 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
441	Juntada petição Autora sobre valores para RPV	2023-12-29 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
442	Decisão homologando valor	2023-12-15 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
443	Juntada Concordancia ERJ	2023-08-23 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
444	Ação Distribuída	2023-03-10 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
445	Intimação ERJ Enviada	2023-06-19 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
446	Juntada Petição Autora pedindo homologação	2023-08-23 00:00:00	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
447	Intimação ERJ Enviada	2023-06-19 00:00:00	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
448	Ação Distribuída	2023-03-10 00:00:00	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
449	Juntada Impugnação ERJ	2023-08-17 00:00:00	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
450	Decisão Determinando Intimação ERJ	2023-05-17 00:00:00	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
451	Envio dos Autos para Contador	2023-08-21 00:00:00	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
452	Juntada Petição Autora em resposta à Impugnação	2023-08-17 00:00:00	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
453	Juntada Petição Autora pedindo homologação	2023-09-07 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
454	Cálculos Homologados 	2023-12-05 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
455	Autos Conclusos	2023-09-07 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
456	Juntada Concordancia ERJ	2023-09-07 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
457	Petição com valores para RPV	2024-01-12 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
458	Ação Distribuída	2023-03-15 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
459	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
460	Intimação ERJ Enviada	2023-07-13 00:00:00	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
461	Decisão - Dando desistencia - custas e sucumbencia ERJ	2023-11-24 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
462	Decisão Determinando Intimação ERJ	2023-05-24 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
463	Petição JG	2023-12-04 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
464	Intimação ERJ Enviada	2023-06-02 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
465	Protocolada Petição Autora pedindo desistência	2023-07-20 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
466	Ação Distribuída	2023-03-21 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
467	Protocolada Petição ERJ sobre litispendência	2023-06-27 00:00:00	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
468	Intimação ERJ Enviada	2023-09-12 00:00:00	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
469	Juntada Petição Autora pedindo homologação	2023-09-27 00:00:00	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
470	Juntada Concordancia ERJ	2023-09-22 00:00:00	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
471	Conclusão	2023-11-01 00:00:00	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
472	Decisão Determinando Intimação ERJ	2023-05-24 00:00:00	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
473	Ação Distribuída	2023-03-23 00:00:00	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
474	Juntada Petição Autor pedindo homologação	2023-10-04 00:00:00	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
475	Ação Distribuída	2023-03-22 00:00:00	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
476	Juntada Concordancia ERJ	2023-09-27 00:00:00	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
477	Conclusão	2023-11-01 00:00:00	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
478	Intimação ERJ Enviada	2023-09-12 00:00:00	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
479	Decisão Determinando Intimação ERJ	2023-05-24 00:00:00	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
480	Conclusão	2023-11-01 00:00:00	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
481	Decisão Determinando Intimação ERJ	2023-05-24 00:00:00	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
482	Ação Distribuída	2023-03-23 00:00:00	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
483	Juntada Petição Autor pedindo homologação	2023-10-17 00:00:00	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
484	Juntada Petição ERJ concordando	2023-10-09 00:00:00	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
485	Intimação ERJ Enviada	2023-09-12 00:00:00	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
486	Juntada Apelação Autora	2023-08-03 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
487	Juntada Petição Autora sobre listagem ERJ	2023-05-08 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
488	Apelação remetida ao TJRJ	2023-12-19 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
489	Ação distribuída	2023-02-15 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
490	ERJ intimado	2023-10-05 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
491	Determinada intimação do ERJ para contrarrazões	2023-09-29 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
492	Decisão determinando juntada de contracheques 2002	2023-04-03 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
493	Sentença extinguindo a ação	2023-06-30 00:00:00	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
494	Conclusão - com o juiz para análise	2023-11-01 00:00:00	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
495	Decisão Determinando Intimação ERJ	2023-05-22 00:00:00	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
496	Juntada Impugnação ERJ	2023-08-17 00:00:00	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
497	Ação Distribuída	2023-03-31 00:00:00	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
498	Intimação ERJ Enviada	2023-07-31 00:00:00	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
499	Juntada Petição Autora em resposta à Impugnação	2023-08-22 00:00:00	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
500	Juntada Petição ERJ informando Agravo (0032933-55.2022.8.19.0000)	2022-05-26 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
501	Decisão acolhendo EDs	2023-08-24 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
502	Ação Distribuída	2021-11-25 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
503	Juntada Petição Autora sobre manifestação Contador	2022-07-06 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
504	Pedido de parâmetros pelo Contador	2022-06-02 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
505	Juntada Impugnação ERJ	2022-01-10 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
506	Juntada Petição Autora em resposta à Impugnação	2022-01-11 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
507	Despacho - Renove-se a intimação do Contador Judicial, salientando que o mesmo foi intimado em 12/09, permanecendo inerte	2023-10-18 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
508	Ofício sobre trânsito do Agravo do ERJ - indeferido	2023-02-16 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
509	Juntado EDs Autora sobre informações de fls. 202	2023-01-25 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
510	Determinado novo envio ao Contador	2022-08-25 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
511	Decisão - deferida JG e intimação ERJ	2021-11-26 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
512	Remessa ao Contador	2023-10-20 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
513	Despacho utilizando informações completamente erradas	2023-01-11 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
514	Decisão determinando envio dos autos ao Contador	2022-03-18 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
515	Manifestação Contador de complexidade do Cálculo	2023-01-11 00:00:00	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
516	Envio de Mandado de intimação	2023-12-19 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
517	Juntada Petição Autora Sobre trânsito Agravo e prosseguimento	2023-05-15 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
518	Ação Distribuída	2021-11-25 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
519	Decisão - deferida JG e intimação ERJ	2021-12-02 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
520	Decisão homologando cálculos e determinando intimação para pagamento	2023-12-18 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
521	Juntada Petição Estado sobre Mandado incorreto	2023-12-25 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
522	Juntada Petição Autora informando valores corretos	2023-12-29 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
523	Juntada Petição Autora em resposta à Impugnação	2022-01-26 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
524	Juntada Petição Autora Sobre Trânsito Sentença	2023-01-27 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
525	Juntada Agravo ERJ	2022-08-04 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
526	Despacho - Informem as partes quanto ao trânsito em julgado do acórdão	2023-02-27 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
527	Processo com juiz para análise	2023-11-24 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
528	Juntada Petição Autora Sobre Agravo e prosseguimento	2023-03-13 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
529	Juntada Impugnação ERJ	2022-01-18 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
530	Sentença de Procedencia	2022-05-10 00:00:00	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
531	Protocolada Petição pedindo homologação	2023-11-01 00:00:00	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
532	Decisão Determinando Intimação ERJ	2023-05-25 00:00:00	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
533	Juntada Petição ERJ - concordando com Cálculo Autora	2023-10-20 00:00:00	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
534	Ação Distribuída	2023-04-04 00:00:00	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
535	Conclusão - com juiz para análise	2023-11-01 00:00:00	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
536	Intimação ERJ Enviada	2023-09-20 00:00:00	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
609	Ação Distribuída	2021-10-28 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
537	Despacho - Ao ERJ para que traga aos autos planilha de cálculos da impugnação	2023-05-22 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
538	Petição ERJ com Cálculos	2023-12-04 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
539	Enviada Intimação ERJ	2023-11-30 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
540	Publicação Decisão	2023-06-20 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
541	Ação Distribuída	2022-04-28 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
542	Decisão Determinando Intimação ERJ	2022-05-19 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
543	Juntada Impugnação ERJ	2023-01-23 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
544	Petição Autora concordando e requerendo homologação	2024-01-16 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
545	Juntada Petição Autora em resposta à Impugnação	2023-04-16 00:00:00	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
546	Autos Conclusos	2023-10-25 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
547	Ação Distribuída	2021-11-05 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
548	Manifestação do Contador sobre necessidade de parâmetros	2022-10-10 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
549	Despacho ao Contador	2022-08-08 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
550	Remessa ao contador	2023-11-07 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
551	Juntada Impugnação ERJ	2022-06-17 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
552	Ação Distribuída	2021-12-07 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
553	Despacho Às partes	2022-10-10 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
554	Juntada Petição Autora pedindo prosseguimento Processo	2023-10-04 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
555	Despacho pedindo documentos JG	2021-12-07 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
556	Juntada Petição Autora em resposta à Impugnação	2022-06-22 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
557	Juntada Petição com documentos JG	2022-01-26 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
558	Juntada Petição ERJ informando Agravo	2023-07-26 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
559	Manifestação MP - sem interesse	2022-07-17 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
560	Decisão com Parâmetros	2023-04-10 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
561	Juntada Petição ERJ sobre Parâmetros	2022-11-12 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
562	Juntada Petição Autora pedindo reconsideração JG	2022-05-23 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
563	Decisão indeferindo JG	2022-05-09 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
564	Juntada Petição Autora sobre Parâmetros	2022-11-09 00:00:00	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
565	Juntada Petição Autora com Cálculos	2022-10-20 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
566	Decisão pedindo novos calculos e negando impugnação	2022-09-27 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
567	Ação Distribuída	2021-10-05 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
568	Juntada Petição Autora concordando com cálculos	2022-08-26 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
569	Juntada de cálculos Contador (R$ 37.563,00)	2022-08-23 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
570	Despacho - Às partes sobre o cálculo de fls. 144	2022-08-23 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
571	Despacho com parâmetros	2022-07-19 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
572	Juntada Petição Autora em resposta à Impugnação	2022-01-07 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
573	Juntada Petição ERJ informando Agravo (0091664-44.2022.8.19.0000)	2022-11-28 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
574	Juntada Petição ERJ reiterando impugnação	2022-08-26 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
575	Manifestação Contador solicitando parâmetros	2022-07-19 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
576	Despacho - Ao contador	2022-05-02 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
577	Juntada Impugnação ERJ	2022-01-04 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
578	Decisão - Deferida JG | Honorarios 10% | Intimação ERJ	2021-10-07 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
579	Certificado Efeito Suspensivo em RESP	2023-08-31 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
580	Despacho solicitando ao cartório que certifique sobre efeito suspensivo	2023-05-30 00:00:00	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
581	Despacho - Considerando o certificado à fl.257, aguarde-se o trânsito em julgado do agravo de instrumento interposto.	2023-10-10 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
582	Despacho sobre duplicidade	2021-10-13 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
583	Decisão negando impugnação	2022-12-01 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
584	Juntada Petição ERJ reiterando impugnação	2022-10-10 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
585	Juntada Petição ERJ informando Agravo (0005479-66.2023.8.19.0000)	2023-02-01 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
586	Despacho determinando que se aguarde julgamento do Agravo	2023-05-18 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
587	Despacho - ao contador	2022-05-02 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
588	Decisão - Deferida JG | Determinada intimação ERJ	2021-10-22 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
589	Despacho - Às partes sobre os cálculos	2022-09-27 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
590	Ação Distribuída	2021-10-07 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
591	Juntada Petição Autora informando duas matrículas	2021-10-13 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
592	Juntada Petição Autora em resposta à Impugnação	2022-03-03 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
593	Autos conclusos	2023-09-20 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
594	Despacho determinando parâmetros	2022-08-19 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
595	Juntada Cálculos contador (R$ 33.531,90)	2022-09-27 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
596	Juntada Petição Autora concordando com cálculos	2022-10-11 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
597	Juntada Petição Autora com PLanilha atualizada	2022-12-29 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
598	Manifestação contador - pedido de parâmetros	2022-08-19 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
599	Juntada Impugnação ERJ	2022-02-23 00:00:00	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
600	Juntada Petição Autora em resposta à Impugnação	2022-01-07 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
601	Decisão - Deferida JG | Determinada Intimação ERJ	2021-11-03 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
602	Certificado não ocorrencia de trânsito no Agravo	2023-08-11 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
603	Despacho - Às partes sobre o cálculo	2022-07-25 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
604	Juntada Petição ERJ reiterando impugnação	2022-08-02 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
605	Juntada Petição ERJ informando Agravo (0076372-19.2022.8.19.0000)	2022-09-19 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
606	Juntada Petição Autora com Cálculos atualizados	2022-09-19 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
607	Juntada Cálculos (R$ 24.525,77)	2022-07-25 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
608	Decisão rejeitando impugnação	2022-08-26 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
610	Certificado não ocorrencia de trânsito no Agravo	2023-02-17 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
611	Juntada Impugnação ERJ	2021-12-15 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
612	Juntada Petição Autora concordando com cálculos	2022-08-01 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
613	Despacho - ao contador	2022-05-02 00:00:00	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
614	Certificada a intimação do Estado e que não teria passado o Prazo	2023-09-04 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
615	Petição Autora pedindo prosseguimento - não houve suspensão	2022-09-27 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
616	Petição da Autora chamando o feito a ordem - já há manifestação da Autora - requerendo homologação dos cálculos	2023-12-15 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
617	Decisão determinando manifestação Autora	2023-04-25 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
618	Juntada de Nova planilha - Autora	2022-06-22 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
619	Petição Autora reiterando inexistência de suspensão pedindo prosseguimento	2023-03-20 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
620	Despacho - Fls.281 - Certifique-se o decurso do prazo para manifestação	2023-10-19 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
621	Decisão intimando ERJ	2023-04-05 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
622	Distribuída Ação	2021-11-01 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
623	Certificado sobre prazos	2023-12-11 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
624	Decisão deferindo JG e determinando intimação ERJ	2021-12-02 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
625	Juntada resposta Autora	2022-03-03 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
626	Petição Autora informando já haver desconto previdenciário nos cálculos	2023-05-04 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
627	Decisão determinando Cálculos do Contador	2023-05-25 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
628	Despacho requerendo certificação de manifestação ERJ	2023-08-08 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
629	Petição ERJ reiterando impugnação	2023-04-15 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
630	Despacho determinando intimação da Autora novamente	2023-12-14 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
631	Juntada Petição Autora concordando com cálculos (cálculos seguindo parâmetros utilizados pela Autora))	2023-07-31 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
632	Juntada impugnação ERJ	2022-02-04 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
633	Juntada de Cálculos dos contador 	2023-07-27 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
634	Decisão Saneadora e solicitando nova planilha	2022-06-06 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
635	Decisão determinando juntada de IR	2021-11-04 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
636	Juntada Petição informando Agravo ERJ (068053-62.2022.8.19.0000)	2022-09-14 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
637	Juntada de Petição com IR	2021-11-23 00:00:00	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
638	Resposta Contador requerendo definição de parâmetros	2022-07-19 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
639	Juntada Petição Autora requerendo prosseguimento da Ação	2023-04-25 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
640	Juntada Impugnação ERJ	2022-01-10 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
641	Juntada Petição ERJ reiterando impugnação	2022-08-28 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
642	Decisão determinando Intimação do ERJ	2023-09-04 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
643	Juntada Petição ERJ informando Agravo (0091161-23.2022.8.19.0000)	2022-11-27 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
644	Juntada Petição autora com cálculos atualizados	2022-10-20 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
645	Ação Distribuída	2021-11-05 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
646	Juntada Petição Autora em resposta à Impugnação	2022-01-11 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
647	Despacho Ao Contador	2022-05-02 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
648	Despacho determinando parâmetros	2022-07-19 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
649	Decisão - Deferida JG | Determinada Intimação ERJ	2021-11-09 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
650	Juntada de Cálculos fls. 138	2022-08-23 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
651	Decisão determinando novos cálculos e aplicação de desconto previdenciário	2022-09-27 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
652	Juntada Petição ERJ com Cópia de ARESP Sobrestado	2023-09-21 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
653	Juntada Petição Autora concordando com cálculos	2022-08-26 00:00:00	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
654	Juntada Petição ERJ - Reiterando impugnação	2023-06-25 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
655	Determinada Suspensão do Processo	2023-12-12 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
656	Decisão - livre distribuição	2021-11-09 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
657	Decisão indeferindo JG	2022-02-04 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
658	Juntada Petição Autora com pagamento Taxa	2022-05-19 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
659	Juntada Petição Autora pedindo a Reconsideração da JG	2022-03-03 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
660	Juntada Petição Autora em resposta à Impugnação	2023-02-23 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
661	Despacho - Ao impugnante	2023-05-03 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
662	Ação Distribuída	2021-11-05 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
663	Decisão reconsiderando quanto à Isenção - determinando pagamento da Taxa	2022-03-22 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
664	Juntada Impugnação ERJ	2023-02-17 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
665	Determinada intimação do ERJ	2022-11-21 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
666	Juntada Petição Autora requerendo prosseguimento	2023-06-28 00:00:00	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
667	Ação Distribuída	2021-12-01 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
668	Decisão - Declínio Competência (JEC)	2021-12-03 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
669	Juntada de cálculos (R$ 22.262,85)	2022-07-25 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
670	Decisão - acolhida reconsideranção | Deferida JG | Determinada Intimação ERJ	2021-12-15 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
671	Juntada Petição Autora em resposta à Impugnação	2022-03-03 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
672	Juntada Contrarrazões ERJ	2022-11-27 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
673	Juntada Petição Autora Concordando com Cálculos	2022-08-01 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
674	Despacho ao Embargado	2022-10-19 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
675	Juntada Petição ERJ requerendo Suspensão	2023-10-01 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
676	Decisão rejeitando EDs	2023-05-30 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
677	Despacho - Às partes sobre os cálculos	2022-07-25 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
678	Juntada Petição ERJ Informando Agravo (0077218-36.2022.8.19.0000)	2022-10-06 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
679	Juntada Impugnação ERJ	2022-02-17 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
680	Certificado Resp Suspenso	2023-09-11 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
681	Juntada Petição Autora requerendo homologação	2023-09-27 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
682	Juntada EDs Autora	2022-09-19 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
683	Juntada Petição ERJ reiterando impugnação	2022-08-02 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
684	Decisão contra impugnação	2022-08-26 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
685	Despacho - Ao contador	2022-05-02 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
686	Despacho às Partes	2023-09-11 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
687	Juntada Petição Autora Requerendo reconsideração	2021-12-06 00:00:00	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
688	Ação Distribuída	2023-07-26 00:00:00	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
689	PEtição ERJ concordando com Cálculos	2023-11-24 00:00:00	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
690	Decisão Determinando Intimação ERJ	2023-08-16 00:00:00	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
691	Publicada Decisão Determinando Intimação ERJ	2023-08-18 00:00:00	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
692	Petição Autora pedindo homologação	2023-11-29 00:00:00	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
693	Enviada a intimação ao ERJ	2023-11-06 00:00:00	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
694	Decisão Determinando Intimação ERJ	2023-08-15 00:00:00	65	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
695	ERJ peticionou concordando com valor	2023-11-07 00:00:00	65	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
696	Petição Autora pedindo homologação	2023-11-13 00:00:00	65	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
697	Enviada intimação ao ERJ	2023-10-30 00:00:00	65	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
698	Ação Distribuída	2023-08-02 00:00:00	65	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
699	Decisão Determinando Intimação ERJ	2023-08-15 00:00:00	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
700	Intimação enviada	2023-11-06 00:00:00	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
701	Juntada Petição Autora solicitando homologação	2023-12-15 00:00:00	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
702	Ação Distribuída	2023-08-07 00:00:00	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
703	Juntada Petição do Estado - concordância	2023-12-10 00:00:00	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
704	Deferida JG e determinada Intimação do Estado	2024-01-16 00:00:00	67	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
705	Ação redistribuída - em razão de reconhecimento de competência de outra vara	2023-09-01 00:00:00	67	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
706	Conclusão com o juiz para análise	2023-11-28 00:00:00	67	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
707	Homologada Desistência	2023-12-15 00:00:00	68	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
708	Conclusão sobre desistência	2023-10-04 00:00:00	68	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
709	Juntada Petição Autora com valores para RPV	2024-01-10 00:00:00	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
710	Decisão deferindo JG e determinando intimação ERJ	2023-10-19 00:00:00	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
711	Junatada concordância do Estado	2023-12-20 00:00:00	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
712	Cálculos homologado	2024-01-08 00:00:00	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
713	Ação Distribuída	2023-09-21 00:00:00	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
714	Conclusão	2023-12-04 00:00:00	70	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
715	Ação distribuída	2023-10-05 00:00:00	70	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
716	Enviada Intimação	2024-01-10 00:00:00	71	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
717	Decisão concedendo JG e determinando intimação do Estado	2023-12-28 00:00:00	71	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
718	Conclusão	2023-11-23 00:00:00	71	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
719	Ação distribuída	2023-10-05 00:00:00	71	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
720	Juntada Petição Autora reiterando RPV	2024-01-10 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
721	Decisão Determinando Intimação ERJ	2023-04-04 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
722	Ação Distribuída	2022-11-03 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
723	Decisão Homologando Cálculos	2023-06-22 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
724	Elaborado ofício para precatório	2023-08-27 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
725	Juntada Petição ERJ concordanco com Ofício	2023-10-02 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
726	Juntada Concordancia ERJ	2023-06-20 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
727	Enviado E-mail pedindo andamento	2023-12-06 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
728	Intimação ERJ Enviada	2023-04-11 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
729	Juntada Petição Autora pedindo expedição de RPV com valores	2023-08-27 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
730	Juntada Petição Autora pedindo homologação	2023-06-20 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
731	Juntada Petição reiterando renúncia e pedindo RPV	2023-10-02 00:00:00	72	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
732	Decisão Determinando Intimação ERJ	2023-05-18 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
733	Protocolada Petição pedindo expedição de RPV com valores	2023-09-27 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
734	Decisão Homologando Cálculos	2023-09-26 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
735	Ação Distribuída	2022-11-09 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
736	Juntada Petição Autora pedindo homologação	2023-09-02 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
737	Intimação ERJ Enviada	2023-07-06 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
738	Juntada Concordancia ERJ	2023-09-02 00:00:00	73	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
739	Juntada Petição ERJ reiterando impugnação	2023-04-27 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
740	Juntada Petição ERJ discondando contador	2023-08-09 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
741	Ação Distribuída	2021-12-03 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
742	Autos Conclusos	2023-08-10 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
743	Juntada Petição Autora concordando com contador e pedindo homologação	2023-08-09 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
744	Envio dos autos ao Contador	2023-04-28 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
745	Ato ordinatório ao Exequente sobre manifestação ERJ	2023-08-09 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
746	Decisão Determinando Intimação ERJ	2022-04-01 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
747	Decisão - remetam-se os autos à Central de Cálculos	2023-11-30 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
748	Intimação ERJ Enviada	2022-04-05 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
749	Remetido ao contador	2023-12-06 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
750	Juntada Petição Autora em resposta à Impugnação	2022-11-22 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
751	Juntada de Cálculos (R$ 30.196,45)	2023-05-22 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
752	Juntada Petição Autora chamando o feito à Ordem reiterando petição anterior	2023-08-10 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
753	Juntada Impugnação ERJ	2022-11-22 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
754	Juntada Petição Autora Reiterando resposta à Impugnação	2023-04-28 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
755	Enviada nova intimação ao ERJ	2022-11-22 00:00:00	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
756	ERJ concordou com cálculos	2023-07-30 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
757	Distribuída Ação	2022-12-01 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
758	Petição Autora reiterando valores	2023-11-16 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
759	Decisão homologando cálculos	2023-09-26 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
760	Decisão determinando intimação ERJ	2023-05-18 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
761	Juntada Petição com renúncia e pedido RPV	2023-10-27 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
762	Petição Autora explicando cálculos corretos - RPV correta	2023-11-28 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
763	Petição Autora pedindo homologação	2023-07-31 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
764	Intimação enviada ao ERJ	2023-07-06 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
765	Petição ERJ informando que valores estariam incorretos na RPV	2023-11-23 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
766	RPV Enviada 	2023-11-13 00:00:00	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
767	Distribuído	2023-11-24 00:00:00	76	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
768	Conclusão de petições iniciais	2023-12-15 00:00:00	76	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
769	Remessa Ag. Cls.	2023-11-29 00:00:00	76	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
770	Decisão - deferida JG e pedido de contracheques 2002	2023-08-22 00:00:00	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
771	Juntada Resposta Autora	2023-12-01 00:00:00	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
772	Juntada de contracheques de 2002	2023-09-11 00:00:00	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
773	Ação distribuída	2023-08-21 00:00:00	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
774	Juntada Impugnação ERJ	2023-11-24 00:00:00	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
775	Ação Redistribuída ao Órgão Especial - 0100885-17.2023.8.19.0000	2023-12-07 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
776	Decisão - Acolhida alegação do Estado para redistribuição	2023-08-23 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
777	Juntada Petição Autora em Resposta	2023-05-08 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
778	Juntada Impugnação ERJ	2023-05-04 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
779	Ação Distribuída	2023-04-11 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
780	Certificados documentos para análise do juiz	2023-12-21 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
781	Decisão - Deferida JG | Determinada Intimação ERJ	2023-04-13 00:00:00	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
782	Juntada Contrarrazões EDs Autora	2023-03-01 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
783	Juntada Petição Autora em Provas	2022-05-18 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
784	Manifestação Mp - sem interesse	2022-11-09 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
785	Juntada Petição ERJ em Provas	2022-05-18 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
786	Decisão declinando para 8ª Vara de Fazenda Pública	2022-05-19 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
787	Juntada EDs ERJ	2023-02-14 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
788	Ação Distribuída	2021-10-05 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
789	Juntada Contrarrazões ERJ	2022-11-14 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
790	Decisão de Sobrestamento (Tema 1033 STJ)	2023-06-13 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
791	Interposto RESP ERJ	2023-05-25 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
792	Juntada Petição Autora em resposta à Impugnação	2021-11-23 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
793	Despacho deferindo JG e determinando intimação ERJ	2021-10-08 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
794	Juntada Impugnação ERJ	2021-11-22 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
795	Sentença reconhecendo Prescrição	2022-10-31 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
796	Despacho em Provas	2022-03-14 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
797	Julgamento - Provido Recurso Autora	2023-02-07 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
798	Manifestação MP - sem interesse	2022-03-09 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
799	Juntada Apelação Autora	2022-11-04 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
800	Despacho ao MP	2022-01-26 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
801	Negados EDs ERJ	2023-04-18 00:00:00	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
802	Juntada Impugnação ERJ	2022-03-04 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
803	Despacho sobre litispendência	2021-10-08 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
804	Juntada Petição Autora informando não haver litispendência	2021-10-14 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
805	Juntada Petição Autora em resposta à Impugnação	2022-03-09 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
806	Sentença determinando Prescrição	2022-10-31 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
807	Juntada Contrarrazões ERJ	2022-11-10 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
808	Declinada Competência para Sexta Câmara de Direito Público	2023-07-20 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
809	Decisão - declino da competência em favor da 8ª Vara de Fazenda Pública	2022-05-16 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
810	Manifestação MP - Sem interesse	2022-11-09 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
811	Juntada Apelação Autora	2022-11-04 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
812	Recebida Apelação em duplo efeito	2023-09-29 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
813	Concluso ao relator	2023-12-15 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
814	Ação Distribuída	2021-10-07 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
815	Decisão reconsiderando anterior	2021-11-04 00:00:00	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
816	Intimação sobre calculos	2023-08-10 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
817	Juntada de cálculos do contador (R$ 27.395,21)	2023-08-04 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
818	Autos conclusos	2023-09-27 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
819	Juntada EDs Autora sobre erro material	2023-10-24 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
820	Juntada Petição Autora concordando com cálculos	2023-08-14 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
821	Juntada Impugnação ERJ	2023-05-11 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
822	Despacho: Ao contador	2023-07-11 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
823	Manifestação MP - Sem interesse	2023-09-25 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
824	Determinada a intimação do Estado sobre Embargos	2023-12-12 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
825	Sentença procedente 	2023-10-19 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
826	Despacho: Ao MP	2023-09-22 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
827	Juntada Petição Autora em resposta à Impugnação ERJ	2023-05-15 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
828	Ação Distribuída	2023-01-09 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
829	Decisão - Concedida JG | Intimação ERJ	2023-02-10 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
830	Petição ERJ reiterando impugnação	2023-08-17 00:00:00	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
831	Juntada Petição Autora informando Agravo ( 0012172-66.2023.8.19.0000)	2023-02-26 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
832	Juntada EDs ERJ	2023-10-09 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
833	Decisão - Deferida JG | Negada Liminar	2023-02-10 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
834	Intimação - Em Provas	2023-04-12 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
835	Ação Distribuída	2023-01-09 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
836	Juntada Réplica	2023-03-30 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
837	Sentença - Ação julgada procedente	2023-09-22 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
838	Juntada Petição Autora em Provas	2023-04-14 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
839	Despacho: Ao MP	2023-07-20 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
840	Juntada Petição ERJ em alegações finais	2023-08-23 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
841	Decisão reconhecendo suspensão quanto a liminar apenas	2023-11-24 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
842	Juntada Petição Autora em alegações finais	2023-08-22 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
843	Juntada Contestação	2023-03-29 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
844	Expedia intimação sobre decisão (Ag. 21/02)	2023-11-30 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
845	Juntada Contrarrazões Autora	2023-10-11 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
846	Despacho: Em alegações Finais	2023-08-17 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
847	Manifestação MP - Sem interesse	2023-07-25 00:00:00	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
848	Despacho: Ao Contador	2023-08-29 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
849	Manifestação MP - Sem interesse	2023-08-15 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
850	Juntada de Cálculos Contador	2023-10-05 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
851	Juntada de Petição autora com informações ao Contador	2023-09-22 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
852	Manifestação do contador por informações	2023-09-18 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
853	Juntada Impugnação ERJ	2023-07-19 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
854	Juntada Petição Autora em resposta à Impugnação ERJ	2023-07-24 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
855	Processo com juiz para análise	2023-11-21 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
856	Despacho: Ao MP	2023-08-08 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
857	Juntada Petição ERJ contrário	2023-10-22 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
858	Juntada Petição Autora - concordancia com calculos contador	2023-10-17 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
859	Decisão - Deferida JG	2023-02-10 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
860	Ação Distribuída	2023-01-10 00:00:00	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
861	Manifestação MP - Sem interesse	2023-08-15 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
862	Petição Autora sobre falta de manifestação ERJ	2023-07-17 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
863	Decisão - Concedida JG | Negada Liminar	2023-02-10 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
864	Juntada Contestação	2023-03-31 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
865	Despacho ao MP	2023-08-04 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
866	Intimação - em provas	2023-05-17 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
867	Processo com juiz para análise	2023-12-19 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
868	Despacho determinando alegações finais	2023-08-28 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
869	Juntada Petição Autora - em provas	2023-05-23 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
870	Petição Autora informando Agravo (0012173-51.2023.8.19.0000)	2023-02-26 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
871	Juntada Petição Autora - Alegações Finais	2023-09-04 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
872	Ação Distribuída	2023-01-10 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
873	Juntada Réplica	2023-04-03 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
874	Juntada Petição ERJ - Alegações Finais	2023-10-11 00:00:00	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
875	Despacho pedindo Carta de Sentença	2023-01-31 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
876	Decisão declarando incompetência	2023-01-27 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
877	Despacho concedendo JG e pedindo Carta de Sentença	2023-04-18 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
878	Juntada Impugnação ERJ	2023-08-31 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
879	Juntada Petição Autora em resposta à Impugnação ERJ	2023-09-05 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
880	Despacho Ao MP	2023-09-15 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
881	Manifestação MP - Sem interesse	2023-09-18 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
882	Despacho determinando Citação ERJ	2023-06-27 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
883	Juntada Petição Autora sobre carta de sentença	2023-02-13 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
884	Decisão negando preliminares do Estado e solicitando informações para cálculos	2023-11-30 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
885	Ação Distribuída	2023-01-23 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
886	Juntada Petição Autora com documentos e informações para cálculos	2023-12-01 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
887	Juntada Petição Autora sobre carta de sentença	2023-04-26 00:00:00	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
888	Petição Autora sobre denecessidade de mais provas e pedindo julgamento	2023-12-13 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
889	Juntada Réplica	0203-05-23 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
890	Certifico que decorreu o prazo legal sem manifestação da parte ré quanto à decisão de fls. 77467659	2023-11-21 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
891	Juntada Petição Autora informando Agravo (033319-51.2023.8.19.0000)	2023-05-09 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
892	Despacho - Intime-se o réu para ciência e eventual manifestação no prazo de quinze dias.	2023-10-01 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
893	Juntada EDs Autora	2023-04-04 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
894	Juntada Petição Autora - Emenda	2023-07-14 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
895	Juntada Contestação	2023-04-21 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
896	Decisão de incompetência (Juizado)	2023-04-03 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
897	Juntada Petição Autora Informando Agravo (016362-72.2023.8.19.0000)	2023-03-14 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
898	Ação Distribuída	2023-01-23 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
899	Decisão não acolhendo EDs	2023-04-28 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
900	Despacho: Digam as partes se existem outras provas a produzir no prazo de 10 dias. 	2023-12-04 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
901	Declarada incompetência - distribuído em alcantara	2023-01-27 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
902	Decisão negando novamente EDs	2023-05-25 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
903	Decisão reconsiderando Decisão de declínio mas pedindo apuração do valor	2023-07-10 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
904	Decisão - Deferida JG | Indeferida Liminar	2023-03-02 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
905	Decisão - Recebimento de Emenda	2023-09-18 00:00:00	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
906	Despacho mantendo decisão Agravada (Agravo ERJ)	2023-06-02 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
907	Juntado Ofício do TJRJ com Decisão Sobre competência da atual Vara 	2023-11-10 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
908	Juntada Contestação	2023-05-18 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
909	Juntada Réplica	2023-05-23 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
910	Ação Distribuída	2023-01-23 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
911	Decisão da nova Vara - Declarando incompetência ao Juizado	2023-02-06 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
912	Decisão declarando incompetência	2023-01-30 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
913	Juntada Petição Autora informando Agravo (009649-81.2023.8.19.0000)	2023-02-14 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
914	Decisão não acolhendo EDs	2023-02-10 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
915	Decisão reformando decisão em razão de Agravo | Deferida Liminar	2023-04-04 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
916	Juntada EDs Autora	2023-02-07 00:00:00	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
917	Ação Distribuída	2023-01-23 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
918	Despacho pedindo Carta de sentença	2023-01-25 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
919	Juntada Petição Autora sobre carta de sentença	2023-03-02 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
920	Despacho ao MP	2023-09-15 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
921	Manifestação MP - Sem interesse	2023-09-19 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
922	Juntada Petição Autora em resposta à Impugnação ERJ	2023-09-05 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
923	Despacho deferindo JG e determinando intimação ERJ	2023-05-17 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
924	Juntada Impugnação ERJ	2023-08-29 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
925	Decisão negando impugnação do Estado	2023-11-30 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
926	Juntada Petição Autora sobre erro cartorário sobre intimação	2023-07-17 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
927	Petição Autora juntando documentos para cálculo	2023-12-02 00:00:00	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
928	Juntada impugnação ERJ	2023-02-20 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
929	Juntada Petição ERJ informando Agravo (0078152-57.2023.8.19.0000)	2023-09-28 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
930	- Decisão Monocrática em Agravo negando provimento -	2023-10-26 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
931	Juntada Cálculos Contador 	2023-12-04 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
932	Juntada Petição Autora concordando com cálculos	2023-12-05 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
933	Despacho remetendo os Autos ao Contador	2023-07-13 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
934	Ação distribuída	2023-02-07 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
935	Decisão - Concedida JG e intimação ERJ	2023-02-09 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
936	Juntada Resposta Autora à impugnação	2023-02-27 00:00:00	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
937	Juntada Réplica	2023-05-08 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
938	Juntada Petição Autora informando Agravo (0815762-15.2023.8.19.0001)	2023-02-26 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
939	Decisão Suspendendo Ação - MS sobre ação coletiva	2023-09-29 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
940	Juntada Contestação	2023-05-01 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
941	Decisão - Concedida JG | Não concedida Liminar	2023-02-09 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
942	Juntada EDs Autora sobre suspensão	2023-10-09 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
943	Ato Ordinatório - Sobre provas	2023-07-20 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
944	Juntada Petição Autora Sobre Provas	2023-07-24 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
945	Ação Distribuída	2023-02-07 00:00:00	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
946	Manifestação MP - Sem interesse	2023-08-03 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
947	Juntada Apelação ERJ	2023-09-22 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
948	Juntada Réplica	2022-12-30 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
949	Juntada Petição Autora - em provas	2023-03-15 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
950	Decisão - Concedida JG | Negada Liminar	2022-08-29 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
951	Juntada Petição Autora informando Agravo (0067863-02.2022.8.19.0000)	2022-09-02 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
952	Autos Remetidos para TJRJ	2023-10-16 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
953	Sentença - Julgada procedente a Ação	2023-08-11 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
954	Juntada Contrarrazões Autora	2023-09-27 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
955	Juntada Contestação	2022-10-18 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
956	Ação Distribuída	2022-08-08 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
957	Despacho ao MP	2023-07-14 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
958	Intimação - em provas	2023-03-13 00:00:00	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
959	Juntada EDs Autor sobre contradição	2023-10-17 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
960	Decisão rejeitando EDs Autor (aguardando intimação para Agravar)	2023-12-11 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
961	Juntada Resposta à Impugnação do ERJ	2023-06-15 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
962	Ação distribuída	2023-03-23 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
963	Juntada impugnação ERJ	2023-06-13 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
964	Despacho determinando intimação ERJ	2023-04-04 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
965	Juntada de manifestação MP - Sem interesse	2023-07-25 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
966	Decisão acolhendo excesso e não homologando qualquer valor 	2023-10-10 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
967	Despacho ao MP	2023-07-20 00:00:00	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
968	Decisão Julgando impugnação procedente em partes - Decisão contraditória	2023-10-06 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
969	Juntada impugnação ERJ	2023-06-05 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
970	Decisão rejeitando EDs Autor (aguardando intimação para Agravar)	2023-12-11 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
971	Ação Distribuída	2023-03-23 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
972	Manifestação MP - sem interesse	2023-07-25 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
973	Despacho ao MP	2023-07-20 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
974	Juntada Resposta à Impugnação ERJ	2023-06-06 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
975	Juntada EDs Autor - sobre contradição	2023-10-16 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
976	Despacho deferindo JG e determinando intimação ERJ	2023-04-04 00:00:00	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1048	Juntada Apelação ERJ	2023-07-21 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
977	Registrada Reclamação na ouvidoria sobre não envio de citação	2024-01-10 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
978	Decisão - Não concedida liminar do Piso e determinando a citação do Estado	2023-06-18 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
979	Enviado E-mail pedindo envio de Citação	2023-08-02 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
980	Juntada de Petição Autora informando Agravo sobre liminar (0052569-70.2023.8.19.0000)	2023-07-05 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
981	Decisão - concedida JG | Determinada liquidação do valor pedido	2023-03-06 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
982	Enviado E-mail pedindo envio de Citação	2023-11-07 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
983	Solicitado envio de citação em atendimento virtual	2023-09-05 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
984	Juntada de Petição Autora sobre valores - informando sobre necessidade de liquidação do valor ao final da ação	2023-03-10 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
985	Solicitado envio de citação em atendimento virtual	2023-10-04 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
986	Distribuída Ação	2023-02-27 00:00:00	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
987	Distribuída Ação	2023-02-27 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
988	Despacho determinando apresentação de provas (se houver)	2023-07-07 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
989	Juntada de Resposta do ERJ	2023-04-02 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
990	Juntada Petição ERJ sobre denecessidade de audiência - não falou sobre provas 	2023-11-04 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
991	- Enviada intimação sobre despacho - após quase 3 meses pedindo o envio toda semana -	2023-10-26 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
992	Juntada Petição Autora sobre provas (sem necessidade - pedindo julgamento)	2023-08-07 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
993	Decisão - Deferida JG | Deteminada intimação ERJ	2023-03-06 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
994	Juntada Petição Autora em resposta ao Estado	2023-04-03 00:00:00	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
995	Juntada de ciência do MP	2023-12-19 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
996	Decisão - Deferida JG | Indeferida Liminar	2022-12-15 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
997	Manifestação MP - Sem interesse	2023-08-15 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
998	Enviada intimação sobre sentença	2023-12-18 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
999	Intimação sobre provas	2023-03-15 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1000	Juntada Petição Autora - inexistência de litispendência	2022-09-28 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1001	Despacho sobre litispendência	2022-09-27 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1002	Petição informando cumprimento de Tutela	2023-07-13 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1003	Juntada PEtição ERJ - Alegações Finais	2023-10-05 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1004	Juntada Réplica	2023-03-09 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1005	Despacho - alegações Finais	2023-08-29 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1006	Sentença de Procedência	2023-12-18 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1007	Despacho - ao MP	2023-08-04 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1008	Juntada Petição Autora - Alegações Finais	2023-09-04 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1009	Juntada Contestação	2023-03-05 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1010	Ação Distribuída	2022-09-16 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1011	Juntada Petição Autora Sobre Provas	2023-03-20 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1012	Juntada Petição Autora informando Agravo (0003154-21.2023.8.19.0000)	2023-01-24 00:00:00	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1013	Juntada Contestação	2022-12-12 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1014	Juntada Réplica	2022-12-30 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1015	Determinação para as partes se manifestarem em provas	2022-11-29 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1016	Despacho ao MP	2023-07-14 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1017	Juntada de Petição Autora sobre denecessidade de aguardar Agravo	2023-04-03 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1018	Manifestação do MP - Sem interesse - novamente	2023-07-17 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1019	Juntada de Petição Autora informando Agravo (0003207-02.2023.8.19.0000)	2023-01-24 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1020	MP Ciente	2023-08-15 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1021	Juntada Contrarrazões	2023-10-04 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1022	Ação Distribuída	2022-09-16 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1023	Despacho ao MP	2023-01-31 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1024	Decisão - Deferida JG | Indeferida Liminar	2022-09-27 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1025	Despacho determinando aguardar o Agravo	2023-03-31 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1026	Juntada Petição Autora pedindo julgamento	2022-11-29 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1027	Juntada Petição Autora em provas	2022-12-01 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1028	Autos remetidos ao TJRJ	2023-10-11 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1029	Sentença Julgando Procedente a Ação	2023-08-11 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1030	Certificado que ERJ não apresentou Contestação	2022-11-23 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1031	Manifestação MP - sem interesse	2023-02-02 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1032	Juntada de Apelação ERJ	2023-09-25 00:00:00	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1033	Juntada manifestação MP - sem interesse	2022-10-10 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1034	Autos conclusos	2023-09-11 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1035	Juntada Contestação	2023-02-01 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1036	Juntada Petição Autora informando Agravo (0090467-54.2022.8.19.0000)	2022-11-22 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1037	Ação distribuída	2022-07-27 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1038	Juntada Petição ERJ sobre provas	2023-07-24 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1039	Decisão - Deferida JG 	2022-09-23 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1040	Decisão negando liminar	2022-11-10 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1041	Juntada Réplica	2023-02-15 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1042	Juntada de Petição pedindo a reconsideração JG com Documentos	2022-08-30 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1043	Despacho - A autora para se manifestar sobre suspensão pedida pelo ERJ	2023-11-13 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1044	Juntada Petição Autora sobre provas	2023-07-24 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1045	Decisão não concedendo JG	2022-07-27 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1046	Despacho - às partes sobre provas	2023-06-26 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1047	Juntada Petição Autora sobre descabimento de suspensão	2023-11-14 00:00:00	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1049	Despacho - às partes sobre provas	2022-09-30 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1050	Manifestação MP - sem interesse	2022-10-10 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1051	Decisão Determinando envio dos Autos para justiça 4.0	2023-03-08 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1052	Ação distribuída	2022-07-27 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1053	Autos Remetidos ao TJRJ	2023-10-05 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1054	Apelação Distribuída para QUINTA CAMARA DE DIREITO PUBLICO	2023-12-05 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1055	Despacho requerendo inclusão da Apelação em Pauta de Julgamento e juntando relatório	2023-12-13 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1056	Juntada de Petição Autora informando Agravo (0080217-59.2022.8.19.0000)	2022-10-13 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1057	Juntada Contestação	2022-08-29 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1058	Sentença - Julgada Procedente a Ação	2023-05-05 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1059	Juntada petição Autora sobre Provas	2022-10-03 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1060	Decisão - Deferida JG | Indeferida liminar	2022-07-27 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1061	Juntada Réplica	2022-09-27 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1062	Juntadas Contrarrazões Autora	2023-07-24 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1063	Juntada de EDs Autora	2023-03-09 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1064	Decisão enviando autos de volta à Vara de Origem	2023-04-17 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1065	Decisão da Justiça 4.0 determinando manifestação da Autora	2023-03-25 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1066	Juntada Petição Autora sobre falta de manifestação ERJ	2022-12-30 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1067	Juntada de Petição da Autora informando Agravo (0020731-12.2023.8.19.0000)	2023-03-27 00:00:00	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1068	Decisão - Deferida JG | Determinada intimação ERJ	2023-02-02 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1069	Decisão Saneadora determinando apresentação de novos cálculos	2023-07-17 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1070	Ato Ordinatório - Autos aguardando decisão definitiva do AI	2023-09-01 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1071	Juntada Petição ERJ informando Agravo	2023-08-28 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1072	Juntada Impugnação ERJ	2023-04-06 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1073	Ação Distribuída	2023-02-01 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1074	Juntada Petição Autora em resposta à Impugnação ERJ	2023-04-10 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1075	Juntada Petição Autora com novos Cálculos	2023-07-18 00:00:00	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1076	Despacho pedindo mais documentos para JG	2023-02-23 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1077	Juntada Petição ERJ pedindo suspensão	2023-08-12 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1078	Processo com juiz para análise	2023-12-07 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1079	Juntada Petição Autora em resposta à Impugnação ERJ	2023-06-02 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1080	Despacho ao impugnante	2023-08-04 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1081	Despacho deferindo JG e determinando intimação ERJ	2023-04-28 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1082	Ação Distribuída	2023-02-01 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1083	Juntada Petição Autora em resposta	2023-10-04 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1084	Juntada Petição Autora com docs JG	2023-03-14 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1085	Juntada Impugnação ERJ	2023-05-31 00:00:00	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1086	Juntada Petição Autora com parâmetros	2023-12-05 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1087	Juntada Impugnação ERJ	2023-03-21 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1088	Despacho em Provas	2023-07-07 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1089	Juntada Petição ERJ em Provas	2023-07-16 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1090	Juntada Petição do Estado solicitando suspensão do processo	2023-12-19 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1091	Juntada Petição Autora em Provas	2023-07-10 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1092	Juntada de pedido de parâmatros pelo Contador	2023-12-04 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1093	Ação Distribuída	2023-01-31 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1094	Decisão determinando envio dos autos ao Contador	2023-09-28 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1095	Juntada Petição Autora em resposta à Impugnação ERJ	2023-03-23 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1096	Determinada nova intimação do Estado	2023-12-12 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1097	Decisão - Deferida JG | Determinada intimação ERJ	2023-02-15 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1098	Juntada Petição Autora em resposta ao Estado	2023-12-29 00:00:00	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1099	Juntada EDs Autora	2023-09-13 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1100	Juntada Réplica	2023-03-20 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1101	Despacho pedindo IR para análise JG	2023-02-02 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1102	Juntada Petição Autora informando Agravo (0016358-35.2023.8.19.0000)	2023-03-14 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1103	Juntada Petição Autora com IR	2023-02-10 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1104	Ação Distribuída	2023-01-31 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1105	Decisão determinando Suspensão do feito (Tema 1218)	2023-09-12 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1106	Despacho em provas	2023-05-03 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1107	Juntada EDs Autora sobre suspensão	2023-10-04 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1108	Juntada Petição Autora em provas	2023-05-04 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1109	Decisão determinando Suspensão com base no MS	2023-09-28 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1110	Decisão - Deferida JG | Indeferida Liminar	2023-02-28 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1111	Juntada Contestação	2023-03-20 00:00:00	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1112	Despacho - Deferida JG | Determinada intimação ERJ	2023-02-06 00:00:00	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1113	Decisão Saneadora - ao contador	2023-08-15 00:00:00	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1114	Juntada Impugnação ERJ	2023-03-09 00:00:00	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1115	Juntada Petição ERJ informando Agravo	2023-09-18 00:00:00	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1116	Juntada Petição Autora em resposta à Impugnação ERJ	2023-03-13 00:00:00	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1117	Ação Distribuída	2023-02-02 00:00:00	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1118	Ação Distribuída	2023-02-06 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1119	Despacho pedindo Carta de Sentença e Docs. JG	2023-02-08 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1120	Autos Remetidos ao TJRJ	2023-10-27 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1121	Juntada Apelação Autora	2023-07-17 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1122	Sentença não acolhendo EDs	2023-06-27 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1123	Juntada EDs Autora	2023-04-26 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1124	Juntada Petição Autora com Documentos	2023-02-15 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1125	Juntada Petição Autora com documentos extras	2023-05-15 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1126	Sentença extinguindo cumprimento em razão de não juntada de Carta de Sentença	2023-04-18 00:00:00	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1127	Despacho - Certifique a serventia acerca do cumprimento do despacho ID  69779346.  Após retornem conclusos para decisão saneadora.	2023-10-30 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1128	Juntada Petição Autora em resposta à Impugnação ERJ	2023-03-27 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1129	Despacho em provas	2023-07-31 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1130	Decisão determinando parâmetros para cálculo e envio ao Contador	2024-01-05 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1131	Decisão - Deferida JG | Determinada intimação ERJ	2023-02-08 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1132	Juntada Petição Autora em provas	2023-08-03 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1133	Juntada Impugnação ERJ	2023-03-16 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1134	Certificado o cumprimento ao Despacho	2023-10-31 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1135	Ação Distribuída	2023-02-06 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1136	Juntada Petição ERJ em provas	2023-08-03 00:00:00	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1137	Juntada Contestação	2023-04-14 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1138	Decisão em Agravo suspendendo liminar	2023-07-26 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1139	Juntada Petição autora reiterando pedido de julgamento - Recurso não impossibilita o julgamento	2023-12-12 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1140	Despacho suspendendo em razão de Agravo	2023-06-15 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1141	Juntada Petição Autora - desnecessidade de suspensão	2023-06-22 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1142	Despacho revogando declínio e pedindo docs JG	2023-02-13 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1143	Decisão de incompetência (Alcantara)	2023-02-09 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1144	Decisão - Deferida JG | Deferida Liminar	2023-03-02 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1145	Despacho em provas	2023-04-27 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1146	Certificado envio de ofício ao ERJ sobre suspensão do Recurso	2023-10-20 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1147	Juntada Petição Autora em provas	2023-05-04 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1148	Manifestação MP - sem interesse	2023-06-01 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1149	Juntada Petição Autora com docs	2023-02-15 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1150	Declinada Competência Alcantara	2023-02-10 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1151	Despacho sobre suspensão do Recurso	2023-09-21 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1152	Juntada Réplica	2023-04-14 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1153	Juntada Petição ERJ pedindo sobrestamento	2023-08-06 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1154	Balcão Virtual explicando sobre competência	2023-02-09 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1155	Ação Distribuída	2023-02-09 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1156	Juntada Petição Autora pedindo julgamento	2023-09-13 00:00:00	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1157	Juntada Réplica	2023-03-09 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1158	Juntada EDs Autora sobre suspensão	2023-10-25 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1159	Ação Distribuída	2023-02-02 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1160	Juntada Petição Autora Informando Agravo ( 0012159-67.2023.8.19.0000)	2023-02-26 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1161	Decisão determinando suspensão da ação	2023-10-24 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1162	Juntada Petição Autora em provas	2023-04-27 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1163	Despacho determinando que se aguarde o Agravo	2023-05-24 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1164	Decisão - Deferida JG | Negada Liminar	2023-02-07 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1165	Juntada Petição Autora pedindo prosseguimento	2023-06-22 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1166	Ato ordinatório: Em provas	2023-04-20 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1167	Juntada Contestação	2023-03-06 00:00:00	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1168	Juntada Contestação	2023-05-31 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1169	Juntada Petição ERJ informando Agravo	2023-05-31 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1170	Decisão - Deferida JG | Deferida Liminar	2023-05-05 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1171	Ofício RioPrev não altera folha	2023-05-24 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1172	Juntada Petição Autora em Provas	2023-06-30 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1173	Juntada Réplica	2023-06-02 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1174	Ato ordinatório - Em provas	2023-06-28 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1175	Petição Autora respondendo sobre suspensão e pedindo julgamento	2023-12-14 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1176	Despacho - Em sede de Juízo de retratação, mantenho a decisão	2023-10-17 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1177	Ação distribuída	2023-04-24 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1178	Petição Estado pedindo suspensão da ação	2023-11-06 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1179	Juntada Petição Autora sobre descumprimento tutela e pedindo julgamento	2023-10-30 00:00:00	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1180	Juntada Impugnação ERJ	2023-03-11 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1181	Juntada Petição Autora concordando com Cálculos	2023-09-27 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1182	Juntada Petição Autora em resposta à Impugnação ERJ	2023-03-13 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1183	Manifestação MP - Sem interesse	2023-07-17 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1184	Juntada EDs Autora - aplicado ano 2003	2023-08-16 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1185	Juntada Petição ERJ informando Agravo (0803105-10.2022.8.19.0055)	2023-09-10 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1186	Despacho - Ao MP	2023-07-14 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1187	Ação Distribuída	2022-11-09 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1188	Juntada de novos cálculos Contador	2023-11-21 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1189	Decisão - Declarada Incompetência	2022-11-25 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1190	Juntada de Cálculos do contador	2023-09-18 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1191	Decisão Saneadora - envio ao contador	2023-08-11 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1192	Despacho - deferida JG | Determinada intimação ERJ	2023-02-03 00:00:00	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1193	Juntada Petição Autora em provas	2023-04-14 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1194	Decisão - Deferida JG | Negada Liminar	2022-11-28 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1195	Ação Distribuída	2022-11-09 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1196	Cartório questionou sobre expedição de ofício	2023-10-03 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1197	Despacho: Expeça-se o ofício como determinado na r. sentença.	2023-10-24 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1198	Despacho em provas	2023-04-13 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1199	Juntada Contestação	2023-02-19 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1200	Manifestação Mp - Sem interesse	2023-05-22 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1201	Juntada Apelação ERJ	2023-08-23 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1202	Juntada Réplica	2023-02-21 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1203	Sentença - Julgada procedente a Ação	2023-08-11 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1204	Juntada Petição Autora informando Agravo (093085-69.2022.8.19.0000)	2022-12-01 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1205	Juntada Contrarrazões Autora	2023-08-24 00:00:00	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1206	Juntada Impugnação ERJ	2023-08-02 00:00:00	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1207	Certificada Impugnação e Resposta	2023-10-23 00:00:00	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1208	Decisão - Deferida JG | Determinada Intimação ERJ	2023-02-23 00:00:00	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1209	Juntada Petição Autora em resposta à Impugnação ERJ	2023-08-03 00:00:00	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1210	Juntada Petição Autora exclarecendo alguns pontos	2023-02-27 00:00:00	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1211	Ação Distribuída	2023-02-03 00:00:00	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1212	Ação distribuída	2023-04-28 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1213	Juntada Petição Autora - em provas	2023-07-10 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1214	Decisão determinando não suspensão - 10 dias para novos documentos	2023-10-26 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1215	Juntada Réplica	2023-06-20 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1216	Juntada Contestação	2023-06-15 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1217	Decisão - Deferida JG | Pedido de docs para liminar	2023-05-05 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1218	Petição Autora informando não possuir mais provas e pedindo julgamento	2023-11-20 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1219	Ato ordinatório - Em provas	2023-06-30 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1220	Juntada Petição Autora sobre docs liminar	2023-05-08 00:00:00	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1221	Juntada resposta à Impugnação ERJ	2023-08-10 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1222	Decisão - Deferida JG | Determinada intimação ERJ	2023-07-07 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1223	Protocolados EDs sobre Suspensão	2023-11-15 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1224	Decisão determinando emenda a inicial	2023-05-05 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1225	Juntada Impugnação ERJ	2023-08-09 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1226	Decisão determinando a suspensão do feito	2023-11-14 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1227	Juntada Emenda à Inicial	2023-05-15 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1228	Decisão mantendo suspensão	2023-12-05 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1229	Ciência da Decisão (prazo para agravo 01/02/2024)	2023-12-15 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1230	Ação distribuída	2023-04-28 00:00:00	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1231	Juntada Contestação	2023-06-13 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1232	Ofício RioRev não pode alterar folha	2023-05-24 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1233	Ação distribuída	2023-04-28 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1234	Juntada Petição Autor Provas e pedindo julgamento	2023-08-29 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1235	Decisão - Deferida JG | Deferida Liminar	2023-05-05 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1236	Juntada Réplica	2023-06-15 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1237	Despacho requerendo ao cartório que certifique os prazos para que possa elaborar Sentença	2023-12-18 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1238	Decisão Saneadora	2023-07-07 00:00:00	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1239	Decisão - Deferida JG | Deferida Liminar	2023-05-05 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1240	Ato ordinatório - Em provas	2023-06-28 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1241	Juntada Réplica	2023-06-28 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1242	Juntada Petição Autor pedindo Julgamento	2023-10-26 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1243	Decisão Saneadora concedendo 10 dias para apresentação de Documentos	2023-10-17 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1244	Juntada Contestação	2023-06-15 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1245	Juntada de Ofício RioPrev de não poder alterar folha	2023-05-15 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1246	Ação distribuída	2023-04-28 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1247	Juntada de Petição Autor - em provas	2023-06-30 00:00:00	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1248	Juntada Impugnação ERJ	2023-05-18 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1249	Juntada Petição Autora com documentos JG	2023-03-10 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1250	Juntada Petição Autora em resposta à Impugnação ERJ	2023-05-23 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1251	Decisão deferindo JG e determinando intimação ERJ	2023-03-28 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1252	Juntada Cálculos Contador	2023-11-30 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1253	Juntada Petição Autora reiterando resposta à Impugnação	2023-08-24 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1254	Ação Distribuída	2023-02-06 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1255	Decisão solicitando documentos para JG	2023-02-24 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1256	Juntada Impugnação ERJ pela segunda vez	2023-08-27 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1257	Juntada Petição Autora concordando com cálculos	2023-12-01 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1258	Decisão concordando com parâmetros Autora e determinando envio ao Contador	2023-10-25 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1259	Despacho ao MP	2023-07-10 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1260	Manifestação do MP - Sem interesse	2023-07-12 00:00:00	117	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1261	Ação Distribuída	2023-02-06 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1262	Autos conclusos	2023-10-24 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1263	Decisão acolhendo EDs | Deferindo JG	2023-04-05 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1264	Juntada Petição Autora sobre não apreciação de liminar	2023-04-10 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1265	Juntada Réplica	2023-05-31 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1266	Despacho - Ao Autor sobre pedido de suspensão	2023-10-19 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1267	Decisão indeferindo Liminar	2023-04-19 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1268	Juntada Petição Autora informando Agravo (0032660-42.2023.8.19.0000)	2023-05-08 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1269	Intimação sobre provas	2023-09-15 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1270	Juntada Contestação	2023-05-29 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1271	Juntada Petição ERJ sobre provas e pedindo Suspensão	2023-09-26 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1272	Juntada Petição Autora sobre descabimento Suspensão	2023-10-24 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1273	Despacho - Manifeste-se a parte ré sobre as alegações da autora. Após, decidirei.	2023-11-30 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1274	Decisão de declínio de competência	2023-02-08 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1275	Envio de Intimação	2023-12-01 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1276	Juntada Petição Autora sobre provas	2023-09-26 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1277	Juntada EDs Autora	2023-02-10 00:00:00	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1278	Despacho determinando a intimação do ERJ	2023-10-17 00:00:00	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1279	Ação Distribuída	2023-05-08 00:00:00	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1280	Despacho negando JG	2023-06-23 00:00:00	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1281	Juntada petição com custas	2023-07-05 00:00:00	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1282	Juntada Petição ERJ	2023-11-29 00:00:00	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1283	Certificada intimação ERJ	2023-10-29 00:00:00	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1284	Despacho determinando intimação do Estado	2023-11-13 00:00:00	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1285	Ação distribuída	2023-05-08 00:00:00	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1286	Despacho - negando JG e requerendo certidão de trânsito	2023-05-23 00:00:00	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1287	Juntada de petição com diferença de custas	2023-07-05 00:00:00	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1288	Certificado recolhimento a menor	2023-07-03 00:00:00	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1289	Juntada de petição com custas e certidão	2023-05-30 00:00:00	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1290	Sentença extinguindo feito por incomeptência	2023-02-13 00:00:00	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1291	Ação Distribuída	2023-02-10 00:00:00	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1292	Sentença não acolhendo EDs	2023-04-28 00:00:00	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1293	Juntada EDs Autora	2023-02-14 00:00:00	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1294	Juntada Apelação Autora	2023-05-09 00:00:00	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1295	Remetidos Autos TJRJ - 5ª Camara - ALEXANDRE TEIXEIRA DE SOUZA	2023-09-18 00:00:00	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1296	Juntada Petição Autora em Provas	2023-09-18 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1297	Ação Distribuída	2023-02-27 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1298	Negado Agravo	2023-09-01 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1299	Decisão - Deferida JG | Indeferida Liminar	2023-03-02 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1300	Despacho em provas	2023-09-05 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1301	Juntada Réplica	2023-06-12 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1302	Juntada Petição Autora informando Agravo (0016367-94.2023.8.19.0000)	2023-03-14 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1303	Juntada Contestação	2023-06-08 00:00:00	122	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1304	Despacho novamente em provas	2023-08-24 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1305	Decisão - Deferida JG | Indeferida Liminar	2023-01-17 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1306	Juntada Petição Autora em Provas	2023-05-31 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1307	Juntada Contestação	2023-02-03 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1308	Juntada Petição Autora pedindo julgamento da ação	2023-11-21 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1309	Determinada redistribuição da ação para outra Vara	2022-08-31 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1310	Despacho - Aguarde-se o julgamento do Agravo de Instrumento nº 0004821-42.2023.8.19.0000. Devendo as partes informarem acerca do julgamento.	2023-11-20 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1311	Certificado - Certifico que as partes devidamente intimadas eletronicamente, se manifesta a parte autora id.74238727, em provas, ficando inerte a parte ré.	2023-11-14 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1312	Despacho em Provas	2023-05-24 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1313	Juntada Petição Autora requerendo julgamento	2023-08-25 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1314	Juntada Réplica	2023-02-15 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1315	Ação distribuída	2022-08-25 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1316	Juntada Petição Autora informando Agravo (0004821-42.2023.8.19.0000)	2023-01-30 00:00:00	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1317	Juntada Impugnação ERJ	2023-09-03 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1318	Juntada Petição em resposta à Carta de sentença - não existe tal documento	2023-03-21 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1319	Opostos EDs Autora - recurso	2023-03-03 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1320	Decisão determinando intimação ERJ	2023-06-14 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1321	Petição Autora com comprovante de peticionamento da Ação Coletiva	2023-05-15 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1322	Decisão com declínio de competência	2023-03-02 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1323	Ação Distribuída	2023-03-01 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1324	Juntada Manifestação MP - sem interesse no feito	2023-11-08 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1325	Petição Autora reiterando desnecessidade de carta	2023-04-26 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1326	Despacho determinando envio ao MP	2023-09-15 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1327	Juntada Resposta Autora	2023-09-05 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1328	EDs Acolhidos | Concedida JG | Determinada juntada de carta de sentença	2023-03-10 00:00:00	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1329	Certificada intimação do ERJ sobre homologação	2023-10-25 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1330	Decisão Deferida JG | Determinada intimação ERJ	2023-03-02 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1331	Estado informou Recurso de Agravo de Instrumento (0091870-24.2023.8.19.0000)	2023-11-09 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1332	Ação Distribuída	2023-03-01 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1333	Juntada Petição Autora em resposta à Impugnação	2023-06-06 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1334	Decisão negando impugnação | Homologando cálculo	2023-10-03 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1335	Autos conclusos com juiz para análise de petições	2023-09-12 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1336	Juntada Impugnação ERJ	2023-06-03 00:00:00	125	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1337	Informação de negativa ao Agravo	2023-07-05 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1338	Juntada Réplica	2023-03-20 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1339	Decisão - Deferida JG | Indeferida Liminar	2023-03-14 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1340	Ação Distribuída	2023-03-09 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1341	Ato Ordinatório - Em provas	2023-09-25 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1342	Juntada Petição Autora em provas	2023-09-27 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1343	Juntada Petição Autora informando Agravo (0020863-69.2023.8.19.0000)	2023-03-27 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1344	Juntada Contestação	2023-03-16 00:00:00	126	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1345	Juntado Ofício contadoria - sobre avaliação 2001/2003	2023-09-19 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1346	Juntada Impugnação ERJ	2023-04-16 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1347	Juntada Petição Autora em resposta à impugnação ERJ	2023-04-17 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1348	Ato ordinatório certificando ofício - autos conclusos	2023-09-20 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1349	Decisão - Deferida JG | Determinada Intimação ERJ	2023-03-11 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1350	Juntada Petição Autora sobre parâmetros de cálculo	2023-10-06 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1351	Despacho ao Contador	2023-08-04 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1352	Ação Distribuída	2023-03-03 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1353	Despacho - às partes sobre manifestação contador	2023-10-04 00:00:00	127	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1354	Juntada Petição Autora sobre Provas	2023-12-01 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1355	Ação distribuída	2023-03-03 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1356	Decisão - Deferida JG | Determinada Intimação ERJ	2023-05-29 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1357	Despacho - Digam as partes se há provas a produzir, justificadamente, no prazo de 05 (cinco) dias	2023-11-28 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1358	Juntada Impugnação ERJ	2023-06-20 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1359	Despacho pedindo documentos JG	2023-03-07 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1360	Juntada Petição Autora em resposta à Impugnação ERJ	2023-06-21 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1361	Juntada Petição Autora com Docs JG	2023-03-16 00:00:00	128	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1362	Certificadas manifestações - autos conclusos	2023-09-11 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1363	Juntada Petição Autora Informando Agravo (0017084-09.2023.8.19.0000)	2023-03-15 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1364	Ofício Expedido	2023-11-14 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1365	Juntada Réplica	2023-06-09 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1366	Juntada Contestação	2023-06-04 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1367	Juntada Petição ERJ em Provas	2023-08-24 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1368	Juntada Petição Autora requerendo julgamento	2024-01-11 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1369	Juntada Petição Autora em Provas	2023-08-24 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1370	Retorno de ofício confirmando paridade	2024-01-08 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1371	Decisão - Deferida JG | Indeferida tutela	2023-03-11 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1372	Decisão deferindo expedição ofício SEEDUC sobre paridade	2023-10-10 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1373	Ato ordinatório - Em provas	2023-08-23 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1374	Ação Distribuída	2023-03-03 00:00:00	129	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1375	Juntada EDs Autora	2023-03-15 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1376	Juntada Petição Autora pedindo julgamento	2023-07-17 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1377	Protocolados EDs Autora Sobre Suspensão	2023-11-15 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1378	Decisão negando EDs	2023-11-22 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1379	Decisão suspendendo feito	2023-11-14 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1380	Juntada Petição Autora informango Agravo (0024828-55.2023.8.19.0000)	2023-04-10 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1381	Decisão - Negada Liminar | Determinada intimação ERJ	2023-03-31 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1382	Juntado ofício RioPrev sobre Folha	2023-06-20 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1383	Ação Distribuída	2023-03-06 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1384	Decisão: Em provas | Sobre intimação ERJ sobre tutela	2023-05-30 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1385	Juntada Contestação	2023-04-28 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1386	Decisão suspendendo ação - IRDR - Nº 0017256-92.2016.8.19.0000	2023-03-14 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1387	Despacho - Em réplica	2023-05-03 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1388	Juntada Petição Autora sobre provas	2023-05-31 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1389	Juntada Réplica	2023-05-09 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1390	Juntada Decisão de Agravo concedendo Tutela	2023-05-24 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1391	Juntada Petição da Autora informando Recurso de Agravo sobre suspensão	2023-11-23 00:00:00	130	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1392	Enviado RPV	2024-01-10 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1393	Juntada Petição Autora requerendo parcelamento Taxa e aplicação isenção	2023-04-24 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1394	Despacho solicitando mais documentos para JG	2023-03-24 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1395	Decisão deferindo parcelamento em 06 vezes	2023-08-28 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1396	Enviada RPV	2024-01-10 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1397	Certificado: Certifico que as 1ª e 2ª parcelas foram recolhidas corretamente.	2023-11-10 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1398	Decisão indeferindo JG	2023-04-17 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1399	Juntada Petição Autora com GRERJ 03/06	2023-11-14 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1400	Juntada Impugnação ERJ	2024-01-10 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1401	Juntada Petição Autora com documentos JG	2023-03-27 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1402	Determinada intimação do Estado	2023-11-27 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1403	Juntada Petição Autora com GRERJ 01/06	2023-09-06 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1404	Ação Distribuída	2023-03-03 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1405	Juntada Resposta Autora	2024-01-11 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1406	Juntada Petição Autora com GRERJ 02/06	2023-10-06 00:00:00	131	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1407	Certificado:  Certifico que somente a parte autora se manifestou sobre index 67353881.	2023-11-06 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1408	Juntada Réplica	2023-05-08 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1409	Despacho: Em provas	2023-07-13 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1410	Juntada de EDs Autora	2023-03-28 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1411	Juntada Petição Autora em Provas	2023-07-17 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1412	Juntada Contestação	2023-05-05 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1413	Decisão de incompetência	2023-03-27 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1414	Decisão acolhendo EDs e determinando intimação ERJ - JG deferida	2032-04-25 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1415	Processo com juiz para análise	2023-11-06 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1416	Ação Distribuída	2023-03-03 00:00:00	132	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1417	Juntada Petição ERJ informando Agravo	2023-08-29 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1418	Juntada Petição Autora sobre não análise de EDs	2023-11-09 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1419	Despacho - Aguarde-se o julgamento do AI interposto.	2023-11-08 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1420	Decisão aplicando avaliação de 2003 e não condenando em sucumbencia	2023-08-03 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1421	Ação Distribuída	2023-03-09 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1422	Juntada Impugnação ERJ	2023-05-02 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1423	Juntada EDs Autora	2023-08-07 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1424	Sentença de desprovimento EDs	2023-11-14 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1425	Juntada Petição Autora em resposta à Impugnação ERJ	2023-05-04 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1426	Juntada de Petição Autora - informando Recurso sobre a avaliação determinada	2023-11-21 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1427	Decisão - Deferida JG | Determinada intimação ERJ	2023-03-14 00:00:00	133	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1428	Desprovido Recurso de Apelação ERJ	2023-09-26 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1429	Protocolas Contrarrazões aos EDs ERJ	2023-11-01 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1430	Decisão Saneadora	2023-03-16 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1431	Processo Distribuído	2022-05-23 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1432	Juntada de Recurso de Apelação ERJ	2023-07-10 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1433	Juntada de Petição Autora sobre paridade	2023-05-19 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1434	Juntada de Petição informando interposição de Agravo de Instrumento 	2022-06-22 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1435	Juntada de Contrarrazões à Apelação	2023-07-11 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1436	Juntada de Réplica	2022-08-25 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1437	Remetido para o Tribunal de Justiça	2023-08-11 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1438	Juntada Petição Provas Autora	2022-09-02 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1439	Sentença de Procedência	2023-05-25 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1440	Decisão concedendo JG e negando Antecipação de Tutela	2022-06-07 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1441	Juntada Petição sobre falta de manifestação ERJ	2022-12-30 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1442	Manifestação MP - Não intervenção	2023-02-15 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1443	Juntada de Contestação	2022-08-15 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1444	Pauta de Julgamento: 25/01/2024	2023-12-01 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1445	- Apelação - Juntada EDs ERJ	2023-10-30 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1446	Despacho sobre Paridade	2023-05-15 00:00:00	134	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1447	Petição Autora informando Agravo sobre liminar (0012151-90.2023.8.19.0000)	2023-02-26 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1448	Expedida intimação	2023-10-26 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1449	Distribuída Ação	2022-11-29 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1450	Decisão Concedendo JG e determinando intimação ERJ	2023-01-13 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1451	Juntada petição com documentos extras	2022-12-06 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1452	Decisão - EDs desprovidos - Mantendo suspensão	2023-10-09 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1453	Processo Suspenso ou Sobrestado por Por decisão judicial	2023-10-17 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1454	Juntada de Contestação (resposta Estado)	2023-03-21 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1455	Decisão pedindo mais documentos para análise JG	2022-11-30 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1456	Decisão negando liminar	2023-02-06 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1457	Apresentamos Recurso sobre a suspensão - Agravo distribuído para 3ª Câmara - 	2023-10-20 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1458	Juntada de Réplica (nossa resposta a resposta do Estado)	2023-03-21 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1459	Juntada de contrarrazões do Estado em Recurso	2023-12-19 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1460	Juntada Petição Autora sobre provas (pedindo julgamento)	2023-04-04 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1461	Determinada apresentação de provas (se necessário)	2023-04-04 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1462	Oposição EDs Autora (não concordando com a suspensão)	2023-09-13 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1463	Petição Autora sobre falta de análise do pedido liminar	2023-01-26 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1464	- Agravo 0084773-70.2023.8.19.0000 -	2023-11-27 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1465	- Intimado ERJ em Agravo para Contrarrazões -	2023-11-27 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1466	Decisão determinando Suspenção da Ação em razão de Tema 1218 STF	2023-09-05 00:00:00	135	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1467	Juntada Impugnação ERJ	2023-06-20 00:00:00	136	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1468	Decisão - Deferida JG | Determinada intimação ERJ	2023-05-15 00:00:00	136	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1469	Ação Distribuída	2023-03-24 00:00:00	136	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1470	Certificada a apresentação das petições	2023-11-06 00:00:00	136	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1471	Processo com o juiz para análise	2023-11-06 00:00:00	136	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1472	Juntada Petição Autora em resposta à Impugnação ERJ	2023-06-21 00:00:00	136	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1473	Juntada Petição ERJ Informando cumprimento Liminar	2023-08-01 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1474	Despacho - A parte autora sobre o cumprimento pelo Estado	2023-11-06 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1475	Ação Distribuída	2023-03-24 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1476	Juntada Réplica	2023-05-08 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1477	Petição Autora sobre suspensão liminar e pedindo julgamento	2023-11-08 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1478	Despacho à Autora sobre info ERJ	2023-08-02 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1479	Juntada Petição Autora informando Agravo (024301-06.2023.8.19.0000)	2023-04-10 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1480	Juntada Petição Autora sobre info ERJ	2023-08-03 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1481	Decisão - Deferida JG | Indeferida Liminar	2023-03-30 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1482	Juntada Contestação	2023-04-28 00:00:00	137	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1483	Certificado que autos aguardam julgamento Agravo	2023-06-14 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1484	Decisão de incompetência (Juizado)	2023-03-27 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1485	Juntado Ofício TJRJ com Decisão de Recurso (Positiva)	2023-12-04 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1486	Ação Distribuída	2023-03-24 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1487	Juntada EDs Autora	2023-03-27 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1488	Juntada Petição Autora informando Agravo (0021583-36.2023.8.19.0000)	2023-03-29 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1489	Decisão não acolhendo EDs	2023-03-28 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1490	Juntada Petição Autora sobre decisão em Agravo e pedindo prosseguimento da ação	2023-12-04 00:00:00	138	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1491	Juntada Resposta Autora sobre impugnação ERJ	2023-11-14 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1492	Decisão determinando Emenda (Faltariam Fatos)	2023-05-16 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1493	Juntada Emenda à Inicial	2023-06-15 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1494	Autos conclusos	2023-09-15 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1495	Ação distribuída	2023-04-20 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1496	Expedida Citação ERJ	2023-11-09 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1497	Decisão - Indeferida Liminar	2023-10-08 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1498	Juntada Impugnação ERJ	2023-11-13 00:00:00	139	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1499	Juntada Petição Autora sobre JG	2023-11-22 00:00:00	140	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1500	Ação distribuída	2023-10-31 00:00:00	140	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1501	Ato Ordinatório sobre custas	2023-11-01 00:00:00	140	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1502	Decisão concedendo JG e concedendo a Tutela	2022-07-01 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1503	Juntada Petição Autora sobre Provas	2023-03-13 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1504	Juntada Petição Autora pedindo Sentença	2023-07-11 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1505	Juntada Petição ERJ informando procedimento para cumprir Tutela	2023-03-31 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1506	Processo distribuído	2022-06-13 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1507	Juntada da Réplica	2022-08-22 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1508	Juntada da Contestação	2022-08-15 00:00:00	141	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1509	Ação distribuída	2023-03-29 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1510	Decisão - Concedida JG | Concedida Liminar	2023-09-11 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1511	Juntada Petição informando isenção legal e comprovando recolhimento Taxa	2023-04-11 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1512	Juntada Petição Autora sobre necessidade de envio para conclusão	2023-09-06 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1513	Decisão indeferindo JG	2023-04-05 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1514	Juntada EDs ERJ	2023-09-25 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1515	Ato ordinatório completamente errado certificando não recolhimento de custas	2023-09-06 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1516	Juntada Réplica	2023-11-08 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1517	Juntada Contrarrazões aos EDs	2023-10-04 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1518	Juntada Contestação ERJ	2023-10-26 00:00:00	142	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1519	Enviado E-mail pedindo análise	2023-10-04 00:00:00	143	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1520	Juntada de IRs	2023-08-31 00:00:00	143	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1521	Decisão - Pedindo IR | Negando Liminar	2023-08-09 00:00:00	143	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1522	Petição Autora informando Agravo (072987-29.2023.8.19.0000)	2023-09-05 00:00:00	143	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1523	Solicitado andamento dos autos em atendimento	2023-11-09 00:00:00	143	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1524	Ação Distribuída	2023-08-02 00:00:00	143	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1525	Juntada Resposta à Impugnação ERJ	2023-11-12 00:00:00	144	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1526	Expedida intimação	2023-10-26 00:00:00	144	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1527	Juntada Resposta Impugnação ERJ	2023-11-09 00:00:00	144	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1528	Ação distribuída	2023-08-03 00:00:00	144	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1529	Decisão - Deferida JG | Determinada intimação ERJ	2023-08-07 00:00:00	144	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1530	Ação distribuída	2023-08-10 00:00:00	145	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1531	Juntada de Petição com Emenda	2023-08-31 00:00:00	145	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1532	Processo com o Juiz para análise	2023-12-13 00:00:00	145	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1533	Decisão determinando Emenda	2023-08-11 00:00:00	145	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1534	Juntada Petição Autora em provas	2023-09-22 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1535	Juntada de aditamento à Inicial	2022-08-08 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1536	Juntada Réplica	2023-05-08 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1537	Juntada Petição ERJ requerendo suspenção com base no Tema1218	2023-09-21 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1538	Juntada Contestação	2023-04-28 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1539	Despacho em provas	2023-09-18 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1540	Ação Distribuída	2022-07-28 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1541	Decisão indeferindo Liminar	2022-12-13 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1542	Despacho requerendo IR Autora	2022-08-01 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1543	Despacho reiterando necessidade de IR	2022-08-10 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1544	Juntada Petição Autora com pagamento de custas	2022-08-22 00:00:00	146	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1545	Ação Distribuída	2022-07-28 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1546	Decisão determinando suspensão (MS)	2023-10-03 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1547	Despacho reiterando necessidade de IR	2022-08-10 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1548	Juntada Petição Autora em provas	2023-04-14 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1549	Certificada falta de manifestação do ERJ	2023-08-29 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1550	Despacho em provas	2023-04-12 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1551	Juntada Petição Autora aditando Inicial	2022-08-08 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1552	Juntada PEtição Autora com GRERJ Custas	2022-08-22 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1553	Juntada EDs Autora sobre suspensão	2023-10-04 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1554	Juntada Contestação	2023-04-05 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1555	Juntada Réplica	2023-04-10 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1556	Decisão não concedendo Tutela	2022-10-25 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1557	Despacho solicitando IR Autora	2022-08-01 00:00:00	147	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1558	Juntada Petição Autora informando Agravo (0060766-48.2022.8.19.0000)	2022-08-16 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1559	Juntada Petição Autora sobre desnecessidade de suspensão	2023-10-25 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1560	Juntada Petição Autora em provas	2023-09-27 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1561	Despacho pedindo mais documentos JG	2022-08-01 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1562	Juntada Petição ERJ sobre suspensão	2023-10-11 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1563	Ação Distribuída	2022-07-28 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1564	Despacho determinando prosseguimento com apresentação de provas	2023-09-26 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1565	Decisão - Deferida JG | Indeferida Liminar	2022-08-05 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1566	Juntada Petição Autora sobre falta de apresentação de Contestação	2022-10-11 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1567	Despacho pedindo para cartório certificar sobre contestação	2023-04-03 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1568	Autos conclusos	2023-10-25 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1569	Juntada Petição Autora com documentos JG	2022-08-02 00:00:00	148	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1570	Ação Distribuída	2022-08-25 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1571	Juntada Petição Autora em provas	2022-12-30 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1572	Decisão não acolhendo EDs	2023-08-03 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1573	Juntada Contestação	2022-09-27 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1574	Decisão - Deferida JG | Indeferida Liminar	2022-08-31 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1575	Despacho em provas	2022-12-12 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1576	Juntada Petiçãpo Autora informando Agravo (0067436-05.2022.8.19.0000)	2022-09-01 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1577	Juntada Réplica	2022-10-03 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1578	Juntada Petição Autora informando Agravo (062687-08.2023.8.19.0000)	2023-08-07 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1579	Juntada EDs Autora	2023-07-17 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1580	Decisão determinando suspensão (ACP)	2023-07-11 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1581	- Decisão negando agravo e determinando baixo - Tema 1218	2023-10-31 00:00:00	149	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1582	Despacho determinando intimação ERJ	2022-11-17 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1583	Decisão - Negando JG - Reconhecendo Isenção	2022-08-12 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1584	Junta Petição Autora de Parcela 03/03 e resposta à Impugnação ERJ	2023-01-06 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1585	Decisão determinando RPV	2023-09-14 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1586	Ação Distribuída	2022-08-03 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1587	Juntada Petição com parcela 01/03	2022-10-11 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1588	Juntada Petição ERJ sobre incompetência	2023-06-26 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1589	Sentença de procedencia	2023-04-18 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1590	Juntada Petição Autora requerendo parcelamento Taxa	2022-08-25 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1591	Despacho deferindo Parcelamento	2022-09-20 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1592	Juntada Petição Autora em resposta ao ERJ	2023-06-28 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1593	Juntada Petição Autora com Parcela 02/03	2022-11-26 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1594	Juntada Impugnação ERJ	2022-12-20 00:00:00	150	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1595	Juntada Contestação	2023-03-01 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1596	Petição Autora informando Agravo (0067435-20.2022.8.19.000)	2022-09-01 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1597	Juntada de Ofício da RioPrev informando não ser o responsável para alterar a folha	2023-04-12 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1598	Decisão requerendo IRs	2022-08-25 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1599	Decisão determinando cumprimento da liminar pelo Estado	2023-04-03 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1600	Despacho Determinando que seja certificada intimação ERJ	2023-05-12 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1601	Juntada petição Autora sobre descumprimento	2023-05-04 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1602	Juntada de Ofício do ERJ sobre cumprimento	2023-06-01 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1603	Despacho determinando Provas	2023-06-14 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1604	Juntada Réplica	2023-03-09 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1605	Decisão informando sobre Decisão em MS 0071377-26.2023.8.19.0000	2023-10-17 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1606	Juntada Petição Autora sobre Provas	2023-06-21 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1607	Autos com o juiz para análise de petições	2023-09-12 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1608	Distribuída Ação	2022-08-18 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1609	Juntada Petição Autora sobre suspensão - inaplicável ao caso da Autora	2023-11-01 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1610	Decisão - Deferida JG | Indeferida Liminar	2022-08-26 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1611	Juntada de IR	2022-08-26 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1612	Juntada petição Autora sobre decisão em Agravo concedendo a liminar	2023-03-09 00:00:00	151	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1613	Decisão Saneadora	2023-08-03 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1614	Juntada Petição Autora em Provas	2023-04-10 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1615	Decisão - Acolhidos EDs | Deferida Liminar	2023-02-16 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1616	Juntada EDs Autora sobre erro material (consta 16h e são 18h)	2023-12-01 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1617	Decisão - Declínio de competência	2022-09-21 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1618	Sentença julgando pedidos da inicial procedentes	2023-11-29 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1619	Despacho sobre Agravo ERJ e em Provas	2023-04-05 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1620	Ação Distribuída	2022-08-26 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1621	Despacho novamente em provas	2023-07-21 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1622	Juntada Petição ERJ em Provas	2023-04-26 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1623	Juntada Réplica	2023-03-09 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1624	Ato ordinatório certificando manifestação em provas	2023-07-24 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1625	Juntada Apelação ERJ	2023-12-01 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1626	Juntada EDs Autora	2022-09-27 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1627	Juntada Contestação	2023-03-02 00:00:00	152	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1628	Despacho reiterando sobre Carta de Sentença	2023-08-28 00:00:00	153	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1629	Despacho pedindo carta de Sentença	2023-06-06 00:00:00	153	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1630	JG não concedida - determinado pagamento das custas	2023-11-29 00:00:00	153	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1631	Juntada Petição Autora novamente sobre Carta de Sentença	2023-09-11 00:00:00	153	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1632	Ação Distribuída	2023-06-02 00:00:00	153	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1633	Juntada Petição Autora sobre carta de sentença	2023-06-26 00:00:00	153	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1634	Ação distribuída	2023-06-02 00:00:00	154	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1635	Juntada Petição Autora com Custas	2023-08-04 00:00:00	154	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1636	Juntada EDs Autora	2023-06-06 00:00:00	154	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1637	Autos conclusos	2023-10-19 00:00:00	154	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1638	Decisão de declínio de competência (Juizado)	2023-06-05 00:00:00	154	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1639	Decisão - Acolhidos EDs | Indeferida JG	2023-07-17 00:00:00	154	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1640	Juntada Petição com diferença de custas	2023-10-01 00:00:00	155	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1641	Certificada diferença de custas	2023-09-11 00:00:00	155	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1642	Decisão - indeferida JG	2023-06-02 00:00:00	155	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1643	Juntada Petição Autora informando recolhimento da diferença	2023-09-26 00:00:00	155	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1644	Juntada Petição Autora com custas	2023-06-26 00:00:00	155	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1645	Ação Distribuída	2023-06-02 00:00:00	155	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1646	Juntada de Apelação	2023-05-11 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1647	Marcado Julgamento Virtual para 09/11	2023-10-25 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1648	Juntada Contestação	2023-02-25 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1649	Decisão - Deferida JG | Negada Liminar	2023-02-15 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1650	Remetidos ao TJRJ - 1 Câmara - José Acir	2023-07-08 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1651	Protocolo de Recursos Especial e Extraordinário ERJ	2023-12-14 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1652	Juntada de Contrarrazões	2023-05-17 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1653	Petição MP sobre não intervenção	2023-04-11 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1654	Juntada Petição informando Agravo (0012183-95.2023.8.19.0000)	2023-02-27 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1655	Juntada Réplica	2023-03-01 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1656	Ação distribuída	2023-02-13 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1657	Conhecido o Recurso e Não-Provido	2023-11-09 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1658	Protocolo de contrarrazões aos Recursos	2023-12-18 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1659	Sentença de Procedência	2023-04-26 00:00:00	156	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1660	Autos conclusos	2023-09-12 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1661	Juntada Contestação	2023-04-14 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1662	Ação Distribuída	2022-09-16 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1663	Decisão - Deferida JG | Indeferida Liminar	2022-10-10 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1664	Juntada Petição Autora em provas	2023-06-21 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1665	Autos Conclusos	2023-10-24 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1666	Juntada Réplica	2023-04-17 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1667	Juntada EDs Autora - sobre suspensão	2023-10-11 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1668	Despacho - Ao Estado/Embargado, sobre os Embargos de Declaração de index 82589069, na forma do art. 1.023, § 2º, do CPC.	2023-12-07 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1669	Juntada Petição Autora informando Agravo (0080777-98.2022.8.19.0000)	2022-10-15 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1670	Despacho em Provas	2023-06-19 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1671	Decisão de suspensão Tema 1218 e MS	2023-10-09 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1672	Certificada não manifestação ERJ	2023-09-12 00:00:00	157	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1673	Juntada Petição Autora informando Agravo (052571-40.2023.8.19.0000)	2023-07-06 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1674	Juntada Contestação	2023-08-19 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1675	Decisão - Deferida JG | Indeferida Liminar	2023-06-20 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1676	Despacho para as partes se manifestarem sobre Agravo	2024-01-09 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1677	Juntada Réplica	2023-08-22 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1678	Ação Distribuída	2023-06-20 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1679	Autos com juíz para análise	2023-12-19 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1680	Juntada Petição Autora requerendo julgamento	2024-01-11 00:00:00	158	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1681	Ação distribuída	2023-06-01 00:00:00	159	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1682	Juntados EDs Autora sobre Decisão de valor	2023-08-14 00:00:00	159	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1683	Decisão determinando recolhimento Taxa judiciária	2023-06-07 00:00:00	159	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1684	Decisão determinando adequação ao valor da causa	2023-08-11 00:00:00	159	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1685	Juntada Petição Autora com taxa judiciária	2023-06-12 00:00:00	159	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1686	Determinada apresentação de mais provas (caso necessário)	2023-09-20 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1687	Juntada de Contestação (Resposta do Estado)	2023-06-27 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1688	Ação Distribuída	2023-06-02 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1689	Decisão negando alegações preliminares do Estado - após prazo retornar para Sentença	2023-12-18 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1690	Juntada Petição Autora sobre Provas (Sem necessidade - pedindo julgamento)	2023-09-22 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1691	Decisão - Não analisou JG | Determinou intimação ERJ	2023-06-06 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1692	Juntada de Réplica (nossa resposta a resposta do Estado)	2023-06-28 00:00:00	160	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1693	Juntada Impugnação ERJ	2023-08-08 00:00:00	161	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1694	Juntada Petição Autora em resposta à Impugnação ERJ	2023-08-10 00:00:00	161	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1695	Certificada a juntada das petições	2023-11-09 00:00:00	161	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1696	Processo com juiz para análise	2023-11-09 00:00:00	161	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1697	Ação Distribuída	2023-06-21 00:00:00	161	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1698	Decisão - deferida JG | Determinada intimação ERJ	2023-07-12 00:00:00	161	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1699	Juntada Contestação	2023-07-28 00:00:00	162	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1700	Juntada Réplica	2023-08-01 00:00:00	162	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1701	Ação distribuída	2023-06-21 00:00:00	162	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1702	Decisão - Deferida JG | Negada Liminar	2023-06-26 00:00:00	162	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1703	Manifestação Mp - sem interesse	2023-06-30 00:00:00	162	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1704	Juntada Petição Autora informando Agravo ( 0052573-10.2023.8.19.0000)	2023-07-06 00:00:00	162	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1705	Juntada Petição Autora sobre Provas (pedindo julgamento - não há novas provas))	2023-11-14 00:00:00	163	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1706	Juntada de Contestação (resposta do Estado)	2023-07-11 00:00:00	163	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1707	Juntada de Réplica (nossa resposta a resposta do Estado)	2023-07-17 00:00:00	163	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1708	Decisão concedendo JG e determinando intimação do Estado	2023-06-30 00:00:00	163	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1709	Distribuição da Ação	2023-06-28 00:00:00	163	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1710	Ação Distribuída	2023-06-28 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1711	Decisão - indeferida JG	2023-07-06 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1712	Juntada EDs Autora sobre isenção	2023-07-17 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1713	Determinada inclusão do Agravo em Pauta para julgamento	2023-09-14 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1714	Juntada Petição Autora informando Agravo (0066780-14.2023.8.19.0000)	2023-08-21 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1715	Decisão negando EDs	2023-08-17 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1716	Juntada Petição Autora com Taxa e informando isenção	2023-07-10 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1717	Decisão ignorando isenção e determinando pagamento	2023-07-14 00:00:00	164	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1718	Certificado que não houve julgamento do Agravo	2023-08-14 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1719	Decisão determinando retorno dos autos para aguardar Agravo	2023-02-02 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1720	Juntada EDs da Autora	2022-12-30 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1721	Juntada Petição Autora informando Agravo (0004045-42.2023.8.19.0000)	2023-01-26 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1722	Decisão determinando incompetência da Vara	2022-12-15 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1723	- Agravo Julgado: Conhecido o Recurso e Provido - Unanimidade -	2023-10-26 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1724	Juntada Petição Autora sobre resultado Agravo	2023-11-01 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1725	Decisão negando EDs	2023-01-18 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1726	Ação Distribuída	2022-12-07 00:00:00	165	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1727	Decisão - Concedida JG | Negada Liminar	2023-07-12 00:00:00	166	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1728	Juntada Contestação	2023-08-08 00:00:00	166	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1729	Ação Distribuída	2023-07-11 00:00:00	166	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1730	Juntada Réplica	2023-08-10 00:00:00	166	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1731	Juntada Petição Autora informando Agravo ( 0058672-93.2023.8.19.0000)	2023-07-24 00:00:00	166	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1732	Ação Distribuída	2023-09-11 00:00:00	167	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1733	Expedido mandado de citação	2023-12-11 00:00:00	167	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1734	Juntada Petição Autora com documentos e Email	2023-09-14 00:00:00	167	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1735	Despacho pedindo docs JG e Email Autora	2023-09-12 00:00:00	167	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1736	Decisão deferindo JG e determinando a intimação do estado	2023-12-04 00:00:00	167	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1737	Despacho em provas	2023-09-19 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1738	Decisão - Deferida JG | Deferida Liminar	2023-08-11 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1739	Ação distribuída	2023-07-26 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1740	Juntada Petição Autora - sobre provas	2023-10-04 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1741	Petição Autora informando desnecessário aguardar decisão em Recurso - pedindo julgamento	2023-12-14 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1742	Certificado Efeito suspensivo Recurso - liminar suspensa	2023-10-06 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1743	Juntada Réplica	2023-08-29 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1744	Juntada Contestação	2023-08-22 00:00:00	168	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1745	Ação Distribuída	2023-07-26 00:00:00	169	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1746	Despacho pedindo mais documentos JG	2023-07-28 00:00:00	169	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1747	Juntada Petição Autora com documentos JG	2023-07-31 00:00:00	169	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1748	Juntada Petição Autora em resposta à impugnação ERJ	2023-10-10 00:00:00	169	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1749	Decisão - Deferida JG | Determinada Intimação ERJ	2023-08-17 00:00:00	169	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1750	Juntada Impugnação ERJ	2023-09-29 00:00:00	169	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1751	Juntada Contestação	2023-10-10 00:00:00	170	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1752	Expedida Citação	2023-09-20 00:00:00	170	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1753	Decisão - Deferida JG | Não decidido quanto a Liminar	2023-09-15 00:00:00	170	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1754	Ação Distribuída	2023-09-15 00:00:00	170	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1755	Juntada Réplica Autora	2023-10-24 00:00:00	170	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1756	Despacho requerendo mais documentos para JG	2023-08-02 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1757	Ação Distribuída	2023-07-28 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1758	Juntada EDs sobre possível suspensão após Réplica	2023-10-04 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1759	Juntada Petição Estado CR aos EDs	2023-11-08 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1760	Despacho - ao Embargado (ERJ)	2023-10-30 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1761	Juntada Petição Autora com documentos JG	2023-08-02 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1762	Decisão - Deferida JG | Indeferida Liminar 	2023-09-21 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1763	Juntada Contestação	2023-11-07 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1764	Juntada Réplica	2023-11-08 00:00:00	171	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1765	Ação Distribuída	2023-08-03 00:00:00	172	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1766	Juntada Petição Autora com documentos JG	2023-09-04 00:00:00	172	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1767	Despacho pedindo documentos para análise JG	2023-08-16 00:00:00	172	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1768	Autos conclusos	2023-10-27 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1769	Juntada Contestação	2023-10-23 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1770	Decisão acolhendo Eds sobre Competência	2023-11-22 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1771	Despacho - Ao MP	2023-10-23 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1772	Certificada tempestividade	2023-10-23 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1773	Juntada Eds Autora sobre competência	2023-10-24 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1774	Ação Distribuída	2023-10-17 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1775	Decisão declinando competência	2023-10-20 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1776	Juntada Réplica Autora	2023-11-23 00:00:00	173	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1777	Despacho: ao MP	2023-11-07 00:00:00	174	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1778	Manifestação MP sobre não interesse	2023-11-08 00:00:00	174	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1779	Ação Distribuída	2023-10-17 00:00:00	174	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1780	Autos conclusos	2023-10-17 00:00:00	174	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1781	Aguardando Decisões sobre o tema para requerer a reconsideração	20223-12-20 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1782	Decisão determinando Suspensão (Ação Coletiva)	2023-09-05 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1783	Juntada Contrarrazões EDs ERJ	2023-09-18 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1784	Ação Distribuída	2023-08-30 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1785	Juntada EDs Autora	2023-09-06 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1786	Decisão - Eds não acolhidos	2023-09-28 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1787	Juntada Contestação	2023-08-31 00:00:00	175	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1788	Despacho pedindo mais documentos para análise JG	2023-09-21 00:00:00	176	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1789	Ação distribuída	2023-09-15 00:00:00	176	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1790	Juntada Petição Autora com Documentos para JG	2023-09-25 00:00:00	176	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1791	Ação distribuída	2023-10-06 00:00:00	177	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1792	Juntada Resposta à  Impugnação ERJ	2023-11-01 00:00:00	177	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1793	Decisão - Deferida JG	2023-10-11 00:00:00	177	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1794	Juntada Impugnação ERJ	2023-10-27 00:00:00	177	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1795	Ação distribuída	2023-10-06 00:00:00	178	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1796	Decisão - deferida JG | Determinada Intimação ERJ	2023-11-08 00:00:00	178	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1797	Juntada Impugnação ERJ	2023-11-30 00:00:00	178	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1798	Juntada Resposta à impugnação	2023-12-05 00:00:00	178	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1799	Decisão - Negada JG	2023-10-23 00:00:00	179	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1800	Juntada Petição Autora pedindo reconsideração JG	2023-11-01 00:00:00	179	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1801	Ação distribuída	2023-10-06 00:00:00	179	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1802	Ação distribuída	2023-10-06 00:00:00	180	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1803	Enviada Citação ERJ	2023-11-13 00:00:00	180	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1804	Juntada Réplica Autora	2024-01-11 00:00:00	180	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1805	Decisão - Deferida JG - Negada liminar	2023-11-10 00:00:00	180	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1806	Juntada de Contestação Estado	2023-12-28 00:00:00	180	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1807	Despacho - deferida JG - Determinada intimação ERJ	2023-12-02 00:00:00	181	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1808	Ação distribuída	2023-11-24 00:00:00	181	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1809	Juntada Petição Estado Impugnando 	2023-12-27 00:00:00	181	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1810	Expedida Intimação ERJ	2023-12-06 00:00:00	181	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1811	Despacho -   Solicitando liquidação do valor	2023-11-29 00:00:00	182	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1812	Ação Distribuída	2023-11-24 00:00:00	182	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1813	Petição informando de necessidade de liquidação ao final	2023-12-01 00:00:00	182	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1814	Ação Distribuída	2023-11-28 00:00:00	183	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1815	Decisão - Deferida JG e determinada Intimação ERJ	2023-12-07 00:00:00	183	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1816	Expedido mandado de citação do Estado	2023-12-13 00:00:00	183	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1817	Expedida Citação Estado	2023-12-13 00:00:00	184	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1818	Juntada Réplica Autora	2024-01-11 00:00:00	184	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1819	Juntada Contestação ERJ	2024-01-10 00:00:00	184	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1820	Decisão - Deferida JG - Determinada Citação ERJ	2023-12-06 00:00:00	184	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1821	Ação Distribuída	2023-11-29 00:00:00	184	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1822	Decisão concedendo JG e não concedendo liminar	2023-12-15 00:00:00	185	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1823	Expedida citação do Estado	2023-12-18 00:00:00	185	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1824	Autos conclusos	2023-12-05 00:00:00	185	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1825	Ação Distribuída	2023-12-04 00:00:00	185	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1826	Ação Distribuída	2023-12-04 00:00:00	186	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1827	Decisão - Deferida JG - Determinada Intimação ERJ	2023-12-07 00:00:00	186	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1828	Mandado de Citação ERJ expedido	2023-12-07 00:00:00	186	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1829	Ação Distribuída	2023-12-04 00:00:00	187	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1830	Despacho solicitando mais documentos sobre JG	2023-12-12 00:00:00	187	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1831	Autos conclusos	2023-12-07 00:00:00	187	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1832	Decisão - Concedida JG - Determinada Intimação ERJ	2023-12-06 00:00:00	188	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1833	Expedido Mandado de Citação	2023-12-13 00:00:00	188	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1834	Ação Distribuída	2023-12-04 00:00:00	188	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1835	Expedido Mandado de Citação ERJ	2023-11-13 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1836	Juntada Emenda	2023-05-30 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1837	Decisão Determinando Suspensão (Ação Coletiva)	2023-07-18 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1838	Decisão acolhendo EDS - JG Deferida - Liminar Indeferida - determinação de verificação de paridade	2023-11-08 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1839	Oposição EDs Autora	2023-07-24 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1840	Ação Distribuída	2023-04-20 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1841	Despacho determinando Emenda (Valor Ação)	2023-05-15 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1842	Autos Conclusos	2023-10-26 00:00:00	189	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1843	Juntada Réplica Autora	2024-01-11 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1844	Decisão - Deferida JG - Determinada juntada de calculos	2023-10-24 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1845	Ação Distribuída	2023-10-19 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1846	Decisão - Recebida a emenda à inicial - indeferida Tutela - Determinada intimação ERJ	2023-10-31 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1847	Autos conclusos	2023-10-19 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1848	Enviada citação ao ERJ	2023-11-06 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1849	Juntada Contestação ERJ	2024-01-09 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1850	Autos conclusos	2023-10-27 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
1851	Petição Autora juntada com cálculos	2023-10-25 00:00:00	190	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
\.


--
-- TOC entry 4915 (class 0 OID 60246)
-- Dependencies: 221
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.person (person_id, nome_completo, email, cpf, rg, estado_civil, data_nascimento, profissao, telefone, vivo, representante, id_funcional, nacionalidade, observacoes, user_id, address_id, created_on, modified_on) FROM stdin;
1	Ana Cristina Vieira Cabral	\N	002.180.347-17	08.883.466-8	Divorciada	1973-01-16 00:00:00	Professora	21 99156-6677	f	\N	4272476-7	Brasileira	\N	3	1	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
2	Ana Lucia de Barros	\N	000.358.187-00	33593S040	Casada	1964-11-02 00:00:00	Aposentada	21 96658-8705	f	\N	3837701-2	Brasileira	\N	4	2	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
3	Ana Maria Caffaro Machado	\N	514.873.147-87	80.788-965-4	Casada	1948-08-12 00:00:00	Aposentada	21 98850-4812	f	\N	714787-2	Brasileira	\N	5	3	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
4	Angela Maria Domingues da Venda	\N	414.102.037-00	81.263.841-9	Divorciada	1951-10-02 00:00:00	Aposentada	21 98723-3454	f	\N	3828962-8	Brasileira	\N	6	4	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
5	Angela Vieira Baptista	\N	325.977.897-72	1410294	verificar	1954-02-06 00:00:00	Aposentada	21 98747-6119	f	\N	4047065-2	Brasileira	\N	7	5	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
6	Carinna Venda França	\N	079.845.237-46	11521540-2	verificar	1979-03-13 00:00:00	Professora	21 96479-8674	f	\N	4219103-3	Brasileira	\N	8	6	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
7	Cecília Santana do Valle Marques	\N	445.440.227-20	30.102.361-0	Casada	1955-08-19 00:00:00	Aposentada	21 99373-8289	f	\N	3917177-9	Brasileira	\N	9	7	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
8	Celi Oliveira de Faria	\N	688.954.967-87	5.114.372	verificar	1948-07-30 00:00:00	Aposentada	21 99522-2464	f	\N	3864874-1	Brasileira	\N	10	8	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
9	Celia Maria da Costa Meireles	\N	324.363.357-53	81.266.285-6	verificar	1952-02-24 00:00:00	Aposentada	21 98714-1779	f	\N	4102716-7	Brasileira	\N	11	9	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
10	Daise Cristina Ribeiro de Carvalho	\N	920.466.737-00	06274529-4	Casada	1962-07-12 00:00:00	Professora	MeA	f	\N	3871844-8	Brasileira	\N	12	10	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
11	Denise Crivelario da Silva	\N	601.556.557-87	06518735-3	Solteira	1959-06-08 00:00:00	Aposentada	MeA	f	\N	3850852-4	Brasileira	\N	13	11	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
12	Dirce Midori Matsumoto	\N	745.955.047-72	08188296-1	verificar	1956-12-13 00:00:00	Aposentada	21 99672-8354	f	\N	3877755-0	Brasileira	\N	14	12	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
13	Diva Maria de Oliveira Cordeiro	\N	278.215.367-68	12265353-8	Casada	1947-06-25 00:00:00	Aposentada	21 99772-0366	f	\N	574757-0	Brasileira	\N	15	13	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
14	Eleane Platner Cezario de Souza	\N	358.421.807-15	81.366.437-2	verificar	1952-06-28 00:00:00	Aposentada	21 98411-7050	f	\N	695761-7	Brasileira	\N	16	14	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
15	Elen Sonia C Campos	\N	186.453.217-34	02300608-3	verificar	1948-10-23 00:00:00	Aposentada	\N	f	\N	549372-2	Brasileira	Geisa: 1 (281) 818-4060	17	15	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
16	Else Maria de Moraes Varela Abreu	\N	391.259.127-04	810494971	verificar	1952-01-26 00:00:00	Aposentada	21 99624-9271	f	\N	658405-5	Brasileira	\N	18	16	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
17	Emilse Neici Marinho Martins	\N	391.676.747-04	05152542-6	Viúva	1955-09-22 00:00:00	Aposentada	21 99508-0933	f	\N	3804025-5	Brasileira	\N	19	17	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
18	Erica Pereira da Silva Ramos Barbosa	\N	902.239.187-68	07.459.102-5	Casada	1966-06-24 00:00:00	Aposentada	21 97017-1680	f	\N	4123345-0	Brasileira	\N	20	18	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
19	Eva Maria Teixeira de Abreu	\N	573.571.377-91	80669116-8	Viúva	1945-05-04 00:00:00	Aposentada	21 97522-8039	f	\N	3865845-3	Brasileira	\N	21	19	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
20	Geni Soares Jardim	\N	829.725.327-91	04.527.518-7	verificar	1959-07-29 00:00:00	Aposentada	21 99865-9556	f	\N	3804216-9	Brasileira	\N	22	20	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
21	Gloria Maria Neto Miranda	\N	390.753.467-00	10429036-6	verificar	1952-09-05 00:00:00	Aposentada	21 99852-0879	f	\N	3802892-1	Brasileira	\N	23	21	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
22	Graziele Cancio Siqueira	\N	038.795.067-28	98598873	Solteira	1976-09-30 00:00:00	Professora	21 99766-1444	f	\N	4200709-7	Brasileira	\N	24	22	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
23	Heloisa Maria Souto Teixeira	\N	572.830.527-04	03.828.249-7	verificar	1945-06-18 00:00:00	Aposentada	21 99655-1099	f	\N	1823628-6	Brasileira	\N	25	23	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
24	Isaura Maria Siqueira Martins	\N	000.983.877-58	05.598.054-4	Casada	1960-03-06 00:00:00	Aposentada	21 97125-0430	f	\N	3802744-5	Brasileira	\N	26	24	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
25	Ivone Cardoso de Andrade	\N	090.466.037-09	24.868.834-3	Casada	1936-08-09 00:00:00	Aposentada	MeA	f	\N	536163-0	Brasileira	\N	27	25	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
26	Ivonete Maria de Oliveira Silva	\N	494.250.307-82	81.296.358-5	verificar	1953-03-31 00:00:00	Aposentada	2199793-2218	f	\N	3804085-9	Brasileira	\N	28	26	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
27	Jadinéa Platner Cezario	\N	883.248.007-78	73829061	Solteira	1966-04-14 00:00:00	Professora	21 98607-8082	f	\N	3927206-0	Brasileira	\N	29	27	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
28	Janete Pinto Rodrigues	\N	641.952.017-72	81421304-7	Solteira	1955-05-21 00:00:00	Aposentada	21 98952-7410	f	\N	3971662-7	Brasileira	\N	30	28	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
29	Jorge Solano de Andrade	\N	107.489.977-68	01.027.577-4	Casado	1933-03-05 00:00:00	Aposentado	MeA	f	\N	4058196-9	Brasileiro	\N	31	29	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
30	Juliana Teixeira Ismerio	\N	971.694.207-91	07.174.649-9	Divorciada	1966-09-21 00:00:00	Aposentada	22 98123-4340	f	\N	3846895-6	Brasileira	\N	32	30	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
31	Lais Moreira Nogueira	\N	117.599.827-33	21.778.153-3	Divorciada	1988-08-06 00:00:00	Professora	MeA	f	\N	5075911-6	Brasileira	\N	33	31	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
32	Lídia Lima	\N	776.348.537-04	06226438-7	Solteira	1959-01-20 00:00:00	Professora	21 99215-9005	f	\N	38593394	Brasileira	\N	34	32	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
33	Lillian Brigitte Ligthart Werner	\N	641.097.207-53	12675676-6	Solteira	1958-09-26 00:00:00	Aposentada	21 97200-2819	f	\N	4071974-0	Brasileira	\N	35	33	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
34	Lourdes Maria Thurler Teixeira	\N	617.389.137-91	03.980.176-6	Solteira	1958-10-28 00:00:00	Aposentada	21 98012-1626	f	\N	3971655-4	Brasileira	\N	36	34	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
35	Lucilea Novaes Freire Baltazar	\N	573.178.447-72	05.206.325-2	Casada	1959-06-30 00:00:00	Aposentada	21 99776-8312	f	\N	3804124-3	Brasileira	\N	37	35	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
36	Márcia Mendes Fernandes	\N	452.136.077-72	81.403.542-4	verificar	1955-01-21 00:00:00	Aposentada	21 99923-0053	f	\N	4047084-9	Brasileira	\N	38	36	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
37	Marco Antonio Braga Abdalah	\N	736.775.907-87	05.677.265-0	Viúva	1962-03-09 00:00:00	Aposentada	\N	f	\N	4344491-1	Brasileira	Yara di Giorgio: 21 99120-5659	39	37	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
38	Maria Apparecida Bragança Mello Coelho	\N	053.631.287-72	01.309.022-0	Viúva	1937-05-30 00:00:00	Aposentada	MeA	f	\N	736894-1	Brasileira	\N	40	38	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
39	Maria da Conceição dos Santos Muniz da Silva	\N	481.653.367-20	11.149.952-1	Casada	1952-10-01 00:00:00	Professora	21 98381-0585	f	\N	3863282-9	Brasileira	\N	41	39	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
40	Sonia Alencar	\N	422.128.557-53	11288379-8	separada	1950-12-24 00:00:00	aposentada	21 99982-5011	f	\N	919053-8	brasileira	\N	42	40	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
41	Maria de Fatima Teles Stancato	\N	620.156.807-72	05481077-5	Casada	1960-07-01 00:00:00	Aposentada	21 97262-5113	f	\N	3872407-3	Brasileira	\N	43	41	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
42	Maria Hortencia Muniz de Andrade	\N	517.543.987-15	81139326-3	Verificar	1952-10-01 00:00:00	Aposentada	21 96454-4294	f	\N	3804105-7	Brasileira	\N	44	42	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
43	Maria Ligia de Bragança Soares	\N	415.065.707-63	80893114-1	Casada	1942-07-22 00:00:00	Aposentada	21 99374-0995	f	\N	3886344-8	Brasileira	\N	45	43	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
44	Maria Terezinha de Souza Vianna	\N	415.066.007-78	81.244.399-2	Viúva	1947-02-18 00:00:00	Aposentada	21 97756-1111	f	\N	3872295-0	Brasileira	\N	46	44	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
45	Marilene Luiza Celino Rodrigues	\N	391.528.637-00	81.271.518-3	Solteira	1948-06-23 00:00:00	Aposentada	21 98959-5348	f	\N	3814632-0	Brasileira	\N	47	45	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
46	Mariza Rodrigues Dantas	\N	003.095.567-08	81.172.074-7	Viúva	1952-02-10 00:00:00	Aposentada	21 98792-2888	f	\N	3520944-5	Brasileira	\N	48	46	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
47	Maria Rosa Lopez Cid	\N	002.622.277-97	06526413-7	Solteira	1965-03-31 00:00:00	Aposentada	21 98410-0073	f	\N	3881316-5	Brasileira	\N	49	47	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
48	Marize Tostes Coimbra Jordão	\N	491.170.417-91	81.444.881-7	Divorciada	1957-06-17 00:00:00	Aposentada	21 98585-1902	f	\N	4058875-0	Brasileira	\N	50	48	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
49	Marli Pires da Rocha	\N	010.315.687-94	07.719.295-3	Verificar	1963-08-29 00:00:00	Aposentada	21 99507-1812	f	\N	3824762-3	Brasileira	\N	51	49	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
50	Marly de Jesus Martins	\N	391.537.707-49	81.098.053-2	Verificar	1947-03-05 00:00:00	Aposentada	21 99348-5956	f	\N	3802901-4	Brasileira	\N	52	50	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
51	Marta Helena Olivier de Paula Siqueira	\N	690.669.657-20	101098770	Casada	1946-05-09 00:00:00	Aposentada	\N	f	\N	759934-0	Brasileira	Patricia: 21 99912-0948	53	51	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
52	Martha Lopes Cardoso	\N	285.155.017-91	12.675.490-2	Viúva	1948-08-10 00:00:00	Aposentada	\N	f	\N	633577-2	Brasileira	André: Verificar	54	52	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
53	Monica Silva Pereira dos Santos	\N	797.061.267-91	06825051-3	Casada	1964-05-02 00:00:00	Professora	MeA	f	\N	4373346-8	Brasileira	\N	55	53	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
54	Neuman Mencari Leal de Souza	\N	721.442.387-15	29310903-9	Verificar	1950-04-06 00:00:00	Aposentada	21 99841-0977	f	\N	3815296-7	Brasileira	\N	56	54	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
55	Neuza Carvalho Caffaro	\N	354.916.047-04	80.967.110-0	Verificar	1950-05-22 00:00:00	Aposentada	21 96976-7520	f	\N	4097987-3	Brasileira	\N	57	55	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
56	Neuza Maria Cardoso Soares	\N	516.615.437-15	04436208-5	Casada	1958-12-09 00:00:00	Aposentada	21 99878-4508	f	\N	3829026-0	Brasileira	\N	58	56	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
57	Nilce Vania da Silva Lopes	\N	789.986.207-82	Verificar	Verificar	1962-10-11 00:00:00	Professora	Verificar	f	\N	3576522-4	Brasileira	\N	59	57	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
58	Nilma da Silva Lopes Araújo	\N	414.995.817-34	81282122-1	Verificar	1954-01-11 00:00:00	Aposentada	21 99889-2458	f	\N	4102746-9	Brasileira	\N	60	58	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
59	Regina Helena Ferro Cornado	\N	305.961.657-49	81.058.313-8	Viúva	1951-11-24 00:00:00	Aposentada	\N	f	\N	3879554-0	Brasileira	Thaiana: 21 98291-8972	61	59	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
60	Regina Rosari Mugayar Guedes	\N	501.390.967-87	82.504.872-1	Divorciada	1957-06-18 00:00:00	Aposentada	21 99217-5688	f	\N	4055406-6	Brasileira	\N	62	60	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
61	Robson Cardoso de Andrade	\N	741.418.217-72	05152486-6	Solteiro	1962-07-04 00:00:00	Professor	Verificar	f	\N	3851015-4	Brasileira	\N	63	61	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
62	Rosa Eli Machado de Santana	\N	325.972.667-53	24.823.055-9	Verificar	1952-09-07 00:00:00	Aposentada	21 98627-1663	f	\N	4046935-2	Brasileira	\N	64	62	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
63	Rosaine Olivier Tostes	\N	194.698.107-91	80926386-6	Separada	1950-04-09 00:00:00	Aposentada	21 99643-8877	f	\N	4053873-7	Brasileira	\N	65	63	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
64	Rosangela Mendes Martins Veloso	\N	641.736.907-20	130540180	Verificar	1957-08-30 00:00:00	Aposentada	21 99997-0663	f	\N	3813963-4	Brasileira	\N	66	64	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
65	Roseli Medeiros Solidade Dos Santos	\N	681.496.207-10	07.893.147-2	Casada	1969-01-07 00:00:00	Professora	Verificar	f	\N	4205179-7	Brasileira	\N	67	65	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
66	Rosilea Sabino dos Santos	\N	012.749.987-35	88448113	Solteira	1970-08-24 00:00:00	Aposentada	21 99618-7744	f	\N	3824801-8	Brasileira	\N	68	66	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
67	Rosilene Novaes Freire	\N	804.073.237-91	05.892.525-6	Verificar	1962-02-11 00:00:00	Aposentada	21 99659-5378	f	\N	3804195-2	Brasileira	\N	69	67	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
68	Rosimeri Sabino dos Santos	\N	517.111.847-72	54623491	Solteira	1959-07-11 00:00:00	Aposentada	21 98122-8398	f	\N	3825528-6	Brasileira	\N	70	68	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
69	Sandra Regina Mello de Moura	\N	305.550.437-20	25.757.621-5	Viúva	1952-08-10 00:00:00	Aposentada	21 99845-9766	f	\N	247607-0	Brasileira	\N	71	69	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
70	Silvia Sandra Silva de Souza	\N	323.889.267-34	011.426.262-9	Verificar	1954-03-20 00:00:00	Aposentada	21 98803-1402	f	\N	3804006-9	Brasileira	\N	72	70	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
71	Solange da Silva Chaves de Moura	\N	733.289.287-15	80.799.414-0	Verificar	1947-10-31 00:00:00	Aposentada	21 98803-1402	f	\N	4102760-4	Brasileira	\N	73	71	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
72	Sonia Maria de Oliveira Lomba	\N	446.419.077-49	04.978.864-9	Divorciada	1955-06-29 00:00:00	Aposentada	19 99361-3815	f	\N	3877523-9	Brasileira	\N	74	72	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
73	Sueli Pimenta Vieira	\N	366.187.727-53	81.400.960-1	Viúva	1954-06-21 00:00:00	Aposentada	21 99139-9155	f	\N	3868317-2	Brasileira	\N	75	73	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
74	Sueli Talarico Lopes	\N	079.887.727-83	81.078.908-1	Verificar	1950-06-03 00:00:00	Aposentada	21 97566-4541	f	\N	3875167-4	Brasileira	\N	76	74	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
75	Suzana Francesconi Soares da Costa	\N	696.210.557-68	5.062.022	Casada	1957-11-22 00:00:00	Aposentada	21 99715-3453	f	\N	3563832-0	Brasileira	\N	77	75	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
76	Telma Regina de Medeiros Silva	\N	422.493.927-49	83.018.317-4	Verificar	1956-09-23 00:00:00	Aposentada	22 98817-1016	f	\N	3747519-3	Brasileira	\N	78	76	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
77	Terezinha Tostes Lopes	\N	640.019.467-34	04932219-1	Solteira	1961-05-01 00:00:00	Aposentada	22 99841-4933	f	\N	4006636-3	Brasileira	\N	79	77	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
78	Valdice Ruth de Souza e Coelho	\N	641.248.277-68	912022142	Verificar	1956-10-27 00:00:00	Aposentada	22 99222-7147	f	\N	3749094-0	Brasileira	\N	80	78	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
79	Vânia Lúcia de Bragança Teixeira	\N	281.984.507-00	20.606.699-5	Divorciada	1949-12-29 00:00:00	Aposentada	\N	f	\N	3927316-4	Brasileira	Letícia e Gustavo: Letícia 21 99605-0808 & Gustavo 21 99605-0909	81	79	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
80	Vera Lucia de Oliveira Correa	\N	247.442.047-91	81.019.905-9	Verificar	1951-05-08 00:00:00	Aposentada	21 99771-6410	f	\N	3857421-7	Brasileira	\N	82	80	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
81	Vera Lucia dos Santos Sant anna	\N	522.286.067-15	13190029-2	Verificar	1951-05-21 00:00:00	Aposentada	21 98192-8643	f	\N	3868423-3	Brasileira	\N	83	81	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
82	Vera Lucia Mendes da Silva	\N	380.638.327-87	83.017.500-6	Verificar	1956-07-25 00:00:00	Aposentada	21 98562-1525	f	\N	4046942-5	Brasileira	\N	84	82	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
83	Vitória Régia Coelho Mello	\N	194.032.167-00	02815451-6	Divorciada	1949-02-14 00:00:00	Aposentada	1 (816) 699-1254	f	\N	4117833-5	Brasileira	\N	85	83	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
84	Zelda Cunha Camacho	\N	592.456.977-20	05.462.201-4	Casada	1940-06-11 00:00:00	Aposentada	21 98812-2820	f	\N	338895-6	Brasileira	\N	86	84	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
85	Zelia Maria dos Santos	\N	494.219.137-87	81248603-3	Casada	1953-06-25 00:00:00	Aposentada	21 97654-9391	f	\N	3804073-5	Brasileira	\N	87	85	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
86	Jucelma Hermsdorff Vellozo Gaichi	\N	499.659.137-49	05139654-7	casada	1957-08-11 00:00:00	aposentada	21 99781-8786	f	\N	4058581-6	brasileira	\N	88	86	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
87	Iracema Bueno Santos	\N	458.189.757-20	81309381-2	divorciada	1950-10-28 00:00:00	aposentada	\N	f	\N	3885802-9	brasileira	Érica Filha: 21 98303-7664	89	87	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
88	Monica Alves Sally	\N	913.989.837-72	07.051.701-6	divorciada	1966-06-14 00:00:00	Professora	21 98644-2819	f	\N	4213543-5	brasileira	\N	90	88	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
89	Emilly Marinho Martins	\N	127.459.837-09	23.117.615-7	viúva	1989-03-21 00:00:00	professora	21 99504-9160	f	\N	4255298-2	brasileira	\N	91	89	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
90	Maria de Fátima Ramos Baldi da Silva	\N	022.211.647-17	27.489.182-9	solteira	1957-05-15 00:00:00	professora	verificar	f	\N	4325939-1	brasileira	\N	92	90	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
91	Narda El-Amme Souza e Silva	\N	949.607.637-87	05152934-5	solteira	1961-05-27 00:00:00	professora aposentada	21 98874-9480	f	\N	3876934-4	brasileira	\N	93	91	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
92	Teresinha Maria de Jesus Mello Ventura	\N	390.330.317-87	27.265	casada	1949-04-26 00:00:00	psicóloga	21 27191567	f	\N	657616-8	brasileira	\N	94	92	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
93	Regina Coeli Codeço	\N	621.902.457-53	05.089.069-8	divorciada	1958-03-10 00:00:00	professora	21 27154295	f	\N	4054077-4	brasileira	\N	95	93	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
94	Neuza Maria Cabral	\N	514.402.537-49	11.949.272-6	viúva	1954-06-23 00:00:00	aposentada	21 99674-1156	f	\N	892714-6	brasileira	\N	96	94	2024-02-06 11:56:25.287265	2024-02-06 11:56:25.287265
\.


--
-- TOC entry 4919 (class 0 OID 60291)
-- Dependencies: 225
-- Data for Name: process; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.process (process_id, numero, comarca, vara, sistema, valor, previsao_honorarios_contratuais, previsao_honorarios_sucumbenciais, distribuido_em, tipo_processo, status, matriculation_id, created_on, modified_on) FROM stdin;
1	0000407-29.2022.8.19.0002	Niterói	9ª Vara Cível	TJRJ	11898.43	2379.69	\N	2022-01-11 00:00:00	NOVAESCOLA	Suspenso NE	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
2	0001164-92.2023.8.19.0000	Capital	Orgão Especial	TJRJ	36269.04	7253.81	\N	2023-01-19 00:00:00	INTERNIVEIS	Ag. Contador	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
3	0001182-16.2023.8.19.0000	Capital	Orgão Especial	TJRJ	37720.54	7544.11	\N	2023-01-19 00:00:00	INTERNIVEIS	Com Depósito	54	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
4	0001185-68.2023.8.19.0000	Capital	Orgão Especial	TJRJ	42345.73	8469.15	\N	2023-09-11 00:00:00	INTERNIVEIS	Ag. Contador	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
5	0002476-06.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-01-24 00:00:00	INTERNIVEIS	RPV Enviado	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
6	0002477-88.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-01-24 00:00:00	INTERNIVEIS	Com Depósito	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
7	0002480-43.2023.8.19.0000	Capital	Orgão Especial	TJRJ	62288.01	12457.6	\N	2023-01-24 00:00:00	INTERNIVEIS	Ag. Envio RPV	70	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
8	0002857-14.2023.8.19.0000	Capital	Orgão Especial	TJRJ	42781.68	8556.34	\N	2023-01-24 00:00:00	INTERNIVEIS	Com Depósito	90	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
9	0004858-69.2023.8.19.0000	Capital	Orgão Especial	TJRJ	49657.06	9931.41	\N	2023-01-31 00:00:00	INTERNIVEIS	Com Depósito	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
10	0004859-54.2023.8.19.0000	Capital	Orgão Especial	TJRJ	43738.98	8747.8	\N	2023-01-31 00:00:00	INTERNIVEIS	Ag. Contador	84	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
11	0004860-39.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-01-31 00:00:00	INTERNIVEIS	Ag. Envio RPV	91	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
12	0004861-24.2023.8.19.0000	Capital	Orgão Especial	TJRJ	34952.63	6990.53	\N	2023-01-31 00:00:00	INTERNIVEIS	RPV enviado	92	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
13	0004862-09.2023.8.19.0000	Capital	Orgão Especial	TJRJ	48521.16	9704.23	\N	2023-01-31 00:00:00	INTERNIVEIS	Com Depósito	47	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
14	0004863-91.2023.8.19.0000	Capital	Orgão Especial	TJRJ	34952.63	6990.53	\N	2023-03-15 00:00:00	INTERNIVEIS	RPV Enviado	48	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
15	0004922-79.2023.8.19.0000	Capital	Orgão Especial	TJRJ	42781.68	8556.34	\N	2023-01-31 00:00:00	INTERNIVEIS	Com Depósito	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
16	0004939-18.2023.8.19.0000	Capital	Orgão Especial	TJRJ	19158.81	3831.76	\N	2023-01-31 00:00:00	INTERNIVEIS	Com Depósito	68	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
17	0004957-39.2023.8.19.0000	Capital	Orgão Especial	TJRJ	37023.57	7404.71	\N	2023-01-31 00:00:00	INTERNIVEIS	Com Depósito	13	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
18	0004961-76.2023.8.19.0000	Capital	Orgão Especial	TJRJ	37023.57	7404.71	\N	2023-01-31 00:00:00	INTERNIVEIS	Com Depósito	12	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
19	0005426-85.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-02-02 00:00:00	INTERNIVEIS	Ag. Homologação	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
20	0005429-40.2023.8.19.0000	Capital	Orgão Especial	TJRJ	56265.12	11253.02	\N	2023-02-02 00:00:00	INTERNIVEIS	Com Depósito	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
21	0005973-28.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-05-16 00:00:00	INTERNIVEIS	Com Depósito	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
22	0006383-86.2023.8.19.0000	Capital	Orgão Especial	TJRJ	49407.45	9881.49	\N	2023-02-09 00:00:00	INTERNIVEIS	RPV enviado	74	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
23	0006388-11.2023.8.19.0000	Capital	Orgão Especial	TJRJ	47642.68	9528.54	\N	2023-02-08 00:00:00	INTERNIVEIS	Ag. Homologação	75	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
24	0006609-91.2023.8.19.0000	Capital	Orgão Especial	TJRJ	44158.94	8831.79	\N	2023-02-09 00:00:00	INTERNIVEIS	Ag. Homologação	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
25	0006620-23.2023.8.19.0000	Capital	Orgão Especial	TJRJ	49500.04	9900.01	\N	2023-02-08 00:00:00	INTERNIVEIS	Ag. Homologação	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
26	0006807-31.2023.8.19.0000	Capital	Orgão Especial	TJRJ	61186.73	12237.35	\N	2023-02-09 00:00:00	INTERNIVEIS	Com Depósito	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
27	0006867-67.2021.8.19.0034	Miracema	2ª Vara Cível	TJRJ	23796.86	4759.37	\N	2021-10-28 00:00:00	NOVAESCOLA	Mandado de pgto	100	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
28	0007154-65.2021.8.19.0087	São Gonçalo	1ª Vara Cível	TJRJ	17847.64	3569.53	\N	2021-10-14 00:00:00	NOVAESCOLA	Liquidação	71	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
29	0007239-07.2021.8.19.0037	Nova Friburgo	2ª Vara Cível	TJRJ	23796.86	4759.37	\N	2021-09-27 00:00:00	NOVAESCOLA	Suspenso Ofício	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
30	0007456-50.2021.8.19.0037	Nova Friburgo	2ª Vara Cível	TJRJ	23796.86	4759.37	\N	2021-10-05 00:00:00	NOVAESCOLA	Suspenso NE	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
31	0007660-06.2021.8.19.0034	Miracema	2ª Vara Cível	TJRJ	22952.41	4590.48	\N	2021-12-06 00:00:00	NOVAESCOLA	Mandado de pgto	99	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
32	0008198-21.2023.8.19.0000	Capital	Orgão Especial	TJRJ	42781.68	8556.34	\N	2023-02-14 00:00:00	INTERNIVEIS	Ag. Envio RPV	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
33	0008767-50.2022.8.19.0002	Niterói	5ª Vara Cível	PJE	63370.75	12674.15	\N	2022-04-04 00:00:00	INTERNIVEIS	Em andamento	102	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
34	0009620-31.2023.8.19.0000	Capital	Orgão Especial	TJRJ	24144.61	4828.92	\N	2023-02-15 00:00:00	INTERNIVEIS	Ag. Envio RPV	87	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
35	0009661-95.2023.8.19.0000	Capital	Orgão Especial	TJRJ	29919.74	5983.95	\N	2023-02-15 00:00:00	INTERNIVEIS	Ag. Homologação	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
36	0010411-97.2023.8.19.0000	Capital	Orgão Especial	TJRJ	24144.61	4828.92	\N	2023-02-16 00:00:00	INTERNIVEIS	RPV Enviado	33	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
37	0012194-27.2023.8.19.0000	Capital	Orgão Especial	TJRJ	34624.68	6924.94	\N	2023-03-01 00:00:00	INTERNIVEIS	Ag. Envio contador	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
38	0012492-19.2023.8.19.0000	Capital	Orgão Especial	TJRJ	33217.63	6643.53	\N	2023-03-01 00:00:00	INTERNIVEIS	Ag. Contador	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
39	0012950-36.2023.8.19.0000	Capital	Orgão Especial	TJRJ	47642.68	9528.54	\N	2023-03-02 00:00:00	INTERNIVEIS	Ag. Homologação	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
40	0012951-21.2023.8.19.0000	Capital	Orgão Especial	TJRJ	23458.91	4691.78	\N	2023-03-02 00:00:00	INTERNIVEIS	Ag. Envio RPV	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
41	0013631-06.2023.8.19.0000	Capital	Orgão Especial	TJRJ	42781.68	8556.34	\N	2023-03-03 00:00:00	INTERNIVEIS	Ag. Envio RPV	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
42	0013632-88.2023.8.19.0000	Capital	Orgão Especial	TJRJ	42395.28	8479.06	\N	2023-03-03 00:00:00	INTERNIVEIS	Com Depósito	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
43	0015309-56.2023.8.19.0000	Capital	Orgão Especial	TJRJ	24132.35	4826.47	\N	2023-03-10 00:00:00	INTERNIVEIS	Ag. Envio RPV	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
44	0015316-48.2023.8.19.0000	Capital	Orgão Especial	TJRJ	21526.75	4305.35	\N	2023-03-10 00:00:00	INTERNIVEIS	Ag. Contador	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
45	0016774-03.2023.8.19.0000	Capital	Orgão Especial	TJRJ	36854.67	7370.93	\N	2023-03-15 00:00:00	INTERNIVEIS	Ag. Envio RPV	46	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
46	0018158-98.2023.8.19.0000	Capital	Orgão Especial	TJRJ	34413.08	6882.62	\N	2023-03-21 00:00:00	INTERNIVEIS	Desistência	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
47	0018724-47.2023.8.19.0000	Capital	Orgão Especial	TJRJ	23458.91	4691.78	\N	2023-03-23 00:00:00	INTERNIVEIS	Ag. Homologação	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
48	0018916-77.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-03-22 00:00:00	INTERNIVEIS	Ag. Homologação	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
49	0019319-46.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-03-23 00:00:00	INTERNIVEIS	Ag. Homologação	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
50	0019673-68.2023.8.19.0001	Capital	8ª Vara de Fazenda	PJE	28283.18	5656.64	\N	2023-02-15 00:00:00	NOVAESCOLA	Apelação	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
51	0022216-47.2023.8.19.0000	Capital	Orgão Especial	TJRJ	41609.41	8321.88	\N	2023-03-31 00:00:00	INTERNIVEIS	Ag. Envio contador	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
52	0022305-29.2021.8.19.0004	São Gonçalo	8ª Vara Cível	TJRJ	17847.64	3569.53	\N	2021-11-25 00:00:00	NOVAESCOLA	Ag. Contador	57	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
53	0022309-66.2021.8.19.0004	São Gonçalo	7ª Vara Cível	TJRJ	17847.64	3569.53	\N	2021-11-25 00:00:00	NOVAESCOLA	Liquidação	58	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
54	0022735-22.2023.8.19.0000	Capital	Orgão Especial	TJRJ	41609.41	8321.88	\N	2023-04-04 00:00:00	INTERNIVEIS	Ag. Homologação	23	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
55	0027473-87.2022.8.19.0000	Capital	Orgão Especial	TJRJ	50156.01	10031.2	\N	2022-04-28 00:00:00	INTERNIVEIS	Em andamento	110	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
56	0039573-96.2021.8.19.0004	São Gonçalo	2ª Vara Cível	TJRJ	23796.86	4759.37	\N	2021-11-05 00:00:00	NOVAESCOLA	Ag. Contador	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
57	0041542-55.2021.8.19.0002	Niterói	7ª Vara Cível	TJRJ	29746.07	5949.21	\N	2021-10-05 00:00:00	NOVAESCOLA	Suspenso NE	61	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
58	0041745-17.2021.8.19.0002	Niterói	7ª Vara Cível	TJRJ	29746.07	5949.21	\N	2021-10-07 00:00:00	NOVAESCOLA	Suspenso NE	62	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
59	0044235-12.2021.8.19.0002	Niterói	7ª Vara Cível	TJRJ	23796.86	4759.37	\N	2021-10-28 00:00:00	NOVAESCOLA	Suspenso NE	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
60	0044655-17.2021.8.19.0002	Niterói	4ª Vara Cível	TJRJ	17847.64	3569.53	\N	2021-11-01 00:00:00	NOVAESCOLA	Ag. Homologação	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
61	0045018-04.2021.8.19.0002	Niterói	7ª Vara Cível	TJRJ	17847.64	2677.15	\N	2021-11-05 00:00:00	NOVAESCOLA	Suspenso NE	28	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
62	0045020-71.2021.8.19.0002	Niterói	6ª Vara Cível	TJRJ	23796.86	4759.37	\N	2021-11-05 00:00:00	NOVAESCOLA	Suspenso NE	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
63	0048769-96.2021.8.19.0002	Niterói	7ª Vara Cível	TJRJ	11898.43	2379.69	\N	2021-12-01 00:00:00	NOVAESCOLA	Suspenso NE	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
64	0059197-75.2023.8.19.0000	Capital	Orgão Especial	TJRJ	37005.04	7401.01	\N	2023-07-26 00:00:00	INTERNIVEIS	Ag. Homologação	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
65	0061175-87.2023.8.19.0000	Capital	Orgão Especial	TJRJ	36818.85	7363.77	\N	2023-08-02 00:00:00	INTERNIVEIS	Ag. Homologação	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
66	0062671-54.2023.8.19.0000	Capital	Orgão Especial	TJRJ	10828	2165.6	\N	2023-08-07 00:00:00	INTERNIVEIS	Ag. Homologação	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
67	0070345-83.2023.8.19.0000	Capital	Orgão Especial	TJRJ	24142.04	4828.41	\N	2023-09-01 00:00:00	INTERNIVEIS	Ag. Cls Inicial	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
68	0071023-98.2023.8.19.0000	Capital	Orgão Especial	TJRJ	33656.95	6731.39	\N	\N	INTERNIVEIS	Desistência	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
69	0075748-33.2023.8.19.0000	Capital	Órgão Especial	TJRJ	57562.81	11512.56	\N	2023-09-21 00:00:00	INTERNIVEIS	Ag. Envio RPV	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
70	0081822-06.2023.8.19.0000	Capital	Orgão Especial	TJRJ	24582.63	4916.53	\N	2023-10-05 00:00:00	INTERNIVEIS	Ag. Cls Inicial	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
71	0081825-58.2023.8.19.0000	Capital	Orgão Especial	TJRJ	20487.1	4097.42	\N	2023-10-05 00:00:00	INTERNIVEIS	Ag. ERJ	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
72	0085456-44.2022.8.19.0000	Capital	Orgão Especial	TJRJ	49678.56	9935.71	\N	2022-11-03 00:00:00	INTERNIVEIS	Ag. Envio RPV	18	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
73	0087520-27.2022.8.19.0000	Capital	Orgão Especial	TJRJ	31096.96	6219.39	\N	2022-11-09 00:00:00	INTERNIVEIS	RPV Enviado	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
74	0091009-09.2021.8.19.0000	Capital	Orgão Especial	TJRJ	34291.2	6858.24	\N	2021-12-03 00:00:00	INTERNIVEIS	Ag. Contador	31	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
75	0092545-21.2022.8.19.0000	Capital	Orgão Especial	TJRJ	49678.56	9935.71	\N	2022-12-01 00:00:00	INTERNIVEIS	RPV Enviado	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
76	0096595-56.2023.8.19.0000	Capital	Orgão Especial	TJRJ	47571.63	9514.33	\N	2023-11-24 00:00:00	INTERNIVEIS	Ag. Cls Inicial	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
77	0100547-40.2023.8.19.0001	Capital	8ª Vara de Fazenda	PJE	22160.38	4432.08	\N	2023-08-21 00:00:00	NOVAESCOLA	Réplica	93	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
78	0100885-17.2023.8.19.0000	Capital	Orgão Especial	TJRJ	50221.1	10044.22	\N	2023-04-11 00:00:00	INTERNIVEIS	Ag. Cls Inicial	51	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
79	0224451-68.2021.8.19.0001	Capital	5ª Vara de Fazenda	TJRJ	17847.64	3569.53	\N	2021-10-05 00:00:00	NOVAESCOLA	Suspenso NE	41	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
80	0225732-59.2021.8.19.0001	Capital	5ª Vara de Fazenda	TJRJ	17847.64	3569.53	\N	2021-10-07 00:00:00	NOVAESCOLA	Apelação	42	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
81	0800038-03.2023.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	28283.18	5656.64	\N	2023-01-09 00:00:00	NOVAESCOLA	Sentença	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
82	0800039-85.2023.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	144028.11	28805.62	\N	2023-01-09 00:00:00	PISO	Sentença	98	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
83	0800058-91.2023.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	21212.38	4242.48	\N	2023-01-10 00:00:00	NOVAESCOLA	Com cálculos contador	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
84	0800059-76.2023.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	142817.38	28563.48	\N	2023-01-10 00:00:00	PISO	Alegações Finais	97	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
85	0800895-50.2023.8.19.0087	São Gonçalo	3ª Vara Cível	PJE	21212.38	4242.48	\N	2023-01-23 00:00:00	NOVAESCOLA	Ag. Contador	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
86	0800896-35.2023.8.19.0087	São Gonçalo	4ª Vara Cível	PJE	55371.38	11074.28	\N	2023-01-23 00:00:00	PISO	Provas	69	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
87	0800899-87.2023.8.19.0087	São Gonçalo	5ª Vara Cível	PJE	47732.92	9546.58	\N	2023-01-23 00:00:00	PISO	Réplica	70	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
88	0801394-89.2023.8.19.0004	São Gonçalo	3ª Vara Cível	PJE	21212.38	4242.48	\N	2023-01-23 00:00:00	NOVAESCOLA	Ag. Contador	24	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
89	0801395-53.2023.8.19.0011	Cabo Frio	3ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-07 00:00:00	NOVAESCOLA	Com cálculos contador	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
90	0801396-38.2023.8.19.0011	Cabo Frio	3ª Vara Cível	PJE	152196.65	30439.33	\N	2023-02-07 00:00:00	PISO	EDs Suspensão	10	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
91	0801704-73.2022.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	161771.42	32354.28	\N	2022-08-08 00:00:00	PISO	Apelação	101	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
92	0802131-94.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	4895.17	979.03	\N	2023-03-23 00:00:00	NOVAESCOLA	Agravar	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
93	0802134-49.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	21212.38	4242.48	\N	2023-03-23 00:00:00	NOVAESCOLA	Agravar	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
94	0802136-33.2023.8.19.0031	Maricá	1ª Vara Cível	PJE	142178.77	28435.75	\N	2023-02-27 00:00:00	PISO	Ag. Citação	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
95	0802138-03.2023.8.19.0031	Maricá	1ª Vara Cível	PJE	14141.59	2828.32	\N	2023-02-27 00:00:00	NOVAESCOLA	Réplica	3	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
96	0802207-94.2022.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	39643.72	7928.74	\N	2022-09-16 00:00:00	PISO	Alegações Finais	109	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
97	0802209-64.2022.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	42286.64	8457.33	\N	2022-09-16 00:00:00	PISO	Apelação	108	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
98	0802330-49.2022.8.19.0037	Nova Friburgo	1ª Vara Cível	PJE	156552.99	31310.6	\N	2022-07-27 00:00:00	PISO	Provas	39	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
99	0802332-19.2022.8.19.0037	Nova Friburgo	2ª Vara Cível	PJE	156552.99	31310.6	\N	2022-07-27 00:00:00	PISO	Apelação	38	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
100	0802494-79.2023.8.19.0004	São Gonçalo	7ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-01 00:00:00	NOVAESCOLA	AI	107	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
101	0802499-04.2023.8.19.0004	São Gonçalo	6ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-01 00:00:00	NOVAESCOLA	Réplica	106	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
102	0802576-19.2023.8.19.0002	Niterói	7ª Vara Cível	PJE	21212.38	4242.48	\N	2023-01-31 00:00:00	NOVAESCOLA	Parâmetros	64	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
103	0802577-04.2023.8.19.0002	Niterói	10ª Vara Cível	PJE	61099.45	12219.89	\N	2023-01-31 00:00:00	PISO	EDs Suspensão	103	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
104	0802686-12.2023.8.19.0004	São Gonçalo	8ª Vara Cível	PJE	28283.18	5656.64	\N	2023-02-02 00:00:00	NOVAESCOLA	Ag. Contador	68	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
105	0803000-55.2023.8.19.0004	São Gonçalo	3ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-06 00:00:00	NOVAESCOLA	Apelação Carta	81	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
106	0803002-25.2023.8.19.0004	São Gonçalo	1ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-06 00:00:00	NOVAESCOLA	Provas	80	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
107	0803060-28.2023.8.19.0004	São Gonçalo	7ª Vara Cível	PJE	111981.06	22396.21	\N	2023-02-09 00:00:00	PISO	Provas	68	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
108	0803096-76.2023.8.19.0002	Niterói	7ª Vara Cível	PJE	129428.25	25885.65	\N	2023-02-02 00:00:00	PISO	EDs Suspensão	20	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
109	0803104-49.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	53003.67	10600.73	\N	2023-04-24 00:00:00	PISO	Provas	32	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
110	0803105-10.2022.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	13930.65	2786.13	\N	2022-11-09 00:00:00	NOVAESCOLA	Com cálculos contador	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
111	0803107-77.2022.8.19.0055	São Pedro da Aldeia	2ª Vara Cível	PJE	31722.75	6344.55	\N	2022-11-09 00:00:00	PISO	Apelação	96	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
112	0803120-07.2023.8.19.0002	Niterói	3ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-03 00:00:00	NOVAESCOLA	Réplica	26	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
113	0803250-90.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	61412.3	12282.46	\N	2023-04-28 00:00:00	PISO	Provas	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
114	0803253-45.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	21212.38	4242.48	\N	2023-04-28 00:00:00	NOVAESCOLA	Agravo Suspensão	16	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
115	0803272-51.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	56659.1	11331.82	\N	2023-04-28 00:00:00	PISO	Provas	36	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
116	0803283-80.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	56659.1	11331.82	\N	2023-04-28 00:00:00	PISO	Provas	37	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
117	0803398-08.2023.8.19.0002	Niterói	4ª Vara Cível	PJE	21212.38	4242.48	\N	2023-02-06 00:00:00	NOVAESCOLA	Com cálculos contador	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
118	0803417-14.2023.8.19.0002	Niterói	8ª Vara Cível	PJE	142817.38	28563.48	\N	2023-02-06 00:00:00	PISO	EDs Suspensão	11	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
119	0803458-74.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	23310.02	4662	\N	2023-05-08 00:00:00	INTERNIVEIS	Em andamento	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
120	0803459-59.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	21212.38	4242.48	\N	2023-05-08 00:00:00	NOVAESCOLA	Ag. Impugnação	79	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
121	0803545-28.2023.8.19.0004	São Gonçalo	2ª Vara Cível	PJE	43982.91	8796.58	\N	2023-02-10 00:00:00	PISO	Apelação competência	66	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
122	0804757-84.2023.8.19.0004	São Gonçalo	4ª Vara Cível	PJE	147123.43	29424.69	\N	2023-02-27 00:00:00	PISO	Provas	63	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
123	0804769-90.2022.8.19.0212	Niterói	9ª Vara Cível	PJE	19738.92	3947.78	\N	2022-08-25 00:00:00	PISO	Provas	9	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
124	0805057-46.2023.8.19.0004	São Gonçalo	3ª Vara Cível	PJE	28283.18	5656.64	\N	2023-03-01 00:00:00	NOVAESCOLA	Réplica	6	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
125	0805058-31.2023.8.19.0004	São Gonçalo	7ª Vara Cível	PJE	28283.18	5656.64	\N	2023-03-01 00:00:00	NOVAESCOLA	Valor Liquidado - AI ERJ	7	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
126	0805937-38.2023.8.19.0004	São Gonçalo	5ª Vara Cível	PJE	80603.27	16120.65	\N	2023-03-09 00:00:00	PISO	Provas	19	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
127	0806011-98.2023.8.19.0002	Niterói	9ª Vara Cível	PJE	21212.38	4242.48	\N	2023-03-03 00:00:00	NOVAESCOLA	Parâmetros	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
128	0806012-83.2023.8.19.0002	Niterói	7ª Vara Cível	PJE	8702.52	1740.5	\N	2023-03-03 00:00:00	NOVAESCOLA	Provas	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
129	0806013-68.2023.8.19.0002	Niterói	9ª Vara Cível	PJE	132293.36	26458.67	\N	2023-03-03 00:00:00	PISO	Provas	55	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
130	0806014-53.2023.8.19.0002	Niterói	10ª Vara Cível	PJE	141112.92	28222.58	\N	2023-03-06 00:00:00	PISO	Agravo Suspensão	56	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
131	0806015-38.2023.8.19.0002	Niterói	4ª Vara Cível	PJE	28283.18	5656.64	\N	2023-03-03 00:00:00	NOVAESCOLA	Ag. Impugnação	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
132	0806016-23.2023.8.19.0002	Niterói	1ª Vara Cível	PJE	180821.53	36164.31	\N	2023-03-03 00:00:00	PISO	Provas	94	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
133	0806936-94.2023.8.19.0002	Niterói	5ª Vara Cível	PJE	21212.38	4242.48	\N	2023-03-09 00:00:00	NOVAESCOLA	Agravo 2001	53	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
134	0807279-27.2022.8.19.0002	Niterói	2ª Vara Cível	PJE	34914.36	5237.15	\N	2022-05-23 00:00:00	PISO	Apelação	43	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
135	0807493-52.2022.8.19.0023	Itaboraí	2ª Vara Cível	PJE	42893.99	8578.8	\N	2022-11-29 00:00:00	PISO	Suspenso	4	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
136	0807647-93.2023.8.19.0004	São Gonçalo	2ª Vara Cível	PJE	21212.38	4242.48	\N	2023-03-24 00:00:00	NOVAESCOLA	Réplica	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
137	0807655-70.2023.8.19.0004	São Gonçalo	2ª Vara Cível	PJE	61412.37	12282.47	\N	2023-03-24 00:00:00	PISO	Réplica	59	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
138	0807660-92.2023.8.19.0004	São Gonçalo	5ª Vara Cível	PJE	31661.93	6332.39	\N	2023-03-24 00:00:00	PISO	Ag. Citação	60	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
139	0808214-94.2023.8.19.0014	Campos	5ª Vara Cível	PJE	45941.74	9188.35	\N	2023-04-20 00:00:00	PISO	Réplica	40	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
140	0808718-35.2023.8.19.0068	Rio das Ostras	2ª Vara Cível	PJE	43725.96	13117.79	\N	2023-10-31 00:00:00	PISO	Ag. JG	118	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
141	0809052-10.2022.8.19.0002	Niterói	3ª Vara Cível	PJE	33750.55	5062.58	\N	2022-06-13 00:00:00	PISO	Provas	44	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
142	0809882-39.2023.8.19.0002	Niterói	10ª Vara Cível	PJE	65506.53	13101.31	\N	2023-03-29 00:00:00	PISO	Réplica	82	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
143	0810060-95.2023.8.19.0031	Maricá	1ª Vara Cível	PJE	196434.46	39286.89	\N	2023-08-02 00:00:00	PISO	Ag. Citação	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
144	0810116-31.2023.8.19.0031	Maricá	1ª Vara Cível	PJE	29547.17	5909.43	\N	2023-08-03 00:00:00	NOVAESCOLA	Réplica	14	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
145	0810443-73.2023.8.19.0031	Maricá	1ª Vara Cível	PJE	50183	10036.6	\N	2023-08-10 00:00:00	PISO	Ag. Citação	15	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
146	0812521-64.2022.8.19.0002	Niterói	8ª Vara Cível	PJE	0	0	\N	2022-07-28 00:00:00	PISO	Provas	78	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
147	0812522-49.2022.8.19.0002	Niterói	8ª Vara Cível	PJE	0	0	\N	2022-07-28 00:00:00	PISO	EDs Suspensão	77	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
148	0812529-41.2022.8.19.0002	Niterói	8ª Vara Cível	PJE	44057.71	8811.54	\N	2022-07-28 00:00:00	PISO	EDs Suspensão	45	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
149	0812669-69.2022.8.19.0004	São Gonçalo	8ª Vara Cível	PJE	137163.62	27432.72	\N	2022-08-25 00:00:00	PISO	Agravo Suspensão	71	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
150	0812960-75.2022.8.19.0002	Niterói	6ª Vara Cível	PJE	36623.67	7324.73	\N	2022-08-03 00:00:00	INTERNIVEIS	Em andamento	49	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
151	0814139-44.2022.8.19.0002	Niterói	8ª Vara Cível	PJE	47362.27	9472.45	\N	2022-08-18 00:00:00	PISO	Provas	5	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
152	0814821-96.2022.8.19.0002	Niterói	5ª Vara Cível	PJE	25547.67	5109.53	\N	2022-08-26 00:00:00	PISO	Sentença Procedencia	8	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
153	0815330-84.2023.8.19.0004	São Gonçalo	3ª Vara Cível	PJE	28283.18	5656.64	\N	2023-06-02 00:00:00	NOVAESCOLA	Custas	86	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
154	0815337-76.2023.8.19.0004	Capital	Orgão Especial	TJRJ	42345.73	8469.15	\N	2023-06-02 00:00:00	INTERNIVEIS	Em andamento	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
155	0815339-46.2023.8.19.0004	São Gonçalo	8ª Vara Cível	PJE	28283.18	5656.64	\N	2023-06-02 00:00:00	NOVAESCOLA	Ag. Citação	88	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
156	0815762-15.2023.8.19.0001	Capital	7ª Vara de Fazenda	PJE	36279.7	7255.94	\N	2023-02-13 00:00:00	PISO	RESP e RE	85	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
157	0816399-94.2022.8.19.0002	Niterói	1ª Vara Cível	PJE	118269.53	23653.91	\N	2022-09-16 00:00:00	PISO	EDs Suspensão	89	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
158	0816893-16.2023.8.19.0004	São Gonçalo	6ª Vara Cível	PJE	128865.75	25773.15	\N	2023-06-20 00:00:00	PISO	Réplica	22	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
159	0818844-51.2023.8.19.0002	Niterói	6ª Vara Cível	PJE	102359.32	20471.86	\N	2023-06-01 00:00:00	PISO	EDs Liquidação	83	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
160	0819015-08.2023.8.19.0002	Niterói	2ª Vara Cível	PJE	43940.05	13182.02	\N	2023-06-02 00:00:00	PISO	Provas	2	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
161	0820979-36.2023.8.19.0002	Niterói	1ª Vara Cível	PJE	21212.38	4242.48	\N	2023-06-21 00:00:00	NOVAESCOLA	Réplica	76	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
162	0820981-06.2023.8.19.0002	Niterói	8ª Vara Cível	PJE	64725.59	12945.12	\N	2023-06-21 00:00:00	PISO	Réplica	76	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
163	0821817-76.2023.8.19.0002	Niterói	10ª Vara Cível	PJE	55211	16563.3	\N	2023-06-28 00:00:00	PISO	Provas	1	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
164	0821818-61.2023.8.19.0002	Niterói	8ª Vara Cível	PJE	63862.65	12772.53	\N	2023-06-28 00:00:00	PISO	Agravo Isenção	104	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
165	0823020-10.2022.8.19.0002	Niterói	10ª Vara Cível	PJE	132355.01	26471	\N	2022-12-07 00:00:00	PISO	Ag. Citação	21	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
166	0823553-32.2023.8.19.0002	Niterói	3ª Vara Cível	PJE	60680.24	12136.05	\N	2023-07-11 00:00:00	PISO	Réplica	65	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
167	0825306-18.2023.8.19.0004	São Gonçalo	3ª Vara Cível	PJE	194208.39	38841.68	\N	2023-09-11 00:00:00	PISO	Ag. Citação	25	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
168	0825587-77.2023.8.19.0002	Niterói	3ª Vara Cível	PJE	62631.92	12526.38	\N	2023-07-26 00:00:00	PISO	Provas	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
169	0825588-62.2023.8.19.0002	Niterói	8ª Vara Cível	PJE	22160.38	4432.08	\N	2023-07-26 00:00:00	NOVAESCOLA	Réplica	17	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
170	0825854-43.2023.8.19.0004	São gonçalo	2ª Vara Cível	PJE	151616.7	30323.34	\N	2023-09-15 00:00:00	PISO	Réplica	111	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
171	0825962-78.2023.8.19.0002	Niterói	5ª Vara Cível	PJE	156136.22	31227.24	\N	2023-07-28 00:00:00	PISO	EDs Suspensão	95	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
172	0826701-51.2023.8.19.0002	Niterói	7ª Vara Cível	PJE	66898.56	13379.71	\N	2023-08-03 00:00:00	PISO	Ag. JG	67	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
173	0828699-48.2023.8.19.0004	São Gonçalo	5ª Vara Cível	PJE	46403.93	9280.79	\N	2023-10-17 00:00:00	PISO	Réplica	115	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
174	0828701-18.2023.8.19.0004	São Gonçalo	2ª Vara Cível	PJE	70947.69	14189.54	\N	2023-10-17 00:00:00	PISO	Ag. JG	116	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
175	0830672-44.2023.8.19.0002	Niterói	4ª Juizado de Fazenda	PJE	29136.99	8741.1	\N	2023-08-30 00:00:00	PISO	Suspenso	34	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
176	0832676-54.2023.8.19.0002	Niterói	6ª Vara Cível	PJE	177585.28	35517.06	\N	2023-09-15 00:00:00	PISO	Ag. JG	52	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
177	0835395-09.2023.8.19.0002	Niterói	8ª Vara Cível	PJE	22160.38	4432.08	\N	2023-10-06 00:00:00	NOVAESCOLA	Réplica	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
178	0835396-91.2023.8.19.0002	Niterói	1ª Vara Cível	PJE	22160.38	4432.08	\N	2023-10-06 00:00:00	NOVAESCOLA	Réplica	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
179	0835397-76.2023.8.19.0002	Niterói	8ª Vara Cível	PJE	169937.28	33987.46	\N	2023-10-06 00:00:00	PISO	Ag. JG	113	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
180	0835398-61.2023.8.19.0002	Niterói	1ª Vara Cível	PJE	158973.58	31794.72	\N	2023-10-06 00:00:00	PISO	Ag. Citação	112	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
181	0841361-50.2023.8.19.0002	Niterói	9ª VC Niterói	PJE	24821.84	4964.37	\N	2023-12-02 00:00:00	NOVAESCOLA	Ag. Impugnação	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
182	0841362-35.2023.8.19.0002	Niterói	6ª VC Niterói	PJE	126221.3	37866.39	\N	2023-11-24 00:00:00	PISO	EDs Liquidação	114	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
183	0842135-80.2023.8.19.0002	Niterói	09ª Vara Cível	PJE	24906.24	4981.25	\N	2023-11-28 00:00:00	NOVAESCOLA	Ag. Impugnação	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
184	0842140-05.2023.8.19.0002	Niterói	09ª Vara Cível	PJE	72460.4	21738.12	\N	2023-11-29 00:00:00	PISO	Ag. Citação	119	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
185	0842806-06.2023.8.19.0002	Niterói	01ª Vara Cível	PJE	114388.06	34316.42	\N	2023-12-04 00:00:00	PISO	Ag. JG	120	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
186	0842824-27.2023.8.19.0002	Niterói	05ª Vara Cível	PJE	30240.97	9072.29	\N	2023-12-04 00:00:00	PISO	Ag. Citação	121	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
187	0842858-02.2023.8.19.0002	Niterói	05ª Vara Cível	PJE	180936.94	54281.08	\N	2023-12-04 00:00:00	PISO	Ag. JG	124	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
188	0842866-76.2023.8.19.0002	Niterói	10ª Vara Cível	PJE	78193.32	23458	\N	2023-12-04 00:00:00	PISO	Ag. Citação	123	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
189	0848723-09.2023.8.19.0001	Capital	15ª Vara de Fazenda	PJE	154710.8	30942.16	\N	2023-04-20 00:00:00	PISO	Ag. Citação	50	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
190	0939350-59.2023.8.19.0001	Capital	3ª Vara de Fazenda Pública	PJE	149348.08	29869.62	\N	2023-10-19 00:00:00	PISO	Ag. Citação	105	2024-02-06 11:56:37.457166	2024-02-06 11:56:37.457166
\.


--
-- TOC entry 4911 (class 0 OID 60222)
-- Dependencies: 217
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, username, password, user_role, created_on, modified_on, last_login) FROM stdin;
1	liz.werner	$2a$12$uq.IM4tb4L356CmSJA1TlejlMfzWR8nUW/JBDBXr0alyc6qQAUq8e	SUPERADMIN	2024-02-06 11:55:53.989868	2024-02-06 11:55:53.989868	\N
2	angelo.masullo	$2a$12$vVWfZ9Lt9tHPS2bBa4GVzOTfVbLbPMLQAeuH8b2UKJphQJdRd5oxK	ADMIN	2024-02-06 11:55:53.989868	2024-02-06 11:55:53.989868	\N
3	ana.cristina.cabral	$2a$12$vqJrjYOvkvOp2t5SzXmdLeLJKnlwHUOJ/Wbn2ebbeUv/bad4rVgp6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
4	ana.lucia.barros	$2a$12$ceB17eaT5fg5MkYyn7x6Ye9xwY0ihm5P/S/bJJaHTG3vQydWGRFLK	REGULAR	\N	2024-02-06 11:56:25.287265	\N
5	ana.maria.machado	$2a$12$UsLFUGguGujy5IEzcnSOs.A40LlE5oyxCr8oSviBuzcVd1Kr1gcIq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
6	angela.maria.venda	$2a$12$tNPoea7v4SoL2YWB2LVZbO3L8jPA9.xaePcBwokDRaWN756Ge4Idu	REGULAR	\N	2024-02-06 11:56:25.287265	\N
7	angela.vieira.baptista	$2a$12$AsQlMRbVl5EZ909MbjAtwezEFNHs9KXId2Mx5q2IM9AdDlJdoq0Ny	REGULAR	\N	2024-02-06 11:56:25.287265	\N
8	carinna.venda.franca	$2a$12$/GRWzZrhuQiHVAiRPzjZRun.erN3nk.17rc0JNS29qRWoI.YPownq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
9	cecilia.santana.marques	$2a$12$rNU/tvEhQYi5wwJgD5bjFeECAcBZBg8wjr7x8rbJhKOcOUoDZyE2u	REGULAR	\N	2024-02-06 11:56:25.287265	\N
10	celi.oliveira.faria	$2a$12$TVasv7229YWekXkK8.Axz.w.dO9aBeLIAOS0P0vf.MsEJK/UNbXou	REGULAR	\N	2024-02-06 11:56:25.287265	\N
11	celia.maria.meireles	$2a$12$ucsl6HWoC4QMmr8hyZ2cO.Iog.bB2.M5vP14QRZt6wfaJqjwxP87a	REGULAR	\N	2024-02-06 11:56:25.287265	\N
12	daise.cristina.carvalho	$2a$12$tRpe5YTQAUsWhmQ9maniReXNp7Jt29/A0k8sP2toGCqcEyBGaTsvq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
13	denise.crivelario.silva	$2a$12$7UNWJelcDT7NgTvcVANNx.H35phzD/3r2TMdBWx5AuRFMOBaDZauq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
14	dirce.midori.matsumoto	$2a$12$wEtS1bjbBR6pTNWBikm.reaFRAZUwrr1U5FT.u2dUYCOwZIk1eK1O	REGULAR	\N	2024-02-06 11:56:25.287265	\N
15	diva.maria.cordeiro	$2a$12$HC6iqmnD4V96x2fdUX8xFuTm6BRKS6EQQebenApmi2vIvg8mHjhgm	REGULAR	\N	2024-02-06 11:56:25.287265	\N
16	eleane.platner.souza	$2a$12$X5geC.ia.8o15UZYFsAwn.XIafgIROaIOlf70Q56eBSf5Wyxl.mgu	REGULAR	\N	2024-02-06 11:56:25.287265	\N
17	elen.sonia.campos	$2a$12$7wOPJRYFtWGWxUQJ1ujKVel0y5xC.QD.9ZgA6JyIE/AIXfigGAMR2	REGULAR	\N	2024-02-06 11:56:25.287265	\N
18	else.maria.abreu	$2a$12$h9QFJXUTYQi1XuquKjrHbO79SAkldLYDg9UZ.ae.jAu68gyfm4Mj.	REGULAR	\N	2024-02-06 11:56:25.287265	\N
19	emilse.neici.martins	$2a$12$4K4cJz29jY1Ung0FAucFm.xeBqjCtv5HGFRkswcBbRyyl1c3GM/4i	REGULAR	\N	2024-02-06 11:56:25.287265	\N
20	erica.pereira.barbosa	$2a$12$jXbkfoXqthVCzyPKHUGqy.UVx4Q8CSd7qc34aQXuKLVJky8mABghK	REGULAR	\N	2024-02-06 11:56:25.287265	\N
21	eva.maria.abreu	$2a$12$TPGdeHWi1hezZi2uyLpbpuo3lSCBXqrjWvirfTIcJbryOD.XgLY4O	REGULAR	\N	2024-02-06 11:56:25.287265	\N
22	geni.soares.jardim	$2a$12$F9JoAKAPrTpDKXhD8Q/Zyun7bDmUi0JbzO67PDMH1E2pPr11b0ie2	REGULAR	\N	2024-02-06 11:56:25.287265	\N
23	gloria.maria.miranda	$2a$12$rT/WnJXYr5v7C3vCxNm04OOurBdIRwYFy3X3jHTFiFh5x5eFDTuFG	REGULAR	\N	2024-02-06 11:56:25.287265	\N
24	graziele.cancio.siqueira	$2a$12$3mPRtJgDTPjNUdT58SCVTO9G4fk/MSjlt6WUHO9Y8yNYDWAGkSO4W	REGULAR	\N	2024-02-06 11:56:25.287265	\N
25	heloisa.maria.teixeira	$2a$12$6GGBqHqk3bFHF9Rvfx6Dv.j5cjSrk0DSGR1jroDvjcnUgifk2AvJW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
26	isaura.maria.martins	$2a$12$9SJLIkQitCrJ4au08qGWZe66KtH14BC2CT8ua.b8iyVm5ngH45dMy	REGULAR	\N	2024-02-06 11:56:25.287265	\N
27	ivone.cardoso.andrade	$2a$12$UO5UURHvL73ZjOfLoAmIp.YrYq.IlkA.jrxojtjrT/0tw.3x7004.	REGULAR	\N	2024-02-06 11:56:25.287265	\N
28	ivonete.maria.silva	$2a$12$UhGBDAtB8W5oFmGLld6Thupz/TLyfTpXZcnNFD8JprRouTJqqSwfa	REGULAR	\N	2024-02-06 11:56:25.287265	\N
29	jadinea.platner.cezario	$2a$12$4MEXt4/hpS6A4cRnsxtPWOomrW8GXzWTLnCI80n4kBkX7jQocNAJW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
30	janete.pinto.rodrigues	$2a$12$.nEmoV0OixvPUc/ztkwj3u7QOjnXyXDeKqYw9aT0ca3HBHGVo5cWG	REGULAR	\N	2024-02-06 11:56:25.287265	\N
31	jorge.solano.andrade	$2a$12$QiPetK7bQSOrT5VYjFpMjuA00r1otPcX7S6pVCgWH0pRmKR07lYJq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
32	juliana.teixeira.ismerio	$2a$12$DcUSTI2TfKAwtnzEN9ovLumpVGdTpmcfsj7ATZ0FS.KPxv6CLgNdW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
33	lais.moreira.nogueira	$2a$12$n8nRn9q/AW.jMzLk2AZecuGio1QevozFPq/E4XJB9Fst5khYDv/mS	REGULAR	\N	2024-02-06 11:56:25.287265	\N
34	lidia.lima	$2a$12$XXU8q9rEagqR7HAB0Qt6xeHlN.N//w.j1/Ywcjfs89bYiHDJ3sVTW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
35	lillian.brigitte.werner	$2a$12$GTCQ.0CaiWmmsqoMPv718OxoBUuxecOp6PThFf8SWBbq3gvrNXOWm	REGULAR	\N	2024-02-06 11:56:25.287265	\N
36	lourdes.maria.teixeira	$2a$12$L4i5D4hgToyS1royZb7OB.2h27luK1ikFCdZKlKh30kHui9hew6Z6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
37	lucilea.novaes.baltazar	$2a$12$6sjY2t0jgUTjlIB38zbsuemmIycyfq49aCG21fagJ2/yBy.CyXNjq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
38	marcia.mendes.fernandes	$2a$12$kFptIIwrDGfWwbS5IATFSe3NUYYSxjMRyRb3hjaKmEuFh7HgPjIZ.	REGULAR	\N	2024-02-06 11:56:25.287265	\N
39	marco.antonio.abdalah	$2a$12$wW7nbLP8RVphBsbT1bWZgeYM3qMpNsRnhnDURqB4IIIDDySSWU7lO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
40	maria.apparecida.coelho	$2a$12$ibJKJ.sWpY.tVoIdI5arCexb3lEt0XAO7xzqLDTMsv87lSypfGzbm	REGULAR	\N	2024-02-06 11:56:25.287265	\N
41	maria.conceicao.silva	$2a$12$k2dzyobDkSEfKrABBWH3yOOs2CsxCVF5pF35EgLNlfsn3PqDGR8NC	REGULAR	\N	2024-02-06 11:56:25.287265	\N
42	sonia.alencar	$2a$12$JhQFpi2J.qFxiGaNTm2T6OL3b.yYBk2EQr0a7lahx3qBzKjKPDsm.	REGULAR	\N	2024-02-06 11:56:25.287265	\N
43	maria.fatima.stancato	$2a$12$QFgLHWBOLmQMX3donZaf6.5ClJBtxpJrXDhErN1lJ13Nrof4Ambxq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
44	maria.hortencia.andrade	$2a$12$Wk.h5ib0yoDAEq1/7/iOVOyJqOYmLfaVE5puYMK.gLbBn1yrSIQUG	REGULAR	\N	2024-02-06 11:56:25.287265	\N
45	maria.ligia.soares	$2a$12$hzstcA3grv5eC.5BaLNgd.pGjvdOHBmc.xYHO.V9S7v/xORK.Vuiy	REGULAR	\N	2024-02-06 11:56:25.287265	\N
46	maria.terezinha.vianna	$2a$12$xnpA5sHuZpTiewek9UuO1uMpf7SXBWja.YCXJc0o1OHBuOfILzici	REGULAR	\N	2024-02-06 11:56:25.287265	\N
47	marilene.luiza.rodrigues	$2a$12$CfNt7Fs6g7ddPkX91zKN6ehjXffLnepH.cCWEYwH1xFi8EZJwppJy	REGULAR	\N	2024-02-06 11:56:25.287265	\N
48	mariza.rodrigues.dantas	$2a$12$BkjzVmoYtCDau62kit0xPOvyjhbRE8XqecnciF2Fr/VCk0ccp5BKq	REGULAR	\N	2024-02-06 11:56:25.287265	\N
49	maria.rosa.cid	$2a$12$1JXP8bmmj0Vu13PKkrjIHeDm/F6Qf7CRzxD37dnUZFv49QerrV70.	REGULAR	\N	2024-02-06 11:56:25.287265	\N
50	marize.tostes.jordao	$2a$12$llzPZ4XLHU.qwlfkHINKcuxz245N1Uvr2adNUtCh5XpcvN2Tftz8C	REGULAR	\N	2024-02-06 11:56:25.287265	\N
51	marli.pires.rocha	$2a$12$actqOlbGQKgn2gQVfGi6gOAUl8cdpR09HsrRi857KlrJpna1aCWf2	REGULAR	\N	2024-02-06 11:56:25.287265	\N
52	marly.jesus.martins	$2a$12$sDFyTuHvVu4e5fudxMbQVOCotcLWkesjFnqqAGzjUP05/NRxhr9iW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
53	marta.helena.siqueira	$2a$12$tEOGSv.DuJ6p4itt.gdI5OC15zYtorrmLyIRrfx3q/9vntAd1gkcK	REGULAR	\N	2024-02-06 11:56:25.287265	\N
54	martha.lopes.cardoso	$2a$12$qd9IbOFGLvv8KXLGC8cfJ.roXlAHgUmm4vPjmeEg0g.Y6wx1VEuQO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
55	monica.silva.santos	$2a$12$d2SjViTXrL9RzTG/dUe0ieyzfmahH2CedbTTFUUeucy8yTc6.PNTW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
56	neuman.mencari.souza	$2a$12$roZrfuIv4kgHLRoYk8dBrO5AEKce1HbAJi1NRC6fHJSvEwq1wQsDO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
57	neuza.carvalho.caffaro	$2a$12$xWnGwZ7K0DEx6kOXZgaAUuCaZRdCYo4bANx6PLYsAyz1Ovu/8bEpS	REGULAR	\N	2024-02-06 11:56:25.287265	\N
58	neuza.maria.soares	$2a$12$G/4wvbWKALnjYa0fuCqjGOB9fYOWVdn30jx1Ap5/QZhnB8HxCg3Im	REGULAR	\N	2024-02-06 11:56:25.287265	\N
59	nilce.vania.lopes	$2a$12$NNh5trvk4Q9aaUoJGxG6/OonX12QJqLx2B.Qyyr0Q6.v3Z3wSrBpO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
60	nilma.silva.araujo	$2a$12$QMk3Sj0bQil8iKFQNc3CwO0/8m74xAHwbFB7WpYdnavZBLj77pYFm	REGULAR	\N	2024-02-06 11:56:25.287265	\N
61	regina.helena.cornado	$2a$12$41h/7YI8g/plQOo1.ymAbueEY7xM1qJ/Dig91DbMnzgiwqMI9Bz3C	REGULAR	\N	2024-02-06 11:56:25.287265	\N
62	regina.rosari.guedes	$2a$12$6cWKTh2vGKj.OXMoM9F4zuizoMziyalPZL4fU..cfp/eP0kw.ud3m	REGULAR	\N	2024-02-06 11:56:25.287265	\N
63	robson.cardoso.andrade	$2a$12$nmPfRSYYzKPCDBO7yJ9WbO8IJ2UhKFetFKRe4oUFAVkf55TmP3p0W	REGULAR	\N	2024-02-06 11:56:25.287265	\N
64	rosa.eli.santana	$2a$12$STmouAOdvu5qeR6PXYrCTOSNqN3zhcJTr83n7hUBziA1/AUTVtls6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
65	rosaine.olivier.tostes	$2a$12$Cjmz0U/deltvXq5REaHauuBeDuiu9xJlC6sphXaPoubIiV8srAWXa	REGULAR	\N	2024-02-06 11:56:25.287265	\N
66	rosangela.mendes.veloso	$2a$12$G5RoFRzh4gkfgn.PKE6oleNiyXvk0o.5NIQ4DhtitVF.wQSNn.Uie	REGULAR	\N	2024-02-06 11:56:25.287265	\N
67	roseli.medeiros.santos	$2a$12$06E73f1Sua83xbst1iBti..sMEx0lFrz70pzr8ZMmY/6oKpwOJIEa	REGULAR	\N	2024-02-06 11:56:25.287265	\N
68	rosilea.sabino.santos	$2a$12$Nk..EzY/GKI5/6VElz5XBuioTYZMl6NIRP4dHmgpJj/MiS7dVppmO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
69	rosilene.novaes.freire	$2a$12$XgD2XqvUb9MkR6rmUNTQAeauCFufXiIuuygpMuBK1/vLVP5NBWYMa	REGULAR	\N	2024-02-06 11:56:25.287265	\N
70	rosimeri.sabino.santos	$2a$12$gRrmOsZETIv4fBnsLghcfeD5Jnvk40NhcjxBGChaW2igkccO522Uy	REGULAR	\N	2024-02-06 11:56:25.287265	\N
71	sandra.regina.moura	$2a$12$l6jfrBKR4jkOa1u/Cl3FOenKz8jn16ZA2eqv8mpud2I1JGj9r.fNC	REGULAR	\N	2024-02-06 11:56:25.287265	\N
72	silvia.sandra.souza	$2a$12$EEOZdvLxfEaAy/AD78tp6OME5JYteQuMppyL0VdGpad80uMoDoirW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
73	solange.silva.moura	$2a$12$jeUnydbjCIGwMbuynjPFE.gY0YGn3z6leu4Q5n5ZsOWOWxD91NWtm	REGULAR	\N	2024-02-06 11:56:25.287265	\N
74	sonia.maria.lomba	$2a$12$mIsJgiLNISeCP7Uzv3uOlO9mYloNxKZDKrdUlc.6nSKyRa6XhyNKW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
75	sueli.pimenta.vieira	$2a$12$AZeZlh29Ud2knnLCGMSH2urwII1oWIZ2L6owuNjusDY7ZANvyVwyO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
76	sueli.talarico.lopes	$2a$12$YDXd2TMIB3XtPPD3Kw65Vuzm6PHXt2/2GHugOaHNNxO87cY/LqKj6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
77	suzana.francesconi.costa	$2a$12$bBBe.VD8vsxCq31E82oF/eKUtH29EQLIBymUcSutx0Qj0n7OQc27C	REGULAR	\N	2024-02-06 11:56:25.287265	\N
78	telma.regina.silva	$2a$12$m9RLLDd7YuWYoiJJqsUEq.lJH/.ydWfkRLQqp3PW38sSCNA1498jC	REGULAR	\N	2024-02-06 11:56:25.287265	\N
79	terezinha.tostes.lopes	$2a$12$anMv6WZL3.1BsDC8.YDufO2D/IRCC3ybB46QCoU5rnUOZQ.m6ZxQ6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
80	valdice.ruth.coelho	$2a$12$LcIAcgXLY6vjigM0I7FuauzjVtyzPwEJp4V3CS2lq8vc8QOhlxP/a	REGULAR	\N	2024-02-06 11:56:25.287265	\N
81	vania.lucia.teixeira	$2a$12$TTAIhpvdvC4114o.qAtpvebr1OTL3kXDx6UUcaCemlRMRwdtCy8jO	REGULAR	\N	2024-02-06 11:56:25.287265	\N
82	vera.lucia.correa	$2a$12$gqj/Ae3w9W6s.ujJcA7efugfCz1bT.dALzC/h4wr6LE5/fCPvnCCK	REGULAR	\N	2024-02-06 11:56:25.287265	\N
83	vera.lucia.anna	$2a$12$0cCD9fHYUUrBPpLFqtgDjOEg4ZcXfNnZPygI.yF4ebFLTwCc6JyOa	REGULAR	\N	2024-02-06 11:56:25.287265	\N
84	vera.lucia.silva	$2a$12$NhubY102XWlgIojfEVsqE.WHnMNoQsoed3S6kmcStLSvBf9D5KWo6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
85	vitoria.regia.mello	$2a$12$ZTkIvHHlxdWxzCrzLmxQte3DJpYo7DTAuq7CFEJb0kYyBiB2vtF2O	REGULAR	\N	2024-02-06 11:56:25.287265	\N
86	zelda.cunha.camacho	$2a$12$kRLo4n2Igorxu2ST83XHJ.GaO0h4XmM8KK9A9WJxVvXS3ddaahsB2	REGULAR	\N	2024-02-06 11:56:25.287265	\N
87	zelia.maria.santos	$2a$12$lt1ntN3xKVRI2TzbxARjb.m0/yRla/YPHn9HcjoMGpXGFxYXqeLQi	REGULAR	\N	2024-02-06 11:56:25.287265	\N
88	jucelma.hermsdorff.gaichi	$2a$12$ijmYh9iC6UMNlaApVAyftuDpnDtBh5Rrt.xYobVWieNyl5vloDhwa	REGULAR	\N	2024-02-06 11:56:25.287265	\N
89	iracema.bueno.santos	$2a$12$z/eYdvtTrfPBjWTLxuPvyOVhFcgg8vYKzj4u4j9FL8hko2tACZNq6	REGULAR	\N	2024-02-06 11:56:25.287265	\N
90	monica.alves.sally	$2a$12$OMmBkSRjD7V0eYLz1YKqZu4coNurahXR7PrLI1BOGm7ew7WT1xUF.	REGULAR	\N	2024-02-06 11:56:25.287265	\N
91	emilly.marinho.martins	$2a$12$BQnivaFqni97AQ.lcqrsVeGxDdJPLz7LLqy.dTwv8xEJltP1A4uYe	REGULAR	\N	2024-02-06 11:56:25.287265	\N
92	maria.fatima.silva	$2a$12$f9TJLAOoSwTIw3XocJO3w.IXipYDWa8eNzXmXOAENs4s90ccqXUVe	REGULAR	\N	2024-02-06 11:56:25.287265	\N
93	narda.el-amme.silva	$2a$12$K.rou6kwcYyXLwdE7nJOR.j1.VM3IWdh9vnaPqkWEw45Ia.PwRW6S	REGULAR	\N	2024-02-06 11:56:25.287265	\N
94	teresinha.maria.ventura	$2a$12$fpajdH33HEH25MFLFohLV.yuZB8ayCTNu2wQUCddj99gUJ7MPPfKK	REGULAR	\N	2024-02-06 11:56:25.287265	\N
95	regina.coeli.codeco	$2a$12$zynzglL4es/kGZ6h23rVpORQ/Vqy7F.2QVP.LJT55uyIpzwqwlcSm	REGULAR	\N	2024-02-06 11:56:25.287265	\N
96	neuza.maria.cabral	$2a$12$kncSV6SDrem6X.xmfi74S.FjRP2pKSGSVdUX2si76JBICa1TcdrMW	REGULAR	\N	2024-02-06 11:56:25.287265	\N
\.


--
-- TOC entry 4933 (class 0 OID 0)
-- Dependencies: 218
-- Name: address_address_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.address_address_id_seq', 111, true);


--
-- TOC entry 4934 (class 0 OID 0)
-- Dependencies: 222
-- Name: matriculation_matriculation_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.matriculation_matriculation_id_seq', 134, true);


--
-- TOC entry 4935 (class 0 OID 0)
-- Dependencies: 226
-- Name: moviment_moviment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.moviment_moviment_id_seq', 1851, true);


--
-- TOC entry 4936 (class 0 OID 0)
-- Dependencies: 220
-- Name: person_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.person_person_id_seq', 111, true);


--
-- TOC entry 4937 (class 0 OID 0)
-- Dependencies: 224
-- Name: process_process_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.process_process_id_seq', 191, true);


--
-- TOC entry 4938 (class 0 OID 0)
-- Dependencies: 216
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 106, true);


--
-- TOC entry 4744 (class 2606 OID 60322)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- TOC entry 4737 (class 2606 OID 60219)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 4756 (class 2606 OID 60336)
-- Name: matriculation matriculation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matriculation
    ADD CONSTRAINT matriculation_pkey PRIMARY KEY (matriculation_id);


--
-- TOC entry 4760 (class 2606 OID 60373)
-- Name: moviment moviment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.moviment
    ADD CONSTRAINT moviment_pkey PRIMARY KEY (moviment_id);


--
-- TOC entry 4746 (class 2606 OID 60261)
-- Name: person person_cpf_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_cpf_key UNIQUE (cpf);


--
-- TOC entry 4748 (class 2606 OID 60259)
-- Name: person person_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_email_key UNIQUE (email);


--
-- TOC entry 4750 (class 2606 OID 60257)
-- Name: person person_nome_completo_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_nome_completo_key UNIQUE (nome_completo);


--
-- TOC entry 4752 (class 2606 OID 60393)
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (person_id);


--
-- TOC entry 4754 (class 2606 OID 60263)
-- Name: person person_rg_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_rg_key UNIQUE (rg);


--
-- TOC entry 4758 (class 2606 OID 60451)
-- Name: process process_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.process
    ADD CONSTRAINT process_pkey PRIMARY KEY (process_id);


--
-- TOC entry 4740 (class 2606 OID 60500)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4742 (class 2606 OID 60233)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4738 (class 1259 OID 60220)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- TOC entry 4761 (class 2606 OID 60419)
-- Name: person fk_address; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES public.address(address_id);


--
-- TOC entry 4764 (class 2606 OID 60487)
-- Name: process fk_matriculation; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.process
    ADD CONSTRAINT fk_matriculation FOREIGN KEY (matriculation_id) REFERENCES public.matriculation(matriculation_id);


--
-- TOC entry 4763 (class 2606 OID 60394)
-- Name: matriculation fk_person; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.matriculation
    ADD CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES public.person(person_id);


--
-- TOC entry 4765 (class 2606 OID 60452)
-- Name: moviment fk_process; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.moviment
    ADD CONSTRAINT fk_process FOREIGN KEY (process_id) REFERENCES public.process(process_id);


--
-- TOC entry 4762 (class 2606 OID 60501)
-- Name: person fk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);


-- Completed on 2025-05-11 12:51:31

--
-- PostgreSQL database dump complete
--

