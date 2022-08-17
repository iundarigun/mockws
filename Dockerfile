FROM openjdk:17-slim

ENV mock_definition-path="/home/config/" \
    mock_files-path="/home/config/json/"

ENTRYPOINT ["java", "-jar", "/home/mock.jar"]

ADD build/libs/*.jar /home/mock.jar