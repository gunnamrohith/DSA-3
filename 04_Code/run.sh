#!/bin/bash
# One-click launch script for DSA-3 Document & Code Similarity Analyzer Web Dashboard
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

PORT=${1:-8080}

if [ ! -d "bin/com" ]; then
    echo "[!] Binaries not found. Running build.sh first..."
    ./build.sh
fi

echo "================================================================="
echo " Launching DSA Similarity Analyzer Web Server on port $PORT..."
echo " Open http://localhost:$PORT in your web browser."
echo " Press Ctrl+C to stop the server."
echo "================================================================="

java -cp bin com.dsa.similarity.Main --server "$PORT"
