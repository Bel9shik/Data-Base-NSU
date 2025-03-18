--4
insert into "trainCategories" ("categoryName")
values ('Пассажирский'),
       ('Скоростной'),
       ('Грузовой'),
       ('Региональный'),
       ('Экспресс')
on conflict do nothing;

--5
insert into "wagonType" (type, capacity)
values ('Общий', 100),
       ('Плацкарт', 40),
       ('Купе', 30),
       ('СВ', 20),
       ('Ресторан', 60),
       ('Грузовой', 1000)
on conflict do nothing;

--1k
insert into stations (name)
select 'Станция ' || generate_series(1, 1000)
on conflict do nothing;

--5k
do
$$
    declare
        station_ids  int[];
        num_stations int;
        departureID  int;
        arrivalID    int;
    begin
        -- получаем массив всех id станций
        select array_agg(id)
        into station_ids
        from stations;

        num_stations = array_length(station_ids, 1);

        for _ in 1..5000
            loop

                departureID = station_ids[floor(random() * num_stations)::int + 1];
                arrivalID = station_ids[floor(random() * num_stations)::int + 1];

                if arrivalID = departureID then
                    continue;
                end if;
                insert into routes("departureStationID", "arrivalStationID")
                values (departureID,
                        arrivalID)
                on conflict
                    do nothing;
            end loop;
    end;
$$;

--1k
do
$$
    declare
        cur_categoryID      int;
        trainCategory_ids   int[];
        num_trainCategories int;
    begin

        select array_agg("categoryID")
        into trainCategory_ids
        from "trainCategories";

        num_trainCategories = array_length(trainCategory_ids, 1);

        for i in 1..1000
            loop

                cur_categoryID = trainCategory_ids[floor(random() * num_trainCategories)::int + 1];

                insert into trains("trainNumber", "categoryID", "quantityWagon")
                values ('Поезд_' || i::varchar,
                        cur_categoryID,
                        floor(random() * 21 + 5)::int -- [5, 20]
                       );
            end loop;
    end;
$$;

--20k
do
$$
    declare
        routes_ids int[];
        num_routes int;
        trains_ids int[];
        num_trains int;
    begin

        select array_agg(id)
        into routes_ids
        from routes;

        num_routes = array_length(routes_ids, 1);

        select array_agg(id)
        into trains_ids
        from trains;

        num_trains = array_length(trains_ids, 1);

        for i in 1..20000
            loop
                insert into schedule ("routeID", "trainID")
                values ((routes_ids[floor(random() * num_routes)::int + 1]),
                        (trains_ids[floor(random() * num_trains)::int + 1]))
                on conflict do nothing;
            end loop;
    end;
$$;

--trains_quantity * trains_wagons_quantity
do
$$
    declare
        cur_trainID         int;
        cur_trainCategoryID int;
        cur_quantityWagons  int;
        cargoTrainTypeID    int;
        cargoWagonTypeID    int;
    begin

        select "categoryID" from "trainCategories" where "categoryName" = 'Грузовой' into cargoTrainTypeID;
        select "id" from "wagonType" where "type" = 'Грузовой' into cargoWagonTypeID;

        for cur_trainID, cur_quantityWagons, cur_trainCategoryID in (select id, "quantityWagon", "categoryID" from trains)
            loop

                for _ in 1..cur_quantityWagons
                    loop
                        if cur_trainCategoryID = cargoTrainTypeID then
                            insert into wagons ("trainID", "wagonTypeID")
                            values (cur_trainID,
                                    cargoWagonTypeID);
                        else
                            insert into wagons ("trainID", "wagonTypeID")
                            values (cur_trainID,
                                    (select id
                                     from "wagonType"
                                     where id != cargoWagonTypeID
                                     order by random()
                                     limit 1));
                        end if;

                    end loop;
            end loop;
    end;
$$;

-- schedule size
do
$$
    declare
        cur_trainID       int;
        cur_scheduleID    int;
        cur_headStationID int;
    begin
        for cur_scheduleID, cur_trainID, cur_headStationID in (select distinct sch.id, sch."trainID", r."departureStationID"
                                                               from schedule sch
                                                                        join routes r on sch."routeID" = r.id)
            loop
                insert into brigades ("trainID", "headStationID") values (cur_trainID, cur_headStationID);
            end loop;
    end;
$$;

