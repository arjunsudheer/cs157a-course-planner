-- Use transactions to ensure that Students Table does not have incomplete starting data
BEGIN;
    -- Create Students table
    CREATE TABLE Students (
        -- Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
        StudentID SERIAL PRIMARY KEY,
        Nickname VARCHAR(100) NOT NULL,
        StudentName VARCHAR(100) NOT NULL,
        -- Can only be "Freshman", "Sophomore", "Junior", or "Senior"
        Year VARCHAR(9) NOT NULL CHECK (Year IN ('Freshman', 'Sophomore', 'Junior', 'Senior'))
    );

    -- Insert the admin user
    INSERT INTO Students VALUES
        (0, 'Admin', 'Admin', 'Senior');

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
    -- Create Courses table
    CREATE TABLE Courses (
        -- Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
        CourseID SERIAL PRIMARY KEY,
        CourseName VARCHAR(100) NOT NULL,
        ProfessorName VARCHAR(100) NOT NULL,
        -- Should be between 1 and 4 units
        Units INTEGER NOT NULL CHECK (Units >= 1 AND Units <= 4),
        -- Should be less than 100 and positive
        SeatsOpen INTEGER NOT NULL CHECK (SeatsOpen > 0 AND SeatsOpen <= 100),
        -- Can only be "All", "Spring", "Fall", "Winter", or "Summer"
        TermOffered VARCHAR(6) NOT NULL CHECK ( TermOffered IN ('All', 'Spring', 'Fall', 'Winter', 'Summer')) ,
        -- Can only be on weekdays
        DayOfWeek VARCHAR(9) NOT NULL CHECK (DayOfWeek IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'))
    );

    -- Insert 15 rows of initial data into Courses table
    INSERT INTO Courses(CourseName, ProfessorName, Units, SeatsOpen, TermOffered, DayOfWeek) VALUES
        ('Introduction to Programming', 'Dr. Smith', 3, 10, 'All', 'Monday'),
        ('Introduction to Engineering', 'Dr. Bob', 3, 15, 'All', 'Tuesday'),
        ('Calculus 3', 'Dr. Jeff', 3, 20, 'All', 'Wednesday'),
        ('Introduction to Data Structures', 'Dr. Samantha', 3, 5, 'All', 'Thursday'),
        ('Physics Mechanics', 'Dr. Sarah', 3, 7, 'All', 'Friday'),
        ('Physics Electricity and Magnetism', 'Dr. Michelle', 3, 9, 'All', 'Monday'),
        ('Introduction to English', 'Dr. Martin', 1, 10, 'Fall', 'Tuesday'),
        ('English 2', 'Dr. Maria', 3, 10, 'Spring', 'Wednesday'),
        ('Introduction to Biology', 'Dr. Wilson', 4, 3, 'Fall', 'Monday'),
        ('Discrete Math', 'Dr. Jimmy', 3, 10, 'Winter', 'Friday'),
        ('Introduction to Database Management Systems', 'Dr. Smith', 3, 10, 'All', 'Monday'),
        ('Introduction to Programming', 'Dr. Jim', 3, 12, 'Winter', 'Wednesday'),
        ('Operating Systems', 'Dr. Nguyen', 4, 14, 'Spring', 'Thursday'),
        ('Computer Networks', 'Dr. Taylor', 3, 16, 'Summer', 'Tuesday'),
        ('Computer Architecture', 'Dr. Singh', 3, 18, 'All', 'Friday');

COMMIT;

-- Use transactions to ensure that PlannedEnrollments Table does not have incomplete starting data
BEGIN;
    -- Create PlannedEnrollments table
    CREATE TABLE PlannedEnrollments (
        StudentID INTEGER NOT NULL REFERENCES Students(StudentID) ON DELETE CASCADE ON UPDATE CASCADE, -- Foreign key to StudentID primary key from Students table
        CourseID INTEGER NOT NULL REFERENCES Courses(CourseID) ON DELETE CASCADE ON UPDATE CASCADE, -- Foreign key to CourseID primary key from Courses table
        -- PostgreSQL regex documentation: https://www.postgresql.org/docs/current/functions-matching.html
        -- Allow terms between 2023 and 2099
        Term VARCHAR(11) NOT NULL CHECK (Term ~ '^(Fall|Spring|Summer|Winter) (202[5-9]|20[3-9][0-9])$'),
        IsRetaking BOOLEAN NOT NULL,
        PRIMARY KEY (StudentID, CourseID, Term) -- Composite primary key
    );

    -- Insert 15 rows of initial data into PlannedEnrollments table
    INSERT INTO PlannedEnrollments(StudentID, CourseID, Term, IsRetaking) VALUES
        (1, 1, 'Fall 2025', FALSE),
        (2, 1, 'Fall 2025', FALSE),
        (3, 1, 'Fall 2025', TRUE),
        (4, 1, 'Fall 2025', TRUE),
        (5, 2, 'Spring 2026', FALSE),
        (6, 2, 'Spring 2026', FALSE),
        (7, 2, 'Spring 2026', TRUE),
        (8, 2, 'Spring 2026', TRUE),
        (9, 3, 'Spring 2026', FALSE),
        (10, 3, 'Fall 2026', FALSE),
        (11, 3, 'Fall 2026', TRUE),
        (12, 3, 'Fall 2026', TRUE),
        (13, 4, 'Spring 2026', FALSE),
        (14, 4, 'Spring 2026', FALSE),
        (15, 4, 'Spring 2026', TRUE);

COMMIT;


-- Use transactions to ensure that Grades Table does not have incomplete starting data
BEGIN;
    -- Create Grades table
    CREATE TABLE Grades (
        StudentID INTEGER NOT NULL REFERENCES Students(StudentID) ON DELETE CASCADE ON UPDATE CASCADE, -- Foreign key to StudentID primary key from Students table
        CourseID INTEGER NOT NULL REFERENCES Courses(CourseID) ON DELETE CASCADE ON UPDATE CASCADE, -- Foreign key to CourseID primary key from Courses table
        -- PostgreSQL regex documentation: https://www.postgresql.org/docs/current/functions-matching.html
        -- Allow terms between 2023 and 2099
        Term VARCHAR(11) NOT NULL CHECK (Term ~ '^(Fall|Spring|Summer|Winter) (202[3-9]|20[3-9][0-9])$'),
        -- Only allow valid A-F grades
        Grade VARCHAR(2) NOT NULL CHECK (Grade IN ('A', 'A+', 'A-', 'B', 'B+', 'B-', 'C', 'C+', 'C-', 'D', 'D+', 'D-', 'F')),
        PRIMARY KEY (StudentID, CourseID, Term) -- Composite primary key
    );

    -- Insert 15 rows of initial data into Grades table
    INSERT INTO Grades(StudentID, CourseID, Term, Grade) VALUES
        (15, 1, 'Fall 2023', 'A'),
        (15, 2, 'Fall 2023', 'B'),
        (15, 3, 'Fall 2023', 'B-'),
        (14, 1, 'Fall 2023', 'A'),
        (14, 2, 'Spring 2023', 'C'),
        (14, 3, 'Spring 2023', 'B'),
        (13, 1, 'Spring 2023', 'A-'),
        (13, 2, 'Fall 2024', 'B+'),
        (9, 1, 'Fall 2024', 'A-'),
        (3, 1, 'Fall 2024', 'F'),
        (4, 1, 'Fall 2024', 'D'),
        (7, 2, 'Spring 2024', 'D'),
        (8, 2, 'Spring 2024', 'D-'),
        (11, 3, 'Spring 2024', 'F'),
        (12, 3, 'Fall 2024', 'D');

COMMIT;
