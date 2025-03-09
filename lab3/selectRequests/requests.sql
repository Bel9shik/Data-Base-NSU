-- в поездках пассажира хранить станцию отбытия и прибытия
-- разное время остановок для разных типов поездов (сделать задержку для каждого поезда)


-- ////////////////////////////////////////// select requests

-- 1) +

-- Отчёт о задержках указанного поезда в указанное время

select s.name,
       date(tt."realArrivalTime")                       as date,
       (tt."realArrivalTime" - tt."plannedArrivalTime") as delay
from "schedule" t
         join "timeSchedule" tt on tt."scheduleID" = t.id
         join "routeStations" rs on rs."routeID" = t.routeid
         join stations s on s.id = rs."stationID"
where t.trainid = 1
  and date(tt."plannedArrivalTime") between '2024-11-28 00:00:00' and '2025-11-30 00:00:00';


-- 2) +

-- Отчёты о маршрутах и поездах между указанными городами (без привязки к датам/расписанию).

with route_stations as (select rs."routeID",
                               s.name as stationName,
                               rs."stopOrder"
                        from "routeStations" rs
                                 join
                             stations s on rs."stationID" = s.id),
     filtered_routes as (select rs1."routeID",
                                rs1."stopOrder" as fromOrder,
                                rs2."stopOrder" as toOrder
                         from route_stations rs1
                                  join
                              route_stations rs2 on rs1."routeID" = rs2."routeID"
                         where rs1.stationName = 'Station_362'
                           and rs2.stationName = 'Station_224')

select distinct t.trainNumber,
                fr."routeID"
from filtered_routes fr
         join "schedule" sch on sch.routeID = fr."routeID"
         join trains t on sch.trainID = t.id;


-- 3) +

-- Все станции-пересадки по маршруту между двумя станциями (от заданной до заданной).

with recursive path as (select rs."stationID",
                               rs."stopOrder",
                               rs."routeID",
                               array [rs."stationID"] as "stationOrder",
                               array [rs."routeID"]   as "routeOrder",
                               1                      as nestedLevel
                        from "routeStations" rs
                        where rs."stationID" = 382

                        union all

                        select rs."stationID",
                               rs."stopOrder",
                               rs."routeID",
                               p."stationOrder" || rs."stationID",
                               p."routeOrder" || p."routeID",
                               p.nestedLevel + 1
                        from "routeStations" rs
                                 join path p on p.nestedLevel <= 3 and
                                                ((rs."routeID" = p."routeID" and rs."stopOrder" = p."stopOrder" + 1)
                                                    or
                                                 (p.nestedLevel != 1 and rs."routeID" != p."routeID" and
                                                  rs."stationID" = p."stationID" and rs."stopOrder" > p."stopOrder" and
                                                  (select count(*)
                                                   from unnest(p."routeOrder") as elements
                                                   where elements = rs."routeID") = 0)
                                                     and (select count(*)
                                                          from unnest(p."stationOrder") as elements
                                                          where elements = rs."stationID") < 2))

select distinct "stationOrder",
                "routeOrder",
                "stationID" as "arrivalStation",
                nestedLevel

from path p
where p."stationID" = 244;



-- 4) +
--
-- Количество свободных билетов на указанный поезд (от заданного города до заданного в указанный промежуток времени) с заданным типом мест (плацкарт/купе/СВ).

with totalQuantityTickets as (select distinct w.trainID,
                                              t.trainNumber,
                                              w.wagonTypeID,
                                              sum(w.capacity) over (partition by w.trainID, wagonTypeID) as capacity
                              from wagons w
                                       join trains t on w.trainID = t.id),

     trips as (select distinct sch.id
                             , tqt.capacity - count(*) over (partition by sch.id) as free
               from routes r
                        join schedule sch on r.id = sch.routeid and sch.trainid = 805
                        join totalQuantityTickets tQT on tQT.trainid = sch.trainid and tqt.wagontypeid = 1
                        join "routeStations" rs1 on r.id = rs1."routeID"
                        join stations s1 on s1.id = rs1."stationID"
                        join "routeStations" rs2 on r.id = rs2."routeID" and rs1."stopOrder" < rs2."stopOrder"
                        join stations s2 on s2.id = rs2."stationID"
                        join "timeSchedule" ts on ts."scheduleID" = sch.id and
                                                  ts."plannedArrivalTime" between '2024-11-28 00:00:00' and '2025-11-30 00:00:00'
                        join "passengersTrips" pT on sch.id = ts."scheduleID"
               where s1.name = 'Station_362'
                 and s2.name = 'Station_224')

select t.id, t.free
from trips t;

-- 5) +
-- Отчёт о едущих ближайших поездах в указанный город в указанный отрезок времени с указанием дат-времён отправления из начальной точки и прибытия в конечную точку.

with route_stations as (select rs1."routeID",
                               rs1."stationID" as "departureStationID",
                               rs1."stopOrder" as "departureStopOrder",
                               rs2."stationID" as "arrivalStationID",
                               rs2."stopOrder" as "arrivalStopOrder"
                        from "routeStations" rs1
                                 join "routeStations" rs2
                                      on rs1."routeID" = rs2."routeID" and rs2."stopOrder" > rs1."stopOrder"
                        where rs1."stationID" = '382'
                          and rs2."stationID" = '244'),

     trips as (select rs."routeID",
                      sch.trainid,
                      rs."arrivalStationID",
                      rs."departureStationID",
                      ts1."plannedArrivalTime" as "departureTime",
                      ts2."plannedArrivalTime" as "arrivalTime"
               from schedule sch
                        join route_stations rs on rs."routeID" = sch.routeid
                        join "timeSchedule" tS1
                             on sch.id = tS1."scheduleID" and ts1."stationID" = rs."departureStationID" and
                                ts1."plannedArrivalTime" between now() and now() + interval '2 hours'
                        join "timeSchedule" ts2
                             on sch.id = ts2."scheduleID" and ts2."stationID" = rs."arrivalStationID")

select *
from trips
order by "departureTime";

-- 6) +
-- Сотрудники РЖД с иерархией (у каждого сотрудника есть непосредственный рук-ль, у него – свой и т.д., у владельца бизнеса рук-ля нет).

with recursive EmployeeHierarchy as (select id,
                                            managerID,
                                            cast(id as varchar)        as hierarchy,
                                            lpad('', 0, ' ') || e.surname || ' ' || e.firstname || ' ' ||
                                            coalesce(e.patronymic, '') as fio,
                                            1                          as level
                                     from employees e
                                     where managerID is null
                                     union all
                                     select e.id,
                                            e.managerID,
                                            cast(eh.hierarchy || ' => ' || e.id as varchar) as hierarchy,
                                            lpad('', (eh.level) * 5, ' ') || e.surname || ' ' || e.firstname || ' ' ||
                                            coalesce(e.patronymic, '')                 as fio,
                                            eh.level + 1
                                     from employees e
                                              join
                                          EmployeeHierarchy eh on e.managerID = eh.id)
select fio,
       managerID,
       level
from EmployeeHierarchy
order by hierarchy;
