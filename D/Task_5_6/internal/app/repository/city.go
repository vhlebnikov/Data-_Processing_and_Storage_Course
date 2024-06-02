package repository

import (
	"github.com/jmoiron/sqlx"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
)

type CityPostgres struct {
	db *sqlx.DB
}

func NewCityPostgres(db *sqlx.DB) *CityPostgres {
	return &CityPostgres{db: db}
}

func (r *CityPostgres) GetAllCities(limit, offset int) ([]model.City, error) {
	query := `SELECT DISTINCT(city) FROM airports LIMIT $1 OFFSET $2`

	var cities []model.City

	err := r.db.Select(&cities, query, limit, offset)
	if err != nil {
		return nil, err
	}
	return cities, nil
}
