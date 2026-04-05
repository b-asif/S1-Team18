CREATE DATABASE trackhire; 
USE trackhire;

CREATE TABLE Users (
    userId INT PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL, 
    lastName VARCHAR(50) NOT NULL,
    userName VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Company (
    companyId INT PRIMARY KEY AUTO_INCREMENT,
    companyName VARCHAR(100) NOT NULL,
    companyContact VARCHAR(100), 
    contactEmail VARCHAR(100)
);

CREATE TABLE Application (
    applicationId INT PRIMARY KEY AUTO_INCREMENT,
    userId INT NOT NULL, 
    companyId INT NOT NULL,
    jobTitle VARCHAR(100) NOT NULL, 
    appStatus VARCHAR(50) NOT NULL, 
    dateApplied DATE NOT NULL, 

    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (companyId) REFERENCES Company(companyId) 
);

CREATE TABLE Interview (
    interviewId INT PRIMARY KEY AUTO_INCREMENT,
    applicationId INT NOT NULL,
    startTime TIME NOT NULL,
    interviewDate DATE NOT NULL,
    location VARCHAR(150),
    role VARCHAR(100),
    
    FOREIGN KEY (applicationId) REFERENCES Application(applicationId) ON DELETE CASCADE
);