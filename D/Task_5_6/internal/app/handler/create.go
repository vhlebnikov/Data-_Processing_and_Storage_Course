package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
	"time"
)

// CreateBooking Handler to Create a booking for a selected route for a single passenger
//
//	@Summary		Create a booking for a selected route for a single passenger
//	@Description	Actually, contact data is optional
//	@Tags			booking
//	@Accept			json
//	@Produce		json
//	@Param			request	body		handler.CreateBooking.Request	true	"request body"
//	@Success		200		{object}	model.CreateBookingResponse
//	@Failure		400,500	{object}	model.Response
//	@Router			/booking [post]
func (h *Handler) CreateBooking(c *gin.Context) {
	type Request struct {
		BookDate       string `json:"bookDate" binding:"required" example:"2017-09-10"`
		PassengerName  string `json:"passengerName" binding:"required" example:"KHLEBNIKOV VADIM"`
		ContactData    gin.H  `json:"contactData" swaggertype:"object,string" example:"email:myemail@gmail.com,phone:+79123456789"`
		FareConditions string `json:"fareConditions" binding:"required" example:"Economy"`
		FlightIds      []int  `json:"flightIds" binding:"required" swaggertype:"array,number" example:"7343,28480"`
	}

	var request Request

	if err := c.ShouldBindJSON(&request); err != nil {
		myerr.New(c, http.StatusBadRequest, err.Error())
		return
	}

	if request.FareConditions != "Economy" && request.FareConditions != "Comfort" &&
		request.FareConditions != "Business" {
		myerr.New(c, http.StatusBadRequest, "unknown 'fareCondition' value")
		return
	}

	bookDateTime, err := time.Parse(time.DateOnly, request.BookDate)
	if err != nil {
		myerr.New(c, http.StatusBadRequest, err.Error())
		return
	}

	booking, err := h.services.Create.CreateBooking(bookDateTime, request.PassengerName, request.FareConditions,
		request.FlightIds, model.JSON(request.ContactData))
	if err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	c.JSON(http.StatusOK, model.Response{
		Message: "ok",
		Payload: booking,
	})

}

// CheckIn Стоит сказать, что оказывается один ticket_no может быть связан с несколькими
// flight_id, именно по этой причине мне нужно передавать оба этих значения
//
//	@Summary		Online check-in for a flight
//	@Description	Pass ticketNo and flightId to get your boarding pass
//	@Tags			check-in
//	@Accept			json
//	@Produce		json
//	@Param			request	body		handler.CheckIn.Request	true	"request body"
//	@Success		200		{object}	model.CheckInResponse
//	@Failure		400,500	{object}	model.Response
//	@Router			/check-in [post]
func (h *Handler) CheckIn(c *gin.Context) {
	type Request struct {
		TicketNo string `json:"ticketNo" binding:"required" example:"_086b048c8144"`
		FlightId int    `json:"flightId" binding:"required" example:"7343"`
	}

	var request Request

	if err := c.ShouldBindJSON(&request); err != nil {
		myerr.New(c, http.StatusBadRequest, err.Error())
		return
	}

	boardingPass, err := h.services.Create.CheckIn(request.TicketNo, request.FlightId)
	if err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	c.JSON(http.StatusOK, model.Response{
		Message: "ok",
		Payload: boardingPass,
	})
}
