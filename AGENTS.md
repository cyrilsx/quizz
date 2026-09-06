# AI Agent Development Guide for Quizz Platform

## Overview

This document provides instructions for AI agents (like Vibe Code) working on the Quizz Platform project. It defines project structure, conventions, workflows, and AI-specific optimizations.

## Project Structure

```
quizz-platform/
├── backend/                    # Spring Boot 4 (Java 25)
│   ├── src/main/java/com/quizzplatform/
│   │   ├── auth/              # Authentication module (JWT)
│   │   ├── quizz/             # Quiz management module
│   │   ├── ia/                # AI integration module
│   │   ├── translation/       # i18n module
│   │   └── config/            # Configuration classes
│   └── src/main/resources/
│       ├── schema.sql         # Database schema
│       ├── data.sql           # Seed data
│       └── openapi.yaml       # API documentation
│
├── frontend/                   # Angular 22.2 application
│   ├── src/app/
│   │   ├── core/              # Core services (Auth, API)
│   │   ├── features/          # Feature modules (quizz, user)
│   │   └── shared/            # Shared components & utilities
│   └── angular.json
│
├── mobile/                     # Ionic + Angular mobile app
│   ├── src/app/
│   │   ├── components/        # Mobile-specific components
│   │   └── services/          # Mobile services (QR scanner, etc.)
│   └── capacitor.config.ts
│
├── docs/                       # Documentation
├── .vibe/                      # AI agent configurations
│   ├── instructions/          # Role-specific instructions
│   ├── prompts/                # Reusable prompt templates
│   └── workflows/              # Development workflows
│
└── AGENTS.md                   # This file
```

## AI Agent Roles

### 1. Backend Developer Agent
**Responsibilities:**
- Spring Boot Java development
- API endpoint implementation
- Database schema design
- Security (JWT, Spring Security)
- AI service integration (Mistral API)

**Key Files:**
- `backend/src/main/java/com/quizzplatform/**/*.java`
- `backend/src/main/resources/schema.sql`
- `backend/src/main/resources/openapi.yaml`

**Conventions:**
- Use Java 25 features (records, sealed classes)
- Follow Spring Boot 4 conventions
- RESTful API design
- JWT authentication required for protected endpoints
- Rate limiting on AI endpoints (5 calls/day/user)

### 2. Frontend Developer Agent
**Responsibilities:**
- Angular 22.2 component development
- Reactive forms and RxJS
- Material Design implementation
- Internationalization (ngx-translate)
- QR code generation and display

**Key Files:**
- `frontend/src/app/**/*.ts`
- `frontend/src/app/**/*.html`
- `frontend/src/app/**/*.scss`

**Conventions:**
- Standalone components (Angular 22.2)
- Signal-based reactivity (Angular 22.2)
- Lazy-loaded feature modules
- Mobile-first responsive design
- Accessibility (WCAG 2.1 AA)

### 3. Mobile Developer Agent
**Responsibilities:**
- Ionic + Angular mobile development
- Capacitor plugin integration
- QR code scanning
- Camera access
- Secure storage

**Key Files:**
- `mobile/src/app/**/*.ts`
- `mobile/capacitor.config.ts`

**Conventions:**
- Reuse Angular components from frontend
- Ionic UI components (ion-button, ion-modal, etc.)
- Capacitor for native features
- Cross-platform compatibility (iOS/Android)

### 4. DevOps/SEO Agent
**Responsibilities:**
- SEO optimization (SSR, meta tags, structured data)
- Performance optimization
- CI/CD pipeline
- Docker configuration
- Monitoring setup

**Key Files:**
- `SEO_AI_OPTIMIZATION.md`
- `Dockerfile`
- `docker-compose.yml`

## Development Workflows

