SHELL := /bin/bash

.PHONY: exercise1 exercise2 exercise3_1 exercise3_2 exercise3_3 exercise3_3_1

BASEDIR := $(PWD)

vertx/bin/vertx:
	@curl -L https://bintray.com/artifact/download/vertx/downloads/vert.x-3.3.3-full.tar.gz | tar -xz

exercise1: vertx/bin/vertx
	@vertx run Exercise1/Exercise1.groovy

exercise2: vertx/bin/vertx
	@vertx run Exercise2/Exercise2.groovy

exercise3_1: vertx/bin/vertx
	@vertx run Exercise3/Exercise3_1.groovy

exercise3_2: vertx/bin/vertx
	@vertx run Exercise3/Exercise3_2.groovy

exercise3_3: vertx/bin/vertx
	@vertx run Exercise3/Exercise3_3.groovy

exercise3_3_1: vertx/bin/vertx
	@vertx run Exercise3/Exercise3_3_1.groovy

exercise4: vertx/bin/vertx
	@vertx run Exercise4/Exercise4.groovy

exercise5: vertx/bin/vertx
	@vertx run Exercise5/Exercise5.groovy

exercise6: vertx/bin/vertx
	@cd $(BASEDIR)/Exercise6; vertx run MainVerticle.groovy

exercise7: vertx/bin/vertx
	@vertx run Exercise7/Exercise7.groovy