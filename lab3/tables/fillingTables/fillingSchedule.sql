insert into "schedule" (routeID, trainID)
select floor(random() * 10000 + 1)::int,
       floor(random() * 1000 + 1)::int
from generate_series(1, 5000);