compose_file=src/docker/compose.yaml

startdb:
	docker compose -f ${compose_file} up -d database
stopdb:
	docker compose -f ${compose_file} down database

start:
	docker compose -f ${compose_file} up

stop:
	docker compose -f ${compose_file} down

.PHONY: start stop startdb stopdb