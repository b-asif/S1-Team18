CREATE DATABASE IF NOT EXISTS trackhire;
USE trackhire;

CREATE TABLE IF NOT EXISTS Users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(100) NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    userName VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    isAdmin TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS Applications (
    applicationId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    jobTitle VARCHAR(255) NOT NULL,
    companyName VARCHAR(255) NOT NULL,
    appStatus VARCHAR(50) NOT NULL DEFAULT 'Applied',
    dateApplied DATE NOT NULL,
    jobUrl VARCHAR(512) NULL,
    jobLocation VARCHAR(255) NULL,
    notes TEXT NULL,
    CONSTRAINT fk_applications_user
        FOREIGN KEY (userId) REFERENCES Users(userId)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Interviews (
    interviewId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    roleTitle VARCHAR(255) NOT NULL,
    departmentName VARCHAR(255),
    interviewerName VARCHAR(255),
    interviewType VARCHAR(100),
    interviewDate DATE NOT NULL,
    startTime TIME NOT NULL,
    endTime TIME NULL,
    location VARCHAR(255),
    notes TEXT,
    CONSTRAINT fk_interviews_user
        FOREIGN KEY (userId) REFERENCES Users(userId)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS technicals (
    assessmentId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    assessmentTitle VARCHAR(255) NOT NULL,
    assignedDate DATE NOT NULL,
    dueDate DATE NULL,
    assessmentNotes TEXT,
    completionStatus VARCHAR(100),
    scoreOrPassFail VARCHAR(100),
    CONSTRAINT fk_technicals_user
        FOREIGN KEY (userId) REFERENCES Users(userId)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PasswordResetTokens (
    resetId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    tokenHash VARCHAR(255) NOT NULL UNIQUE,
    expiresAt DATETIME NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (userId) REFERENCES Users(userId)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Tag (
    tagId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_tag_user
        FOREIGN KEY (userId) REFERENCES Users(userId)
        ON DELETE CASCADE,
    CONSTRAINT uq_tag_user_name UNIQUE (userId, name)
);

CREATE TABLE IF NOT EXISTS ApplicationTag (
    applicationId INT NOT NULL,
    tagId INT NOT NULL,
    PRIMARY KEY (applicationId, tagId),
    CONSTRAINT fk_app_tag_application
        FOREIGN KEY (applicationId) REFERENCES Applications(applicationId)
        ON DELETE CASCADE,
    CONSTRAINT fk_app_tag_tag
        FOREIGN KEY (tagId) REFERENCES Tag(tagId)
        ON DELETE CASCADE
);

-- Demo accounts (BCrypt plaintext for both: password)
--   alicej  - ADMIN  - /users and directory edit
--   brianl  - regular user - sample pipeline (applications, tags, interviews, technicals)
INSERT INTO Users (firstName, lastName, email, userName, `password`, isAdmin) VALUES
('Alice', 'Johnson', 'alice@example.com', 'alicej', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1),
('Brian', 'Lee', 'brian@example.com', 'brianl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0)
ON DUPLICATE KEY UPDATE
firstName = VALUES(firstName),
lastName = VALUES(lastName),
`password` = VALUES(`password`),
isAdmin = VALUES(isAdmin);

-- Restores demo login password if hashes were overwritten (e.g. copied from another user during debugging).
-- Plaintext for alicej and brianl is: password
UPDATE Users SET `password` = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE userName IN ('alicej', 'brianl');

-- Re-run note: only Users upserts above; application rows append unless you drop/truncate tables first.

-- Brian (brianl): 9 applications — all statuses + varied seniority / fields
INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Intern Software Engineer', 'Northwind Labs', 'Applied', '2026-04-28', 'https://careers.example.com/northwind/intern-2026', 'Boston, MA (hybrid)', 'Campus career fair follow-up.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Junior Full Stack Developer', 'PixelForge Inc', 'Applied', '2026-05-02', 'https://jobs.example.com/pixelforge/jrt-fs-42', 'Remote (US)', 'Stack: React + Spring. Enthusiastic about design system work.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Associate Data Engineer', 'Riverdale Analytics', 'Applied', '2026-05-05', NULL, 'Chicago, IL', 'Referral from alumni; recruiter phone screen scheduled.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Mid-Level Backend Engineer', 'CloudScout', 'Interviewing', '2026-04-10', 'https://cloudscout.example.com/jobs/be-mid-7', 'Seattle, WA', 'Passed HM screen; onsite panel 2026-05-12. Focus on distributed systems.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Lead Developer', 'BrightMatter SaaS', 'Interviewing', '2026-03-22', 'https://brightmatter.example.com/careers/lead-dev', 'Austin, TX (hybrid)', 'Take-home review next; small team leadership emphasis.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Senior Platform Engineer', 'Orbit Commerce', 'Offer', '2026-02-14', 'https://orbit.example.com/jobs/senior-platform', 'Remote (US)', 'Verbal offer 2026-05-08; compensation packet in email. Deadline to respond 2026-05-20.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Staff Software Engineer', 'Unicorn Dynamics', 'Rejected', '2026-01-30', 'https://unicornd.example.com/staff-swe', 'San Francisco, CA', 'Strong panel feedback but role filled internally. Closed 2026-04-18.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Principal Engineer', 'Helix Therapeutics', 'Withdrawn', '2026-03-01', NULL, 'Cambridge, MA', 'Withdrew after accepting another offer; stay in touch with hiring manager.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Mobile Engineer (iOS)', 'StudioWave Games', 'Applied', '2026-05-07', 'https://jobs.example.com/studiowave/ios', 'Los Angeles, CA', 'Portfolio link sent in cover letter.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

-- Alice (alicej): small admin sample
INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes)
SELECT u.userId, 'Engineering Manager', 'Acme Systems', 'Interviewing', '2026-04-05', 'https://acme.example.com/careers/em', 'Denver, CO', 'Team of eight; charter includes platform reliability.'
FROM Users u WHERE u.userName = 'alicej' LIMIT 1;

-- Tags for Brian (application tag chips / filters)
INSERT INTO Tag (userId, name)
SELECT u.userId, v.name FROM Users u
JOIN (
    SELECT 'Remote-first' AS name
    UNION ALL SELECT 'Referral'
    UNION ALL SELECT 'Leetcode-heavy'
    UNION ALL SELECT 'Offer-stage'
) AS v
WHERE u.userName = 'brianl';

INSERT INTO ApplicationTag (applicationId, tagId)
SELECT a.applicationId, t.tagId FROM Applications a
JOIN Users u ON u.userId = a.userId AND u.userName = 'brianl'
JOIN Tag t ON t.userId = a.userId AND t.name = 'Remote-first'
WHERE a.jobTitle = 'Junior Full Stack Developer' AND a.companyName = 'PixelForge Inc';

INSERT INTO ApplicationTag (applicationId, tagId)
SELECT a.applicationId, t.tagId FROM Applications a
JOIN Users u ON u.userId = a.userId AND u.userName = 'brianl'
JOIN Tag t ON t.userId = a.userId AND t.name = 'Remote-first'
WHERE a.jobTitle = 'Senior Platform Engineer' AND a.companyName = 'Orbit Commerce';

INSERT INTO ApplicationTag (applicationId, tagId)
SELECT a.applicationId, t.tagId FROM Applications a
JOIN Users u ON u.userId = a.userId AND u.userName = 'brianl'
JOIN Tag t ON t.userId = a.userId AND t.name = 'Referral'
WHERE a.jobTitle = 'Associate Data Engineer' AND a.companyName = 'Riverdale Analytics';

INSERT INTO ApplicationTag (applicationId, tagId)
SELECT a.applicationId, t.tagId FROM Applications a
JOIN Users u ON u.userId = a.userId AND u.userName = 'brianl'
JOIN Tag t ON t.userId = a.userId AND t.name = 'Leetcode-heavy'
WHERE a.jobTitle = 'Staff Software Engineer' AND a.companyName = 'Unicorn Dynamics';

INSERT INTO ApplicationTag (applicationId, tagId)
SELECT a.applicationId, t.tagId FROM Applications a
JOIN Users u ON u.userId = a.userId AND u.userName = 'brianl'
JOIN Tag t ON t.userId = a.userId AND t.name = 'Offer-stage'
WHERE a.jobTitle = 'Senior Platform Engineer' AND a.companyName = 'Orbit Commerce';

-- Interviews
INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, interviewType, interviewDate, startTime, endTime, location, notes)
SELECT u.userId, 'Mid-Level Backend Engineer', 'Platform', 'John Okonkwo', 'Virtual', '2026-05-04', '15:00:00', '16:00:00', 'Zoom', 'HM screen completed; discussed cache invalidation.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, interviewType, interviewDate, startTime, endTime, location, notes)
SELECT u.userId, 'Mid-Level Backend Engineer', 'Platform', 'Sarah Chen & team', 'Onsite', '2026-05-12', '10:00:00', '15:30:00', 'CloudScout Seattle HQ', 'Panel + lunch; bring laptop for architecture exercise.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, interviewType, interviewDate, startTime, endTime, location, notes)
SELECT u.userId, 'Lead Developer', 'Product Engineering', 'Marcus Reed', 'Virtual', '2026-05-11', '14:00:00', '14:45:00', 'Google Meet', 'Take-home walkthrough; prep slides optional.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, interviewType, interviewDate, startTime, endTime, location, notes)
SELECT u.userId, 'Senior Platform Engineer', 'Infrastructure', 'Priya Natarajan', 'Virtual', '2026-05-09', '11:00:00', '12:00:00', 'Zoom', 'Offer negotiation / benefits Q&A.'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, interviewType, interviewDate, startTime, endTime, location, notes)
SELECT u.userId, 'Engineering Manager', 'Engineering', 'Dana Kim', 'Hybrid', '2026-05-15', '09:30:00', '11:00:00', 'Acme Denver office', 'Loop with director + peer EM.'
FROM Users u WHERE u.userName = 'alicej' LIMIT 1;

-- Technical assessments
INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, assessmentNotes, completionStatus, scoreOrPassFail)
SELECT u.userId, 'Systems design packet', '2026-05-06', '2026-05-13', 'Submit architecture doc for notification pipeline.', 'Pending', 'N/A'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, assessmentNotes, completionStatus, scoreOrPassFail)
SELECT u.userId, 'HackerRank timed assessment', '2026-04-28', '2026-05-03', '90 min; algorithms + SQL.', 'Pending', 'N/A'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, assessmentNotes, completionStatus, scoreOrPassFail)
SELECT u.userId, 'Take-home: REST API mini service', '2026-05-01', '2026-05-08', 'Java 17 + JUnit; README required.', 'Completed', 'Pass'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, assessmentNotes, completionStatus, scoreOrPassFail)
SELECT u.userId, 'Security review quiz', '2026-04-20', '2026-04-25', 'OWASP top 10 fundamentals.', 'Completed', '92%'
FROM Users u WHERE u.userName = 'brianl' LIMIT 1;

INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, assessmentNotes, completionStatus, scoreOrPassFail)
SELECT u.userId, 'Leadership scenario written exercise', '2026-05-09', '2026-05-16', '2-page max on incident response & stakeholder comms.', 'Pending', 'N/A'
FROM Users u WHERE u.userName = 'alicej' LIMIT 1;
