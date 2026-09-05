# Spring Boot Java Prompt Templates

## API Endpoint Creation

**Prompt:**
```
Create a Spring Boot REST controller for managing quizzes with the following requirements:

- Base path: /api/quizzes
- Endpoints:
  - GET /api/quizzes - List all public quizzes (no auth required)
  - GET /api/quizzes/my-quizzes - List authenticated user's quizzes (auth required)
  - GET /api/quizzes/{id} - Get quiz by ID (public if quiz is public, auth if private)
  - POST /api/quizzes - Create new quiz (auth required)
  - PUT /api/quizzes/{id} - Update quiz (auth required, owner only)
  - DELETE /api/quizzes/{id} - Delete quiz (auth required, owner or admin)
  - POST /api/quizzes/{id}/generate-questions - Generate AI questions for quiz (auth required, rate limited)

- Use proper HTTP status codes
- Use @Valid for request validation
- Use @PreAuthorize for authorization
- Return consistent response format (see AGENTS.md)
- Use service layer for business logic
- Add proper exception handling
- Follow existing code patterns in the project

Project context:
- Spring Boot 4, Java 25
- Spring Security with JWT
- PostgreSQL database
- Existing modules: auth, quizz, ia, translation
- Rate limiting: 5 AI calls/day per user
```

## Service Layer Implementation

**Prompt:**
```
Create a Spring Boot service class for quiz management with the following methods:

- List<QuizResponse> getPublicQuizzes()
- List<QuizResponse> getUserQuizzes(Long userId)
- QuizResponse getQuizById(Long id)
- QuizResponse createQuiz(QuizRequest request, Long userId)
- QuizResponse updateQuiz(Long id, QuizRequest request, Long userId)
- void deleteQuiz(Long id, Long userId)
- List<Question> generateQuestionsForQuiz(Long quizId, String topic, int count, String difficulty)

Requirements:
- Use repository pattern for database operations
- Add proper validation
- Handle authorization (check quiz ownership)
- Use transactions where appropriate
- Cache public quizzes for 5 minutes
- Rate limit AI question generation
- Follow existing code patterns
- Add comprehensive logging
- Throw appropriate exceptions

Dependencies:
- QuizRepository
- QuestionRepository
- AnswerRepository
- IaService (for AI question generation)
- TempSessionService (for unauthenticated users)
```

## Entity Class Creation

**Prompt:**
```
Create a JPA entity class for Quiz with the following requirements:

- Table name: quizzes
- Fields:
  - id: BIGSERIAL, primary key
  - title: VARCHAR(100), not null
  - description: TEXT
  - subject: VARCHAR(50)
  - difficulty: VARCHAR(20), default 'MEDIUM'
  - is_public: BOOLEAN, default false
  - user_id: BIGINT, foreign key to users table
  - created_at: TIMESTAMP WITH TIME ZONE, default NOW()
  - updated_at: TIMESTAMP WITH TIME ZONE, default NOW()

- Relationships:
  - One-to-many with questions (cascade all, orphan removal)
  - Many-to-one with user

- Add proper JPA annotations
- Use Lombok for boilerplate code
- Add validation annotations
- Implement equals and hashCode
- Add toString method
- Follow existing entity patterns in the project
```

## Repository Interface

**Prompt:**
```
Create a Spring Data JPA repository interface for QuizEntity with the following custom methods:

- List<QuizEntity> findPublicQuizzes()
- List<QuizEntity> findByUserId(Long userId)
- Optional<QuizEntity> findByIdAndUserId(Long id, Long userId)
- List<QuizEntity> findBySubject(String subject)
- List<QuizEntity> findByDifficulty(String difficulty)
- List<QuizEntity> findBySubjectAndDifficulty(String subject, String difficulty)
- Page<QuizEntity> findPublicQuizzes(Pageable pageable)
- Page<QuizEntity> findByUserId(Long userId, Pageable pageable)

Requirements:
- Extend JpaRepository<QuizEntity, Long>
- Use @Query annotations for custom queries
- Add proper @Modifying annotations for update/delete queries
- Use @Transactional where appropriate
- Follow existing repository patterns
- Add proper documentation
```

## DTO/Record Creation

**Prompt:**
```
Create the following record classes for quiz DTOs:

1. QuizRequest (for creating/updating quizzes):
   - title: String (required, 3-100 chars)
   - description: String (optional, max 500 chars)
   - subject: String (optional, max 50 chars)
   - difficulty: String (required, one of: EASY, MEDIUM, HARD)
   - isPublic: boolean (default false)
   - questionCount: int (optional, 1-100)

2. QuizResponse (for returning quiz data):
   - id: Long
   - title: String
   - description: String
   - subject: String
   - difficulty: String
   - isPublic: boolean
   - questionCount: int
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime
   - authorUsername: String

3. QuestionRequest (for creating questions):
   - text: String (required)
   - questionType: String (required, one of: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER)
   - points: int (default 1)
   - answers: List<AnswerRequest> (required, 2-4 answers)

4. AnswerRequest (for creating answers):
   - text: String (required)
   - isCorrect: boolean (required)

Requirements:
- Use Java 25 records
- Add proper validation annotations
- Use appropriate data types
- Follow existing DTO patterns
- Add proper documentation
```

