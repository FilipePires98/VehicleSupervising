#
# Python script for automatic generation of 'CAR.TXT'.
# Run this script inside the scripts package so that it stores the resulting file in the data package.
#
# @author Filipe Pires (85122) and João Alegria (85048)
#

import sys
import random
import time

# Script Functions

def generateCar():
    alphabet = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"]
    car = "" + str(random.randint(0,9)) + str(random.randint(0,9)) + "-" + alphabet[random.randint(0,len(alphabet)-1)] + alphabet[random.randint(0,len(alphabet)-1)] + "-" + str(random.randint(0,9)) + str(random.randint(0,9))
    return car

def selectCar(cars):
    return cars[random.randint(0,len(cars)-1)]

def getMsgType():
    type = "00"   # heartbeat  (6/10 prob)
    aux = random.randint(0,9)
    if aux < 2:   # car speed  (2/10 prob)
        type = "01"
    elif aux > 7: # car status (2/10 prob)
        type = "02"
    return type

def getCarSpeed():
    return str(random.randint(0,15) * 10)

def getCarStatus():
    status = "OK"       # (8/10 prob)
    if random.randint(0,9) > 7:
        status = "KO"   # (2/10 prob)
    return status

def createMsg(car_reg, type, info, t):
    timestamp = int(time.time()*1000)
    time.sleep(random.randint(1,t))
    msg = "| " + car_reg + " | " + str(timestamp) + " | " + type + " |"
    if(info):
        msg += " " + info + " |"
    msg += "\n"
    return msg

# Actual Script

# Process script arguments

nCars = 10
nLines = 100
maxT = 5
if(len(sys.argv)>1):
    new_nLines = 0
    try:
        new_nLines += int(sys.argv[1])
        if(new_nLines > 0 and new_nLines < 10000):
            nLines = new_nLines
    except TypeError:
        print("Error: invalid arguments, set to default.")
if(len(sys.argv)>2):
    new_nCars = 0
    try:
        new_nCars += int(sys.argv[2])
        if(new_nCars > 0 and new_nCars < int(nLines/10)):
            nCars = new_nCars
    except TypeError:
        print("Error: invalid arguments, set to default.")

# Generate cars

cars = []
while(len(cars)<nCars):
    car = generateCar()
    if car not in cars:
        cars.append(car)

# Generate messages and write them

file = open("../data/CAR2.TXT", "w")
for i in range(0,nLines):
    type = getMsgType()
    if type == "01":
        file.write(createMsg(selectCar(cars), type, getCarSpeed(), maxT))
    elif type == "02":
        file.write(createMsg(selectCar(cars), type, getCarStatus(), maxT))
    else:
        file.write(createMsg(selectCar(cars), type, None, maxT))
file.close()
