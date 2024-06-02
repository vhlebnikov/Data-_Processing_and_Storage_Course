package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/service"
)

type Handler struct {
	services *service.Service
}

func NewHandler(services *service.Service) *Handler {
	return &Handler{services: services}
}

func (h *Handler) InitRoutes() *gin.Engine {
	router := gin.New()

	router.Use(gin.Recovery())

	api := router.Group("")
	{
		api.GET("/cities", h.GetCities)
		api.GET("/airports", h.GetAirports) // if city was passed then -> search within city
		api.GET("/schedule", h.GetSchedule) // ?direction=inbound || outbound
		api.GET("/routes", h.GetRoute)
		api.POST("/booking", h.CreateBooking)
		api.POST("/check-in", h.CheckIn)
	}

	return router
}
