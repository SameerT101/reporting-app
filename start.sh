#!/bin/bash

# Reporting Application - App Server Restart Script
echo "Restarting Spring Boot Application..."
echo "========================================"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Stop only the Spring Boot app container
echo "Stopping Spring Boot application..."
docker-compose stop app
docker-compose rm -f app

# Rebuild and start only the Spring Boot app
echo "Building and starting Spring Boot application..."
docker-compose up --build -d app

# Wait for app to start
echo "Waiting for application to start up..."
sleep 15

# Check app status
echo ""
echo "Application Status:"
echo "====================="
docker-compose ps app

echo ""
echo "Application Restart Complete!"
echo "==============================="
echo ""
echo "📱 Application URL:"
echo "  • Main App: http://localhost:3000"
echo ""
echo "🔧 Useful Commands:"
echo "  • View app logs:   docker-compose logs -f app"
echo "  • Stop app:        docker-compose stop app"
echo "  • Restart app:     ./start.sh"
echo ""

# Check if application is responding
echo "Health Check:"
sleep 5
if curl -f http://localhost:3000/api/public/health &> /dev/null; then
    echo "Application is healthy and ready!"
else
    echo "Application might still be starting up..."
    echo "   Check logs with: docker-compose logs -f app"
fi