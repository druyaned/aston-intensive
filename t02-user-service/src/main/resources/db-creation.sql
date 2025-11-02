/*
 * The script is written for PostgreSQL Database, which should be installed.
 * This file can be executed by the following console command:
 * "psql -d postgres -U postgres -f db-creation.sql".
 * Next step is to type the password in. After the execution an access to the
 * created DB "user_service_druyaned" can be gotten by the App or the command:
 * "psql -d user_service_druyaned -U druyaned".
 */

CREATE ROLE druyaned WITH LOGIN PASSWORD '0808';

CREATE DATABASE user_service_druyaned
    WITH OWNER = druyaned
    ENCODING = 'UTF8';

\c user_service_druyaned

CREATE SCHEMA AUTHORIZATION druyaned;

GRANT ALL ON ALL TABLES IN SCHEMA druyaned TO druyaned;
