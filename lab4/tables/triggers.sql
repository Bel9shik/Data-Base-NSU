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

create or replace function check_timetable_validity()
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
execute function check_timetable_validity();


-- триггер, проверяющий, верно ли в таблице расписаний стоит время при внесении новых данных (новой строки): оно должно быть больше времени,
-- стоящего для предыдущей (в соответствии с маршрутом) станции данного поезда. Если текущее время меньше предыдущего триггер должен
-- автоматически сделать текущее время больше предыдущего на заданный интервал. Интервал ищется триггером автоматически путём поиска
-- онного у других поездов, передвигающихся между этими же двумя точками. Если таковых нет, интервал берётся дефолтный (константа).

create or replace function adjust_schedule_time()
    returns trigger as
$$
declare
    prev_time timestamp with time zone;
    travel_interval interval;
    default_interval interval = '30 minutes';  -- Дефолтный интервал
begin
    -- Получаем время прибытия на предыдущую станцию
    select ts."plannedArrivalTime"
    into prev_time
    from "timeSchedule" ts
             join "routeStations" rs on ts."stationID" = rs."stationID"
    where ts."scheduleID" = new."scheduleID"
      and rs."routeID" = (select "routeID" from schedule where id = new."scheduleID")
      and rs."stopOrder" = (select rs2."stopOrder" - 1
                            from "routeStations" rs2
                            where rs2."stationID" = new."stationID"
                              and rs2."routeID" = (select "routeID" from schedule where id = new."scheduleID"))
    order by ts."plannedArrivalTime" desc
    limit 1;

    -- Если предыдущая станция есть
    if prev_time is not null then
        -- Ищем интервал между станциями по другим поездам
        select avg(ts2."plannedArrivalTime" - ts1."plannedArrivalTime")
        into travel_interval
        from "timeSchedule" ts1
                 join "timeSchedule" ts2 on ts1."scheduleID" = ts2."scheduleID"
        where ts1."stationID" = (select rs2."stationID"
                                 from "routeStations" rs2
                                 where rs2."routeID" = (select "routeID" from schedule where id = new."scheduleID")
                                   and rs2."stopOrder" = (select rs3."stopOrder" - 1
                                                          from "routeStations" rs3
                                                          where rs3."stationID" = new."stationID"))
          and ts2."stationID" = new."stationID";

        -- Если интервал не найден, берём дефолтный
        if travel_interval is null then
            travel_interval := default_interval;
        end if;

        -- Проверяем время и исправляем, если необходимо
        if new."plannedArrivalTime" <= prev_time then
            new."plannedArrivalTime" := prev_time + travel_interval;
        end if;
    end if;

    return new;
end;
$$ language plpgsql;

create trigger enforce_schedule_order
    before insert or update on "timeSchedule"
    for each row
execute function adjust_schedule_time();


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
        select min(t.id) into new_id
        from generate_series(1, (select count(*) + 1 from routes)) t(id)
        where not exists (select 1 from routes r where r.id = t.id)
          and not exists (select 1 from schedule s where s."routeID" = t.id)
        limit 1;

        new.id := new_id;
    end if;

    return new;
end;
$$ language plpgsql;

create trigger auto_route_id
    before insert on routes
    for each row
execute function auto_assign_route_id();


-- триггер, который логирует (записывает все параметры в отдельную таблицу учёта) все удаления поездов, на которых было продано более 300 билетов.
-- В таблицу аудита надо бы записать и число удалённых билетов.

create or replace function log_deleted_trains()
    returns trigger as
$$
declare
    ticket_count int;
    train_num varchar(20);
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
    before delete on trains
    for each row
execute function log_deleted_trains();

