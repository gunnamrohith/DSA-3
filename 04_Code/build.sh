#!/bin/bash
# One-click build script for DSA-3 Document & Code Similarity Analyzer
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "================================================================="
echo " Building DSA-3 Code Similarity Analyzer (Java 21 Zero-Dependency)"
echo "================================================================="

mkdir -p bin

echo "[1/2] Compiling Java source files..."
javac -d bin $(find src -name "*.java")

echo "[2/2] Running quick algorithmic verification test..."
java -cp bin com.dsa.similarity.Main --test

echo "================================================================="
echo " Build successful! Binaries compiled in: $PROJECT_DIR/bin"
echo " To start the Web Server, run: ./run.sh"
echo "================================================================="
