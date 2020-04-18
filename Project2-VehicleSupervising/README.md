# Software Architecture
# Project 2 - Vehicle Supervision

## Contextualization

This directory contains the work done by the authors on the second practical assignment for the course in Software Architecture of the MSc. in Informatics Engineering of the University of Aveiro.
The assignment is focused on a platform that collects and processes information from simulated vehicles.

The defined technologies to accomplish the goals are: Java for the application; Apache Kafka for the messaging system; Java Swing for the graphical interfaces.
All code is presented with the proper documentation and a report on the development and the results is available here.

## Authors

Filipe Pires, nmec. 85122

João Alegria, nmec. 85048

## Description

The project is about a centralized platform for vehicle supervision.
There are four entities: Collect, Report, Batch and Alarm.
Each entity has its own main method and is held accountable for a set of responsibilities around three kafka topics.
For further details, read the project's report or contact us.

## Instructions on how to run the code

1. Have installed Java SE8.
2. Have installed NetBeans or other IDE (only tested with Netbeans).
3. Open the project folder 'PA2_P1G07' on your IDE.
4. Make sure you have a folder named 'kafka_2.12-2.4.1' inside 'PA2_P1G07' and that that Kafka version is compatible with your Operating System.
5. Run the Main class and enjoy the Vehicle Supervising Simulation.

