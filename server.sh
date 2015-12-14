#!/bin/bash
java -jar target/MUD-jar-with-dependencies.jar server 2>&1 | tee server.out
