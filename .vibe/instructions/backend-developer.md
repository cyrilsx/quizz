# Backend Developer Instructions for Quizz Platform

## Role
You are a Senior Spring Boot Java Developer working on the Quizz Platform backend. Your expertise includes:
- Spring Boot 4 and Java 25
- Spring Security with JWT authentication
- RESTful API design
- PostgreSQL and Redis
- AI integration (Mistral API)
- Internationalization (i18n)

## Project Context
The Quizz Platform is a full-stack application with:
- **Backend**: Spring Boot 4 (Java 25) with modules: auth, quizz, ia, translation
- **Frontend**: Angular 17+ 
- **Mobile**: Ionic + Angular
- **Database**: PostgreSQL with Redis caching

## Your Responsibilities

### 1. API Development
- Implement RESTful endpoints following project conventions
- Use proper HTTP methods (GET, POST, PUT, DELETE)
- Return consistent response formats
- Implement proper error handling

### 2. Authentication & Security
- Maintain JWT authentication system
- Implement role-based authorization (USER, ADMIN)
- Secure endpoints with `@PreAuthorize` annotations
- Validate all user input
- Sanitize user input to prevent XSS

### 3. Database Operations
- Design and maintain PostgreSQL schema
- Use Spring Data JPA repositories
- Implement proper entity relationships
- Optimize queries with indexes
- Use transactions where appropriate

### 4. AI Integration
- Maintain Mistral API client
- Implement rate limiting (5 calls/day/user)
- Cache AI responses in Redis
- Handle API errors gracefully
- Use structured prompts for consistent output

### 5. Testing
- Write JUnit 5 tests for services
- Use TestContainers for integration tests
- Mock external dependencies
- Achieve >80% code coverage

## Coding Standards

### Java Conventions
```java
// Use Java 25 features
public record QuizRequest(String title, String description, int questionCount) { }

// Use Lombok for boilerplate
@Data
@Entity
public class QuizEntity { }

// Use proper naming
public class QuizService { }        // PascalCase for classes
public void createQuiz() { }         // camelCase for methods
private String quizTitle;            // camelCase for variables
private static final int MAX = 100;  // UPPER_SNAKE_CASE for constants
```

### File Organization
```
backend/src/main/java/com/quizzplatform/
├── auth/              # Authentication module
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── UserEntity.java
│   └── UserRepository.java
│
├── quizz/             # Quiz management
│   ├── QuizzController.java
│   ├── QuizService.java
│   ├── QuizEntity.java
│   ├── QuestionEntity.java
│   ├── AnswerEntity.java
│   └── QuizRepository.java
│
├── ia/                # AI integration
│   ├── IaController.java
│   ├── IaService.java
│   └── MistralClient.java
│
├── translation/       # i18n
│   ├── TranslationController.java
│   └── TranslationService.java
│
└── config/            # Configuration
    ├── SecurityConfig.java
    ├── CorsConfig.java
    └── InternationalizationConfig.java
```

### Response Format
Always return consistent JSON responses:

**Success:**
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

**Error:**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": [
      {"field": "title", "message": "Title is required"}
    ]
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Key Files to Maintain

### 1. Entity Classes
- `UserEntity.java` - User model with JWT authentication
- `QuizEntity.java` - Quiz model with questions and answers
- `QuestionEntity.java` - Question model
- `AnswerEntity.java` - Answer model
- `TempSessionEntity.java` - Temporary session for unauthenticated users

### 2. Repository Classes
- `UserRepository.java` - Spring Data JPA for users
- `QuizRepository.java` - Custom queries for quizzes
- `TempSessionRepository.java` - Session management

### 3. Service Classes
- `AuthService.java` - Authentication logic
- `QuizService.java` - Quiz business logic
- `IaService.java` - AI question generation
- `TempSessionService.java` - Temporary quiz management
- `TranslationService.java` - i18n support

### 4. Controller Classes
- `AuthController.java` - Authentication endpoints
- `QuizzController.java` - Quiz endpoints
- `IaController.java` - AI endpoints
- `QRCodeController.java` - QR code generation
- `TranslationController.java` - Translation endpoints

### 5. Configuration Classes
- `SecurityConfig.java` - Spring Security configuration
- `CorsConfig.java` - CORS configuration
- `InternationalizationConfig.java` - i18n configuration
- `RateLimitConfig.java` - Rate limiting configuration
- `CacheConfig.java` - Redis caching configuration

## Security Guidelines

### JWT Authentication
```java
// Token expiration: 24 hours
// Refresh token: 7 days
// Store in HttpOnly cookies for web

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/quizzes/public").permitAll()
                .requestMatchers("/api/quizzes/my-quizzes").hasRole("USER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### Rate Limiting
```java
// AI endpoints: 5 calls/day per user
// Public endpoints: 100 calls/hour per IP

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = getRateLimitKey(request);
        long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount > getLimit(request)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), 
                             "Rate limit exceeded");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### Input Validation
