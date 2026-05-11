-- TrackHire: incremental DDL for existing MySQL `trackhire` databases (Workbench).
-- Run statements one group at a time; skip any that error because the column/table already exists.
-- Fresh installs: use seed.sql in repo root instead (includes full schema).

USE trackhire;

-- Richer application row (FR 8 / 11 / 12)
ALTER TABLE Applications ADD COLUMN jobUrl VARCHAR(512) NULL;
ALTER TABLE Applications ADD COLUMN jobLocation VARCHAR(255) NULL;
ALTER TABLE Applications ADD COLUMN notes TEXT NULL;

-- Admin flag for user directory edits (FR admin-only edit)
ALTER TABLE Users ADD COLUMN isAdmin TINYINT(1) NOT NULL DEFAULT 0;
UPDATE Users SET isAdmin = 1 WHERE email = 'alice@example.com' LIMIT 1;

-- Tags (FR 9)
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
