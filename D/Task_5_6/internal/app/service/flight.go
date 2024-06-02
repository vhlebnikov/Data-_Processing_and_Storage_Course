package service

import (
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"strconv"
)

type FlightService struct {
	repo repository.Flight
}

func NewFlightService(repo repository.Flight) *FlightService {
	return &FlightService{repo: repo}
}

func (s *FlightService) GetSchedule(limit, page, direction, airportCode string) (int, []model.ScheduleFlight, error) {
	limitInt, err := strconv.Atoi(limit)
	if err != nil {
		return 0, nil, err
	}
	pageInt, err := strconv.Atoi(page)
	if err != nil {
		return 0, nil, err
	}
	offset := limitInt * pageInt

}
