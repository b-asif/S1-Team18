DROP TABLE IF EXISTS ApplicationTag;
DROP TABLE IF EXISTS Tag;
DROP TABLE IF EXISTS PasswordResetTokens;
DROP TABLE IF EXISTS technicals;
DROP TABLE IF EXISTS Interviews;
DROP TABLE IF EXISTS Applications;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(100) NOT NULL,
    lastName VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    userName VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    isAdmin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Applications (
    applicationId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    jobTitle VARCHAR(255) NOT NULL,
    companyName VARCHAR(255) NOT NULL,
    appStatus VARCHAR(50) NOT NULL,
    dateApplied DATE NOT NULL,
    jobUrl VARCHAR(512) NULL,
    jobLocation VARCHAR(255) NULL,
    notes CLOB NULL,
    CONSTRAINT fk_applications_user FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE
);

CREATE TABLE Interviews (
    interviewId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    roleTitle VARCHAR(255) NOT NULL,
    departmentName VARCHAR(255),
    interviewerName VARCHAR(255),
    interviewType VARCHAR(100),
    interviewDate DATE NOT NULL,
    startTime TIME NOT NULL,
    endTime TIME,
    location VARCHAR(255),
    notes CLOB,
    CONSTRAINT fk_interviews_user FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE
);

CREATE TABLE technicals (
    assessmentId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    assessmentTitle VARCHAR(255) NOT NULL,
    assignedDate DATE NOT NULL,
    dueDate DATE,
    assessmentNotes CLOB,
    completionStatus VARCHAR(100),
    scoreOrPassFail VARCHAR(100),
    CONSTRAINT fk_technicals_user FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE
);

CREATE TABLE PasswordResetTokens (
    resetId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    tokenHash VARCHAR(255) NOT NULL UNIQUE,
    expiresAt TIMESTAMP NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reset_tokens_user FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE
);

CREATE TABLE Tag (
    tagId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_tag_user FOREIGN KEY (userId) REFERENCES Users(userId) ON DELETE CASCADE,
    CONSTRAINT uq_tag_user_name UNIQUE (userId, name)
);

CREATE TABLE ApplicationTag (
    applicationId INT NOT NULL,
    tagId INT NOT NULL,
    PRIMARY KEY (applicationId, tagId),
    CONSTRAINT fk_app_tag_application FOREIGN KEY (applicationId) REFERENCES Applications(applicationId) ON DELETE CASCADE,
    CONSTRAINT fk_app_tag_tag FOREIGN KEY (tagId) REFERENCES Tag(tagId) ON DELETE CASCADE
);
