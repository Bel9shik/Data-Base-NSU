--4
insert into "trainCategories" ("categoryName")
values ('Пассажирский'),
       ('Скоростной'),
       ('Грузовой'),
       ('Региональный'),
       ('Экспресс')
on conflict do nothing;

--5
insert into "wagonType" (type)
values ('Общий'),
       ('Плацкарт'),
       ('Купе'),
       ('СВ'),
       ('Ресторан'),
       ('Грузовой')
on conflict do nothing;

--1k
insert into stations (name)
select 'Станция ' || generate_series(1, 1000)
on conflict do nothing;

--1k
insert into trains ("trainNumber", "categoryID", "headStation", "quantityWagon")
select 'Поезд_' || gs::text          AS "trainNumber",
       (select "categoryID"
        from "trainCategories"
        order by random()
        limit 1)                     AS "categoryID",
       (select "departureStation"
        from routes
        order by random()
        limit 1)                     AS "headStation",
       floor(random() * 21 + 5)::int AS "quantityWagon" -- [5, 20]
from generate_series(1, 1000) gs
on conflict do nothing;

--5k
insert into wagons ("trainID", capacity, "wagonTypeID")
select (select id from trains order by random() limit 1),
       floor(random() * 50 + 1)::int, -- [1, 50]
       (select id from "wagonType" order by random() limit 1)
from generate_series(1, 5000)
on conflict do nothing;

