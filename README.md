# Vehicle Supervising
A Kafka-based Centralized Platform for Smart Vehicle Supervising

![](https://img.shields.io/badge/Academical%20Project-Yes-success)
![](https://img.shields.io/badge/License-Free%20To%20Use-green)
![](https://img.shields.io/badge/Made%20With-Java-red)
![](https://img.shields.io/badge/Made%20With-Kafka-red)
![](https://img.shields.io/badge/Maintained-No-red)

## Description

The goal of this project is to provide a system focused on a platform that collects and processes information from simulated vehicles.
The programming tools used are: Java for the application; Apache Kafka for the messaging system; Java Swing for the graphical interfaces.

There are four entities: Collect, Report, Batch and Alarm.
Each entity has its own main method and is held accountable for a set of responsibilities around three kafka topics.

![UserInterface2](https://github.com/FilipePires98/VehicleSupervising/blob/master/docs/img/GUI_3.png)

## Repository Structure

/docs - contains project report and diagrams

/src - contains the source code, written in Java

## Instructions to Build and Run

1. Have installed Java SE8.
2. Have installed NetBeans or other IDE (only tested with Netbeans).
3. Open the project folder 'PA2_P1G07' on your IDE.
4. Make sure you have a folder named 'kafka_2.12-2.4.1' inside 'PA2_P1G07' and that that Kafka version is compatible with your Operating System.
5. Run the Main class and enjoy the Vehicle Supervising Simulation.

## Authors:

The authors of this repository are Filipe Pires and João Alegria, and the project was developed for the Software Architecture Course of the Master's degree in Informatics Engineering of the University of Aveiro.

For further information, please read our [report](https://github.com/FilipePires98/VehicleSupervising/blob/master/docs/report.pdf) or contact us at filipesnetopires@ua.pt or joao.p@ua.pt.
