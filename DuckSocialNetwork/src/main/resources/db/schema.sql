-- Database schema for DuckSocialNetwork application
-- Creates all tables needed for the application

-- Create Persons table
CREATE TABLE IF NOT EXISTS persons (
                                       id BIGINT PRIMARY KEY,
                                       username VARCHAR(255) NOT NULL UNIQUE,
                                       password VARCHAR(255) NOT NULL,
                                       email VARCHAR(255) NOT NULL,
                                       first_name VARCHAR(255),
                                       last_name VARCHAR(255),
                                       occupation VARCHAR(255),
                                       date_of_birth DATE,
                                       empathy_level DOUBLE PRECISION
);

-- Create Ducks table
CREATE TABLE IF NOT EXISTS ducks (
                                     id BIGINT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL,
                                     duck_type VARCHAR(50) NOT NULL,
                                     speed DOUBLE PRECISION,
                                     rezistance DOUBLE PRECISION
);

-- Create Friendships table
CREATE TABLE IF NOT EXISTS friendships (
                                           id BIGINT PRIMARY KEY,
                                           user1_id BIGINT NOT NULL,
                                           user2_id BIGINT NOT NULL
);

-- Create Flocks table
CREATE TABLE IF NOT EXISTS flocks (
                                      id BIGINT PRIMARY KEY,
                                      flock_name VARCHAR(255) NOT NULL
);

-- Create Flock Members table (junction table)
CREATE TABLE IF NOT EXISTS flock_members (
                                             flock_id BIGINT NOT NULL,
                                             duck_id BIGINT NOT NULL,
                                             PRIMARY KEY (flock_id, duck_id)
);

-- Create Race Events table
CREATE TABLE IF NOT EXISTS race_events (
                                           id BIGINT PRIMARY KEY,
                                           name VARCHAR(255) NOT NULL,
                                           max_time DOUBLE PRECISION
);

-- Create Race Event Participants table (junction table)
CREATE TABLE IF NOT EXISTS race_event_participants (
                                                       event_id BIGINT NOT NULL,
                                                       duck_id BIGINT NOT NULL,
                                                       PRIMARY KEY (event_id, duck_id)
);


