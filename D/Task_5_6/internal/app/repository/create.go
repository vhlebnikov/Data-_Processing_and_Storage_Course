package repository

import (
	"database/sql"
	"errors"
	"fmt"
	"github.com/jmoiron/sqlx"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"time"
)

type CreatePostgres struct {
	db *sqlx.DB
}

func NewCreatePostgres(db *sqlx.DB) *CreatePostgres {
	return &CreatePostgres{db: db}
}

func (r *CreatePostgres) GetFlightsPrices(flightsIds []int, fareConditions string, bookDate time.Time) ([]model.FlightPrice, error) {
	query := `SELECT f.flight_id, MIN(tf.amount) AS amount
				FROM flights f
				JOIN ticket_flights tf
				ON f.flight_id=tf.flight_id
				WHERE f.flight_id=$1
				AND tf.fare_conditions=$2
				GROUP BY f.flight_id, tf.fare_conditions`

	queryCreateFlights := fmt.Sprintf(`INSERT INTO flights (flight_no, scheduled_departure, scheduled_arrival, departure_airport, arrival_airport,
                     		status, aircraft_code)
							SELECT flight_no,
								   MAKE_TIMESTAMP(%d::INT, %d::INT, %d::INT,
									   EXTRACT(HOUR FROM scheduled_departure)::INT,
									   EXTRACT(MINUTE FROM scheduled_departure)::INT,
									   EXTRACT(SECOND FROM scheduled_departure)::DOUBLE PRECISION) AS scheduled_departure,
								   MAKE_TIMESTAMP(%d::INT, %d::INT, %d::INT,
									   EXTRACT(HOUR FROM scheduled_arrival)::INT,
									   EXTRACT(MINUTE FROM scheduled_arrival)::INT,
									   EXTRACT(SECOND FROM scheduled_arrival)::DOUBLE PRECISION) AS scheduled_arrival,
								   departure_airport, arrival_airport, status, aircraft_code
							FROM flights
							WHERE flight_id=$1
							RETURNING flight_id`, bookDate.Year(), bookDate.Month(), bookDate.Day(),
		bookDate.Year(), bookDate.Month(), bookDate.Day())

	tx, err := r.db.Beginx()
	if err != nil {
		return nil, err
	}
	defer tx.Rollback()

	newFlightIds := make([]int, 0, len(flightsIds))
	if bookDate.Year() > 2017 {
		for _, flightId := range flightsIds {
			var id int
			err = tx.Get(&id, queryCreateFlights, flightId)
			if err != nil {
				return nil, err
			}
			newFlightIds = append(newFlightIds, id)
		}
	}

	prices := make([]model.FlightPrice, 0, len(flightsIds))
	stmt, err := tx.Preparex(query)
	if err != nil {
		return nil, err
	}
	defer stmt.Close()

	for i, id := range flightsIds {
		var price model.FlightPrice
		err = stmt.Get(&price, id, fareConditions)
		if err != nil {
			if errors.Is(err, sql.ErrNoRows) {
				price = model.FlightPrice{
					FlightId: id,
					Amount:   0,
				}
			} else {
				return nil, err
			}
		}
		if bookDate.Year() > 2017 {
			price.FlightId = newFlightIds[i]
		}
		prices = append(prices, price)
	}

	if err = tx.Commit(); err != nil {
		return nil, err
	}
	return prices, nil
}

func (r *CreatePostgres) CreateBooking(bookDate time.Time, totalAmount float64, bookRef string) error {
	query := `INSERT INTO bookings (book_ref, book_date, total_amount) VALUES ($1, $2, $3)`

	_, err := r.db.Exec(query, bookRef, bookDate, totalAmount)
	return err
}

func (r *CreatePostgres) CreateTickets(flightPrices []model.FlightPrice, bookRef, fareConditions,
	passengerName string, passengerIds, ticketsNo []string, contactData model.JSON) ([]model.Ticket, error) {

	queryCreateTicket := `INSERT INTO tickets (ticket_no, book_ref, passenger_id, passenger_name, contact_data)
							VALUES ($1, $2, $3, $4, $5)`

	queryCreateTicketFlights := `INSERT INTO ticket_flights (ticket_no, flight_id, fare_conditions, amount)
									VALUES ($1, $2, $3, $4) RETURNING ticket_no, flight_id, fare_conditions, amount`

	tx, err := r.db.Beginx()
	if err != nil {
		return nil, err
	}
	defer tx.Rollback()

	stmtTicket, err := tx.Preparex(queryCreateTicket)
	if err != nil {
		return nil, err
	}
	defer stmtTicket.Close()

	stmtTicketFlights, err := tx.Preparex(queryCreateTicketFlights)
	if err != nil {
		return nil, err
	}
	defer stmtTicketFlights.Close()

	tickets := make([]model.Ticket, 0, len(flightPrices))

	for i, price := range flightPrices {
		_, err = stmtTicket.Exec(ticketsNo[i], bookRef, passengerIds[i], passengerName, contactData)
		if err != nil {
			return nil, err
		}

		var ticket model.Ticket
		err = stmtTicketFlights.Get(&ticket, ticketsNo[i], price.FlightId, fareConditions, price.Amount)
		if err != nil {
			return nil, err
		}
		tickets = append(tickets, ticket)
	}

	if err = tx.Commit(); err != nil {
		return nil, err
	}
	return tickets, nil
}

func (r *CreatePostgres) GetFareConditions(ticketNo string, flightId int) (string, error) {
	query := `SELECT fare_conditions
				FROM ticket_flights
				WHERE flight_id=$1
				AND ticket_no=$2`

	var fareConditions string
	err := r.db.Get(&fareConditions, query, flightId, ticketNo)
	if err != nil {
		return "", err
	}
	return fareConditions, nil
}

func (r *CreatePostgres) GetFreeSeatForFlight(flightId int, fareConditions string) (string, error) {
	query := `SELECT s.seat_no
				FROM seats s
				JOIN flights f
				ON s.aircraft_code = f.aircraft_code
				WHERE f.flight_id=$1
				AND s.fare_conditions=$2
				
				EXCEPT
				
				SELECT b.seat_no
				FROM boarding_passes b
				JOIN seats s
				ON b.seat_no = s.seat_no
				WHERE b.flight_id=$3
				AND s.fare_conditions=$4
				LIMIT 1`

	var seatNo string
	err := r.db.Get(&seatNo, query, flightId, fareConditions, flightId, fareConditions)
	if err != nil {
		return "", err
	}
	return seatNo, nil
}

func (r *CreatePostgres) GetNextBoardingNo() (int, error) {
	query := `SELECT boarding_no + 1 as id
				FROM boarding_passes
				ORDER BY boarding_no DESC
				LIMIT 1`

	var id int
	err := r.db.Get(&id, query)
	if err != nil {
		return 0, err
	}
	return id, nil
}

func (r *CreatePostgres) CreateBoardingPass(boardingNo, flightId int, seatNo, ticketNo string) (model.BoardingPass, error) {
	query := `INSERT INTO boarding_passes (ticket_no, flight_id, boarding_no, seat_no)
				VALUES ($1, $2, $3, $4) RETURNING ticket_no, flight_id, boarding_no, seat_no`

	var boardingPass model.BoardingPass
	err := r.db.Get(&boardingPass, query, ticketNo, flightId, boardingNo, seatNo)
	if err != nil {
		return model.BoardingPass{}, err
	}
	return boardingPass, nil
}
