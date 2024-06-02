package repository

import (
	"github.com/jmoiron/sqlx"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
)

type Airport interface {
	GetAirports(limit, offset int, city string) (int, []model.Airport, error)
}

type City interface {
	GetAllCities(limit, offset int) (int, []model.City, error)
}

type Create interface {
}

type Flight interface {
	GetSchedule(limit, offset int, direction, airportCode string) (int, []model.ScheduleFlight, error)
}

type Repository struct {
	Airport
	City
	Create
	Flight
}

func NewRepository(db *sqlx.DB) *Repository {
	return &Repository{
		Airport: NewAirportPostgres(db),
		City:    NewCityPostgres(db),
		Create:  NewCreatePostgres(db),
		Flight:  NewFlightPostgres(db),
	}
}
