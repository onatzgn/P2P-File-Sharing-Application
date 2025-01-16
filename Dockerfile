FROM openjdk:19-jdk-slim


RUN apt-get update && apt-get install -y findutils


WORKDIR /app


COPY . /app


RUN mkdir -p out && javac -d out $(find . -name "*.java")


RUN jar cf p2p-app.jar -C out .


CMD ["java", "-cp", "p2p-app.jar", "p2p.app.MainApp"]
