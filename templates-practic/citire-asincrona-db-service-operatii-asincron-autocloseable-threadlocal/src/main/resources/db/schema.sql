-- Auto-generated Schema
-- Generated at: 2026-01-23T17:06:01.811359300

-- Table for Staff
CREATE TABLE IF NOT EXISTS staff (
	salary DECIMAL(19, 2) NOT NULL,
	hire_date DATE,
	department VARCHAR(255),
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255) UNIQUE,
	phone_number VARCHAR(255),
	id BIGSERIAL PRIMARY KEY
);

-- Table for Manager
CREATE TABLE IF NOT EXISTS managers (
	bonus DOUBLE PRECISION NOT NULL,
	team_size INT,
	access_level INT,
	salary DECIMAL(19, 2) NOT NULL,
	hire_date DATE,
	department VARCHAR(255),
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255) UNIQUE,
	phone_number VARCHAR(255),
	id BIGSERIAL PRIMARY KEY
);

-- Table for Client
CREATE TABLE IF NOT EXISTS clients (
	full_name VARCHAR(255) NOT NULL,
	client_type VARCHAR(255),
	budget DOUBLE PRECISION NOT NULL,
	registration_date DATE,
	id SERIAL PRIMARY KEY
);

-- Table for VipClient
CREATE TABLE IF NOT EXISTS vip_clients (
	loyalty_points INT,
	personalManager_id BIGINT REFERENCES managers(id),
	full_name VARCHAR(255) NOT NULL,
	client_type VARCHAR(255),
	budget DOUBLE PRECISION NOT NULL,
	registration_date DATE,
	id SERIAL PRIMARY KEY
);

