FROM gradle:jdk17-alpine
WORKDIR /src

COPY ./gradle ./gradle
COPY ./service ./service
COPY ./testing ./testing
COPY ./build.gradle ./build.gradle
COPY ./settings.gradle ./settings.gradle
COPY ./gradlew ./gradlew

RUN ./gradlew build -x test
