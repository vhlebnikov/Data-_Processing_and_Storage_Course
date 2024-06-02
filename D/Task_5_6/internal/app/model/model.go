package model

import (
	"errors"
	"fmt"
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

type AirportDB struct {
	AirportCode string  `db:"airport_code"`
	AirportName string  `db:"airport_name"`
	City        string  `db:"city"`
	Coordinates []uint8 `db:"coordinates"`
	Timezone    string  `db:"timezone"`
}

type ScheduleFlight struct {
	FlightNo      string `json:"flightNo" db:"flight_no"`
	ArrivalTime   string `json:"arrivalTime" db:"arrival_time"`
	DepartureTime string `json:"departureTime" db:"departure_time"`
	DaysOfWeek    []int  `json:"daysOfWeek" db:"dow"`
	AirportName   string `json:"airportName" db:"airport_name"`
}
