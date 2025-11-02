/*
 * 1. Create "src/main/resources/db_connection.properties" file and write down
 *   something like this:
 * '''
 * db.url = jdbc:postgresql://localhost:5432/user_service_druyaned
 * db.user = druyaned
 * db.password = <UP_TO_YOUR_IMAGINATION>
 * '''
 * 
 * 2. The password below must the one in the "db_connection.properties" file.
 * 
 * 3. After the provision of the password this script can be executed by console
 *   command (in case PostgreSQL databse is installed, as well as "psql" CLI):
 * '''
 * $ psql -d postgres -U postgres -f db-creation.sql
 * '''
 * Or simply run each command one by one anywhere you want.
 * 
 * 4. So the database is ready to use and the app can be launched.
 */

CREATE ROLE druyaned WITH LOGIN PASSWORD /* password from
    db_connection.properties */;

CREATE DATABASE user_service_druyaned
    WITH OWNER = druyaned
    ENCODING = 'UTF8';

\c user_service_druyaned

CREATE SCHEMA AUTHORIZATION druyaned;

GRANT ALL ON ALL TABLES IN SCHEMA druyaned TO druyaned;
