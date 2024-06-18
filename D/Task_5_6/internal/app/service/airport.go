package service

import (
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"strconv"
)

type AirportService struct {
	repo repository.Airport
}

func NewAirportService(repo repository.Airport) *AirportService {
	return &AirportService{repo: repo}
}

func (s *AirportService) GetAirports(limit, page, city string) (int, []model.Airport, error) {
	limitInt, err := strconv.Atoi(limit)
	if err != nil {
		return 0, nil, err
	}

	pageInt, err := strconv.Atoi(page)
	if err != nil {
		return 0, nil, err
	}
	offset := limitInt * pageInt

	count, airports, err := s.repo.GetAirports(limitInt, offset, city)
	if err != nil {
		return 0, nil, err
	}

	return count, airports, nil
}

func (s *AirportService) SetLanguage(language string) error {
	return s.repo.SetLanguage(language)
}
