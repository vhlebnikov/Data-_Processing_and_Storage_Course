package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
	"strconv"
	"time"
)

// GetSchedule Handler to get schedule for certain airport
//
//	@Summary		Get schedule for certain airport
//	@Description	Supports pagination (limit, page params)
//	@Description	Choose direction using 'direction' parameter
//	@Description	Choose airport passing 'airportCode' parameter
//	@Tags			flights
//	@Produce		json
//	@Param			direction	query		string	true	"direction"											example("inbound")	Enums(inbound, outbound)
//	@Param			airportCode	query		string	true	"airport code"										example("SVO")
//	@Param			limit		query		int		false	"limit of received data, default=30"				example(10)
//	@Param			page		query		int		false	"page of data that you want to receive, default=0"	example(3)
//	@Success		200			{object}	model.ScheduleResponse
//	@Failure		400,500		{object}	model.Response
//	@Router			/schedule [get]
func (h *Handler) GetSchedule(c *gin.Context) {
	direction := c.Query("direction")
	if direction == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'direction' query parameter")
		return
	}
	if direction != "inbound" && direction != "outbound" {
		myerr.New(c, http.StatusBadRequest, "unknown 'direction' value")
		return
	}

	airportCode := c.Query("airportCode")
	if airportCode == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'airportCode' query parameter")
		return
	}

	limit := c.Query("limit")
	if limit == "" {
		limit = "30"
	}
	page := c.Query("page")
	if page == "" {
		page = "0"
	}

	count, flights, err := h.services.Flight.GetSchedule(limit, page, direction, airportCode)
	if err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	c.JSON(http.StatusOK, model.Response{
		Message: "ok",
		Payload: gin.H{
			"count": count,
			"rows":  flights,
		},
	})
}

// GetRoute Handler to get routes connecting two points
//
//	@Summary		Get routes connecting two points (airports or cities) in some date (between date and tomorrow's date)
//	@Description	Supports pagination (limit, page params)
//	@Description	You can limit the number of transfers using 'stepLimit' parameter, by default it will set to 'unbound'
//	@Description	Choose origin and destination points which can be an airport or a city
//	@Description	Choose fare conditions passing 'fareConditions' parameter
//	@Description	Choose departure date passing 'departureDate' parameter
//	@Tags			flights
//	@Produce		json
//	@Param			origin			query		string	true	"origin airport or city"							example("SVO")
//	@Param			destination		query		string	true	"destination airport or city"						example("DME")
//	@Param			fareConditions	query		string	true	"direction"											example("Economy")	Enums(Economy, Comfort, Business)
//	@Param			departureDate	query		string	true	"direction"											example("2017-09-10")
//	@Param			stepLimit		query		string	false	"limit the number of transfers, default=unbound"	example("1")
//	@Param			limit			query		int		false	"limit of received data, default=30"				example(10)
//	@Param			page			query		int		false	"page of data that you want to receive, default=0"	example(3)
//	@Success		200				{object}	model.RoutesResponse
//	@Failure		400,500			{object}	model.Response
//	@Router			/routes [get]
func (h *Handler) GetRoute(c *gin.Context) {
	origin := c.Query("origin")
	if origin == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'origin' query parameter")
		return
	}

	destination := c.Query("destination")
	if destination == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'destination' query parameter")
		return
	}

	fareConditions := c.Query("fareConditions")
	if fareConditions == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'fareConditions' query parameter")
		return
	}
	if fareConditions != "Economy" && fareConditions != "Comfort" && fareConditions != "Business" {
		myerr.New(c, http.StatusBadRequest, "unknown 'fareConditions' value")
		return
	}

	departureDateQuery := c.Query("departureDate")
	if departureDateQuery == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'departureDate' query parameter")
		return
	}
	departureDate, err := time.Parse(time.DateOnly, departureDateQuery)
	if err != nil {
		myerr.New(c, http.StatusBadRequest, "invalid 'departureDate' query parameter: "+err.Error())
		return
	}

	limit := c.Query("limit")
	if limit == "" {
		limit = "10"
	}
	page := c.Query("page")
	if page == "" {
		page = "0"
	}

	stepLimitQuery := c.Query("stepLimit")
	if stepLimitQuery == "" {
		stepLimitQuery = "unbound"
	}
	var stepLimit int
	if stepLimitQuery != "unbound" {
		stepLimit, err = strconv.Atoi(stepLimitQuery)
		if err != nil {
			myerr.New(c, http.StatusBadRequest, err.Error())
			return
		}
	} else {
		stepLimit = -1
	}

	count, rows, err := h.services.Flight.GetRoutes(limit, page, origin, destination,
		fareConditions, departureDate, stepLimit)
	if err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	c.JSON(http.StatusOK, model.Response{
		Message: "ok",
		Payload: gin.H{
			"count": count,
			"rows":  rows,
		},
	})
}
