with total_distance as (
    -- вычисляем накопительное расстояние для каждого маршрута
    select "routeID",
           "stationID",
           "stopOrder",
           sum("distance") over (partition by "routeID" order by "stopOrder") as cum_dist
    from "routeStations"
),
     trip_data as (
         -- собираем данные по каждой поездке пассажира
         select
             (ts."realArrivalTime")::date as trip_date,
             pt."scheduleID",
             pt."passengerID",
             pt."departureStationID",
             pt."arrivalStationID",
             sch."routeID",
             arr.cum_dist - dep.cum_dist as trip_distance
         from "passengersTrips" pt
                  join schedule sch on sch.id = pt."scheduleID"
                  join "timeSchedule" ts on ts."scheduleID" = sch.id
             and ts."stationID" = pt."departureStationID"
                  join total_distance dep
                            on dep."routeID" = sch."routeID"
                                and dep."stationID" = pt."departureStationID"
                  join total_distance arr
                            on arr."routeID" = sch."routeID"
                                and arr."stationID" = pt."arrivalStationID"
     ),
     daily_trips as (
         -- группируем данные по дате поездки
         select
             trip_date as date,
             count(distinct "scheduleID") as quantity_trips,
             count(*) as quantity_passengers,
             sum(trip_distance) as passengers_kilometers
         from trip_data
         group by trip_date
     )
select
    to_char(date, 'DD.MM.YYYY') as "дата",
    sum(quantity_trips) over (order by date rows between unbounded preceding and current row) as "число поездок",
    sum(quantity_passengers) over (order by date rows between unbounded preceding and current row) as "число перевезённых пассажиров",
    sum(passengers_kilometers) over (order by date rows between unbounded preceding and current row) as "число пассажиро-километров"
from daily_trips
order by date;

