.PHONY: build
build:
	./gradlew --quiet installDist

.PHONY: test
test:
	./gradlew test