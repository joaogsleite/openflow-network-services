
DOCKER.ACCOUNT	:= joaogsleite

REPO.MININET 	:= mininet
REPO.FLOODLIGHT := floodlight

build-%:
	$(eval TARGET := $(patsubst build-%,%,$@))
	cd $(TARGET) && \
	docker build -t "$(DOCKER.ACCOUNT)/$(TARGET)" .
build: build-$(REPO.MININET) build-$(REPO.FLOODLIGHT)

x11:
	xhost + $(shell ipconfig getifaddr en0)
start: x11
	docker-compose up -d    

stop:
	docker-compose down

clean-%:
	docker rmi -f "$(DOCKER.ACCOUNT)/$(patsubst clean-%,%,$@)"
clean: clean-$(REPO.MININET) clean-$(REPO.FLOODLIGHT)

attach-%:
	$(eval TARGET := $(patsubst attach-%,%,$@))
	@docker exec -it $(TARGET) \
		bash -c 'cd $(TARGET) && bash'
