package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
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

}

func (h *Handler) GetRoute(c *gin.Context) {

}
