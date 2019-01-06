#!/bin/bash
mvn compile assembly:single && aws s3 cp --acl=public-read target/simple-project-1.0-jar-with-dependencies.jar s3://sdtdk8s