### Feature Development

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/<short-description>
   ```

2. **Implement Changes**
   - Follow existing code patterns
   - Add tests for new functionality
   - Update documentation

3. **Commit Changes**
   ```bash
   git add .
   git commit -m "feat: <description of changes>"
   ```

4. **Push to Remote**
   ```bash
   git push -u origin feature/<short-description>
   ```

5. **Create Draft PR**
   - Use GitHub draft PRs for review
   - Include screenshots for UI changes
   - Reference related issues

### Code Review Process

1. **Self-Review Checklist**
   - [ ] Code follows project conventions
   - [ ] All tests pass
   - [ ] No breaking changes to existing APIs
   - [ ] Documentation updated
   - [ ] Security considerations addressed

2. **AI Agent Review**
   - Analyze code for best practices
   - Check for security vulnerabilities
   - Verify performance implications
   - Suggest improvements

3. **Human Review**
   - Functional testing
   - UX/UI validation
   - Business logic verification

## AI-Specific Optimizations

### Backend AI Integration

The `ia/` module handles AI question generation:

```java
// IaService.java - Key methods
public class IaService {
    
    // Generate questions from a topic
    public List<Question> generateQuestions(String topic, int count, String difficulty);
    
    // Generate quiz from topic
    public Quiz generateQuiz(String topic, QuizSettings settings);
    
    // Analyze question quality
    public QuestionAnalysis analyzeQuestion(String questionText);
}
```

**AI Provider Configuration:**
- Primary: Mistral API
- Fallback: Local LLM (optional)
- Rate limiting: 5 calls/day per user
- Caching: Redis for generated questions

**Prompt Engineering:**
- Use structured prompts for consistent output
- Include context about quiz difficulty
- Specify response format (JSON)
- Handle errors gracefully

### Frontend AI Features

1. **AI Question Generator Component**
   - Topic input with suggestions
   - Difficulty selector
   - Question count slider
   - Preview before adding to quiz

2. **AI-Powered Search**
   - Semantic search for quizzes
   - Natural language query support
   - Filter by subject, difficulty, etc.

3. **Smart Recommendations**
   - Suggest similar quizzes
   - Recommend question types
   - Personalized quiz suggestions

### SEO & AI Engine Optimization

See `SEO_AI_OPTIMIZATION.md` for comprehensive guidelines including:

1. **Technical SEO**
   - Angular Universal (SSR)
   - Lazy loading
   - Meta tags
   - OpenGraph support

2. **Structured Data**
   - Quiz schema (JSON-LD)
   - FAQ schema
   - How-To schema
   - Breadcrumbs

3. **Content Optimization**
   - AI-friendly content templates
   - Semantic HTML
   - Proper heading hierarchy
   - Alt text for images

4. **Performance**
   - Fast API responses
   - Caching strategies
   - Image optimization
   - Bundle size reduction

## Coding Standards

### Java (Backend)

```java
// Class naming
public class QuizService { }        // PascalCase for classes
public class quizController { }      // NOT like this

// Method naming
public void createQuiz() { }         // camelCase for methods
public void CreateQuiz() { }          // NOT like this

// Variable naming
private String quizTitle;            // camelCase for variables
private String QuizTitle;            // NOT like this

// Constants
private static final int MAX_QUESTIONS = 100;  // UPPER_SNAKE_CASE

// DTOs use records (Java 25)
public record QuizRequest(String title, String description, int questionCount) { }

// Entities use Lombok or manual getters/setters
@Entity
@Data
public class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    // Relationships
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuestionEntity> questions = new ArrayList<>();
}
```

### TypeScript (Frontend)

```typescript
// Interface naming
interface Quiz {          // PascalCase for interfaces
  id: number;
  title: string;
  questions: Question[];
}

// Component naming
@Component({             // PascalCase for components
  selector: 'app-quiz-creator',
  templateUrl: './quizz-creator.component.html',
  styleUrls: ['./quizz-creator.component.scss']
})
export class QuizCreatorComponent { }

// Service naming
export class QuizService { }    // PascalCase for services

// Variable naming
const quizTitle: string = 'My Quiz';  // camelCase for variables
const QUIZ_TITLE: string = 'My Quiz';  // UPPER_SNAKE_CASE for constants

// Use signals for reactivity (Angular 22.2)
quizTitle = signal<string>('');
questions = signal<Question[]>([]);

// Use standalone components
@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule]
})
```

### File Organization

```
backend/src/main/java/com/quizzplatform/
├── auth/
│   ├── AuthController.java      # REST endpoints
│   ├── AuthService.java         # Business logic
│   ├── UserEntity.java          # JPA entity
│   └── UserRepository.java      # Spring Data JPA
│
├── quizz/
│   ├── QuizzController.java
│   ├── QuizService.java
│   ├── QuizEntity.java
│   ├── QuestionEntity.java
│   ├── AnswerEntity.java
│   └── QuizRepository.java
│
├── ia/
│   ├── IaController.java
│   ├── IaService.java
│   └── MistralClient.java        # AI provider client
│
├── translation/
│   ├── TranslationController.java
│   └── TranslationService.java
│
└── config/
    ├── SecurityConfig.java
    ├── CorsConfig.java
    └── InternationalizationConfig.java
