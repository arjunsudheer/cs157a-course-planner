-- Use transactions to ensure that all the initial data is added completely
BEGIN;
    -- Insert the admin user
    INSERT INTO Students VALUES
        (0, 'Admin', 'Senior')
    ON CONFLICT (StudentID) DO NOTHING; -- Error handling to do nothing if the admin already exists

    -- Insert 15 rows of initial data into Students table
    INSERT INTO Students(StudentName, Year) VALUES 
        ('Bob', 'Freshman'),
        ('Tom', 'Freshman'),
        ('Jeff', 'Freshman'),
        ('Billy', 'Freshman'),
        ('Sarah', 'Sophomore'),
        ('Grace', 'Sophomore'),
        ('Becky', 'Sophomore'),
        ('Linda', 'Sophomore'),
        ('Timmy', 'Junior'),
        ('Tyler', 'Junior'),
        ('Melinda', 'Junior'),
        ('Aditya', 'Senior'),
        ('Arnav', 'Senior'),
        ('Pranav', 'Senior'),
        ('Ethan', 'Senior')
    ON CONFLICT DO NOTHING; -- Error handling to do nothing if the Students table does not exist or a duplicate row exists

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
        ('Computer Architecture', 'Dr. Singh', 3, 18, 'All', 'Friday')
    ON CONFLICT DO NOTHING; -- Error handling to do nothing if the Courses table does not exist or a duplicate row exists

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
        (15, 4, 'Spring 2026', TRUE)
    ON CONFLICT DO NOTHING; -- Error handling to do nothing if the PlannedEnrollments table does not exist or a duplicate row exists

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
        (12, 3, 'Fall 2024', 'D')
    ON CONFLICT DO NOTHING; -- Error handling to do nothing if the Grades table does not exist or a duplicate row exists

COMMIT;
