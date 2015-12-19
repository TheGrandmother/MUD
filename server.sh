#!/bin/bash
java -jar target/MUD-jar-with-dependencies.jar 2>&1 | tee server.out
