package main

import (
	"context"
	"errors"
	"github.com/joho/godotenv"
	_ "github.com/lib/pq"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/handler"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/server"
	"github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/service"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
)

//	@title			Airlines Service
//	@version		1.0
//	@description	API Server for Airlines Service (D_6 task for DPaS course)

//	@host		localhost:8080
//	@BasePath	/

func main() {
	if err := godotenv.Load(); err != nil {
		log.Fatalf("Error loading .env file: %v\n", err.Error())
	}

	db, err := repository.NewPostgresDB(repository.DBConfig{
		Host:     os.Getenv("DB_HOST"),
		Port:     os.Getenv("DB_PORT"),
		User:     os.Getenv("DB_USER"),
		Password: os.Getenv("DB_PASSWORD"),
		Database: os.Getenv("DB_NAME"),
		SSLMode:  "disable",
	})
	if err != nil {
		log.Fatalf("Error connecting to DB: %v\n", err.Error())
	}

	repos := repository.NewRepository(db)
	services := service.NewService(repos)
	handlers := handler.NewHandler(services)

	srv := &server.Server{}
	bindAddr := os.Getenv("BIND_ADDR")

	go func() {
		if err = srv.Run(bindAddr, handlers.InitRoutes()); !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("Error while starting server: %v\n", err.Error())
		}
		log.Printf("Server was shutted down\n")
	}()

	log.Printf("Server started on port: %v\n", bindAddr)

	quitSignal := make(chan os.Signal, 1)
	signal.Notify(quitSignal, syscall.SIGINT, syscall.SIGTERM)
	<-quitSignal

	if err = srv.Shutdown(context.Background()); err != nil {
		log.Fatalf("Error while shutting down server: %v\n", err.Error())
	}
	if err = db.Close(); err != nil {
		log.Fatalf("Error while closing DB: %v\n", err.Error())
	}
}