```

## Testing Standards

### Backend Tests

```java
// Use JUnit 5
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

// Use TestContainers for integration tests
@Testcontainers
@SpringBootTest
class QuizIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    // Test with real database
}
```

### Frontend Tests

```typescript
// Component tests
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { QuizCreatorComponent } from './quizz-creator.component';

describe('QuizCreatorComponent', () => {
  let component: QuizCreatorComponent;
  let fixture: ComponentFixture<QuizCreatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizCreatorComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(QuizCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

// Service tests
import { TestBed } from '@angular/core/testing';
import { QuizService } from './quiz.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('QuizService', () => {
  let service: QuizService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [QuizService]
    });
    service = TestBed.inject(QuizService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
```

## Security Guidelines

### Authentication & Authorization

1. **JWT Implementation**
   - Token expiration: 24 hours
   - Refresh token: 7 days
   - Store in HttpOnly cookies for web
   - Store in secure storage for mobile

2. **Endpoint Security**
   ```java
   // Public endpoints (no auth required)
   @GetMapping("/api/quizzes/public")
   public List<Quiz> getPublicQuizzes() { }
   
   // Authenticated endpoints
   @GetMapping("/api/quizzes/my-quizzes")
   @PreAuthorize("hasRole('USER')")
   public List<Quiz> getMyQuizzes() { }
   
   // Admin endpoints
   @DeleteMapping("/api/users/{id}")
   @PreAuthorize("hasRole('ADMIN')")
   public void deleteUser(@PathVariable Long id) { }
   ```

3. **Rate Limiting**
   - AI endpoints: 5 calls/day per user
   - Public endpoints: 100 calls/hour per IP
   - Use Redis for rate limiting storage

### Input Validation

```java
// Always validate input
public Quiz createQuiz(@Valid @RequestBody QuizRequest request) {
    // Business logic
}

// Custom validators
public class QuizRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be 3-100 characters")
    private String title;
    
    @NotNull(message = "Question count is required")
    @Min(value = 1, message = "At least 1 question required")
    @Max(value = 100, message = "Maximum 100 questions")
    private Integer questionCount;
}
```

### Sanitization

```java
// Sanitize all user input
@Component
public class InputSanitizer {
    public String sanitize(String input) {
        if (input == null) return null;
        // Remove HTML tags
        return input.replaceAll("<[^>]*>", "");
    }
}
```

## Database Design

### Schema Overview

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
    position INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Answers
CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES questions(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    position INTEGER NOT NULL
);

-- Temporary Sessions (for unauthenticated users)
CREATE TABLE temp_sessions (
    id VARCHAR(36) PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT,
    quiz_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Translations
CREATE TABLE translations (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR(100) NOT NULL,
    language VARCHAR(10) NOT NULL,
    value TEXT NOT NULL,
    UNIQUE(key, language)
);
```

### Indexes

```sql
-- Performance indexes
CREATE INDEX idx_quizzes_user_id ON quizzes(user_id);
CREATE INDEX idx_quizzes_is_public ON quizzes(is_public) WHERE is_public = TRUE;
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);
CREATE INDEX idx_answers_question_id ON answers(question_id);
CREATE INDEX idx_temp_sessions_ip ON temp_sessions(ip_address);
CREATE INDEX idx_temp_sessions_expires ON temp_sessions(expires_at);
CREATE INDEX idx_translations_key ON translations(key);
CREATE INDEX idx_translations_language ON translations(language);
```

## API Design

### RESTful Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | User login | No |
| POST | `/api/auth/register` | User registration | No |
| GET | `/api/quizzes` | List all quizzes | No (public only) |
| GET | `/api/quizzes/my-quizzes` | List user's quizzes | Yes |
| POST | `/api/quizzes` | Create quiz | Yes |
| GET | `/api/quizzes/{id}` | Get quiz details | No (if public) |
| PUT | `/api/quizzes/{id}` | Update quiz | Yes (owner) |
| DELETE | `/api/quizzes/{id}` | Delete quiz | Yes (owner/admin) |
| POST | `/api/quizzes/{id}/generate-questions` | AI generate questions | Yes |
| GET | `/api/ia/questions` | Generate AI questions | Yes (rate limited) |
| GET | `/api/translations` | Get translations | No |
| POST | `/api/temp-sessions` | Create temp session | No |

### Response Format

```json
{
  "success": true,
  "data": { ... },
  "message": "Operation successful",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

### Error Format

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": [
      {
        "field": "title",
        "message": "Title is required"
      }
    ]
  },
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Internationalization (i18n)

### Backend (Spring Boot)

```java
// messages.properties (default - English)
quiz.title.required=Quiz title is required
quiz.description.max=Description cannot exceed 500 characters

// messages_fr.properties (French)
quiz.title.required=Le titre du quiz est obligatoire
quiz.description.max=La description ne peut pas dépasser 500 caractères

// Configuration
@Configuration
public class InternationalizationConfig {
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
    
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }
}
```

### Frontend (Angular)

```typescript
// Use ngx-translate
import { TranslateService } from '@ngx-translate/core';

