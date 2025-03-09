DO $$
    declare
        train_rec record;
    begin
        for train_rec in select id from "trains" loop
                insert into wagons(trainid, wagontypeid, capacity)
                values
                    (train_rec.id, 1, 1),
                    (train_rec.id, 1, 2),
                    (train_rec.id, 2, 3),
                    (train_rec.id, 2, 4),
                    (train_rec.id, 3, 5),
                    (train_rec.id, 3, 6),
                    (train_rec.id, 4, 7),
                    (train_rec.id, 4, 8);
            end loop;
    END $$