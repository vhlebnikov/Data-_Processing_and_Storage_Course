package myerr

import (
	"github.com/gin-gonic/gin"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/model"
	"log"
)

func New(c *gin.Context, statusCode int, message string) {
	log.Println("method: ", c.Request.Method, ", url: ", c.Request.URL,
		", statusCode: ", statusCode, ", msg: ", message)

	c.AbortWithStatusJSON(statusCode, model.Response{
		Message: message,
		Payload: nil,
	})
}