// In component
constructor(private translate: TranslateService) {
    translate.setDefaultLang('en');
    translate.use('en');
}

// In template
<h1>{{ 'QUIZ.TITLE' | translate }}</h1>
```

```json
// en.json
{
  "QUIZ": {
    "TITLE": "Quiz Platform",
    "CREATE": "Create Quiz",
    "PLAY": "Play Quiz"
  }
}

// fr.json
{
  "QUIZ": {
    "TITLE": "Plateforme de Quiz",
    "CREATE": "Créer un Quiz",
    "PLAY": "Jouer au Quiz"
  }
}
```

## AI Integration Details

### Mistral API Client

```java
@Service
public class MistralClient {
    
    private static final String API_URL = "https://api.mistral.ai/v1";
    private static final String API_KEY = "${mistral.api.key}";
    
    private final RestClient restClient;
    
    public MistralClient(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl(API_URL)
            .defaultHeader("Authorization", "Bearer " + API_KEY)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    
    public String generateQuestions(String prompt, int maxTokens) {
        String requestBody = String.format("""
            {
                "model": "mistral-tiny",
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ],
                "max_tokens": %d,
                "temperature": 0.7
            }
            """, escapeJson(prompt), maxTokens);
        
        return restClient.post()
            .uri("/chat/completions")
            .body(requestBody)
            .retrieve()
            .body(String.class);
    }
    
    private String escapeJson(String input) {
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }
}
```

### AI Prompt Templates

```java
public class AiPromptTemplates {
    
    // Question generation prompt
    public static String generateQuestionsPrompt(String topic, int count, String difficulty) {
        return String.format("""
            You are an expert quiz creator. Generate %d multiple-choice questions about '%s'.
            
            Requirements:
            - Difficulty: %s
            - Each question must have exactly 4 answer choices
            - Exactly one correct answer per question
            - Questions should be clear and educational
            - Include plausible distractors (incorrect answers)
            
            Format the response as JSON:
            {
                "questions": [
                    {
                        "text": "Question text",
                        "answers": [
                            {"text": "Answer 1", "correct": false},
                            {"text": "Answer 2", "correct": false},
                            {"text": "Answer 3", "correct": false},
                            {"text": "Answer 4", "correct": true}
                        ]
                    }
                ]
            }
            
            Generate the questions now.
            """, count, topic, difficulty);
    }
    
    // Quiz generation prompt
    public static String generateQuizPrompt(String topic, String description, 
                                           int questionCount, String difficulty) {
        return String.format("""
            Create a complete quiz about '%s' with the description: '%s'.
            
            Quiz settings:
            - Number of questions: %d
            - Difficulty: %s
            - Include a mix of question types
            
            Format the response as JSON:
            {
                "title": "Quiz title",
                "description": "Quiz description",
                "subject": "Subject",
                "difficulty": "%s",
                "questions": [
                    {
                        "text": "Question text",
                        "type": "MULTIPLE_CHOICE",
                        "answers": [
                            {"text": "Answer 1", "correct": false},
                            {"text": "Answer 2", "correct": true}
                        ]
                    }
                ]
            }
            """, topic, description, questionCount, difficulty, difficulty);
    }
}
```

## Temporary Quiz Management

### Cookie-Based Tracking

```java
@Service
public class TempSessionService {
    
