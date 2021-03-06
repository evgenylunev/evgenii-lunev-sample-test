SELECT table1.station_number, table1.month, table2.month, first_day, second_day, (PARSE_UTC_USEC(CAST(table1.year as string) + '-' + CAST(table1.month as string) + '-' + CAST(first_day as string) + ' 01:00:00') / 1000) as timestamp,
 (mean_temp1 - 32)*5/9 as temperature1, (mean_temp2 - 32)*5/9 as temperature2 FROM (select station_number, year, month, day as first_day, mean_temp as mean_temp1 from [publicdata:samples.gsod] where station_number=726506) as table1 
join each (select station_number, year, month, (day + 1) as second_day, mean_temp as mean_temp2 from [publicdata:samples.gsod] where station_number=726506) as table2 
on first_day = second_day and table1.month = table2.month and table1.year = table2.year
where (mean_temp1 > 0 and mean_temp2 < 0) or (mean_temp1 < 0 and mean_temp2 > 0)
order by table1.year desc, table1.month desc, first_day desc LIMIT 1000

SELECT table1.station_number, table1.year, table2.year, table1.month, table2.month, first_day, second_day -1, (PARSE_UTC_USEC(CAST(table1.year as string) + '-' + CAST(table1.month as string) + '-' + CAST(first_day as string) + ' 01:00:00') / 1000) as timestamp,
 (mean_temp1 - 32)*5/9 as temperature1, (mean_temp2 - 32)*5/9 as temperature2, rain1, rain2 FROM (select station_number, year, month, day as first_day, mean_temp as mean_temp1, rain as rain1 from [publicdata:samples.gsod] where station_number=726506) as table1 
join each (select station_number, year, month, (day + 1) as second_day, mean_temp as mean_temp2,  rain as rain2 from [publicdata:samples.gsod] where station_number=726506) as table2 
on first_day = second_day and table1.month = table2.month and table1.year = table2.year
where rain1 != rain2
order by table1.year desc, table1.month desc, first_day desc LIMIT 1000