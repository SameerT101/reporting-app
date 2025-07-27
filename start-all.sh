#!/bin/bash

# Reporting Application - Full  Deployment Script
echo "🚀 Starting Complete Application Stack..."
echo "=============================================="

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

# Stop any existing containers
echo "Stopping existing containers..."
docker-compose down

# Clean up old images (optional)
echo "Cleaning up old images..."
docker system prune -f

# Build and start all services
echo "Building and starting all services..."
docker-compose up --build -d

# Wait for services to be healthy
echo "Waiting for services to start up..."
sleep 10

# Check service status
echo ""
echo "Service Status:"
echo "==================="
docker-compose ps

echo ""
echo "Deployment Complete!"
echo "========================"
echo ""
echo "📱 Application URLs:"
echo "  • Main App:        http://localhost:3000"
echo "  • MailHog UI:      http://localhost:8025"
echo "  • RabbitMQ UI:     http://localhost:15672 (guest/guest)"
echo "  • PostgreSQL:      localhost:5432 (demo/demo)"
echo ""
echo "Test Credentials:"
echo "  • Username: noname@acme.com"
echo "  • Password: Passw0rd$123"
echo ""
echo "Useful Commands:"
echo "  • Restart app:     ./start.sh"
echo "  • Stop app:        ./stop.sh"
echo "  • View logs:       docker-compose logs -f"
echo "  • Stop all:        docker-compose down"
echo ""

# Check if application is responding
echo "Health Check:"
sleep 5
if curl -f http://localhost:3000/api/public/health &> /dev/null; then
    echo "✅ Application is healthy and ready!"
else
    echo "Application might still be starting up..."
    echo "   Check logs with: docker-compose logs app"
fi