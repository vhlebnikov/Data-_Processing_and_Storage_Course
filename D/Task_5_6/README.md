# Airlines API

Swagger: [```localhost:8080/swagger```](localhost:8080/swagger)
or [```localhost:8080/swagger/index.html```](localhost:8080/swagger/index.html)

## Regenerate Swagger docs

Do it using:
- ```swag fmt -g ./cmd/app/main.go``` - to format **swag** annotations
- ```swag init -g ./cmd/app/main.go``` - to generate **swag** necessary files
- ```go run ./cmd/app/main.go``` - **RUN SERVER!!!**