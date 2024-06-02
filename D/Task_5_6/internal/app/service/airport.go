package service

import "github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"

type AirportService struct {
	repo repository.Airport
}

func NewAirportService(repo repository.Airport) *AirportService {
	return &AirportService{repo: repo}
}
