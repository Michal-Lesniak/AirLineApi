DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS flight;

CREATE TABLE person (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(30) NOT NULL,
                        last_name VARCHAR(40) NOT NULL,
                        email VARCHAR(50) NOT NULL,
                        phone_number VARCHAR(10) NOT NULL,
                        date_of_birth DATE NOT NULL
);

CREATE TABLE flight (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        flight_number VARCHAR(10) NOT NULL,
                        departure VARCHAR(30) NOT NULL,
                        destination VARCHAR(30) NOT NULL,
                        departure_date DATE NOT NULL,
                        arrival_date DATE NOT NULL,
                        available_seats INT NOT NULL
);

CREATE TABLE ticket (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        seat_number INT NOT NULL,
                        ticket_number INT NOT NULL,
                        price DECIMAL(10, 2) NOT NULL,
                        flight_id BIGINT,
                        person_id BIGINT,
                        CONSTRAINT ticket_flight_fk FOREIGN KEY (flight_id) REFERENCES flight(id),
                        CONSTRAINT ticket_person_fk FOREIGN KEY (person_id) REFERENCES person(id)
);