package model

import (
	"database/sql/driver"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/lib/pq"
	"time"
)

type Response struct {
	Message string `json:"message" example:"message"`
	Payload any    `json:"payload" swaggertype:"string" example:"null"`
}

type City struct {
	City string `json:"city" db:"city" example:"Братск"`
}

type Point struct {
	Longitude float64 `json:"longitude" db:"longitude" example:"129.77099609375"`
	Latitude  float64 `json:"latitude" db:"latitude" example:"62.093299865722656"`
}

func (p *Point) Scan(value interface{}) error {
	var s string
	switch v := value.(type) {
	case []byte:
		s = string(v)
	case string:
		s = v
	default:
		return errors.New("unsupported type: " + fmt.Sprintf("%T", v))
	}

	_, err := fmt.Sscanf(s, "(%f,%f)", &p.Longitude, &p.Latitude)
	if err != nil {
		return err
	}
	return nil
}

type Airport struct {
	AirportCode string `json:"code" db:"airport_code" example:"YKS"`
	AirportName string `json:"name" db:"airport_name" example:"Якутск"`
	City        string `json:"city" db:"city" example:"Якутск"`
	Coordinates Point  `json:"coordinates" db:"coordinates"`
	Timezone    string `json:"timezone" db:"timezone" example:"Asia/Yakutsk"`
}

type ScheduleFlight struct {
	FlightNo      string        `json:"flightNo" db:"flight_no" example:"PG0013"`
	ArrivalTime   string        `json:"arrivalTime,omitempty" db:"arrival_time" example:"0000-01-01T17:00:00Z"`
	DepartureTime string        `json:"departureTime,omitempty" db:"departure_time" example:"0000-01-01T17:00:00Z"`
	DaysOfWeek    pq.Int64Array `json:"daysOfWeek" db:"dow" swaggertype:"array,number" example:"1,2,3"`
	AirportName   string        `json:"airportName" db:"airport_name" example:"Сочи"`
}

type Route struct {
	AirportPath        pq.StringArray `json:"airportPath" db:"airport_path" swaggertype:"array,string" example:"SVO,ROV,DME"`
	FlightNoPath       pq.StringArray `json:"flightNoPath" db:"flight_no_path" swaggertype:"array,string" example:"PG0317,PG0215"`
	FlightIdPath       pq.Int64Array  `json:"flightIdPath" db:"flight_id_path" swaggertype:"array,number" example:"5445,12916"`
	Step               int            `json:"step" db:"step" example:"2"`
	ScheduledDeparture time.Time      `json:"scheduledDeparture" db:"scheduled_departure" swaggertype:"string" example:"2017-09-10T13:45:00Z"`
	ScheduledArrival   time.Time      `json:"scheduledArrival" db:"scheduled_arrival" swaggertype:"string" example:"2017-09-10T16:30:00Z"`
}

type Booking struct {
	BookRef     string   `json:"bookRef" db:"book_ref" example:"_67429"`
	TotalAmount float64  `json:"totalAmount" db:"total_amount" example:"7700"`
	Tickets     []Ticket `json:"tickets"`
}

type Ticket struct {
	TicketNo       string  `json:"ticketNo" db:"ticket_no" example:"_086b048c8144"`
	FareConditions string  `json:"fareConditions" db:"fare_conditions" example:"Economy"`
	Amount         float64 `json:"amount" db:"amount" example:"7700"`
	FlightId       int     `json:"flightId" db:"flight_id" example:"7343"`
}

type FlightPrice struct {
	FlightId int     `db:"flight_id"`
	Amount   float64 `db:"amount"`
}

type JSON map[string]any

func (j JSON) Value() (driver.Value, error) {
	return json.Marshal(j)
}

func (j JSON) Scan(value interface{}) error {
	b, ok := value.([]byte)
	if !ok {
		return errors.New("type assertion to []byte failed")
	}
	return json.Unmarshal(b, &j)
}

type BoardingPass struct {
	BoardingNo int    `json:"boardingNo" db:"boarding_no" example:"376"`
	SeatNo     string `json:"seatNo" db:"seat_no" example:"11F"`
	TicketNo   string `json:"ticketNo" db:"ticket_no" example:"_086b048c8144"`
	FlightId   int    `json:"flightId" db:"flight_id" example:"7343"`
}
