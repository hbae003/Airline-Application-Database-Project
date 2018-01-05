-- INSERT IGNORE INTO Passenger (pID, passNum, fullName, bdate, country)
-- VALUES (555, 'FDOIEFJKLE', 'Tom B. Erichsen', '01/01/01', 'Stavanger');

-- SELECT A.name, F.flightNum, F.origin, F.destination, F.plane, F.duration
-- FROM Airline A, Flight F
-- WHERE A.airId = F.airId AND F.origin = 'Beijing' AND F.destination = 'San Francisco'
-- GROUP BY A.name, F.flightNum, F.duration
-- ORDER BY F.duration ASC
-- Limit 2


-- SELECT F.*
-- FROM Flight F, Booking B
-- WHERE F.flightNum = B.flightNum 
-- AND F.origin = 'Lisbon' 
-- AND F.destination = 'Madrid'
-- GROUP BY F.airId, F.flightNum
-- ORDER BY (F.seats - count(B)) > 0
-- LIMIT 1;

-- SELECT B.* 
-- FROM Booking B 
-- LIMIT 8;

-- INSERT INTO Booking(bookRef, departure, flightNum, pID) 
-- VALUES( 'DDECB8829K' ,'10/23/17','IB903',4);

SELECT F.seats - COUNT(B)
FROM Flight F, Booking B
WHERE F.flightNum = B.flightNum
	AND F.flightNum = 'AA'
