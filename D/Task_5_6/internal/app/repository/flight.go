package repository

import (
	"errors"
	"github.com/jmoiron/sqlx"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
)

type FlightPostgres struct {
	db *sqlx.DB
}

func NewFlightPostgres(db *sqlx.DB) *FlightPostgres {
	return &FlightPostgres{db: db}
}

// GetSchedule If only time needed -> just cast time to string
func (r *FlightPostgres) GetSchedule(limit, offset int, direction, airportCode string) (int, []model.ScheduleFlight, error) {
	var query string
	var queryCount string

	switch direction {
	case "inbound":
		query = `SELECT flight_no, arrival_time, array_agg(dow) as dow, airport_name
					FROM (SELECT f.flight_no,
								 CAST(f.scheduled_arrival AS TIME) AS arrival_time,
								 to_char(f.scheduled_arrival, 'ID'::text)::integer as dow,
								 a.airport_name, f.arrival_airport
						  FROM flights f
						  JOIN airports a
						  ON f.departure_airport = a.airport_code
						  GROUP BY f.flight_no, dow, arrival_time, a.airport_name, f.arrival_airport
						  ORDER BY flight_no) AS t1
					WHERE arrival_airport = $1
					GROUP BY flight_no, arrival_time, airport_name
					ORDER BY flight_no
					LIMIT $2 OFFSET $3`

		queryCount = `SELECT DISTINCT(COUNT(*) OVER())
						FROM (SELECT f.flight_no,
									 CAST(f.scheduled_arrival AS TIME) AS arrival_time,
									 to_char(f.scheduled_arrival, 'ID'::text)::integer as dow,
									 a.airport_name, f.arrival_airport
							  FROM flights f
							  JOIN airports a
							  ON f.departure_airport = a.airport_code
							  GROUP BY f.flight_no, dow, arrival_time, a.airport_name, f.arrival_airport
							  ORDER BY flight_no) AS t1
						WHERE arrival_airport = $1
						GROUP BY flight_no, arrival_time, airport_name`
	case "outbound":
		query = `SELECT flight_no, departure_time, array_agg(dow) as dow, airport_name
					FROM (SELECT f.flight_no,
								 CAST(f.scheduled_departure AS TIME) AS departure_time,
								 to_char(f.scheduled_departure, 'ID'::text)::integer as dow,
								 a.airport_name, f.departure_airport
						  FROM flights f
								   JOIN airports a
										ON f.arrival_airport = a.airport_code
						  GROUP BY f.flight_no, dow, departure_time, a.airport_name, f.departure_airport
						  ORDER BY flight_no) AS t1
					WHERE departure_airport = $1
					GROUP BY flight_no, departure_time, airport_name
					ORDER BY flight_no
					LIMIT $2 OFFSET $3`

		queryCount = `SELECT DISTINCT(COUNT(*) OVER())
						FROM (SELECT f.flight_no,
									 CAST(f.scheduled_departure AS TIME) AS departure_time,
									 to_char(f.scheduled_departure, 'ID'::text)::integer as dow,
									 a.airport_name, f.departure_airport
							  FROM flights f
									   JOIN airports a
											ON f.arrival_airport = a.airport_code
							  GROUP BY f.flight_no, dow, departure_time, a.airport_name, f.departure_airport
							  ORDER BY flight_no) AS t1
						WHERE departure_airport = $1
						GROUP BY flight_no, departure_time, airport_name`
	default:
		return 0, nil, errors.New("unknown 'direction' value")
	}

	var scheduledFlights []model.ScheduleFlight
	var count int

	tx, err := r.db.Beginx()
	if err != nil {
		return 0, nil, err
	}
	defer tx.Rollback()

	if err = tx.Select(&scheduledFlights, query, airportCode, limit, offset); err != nil {
		return 0, nil, err
	}

	if err = tx.Get(&count, queryCount, airportCode); err != nil {
		return 0, nil, err
	}

	if err = tx.Commit(); err != nil {
		return 0, nil, err
	}

	return count, scheduledFlights, nil
}
