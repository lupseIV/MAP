# DuckSocialNetwork Database Schema

## Overview

The DuckSocialNetwork database is designed to support a social networking application for ducks and persons. The schema includes tables for users (both persons and ducks), friendships, duck flocks, race events, messaging, and notifications.

## Database Files

- **Schema Definition**: `src/main/resources/db/schema.sql` - Contains all table definitions
- **Sample Data**: `src/main/resources/db/insert_data.sql` - Contains boilerplate data for testing and development

## Entity Relationship Diagram

```
┌─────────────┐         ┌─────────────┐
│   Persons   │         │    Ducks    │
├─────────────┤         ├─────────────┤
│ id (PK)     │         │ id (PK)     │
│ username    │         │ username    │
│ password    │         │ password    │
│ email       │         │ email       │
│ first_name  │         │ duck_type   │
│ last_name   │         │ speed       │
│ occupation  │         │ rezistance  │
│ date_of_birth│        └─────────────┘
│ empathy_level│              │
└─────────────┘              │
       │                      │
       │                      ├─────────────┐
       │                      │             │
       │         ┌────────────▼──┐   ┌──────▼─────────────┐
       └────────►│  Friendships  │   │   Flock Members    │
                 ├───────────────┤   ├────────────────────┤
                 │ id (PK)       │   │ flock_id (PK, FK)  │
                 │ user1_id (FK) │   │ duck_id (PK, FK)   │
                 │ user2_id (FK) │   └────────────────────┘
                 └───────────────┘              │
                                                │
                                         ┌──────▼──────┐
                                         │   Flocks    │
                                         ├─────────────┤
                                         │ id (PK)     │
                                         │ flock_name  │
                                         └─────────────┘

┌─────────────────────┐         ┌──────────────────────────┐
│   Race Events       │◄────────│ Race Event Participants  │
├─────────────────────┤         ├──────────────────────────┤
│ id (PK)             │         │ event_id (PK, FK)        │
│ name                │         │ duck_id (PK, FK)         │
│ max_time            │         └──────────────────────────┘
└─────────────────────┘

┌─────────────────────┐         ┌──────────────────────┐
│     Messages        │◄────────│ Message Recipients   │
├─────────────────────┤         ├──────────────────────┤
│ id (PK)             │         │ message_id (PK, FK)  │
│ from_user_id (FK)   │         │ recipient_id (PK, FK)│
│ message             │         └──────────────────────┘
│ date                │
│ reply_to_id (FK)    │
└─────────────────────┘

┌─────────────────────┐
│   Notifications     │
├─────────────────────┤
│ id (PK)             │
│ recipient_id (FK)   │
│ sender_id (FK)      │
│ message_preview     │
│ timestamp           │
│ is_read             │
└─────────────────────┘
```

*Note: 'rezistance' is the actual column name in the database schema (alternative spelling of 'resistance').

## Tables

### 1. Persons

Stores information about human users of the social network.

| Column        | Type           | Constraints      | Description                           |
|--------------|----------------|------------------|---------------------------------------|
| id           | BIGINT         | PRIMARY KEY      | Unique identifier for the person      |
| username     | VARCHAR(255)   | NOT NULL, UNIQUE | Unique username for login             |
| password     | VARCHAR(255)   | NOT NULL         | User's password (should be hashed)    |
| email        | VARCHAR(255)   | NOT NULL         | User's email address                  |
| first_name   | VARCHAR(255)   |                  | User's first name                     |
| last_name    | VARCHAR(255)   |                  | User's last name                      |
| occupation   | VARCHAR(255)   |                  | User's occupation                     |
| date_of_birth| DATE           |                  | User's date of birth                  |
| empathy_level| DOUBLE PRECISION|                 | User's empathy level (0-10)           |

**Sample Data**: Contains 20 persons (IDs 21-40) with various occupations like Engineer, Doctor, Teacher, etc.

### 2. Ducks

Stores information about duck users of the social network.

