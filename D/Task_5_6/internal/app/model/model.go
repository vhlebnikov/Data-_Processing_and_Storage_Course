package model

type City struct {
	City string `json:"city" db:"city"`
}

type Response struct {
	Message string `json:"message"`
	Payload any    `json:"payload"`
}
