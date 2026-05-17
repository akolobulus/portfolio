#!/usr/bin/env bash
set -euo pipefail

# Simple run script to execute the application JAR from project root.
JAR_PATH="target/fashion-store-management-1.0-SNAPSHOT.jar"

if [ ! -f "$JAR_PATH" ]; then
  echo "Error: JAR not found at $JAR_PATH"
  echo "Build the project first: mvn clean package"
  exit 1
fi

java -jar "$JAR_PATH" "$@"
