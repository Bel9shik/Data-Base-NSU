with total_distance as (
    -- Вычисляем накопительное расстояние для каждого маршрута
    select "routeID",
           "stationID",
           "stopOrder",
           sum("distance") over (partition by "routeID" order by "stopOrder") as cum_dist
    from "routeStations"),
     trip_data as (
         -- Собираем данные по каждой поездке пассажира
         select (ts."realArrivalTime")::date                             as trip_date,
                pt."scheduleID",
                pt."passengerID",
                pt."departureStationID",
                pt."arrivalStationID",
                sch."routeID",
                (
                    -- Накопительное расстояние до станции прибытия
                    select tot_arr.cum_dist
                    from total_distance tot_arr
                    where tot_arr."routeID" = sch."routeID"
                      and tot_arr."stationID" = pt."arrivalStationID")
                    -
                (
                    -- Накопительное расстояние до станции отправления
                    select tot_dep.cum_dist
                    from total_distance tot_dep
                    where tot_dep."routeID" = sch."routeID"
                      and tot_dep."stationID" = pt."departureStationID") as trip_distance
         from "passengersTrips" pt
                  join schedule sch on sch.id = pt."scheduleID"
                  join "timeSchedule" ts on ts."scheduleID" = sch.id
             and ts."stationID" = pt."departureStationID"),
     daily_trips as (select trip_date                    as date,
                               count(distinct "scheduleID") as quantity_trips,
                               count(*)                     as quantity_passengers,
                               sum(trip_distance)           as passengers_kilometers
                        from trip_data
                        group by trip_date)

select to_char(date, 'DD.MM.YYYY') as "Дата",
       sum(quantity_trips) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS "Число поездок",
       sum(quantity_passengers) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS "Число перевезённых пассажиров",
       sum(passengers_kilometers) OVER (ORDER BY date ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS "Число пассажиро-километров"
from daily_trips
order by date;


