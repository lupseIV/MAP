-- Database schema for DuckSocialNetwork application
-- Creates all tables needed for the application

-- Create sequences for all ID generation
CREATE SEQUENCE IF NOT EXISTS persons_id_seq;
CREATE SEQUENCE IF NOT EXISTS ducks_id_seq;
CREATE SEQUENCE IF NOT EXISTS friendships_id_seq;
CREATE SEQUENCE IF NOT EXISTS flocks_id_seq;
CREATE SEQUENCE IF NOT EXISTS race_events_id_seq;
CREATE SEQUENCE IF NOT EXISTS messages_id_seq;
CREATE SEQUENCE IF NOT EXISTS notifications_id_seq;

-- Create Persons table
CREATE TABLE IF NOT EXISTS persons (
                                       id BIGINT PRIMARY KEY DEFAULT nextval('persons_id_seq'),
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
                                     id BIGINT PRIMARY KEY DEFAULT nextval('ducks_id_seq'),
                                     username VARCHAR(255) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL,
                                     duck_type VARCHAR(50) NOT NULL,
                                     speed DOUBLE PRECISION,
                                     rezistance DOUBLE PRECISION
);

-- Create Friendships table
CREATE TABLE IF NOT EXISTS friendships (
                                           id BIGINT PRIMARY KEY DEFAULT nextval('friendships_id_seq'),
                                           user1_id BIGINT NOT NULL,
                                           user2_id BIGINT NOT NULL
);

-- Create Flocks table
CREATE TABLE IF NOT EXISTS flocks (
                                      id BIGINT PRIMARY KEY DEFAULT nextval('flocks_id_seq'),
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
                                           id BIGINT PRIMARY KEY DEFAULT nextval('race_events_id_seq'),
                                           name VARCHAR(255) NOT NULL,
                                           max_time DOUBLE PRECISION
);

-- Create Race Event Participants table (junction table)
CREATE TABLE IF NOT EXISTS race_event_participants (
                                                       event_id BIGINT NOT NULL,
                                                       duck_id BIGINT NOT NULL,
                                                       PRIMARY KEY (event_id, duck_id)
);

-- Create Messages table
CREATE TABLE IF NOT EXISTS messages (
                                        id BIGINT PRIMARY KEY DEFAULT nextval('messages_id_seq'),
                                        from_user_id BIGINT NOT NULL,
                                        message TEXT NOT NULL,
                                        date TIMESTAMP NOT NULL,
                                        reply_to_id BIGINT
);

-- Create Message Recipients table (junction table)
CREATE TABLE IF NOT EXISTS message_recipients (
                                                  message_id BIGINT NOT NULL,
                                                  recipient_id BIGINT NOT NULL,
                                                  PRIMARY KEY (message_id, recipient_id)
);

-- Create Notifications table
CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGINT PRIMARY KEY DEFAULT nextval('notifications_id_seq'),
                                             recipient_id BIGINT NOT NULL,
                                             sender_id BIGINT NOT NULL,
                                             message_preview TEXT NOT NULL,
                                             timestamp TIMESTAMP NOT NULL,
                                             is_read BOOLEAN NOT NULL DEFAULT FALSE
);
