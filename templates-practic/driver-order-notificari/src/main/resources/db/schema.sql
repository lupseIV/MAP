-- Auto-generated Schema
-- Generated at: 2026-01-25T09:38:18.717425900

-- Table for Driver
CREATE TABLE IF NOT EXISTS drivers (
	name VARCHAR(255) NOT NULL,
	id BIGSERIAL PRIMARY KEY
);

-- Table for Order
CREATE TABLE IF NOT EXISTS orders (
	driverId_id BIGINT REFERENCES drivers(id),
	status VARCHAR(50) NOT NULL,
	start_date TIMESTAMP NOT NULL,
	end_date TIMESTAMP,
	pick_up_address VARCHAR(255) NOT NULL,
	client_name VARCHAR(255) NOT NULL,
	id SERIAL PRIMARY KEY
);

