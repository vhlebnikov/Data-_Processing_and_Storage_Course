package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
	"strconv"
	"time"
)

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

	fareCondition := c.Query("fareCondition")
	if fareCondition == "" {
		myerr.New(c, http.StatusBadRequest, "empty 'fareCondition' query parameter")
		return
	}
	if fareCondition != "Economy" && fareCondition != "Comfort" && fareCondition != "Business" {
		myerr.New(c, http.StatusBadRequest, "unknown 'fareCondition' value")
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
		fareCondition, departureDate, stepLimit)
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
