# cs157a-course-planner

This repository is dedicated to our CS 157A Database Management Systems class project.

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

First start the PostgreSQL DBMS:

```
sudo service postgresql start
```

For our backend we use a Spring Boot project with Maven. You can start running the backend by running the following commands in your terminal:

```
cd backend
mvn spring-boot:run
```

To stop the PostgreSQL DMBS:

```
sudo service postgresql stop
```

## Running the frontend

For our frontend we use React. Please create a new terminal window so your can run the frontend and backend simultaneously. You can start running the frontend by running the following commands in your terminal:

```
cd frontend
npm run dev
```
