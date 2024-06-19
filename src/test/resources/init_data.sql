INSERT INTO person (first_name, last_name, email, phone_number, date_of_birth) VALUES
                                                                                   ('Alice', 'Doe', 'alice.doe@example.com', '1234567890', '1990-01-01'),
                                                                                   ('Bob', 'Smith', 'bob.smith@example.com', '0987654321', '1995-02-02'),
                                                                                   ('Charlie', 'Brown', 'charlie.brown@example.com', '1122334455', '1985-03-03'),
                                                                                      ('David', 'White', 'david.2s@ds.pl','1234567890', '1990-01-01');

INSERT INTO flight (flight_number, origin, destination, departure_time, arrival_time, number_of_seats) VALUES
                                                                                                              ('AA123', 'New York', 'Los Angeles', '2024-06-01', '2024-06-01', 150),
                                                                                                              ('BB456', 'Chicago', 'San Francisco', '2024-06-02', '2024-06-02', 200),
                                                                                                              ('CC789', 'Miami', 'Seattle', '2024-06-03', '2024-06-03', 180);

INSERT INTO ticket (seat_number, ticket_number, price, flight_id, person_id) VALUES
                                                                                 (1, 1001, 300.00, 1, 1),
                                                                                 (2, 1002, 350.00, 1, 2),
                                                                                 (3, 1003, 400.00, 2, 1),
                                                                                 (4, 1004, 450.00, 2, 3),
                                                                                 (5, 1005, 500.00, 2, 2);