--75k
do
$$
    declare
        surname           varchar[] := array [
            'Иванов', 'Наумов', 'Флях', 'Белый', 'Резников',
            'Самоваров', 'Сборщик', 'Андреев', 'Красный', 'Черный',
            'Добрый', 'Петров', 'Сидоров', 'Кузнецов', 'Смирнов',
            'Попов', 'Зайцев', 'Ковалев', 'Соловьев', 'Морозов',
            'Федоров', 'Григорьев', 'Тихонов', 'Сергеев', 'Белов',
            'Ковалев', 'Лебедев', 'Михайлов', 'Гусев', 'Семенов',
            'Мельников', 'Киселев', 'Николаев', 'Данилов', 'Алексеев',
            'Васильев', 'Савельев', 'Климов', 'Фомин', 'Титов',
            'Логинов', 'Баранов', 'Сорокин', 'Филиппов', 'Кириллов',
            'Герасимов', 'Долгих', 'Савин', 'Капустин', 'Рябов',
            'Шевченко', 'Костин', 'Терехов', 'Сухов', 'Мартынов',
            'Фролов', 'Селезнев', 'Ширяев', 'Сухов', 'Захаров',
            'Громов', 'Костяев', 'Стариков', 'Куров', 'Гладков',
            'Шумилов', 'Зимин', 'Дружков', 'Сидоренко', 'Яковлев',
            'Носов', 'Федосеев', 'Савин', 'Кочетков', 'Шевцов',
            'Кузьмин', 'Буров', 'Панин', 'Долинин', 'Кудрявцев',
            'Ларионов', 'Панфилов', 'Тимофеев', 'Горшков', 'Тихомиров',
            'Лебедев', 'Романов', 'Белов', 'Сычев', 'Кузнецов',
            'Семенов', 'Ковалев', 'Петров', 'Сидоров', 'Лебедев',
            'Федоров', 'Григорьев', 'Тихонов', 'Сергеев', 'Морозов',
            'Данилов', 'Алексеев', 'Николаев', 'Васильев', 'Савельев',
            'Климов', 'Фомин', 'Титов', 'Логинов', 'Баранов',
            'Сорокин', 'Филиппов', 'Кириллов', 'Герасимов', 'Долгих',
            'Савин', 'Капустин', 'Рябов', 'Шевченко', 'Костин',
            'Терехов', 'Сухов', 'Мартынов', 'Фролов', 'Селезнев',
            'Ширяев', 'Сухов', 'Захаров', 'Громов', 'Костяев',
            'Стариков', 'Куров', 'Гладков', 'Шумилов', 'Зимин',
            'Дружков', 'Сидоренко', 'Яковлев', 'Носов', 'Федосеев',
            'Савин', 'Кочетков', 'Шевцов', 'Кузьмин', 'Буров',
            'Панин', 'Долинин', 'Кудрявцев', 'Ларионов', 'Панфилов',
            'Тимофеев', 'Горшков', 'Тихомиров', 'Лебедев', 'Романов',
            'Белов', 'Сычев', 'Кузнецов', 'Семенов', 'Ковалев',
            'Петров', 'Сидоров', 'Лебедев', 'Федоров', 'Григорьев',
            'Тихонов', 'Сергеев', 'Морозов', 'Данилов'];
        name              varchar[] := array [
            'Иван', 'Александр', 'Михаил', 'Сергей', 'Андрей',
            'Григорий', 'Владимир', 'Егор', 'Рамиль', 'Наиль',
            'Дмитрий', 'Арсений', 'Денис', 'Анатолий', 'Константин',
            'Станислав', 'Алексей', 'Николай', 'Павел', 'Роман',
            'Игорь', 'Виктор', 'Даниил', 'Матвей', 'Тимур',
            'Максим', 'Артем', 'Станислав', 'Юрий', 'Василий',
            'Геннадий', 'Федор', 'Александр', 'Виктор', 'Анатолий',
            'Семен', 'Кирилл', 'Ярослав', 'Дмитрий', 'Никита',
            'Владислав', 'Тихон', 'Савелий', 'Артур', 'Денис',
            'Эдуард', 'Валентин', 'Григорий', 'Сергей', 'Роман',
            'Евгений', 'Петр', 'Даниил', 'Тимофей', 'Антон',
            'Николай', 'Станислав', 'Алексей', 'Филипп', 'Илья',
            'Станислав', 'Валерий', 'Кирилл', 'Дмитрий', 'Денис',
            'Александр', 'Сергей', 'Ярослав', 'Артем', 'Никита',
            'Даниил', 'Максим', 'Игорь', 'Роман', 'Павел',
            'Владимир', 'Геннадий', 'Тимур', 'Федор', 'Анатолий',
            'Александр', 'Константин', 'Юрий', 'Арсений', 'Семен',
            'Денис', 'Тихон', 'Анатолий', 'Григорий', 'Станислав',
            'Дмитрий', 'Алексей', 'Николай', 'Петр', 'Виктор',
            'Евгений', 'Илья', 'Максим', 'Филипп', 'Антон',
            'Ярослав', 'Савелий', 'Кирилл', 'Тимофей', 'Валентин',
            'Геннадий', 'Денис', 'Сергей', 'Арсений', 'Владислав',
            'Тимур', 'Роман', 'Игорь', 'Александр', 'Семен',
            'Дмитрий', 'Павел', 'Никита', 'Анатолий', 'Станислав',
            'Кирилл', 'Ярослав', 'Артем', 'Даниил', 'Виктор',
            'Федор', 'Юрий', 'Тихон', 'Алексей', 'Сергей',
            'Григорий', 'Денис', 'Максим', 'Николай', 'Евгений',
            'Илья', 'Роман', 'Петр', 'Арсений', 'Анатолий',
            'Семен', 'Тимур', 'Валентин', 'Дмитрий', 'Александр',
            'Станислав', 'Кирилл', 'Ярослав', 'Тихон', 'Филипп',
            'Антон', 'Денис', 'Геннадий', 'Игорь', 'Артем',
            'Владимир', 'Никита', 'Данила', 'Савелий', 'Максим',
            'Сергей', 'Даниил', 'Виктор', 'Евгений', 'Роман',
            'Павел', 'Алексей', 'Анатолий', 'Станислав', 'Кирилл',
            'Ярослав', 'Тимур', 'Федор', 'Юрий', 'Арсений',
            'Денис', 'Григорий'];
        patronymics       varchar[] := array [ null,
            'Алексеевич', 'Александрович', 'Андреевич', 'Борисович', 'Васильевич',
            'Викторович', 'Григорьевич', 'Дмитриевич', 'Евгеньевич', 'Иванович',
            'Константинович', 'Леонидович', 'Михайлович', 'Николаевич', 'Олегович',
            'Павлович', 'Петрович', 'Романович', 'Сергеевич', 'Станиславович',
            'Фёдорович', 'Юрьевич', 'Ярославович', 'Ильич', 'Максимович',
            'Владимирович', 'Эдуардович', 'Аркадьевич', 'Игоревич', 'Тимофеевич',
            'Владиславович', 'Денисович', 'Егорович', 'Захарович', 'Исаевич',
            'Карпович', 'Львович', 'Маркович', 'Никитич', 'Олегович',
            'Платонович', 'Русланович', 'Савельевич', 'Тарасович', 'Устинович',
            'Филиппович', 'Харитонович', 'Цветкович', 'Чеснокович', 'Шаманович',
            'Щербакович', 'Эмирович', 'Юрьевич', 'Яковлевич', 'Афанасьевич',
            'Белович', 'Василькович', 'Гаврилович', 'Давыдович', 'Елисеевич',
            'Жукович', 'Зубаревич', 'Иванович', 'Козлович', 'Лазаревич',
            'Мельникович', 'Носкович', 'Орлович', 'Панфилович', 'Родионович',
            'Савельевич', 'Трофимович', 'Ульянович', 'Федорович', 'Харитонович',
            'Цветкович', 'Чубович', 'Шарапович', 'Щукинич', 'Энштейнович',
            'Юрьевич', 'Яковлевич', 'Анисимович', 'Борисович', 'Викентьевич',
            'Георгиевич', 'Данилович', 'Егорович', 'Зиновьевич', 'Измайлович'
            ];
        positions         varchar[] := array
            ['Кассир', 'Дежурный по станции', 'Машинист', 'Проводник', 'Начальник поезда', 'Инженер', 'Менеджер'];
        random_surname    varchar(50);
        random_name       varchar(50);
        random_patronymic varchar(50);
        random_passport   text;
        -- ==========================================
        cur_station       record;
        cur_train         record;
        employee_count    int;
        cur_employee_id   int;
        cur_headStationID int;

    begin

        --passengers
        for i in 1..75000
            loop
                select surname[1 + floor(random() * array_length(surname, 1))] into random_surname;

                select name[1 + floor(random() * array_length(name, 1))] into random_name;

                select patronymics[1 + floor(random() * array_length(name, 1))] into random_patronymic;

                select (floor(random() * 8000000000) + 1231231234)::varchar into random_passport;

                insert into passengers ("passportData", firstname, surname, patronymic)
                values (random_passport, random_name, random_surname, random_patronymic);
            end loop;

        --===============================================================================
        --employees

        -- Перебираем все станции
        for cur_station in (select id from stations)
            loop
                -- Генерируем случайное количество сотрудников для станции
                employee_count := floor(random() * 21 + 5)::int; --[5; 25]

                for _ in 1..employee_count
                    loop
                        -- Выбираем случайные ФИО
                        insert into employees (firstname, surname, patronymic, position, "managerID", "stationID",
                                               "brigadeID")
                        values (name[1 + floor(random() * array_length(name, 1))],
                                surname[1 + floor(random() * array_length(surname, 1))],
                                patronymics[1 + floor(random() * array_length(patronymics, 1))],
                                positions[1 + floor(random() * array_length(positions, 1))],
                                null, -- Пока что начальников не назначаем
                                cur_station.id,
                                null); -- Бригады тоже пока не назначаем
                    end loop;
            end loop;

