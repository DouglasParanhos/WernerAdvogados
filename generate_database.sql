BEGIN;

-- ============================================
-- SEQUENCES
-- ============================================

CREATE SEQUENCE IF NOT EXISTS public.address_address_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.person_person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.matriculation_matriculation_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.process_process_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.moviment_moviment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ============================================
-- TABLES
-- ============================================

CREATE TABLE IF NOT EXISTS public.address (
    address_id bigint NOT NULL,
    logradouro character varying(255) NOT NULL,
    cidade character varying(255) NOT NULL,
    estado character varying(255) NOT NULL,
    cep character varying(255) NOT NULL,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS public.users (
    user_id bigint NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(512) NOT NULL,
    user_role character varying(16) NOT NULL,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_login timestamp(6) without time zone
);

CREATE TABLE IF NOT EXISTS public.person (
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

CREATE TABLE IF NOT EXISTS public.matriculation (
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

CREATE TABLE IF NOT EXISTS public.process (
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

CREATE TABLE IF NOT EXISTS public.moviment (
    moviment_id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    date timestamp(6) without time zone NOT NULL,
    process_id bigint,
    created_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_on timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- ============================================
-- SEQUENCE OWNERSHIP
-- ============================================

ALTER SEQUENCE public.address_address_id_seq OWNED BY public.address.address_id;
ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;
ALTER SEQUENCE public.person_person_id_seq OWNED BY public.person.person_id;
ALTER SEQUENCE public.matriculation_matriculation_id_seq OWNED BY public.matriculation.matriculation_id;
ALTER SEQUENCE public.process_process_id_seq OWNED BY public.process.process_id;
ALTER SEQUENCE public.moviment_moviment_id_seq OWNED BY public.moviment.moviment_id;

-- ============================================
-- COLUMN DEFAULTS
-- ============================================

ALTER TABLE ONLY public.address ALTER COLUMN address_id SET DEFAULT nextval('public.address_address_id_seq'::regclass);
ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);
ALTER TABLE ONLY public.person ALTER COLUMN person_id SET DEFAULT nextval('public.person_person_id_seq'::regclass);
ALTER TABLE ONLY public.matriculation ALTER COLUMN matriculation_id SET DEFAULT nextval('public.matriculation_matriculation_id_seq'::regclass);
ALTER TABLE ONLY public.process ALTER COLUMN process_id SET DEFAULT nextval('public.process_process_id_seq'::regclass);
ALTER TABLE ONLY public.moviment ALTER COLUMN moviment_id SET DEFAULT nextval('public.moviment_moviment_id_seq'::regclass);

-- ============================================
-- PRIMARY KEYS
-- ============================================

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (person_id);

ALTER TABLE ONLY public.matriculation
    ADD CONSTRAINT matriculation_pkey PRIMARY KEY (matriculation_id);

ALTER TABLE ONLY public.process
    ADD CONSTRAINT process_pkey PRIMARY KEY (process_id);

ALTER TABLE ONLY public.moviment
    ADD CONSTRAINT moviment_pkey PRIMARY KEY (moviment_id);

-- ============================================
-- UNIQUE CONSTRAINTS
-- ============================================

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_nome_completo_key UNIQUE (nome_completo);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_email_key UNIQUE (email);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_cpf_key UNIQUE (cpf);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_rg_key UNIQUE (rg);

-- ============================================
-- FOREIGN KEYS
-- ============================================

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.users(user_id);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES public.address(address_id);

ALTER TABLE ONLY public.matriculation
    ADD CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES public.person(person_id);

ALTER TABLE ONLY public.process
    ADD CONSTRAINT fk_matriculation FOREIGN KEY (matriculation_id) REFERENCES public.matriculation(matriculation_id);

ALTER TABLE ONLY public.moviment
    ADD CONSTRAINT fk_process FOREIGN KEY (process_id) REFERENCES public.process(process_id);

COMMIT;