    private static final String SESSION_COOKIE = "quizz-temp-session";
    private static final int MAX_TEMP_QUIZZES = 5;
    private static final long SESSION_DURATION_HOURS = 24;
    
    @Autowired
    private TempSessionRepository sessionRepository;
    
    public TempSession getOrCreateSession(HttpServletRequest request) {
        String sessionId = getSessionIdFromCookie(request);
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        if (sessionId != null) {
            TempSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session != null && !session.isExpired()) {
                return session;
            }
        }
        
        // Create new session
        TempSession session = new TempSession();
        session.setId(UUID.randomUUID().toString());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setQuizCount(0);
        session.setExpiresAt(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS));
        
        sessionRepository.save(session);
        return session;
    }
    
    public boolean canCreateTempQuiz(HttpServletRequest request) {
        TempSession session = getOrCreateSession(request);
        return session.getQuizCount() < MAX_TEMP_QUIZZES;
    }
    
    public void incrementQuizCount(HttpServletRequest request) {
        TempSession session = getOrCreateSession(request);
        session.setQuizCount(session.getQuizCount() + 1);
        sessionRepository.save(session);
    }
    
    private String getSessionIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
```

### Frontend Cookie Management

```typescript
// Cookie service for temporary quizzes
@Injectable({ providedIn: 'root' })
export class TempSessionService {
    private readonly SESSION_KEY = 'quizz-temp-session';
    private readonly MAX_QUIZZES = 5;
    
    constructor(private cookieService: CookieService) {}
    
    getSessionId(): string | null {
        return this.cookieService.get(this.SESSION_KEY);
    }
    
    setSessionId(sessionId: string): void {
        const expires = new Date();
        expires.setDate(expires.getDate() + 1); // 1 day
        this.cookieService.set(this.SESSION_KEY, sessionId, { 
            expires,
            path: '/',
            sameSite: 'Lax'
        });
    }
    
    canCreateTempQuiz(): boolean {
        const quizCount = this.getQuizCount();
        return quizCount < this.MAX_QUIZZES;
    }
    
    incrementQuizCount(): void {
        const quizCount = this.getQuizCount() + 1;
        this.setQuizCount(quizCount);
    }
    
    private getQuizCount(): number {
        const count = this.cookieService.get('quizz-temp-count');
        return count ? parseInt(count, 10) : 0;
    }
    
    private setQuizCount(count: number): void {
        this.cookieService.set('quizz-temp-count', count.toString(), { 
            expires: new Date(Date.now() + 86400000), // 1 day
            path: '/',
            sameSite: 'Lax'
        });
    }
}
```

## QR Code Generation

### Backend Service

```java
@Service
public class QRCodeService {
    
    private static final int QR_CODE_SIZE = 250;
    private static final String QR_CODE_FORMAT = "png";
    
    public byte[] generateQRCode(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 
                                                   QR_CODE_SIZE, QR_CODE_SIZE);
        
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, QR_CODE_FORMAT, baos);
        
        return baos.toByteArray();
    }
    
    public String generateShareUrl(Long quizId) {
        return String.format("https://quizzplatform.com/quiz/play/%d", quizId);
    }
    
    public String generateShortUrl(Long quizId) {
        // Implement URL shortening logic
        String shortCode = Base62.encode(quizId);
        return String.format("https://quizzplatform.com/q/%s", shortCode);
    }
}
```

### Frontend Component

```typescript
@Component({
    selector: 'app-qr-code',
    standalone: true,
    template: `
        <div class="qr-code-container">
            <img [src]="qrCodeImage" [alt]="'QR Code for ' + url" />
            <p>{{ url }}</p>
            <button mat-button (click)="downloadQRCode()">Download</button>
        </div>
    `,
    styles: [`
        .qr-code-container {
            text-align: center;
            padding: 1rem;
            border: 1px solid #ddd;
            border-radius: 8px;
            background: white;
        }
        img {
            width: 200px;
            height: 200px;
        }
    `]
})
export class QRCodeComponent {
    @Input() url: string = '';
    qrCodeImage: string = '';
    
