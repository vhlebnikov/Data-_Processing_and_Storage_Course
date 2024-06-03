package repository

import (
	"errors"
	"fmt"
	"github.com/jmoiron/sqlx"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"log"
	"time"
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

func (r *FlightPostgres) GetAirportCodeFromCity(city string) (string, error) {
	query := `SELECT airports.airport_code FROM airports WHERE city=$1 LIMIT 1`

	var code string
	err := r.db.Get(&code, query, city)
	if err != nil {
		return "", err
	}
	return code, nil
}

func (r *FlightPostgres) GetRoutes(limit, offset, stepLimit int, origin, destination,
	fareCondition string, departureDate time.Time) (int, []model.Route, error) {

	date := fmt.Sprintf("%d-%d-%d", departureDate.Year(), departureDate.Month(), departureDate.Day())

	queryCreateView := fmt.Sprintf(`CREATE OR REPLACE VIEW my_routes(flight_id, flight_no, departure_airport, arrival_airport, scheduled_departure, scheduled_arrival) AS
						SELECT DISTINCT flight_id, flight_no, departure_airport, arrival_airport, scheduled_departure, scheduled_arrival
						FROM flights f
						JOIN seats s
						  ON f.aircraft_code = s.aircraft_code
						WHERE s.fare_conditions='%s'
						AND f.scheduled_departure >= '%s'::DATE
						AND f.scheduled_departure <= date_trunc('day', '%s'::DATE + 1)
						AND f.status <> 'Cancelled'`, fareCondition, date, date)

	queryCreateIndexDeparture := `CREATE INDEX IF NOT EXISTS idx_departure_airport_my_routes ON flights(departure_airport)`
	queryCreateIndexArrival := `CREATE INDEX IF NOT EXISTS idx_arrival_airport_my_routes ON flights(arrival_airport)`

	queryGetRoutes := `WITH RECURSIVE flight_paths AS (
						SELECT
							departure_airport, arrival_airport, scheduled_departure, scheduled_arrival,
							1 AS step,
							ARRAY[departure_airport, arrival_airport]::VARCHAR[] AS airport_path,
							ARRAY[flight_no]::VARCHAR[] AS flight_no_path,
							ARRAY[flight_id::VARCHAR]::VARCHAR[] as flight_id_path
						FROM my_routes
						WHERE departure_airport=$1
					
						UNION ALL
					
						SELECT
							r.departure_airport, r.arrival_airport, fp.scheduled_departure, r.scheduled_arrival,
							fp.step + 1,
							(fp.airport_path || r.arrival_airport)::VARCHAR[],
							(fp.flight_no_path || r.flight_no)::VARCHAR[],
							(fp.flight_id_path || r.flight_id::VARCHAR)::VARCHAR[]
						FROM my_routes r
						JOIN flight_paths fp ON r.departure_airport=fp.arrival_airport
						WHERE fp.step < $2
						AND r.scheduled_departure >= fp.scheduled_arrival
						AND r.arrival_airport <> ALL(fp.airport_path)
					)
					
					SELECT airport_path,
						   step,
						   flight_no_path,
						   flight_id_path,
						   scheduled_departure,
						   scheduled_arrival
					FROM flight_paths
					WHERE arrival_airport = $3
					LIMIT $4 OFFSET $5`

	queryCount := `WITH RECURSIVE flight_paths AS (
						SELECT
							departure_airport, arrival_airport, scheduled_departure, scheduled_arrival,
							1 AS step,
							ARRAY[departure_airport, arrival_airport]::VARCHAR[] AS airport_path,
							ARRAY[flight_no]::VARCHAR[] AS flight_no_path,
							ARRAY[flight_id::VARCHAR]::VARCHAR[] as flight_id_path
						FROM my_routes
						WHERE departure_airport=$1
					
						UNION ALL
					
						SELECT
							r.departure_airport, r.arrival_airport, fp.scheduled_departure, r.scheduled_arrival,
							fp.step + 1,
							(fp.airport_path || r.arrival_airport)::VARCHAR[],
							(fp.flight_no_path || r.flight_no)::VARCHAR[],
							(fp.flight_id_path || r.flight_id::VARCHAR)::VARCHAR[]
						FROM my_routes r
						JOIN flight_paths fp ON r.departure_airport=fp.arrival_airport
						WHERE fp.step < $2
						AND r.scheduled_departure >= fp.scheduled_arrival
						AND r.arrival_airport <> ALL(fp.airport_path)
					)
					
					SELECT COUNT(*)
					FROM flight_paths
					WHERE arrival_airport = $3`

	tx, err := r.db.Beginx()
	if err != nil {
		return 0, nil, err
	}
	defer tx.Rollback()

	_, err = tx.Exec(queryCreateView)
	if err != nil {
		log.Println(departureDate)
		return 0, nil, err
	}
	_, err = tx.Exec(queryCreateIndexDeparture)
	if err != nil {
		return 0, nil, err
	}
	_, err = tx.Exec(queryCreateIndexArrival)
	if err != nil {
		return 0, nil, err
	}

	var routes []model.Route
	var count int

	err = tx.Select(&routes, queryGetRoutes, origin, stepLimit, destination, limit, offset)
	if err != nil {
		return 0, nil, err
	}
	err = tx.Get(&count, queryCount, origin, stepLimit, destination)
	if err != nil {
		return 0, nil, err
	}

	if err = tx.Commit(); err != nil {
		return 0, nil, err
	}

	return count, routes, nil
}
