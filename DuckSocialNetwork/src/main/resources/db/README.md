# Database Scripts

This directory contains SQL scripts for setting up the DuckSocialNetwork database.

## Files

### `schema.sql`
Contains the complete database schema definition with all table structures.

**Usage:**
```bash
psql -U username -d database_name -f schema.sql
```

### `insert_data.sql`
Contains boilerplate/sample data for testing and development purposes.

**Data included:**
- 20 Persons (IDs 21-40)
- 20 Ducks (IDs 1-20)
- 15 Friendships
- 15 Flocks with member assignments
- 4 Race events with participants

**Usage:**
```bash
psql -U username -d database_name -f insert_data.sql
```

## Quick Setup

To set up the complete database with sample data:

```bash
# Create schema and load data
psql -U username -d database_name -f schema.sql
psql -U username -d database_name -f insert_data.sql
```

## Documentation

For detailed information about the database structure, relationships, and usage, see:
- [DATABASE_SCHEMA.md](../../DATABASE_SCHEMA.md) - Complete database schema documentation

## Notes

- Run `schema.sql` before `insert_data.sql`
- The insert script assumes the tables already exist
- Sample data uses sequential IDs; adjust as needed for your use case
