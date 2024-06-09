FROM openjdk:17-slim

WORKDIR ./project
COPY ./gradle ./gradle
COPY ./gradlew ./*.gradle.kts ./
RUN ./gradlew wrapper
COPY ./src ./src

RUN ./gradlew bootJar
RUN mv ./build/libs/*.jar ../app.jar
RUN mv ./src/main/resources/application.properties ../application.properties

WORKDIR ..
RUN rm -r ./project

CMD java -jar ./app.jar
