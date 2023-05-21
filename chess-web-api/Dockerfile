# Etapa de construcción de Stockfish
FROM docker.io/bitnami/minideb:buster as stockfish-builder

WORKDIR /usr/app

ADD https://github.com/official-stockfish/Stockfish/archive/master.tar.gz ./

RUN if [ ! -d Stockfish-master ]; then tar xvzf *.tar.gz; fi \
    && cd Stockfish-master/src \
    && install_packages make g++ curl \
    && make build ARCH=x86-64-modern \
    && make install \
    && cd ../.. && rm -rf Stockfish-master *.tar.gz

# Etapa de construcción de Maven
FROM maven:3.8.2-openjdk-17-slim as maven-builder

WORKDIR /usr/src/app

COPY . .

RUN mvn clean install -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim-buster

WORKDIR /usr/app

# Copia el archivo JAR a la nueva imagen
COPY --from=maven-builder /usr/src/app/target/*.jar ./app.jar

# Copia el ejecutable de Stockfish a la nueva imagen
COPY --from=stockfish-builder /usr/local/bin/stockfish /usr/games/stockfish

ENTRYPOINT ["java","-jar","app.jar"]
