# LeakHawk

Leakhawk is  a product designed to identify potential data breaches or hacking attacks in near real-time related to a domain(s) defined by the user. 
It performs multilevel filtering and classification to identify as well as classify data leakages and evidence of hacking attacks. 


## Getting Started

These instructions will guide you to install and start the leakhawk system in your local machine. 

### Prerequisites

We need to start apache kafka server before getting start with the LeakHawk. 
```
* Download Apache Kafka (With zookeeper)

https://kafka.apache.org/downloads
```

```
* Install Maven for your machine

https://maven.apache.org/install.html
```

```
* Clone the project 
```

### Installing


Step 1

```
Start zookeeper server

/<path-to-kafka-installation>/bin/zookeeper-server-start.sh /<path-to-kafka-installation>/config/zookeeper.properties
```

Step 2

```
Start kafka server

/<path-to-kafka-installation>/bin/kafka-server-start.sh /<path-to-kafka-installation>/config/server.properties
```

Step 3

```
Create a topic for tweets in the kafka server.

/<path-to-kafka-installation>/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic tweets --partitions 2 --replication-factor 1
```

Step 4

```
Use following command to build the project with maven.

mvn clean install 
```

Now you should be able to run the projet. 

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Sugeesh Chandraweera** - [Sugeesh](https://github.com/sugeesh)
* **Isuru Chandima** - [Isuru](https://github.com/isuru-c)
* **Warunika Amali** - [Warunika](https://github.com/warunikaAmali)
* **Udeshika Sewwandi** - [Sewwandi](https://github.com/udeshika-sewwandi)


## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details

