-- триггер для добавления вагона в состав

create or replace function check_wagon_limit()
    returns trigger as
$$
declare
    count_wagons int;
    max_wagons   int;
begin
    select count(*) into count_wagons from wagons where "trainID" = new."trainID";

    select "quantityWagon" into max_wagons from trains where id = new."trainID";

    if count_wagons >= max_wagons then
        raise exception 'Невозможно добавить вагон. У поезда % превышен лимит вагонов(%)', new."trainID", max_wagons;
    end if;

    return new;
end;
$$ language plpgsql;


create trigger trig_check_wagon_limit
    before insert or update
    on wagons
    for each row
execute function check_wagon_limit();

-- триггер для проверки станции отправления и прибытия

create or replace function check_passengers_trip_stations()
    returns trigger as
$$
begin
    if new."departureStationID" = new."arrivalStationID" then
        raise exception 'Станция отправления не может быть станцией прибытия';
    end if;

    return new;
end;


$$
    language plpgsql;

create trigger trig_check_passenger_trip
    before insert or update
    on "passengersTrips"
    for each row
execute function check_passengers_trip_stations();

-- триггер для проверки, что время остановки не может быть < 0

create or replace function check_stop_duration()
    returns trigger as
$$

begin
    if new."stopDuration" < interval '0' then
        raise exception 'Время остановки не может быть отрицательным';
    end if;

    return new;

end;

$$ language plpgsql;

create trigger trig_check_stop_duration
    before insert or update
    on "timeSchedule"
    for each row
execute function check_stop_duration();

-- проверка что место вообще существует в вагоне

-- триггер для проверки станции отправления и прибытия в маршрутах

create or replace function check_routes_stations()
    returns trigger as
$$
begin
    if new."departureStation" = new."arrivalStation" then
        raise exception 'Станция отправления не может быть станцией прибытия';
    end if;

    return new;
end;


$$
    language plpgsql;

create trigger trig_check_routes_stations
    before insert or update
    on "routes"
    for each row
execute function check_routes_stations();

-- ===========================================================

-- триггер, проверяющий, корректные ли данные вносятся в timetable в части соответствия поезда и станции назначенному на данный поезд маршруту.

create or replace function check_timeschedule_validity()
    returns trigger as
$$
declare
    route_count int;
begin
    -- Проверяем, есть ли такая станция в маршруте поезда
    select count(*)
    into route_count
    from schedule s
             join "routeStations" rs on s."routeID" = rs."routeID"
    where s.id = new."scheduleID"
      and rs."stationID" = new."stationID";

    -- Если маршрут не включает станцию, выбрасываем ошибку
    if route_count = 0 then
        raise exception 'Ошибка: Станция % не входит в маршрут поезда с расписанием %!', new."stationID", new."scheduleID";
    end if;

    return new;
end;
$$ language plpgsql;

create trigger validate_timetable
    before insert or update
    on "timeSchedule"
    for each row
execute function check_timeschedule_validity();


-- триггер, проверяющий, верно ли в таблице расписаний стоит время при внесении новых данных (новой строки): оно должно быть больше времени,
-- стоящего для предыдущей (в соответствии с маршрутом) станции данного поезда. Если текущее время меньше предыдущего триггер должен
-- автоматически сделать текущее время больше предыдущего на заданный интервал. Интервал ищется триггером автоматически путём поиска
-- онного у других поездов, передвигающихся между этими же двумя точками. Если таковых нет, интервал берётся дефолтный (константа).

create or replace function update_timeschedule_planned()
    returns trigger as
$$
declare
    cur_route_id     int;
    cur_stop_order   int;
    prev_station     int;
    prev_time        timestamp with time zone;
    default_interval interval = '10 min';
begin
    select "routeID"
    into cur_route_id
    from schedule
    where id = new."scheduleID";

    select "stopOrder"
    into cur_stop_order
    from "routeStations"
    where "routeID" = cur_route_id
      and "stationID" = new."stationID";

    if cur_stop_order is null then
        return null;
    end if;

    if cur_stop_order = 0 then
        return new;
    end if;

    -- получаем предыдущую станцию по маршруту
    select "stationID"
    into prev_station
    from "routeStations"
    where "routeID" = cur_route_id
      and "stopOrder" = cur_stop_order - 1;

    if prev_station is null then
        return null;
    end if;

    -- получаем planned arrival time предыдущей станции для данного расписания
    select "plannedArrivalTime"
    into prev_time
    from "timeSchedule"
    where "scheduleID" = new."scheduleID"
      and "stationID" = prev_station;

    if prev_time is null then
        return new;
    end if;

    -- если новое время меньше или равно времени предыдущей станции, корректируем его
    if new."plannedArrivalTime" <= prev_time then
        -- пытаемся найти интервал между этими двумя станциями у других расписаний
        select avg(ts2."plannedArrivalTime" - ts1."plannedArrivalTime")
        into default_interval
        from schedule sch
                 join "timeSchedule" ts1 on ts1."scheduleID" = sch.id and ts1."stationID" = prev_station
                 join "timeSchedule" ts2 on ts2."scheduleID" = sch.id and ts2."stationID" = new."stationID"
        where sch."routeID" = cur_route_id;

        new."plannedArrivalTime" = prev_time + default_interval;
    end if;

    return new;
end;
$$
    language plpgsql;

create trigger trg_update_timeschedule
    before insert or update
    on "timeSchedule"
    for each row
execute function update_timeschedule_planned();

-- триггер для таблицы маршрутов, который автоматически ставит номер маршрута, если он не заполнен при вставке данных.
-- Номер ставится по следующему правилу: минимальное число, которого нет ни в таблице маршрутов, ни в таблице расписания.

create or replace function auto_assign_route_id()
    returns trigger as
$$
declare
    new_id int;
begin
    if new.id is null then
        -- Ищем минимальный ID, которого нет в routes и schedule
        select min(t.id)
        into new_id
        from generate_series(1, max((select count(*) + 1 from routes), (select count(*) + 1 from schedule))) t(id)
        where not exists (select 1 from routes r where r.id = t.id)
          and not exists (select 1 from schedule s where s."id" = t.id)
        limit 1;

        new.id := new_id;
    end if;

    return new;
end;
$$ language plpgsql;

create trigger auto_route_id
    before insert
    on routes
    for each row
execute function auto_assign_route_id();


-- триггер, который логирует (записывает все параметры в отдельную таблицу учёта) все удаления поездов, на которых было продано более 300 билетов.
-- В таблицу аудита надо бы записать и число удалённых билетов.

create or replace function log_deleted_trains()
    returns trigger as
$$
declare
    ticket_count int;
    train_num    varchar(20);
begin
    -- Считаем количество проданных билетов на этот поезд
    select count(*)
    into ticket_count
    from "passengersTrips"
    where "scheduleID" in (select id from schedule where "trainID" = old.id);

    -- Если билетов больше 300, логируем удаление
    if ticket_count > 300 then
        select "trainNumber" into train_num from trains where id = old.id;

        insert into "trainDeletionLog" (train_id, train_number, deleted_ticket_count)
        values (old.id, train_num, ticket_count);
    end if;

    return old;
end;
$$ language plpgsql;

create trigger log_train_deletions
    before delete
    on trains
    for each row
execute function log_deleted_trains();

