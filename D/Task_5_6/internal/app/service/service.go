package service

import (
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
)

type Airport interface {
	GetAirports(limit, page, city string) (int, []model.Airport, error)
}

type City interface {
	GetAllCities(limit, page string) (int, []model.City, error)
}

type Create interface {
}

type Flight interface {
	GetSchedule(limit, page, direction, airportCode string) (int, []model.ScheduleFlight, error)
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
