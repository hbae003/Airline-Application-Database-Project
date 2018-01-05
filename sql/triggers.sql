CREATE LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION return_val() RETURNS trigger AS
$$
BEGIN 
NEW.pID = nextval('part_number_seq');
RETURN NEW;
END;
$$
LANGUAGE plpgsql VOLATILE;


CREATE TRIGGER set_passenger_number
BEFORE INSERT 
ON Passenger
FOR EACH ROW
EXECUTE PROCEDURE return_val();
