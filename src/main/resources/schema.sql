CREATE SEQUENCE events_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE processed_events (
    id INT PRIMARY KEY,
    request_id VARCHAR NOT NULL,
    status VARCHAR(15) NOT NULL
);
