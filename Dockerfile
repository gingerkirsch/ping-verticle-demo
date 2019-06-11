FROM mycujoo/java-docker

COPY . .

ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_FILE cmm-ping-verticle-fat.jar
ENV PORT 8666

RUN gradle build

RUN mkdir -p $VERTICLE_HOME
RUN cp build/libs/$VERTICLE_FILE $VERTICLE_HOME/$VERTICLE_FILE

WORKDIR $VERTICLE_HOME

EXPOSE $PORT
CMD ["java", "-jar", "cmm-ping-verticle-fat.jar", "-cluster"]
