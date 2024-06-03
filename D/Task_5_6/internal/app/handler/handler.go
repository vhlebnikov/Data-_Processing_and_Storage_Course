package handler

import (
	"github.com/gin-gonic/gin"
	"github.com/swaggo/files"
	"github.com/swaggo/gin-swagger"
	_ "github.com/vhlebnikov/Data_Processing_and_Storage_Course/docs"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/service"
	"net/http"
)

type Handler struct {
	services *service.Service
}

func NewHandler(services *service.Service) *Handler {
	return &Handler{services: services}
}

func (h *Handler) InitRoutes() *gin.Engine {
	gin.SetMode(gin.ReleaseMode)
	router := gin.New()

	router.Use(gin.Recovery())

	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	router.GET("/swagger", func(c *gin.Context) {
		c.Header("Cache-Control", "no-cache, no-store")
		c.Redirect(http.StatusMovedPermanently, "/swagger/index.html")
	})

	api := router.Group("")
	{
		api.GET("/cities", h.GetCities)
		api.GET("/airports", h.GetAirports)
		api.GET("/schedule", h.GetSchedule)
		api.GET("/routes", h.GetRoute)
		api.POST("/booking", h.CreateBooking)
		api.POST("/check-in", h.CheckIn)
	}

	return router
}
