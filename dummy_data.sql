INSERT INTO VEHICLETYPE VALUES ('SUV', 'stuff', 10.00, 20.00, 30.00, 11.00, 22.00, 33.00, 69.00);
INSERT INTO VEHICLETYPE VALUES ('Sedan', 'stuff', 10.00, 20.00, 30.00, 11.00, 22.00, 33.00, 69.00);
INSERT INTO VEHICLETYPE VALUES ('Convertible', 'stuff', 10.00, 20.00, 30.00, 11.00, 22.00, 33.00, 69.00);

INSERT INTO VEHICLE VALUES('ABCDEF1234', 'Toyota', 'Camry', 2014, 'blue', 92.35, 'available', 'Sedan', 'Loc1', 'City1');
INSERT INTO VEHICLE VALUES('ABCDEF1235', 'Chevrolet', 'Tahoe', 2014, 'white', 92.35, 'available', 'SUV', 'Loc2', 'City2');
INSERT INTO VEHICLE VALUES('ABCDEF1236', 'Nissan', 'Roadster', 2015, 'red', 92.35, 'available', 'Convertible', 'Loc3', 'City3');
INSERT INTO VEHICLE VALUES('ABCDEF1237', 'Toyota', 'Camry', 2014, 'blue', 92.35, 'available', 'Sedan', 'Loc3', 'City3');
INSERT INTO VEHICLE VALUES('ABCDEF1238', 'Chevrolet', 'Tahoe', 2014, 'white', 92.35, 'available', 'SUV', 'Loc2', 'City2');
INSERT INTO VEHICLE VALUES('ABCDEF1239', 'Nissan', 'Roadster', 2015, 'red', 92.35, 'available', 'Convertible', 'Loc1', 'City1');


INSERT INTO CUSTOMER VALUES(12345678912, 'John Doe', 'some address', 'A91748182K');
INSERT INTO CUSTOMER VALUES(12345678913, 'Dohn Joe', 'some address 2', 'A12341182K');

INSERT INTO reservation VALUES ('1234567890', 'Sedan', 'A91748182K', TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00');
INSERT INTO reservation VALUES ('1234567891', 'SUV', 'A12341182K', TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00');
INSERT INTO reservation VALUES ('1234567892', 'Convertible', 'A91748182K', TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00');
INSERT INTO reservation VALUES ('1234567893', 'Sedan', 'A12341182K', TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00');
INSERT INTO reservation VALUES ('1234567894', 'SUV', 'A91748182K', TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00');
INSERT INTO reservation VALUES ('1234567895', 'Convertible', 'A12341182K', TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00');

INSERT INTO rental VALUES('A12345', 'ABCDEF1234', 'A91748182K',  TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00', 72, 'JOHN DOE',  'ABCDEFG123456789', '11/25', '1234567890');
INSERT INTO rental VALUES('A12346', 'ABCDEF1235', 'A12341182K',  TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00', 72, 'JOHN DOE',  'ABCDEFG123456789', '11/25', '1234567891');
INSERT INTO rental VALUES('A12347', 'ABCDEF1236', 'A91748182K',  TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00', 72, 'JOHN DOE',  'ABCDEFG123456789', '11/25', '1234567892');
INSERT INTO rental VALUES('A12348', 'ABCDEF1237', 'A12341182K',  TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00', 72, 'JOHN DOE',  'ABCDEFG123456789', '11/25', '1234567893');
INSERT INTO rental VALUES('A12349', 'ABCDEF1238', 'A91748182K',  TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00', 72, 'JOHN DOE',  'ABCDEFG123456789', '11/25', '1234567894');
INSERT INTO rental VALUES('A12350', 'ABCDEF1239', 'A12341182K',  TIMESTAMP '2019-11-23 17:30:00.00', TIMESTAMP '2019-12-23 17:30:00.00', 72, 'JOHN DOE',  'ABCDEFG123456789', '11/25', '1234567895');

INSERT INTO return VALUES('A12345', TIMESTAMP '2019-11-23 17:30:00.00', 1200, '0', 1);
INSERT INTO return VALUES('A12346', TIMESTAMP '2019-11-23 17:30:00.00', 1200, '0', 2);
INSERT INTO return VALUES('A12347', TIMESTAMP '2019-11-23 17:30:00.00', 1200, '0', 3);
INSERT INTO return VALUES('A12348', TIMESTAMP '2019-11-23 17:30:00.00', 1200, '0', 4);
INSERT INTO return VALUES('A12349', TIMESTAMP '2019-11-23 17:30:00.00', 1200, '0', 5);
INSERT INTO return VALUES('A12350', TIMESTAMP '2019-11-23 17:30:00.00', 1200, '0', 6);