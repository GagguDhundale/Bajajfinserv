# Quiz Leaderboard Application

A Java backend application that polls an external API 10 times to collect quiz score data, handles duplicates efficiently, aggregates total scores per participant, and generates a leaderboard.

## Features

- **API Polling**: Polls the API 10 times to collect quiz score data
- **Deduplication**: Uses `HashSet` to identify and filter duplicate records using `roundId + participant` as the unique key
- **Score Aggregation**: Uses `HashMap` to efficiently aggregate total scores per participant
- **Leaderboard Generation**: Creates a sorted leaderboard with participants ranked by total score in descending order
- **API Submission**: Submits the computed leaderboard back to the API
- **Logging**: Comprehensive logging for debugging and monitoring

## Project Structure

```
Bajajfinserv/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── quiz/
│                   ├── Main.java                      # Application entry point
│                   ├── service/
│                   │   └── QuizService.java            # Core business logic
│                   ├── model/
│                   │   ├── Event.java                  # Quiz event model
│                   │   ├── ApiResponse.java            # API response wrapper
│                   │   └── LeaderboardEntry.java       # Leaderboard entry model
│                   └── util/
│                       └── HttpClientUtil.java         # HTTP client utilities
├── pom.xml                                              # Maven configuration
├── README.md                                            # This file
└── .gitignore                                           # Git ignore rules
```

## Dependencies

- **Gson 2.10.1**: JSON serialization/deserialization
- **Apache HttpClient 5.2.1**: HTTP operations
- **SLF4J 2.0.7**: Logging framework
- **JUnit 4.13.2**: Unit testing

## Build Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Compile and Package

```bash
cd /Users/gaggu/Documents/Bajajfinserv

# Clean and build
mvn clean package

# Build with skipping tests
mvn clean package -DskipTests
```

## Running the Application

### Option 1: Using Maven
```bash
# Run with default API endpoints (need to update URLs in Main.java)
mvn exec:java -Dexec.mainClass="com.quiz.Main"

# Run with custom API endpoints
mvn exec:java -Dexec.mainClass="com.quiz.Main" \
  -Dexec.args="https://api.example.com/fetch https://api.example.com/submit"
```

### Option 2: Using JAR File
```bash
# With default endpoints
java -jar target/quiz-leaderboard-1.0.0-shaded.jar

# With custom endpoints
java -jar target/quiz-leaderboard-1.0.0-shaded.jar \
  https://api.example.com/fetch https://api.example.com/submit
```

## Configuration

### Update API Endpoints

Edit `src/main/java/com/quiz/Main.java` and update the constants:

```java
private static final String FETCH_URL = "https://your-api-domain.com/quiz/scores";
private static final String SUBMIT_URL = "https://your-api-domain.com/quiz/leaderboard";
```

## Workflow

1. **Polling**: Polls the API 10 times to collect quiz score data
2. **Deduplication**: Filters duplicate events using `HashSet<String>` with unique keys
3. **Aggregation**: Aggregates scores per participant using `HashMap<String, Integer>`
4. **Leaderboard Generation**: Creates sorted leaderboard (descending by score)
5. **Ranking**: Assigns ranks to leaderboard entries
6. **Submission**: Submits the leaderboard to the API

## Expected API Response Format

### Fetch Endpoint Response
```json
{
  "data": [
    {
      "roundId": "round-001",
      "participant": "John Doe",
      "score": 85
    },
    {
      "roundId": "round-001",
      "participant": "Jane Smith",
      "score": 92
    }
  ],
  "status": "success",
  "message": "Data retrieved successfully"
}
```

### Leaderboard Submission Format
```json
{
  "data": [
    {
      "roundId": "leaderboard",
      "participant": "Jane Smith",
      "score": 275
    },
    {
      "roundId": "leaderboard",
      "participant": "John Doe",
      "score": 240
    }
  ],
  "status": "success",
  "message": "Leaderboard aggregated from 2 participants"
}
```

## Key Implementation Details

### Event Model
- Uses `getUniqueKey()` method to generate composite key: `roundId|participant`
- Supports POJO serialization with Gson

### Deduplication Strategy
- `HashSet<String>` maintains unique keys
- Prevents duplicate processing of same event
- Time complexity: O(1) per lookup

### Score Aggregation Strategy
- `HashMap<String, Integer>` for O(1) score lookups and updates
- `Integer.sum()` for merge operation
- Efficient score accumulation

### Leaderboard Sorting
- `Comparable<LeaderboardEntry>` interface for natural ordering
- Sorts in descending order (highest score first)
- `Collections.sort()` for efficient sorting

## Logging

The application uses SLF4J with SimpleLogger for console output. Logs include:
- Poll progress (which poll number, URL being called)
- Duplicate detection
- Score aggregation details
- Final leaderboard
- Submission status

## Error Handling

- HTTP errors logged and handled gracefully
- JSON parsing errors caught and reported
- Thread interruption handling during polling
- Retry-friendly architecture (can be enhanced with retry logic)

## Testing

To run tests:
```bash
mvn test
```

## Performance Characteristics

- **Space**: O(n) where n is the number of unique events
- **Time Polling**: O(p * e) where p = polls (10), e = events per poll
- **Time Aggregation**: O(u) where u = unique events
- **Time Sorting**: O(u log u) where u = unique participants

## License

Open source - modify and use as needed

## Support

For issues or improvements, refer to the logging output and stack traces provided by the application.
