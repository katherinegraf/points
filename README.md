# Points
### Fetch Rewards Points Take Home Test

This app is built with Spring Boot, Gradle, and Kotlin, and connects to Postgres databases running on port 5432.
___
## Setup
Use the below SQL to generate a `points` table in a `points` database.
~~~~sql
CREATE DATABASE points;
~~~~
~~~~sql
CREATE TABLE points (
    id integer DEFAULT nextval('points_id_seq'::regclass) PRIMARY KEY,
    payer text,
    points integer,
    timestamp timestamp without time zone
);
~~~~
To run the application, use `./gradlew bootRun`

## Testing
Use the below SQL to generate a `points` table in a `points_test` database.
~~~~sql
CREATE DATABASE points_test;
~~~~
~~~~sql
CREATE TABLE points (
    id integer DEFAULT nextval('points_test_id_seq'::regclass) PRIMARY KEY,
    payer text,
    points integer,
    timestamp timestamp without time zone
);
~~~~
To run tests, use `./gradlew test`
