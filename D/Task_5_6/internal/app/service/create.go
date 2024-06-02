package service

import "github.com/vhlebnikov/Data_Processing_and_Storage_Course/internal/app/repository"

type CreateService struct {
	repo repository.Create
}

func NewCreateService(repo repository.Create) *CreateService {
	return &CreateService{repo: repo}
}
