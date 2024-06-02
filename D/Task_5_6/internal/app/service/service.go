package service

import (
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
)

type Airport interface {
}

type City interface {
	GetAllCities(limit, page string) ([]model.City, error)
}

type Create interface {
}

type Flight interface {
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
