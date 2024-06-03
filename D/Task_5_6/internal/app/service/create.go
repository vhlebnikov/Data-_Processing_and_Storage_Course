package service

import (
	"github.com/google/uuid"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"strings"
	"time"
)

type CreateService struct {
	repo repository.Create
}

func NewCreateService(repo repository.Create) *CreateService {
	return &CreateService{repo: repo}
}

func (s *CreateService) CreateBooking(bookDate time.Time, passengerName, fareCondition string, flightIds []int,
	contactData model.JSON) (model.Booking, error) {

	bookRef := "_" + uuid.New().String()[1:6]
	ticketsNo := make([]string, 0, len(flightIds))
	passengerIds := make([]string, 0, len(flightIds))
	for range flightIds {
		ticketsNo = append(ticketsNo, "_"+strings.Replace(uuid.New().String(), "-", "", -1)[1:13])
		passengerIds = append(passengerIds, "_"+strings.Replace(uuid.New().String(), "-", "", -1)[1:20])
	}

	ticketsPrices, err := s.repo.GetFlightsPrices(flightIds, fareCondition)
	if err != nil {
		return model.Booking{}, err
	}
	var totalAmount float64 = 0
	for _, price := range ticketsPrices {
		totalAmount += price.Amount
	}

	if err = s.repo.CreateBooking(bookDate, totalAmount, bookRef); err != nil {
		return model.Booking{}, err
	}

	tickets, err := s.repo.CreateTickets(ticketsPrices, bookRef, fareCondition, passengerName,
		passengerIds, ticketsNo, contactData)
	if err != nil {
		return model.Booking{}, err
	}

	return model.Booking{
		BookRef:     bookRef,
		TotalAmount: totalAmount,
		Tickets:     tickets,
	}, nil
}
