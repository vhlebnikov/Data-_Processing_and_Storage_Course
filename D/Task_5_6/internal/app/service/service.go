package service

import (
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"time"
)

type Airport interface {
	GetAirports(limit, page, city string) (int, []model.Airport, error)
}

type City interface {
	GetAllCities(limit, page string) (int, []model.City, error)
}

type Create interface {
	CreateBooking(bookDate time.Time, passengerName, fareConditions string, flightIds []int, contactData model.JSON) (model.Booking, error)
	CheckIn(ticketNo string, flightId int) (model.BoardingPass, error)
}

type Flight interface {
	GetSchedule(limit, page, direction, airportCode string) (int, []model.ScheduleFlight, error)
	GetRoutes(limit, page, origin, destination, fareConditions string, departureDate time.Time, stepLimit int) (int, []model.Route, error)
}

type Service struct {
	Airport
	City
	Create
	Flight
}

func NewService(repos *repository.Repository) *Service {
	return &Service{
		Airport: NewAirportService(repos.Airport),
		City:    NewCityService(repos.City),
		Create:  NewCreateService(repos.Create),
		Flight:  NewFlightService(repos.Flight),
	}
}
