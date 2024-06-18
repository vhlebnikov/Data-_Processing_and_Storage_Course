SELECT flight_no, day_of_week, fare_conditions, avg, min, max
FROM
(SELECT f.flight_no, to_char(f.scheduled_departure, 'day') AS day_of_week,
        EXTRACT(DOW FROM f.scheduled_departure) AS dow_num, tf.fare_conditions,
        ROUND(AVG(tf.amount), 2) AS avg, MIN(tf.amount) AS min, MAX(tf.amount) AS max
 FROM flights f
     JOIN ticket_flights tf ON f.flight_id = tf.flight_id
 GROUP BY f.flight_no, day_of_week, dow_num, tf.fare_conditions
 ORDER BY f.flight_no, dow_num, avg) AS t;