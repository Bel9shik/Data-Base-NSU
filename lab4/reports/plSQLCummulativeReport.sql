set serveroutput on;

do
$$
    declare
        type                daily_rec is record (
    dt                  date,
    trip_count          number,
    passenger_count     number,
    passenger_km        number,
    quarter             varchar2(10),
    yr                  varchar2(10)
  );
        type                daily_tab is table of daily_rec index by pls_integer;
        daily_data          daily_tab;
        cum_trip_count      number := 0;
        cum_passenger_count number := 0;
        cum_passenger_km    number := 0;
        v_line              varchar2(4000);
    begin
        -- пакетный выбор агрегированных данных по датам (без вложенности, простое group by)
        select trunc(ts."plannedArrivalTime")                  as dt,
               count(distinct sch.id)                          as trip_count,
               count(pt.id)                                    as passenger_count,
               0                                               as passenger_km, -- здесь можно подставить вычисление пассажиро-километров
               to_char(trunc(ts."plannedArrivalTime"), 'q')    as quarter,
               to_char(trunc(ts."plannedArrivalTime"), 'yyyy') as yr
            bulk collect
        into daily_data
        from "passengersTrips" pt
                 join "timeSchedule" ts on pt."scheduleID" = ts."scheduleID"
            and ts."stationID" = pt."departureStationID"
                 join schedule sch on sch.id = pt."scheduleID"
        group by trunc(ts."plannedArrivalTime"),
                 to_char(trunc(ts."plannedArrivalTime"), 'q'),
                 to_char(trunc(ts."plannedArrivalTime"), 'yyyy')
        order by trunc(ts."plannedArrivalTime");

        dbms_output.put_line('дата       | число_поездок | число перевезённых пассажиров | пассажиро-километры | квартал | год');
        dbms_output.put_line('-----------------------------------------------------------------------------------------------');

        for i in 1..daily_data.count
            loop
                cum_trip_count := cum_trip_count + daily_data(i).trip_count;
                cum_passenger_count := cum_passenger_count + daily_data(i).passenger_count;
                cum_passenger_km := cum_passenger_km + daily_data(i).passenger_km;

                v_line := to_char(daily_data(i).dt, 'dd.mm.yyyy') || ' | ' ||
                          lpad(cum_trip_count, 14) || ' | ' ||
                          lpad(cum_passenger_count, 30) || ' | ' ||
                          lpad(cum_passenger_km, 21) || ' | ' ||
                          lpad(daily_data(i).quarter, 7) || ' | ' ||
                          daily_data(i).yr;

                dbms_output.put_line(v_line);
            end loop;

    exception
        when others then
            dbms_output.put_line('ошибка: ' || sqlerrm);
    end
$$;
