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
                                           user2_id BIGINT NOT NULL,
                                           status varchar(20) DEFAULT 'PENDING' CHECK ( status in ('PENDING', 'APPROVED', 'REJECTED'))
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
                                           max_time DOUBLE PRECISION,
                                           state VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED' CHECK ( state in ('SCHEDULED', 'ONGOING', 'COMPLETED')),
                                           owner_person_id BIGINT NOT NULL,
                                           FOREIGN KEY (owner_person_id) REFERENCES persons(id)
                                       );

-- Create Race Event Participants table (junction table)
CREATE TABLE IF NOT EXISTS race_event_participants (
                                                       event_id BIGINT NOT NULL,
                                                       duck_id BIGINT NOT NULL,
                                                       PRIMARY KEY (event_id, duck_id)
);
CREATE TABLE IF NOT EXISTS messages (
                                        id BIGINT PRIMARY KEY,
                                        from_user_id BIGINT NOT NULL,
                                        message TEXT NOT NULL,
                                        date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        reply_to_id BIGINT,
                                        status varchar(20) DEFAULT 'NEW' CHECK ( status in ('NEW', 'READ')),
                                        FOREIGN KEY (from_user_id) REFERENCES persons(id),
                                        FOREIGN KEY (reply_to_id) REFERENCES messages(id)
);

-- Create Message Recipients table (for multiple recipients)
CREATE TABLE IF NOT EXISTS message_recipients (
                                                  message_id BIGINT NOT NULL,
                                                  user_id BIGINT NOT NULL,
                                                  PRIMARY KEY (message_id, user_id),
                                                  FOREIGN KEY (message_id) REFERENCES messages(id),
                                                  FOREIGN KEY (user_id) REFERENCES persons(id)
);
CREATE TABLE IF NOT EXISTS notifications (
                                             id SERIAL PRIMARY KEY,
                                             type VARCHAR(50) NOT NULL,
                                             status varchar(20) DEFAULT 'NEW' CHECK ( status in ('NEW', 'READ', 'DELETED')),
                                             description TEXT,
                                             sender_id BIGINT,
                                             receiver_id BIGINT NOT NULL,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             data TEXT
);
CREATE TABLE IF NOT EXISTS race_event_distances (
                                      event_id BIGINT NOT NULL,
                                      distance INT NOT NULL,
                                      lane_index INT NOT NULL,
                                      PRIMARY KEY (event_id, lane_index),
                                      FOREIGN KEY (event_id) REFERENCES race_events(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS race_event_winners (
                                        event_id BIGINT NOT NULL,
                                        duck_id BIGINT NOT NULL,
                                        position INT NOT NULL,
                                        PRIMARY KEY (event_id, position),
                                        FOREIGN KEY (event_id) REFERENCES  race_events(id) ON DELETE CASCADE
);
ALTER TABLE ducks
ADD COLUMN IF NOT EXISTS description TEXT;

ALTER TABLE persons
ADD COLUMN IF NOT EXISTS description TEXT;