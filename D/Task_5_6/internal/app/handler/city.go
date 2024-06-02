package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
)

func (h *Handler) GetCities(c *gin.Context) {
	limit := c.Query("limit")
	if limit == "" {
		limit = "120"
	}
	page := c.Query("page")
	if page == "" {
		page = "0"
	}

	count, cities, err := h.services.City.GetAllCities(limit, page)
	if err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	c.JSON(http.StatusOK, model.Response{
		Message: "ok",
		Payload: gin.H{
			"count": count,
			"rows":  cities,
		},
	})
}
