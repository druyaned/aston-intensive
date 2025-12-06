/*
 * 1. Create "src/main/resources/db-connection.properties" file and write down
 *   something like this (change <DB_USER> and <DB_PASSWORD>):
 * '''
 * spring.datasource.url=jdbc:postgresql://localhost:5432/user_service_<DB_USER>
 * spring.datasource.username=<DB_USER>
 * spring.datasource.password=<DB_PASSWORD>
 * '''
 *
 * 2. Make changes to this file by the same <DB_USER> and <DB_PASSWORD>
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

CREATE ROLE /* <DB_USER> */ WITH
    LOGIN
    PASSWORD /* <DB_PASSWORD> */;

CREATE DATABASE user_service_/* <DB_USER> */ WITH
    OWNER = druyaned
    ENCODING = 'UTF8';

\c user_service_/* <DB_USER> */

CREATE SCHEMA AUTHORIZATION /* <DB_USER> */;

GRANT ALL ON ALL TABLES IN SCHEMA /* <DB_USER> */ TO /* <DB_USER> */;
