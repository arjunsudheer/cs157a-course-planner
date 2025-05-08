-- Use transactions to ensure that Students Table does not have incomplete starting data
BEGIN;
    -- Create Students table
    CREATE TABLE Students (
        -- Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
        StudentID SERIAL PRIMARY KEY,
        Nickname VARCHAR(100) NOT NULL,
        StudentName VARCHAR(100) NOT NULL,
        Year VARCHAR(9) NOT NULL
    );

    -- Insert 15 rows of initial data into Students table
    INSERT INTO Students(StudentName, Nickname, Year) VALUES 
        ('Bob', 'Bob', 'Freshman'),
        ('Tom', 'Tom', 'Freshman'),
        ('Jeff', 'Jeff', 'Freshman'),
        ('Billy', 'Billy', 'Freshman'),
        ('Sarah', 'Sarah', 'Sophomore'),
        ('Grace', 'Grace', 'Sophomore'),
        ('Becky', 'Becky', 'Sophomore'),
        ('Linda', 'Linda', 'Sophomore'),
        ('Timmy', 'Tim', 'Junior'),
        ('Tyler', 'Ty', 'Junior'),
        ('Melinda', 'Mel', 'Junior'),
        ('Aditya', 'Adi', 'Senior'),
        ('Arnav', 'Arnav', 'Senior'),
        ('Pranav', 'Pranav', 'Senior'),
        ('Ethan', 'Ethan', 'Senior');

COMMIT;

-- Use transactions to ensure that Course Table does not have incomplete starting data
BEGIN;
    -- Create Courses table if it doesn't exist
    CREATE TABLE Courses (
        -- Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
        CourseID SERIAL PRIMARY KEY,
        CourseName VARCHAR(100) NOT NULL,
        ProfessorName VARCHAR(100) NOT NULL,
        Units INTEGER NOT NULL,
        SeatsOpen INTEGER NOT NULL,
        TermsOffered VARCHAR(11) NOT NULL,
        DaysOfWeek VARCHAR(9) NOT NULL
    );

    -- Insert 15 rows of initial data into Courses table
    INSERT INTO Courses(CourseName, ProfessorName, Units, SeatsOpen, TermsOffered, DaysOfWeek) VALUES
        ('Introduction to Programming', 'Dr. Smith', 3, 10, 'All', 'Monday'),
        ('Introduction to Engineering', 'Dr. Bob', 3, 15, 'All', 'Tuesday'),
        ('Calculus 3', 'Dr. Jeff', 3, 20, 'All', 'Wednesday'),
        ('Introduction to Data Structures', 'Dr. Samantha', 3, 5, 'All', 'Thursday'),
        ('Physics Mechanics', 'Dr. Sarah', 3, 7, 'All', 'Friday'),
        ('Physics Electricity and Magnetism', 'Dr. Michelle', 3, 9, 'All', 'Monday'),
        ('Introduction to English', 'Dr. Martin', 3, 10, 'All Fall', 'Tuesday'),
        ('English 2', 'Dr. Maria', 3, 10, 'All Spring', 'Wednesday'),
        ('Introduction to Biology', 'Dr. Wilson', 4, 3, 'All Fall', 'Monday'),
        ('Discrete Math', 'Dr. Jimmy', 3, 10, 'Fall 2025', 'Friday'),
        ('Introduction to Database Management Systems', 'Dr. Smith', 3, 10, 'All', 'Monday'),
        ('Introduction to Programming', 'Dr. Jim', 3, 12, 'All', 'Wednesday'),
        ('Operating Systems', 'Dr. Nguyen', 4, 14, 'Spring 2024', 'Thursday'),
        ('Computer Networks', 'Dr. Taylor', 3, 16, 'All', 'Tuesday'),
        ('Computer Architecture', 'Dr. Singh', 3, 18, 'All', 'Friday');

COMMIT;

-- Use transactions to ensure that PlannedEnrollments Table does not have incomplete starting data
BEGIN;
    -- Create PlannedEnrollments table
    CREATE TABLE PlannedEnrollments (
        StudentID INTEGER NOT NULL REFERENCES Students(StudentID), -- Foreign key to StudentID primary key from Students table
        CourseID INTEGER NOT NULL REFERENCES Courses(CourseID), -- Foreign key to CourseID primary key from Courses table
        Term VARCHAR(11) NOT NULL,
        IsRetaking BOOLEAN NOT NULL,
        PRIMARY KEY (StudentID, CourseID, Term) -- Composite primary key
    );

    -- Insert 15 rows of initial data into PlannedEnrollments table
    INSERT INTO PlannedEnrollments(StudentID, CourseID, Term, IsRetaking) VALUES
        (1, 1, 'All', FALSE),
        (2, 1, 'All', FALSE),
        (3, 1, 'All', TRUE),
        (4, 1, 'All', TRUE),
        (5, 2, 'All', FALSE),
        (6, 2, 'All', FALSE),
        (7, 2, 'All', TRUE),
        (8, 2, 'All', TRUE),
        (9, 3, 'All', FALSE),
        (10, 3, 'All', FALSE),
        (11, 3, 'All', TRUE),
        (12, 3, 'All', TRUE),
        (13, 4, 'Spring 2024', FALSE),
        (14, 4, 'All Fall', FALSE),
        (15, 4, 'All Spring', TRUE);

COMMIT;


-- Use transactions to ensure that Grades Table does not have incomplete starting data
BEGIN;
    -- Create Grades table
    CREATE TABLE Grades (
        StudentID INTEGER NOT NULL REFERENCES Students(StudentID), -- Foreign key to StudentID primary key from Students table,
        CourseID INTEGER NOT NULL REFERENCES Courses(CourseID), -- Foreign key to CourseID primary key from Courses table,
        Term VARCHAR(11) NOT NULL,
        Grade VARCHAR(2) NOT NULL,
        PRIMARY KEY (StudentID, CourseID, Term) -- Composite primary key
    );

    -- Insert 15 rows of initial data into Grades table
    INSERT INTO Grades(StudentID, CourseID, Term, Grade) VALUES
        (15, 1, 'All', 'A'),
        (15, 2, 'All', 'B'),
        (15, 3, 'All', 'B-'),
        (14, 1, 'All', 'A'),
        (14, 2, 'All', 'C'),
        (14, 3, 'All', 'B'),
        (13, 1, 'All', 'A-'),
        (13, 2, 'All', 'B+'),
        (9, 1, 'All', 'A-'),
        (3, 1, 'All', 'F'),
        (4, 1, 'All', 'D'),
        (7, 2, 'All', 'D'),
        (8, 2, 'Spring 2024', 'D-'),
        (11, 3, 'All Fall', 'F'),
        (12, 3, 'All Spring', 'D');

COMMIT;
