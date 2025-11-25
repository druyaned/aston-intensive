/*
 * 1. Create "src/main/resources/db-connection.properties" file and write down
 *   something like this (change <USERNAME> and <PASSWORD>):
 * '''
 * spring.datasource.url=jdbc:postgresql://localhost:5432/user_service_<USERNAME>
 * spring.datasource.username=<USERNAME>
 * spring.datasource.password=<PASSWORD>
 * '''
 *
 * 2. Make changes to this file by the same <USERNAME> and <PASSWORD>
 *
 * 3. Then this script can be executed by console command (in case PostgreSQL
 *   databse is installed, as well as "psql" CLI):
 * '''
 * $ psql -d postgres -U postgres -f db-create.sql
 * '''
 * Or simply run each command one by one anywhere you want
 *
 * 4. So the database is ready to use and the app can be launched
 */

CREATE ROLE /* <USERNAME> */ WITH
    LOGIN
    PASSWORD /* <PASSWORD> */;

CREATE DATABASE user_service_/* <USERNAME> */ WITH
    OWNER = druyaned
    ENCODING = 'UTF8';

\c user_service_/* <USERNAME> */

CREATE SCHEMA AUTHORIZATION /* <USERNAME> */;

GRANT ALL ON ALL TABLES IN SCHEMA /* <USERNAME> */ TO /* <USERNAME> */;
