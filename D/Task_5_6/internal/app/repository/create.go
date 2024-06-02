package repository

import "github.com/jmoiron/sqlx"

type CreatePostgres struct {
	db *sqlx.DB
}

func NewCreatePostgres(db *sqlx.DB) *CreatePostgres {
	return &CreatePostgres{db: db}
}
