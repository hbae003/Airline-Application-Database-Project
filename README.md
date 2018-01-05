# Airline Application Database Project

This is the final project for CS166 at University of Califonia, Riverside.

This code is the product of joint efforts by myself and a classmate. We were provided the java code that connects the Postgres Database to a client application. 

**PLEASE NOTE: PostgreSQL needs to be installed on the machine to run this program**

## Contributers
* Han Sung Bae - [hbae003@ucr.edu](mailto:hbae003@ucr.edu)
* Xiao Zhou - [xzhou016@ucr.edu](mailto:xzhou016@ucr.edu)

## Installing and Running 

Instructions on Installing and running java application:

### 1. Scripts
1. Running initdb.sh creates necessary folders and initializes database. Copies csv files under /tmp/$LOGNAME/myDB/data.
2. Running start.sh starts the database.
3. Running createdb.sh creates the database.
4. Running createtb.sh creates schema and inserts data from csv files by calling create.sql script. It also creates a user that access the created table

**NOTE: Running stop.sh stops the database server**

### 2. Java
1. Running compile.sh compiles your code in src.
2. Running run.sh executes src code with inputs dbname, port, user.

example: run.sh flightDB 7432 hbae003?
