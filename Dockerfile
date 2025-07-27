# Use Eclipse Temurin JDK 21 for building
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN chmod +x mvnw && ./mvnw dependency:resolve

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Use Eclipse Temurin JDK 21 for runtime (needed for JasperReports compilation)
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the built JAR file
COPY --from=builder /app/target/*.jar app.jar

# Install fonts for JasperReports (must be done as root)
RUN apt-get update && \
    apt-get install -y fontconfig fonts-liberation fonts-dejavu-core curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Extract the JAR to make dependencies available for JasperReports compilation
RUN jar -xf app.jar && \
    chmod -R 755 /app

# Create non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring && \
    chown -R spring:spring /app
USER spring:spring

# Expose port 3000
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:3000/api/public/health || exit 1

# Set classpath to include extracted dependencies
ENV CLASSPATH="/app/BOOT-INF/classes:/app/BOOT-INF/lib/*"

# Run the application with explicit classpath
ENTRYPOINT ["java", "-cp", "/app/BOOT-INF/classes:/app/BOOT-INF/lib/*", "com.sam.reporting.ReportappApplication"]