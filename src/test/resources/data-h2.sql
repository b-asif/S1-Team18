-- Aligns with seed.sql: userId 1 = Alice (admin), 2 = Brian (regular). Password for both: password

INSERT INTO Users (firstName, lastName, email, userName, password, isAdmin) VALUES
('Alice', 'Johnson', 'alice@example.com', 'alicej', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
('Brian', 'Lee', 'brian@example.com', 'brianl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE);

-- Brian: applications 1–9 (then Alice app id 10)
INSERT INTO Applications (userId, jobTitle, companyName, appStatus, dateApplied, jobUrl, jobLocation, notes) VALUES
(2, 'Intern Software Engineer', 'Northwind Labs', 'Applied', DATE '2026-04-28', 'https://careers.example.com/northwind/intern-2026', 'Boston, MA (hybrid)', 'Campus career fair follow-up.'),
(2, 'Junior Full Stack Developer', 'PixelForge Inc', 'Applied', DATE '2026-05-02', 'https://jobs.example.com/pixelforge/jrt-fs-42', 'Remote (US)', 'Stack: React + Spring.'),
(2, 'Associate Data Engineer', 'Riverdale Analytics', 'Applied', DATE '2026-05-05', NULL, 'Chicago, IL', 'Referral from alumni.'),
(2, 'Mid-Level Backend Engineer', 'CloudScout', 'Interviewing', DATE '2026-04-10', 'https://cloudscout.example.com/jobs/be-mid-7', 'Seattle, WA', 'Onsite panel scheduled.'),
(2, 'Lead Developer', 'BrightMatter SaaS', 'Interviewing', DATE '2026-03-22', 'https://brightmatter.example.com/careers/lead-dev', 'Austin, TX (hybrid)', 'Take-home review next.'),
(2, 'Senior Platform Engineer', 'Orbit Commerce', 'Offer', DATE '2026-02-14', 'https://orbit.example.com/jobs/senior-platform', 'Remote (US)', 'Verbal offer; respond by deadline.'),
(2, 'Staff Software Engineer', 'Unicorn Dynamics', 'Rejected', DATE '2026-01-30', 'https://unicornd.example.com/staff-swe', 'San Francisco, CA', 'Role filled internally.'),
(2, 'Principal Engineer', 'Helix Therapeutics', 'Withdrawn', DATE '2026-03-01', NULL, 'Cambridge, MA', 'Withdrew after accepting another offer.'),
(2, 'Mobile Engineer (iOS)', 'StudioWave Games', 'Applied', DATE '2026-05-07', 'https://jobs.example.com/studiowave/ios', 'Los Angeles, CA', 'Portfolio in cover letter.'),
(1, 'Engineering Manager', 'Acme Systems', 'Interviewing', DATE '2026-04-05', 'https://acme.example.com/careers/em', 'Denver, CO', 'Team of eight; platform reliability.');

INSERT INTO Tag (userId, name) VALUES
(2, 'Remote-first'),
(2, 'Referral'),
(2, 'Leetcode-heavy'),
(2, 'Offer-stage');

-- applicationId: 2=Junior+PixelForge, 3=Associate, 6=Senior+Orbit, 7=Staff
INSERT INTO ApplicationTag (applicationId, tagId) VALUES
(2, 1),
(6, 1),
(6, 4),
(3, 2),
(7, 3);

INSERT INTO Interviews (userId, roleTitle, departmentName, interviewerName, interviewType, interviewDate, startTime, endTime, location, notes) VALUES
(2, 'Mid-Level Backend Engineer', 'Platform', 'John Okonkwo', 'Virtual', DATE '2026-05-04', TIME '15:00:00', TIME '16:00:00', 'Zoom', 'HM screen completed.'),
(2, 'Mid-Level Backend Engineer', 'Platform', 'Sarah Chen & team', 'Onsite', DATE '2026-05-12', TIME '10:00:00', TIME '15:30:00', 'CloudScout Seattle HQ', 'Panel day.'),
(2, 'Lead Developer', 'Product Engineering', 'Marcus Reed', 'Virtual', DATE '2026-05-11', TIME '14:00:00', TIME '14:45:00', 'Google Meet', 'Take-home walkthrough.'),
(2, 'Senior Platform Engineer', 'Infrastructure', 'Priya Natarajan', 'Virtual', DATE '2026-05-09', TIME '11:00:00', TIME '12:00:00', 'Zoom', 'Offer discussion.'),
(1, 'Engineering Manager', 'Engineering', 'Dana Kim', 'Hybrid', DATE '2026-05-15', TIME '09:30:00', TIME '11:00:00', 'Acme Denver office', 'Director + peer EM loop.');

INSERT INTO technicals (userId, assessmentTitle, assignedDate, dueDate, assessmentNotes, completionStatus, scoreOrPassFail) VALUES
(2, 'Systems design packet', DATE '2026-05-06', DATE '2026-05-13', 'Architecture doc for notification pipeline.', 'Pending', 'N/A'),
(2, 'HackerRank timed assessment', DATE '2026-04-28', DATE '2026-05-03', '90 min; algorithms + SQL.', 'Pending', 'N/A'),
(2, 'Take-home: REST API mini service', DATE '2026-05-01', DATE '2026-05-08', 'Java 17 + JUnit.', 'Completed', 'Pass'),
(2, 'Security review quiz', DATE '2026-04-20', DATE '2026-04-25', 'OWASP top 10.', 'Completed', '92%'),
(1, 'Leadership scenario written exercise', DATE '2026-05-09', DATE '2026-05-16', 'Incident response narrative.', 'Pending', 'N/A');
