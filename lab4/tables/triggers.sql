-- триггер для добавления вагона в состав

create or replace function check_wagon_limit()
    returns trigger as
$$
declare
    count_wagons int;
    max_wagons   int;
begin
    select count(*) into count_wagons from wagons where "trainID" = NEW."trainID";

    select "quantityWagon" into max_wagons from trains where id = NEW."trainID";

    if count_wagons >= max_wagons then
        raise exception 'Невозможно добавить вагон. У поезда % превышен лимит вагонов(%)', new."trainID", max_wagons;
    end if;

    return NEW;
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



