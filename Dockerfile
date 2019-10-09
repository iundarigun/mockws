FROM openjdk:12

ENV mock_definition-path="" \
    mock_files-path=""

ENTRYPOINT ["/usr/bin/java", "-jar", "/home/mock.jar"]

ADD build/libs/*.jar /home/mock.jar