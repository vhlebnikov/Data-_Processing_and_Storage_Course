package model

import (
	"errors"
	"fmt"
	"github.com/lib/pq"
	"time"
)

type Response struct {
	Message string `json:"message"`
	Payload any    `json:"payload"`
}

type City struct {
	City string `json:"city" db:"city"`
}

type Point struct {
	Longitude float64 `json:"longitude" db:"longitude"`
	Latitude  float64 `json:"latitude" db:"latitude"`
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
	AirportCode string `json:"code" db:"airport_code"`
	AirportName string `json:"name" db:"airport_name"`
	City        string `json:"city" db:"city"`
	Coordinates Point  `json:"coordinates" db:"coordinates"`
	Timezone    string `json:"timezone" db:"timezone"`
}

type ScheduleFlight struct {
	FlightNo      string        `json:"flightNo" db:"flight_no"`
	ArrivalTime   string        `json:"arrivalTime,omitempty" db:"arrival_time"`
	DepartureTime string        `json:"departureTime,omitempty" db:"departure_time"`
	DaysOfWeek    pq.Int64Array `json:"daysOfWeek" db:"dow"`
	AirportName   string        `json:"airportName" db:"airport_name"`
}

type Route struct {
	AirportPath        pq.StringArray `json:"airportPath" db:"airport_path"`
	FlightNoPath       pq.StringArray `json:"flightNoPath" db:"flight_no_path"`
	Step               int            `json:"step" db:"step"`
	ScheduledDeparture time.Time      `json:"scheduledDeparture" db:"scheduled_departure"`
	ScheduledArrival   time.Time      `json:"scheduledArrival" db:"scheduled_arrival"`
}
