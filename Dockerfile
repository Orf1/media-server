FROM openjdk:8-jdk
EXPOSE 80:80
RUN mkdir /app
COPY ./build/install/media-server/ /app/
WORKDIR /app/bin
CMD ["./media-server"]