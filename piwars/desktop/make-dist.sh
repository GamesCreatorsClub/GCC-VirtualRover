#!/bin/bash

cd ../core && gradle clean build && cd ../desktop && gradle clean build dist && cp build/libs/desktop-1.0.jar ../../simulator/libs/piwars-simulator.jar
