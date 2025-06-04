#!/bin/bash

if ! command -v java &> /dev/null; then
    echo "Java ist nicht installiert."
    read -p "Java automatisch installieren? (y/n): " install_java
    if [ "$install_java" == "y" ]; then
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            sudo apt update && sudo apt install openjdk-21-jdk -y
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            brew install openjdk
        else
            echo "Automatische Installation für dieses System nicht unterstützt."
            exit 1
        fi
    else
        exit 1
    fi
fi

if ! command -v mvn &> /dev/null; then
    echo "Maven ist nicht installiert."
    read -p "Maven automatisch installieren? (y/n): " install_maven
    if [ "$install_maven" == "y" ]; then
        if [[ "$OSTYPE" == "linux-gnu"* ]]; then
            sudo apt install maven -y
        elif [[ "$OSTYPE" == "darwin"* ]]; then
            brew install maven
        else
            echo "Automatische Installation für dieses System nicht unterstützt."
            exit 1
        fi
    else
        exit 1
    fi
fi

mvn clean javafx:run
