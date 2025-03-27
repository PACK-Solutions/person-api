# Person API

A RESTful service for managing person records.

## Prerequisites

- Docker and Docker Compose installed

## Running the stack

- Start the main application and monitoring stack:

```bash
docker-compose up -d
```

## Accessing the Application

- **Person Management UI**: Open [http://localhost:8080](http://localhost:8080) in your browser to access the Person
  Management interface

## Monitoring and Observability

This project includes a comprehensive monitoring and observability stack with the following components:

### Grafana

Grafana provides visualization dashboards for metrics, logs, and traces.

- **Access Grafana**: Open [http://localhost:3000](http://localhost:3000) in your browser
- **Default credentials**: admin/admin (you'll be prompted to change on first login)
- **Pre-configured dashboards**:
    - Spring Boot Observability: Overall view of application health, performance metrics, and traces
    - Spring Boot Statistics: Detailed application metrics and statistics

### Prometheus

Prometheus collects and stores metrics from the application.

- **Access Prometheus**: [http://localhost:9090](http://localhost:9090)

### Loki

Loki aggregates and indexes logs from the application.

- Logs are accessible through Grafana dashboards

### Tempo

Tempo stores distributed traces for request tracking.

- Traces are accessible through Grafana dashboards

## Exploring the Observability Data

1. **View application metrics**:
    - Go to Grafana at [http://localhost:3000](http://localhost:3000)
    - Navigate to the "Spring Boot Observability" or "Spring Boot Statistics" dashboards

2. **Explore logs**:
    - In Grafana, go to Explore
    - Select "Loki" as the data source
    - Query logs with labels (e.g., `{compose_service="person-api"}`)

3. **Analyze traces**:
    - In Grafana, go to Explore
    - Select "Tempo" as the data source
    - Search for traces by service name or use trace IDs from logs

4. **Correlate data**:
    - From metrics in dashboards, you can drill down to related logs and traces
    - From logs containing trace IDs, you can click through to see the full trace

## API Testing with IntelliJ HTTP Client

This project includes HTTP request samples that can be used with IntelliJ IDEA's HTTP Client feature. These samples
demonstrate how to interact with the Person API endpoints.

### Using HTTP Request Samples

1. Open the file `src/main/resources/http-requests/person-api.http` in IntelliJ IDEA
2. Click the "Run" button next to any request to execute it
3. View the response in the "Response" tab

The samples include:

- Getting all persons
- Getting a person by ID
- Creating a new person
- Updating an existing person
- Deleting a person

## Load Testing with k6

This project includes a k6 script for load testing the Person API. The script performs CRUD operations on person records
and measures performance metrics.

### Installing k6

#### Mac

```bash
# Using Homebrew
brew install k6
```

#### Windows

```powershell
# Using Chocolatey
choco install k6

# OR using Windows Package Manager (winget)
winget install k6

# OR download the installer from the official website
# https://k6.io/docs/get-started/installation/
```

### Running the Load Test

1. Make sure the Person API is running:

```bash
docker-compose up -d
```

2. Run the k6 script:

```bash
k6 run k6-script.js
```

The script will simulate user traffic according to the defined stages:

- Ramp up to 5 users over 10 seconds
- Ramp up to 10 users over 30 seconds
- Stay at 10 users for 2 minutes
- Ramp up to 15 users over 1 minute
- Stay at 15 users for 2 minutes
- Ramp down to 0 users over 1 minute

The test results will be displayed in the console, showing metrics like:

- Request duration
- Success rate
- Error counts
- HTTP request rates
