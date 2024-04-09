.PHONY: migrationUp
migrationUp:
	migrate -path ./migrations -database 'postgres://postgres:31032003@localhost:5432/postgres?sslmode=disable' up

.PHONY: migrationDown
migrationDown:
	migrate -path ./migrations -database 'postgres://postgres:31032003@localhost:5432/postgres?sslmode=disable' down