# Software Architecture - Project 1: Harvest Simulation

## Contextualization

This directory contains the work done by the authors on the first practical assignment for the course in Software Architecture of the MSc. in Informatics Engineering of the University of Aveiro.
The assignment is focused on an architecture where concurrency (processes and threads) are a key aspect.

The defined technologies to accomplish the goals are: Java for the concurrent application; Java Swing for the UI.
All code is presented with the proper documentation and a report on the development and the results is available here.

## Authors

Filipe Pires, nmec. 85122

João Alegria, nmec. 85048

## Description

The project is about a simulation of a harvest in an agricultural farm.
There are two main entities: the Control Center (CC) and the Farm Infrastructure (FI). 
The CC is responsible for supervising the harvest.
The FI is the infrastructure for the agricultural harvest.
The CC and the FI are implemented as two different processes and the communication between them is through sockets.
Each farmer is implemented as a different Thread.

## Instructions on how to run the code

1. Have installed Java SE8.
2. Have installed NetBeans or other IDE (only tested with Netbeans).
3. Open the project folder 'PA1_P1G01' on your IDE.
4. Run the Main class and enjoy the Harvest Simulation.