    constructor(private qrCodeService: QRCodeService) {}
    
    ngOnChanges(): void {
        this.generateQRCode();
    }
    
    generateQRCode(): void {
        this.qrCodeService.generateQRCode(this.url).subscribe(image => {
            this.qrCodeImage = image;
        });
    }
    
    downloadQRCode(): void {
        // Implement download logic
    }
}
```

## Mobile-Specific Features

### QR Code Scanner

```typescript
// qr-scanner.service.ts
@Injectable({ providedIn: 'root' })
export class QRScannerService {
    
    constructor() {}
    
    async scanQRCode(): Promise<string> {
        // Check if running on mobile
        if (!Capacitor.isPluginAvailable('BarcodeScanner')) {
            throw new Error('QR Scanner not available');
        }
        
        // Request camera permission
        const status = await BarcodeScanner.checkPermission();
        if (status.granted) {
            // Start scanning
            const result = await BarcodeScanner.startScan();
            
            if (result.hasContent) {
                return result.content;
            }
            throw new Error('No QR code detected');
        } else {
            // Request permission
            const requestStatus = await BarcodeScanner.requestPermission();
            if (requestStatus.granted) {
                return this.scanQRCode();
            }
            throw new Error('Camera permission denied');
        }
    }
    
    async stopScan(): Promise<void> {
        await BarcodeScanner.stopScan();
    }
}
```

### Mobile Sharing

```typescript
// mobile-share.service.ts
@Injectable({ providedIn: 'root' })
export class MobileShareService {
    
    constructor() {}
    
    async shareQuiz(quizId: number, quizTitle: string): Promise<void> {
        const url = `https://quizzplatform.com/quiz/play/${quizId}`;
        const message = `Check out this quiz: ${quizTitle}`;
        
        if (Capacitor.isPluginAvailable('Share')) {
            await Share.share({
                title: quizTitle,
                text: message,
                url: url,
                dialogTitle: 'Share Quiz'
            });
        } else {
            // Fallback for web
            navigator.share({
                title: quizTitle,
                text: message,
                url: url
            });
        }
    }
    
    async generateAndShareQRCode(quizId: number): Promise<void> {
        // Generate QR code and share as image
        // Implementation depends on platform
    }
}
```

## Performance Optimization

### Backend Caching

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeValuesWith(SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));
    }
    
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
            .withCacheConfiguration("quizzes",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
            .withCacheConfiguration("aiQuestions",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
    }
}

// Usage in service
@Service
public class QuizService {
    
    @Cacheable(value = "quizzes", key = "#id")
    public Quiz getQuizById(Long id) {
        // Database call
    }
    
    @CacheEvict(value = "quizzes", key = "#quiz.id")
    public Quiz updateQuiz(Quiz quiz) {
        // Update and save
    }
}
```

### Frontend Performance

```typescript
// Lazy loading example
const routes: Routes = [
    {
        path: 'quiz',
        loadChildren: () => import('./features/quiz/quiz.module').then(m => m.QuizModule)
    },
    {
        path: 'create',
        loadChildren: () => import('./features/quiz-creator/quiz-creator.module')
            .then(m => m.QuizCreatorModule)
    }
];

// Image optimization
@Component({
    template: `
        <img [src]="imageUrl" 
             [ngSrc]="imageUrl" 
             [alt]="altText"
             loading="lazy"
             width="800"
             height="600" />
    `
})
```

## Monitoring and Analytics

### Backend Metrics

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
            "application", "quizz-platform",
            "version", "1.0.0"
        );
    }
    
    @Bean
    public Timer quizCreationTimer(MeterRegistry registry) {
        return Timer.builder("quiz.creation.time")
            .description("Time taken to create a quiz")
            .register(registry);
    }
}

// Usage in service
@Service
public class QuizService {
    
    @Autowired
    private Timer quizCreationTimer;
    
    public Quiz createQuiz(QuizRequest request) {
        return quizCreationTimer.record(() -> {
            // Create quiz logic
        });
    }
}
```

### Frontend Analytics

```typescript
// analytics.service.ts
@Injectable({ providedIn: 'root' })
export class AnalyticsService {
    
    constructor() {}
    