| Column     | Type           | Constraints      | Description                              |
|-----------|----------------|------------------|------------------------------------------|
| id        | BIGINT         | PRIMARY KEY      | Unique identifier for the duck           |
| username  | VARCHAR(255)   | NOT NULL, UNIQUE | Unique username for login                |
| password  | VARCHAR(255)   | NOT NULL         | Duck's password (should be hashed)       |
| email     | VARCHAR(255)   | NOT NULL         | Duck's email address                     |
| duck_type | VARCHAR(50)    | NOT NULL         | Type of duck (FLYING, SWIMMING, or FLYING_AND_SWIMMING) |
| speed     | DOUBLE PRECISION|                 | Duck's speed attribute                   |
| rezistance| DOUBLE PRECISION|                 | Duck's resistance/endurance attribute (note: column name has alternative spelling) |

**Sample Data**: Contains 20 ducks (IDs 1-20) with different types and attributes.

**Duck Types**:
- FLYING: Ducks that can fly
- SWIMMING: Ducks that can swim
- FLYING_AND_SWIMMING: Ducks that can both fly and swim

### 3. Friendships

Represents connections between users (persons and ducks).

| Column    | Type   | Constraints   | Description                    |
|-----------|--------|---------------|--------------------------------|
| id        | BIGINT | PRIMARY KEY   | Unique identifier for friendship|
| user1_id  | BIGINT | NOT NULL      | ID of first user in friendship |
| user2_id  | BIGINT | NOT NULL      | ID of second user in friendship|

**Sample Data**: Contains 15 friendships connecting ducks (IDs 1-15) with persons (IDs 21-35).

### 4. Flocks

Groups of ducks that form social groups.

| Column     | Type         | Constraints   | Description                     |
|-----------|--------------|---------------|---------------------------------|
| id        | BIGINT       | PRIMARY KEY   | Unique identifier for flock     |
| flock_name| VARCHAR(255) | NOT NULL      | Name of the flock               |

**Sample Data**: Contains 15 flocks named FlockA through FlockO.

### 5. Flock Members (Junction Table)

Maps the many-to-many relationship between flocks and ducks.

| Column   | Type   | Constraints             | Description                  |
|----------|--------|-------------------------|------------------------------|
| flock_id | BIGINT | PRIMARY KEY, NOT NULL   | ID of the flock              |
| duck_id  | BIGINT | PRIMARY KEY, NOT NULL   | ID of the duck               |

**Note**: The composite primary key (flock_id, duck_id) ensures a duck can only be in a flock once.

**Sample Data**: Distributes ducks across various flocks, with some ducks belonging to multiple flocks. FlockN (ID 14) has no members.

### 6. Race Events

Stores information about racing competitions for ducks.

| Column   | Type              | Constraints   | Description                        |
|----------|-------------------|---------------|------------------------------------|
| id       | BIGINT            | PRIMARY KEY   | Unique identifier for race event   |
| name     | VARCHAR(255)      | NOT NULL      | Name of the race event             |
| max_time | DOUBLE PRECISION  |               | Maximum time allowed for race      |

**Sample Data**: Contains 4 race events with IDs 1, 3, 4, and 5 (named Dinamo, Mihai, test, TEST respectively). Note: IDs are not sequential.

### 7. Race Event Participants (Junction Table)

Maps the many-to-many relationship between race events and participating ducks.

| Column   | Type   | Constraints             | Description                     |
|----------|--------|-------------------------|---------------------------------|
| event_id | BIGINT | PRIMARY KEY, NOT NULL   | ID of the race event            |
| duck_id  | BIGINT | PRIMARY KEY, NOT NULL   | ID of the participating duck    |

**Sample Data**: Multiple ducks participate in various race events.

### 8. Messages

Stores messages sent between users.

| Column       | Type      | Constraints   | Description                              |
|-------------|-----------|---------------|------------------------------------------|
| id          | BIGINT    | PRIMARY KEY   | Unique identifier for message            |
| from_user_id| BIGINT    | NOT NULL      | ID of user who sent the message          |
| message     | TEXT      | NOT NULL      | Content of the message                   |
| date        | TIMESTAMP | NOT NULL      | Date and time message was sent           |
| reply_to_id | BIGINT    |               | ID of message this is replying to (nullable)|

