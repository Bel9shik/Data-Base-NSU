do $$
    declare
    begin
        for i in 1..1250 loop
                for j in 1..4 loop
                        insert into "routes"(arrivalstation, departurestation)
                        values (i + (j - 1) * 5, i + 4 + (j - 1) * 5);
                    end loop;
            end loop;
    end$$