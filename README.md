# cs157a-course-planner

This repository is dedicated to our CS 157A Database Management Systems class project. This guide assumes you have Ubuntu Linux installed.

## Database Setup

We use PostgreSQL for our DBMS. You can install PostgreSQL on your system by running the following commands in your terminal:

```
sudo apt update
sudo apt install postgresql postgresql-contrib
```

Confirm your installation:

```
psql --version
```

Enter the psql interactive shell as the postgres user (all permissions granted):

```
sudo -u postgres psql
```

Our backend expects a user called "student" with a password of "pass". The student user should have permission to created databases, and should own the courseplanner database. You can create a new user by running the following commands in your terminal:

```
CREATE USER student WITH PASSWORD 'pass' CREATEDB;
```

While you are still in the psql interactive shell, type _\du_ to verify that the student user was created successfully.

You can create the courseplanner database with the newly created student user as the owner by running the following command in your terminal:

```
CREATE DATABASE courseplanner WITH OWNER = 'student';
```

While you are still in the psql interactive shell, you can type _\l_ to verify that the courseplanner database was created, and the student user is the owner.

To exit the psql interactive shell, type _\q_.

## Running the backend

All the backend files are located within the backend directory. Navigate to the backend directory by executing the following command in your terminal:

```
cd backend
```

Start the PostgreSQL DBMS:

```
sudo service postgresql start
```

### Setting up the tables and initial data

The provided create_schema.sql (located in the src directory) will create the required tables and insert some initial data. To run this script, type the following command in your terminal:

```
psql -h localhost -p 5432 -U student -d courseplanner -a -f src/create_schema.sql
```

To verify that the tables were created, you can enter the psql interactive shell:

```
sudo -u postgres psql
```

To view the tables in the courseplanner database, type the following commands into the psql interactive shell:

```
\c courseplanner
\dt
```

The first command will select the courseplanner database. The second command will list all tables that exist in the selected database.

After verifying that the tables were created, exit the psql interactive shell by typing the following command into the psql interactive shell:

```
\q
```

### Starting the SpringBoot backend server

For our backend we use a Spring Boot project with Maven. You can start running the backend by running the following commands in your terminal:

```
mvn spring-boot:run
```

### Stopping the PostgreSQL DBMS

When you are ready to stop running the backend, you can stop the PostgreSQL DBMS by running the following command in your terminal window:

```
sudo service postgresql stop
```

## Running the frontend

For our frontend we use React. Please create a new terminal window so your can run the frontend and backend simultaneously. You can start running the frontend by running the following commands in your terminal:

```
cd frontend
npm install
npm run dev
```

For MAC:

## Database Setup

```
sudo apt update
sudo apt install postgresql postgresql-contrib
```

Confirm your installation:

```
psql --version
```

```
psql --version
```

Enter the psql interactive shell as the postgres user (all permissions granted):

```
sudo -u postgres psql
```

Our backend expects a user called "student" with a password of "pass". The student user should have permission to created databases, and should own the courseplanner database. You can create a new user by running the following commands in your terminal:

```
CREATE USER student WITH PASSWORD 'pass' CREATEDB;
```

While you are still in the psql interactive shell, type _\du_ to verify that the student user was created successfully.

You can create the courseplanner database with the newly created student user as the owner by running the following command in your terminal:

```
CREATE DATABASE courseplanner WITH OWNER = 'student';
```

While you are still in the psql interactive shell, you can type _\l_ to verify that the courseplanner database was created, and the student user is the owner.

To exit the psql interactive shell, type _\q_.
