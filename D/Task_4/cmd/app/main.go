package main

import (
	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
	"log"
	"os"
)

type FlightPrice struct {
	FlightNo       string `db:"flight_no"`
	DayOfWeek      string `db:"day_of_week"`
	FareConditions string `db:"fare_conditions"`
	AvgPrice       string `db:"avg"`
	MinPrice       string `db:"min"`
	MaxPrice       string `db:"max"`
}

func main() {
	db, err := sqlx.Open("postgres",
		"host=localhost port=7899 user=postgres dbname=demo password=12345 sslmode=disable")
	if err != nil {
		log.Fatalf("Can't create coonection to database: %v", err.Error())
	}
	defer db.Close()

	if err = db.Ping(); err != nil {
		log.Fatalf("Can't connect to database: %v", err.Error())
	}

	c, err := os.ReadFile("./query/query.sql")
	if err != nil {
		log.Fatalf("Can't read query from file: %v", err.Error())
	}
	queryToGetData := string(c)

	tx, err := db.Beginx()
	if err != nil {
		log.Fatalf("Can't start transaction: %v", err.Error())
	}
	defer tx.Rollback()

	var flightPrices []FlightPrice
	err = tx.Select(&flightPrices, queryToGetData)
	if err != nil {
		log.Fatalf("Can't exec query: %v", err.Error())
	}

	queryCreateTable := `CREATE TABLE IF NOT EXISTS pricing_rule_table (
							id SERIAL PRIMARY KEY,
							flight_no CHAR(6) NOT NULL,
							day_of_week VARCHAR(10) NOT NULL,
							fare_conditions VARCHAR(10) NOT NULL,
							avg NUMERIC(10, 2) NOT NULL,
							min NUMERIC(10, 2) NOT NULL,
							max NUMERIC(10, 2) NOT NULL
						 )`

	_, err = tx.Exec(queryCreateTable)
	if err != nil {
		log.Fatalf("Can't create table: %v", err.Error())
	}

	queryInsert := `INSERT INTO pricing_rule_table (flight_no, day_of_week, fare_conditions, avg, min, max)
					VALUES ($1, $2, $3, $4, $5, $6)`

	stmt, err := tx.Preparex(queryInsert)
	if err != nil {
		log.Fatalf("Can't create statement: %v", err.Error())
	}
	defer stmt.Close()

	for _, row := range flightPrices {
		_, err = stmt.Exec(row.FlightNo, row.DayOfWeek, row.FareConditions, row.AvgPrice, row.MinPrice, row.MaxPrice)
	}

	err = tx.Commit()
	if err != nil {
		log.Fatalf("Can't commit transaction: %v", err.Error())
	}
}
