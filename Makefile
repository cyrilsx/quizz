.PHONY: help build up down logs clean reset

help:
	@echo "Quizz Platform - local environment"
	@echo "  make build  - build the backend image with Jib (requires Docker + JDK 25 + Maven)"
	@echo "  make up     - build if needed, then start all services (db, redis, backend, frontend)"
	@echo "  make down   - stop all services"
	@echo "  make logs   - tail logs of all services"
	@echo "  make reset  - stop services and wipe the database volume"
	@echo ""
	@echo "Required environment variables (put them in a .env file next to this Makefile):"
	@echo "  JWT_SECRET       - secret for JWT signing (min 32 chars, required)"
	@echo "  MISTRAL_API_KEY  - optional, enables AI question generation"
	@echo ""
	@echo "URLs once started:"
	@echo "  Frontend:  http://localhost:8081"
	@echo "  Backend:    http://localhost:8080/api (Swagger: http://localhost:8080/swagger-ui.html)"

build:
	mvn -f backend/pom.xml jib:dockerBuild

up: build
	docker compose up -d
	@echo ""
	@echo "Frontend: http://localhost:8081 | Backend API: http://localhost:8080/api"

down:
	docker compose down

logs:
	docker compose logs -f

reset: down
	docker compose down -v
