
ALTER TABLE activities DROP CONSTRAINT fkkx78ofjocchsrmnsf02creh65;


ALTER TABLE activities
    ALTER COLUMN trip_id TYPE BIGINT;


ALTER TABLE activities
    ADD CONSTRAINT fk_trip
        FOREIGN KEY (trip_id) REFERENCES trips(id);
