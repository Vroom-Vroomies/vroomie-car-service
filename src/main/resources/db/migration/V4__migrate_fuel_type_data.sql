UPDATE tbl_car SET fuel_type =
                       CASE
                           WHEN fuel_type_varchar = '휘발유' THEN 'GASOLINE'
                           WHEN fuel_type_varchar = '경유' THEN 'DIESEL'
                           WHEN fuel_type_varchar = 'LPG' THEN 'LPG'
                           WHEN fuel_type_varchar = '하이브리드' THEN 'HYBRID'
                           WHEN fuel_type_varchar = '전기' THEN 'ELECTRIC'
                           ELSE NULL
                           END;