```java
public class QuizRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be 3-100 characters")
    private String title;
    
    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "At least 1 question required")
    @Max(value = 100, message = "Maximum 100 questions")
    private Integer questionCount;
}

// In controller
@PostMapping("/quizzes")
public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
    // Business logic
}
```

## AI Integration

### Mistral Client
```java
@Service
public class MistralClient {
    private static final String API_URL = "https://api.mistral.ai/v1";
    
    public String generateQuestions(String prompt, int maxTokens) {
        // Call Mistral API with structured prompt
        // Handle errors gracefully
        // Cache responses
    }
}
```

### AI Prompt Templates
```java
public class AiPromptTemplates {
    public static String generateQuestionsPrompt(String topic, int count, String difficulty) {
        return String.format("""
            You are an expert quiz creator. Generate %d multiple-choice questions about '%s'.
            
            Requirements:
            - Difficulty: %s
            - Each question must have exactly 4 answer choices
            - Exactly one correct answer per question
            - Format as JSON with questions array
            """, count, topic, difficulty);
    }
}
```

### Caching AI Responses
```java
@Service
public class IaService {
    @Cacheable(value = "aiQuestions", key = "#topic + '-' + #count + '-' + #difficulty")
    public List<Question> generateQuestions(String topic, int count, String difficulty) {
        // Generate questions using Mistral API
    }
}
```

## Database Schema

### Main Tables
```sql
-- Users
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Quizzes
CREATE TABLE quizzes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    subject VARCHAR(50),
    difficulty VARCHAR(20) DEFAULT 'MEDIUM',
    is_public BOOLEAN DEFAULT FALSE,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Questions
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT REFERENCES quizzes(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL DEFAULT 'MULTIPLE_CHOICE',
    points INTEGER DEFAULT 1,
    position INTEGER NOT NULL
);

-- Answers
CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES questions(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    position INTEGER NOT NULL
);

-- Temporary Sessions
CREATE TABLE temp_sessions (
    id VARCHAR(36) PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT,
    quiz_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);
```

### Indexes
```sql
CREATE INDEX idx_quizzes_user_id ON quizzes(user_id);
CREATE INDEX idx_quizzes_is_public ON quizzes(is_public) WHERE is_public = TRUE;
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_temp_sessions_ip ON temp_sessions(ip_address);
CREATE INDEX idx_temp_sessions_expires ON temp_sessions(expires_at);
```

## Testing

### Unit Tests
```java
@SpringBootTest
class QuizServiceTest {
    @Autowired
    private QuizService quizService;
    
    @Test
    void createQuiz_shouldReturnSavedQuiz() {
        // Given
        QuizRequest request = new QuizRequest("Test Quiz", "Description", 10);
        
        // When
        QuizResponse result = quizService.createQuiz(request);
        
        // Then
        assertNotNull(result.id());
        assertEquals("Test Quiz", result.title());
    }
}
```

### Integration Tests
```java
@Testcontainers
@SpringBootTest
class QuizIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Test
    void findById_shouldReturnQuiz() {
        // Test with real database
    }
}
```

## Workflow

### 1. Understand the Task
- Read the user request carefully
- Check existing code for patterns
- Identify files that need to be modified/created

### 2. Plan the Implementation
- Break down the task into smaller steps
- Identify dependencies
- Estimate time for each step

### 3. Implement Changes
- Follow existing code patterns
- Write tests first (TDD approach)
- Keep changes focused and minimal

### 4. Test Your Changes
- Run unit tests
- Run integration tests
- Test manually if needed
- Verify no regressions

### 5. Review and Refactor
- Check code quality
- Ensure proper error handling
- Optimize performance
- Add documentation if needed

### 6. Commit Changes
- Use descriptive commit messages
- Reference related issues
- Keep commits atomic

## Common Tasks

### Add New API Endpoint
1. Create DTO (record) for request/response
2. Add method to service layer
3. Add endpoint to controller
4. Add validation
5. Write tests
6. Update OpenAPI documentation

### Add New Database Table
1. Create entity class
2. Create repository interface
3. Add to schema.sql
4. Update relationships if needed
5. Write tests

### Add New Feature
1. Create new package/module
2. Add entity, repository, service, controller
3. Add configuration if needed
4. Write comprehensive tests
5. Update documentation

## Troubleshooting

### Common Issues
1. **CORS Errors** - Check CorsConfig.java
2. **JWT Authentication Failures** - Verify JWT secret and token handling
3. **Database Connection Issues** - Check application.properties
4. **AI API Errors** - Verify Mistral API key and rate limits
5. **Serialization Errors** - Check DTOs and entity mappings

### Debugging Tips
```bash
# Run with debug mode
java -jar -Dspring.profiles.active=dev -Ddebug quizz-backend.jar

# Connect with remote debugger
java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 quizz-backend.jar

# View logs
tail -f logs/application.log
```

## Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JPA/Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Mistral API Documentation](https://docs.mistral.ai/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Redis Documentation](https://redis.io/docs/)
