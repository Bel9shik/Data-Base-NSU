-- в timeschedule добавить pk +
-- capasity in wagonstype +
-- бригада как отдельная сущность и её связи +

do
$$
    declare
        batch_size constant int := 100; -- размер пакета
        offset_val          int := 0; -- смещение для выборки очередного пакета
        rec                 record;
        rows_found          int;
        count               int;
    begin
        --------------------------------------------------------------------------
        -- шаг 1. предварительные расчёты во временных таблицах
        --------------------------------------------------------------------------
        drop table if exists temp_total_distance;
        create temp table temp_total_distance as
        select "routeID",
               "stationID",
               "stopOrder",
               sum("distance") over (partition by "routeID" order by "stopOrder") as cum_dist
        from "routeStations";

        drop table if exists temp_trip_data;
        create temp table temp_trip_data as
        select (ts."realArrivalTime")::date as trip_date,
               pt."scheduleID",
               pt."passengerID",
               pt."departureStationID",
               pt."arrivalStationID",
               sch."routeID",
               arr.cum_dist - dep.cum_dist  as trip_distance
        from "passengersTrips" pt
                 join schedule sch
                      on sch.id = pt."scheduleID"
                 join "timeSchedule" ts
                      on ts."scheduleID" = sch.id
                          and ts."stationID" = pt."departureStationID"
                 join temp_total_distance dep
                      on dep."routeID" = sch."routeID"
                          and dep."stationID" = pt."departureStationID"
                 join temp_total_distance arr
                      on arr."routeID" = sch."routeID"
                          and arr."stationID" = pt."arrivalStationID";

        drop table if exists temp_daily_trips;
        create temp table temp_daily_trips as
        select trip_date,
               count(distinct "scheduleID") as quantity_trips,
               count(*)                     as quantity_passengers,
               sum(trip_distance)           as passengers_kilometers
        from temp_trip_data
        group by trip_date
        order by trip_date;

        --------------------------------------------------------------------------
        -- шаг 2. итоговая выборка с пакетной обработкой (limit/offset)
        --------------------------------------------------------------------------
        raise notice 'Дата: |, Число поездок: |, Число перевезённых пассажиров: |, Число пассажиро-километров: |';
        loop
            count = 0;
            for rec in
                select to_char(trip_date, 'DD.MM.YYYY')                      as trip_date_formatted,
                       sum(quantity_trips) over (order by trip_date
                           rows between unbounded preceding and current row) as total_trips,
                       sum(quantity_passengers) over (order by trip_date
                           rows between unbounded preceding and current row) as total_passengers,
                       sum(passengers_kilometers) over (order by trip_date
                           rows between unbounded preceding and current row) as total_pk
                from temp_daily_trips
                order by trip_date
                limit batch_size offset offset_val
                loop
                count = count + 1;
                    -- вывод данных текущего пакета в консоль
                    raise notice '% % % %',
                        lpad(rec.trip_date_formatted::text, 10) || ' | ', lpad(rec.total_trips::text, 10) || ' | ', lpad(rec.total_passengers::text, 10) || ' | ', lpad(rec.total_pk::text, 15);
                end loop;

            exit when count = 0; -- если пакет пустой, выходим из цикла

            offset_val := offset_val + batch_size; -- смещаем окно выборки на следующий пакет
        end loop;

    exception
        when others then
            raise notice 'Произошла ошибка: %', sqlerrm;
    end;
$$ language plpgsql;





create or replace function update_delayed_trains(p_date date)
    returns void as
$$
declare
    r record;
begin
    -- Выбираем поезда, которые задерживаются на указанную дату
    for r in
        select ts."scheduleID" as schedule_id, ts."stationID" as station_id, ts."realArrivalTime"
        from "timeSchedule" ts
        where ts."realArrivalTime"::date = p_date
          and ts."realArrivalTime" - ts."plannedArrivalTime" != interval '0 minute'
        loop
            -- Обновляем время прибытия и сбрасываем задержку
            update "timeSchedule" ts
            set "plannedArrivalTime" = ts."realArrivalTime"
            where "scheduleID" = r.schedule_id and "stationID" = r.station_id;

            raise notice 'Расписание: % | Станция: % | Новое время: %', r.schedule_id, r.station_id, r."realArrivalTime";
        end loop;

exception
    when others then
        raise warning 'Ошибка: %', sqlerrm;
end
$$
    language plpgsql;

select update_delayed_trains('2025-03-17');

select * from "timeSchedule" where "realArrivalTime"::date = '2025-03-17';

select ts."scheduleID" as schedule_id, ts."stationID" as station_id, ts."realArrivalTime"
from "timeSchedule" ts
where ts."realArrivalTime"::date = '2025-03-17'
  and ts."realArrivalTime" - ts."plannedArrivalTime" != interval '0 minute';