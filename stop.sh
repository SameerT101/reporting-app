#!/bin/bash

# Reporting Application - App Server Stop Script
echo "Stopping Application..."
echo "====================================="

# Stop only the  app container
echo "Stopping application..."
docker-compose stop app

echo ""
echo "Application stopped successfully!"
echo ""
echo "Note: Database, RabbitMQ, and MailHog are still running"
echo ""
echo "Other options:"
echo "  • Start app:       ./start.sh"
echo "  • View app logs:   docker-compose logs app"
echo "  • Stop all:        docker-compose down"
echo "  • Remove app:      docker-compose rm app"