-- Теперь назначаем начальников (менеджеров) случайным сотрудникам
        update employees
        set "managerID" = (select id
                           from employees e2
                           where e2."stationID" = employees."stationID"
                           order by random()
                           limit 1)
        where position = 'Менеджер';

        -- Теперь назначаем сотрудников в поездные бригады
        for cur_train in (select id from trains)
            loop
                select "headStationID" from brigades where "trainID" = cur_train.id into cur_headStationID;

                -- Назначаем бригаду только сотрудникам станции формирования поезда
                for cur_employee_id in (select id from employees where "stationID" = cur_headStationID)
                    loop
                        -- Выбираем случайную должность для бригады
                        if random() < 0.5 then -- 50% шанс попадания в бригаду
                            update employees
                            set "brigadeID" = cur_train.id
                            where id = cur_employee_id
                              and position in ('Машинист', 'Проводник', 'Начальник поезда');
                        end if;
                    end loop;
            end loop;

    end
$$;


--routes size
do
$$
    declare
        stations_ids      int[];
        num_stations_ids  int;
        --===================
        stations_quantity int;
        cur_stationID     int;
        cur_arrStationID  int;
        cur_depStationID  int;
        cur_routeID       int;
        cur_distance      int;
    begin
        select array_agg(id) from stations into stations_ids;
        num_stations_ids = array_length(stations_ids, 1);

        for cur_routeID in (select id from routes)
            loop
                stations_quantity = floor(random() * 15)::int + 5; --[5,20) станций
                select "departureStationID", "arrivalStationID"
                from routes
                where id = cur_routeID
                into cur_depStationID, cur_arrStationID;

                insert into "routeStations" ("routeID", "stationID", "stopOrder", distance)
                values (cur_routeID, cur_depStationID, 0, 0);
                for j in 1..(stations_quantity - 1)
                    loop
                        <<retry_insertion>>
                        loop
                            cur_stationID = stations_ids[floor(random() * num_stations_ids)::int + 1];
                            if cur_stationID = cur_arrStationID or cur_stationID = cur_depStationID then
                                continue retry_insertion;
                            end if;
                            cur_distance = floor(random() * 45 + 5)::int; -- [5, 50) km
                            begin
                                insert into "routeStations" ("routeID", "stationID", "stopOrder", distance)
                                values (cur_routeID, cur_stationID, j, cur_distance);
                                exit retry_insertion;
                            exception
                                when unique_violation then null;
                            end;
                        end loop retry_insertion;
                    end loop;
                cur_distance = floor(random() * 45 + 5)::int; -- [5, 50) km
                insert into "routeStations" ("routeID", "stationID", "stopOrder", distance)
                values (cur_routeID, cur_arrStationID, stations_quantity, cur_distance);
            end loop;
    end;
