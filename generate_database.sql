BEGIN;

CREATE TABLE IF NOT EXISTS users(
   id bigserial PRIMARY KEY,
   username VARCHAR ( 255 ) UNIQUE NOT NULL,
   password VARCHAR ( 512 ) NOT NULL,
   role VARCHAR ( 16 ) DEFAULT 'USER' NOT NULL,
   created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS address (
	id bigserial PRIMARY KEY,
	logradouro VARCHAR ( 255 ) NOT NULL,
	cidade VARCHAR ( 255 ) NOT NULL,
	estado VARCHAR ( 255 ) NOT NULL,
	cep VARCHAR ( 255 ) NOT NULL,
	
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS person (
	id bigserial PRIMARY KEY,
	fullname VARCHAR ( 50 ) UNIQUE NOT NULL,
	email VARCHAR ( 255 ) UNIQUE NOT NULL,
	cpf VARCHAR ( 16 ) UNIQUE NOT NULL,
	rg VARCHAR ( 16 ) UNIQUE NOT NULL,
	estado_civil VARCHAR ( 16 ) NOT NULL,
	data_nascimento DATE NOT NULL,
	profissao VARCHAR ( 255 ) NOT NULL,
	telefone VARCHAR ( 16 ) NOT NULL,
	vivo BIT NOT NULL,
	representante VARCHAR ( 255 ),
	id_funcional VARCHAR ( 16 ),
	nacionalidade VARCHAR ( 32 ),
	
	user_id BIGINT,
	CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
	
	address_id BIGINT,
	CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES address(id),
	
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS matriculation (
	id bigserial PRIMARY KEY,
	numero VARCHAR ( 32 ) NOT NULL,
	inicio_erj DATE NOT NULL,
	cargo VARCHAR ( 255 ) UNIQUE NOT NULL,
	data_aposentadoria DATE NOT NULL,
	nivel_atual VARCHAR NOT NULL,
	trienio_atual VARCHAR NOT NULL,
	referencia VARCHAR NOT NULL,
	
	person_id BIGINT,
	CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES person(id),
	
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS process (
	id bigserial PRIMARY KEY,
	numero VARCHAR ( 255 ) NOT NULL,
	comarca VARCHAR ( 255 ) NOT NULL,
	vara VARCHAR ( 255 ) NOT NULL,
	sistema VARCHAR ( 255 ) NOT NULL,
	valor DECIMAL,
	previsao_honorarios_contratuais DECIMAL,
	previsao_honorarios_sucumbenciais DECIMAL,
	distribuido_em DATE,
	
	matriculation_id BIGINT,
	CONSTRAINT fk_matriculation FOREIGN KEY (matriculation_id) REFERENCES matriculation(id),
	
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS moviment (
	id bigserial PRIMARY KEY,
	descricao VARCHAR ( 255 ) NOT NULL,
	date DATE NOT NULL,
	
	process_id BIGINT,
	CONSTRAINT fk_process FOREIGN KEY (process_id) REFERENCES process(id),
	
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
	modified_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

INSERT INTO users(username, password, role) 
VALUES ('liz.werner', '$2a$12$uq.IM4tb4L356CmSJA1TlejlMfzWR8nUW/JBDBXr0alyc6qQAUq8e', 'SUPER_ADMIN');
INSERT INTO users(username, password, role) 
VALUES ('angelo.masullo', '$2a$12$vVWfZ9Lt9tHPS2bBa4GVzOTfVbLbPMLQAeuH8b2UKJphQJdRd5oxK', 'ADMIN');

COMMIT;