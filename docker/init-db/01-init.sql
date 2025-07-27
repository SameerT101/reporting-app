-- Database init script

CREATE USER demo WITH PASSWORD 'demo' CREATEDB;
ALTER USER demo CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE postgres TO demo;

-- Switch to the postgres database
\c postgres;

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO demo;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO demo;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO demo;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO demo;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO demo;

-- Note: The actual table creation is handled by Flyway migrations in the Spring Boot application