package repository

import (
	"database/sql"
	"errors"
	"github.com/jmoiron/sqlx"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
)

type AirportPostgres struct {
	db *sqlx.DB
}

func NewAirportPostgres(db *sqlx.DB) *AirportPostgres {
	return &AirportPostgres{db: db}
}

func (r *AirportPostgres) GetAirports(limit, offset int, city string) (int, []model.Airport, error) {
	queryCount := `SELECT COUNT(*) FROM airports`
	query := `SELECT * FROM airports`

	var count int
	var airports []model.Airport
	var err error

	tx, err := r.db.Beginx()
	if err != nil {
		return 0, nil, err
	}
	defer tx.Rollback()

	if city != "" {
		queryCount += ` WHERE city=$1`
		query += ` WHERE city=$1 ORDER BY airport_name LIMIT $2 OFFSET $3`
		err = tx.Select(&airports, query, city, limit, offset)
	} else {
		query += ` ORDER BY airport_name LIMIT $1 OFFSET $2`
		err = tx.Select(&airports, query, limit, offset)
	}

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return 0, nil, nil
		}
		return 0, nil, err
	}

	if city != "" {
		err = tx.Get(&count, queryCount, city)
	} else {
		err = tx.Get(&count, queryCount)
	}

	if err != nil {
		return 0, nil, err
	}
	if err = tx.Commit(); err != nil {
		return 0, nil, err
	}

	return count, airports, nil
}

func (r *AirportPostgres) SetLanguage(language string) error {
	query := `SELECT set_config('bookings.lang', $1, false)`
	_, err := r.db.Exec(query, language)
	return err
}
