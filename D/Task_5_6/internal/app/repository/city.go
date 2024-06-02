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

func (r *CityPostgres) GetAllCities(limit, offset int) (int, []model.City, error) {
	queryCount := `SELECT COUNT(*) FROM (SELECT DISTINCT(city) FROM airports)`
	query := `SELECT DISTINCT(city) FROM airports ORDER BY city LIMIT $1 OFFSET $2`

	var count int
	var cities []model.City

	tx, err := r.db.Beginx()
	if err != nil {
		return 0, nil, err
	}
	defer tx.Rollback()

	if err = tx.Get(&count, queryCount); err != nil {
		return 0, nil, err
	}

	if err = tx.Select(&cities, query, limit, offset); err != nil {
		return 0, nil, err
	}

	if err = tx.Commit(); err != nil {
		return 0, nil, err
	}

	return count, cities, nil
}