    trackEvent(eventName: string, properties: Record<string, any> = {}): void {
        // Send to analytics provider
        console.log('Event:', eventName, properties);
    }
    
    trackQuizCreated(quizId: number, questionCount: number): void {
        this.trackEvent('Quiz Created', {
            quizId,
            questionCount,
            timestamp: new Date().toISOString()
        });
    }
    
    trackQuizStarted(quizId: number): void {
        this.trackEvent('Quiz Started', { quizId });
    }
    
    trackQuizCompleted(quizId: number, score: number, total: number): void {
        this.trackEvent('Quiz Completed', {
            quizId,
            score,
            total,
            percentage: Math.round((score / total) * 100)
        });
    }
}
```

## Deployment

### Docker Configuration

```yaml
# docker-compose.yml
version: '3.8'

services:
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/quizz
      - SPRING_DATASOURCE_USERNAME=quizz
      - SPRING_DATASOURCE_PASSWORD=secret
      - JWT_SECRET=your-jwt-secret
      - MISTRAL_API_KEY=your-mistral-key
    depends_on:
      - db
      - redis

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=quizz
      - POSTGRES_USER=quizz
      - POSTGRES_PASSWORD=secret
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:7
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  frontend:
    build: ./frontend
    ports:
      - "4200:80"
    depends_on:
      - backend

volumes:
  postgres_data:
  redis_data:
```

### Backend Dockerfile

```dockerfile
# Dockerfile for Spring Boot backend
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy build files
COPY pom.xml ./
COPY src ./src/

# Build the application
RUN apt-get update && apt-get install -y git && \
    ./mvnw clean package -DskipTests

# Run the application
ENTRYPOINT ["java", "-jar", "target/quizz-backend.jar"]
```

### Frontend Dockerfile

```dockerfile
# Dockerfile for Angular frontend
FROM node:18-alpine as build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source files
COPY . .

# Build the application
RUN npm run build -- --configuration=production

# Use nginx to serve the app
FROM nginx:alpine

# Copy built files
COPY --from=build /app/dist/quizz-platform /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Ensure `CorsConfig.java` is properly configured
   - Check frontend API base URL
   - Verify backend CORS headers

2. **JWT Authentication Failures**
   - Verify JWT secret matches between frontend and backend
   - Check token expiration time
   - Ensure tokens are stored securely

3. **AI API Errors**
   - Verify Mistral API key is valid
   - Check rate limiting
   - Validate prompt format

4. **Database Connection Issues**
   - Verify database credentials
   - Check network connectivity
   - Ensure database is running

5. **QR Code Scanning Problems**
   - Check camera permissions
   - Verify Capacitor plugin is installed
   - Test on physical device (not just emulator)

### Debugging Tips

1. **Backend Debugging**
   ```bash
   # Run with debug mode
   java -jar -Dspring.profiles.active=dev -Ddebug quizz-backend.jar
   
   # Connect with remote debugger
   java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 quizz-backend.jar
   ```

2. **Frontend Debugging**
   ```bash
   # Run with source maps
   ng serve --source-map
   
   # Debug in Chrome DevTools
   # Press F12 and use Sources tab
   ```

3. **Mobile Debugging**
   ```bash
   # Ionic serve with live reload
   ionic serve --livereload
   
   # Debug on device
   ionic capacitor run android --livereload --external
   ```

## Resources

### Documentation
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [Ionic Documentation](https://ionicframework.com/docs)
- [Mistral API Documentation](https://docs.mistral.ai/)

### Tools
- [Postman](https://www.postman.com/) - API testing
- [pgAdmin](https://www.pgadmin.org/) - PostgreSQL management
- [RedisInsight](https://redis.com/redis-enterprise/redis-insight/) - Redis management
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) - Container management

### Learning
- [Spring Boot Tutorials](https://spring.io/guides)
- [Angular Tutorial](https://angular.io/tutorial)
- [Ionic Academy](https://ionicacademy.com/)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Update documentation
6. Submit a pull request

### Pull Request Guidelines
- Use descriptive commit messages
- Reference related issues
- Include screenshots for UI changes
- Keep PRs focused on a single feature/bugfix
- Update CHANGELOG.md if applicable

## License

This project is licensed under the MIT License.

## Contact

For questions or support, please contact the development team.

---

**Last Updated:** 2024
**Version:** 1.0.0
