.PHONY: build
build:
	./gradlew --quiet installDist

.PHONY: run
run:
	./build/install/search-cli/bin/search-cli

.PHONY: test
test:
	./gradlew test

.PHONY: docs
docs:
	./gradlew dokkaGfm dokkaJavadoc