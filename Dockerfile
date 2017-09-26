FROM maven:latest

WORKDIR /server

COPY src/ /server/src/
COPY adminpass /server/adminpass
COPY pom.xml /server/pom.xml
ENV THIS_ANNOYS_ME "world files/"
COPY ${THIS_ANNOYS_ME} /server/${THIS_ANNOYS_ME}
RUN mvn

EXPOSE 1337
CMD ["java", "-jar", "target/MUD-jar-with-dependencies.jar"]
