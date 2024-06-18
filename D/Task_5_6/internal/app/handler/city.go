package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/myerr"
	"net/http"
)

// GetCities Handler to get all available cities
//
//	@Summary		Get all available cities
//	@Description	Supports pagination (limit, page params)
//	@Tags			cities
//	@Produce		json
//	@Param			lang	query		string	false	"language of data to receive, default='ru'"			Enums(en, ru)	example(ru)
//	@Param			limit	query		int		false	"limit of received data, default=120"				example(10)
//	@Param			page	query		int		false	"page of data that you want to receive, default=0"	example(2)
//	@Success		200		{object}	model.CitiesResponse
//	@Failure		400,500	{object}	model.Response
//	@Router			/cities [get]
func (h *Handler) GetCities(c *gin.Context) {
	lang := c.Query("lang")
	if lang == "" {
		lang = "ru"
	}
	if lang != "ru" && lang != "en" {
		myerr.New(c, http.StatusBadRequest, "unknown 'lang' value")
		return
	}
	if err := h.services.Airport.SetLanguage(lang); err != nil {
		myerr.New(c, http.StatusInternalServerError, err.Error())
		return
	}

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
