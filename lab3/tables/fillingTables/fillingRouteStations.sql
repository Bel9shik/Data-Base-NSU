insert into "routeStations" ("routeID", "stationID", "stopOrder")
select idRoute,
       idStation,
       row_number() over (partition by idRoute order by random())
from (select floor(random() * 5000 + 1)::int as idRoute,
             floor(random() * 500 + 1)::int  as idStation
      from generate_series(1, 50000)) as subquery
on conflict do nothing;