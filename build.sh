#!/bin/bash
mvn

mkdir test
cp target/MUD.jar test/
cp world\ files/items.txt test/
cp world\ files/rooms.txt test/

gnome-terminal -x ./runserver.sh
/bin/bash ./runclient.sh
