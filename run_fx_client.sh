#!/bin/bash
# Navigate to the project root
cd ~/project/yaima || exit

# Install the parent POM only
mvn install -N || exit

# Build the common and client modules
mvn install -pl yaima-common,yaima-client || exit

# Go to the FX client module
cd yaima-fx-client || exit

# Run the JavaFX application
mvn javafx:run
