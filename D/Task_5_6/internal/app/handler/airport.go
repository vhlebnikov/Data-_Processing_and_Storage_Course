package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
)

// GetAirports Handler to get all available airports or in certain city
//
//	@Summary		Get all available airports or in certain city
//	@Description	Supports pagination (limit, page params)
//	@Description	If you want to get airports in certain city pass 'city' parameter
//	@Tags			airports
//	@Produce		json
//	@Param			lang	query		string	false	"language of data to receive, default='ru'"			Enums(en, ru)	example(ru)
//	@Param			city	query		string	false	"city where airports are located"					example("Якутск")
//	@Param			limit	query		int		false	"limit of received data, default=30"				example(10)
//	@Param			page	query		int		false	"page of data that you want to receive, default=0"	example(2)
//	@Success		200		{object}	model.AirportsResponse
//	@Failure		400,500	{object}	model.Response
//	@Router			/airports [get]
func (h *Handler) GetAirports(c *gin.Context) {
	lang := c.Query("lang")
	if lang == "" {
		lang = "ru"
	}
	if lang != "ru" && lang != "en" {
		myerr.New(c, http.StatusBadRequest, "unknown 'lang' value")
		return
	}
	if err := h.services.City.SetLanguage(lang); err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	city := c.Query("city")
	limit := c.Query("limit")
	if limit == "" {
		limit = "30"
	}
	page := c.Query("page")
	if page == "" {
		page = "0"
	}

	count, airports, err := h.services.Airport.GetAirports(limit, page, city)
	if err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

	c.JSON(http.StatusOK, model.Response{
		Message: "ok",
		Payload: gin.H{
			"count": count,
			"rows":  airports,
		},
	})
}
