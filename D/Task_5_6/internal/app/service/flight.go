package service

import (
	"errors"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"regexp"
	"strconv"
	"time"
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

	return s.repo.GetSchedule(limitInt, offset, direction, airportCode)
}

func (s *FlightService) GetRoutes(limit, page, origin, destination, fareCondition string,
	departureDate time.Time, stepLimit int) (int, []model.Route, error) {
	limitInt, err := strconv.Atoi(limit)
	if err != nil {
		return 0, nil, err
	}
	pageInt, err := strconv.Atoi(page)
	if err != nil {
		return 0, nil, err
	}
	offset := limitInt * pageInt

	regularExpression := `\b[A-Z]{3}\b`

	realOrigin := origin
	if ok, _ := regexp.Match(regularExpression, []byte(origin)); !ok {
		realOrigin, err = s.repo.GetAirportCodeFromCity(origin)
		if err != nil {
			return 0, nil, errors.New("Can't get city for 'origin': " + err.Error())
		}
	}

	realDestination := destination
	if ok, _ := regexp.Match(regularExpression, []byte(destination)); !ok {
		realDestination, err = s.repo.GetAirportCodeFromCity(destination)
		if err != nil {
			return 0, nil, errors.New("Can't get city for 'destination': " + err.Error())
		}
	}

	return s.repo.GetRoutes(limitInt, offset, stepLimit, realOrigin, realDestination, fareCondition, departureDate)
}
