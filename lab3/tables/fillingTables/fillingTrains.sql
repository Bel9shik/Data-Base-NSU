DO $$
    DECLARE
        random_train_number int;
        random_train_category int;
        random_head_station int;
    BEGIN
        FOR i in 1..1000 LOOP
                random_train_number := FLOOR(RANDOM() * 90000  + 10000);
                random_train_category := FLOOR(RANDOM() * 5  + 1);
                random_head_station := FLOOR(RANDOM() * 1000  + 1);
                INSERT INTO trains (trainnumber, categoryid, headstation, quantitywagon)
                VALUES ( random_train_number,random_train_category, random_head_station, FLOOR(RANDOM() * 50  + 1));
            end loop;
    END $$;