## AI Service Integration

**Prompt:**
```
Create a service class for AI question generation with the following requirements:

Class: IaService

Methods:
- List<Question> generateQuestions(String topic, int count, String difficulty)
- Quiz generateQuiz(String topic, QuizSettings settings)
- QuestionAnalysis analyzeQuestion(String questionText)

Requirements:
- Use MistralClient to call Mistral API
- Implement rate limiting (5 calls/day per user)
- Cache generated questions in Redis for 1 hour
- Use structured prompts (see AiPromptTemplates)
- Handle API errors gracefully
- Parse and validate AI responses
- Add proper logging
- Follow existing service patterns

Dependencies:
- MistralClient
- RedisTemplate
- RateLimiter

Prompt templates should:
- Specify required format (JSON)
- Include context about quiz difficulty
- Request specific number of questions
- Request specific number of answers per question
- Specify that exactly one answer should be correct
- Request plausible distractors
```

## Mistral API Client

**Prompt:**
```
Create a Spring service class for Mistral API integration with the following requirements:

Class: MistralClient

Configuration:
- API URL: https://api.mistral.ai/v1
- API Key: from application.properties (mistral.api.key)
- Model: mistral-tiny (configurable)
- Timeout: 30 seconds

Methods:
- String chatCompletion(String prompt, int maxTokens, double temperature)
- String chatCompletion(List<Message> messages, String model, int maxTokens, double temperature)

Requirements:
- Use RestClient for HTTP calls
- Add proper headers (Authorization, Content-Type)
- Handle errors gracefully
- Implement retry logic for rate limits
- Add proper logging
- Use proper request/response DTOs
- Follow existing client patterns

Error handling:
- Handle 401 Unauthorized (invalid API key)
- Handle 429 Too Many Requests (rate limit)
- Handle 5xx Server Errors
- Handle network timeouts
- Provide meaningful error messages
```

## Security Configuration

**Prompt:**
```
Create a Spring Security configuration class with the following requirements:

Class: SecurityConfig

Configuration:
- Disable CSRF (for JWT-based authentication)
- Use stateless session management
- Configure CORS (allow all origins in dev, specific in prod)
- Add JWT authentication filter
- Configure authorization rules:
  - /api/auth/** - permit all
  - /api/quizzes/public - permit all
  - /api/quizzes/{id} - permit all if quiz is public
  - /api/quizzes/my-quizzes - hasRole('USER')
  - /api/quizzes - hasRole('USER')
  - /api/ia/** - hasRole('USER') and rate limited
  - /api/admin/** - hasRole('ADMIN')
  - All other requests - authenticated

Components:
- JwtAuthenticationFilter (extract and validate JWT from request)
- JwtTokenProvider (generate and validate JWT tokens)
- CustomUserDetailsService (load user from database)

Requirements:
- Use Spring Security 6+ conventions
- Token expiration: 24 hours for access token, 7 days for refresh token
- Store tokens in HttpOnly cookies for web
- Add proper error handling
- Follow existing security patterns
```

## Rate Limiting

**Prompt:**
```
Create a rate limiting configuration for the Quizz Platform with the following requirements:

1. RateLimitConfig class:
   - Configure Redis for rate limiting storage
   - Set up rate limiters for different endpoints

2. RateLimitFilter class:
   - Extend OncePerRequestFilter
   - Check rate limits before processing requests
   - Return 429 Too Many Requests when limit exceeded
   - Add proper headers (X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset)

Rate limits:
- AI endpoints: 5 calls/day per user
- Public endpoints: 100 calls/hour per IP
- Auth endpoints: 10 calls/minute per IP
- All other endpoints: 1000 calls/hour per user

Requirements:
- Use Redis for distributed rate limiting
- Use Bucket4j or similar library
- Add proper error responses
- Add logging for rate limit events
- Follow existing filter patterns
```

## Caching Configuration

**Prompt:**
```
Create a Redis caching configuration for the Quizz Platform with the following requirements:

Class: CacheConfig

Configuration:
- Redis host: localhost (configurable)
- Redis port: 6379 (configurable)
- Default TTL: 10 minutes
- Disable caching null values
- Use JSON serialization

Cache configurations:
- quizzes: 5 minutes TTL
- aiQuestions: 1 hour TTL
- users: 30 minutes TTL
- translations: 24 hours TTL

Requirements:
- Use Spring Cache abstraction
- Use Redis as cache manager
- Add proper error handling
- Add cache eviction policies
- Follow existing configuration patterns
```

