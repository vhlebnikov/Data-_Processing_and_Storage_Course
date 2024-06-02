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

func (r *FlightPostgres) GetSchedule(limit, offset int, direction, airportCode string) (int, []model.ScheduleFlight, error) {
	var query string
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
					WHERE arrival_airport = 'BTK'
					GROUP BY flight_no, arrival_time, airport_name
					ORDER BY flight_no;`
	case "outbound":

	default:
		return 0, nil, errors.New("unknown 'direction' value")
	}
}
