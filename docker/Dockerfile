FROM openjdk:17-slim

COPY . ./project
WORKDIR ./project

RUN ./gradlew bootJar
RUN mv ./build/libs/*.jar ../app.jar
RUN mv ./src/main/resources/application.properties ../application.properties
RUN mv ./src/main/resources/env.properties ../env.properties

WORKDIR ..
RUN rm -r ./project

CMD java -jar ./app.jar
