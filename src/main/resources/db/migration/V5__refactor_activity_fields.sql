ALTER TABLE activities
    DROP COLUMN IF EXISTS type;

ALTER TABLE activities
    ADD COLUMN note VARCHAR(120);

UPDATE activities
SET name = SUBSTRING(name, 1, 50)
WHERE LENGTH(name) > 50;

ALTER TABLE activities
    ALTER COLUMN name TYPE VARCHAR(50);