$$;

--100k
do
$$
    declare
        schedule_ids           int[];
        passengers_ids         int[];
        num_schedule_ids       int;
        num_passengers_ids     int;
        cargoTrainCategoryID   int;
        -- ============================
        cur_routeID            int;
        cur_scheduleID         int;
        cur_passengerID        int;
        cur_stationsQuantity   int;
        cur_departureStationID int;
        cur_arrivalStationID   int;
        cur_trainID            int;
        cur_wagonID            int;
        cur_placeNumber        int;
        cur_departureStopOrder int;
        cur_capacity           int;
    begin

        select array_agg(id)
        into schedule_ids
        from schedule;

        num_schedule_ids = array_length(schedule_ids, 1);

        select array_agg(id)
        into passengers_ids
        from passengers;

        num_passengers_ids = array_length(passengers_ids, 1);

        select "categoryID" from "trainCategories" where "categoryName" = 'Грузовой' into cargoTrainCategoryID;

        for _ in 1..100000
            loop

                cur_scheduleID = schedule_ids[floor(random() * num_schedule_ids)::int + 1];
                cur_passengerID = passengers_ids[floor(random() * num_passengers_ids)::int + 1];

                select "routeID", "trainID" from schedule where id = cur_scheduleID into cur_routeID, cur_trainID;

                select count(*) from "routeStations" where "routeID" = cur_routeID into cur_stationsQuantity;

                cur_departureStopOrder =
                        floor(random() * (cur_stationsQuantity - 3))::int + 1; -- [1, cur_stationsQuantity - 3]

                select "stationID"
                from "routeStations"
                where "routeID" = cur_routeID
                  and "stopOrder" = cur_departureStopOrder
                into cur_departureStationID;

                select "stationID"
                from "routeStations"
                where "routeID" = cur_routeID
                  and "stopOrder" > cur_departureStopOrder
                order by random()
                limit 1
                into cur_arrivalStationID;

                select w.id, wT.capacity
                from wagons w
                         join "wagonType" wT on w."wagonTypeID" = wT.id
                where "trainID" = cur_trainID
                order by random()
                limit 1
                into cur_wagonID, cur_capacity;

                cur_placeNumber = floor(random() * cur_capacity + 1)::int; -- [1, capacity]

                if cur_placeNumber in (select "placeNumber"
                                       from "passengersTrips" ps
                                                join schedule sch on sch.id = cur_scheduleID
                                                join "routeStations" rs on rs."routeID" = cur_routeID and
                                                                           rs."stopOrder" >=
                                                                           cur_departureStopOrder
                                           and ps."wagonID" = cur_wagonID) then
                    continue; --если место занято
                end if;

                insert into "passengersTrips" ("scheduleID", "passengerID", "departureStationID", "arrivalStationID",
                                               "wagonID", "placeNumber")
                values (cur_scheduleID, cur_passengerID, cur_departureStationID, cur_arrivalStationID, cur_wagonID,
                        cur_placeNumber)
                on conflict do nothing;

            end loop;
    end;
