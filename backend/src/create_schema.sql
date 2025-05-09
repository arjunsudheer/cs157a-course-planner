-- Use transactions to ensure that all tables are created completely
BEGIN;
    -- Drop tables if they exist to make the script re-runnable
    DROP TABLE IF EXISTS Grades, PlannedEnrollments, Courses, Students CASCADE;

    -- Create Students table
    CREATE TABLE Students (
        -- Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
        StudentID SERIAL PRIMARY KEY,
        StudentName VARCHAR(100) NOT NULL,
        -- Can only be "Freshman", "Sophomore", "Junior", or "Senior"
        Year VARCHAR(9) NOT NULL CHECK (Year IN ('Freshman', 'Sophomore', 'Junior', 'Senior'))
    );

    -- Create Courses table
    CREATE TABLE Courses (
        -- Use SERIAL keyword in PostgreSQL DB to make auto-incremented integer value
        CourseID SERIAL PRIMARY KEY,
        CourseName VARCHAR(100) NOT NULL,
        ProfessorName VARCHAR(100) NOT NULL,
        -- Should be between 1 and 4 units
        Units INTEGER NOT NULL CHECK (Units >= 1 AND Units <= 4),
        -- Should be less than 100 and non-negative
        SeatsOpen INTEGER NOT NULL CHECK (SeatsOpen >= 0 AND SeatsOpen <= 100),
        -- Can only be "All", "Spring", "Fall", "Winter", or "Summer"
        TermOffered VARCHAR(6) NOT NULL CHECK ( TermOffered IN ('All', 'Spring', 'Fall', 'Winter', 'Summer')) ,
        -- Can only be on weekdays
        DayOfWeek VARCHAR(9) NOT NULL CHECK (DayOfWeek IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'))
    );

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

COMMIT;
