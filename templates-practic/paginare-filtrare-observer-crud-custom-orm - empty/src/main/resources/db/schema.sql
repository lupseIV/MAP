-- Auto-generated Schema
-- Generated at: 2026-01-25T17:53:15.427783400

-- Table for Doctor
CREATE TABLE IF NOT EXISTS doctors (
	name VARCHAR(255) NOT NULL,
	specialty VARCHAR(255) NOT NULL,
	id BIGSERIAL PRIMARY KEY
);

-- Table for Pacient
CREATE TABLE IF NOT EXISTS pacients (
	name VARCHAR(255) NOT NULL,
	cnp VARCHAR(255) NOT NULL UNIQUE,
	id BIGSERIAL PRIMARY KEY
);

-- Table for Programare
CREATE TABLE IF NOT EXISTS programari (
	id_medic VARCHAR(255) NOT NULL,
	id_pacient VARCHAR(255) NOT NULL,
	data_ora TIMESTAMP NOT NULL,
	status VARCHAR(255) NOT NULL,
	id BIGSERIAL PRIMARY KEY
);