--75k
do
$$
    declare
        surname           text[] := array [
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
        name              text[] := array [
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
        patronymics       text[] := array [ null,
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
        positions         text[] := array
            ['Кассир', 'Дежурный по станции', 'Машинист', 'Проводник', 'Начальник поезда', 'Инженер', 'Менеджер'];
        random_surname    varchar(50);
        random_name       varchar(50);
        random_patronymic varchar(50);
        random_passport   text;
        -- =====
        cur_station       record;
        cur_train         record;
        employee_count    int;
        cur_employee_id   int;
        manager_id        int;
        brigade_id        int;

    begin

        --passengers
        for i in 1..75000
            loop
                select surname[1 + floor(random() * array_length(surname, 1))] into random_surname;

                select name[1 + floor(random() * array_length(name, 1))] into random_name;

                select patronymics[1 + floor(random() * array_length(name, 1))] into random_patronymic;

                select floor(random() * 8000000000) + 1231231234 into random_passport;

                insert into passengers ("passportData", firstname, surname, patronymic)
                values (random_passport, random_name, random_surname, random_patronymic);
            end loop;


        --employees

        -- Перебираем все станции
        for cur_station in (select id from stations)
            loop
                -- Генерируем случайное количество сотрудников для станции
                employee_count := floor(random() * 21 + 5)::int; --[5; 25]

                for i in 1..employee_count
                    loop
                        -- Выбираем случайные ФИО
                        insert into employees (firstname, surname, patronymic, position, "managerID", "brigadeID",
                                               "stationID")
                        values (name[1 + floor(random() * array_length(name, 1))],
                                surname[1 + floor(random() * array_length(surname, 1))],
                                patronymics[1 + floor(random() * array_length(patronymics, 1))],
                                positions[1 + floor(random() * array_length(positions, 1))],
                                null, -- Пока что начальников не назначаем
                                null, -- Бригады тоже пока не назначаем
                                cur_station.id);
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
        for cur_train in (select id, "headStation" from trains)
            loop
                -- Назначаем бригаду только сотрудникам станции формирования поезда
                for cur_employee_id in (select id from employees where "stationID" = cur_train."headStation")
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

--5k
insert into routes ("departureStation", "arrivalStation")
select (select id from stations order by random() limit 1),
       (select id from stations order by random() limit 1)
from generate_series(1, 5000)
on conflict do nothing;

--20k
insert into schedule ("routeID", "trainID")
select (select id from routes order by random() limit 1),
       (select id from trains order by random() limit 1)
from generate_series(1, 20000)
on conflict do nothing;

--routes size
do
$$
    declare
        routes_size       int;
        stations_quantity int;
        cur_stationID     int;
        cur_routeID       int;
        cur_distance      int;
    begin
        select count(*) from routes into routes_size;

        for i in 1..routes_size
            loop
                cur_routeID = i;
                stations_quantity = floor(random() * 15 + 5)::int; --[5,20) станций
                for j in 1..stations_quantity
                    loop
                        select id from stations order by random() limit 1 into cur_stationID;
                        cur_distance = floor(random() * 45 + 5)::int; -- [5, 50) km
                        insert into "routeStations" ("routeID", "stationID", "stopOrder", distance)
                        values (cur_routeID, cur_stationID, j, cur_distance);
                    end loop;
            end loop;
    end;
$$;

--100k
do
$$
    declare
        cur_routeID                   int;
        cur_scheduleID                int;
        cur_passengerID               int;
        cur_stationsQuantity          int;
        cur_stopOrderRandom           int;
        cur_departureStationID        int;
        cur_departureStationStopOrder int;
        cur_arrivalStationID          int;
        cur_trainID                   int;
        cur_wagonID                   int;
        cur_placeNumber               int;
    begin

        for _ in 1..100000
            loop
                select id, "routeID", "trainID"
                from schedule
                order by random()
                limit 1
                into cur_scheduleID, cur_routeID, cur_trainID;

                select id from passengers order by random() limit 1 into cur_passengerID;


                select count(*) from "routeStations" where "routeID" = cur_routeID into cur_stationsQuantity;

                cur_stopOrderRandom = floor(random() * (cur_stationsQuantity - 2) + 1)::int;
                -- [1, cur_stationsQuantity - 3]

                -- станции не могут совпадать из-за триггера
                select "stationID", "stopOrder"
                from "routeStations"
                where "routeID" = cur_routeID
                  and "stopOrder" = cur_stopOrderRandom
                into cur_departureStationID, cur_departureStationStopOrder;

                cur_stopOrderRandom = floor(random() * (cur_stationsQuantity - cur_stopOrderRandom + 1) +
                                            cur_stopOrderRandom)::int; -- [cur_stopOrderRandom, cur_stationsQuantity]

                select "stationID"
                from "routeStations"
                where "routeID" = cur_routeID
                  and "stopOrder" = cur_stopOrderRandom
                into cur_arrivalStationID;

                select id from wagons w where "trainID" = cur_trainID order by random() limit 1 into cur_wagonID;

                cur_placeNumber =
                        floor(random() * (select capacity from wagons where id = cur_wagonID) + 1)::int; -- [1, capacity]

                if cur_placeNumber in (select "placeNumber"
                                       from "passengersTrips" ps
                                                join schedule sch on sch.id = cur_scheduleID
                                                join "routeStations" rs on rs."routeID" = cur_routeID and
                                                                           rs."stopOrder" >=
                                                                           cur_departureStationStopOrder
                                           and ps."wagonID" = cur_wagonID) then
                    continue; --если место занято
                end if;

                insert into "passengersTrips" ("scheduleID", "passengerID", "departureStationID", "arrivalStationID",
                                               "wagonID", "placeNumber")
                values (cur_scheduleID, cur_passengerID, cur_departureStationID, cur_arrivalStationID, cur_wagonID,
                        cur_placeNumber);

            end loop;
    end;
$$;

--schedule size
do
$$
    declare
        cur_scheduleID    INT;
        cur_routeID       INT;
        cur_stationID     INT;
        cur_stopOrder     INT;
        prev_arrival_time TIMESTAMP;
        travel_time       INTERVAL;
        stop_duration     INTERVAL;
        avg_speed         INT := 60; -- Средняя скорость поезда (км/ч)
    begin
        for cur_scheduleID, cur_routeID in (select id, "routeID" from schedule)
            loop

                prev_arrival_time = now() + (random() * interval '7 days'); --стартовое время

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
                            values (cur_scheduleID, cur_stationID, prev_arrival_time, prev_arrival_time, cur_stopOrder);
                        else
                            select interval '1 min' * (rs.distance / avg_speed * 60)
                            from "routeStations" rs
                            where "routeID" = cur_routeID
                              and "stationID" = cur_stationID
                            into travel_time;

                            prev_arrival_time = prev_arrival_time + travel_time;

                            insert into "timeSchedule" ("scheduleID", "stationID", "plannedArrivalTime",
                                                        "realArrivalTime", "stopDuration")
                            values (cur_scheduleID, cur_stationID, prev_arrival_time, prev_arrival_time,
                                    stop_duration);

                        end if;

                        prev_arrival_time = prev_arrival_time + stop_duration;

                    end loop;
            end loop;
    end;
$$;