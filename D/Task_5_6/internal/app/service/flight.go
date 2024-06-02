package service

import "github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"

type FlightService struct {
	repo repository.Flight
}

func NewFlightService(repo repository.Flight) *FlightService {
	return &FlightService{repo: repo}
}