## Exception Handling

**Prompt:**
```
Create a global exception handler for the Quizz Platform with the following requirements:

Class: GlobalExceptionHandler

Annotation: @ControllerAdvice

Handle the following exceptions:
- MethodArgumentNotValidException (validation errors)
- ConstraintViolationException (database constraint violations)
- AccessDeniedException (authorization errors)
- AuthenticationException (authentication errors)
- ResourceNotFoundException (custom exception for not found resources)
- BusinessException (custom exception for business logic errors)
- All other exceptions (generic error handling)

Response format:
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error message",
    "details": [{"field": "fieldName", "message": "Error message"}]
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

Requirements:
- Use @ExceptionHandler annotations
- Return appropriate HTTP status codes
- Add proper logging
- Follow existing exception handling patterns
```

## Database Schema Updates

**Prompt:**
```
Create a database migration script to add the following features to the Quizz Platform:

1. Add quiz tags support:
   - New table: quiz_tags
     - id: BIGSERIAL, primary key
     - quiz_id: BIGINT, foreign key to quizzes
     - tag: VARCHAR(50), not null
     - Unique constraint: (quiz_id, tag)

2. Add quiz statistics:
   - New table: quiz_statistics
     - id: BIGSERIAL, primary key
     - quiz_id: BIGINT, foreign key to quizzes
     - total_plays: INTEGER, default 0
     - total_completions: INTEGER, default 0
     - average_score: DECIMAL(5,2), default 0
     - last_played_at: TIMESTAMP WITH TIME ZONE
     - Unique constraint: quiz_id

3. Add user favorites:
   - New table: user_favorites
     - id: BIGSERIAL, primary key
     - user_id: BIGINT, foreign key to users
     - quiz_id: BIGINT, foreign key to quizzes
     - created_at: TIMESTAMP WITH TIME ZONE, default NOW()
     - Unique constraint: (user_id, quiz_id)

4. Add indexes:
   - idx_quiz_tags_quiz_id ON quiz_tags(quiz_id)
   - idx_quiz_tags_tag ON quiz_tags(tag)
   - idx_quiz_statistics_quiz_id ON quiz_statistics(quiz_id)
   - idx_user_favorites_user_id ON user_favorites(user_id)
   - idx_user_favorites_quiz_id ON user_favorites(quiz_id)

Requirements:
- Use Flyway or Liquibase for migrations
- Add proper foreign key constraints
- Add proper indexes
- Follow existing schema patterns
- Add rollback scripts
```

## Integration Tests

**Prompt:**
```
Create integration tests for the QuizService with the following requirements:

Test class: QuizServiceIntegrationTest

Annotations:
- @SpringBootTest
- @Testcontainers
- @ActiveProfiles("test")

Test containers:
- PostgreSQLContainer (postgres:15)
- RedisContainer (redis:7)

Test methods:
- testCreateQuiz_shouldSaveAndReturnQuiz()
- testGetQuizById_shouldReturnQuiz()
- testGetQuizById_shouldThrowNotFound()
- testUpdateQuiz_shouldUpdateAndReturnQuiz()
- testUpdateQuiz_shouldThrowNotFound()
- testUpdateQuiz_shouldThrowAccessDenied()
- testDeleteQuiz_shouldDeleteQuiz()
- testDeleteQuiz_shouldThrowNotFound()
- testDeleteQuiz_shouldThrowAccessDenied()
- testGetPublicQuizzes_shouldReturnOnlyPublicQuizzes()
- testGetUserQuizzes_shouldReturnOnlyUserQuizzes()
- testGenerateQuestions_shouldReturnQuestions()
- testGenerateQuestions_shouldRespectRateLimit()

Requirements:
- Use TestContainers for database
- Use @Transactional for each test
- Clean database after each test
- Test both success and error cases
- Follow existing test patterns
```

## OpenAPI Documentation

**Prompt:**
```
Update the OpenAPI documentation (openapi.yaml) to include the following endpoints:

1. Quiz endpoints:
   - GET /api/quizzes - List public quizzes
   - GET /api/quizzes/my-quizzes - List user's quizzes
   - GET /api/quizzes/{id} - Get quiz by ID
   - POST /api/quizzes - Create quiz
   - PUT /api/quizzes/{id} - Update quiz
   - DELETE /api/quizzes/{id} - Delete quiz
   - POST /api/quizzes/{id}/generate-questions - Generate AI questions

2. AI endpoints:
   - POST /api/ia/generate-questions - Generate questions from topic
   - POST /api/ia/generate-quiz - Generate complete quiz from topic
   - POST /api/ia/analyze-question - Analyze question quality

3. Add all request/response schemas
4. Add proper descriptions
5. Add security requirements
6. Add example responses

Requirements:
- Follow OpenAPI 3.0 specification
- Use proper data types
- Add validation rules
- Add error responses
- Follow existing documentation patterns
```
