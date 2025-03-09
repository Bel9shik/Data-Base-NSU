insert into "timeSchedule" ("scheduleID", "stationID", "plannedArrivalTime", "realArrivalTime", "stopDuration")
select
    s.id AS "scheduleID",
    st.id AS "stationID",
    planned_time AS "plannedArrivalTime",
    planned_time + interval '1 minute' * floor(random() * 30) AS "realArrivalTime", -- Реальное время может отличаться на 0-30 минут
    interval '00:05' * floor(random() * 4 + 1) AS "stopDuration" -- Длительность остановки от 5 до 20 минут
FROM
    "schedule" s
        JOIN
    "routeStations" rs ON s.routeID = rs."routeID"
        JOIN
    "stations" st ON rs."stationID" = st.id
        CROSS JOIN LATERAL (
        SELECT
            now() + interval '1 hour' * rs."stopOrder" AS planned_time -- Генерация времени прибытия на основе порядка станции
        ) AS t;

