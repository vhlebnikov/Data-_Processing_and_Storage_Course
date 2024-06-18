package service

import (
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"strconv"
)

type CityService struct {
	repo repository.City
}

func NewCityService(repo repository.City) *CityService {
	return &CityService{repo: repo}
}

func (s *CityService) GetAllCities(limit, page string) (int, []model.City, error) {
	limitInt, err := strconv.Atoi(limit)
	if err != nil {
		return 0, nil, err
	}

	pageInt, err := strconv.Atoi(page)
	if err != nil {
		return 0, nil, err
	}

	offset := limitInt * pageInt

	return s.repo.GetAllCities(limitInt, offset)
}

func (s *CityService) SetLanguage(language string) error {
	return s.repo.SetLanguage(language)
}
