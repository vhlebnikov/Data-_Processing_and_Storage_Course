package handler

import "github.com/gin-gonic/gin"

func (h *Handler) GetAirports(c *gin.Context) {
	city := c.Query("city")

}