**Sample Data**: Currently empty in the boilerplate data.

### 9. Message Recipients (Junction Table)

Maps the many-to-many relationship between messages and their recipients.

| Column       | Type   | Constraints             | Description                    |
|-------------|--------|-------------------------|--------------------------------|
| message_id  | BIGINT | PRIMARY KEY, NOT NULL   | ID of the message              |
| recipient_id| BIGINT | PRIMARY KEY, NOT NULL   | ID of the recipient user       |

**Sample Data**: Currently empty in the boilerplate data.

### 10. Notifications

Stores notifications sent to users.

| Column          | Type      | Constraints              | Description                              |
|----------------|-----------|--------------------------|------------------------------------------|
| id             | BIGINT    | PRIMARY KEY              | Unique identifier for notification       |
| recipient_id   | BIGINT    | NOT NULL                 | ID of user receiving the notification    |
| sender_id      | BIGINT    | NOT NULL                 | ID of user who triggered the notification|
| message_preview| TEXT      | NOT NULL                 | Preview text of the notification         |
| timestamp      | TIMESTAMP | NOT NULL                 | Date and time notification was created   |
| is_read        | BOOLEAN   | NOT NULL, DEFAULT FALSE  | Whether notification has been read       |

**Sample Data**: Currently empty in the boilerplate data.

## Usage

### Creating the Database Schema

To create all tables in your database, execute the schema file:

```sql
-- Run the schema.sql file
\i src/main/resources/db/schema.sql
```

Or using command line (PostgreSQL example):
```bash
psql -U your_username -d your_database -f src/main/resources/db/schema.sql
```

### Loading Boilerplate Data

To populate the database with sample data for testing and development:

```sql
-- Run the insert_data.sql file
\i src/main/resources/db/insert_data.sql
```

Or using command line:
```bash
psql -U your_username -d your_database -f src/main/resources/db/insert_data.sql
```

### Complete Setup (Schema + Data)

To set up both schema and data, run the commands sequentially:

```bash
psql -U your_username -d your_database -f src/main/resources/db/schema.sql
psql -U your_username -d your_database -f src/main/resources/db/insert_data.sql
```

## Key Relationships

1. **Persons ↔ Ducks (via Friendships)**: Both persons and ducks can be friends with each other through the friendships table.

2. **Ducks ↔ Flocks (via Flock Members)**: Many-to-many relationship allowing ducks to belong to multiple flocks.

3. **Ducks ↔ Race Events (via Race Event Participants)**: Many-to-many relationship allowing ducks to participate in multiple races.

4. **Messages ↔ Users (via Message Recipients)**: Messages can have multiple recipients, supporting group messaging.

5. **Messages (self-referencing)**: Messages can reply to other messages through the reply_to_id field.

6. **Notifications**: Links sender and recipient users for notification management.

## Notes

- **User IDs**: The schema uses a shared ID space where persons have IDs starting from 21 and ducks have IDs from 1-20 in the sample data.
- **Password Storage**: The current sample data shows plain text passwords. In production, passwords should be properly hashed using bcrypt or similar algorithms.
- **Foreign Key Constraints**: The schema does not explicitly define foreign key constraints in the CREATE TABLE statements. These may be enforced at the application level or should be added for referential integrity.
- **Indexing**: Consider adding indexes on frequently queried columns (e.g., username, email, foreign key columns) for better performance.

## Future Enhancements

Potential improvements to the schema:
1. Add explicit foreign key constraints for referential integrity
2. Add indexes for better query performance
3. Add created_at/updated_at timestamps to track record changes
4. Implement soft deletes (deleted_at) instead of hard deletes
5. Add constraints for data validation (e.g., email format, duck_type enum)
6. Normalize duck_type into a separate lookup table if more types are added
