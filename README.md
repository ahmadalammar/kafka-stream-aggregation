# Kafka Stream - Aggregation Example
### Content
- Introduction and Terminology
- Setup and Run Application

### Introduction and Terminology

In this example, we will demonstrate how to read a data stream from a trade website, 
aggregate "quotes" every 1 minute, and then generate a [candlestick](https://en.wikipedia.org/wiki/Candlestick_chart) chart.
![chart](https://t4.ftcdn.net/jpg/02/79/65/79/360_F_279657943_FALhJZ6g4shXyfqMIRifp1l6lhiwhbwm.jpg)

#### What is window aggregation?
In Kafka Streams, a windowed operation is a powerful concept used for processing and
aggregating data within specific time intervals. It allows us to group and analyze events
that occur within a defined window of time, enabling us to perform temporal computations on
the streaming data.

### Setup and Run Application
To run the app, please follow these steps:
- Start kafka server:
```
cd kafka/
docker-compose up -d

// verify the service
docker ps
```
- Start Quote service:
```
cd quote-service/
docker-compose up -d

// verify the service
docker ps
```
- Run the application:
```
mvn spring-boot:run
```
- Once the service is up we can verify the results at:
```
http://localhost:8080/candlesticks?isin={ISIN}
```
- Run the test:
```
mvn test
```



