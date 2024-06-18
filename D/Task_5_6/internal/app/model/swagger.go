package model

type CitiesResponse struct {
	Message string `json:"message" example:"ok"`
	Payload struct {
		Count int    `json:"count" example:"10"`
		Rows  []City `json:"rows"`
	}
}

type AirportsResponse struct {
	Message string `json:"message" example:"ok"`
	Payload struct {
		Count int       `json:"count" example:"10"`
		Rows  []Airport `json:"rows"`
	}
}

type ScheduleResponse struct {
	Message string `json:"message" example:"ok"`
	Payload struct {
		Count int              `json:"count" example:"10"`
		Rows  []ScheduleFlight `json:"rows"`
	}
}

type RoutesResponse struct {
	Message string `json:"message" example:"ok"`
	Payload struct {
		Count int     `json:"count" example:"10"`
		Rows  []Route `json:"rows"`
	}
}

type CreateBookingResponse struct {
	Message string `json:"message" example:"ok"`
	Payload struct {
		Booking
	}
}

type CheckInResponse struct {
	Message string `json:"message" example:"ok"`
	Payload struct {
		BoardingPass
	}
}