$$;

--schedule size
do
$$
    declare
        cur_scheduleID         int;
        cur_routeID            int;
        cur_stationID          int;
        cur_stopOrder          int;
        prev_real_arrival_time timestamp;
        prev_plan_arrival_time timestamp;
        travel_time            interval;
        stop_duration          interval;
        avg_speed              int := 25; -- Средняя скорость поезда (км/ч)
    begin
        for cur_scheduleID, cur_routeID in (select id, "routeID" from schedule)
            loop

                prev_plan_arrival_time = now() - interval '2 years' + (random() * interval '2 years'); --стартовое время

                for cur_stationID, cur_stopOrder in (select "stationID", "stopOrder"
                                                     from "routeStations" rs
                                                     where "routeID" = cur_routeID
                                                     order by "stopOrder")
                    loop

                        stop_duration = (random() * interval '20 min') + interval '1 min'; --[1 min; 20 min]

                        if cur_stopOrder = 1 then
                            insert into "timeSchedule" ("scheduleID", "stationID", "plannedArrivalTime",
                                                        "realArrivalTime",
                                                        "stopDuration")
                            values (cur_scheduleID, cur_stationID, prev_plan_arrival_time, prev_plan_arrival_time,
                                    stop_duration);
                        else
                            select interval '1 min' * (rs.distance / avg_speed * 60)
                            from "routeStations" rs
                            where "routeID" = cur_routeID
                              and "stationID" = cur_stationID
                            into travel_time;

                            prev_plan_arrival_time = prev_plan_arrival_time + travel_time;

                            if random() > 0.5 then
                                prev_real_arrival_time = prev_plan_arrival_time + (random() * interval '15 minutes') + '1 minute'; -- [1; 15] minutes
                                else prev_real_arrival_time = prev_plan_arrival_time;
                            end if;

                            insert into "timeSchedule" ("scheduleID", "stationID", "plannedArrivalTime",
                                                        "realArrivalTime", "stopDuration")
                            values (cur_scheduleID, cur_stationID, prev_plan_arrival_time, prev_plan_arrival_time,
                                    stop_duration);

                        end if;

                        prev_plan_arrival_time = prev_plan_arrival_time + stop_duration;

                    end loop;
            end loop;
    end;
$$;