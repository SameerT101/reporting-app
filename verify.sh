#!/bin/bash

# Deployment Verification Script

echo "Verifying Deployment..."
echo "========================="

# Function to check if a service is responding
check_service() {
    local name=$1
    local url=$2
    local expected_status=${3:-200}
    
    echo -n "🌐 Checking $name... "
    
    if curl -s -o /dev/null -w "%{http_code}" "$url" | grep -q "$expected_status"; then
        echo "OK"
        return 0
    else
        echo "❌ Failed"
        return 1
    fi
}

# Check if containers are running
echo "Running Container Status:"
docker-compose ps

echo ""
echo "Health Checks:"

# Wait a moment for services to be ready
sleep 5

# Check each service
check_service "Main Application" "http://localhost:3000/api/public/health"
check_service "MailHog" "http://localhost:8025"
check_service "RabbitMQ Management" "http://localhost:15672"

# Test database connectivity (via application)
echo -n "Database connectivity... "
if curl -s "http://localhost:3000/api/public/health" | grep -q "OK"; then
    echo "OK"
else
    echo "Failed"
fi

echo ""
echo "API Tests:"

# Test client credentials endpoint
echo -n "Auth endpoint... "
response=$(curl -s -w "%{http_code}" -X POST "http://localhost:3000/api/auth/api-login-client")
if echo "$response" | grep -q "200"; then
    echo "OK"
else
    echo "Failed (Status: ${response: -3})"
fi

echo "==========="
echo " INFORMATION:"
echo "==========="

if docker-compose ps | grep -q "Up (healthy)"; then
    echo "All services are up and running"
    echo ""
    echo "Deployment verification completed successfully!"
    echo ""
    echo "Access URLs:"
    echo "  • Application: http://localhost:3000"
    echo "  • MailHog:     http://localhost:8025"
    echo "  • RabbitMQ:    http://localhost:15672"
    echo ""
    echo "Test login: noname@acme.com / Passw0rd$123"
else
    echo " Some services may not be fully ready yet"
    echo "   Check logs with: docker-compose logs"
fi