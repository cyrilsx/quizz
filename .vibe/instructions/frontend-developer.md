# Frontend Developer Instructions for Quizz Platform

## Role
You are a Senior Angular Developer working on the Quizz Platform frontend. Your expertise includes:
- Angular 22.2 with standalone components
- TypeScript and RxJS
- Angular Material Design
- ngx-translate for i18n
- Reactive forms
- Signal-based reactivity

## Project Context
The Quizz Platform is a full-stack application with:
- **Backend**: Spring Boot 4 (Java 25)
- **Frontend**: Angular 22.2 with modules: core, features, shared
- **Mobile**: Ionic + Angular (reuses frontend components)
- **Styling**: Angular Material + custom SCSS

## Your Responsibilities

### 1. Component Development
- Create standalone components (Angular 22.2)
- Use signal-based reactivity
- Implement responsive design
- Follow accessibility standards (WCAG 2.1 AA)

### 2. State Management
- Use RxJS for complex state
- Consider NgRx for global state (optional)
- Use signals for local component state
- Implement proper change detection

### 3. API Integration
- Create HTTP services for backend APIs
- Handle errors gracefully
- Implement loading states
- Use interceptors for JWT authentication

### 4. Internationalization (i18n)
- Use ngx-translate for translations
- Support multiple languages
- Handle RTL languages if needed
- Format dates, numbers, currencies

### 5. UI/UX Implementation
- Use Angular Material components
- Create custom reusable components
- Implement responsive layouts
- Ensure mobile-first design

### 6. Testing
- Write component tests with TestBed
- Test services with HttpClientTestingModule
- Mock dependencies
- Achieve >80% code coverage

## Coding Standards

### TypeScript Conventions
```typescript
// Interface naming - PascalCase
interface Quiz {
  id: number;
  title: string;
  questions: Question[];
}

// Class naming - PascalCase
export class QuizService { }

// Component naming - PascalCase with 'Component' suffix
@Component({...})
export class QuizCreatorComponent { }

// Variable naming - camelCase
const quizTitle: string = 'My Quiz';

// Constants - UPPER_SNAKE_CASE
const MAX_QUESTIONS: number = 100;

// Use signals for reactivity (Angular 22.2)
quizTitle = signal<string>('');
questions = signal<Question[]>([]);

// Use computed for derived values
questionCount = computed(() => this.questions().length);
```

### File Organization
```
frontend/src/app/
├── core/                      # Core module (singleton services)
│   ├── api/                   # API services
│   │   ├── quiz.service.ts
│   │   ├── auth.service.ts
│   │   └── ia.service.ts
│   ├── guards/                # Route guards
│   │   ├── auth.guard.ts
│   │   └── admin.guard.ts
│   ├── interceptors/          # HTTP interceptors
│   │   ├── jwt.interceptor.ts
│   │   └── error.interceptor.ts
│   ├── models/                # TypeScript interfaces
│   │   ├── quiz.model.ts
│   │   ├── user.model.ts
│   │   └── question.model.ts
│   └── services/              # Core services
│       ├── translate.service.ts
│       └── storage.service.ts
│
├── features/                  # Feature modules (lazy-loaded)
│   ├── quizz/                 # Quiz feature
│   │   ├── components/
│   │   │   ├── quiz-list/
│   │   │   ├── quiz-detail/
│   │   │   ├── quiz-creator/
│   │   │   └── quiz-player/
│   │   ├── services/
│   │   └── quizz.module.ts
│   │
│   ├── auth/                  # Authentication feature
│   │   ├── components/
│   │   │   ├── login/
│   │   │   └── register/
│   │   └── auth.module.ts
│   │
│   └── shared-features/       # Shared feature components
│
└── shared/                    # Shared module (reusable components)
    ├── components/
    │   ├── header/
    │   ├── footer/
    │   ├── qr-code/
    │   ├── dialog/
    │   └── loading/
    ├── directives/
    ├── pipes/
    └── shared.module.ts
```

### Component Structure
```typescript
// Use standalone components (Angular 22.2)
@Component({
  selector: 'app-quiz-creator',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    TranslateModule
  ],
  templateUrl: './quiz-creator.component.html',
  styleUrls: ['./quiz-creator.component.scss']
})
export class QuizCreatorComponent implements OnInit {
  // Use signals for state
  quizForm = new FormGroup({...});
  
  constructor(private quizService: QuizService) { }
  
  ngOnInit(): void { }
}
```

## Key Files to Maintain

### 1. Core Services
- `auth.service.ts` - Authentication (login, register, logout)
- `quiz.service.ts` - Quiz CRUD operations
- `ia.service.ts` - AI question generation
- `translate.service.ts` - i18n management
- `storage.service.ts` - Local storage wrapper

### 2. API Services
- `api.service.ts` - Base HTTP service
- `quiz-api.service.ts` - Quiz-specific endpoints
- `auth-api.service.ts` - Authentication endpoints
- `ia-api.service.ts` - AI endpoints

### 3. Components
- `quiz-creator.component.ts` - Create new quizzes
- `quiz-player.component.ts` - Take quizzes
- `quiz-list.component.ts` - List available quizzes
- `quiz-detail.component.ts` - View quiz details
- `quiz-share.component.ts` - Share quizzes via QR code
- `login.component.ts` - User login
- `register.component.ts` - User registration

### 4. Models/Interfaces
- `quiz.model.ts` - Quiz interface
- `question.model.ts` - Question interface
- `answer.model.ts` - Answer interface
- `user.model.ts` - User interface
- `api-response.model.ts` - Standard API response format

### 5. Guards & Interceptors
- `auth.guard.ts` - Protect authenticated routes
- `admin.guard.ts` - Protect admin routes
- `jwt.interceptor.ts` - Add JWT to requests
- `error.interceptor.ts` - Handle API errors
- `loading.interceptor.ts` - Show loading indicators

## API Integration

### HTTP Service Base
```typescript
// api.service.ts
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly apiUrl = environment.apiUrl;
  
  constructor(private http: HttpClient) { }
  
  get<T>(endpoint: string, params?: any): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}${endpoint}`, { params });
  }
  
  post<T>(endpoint: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.apiUrl}${endpoint}`, body);
  }
  
  put<T>(endpoint: string, body: any): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}${endpoint}`, body);
  }
  
  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.apiUrl}${endpoint}`);
  }
}
```

### Quiz Service
```typescript
// quiz.service.ts
@Injectable({ providedIn: 'root' })
export class QuizService {
  private readonly endpoint = '/quizzes';
  
  constructor(private api: ApiService) { }
  
  getPublicQuizzes(): Observable<Quiz[]> {
    return this.api.get<Quiz[]>(`${this.endpoint}/public`);
  }
  
  getMyQuizzes(): Observable<Quiz[]> {
    return this.api.get<Quiz[]>(`${this.endpoint}/my-quizzes`);
  }
  
  getQuizById(id: number): Observable<Quiz> {
    return this.api.get<Quiz>(`${this.endpoint}/${id}`);
  }
  
  createQuiz(quiz: QuizRequest): Observable<Quiz> {
    return this.api.post<Quiz>(this.endpoint, quiz);
  }
  
  updateQuiz(id: number, quiz: QuizRequest): Observable<Quiz> {
    return this.api.put<Quiz>(`${this.endpoint}/${id}`, quiz);
  }
  
  deleteQuiz(id: number): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${id}`);
  }
}
```

### AI Service
```typescript
// ia.service.ts
@Injectable({ providedIn: 'root' })
export class IaService {
  private readonly endpoint = '/ia';
  
  constructor(private api: ApiService) { }
  
  generateQuestions(topic: string, count: number, difficulty: string): Observable<Question[]> {
    return this.api.post<Question[]>(`${this.endpoint}/generate-questions`, {
      topic,
      count,
      difficulty
    });
  }
  
  generateQuiz(topic: string, settings: QuizSettings): Observable<Quiz> {
    return this.api.post<Quiz>(`${this.endpoint}/generate-quiz`, {
      topic,
      settings
    });
  }
}
```

### Authentication Service
```typescript
// auth.service.ts
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly endpoint = '/auth';
  private readonly TOKEN_KEY = 'quizz_jwt_token';
  private readonly REFRESH_TOKEN_KEY = 'quizz_refresh_token';
  
  constructor(private api: ApiService, private router: Router) { }
  
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(`${this.endpoint}/login`, credentials).pipe(
      tap(response => this.storeTokens(response))
    );
  }
  
  register(user: RegisterRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(`${this.endpoint}/register`, user).pipe(
      tap(response => this.storeTokens(response))
    );
  }
  
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    this.router.navigate(['/login']);
  }
  
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }
  
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
  
  private storeTokens(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
  }
}
```

## JWT Interceptor

```typescript
// jwt.interceptor.ts
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) { }
  
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();
    
    if (token && this.isApiRequest(request.url)) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(request).pipe(
      catchError(error => {
        if (error.status === 401) {
          // Handle token expiration
          this.authService.logout();
        }
        return throwError(() => error);
      })
    );
  }
  
  private isApiRequest(url: string): boolean {
    return url.startsWith(environment.apiUrl);
  }
}
```

## Error Handling

### Error Interceptor
```typescript
// error.interceptor.ts
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private notificationService: NotificationService) { }
  
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(
      catchError(error => {
        this.handleError(error);
        return throwError(() => error);
      })
    );
  }
  
  private handleError(error: HttpErrorResponse): void {
    let message = 'An unknown error occurred';
    
    if (error.error?.message) {
      message = error.error.message;
    } else if (error.status === 400) {
      message = 'Bad request';
    } else if (error.status === 401) {
      message = 'Unauthorized';
    } else if (error.status === 403) {
      message = 'Forbidden';
    } else if (error.status === 404) {
      message = 'Not found';
    } else if (error.status === 429) {
      message = 'Too many requests';
    } else if (error.status >= 500) {
      message = 'Server error';
    }
    
    this.notificationService.showError(message);
  }
}
```

### API Response Model
```typescript
// api-response.model.ts
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  timestamp: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: ErrorDetail[];
}

export interface ErrorDetail {
  field: string;
  message: string;
}

// Helper function to handle API responses
export function handleApiResponse<T>(response: ApiResponse<T>): T {
  if (!response.success) {
    throw new Error(response.message || 'API request failed');
  }
  return response.data!;
}
```

## Internationalization (i18n)

### Translation Service
```typescript
// translate.service.ts
@Injectable({ providedIn: 'root' })
export class TranslateServiceWrapper {
  private readonly LANG_KEY = 'quizz_language';
  
  constructor(private translate: TranslateService) { }
  
  initialize(): void {
    this.translate.setDefaultLang('en');
    const savedLang = localStorage.getItem(this.LANG_KEY) || 'en';
    this.translate.use(savedLang);
  }
  
  setLanguage(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem(this.LANG_KEY, lang);
  }
  
  getCurrentLanguage(): string {
    return this.translate.currentLang;
  }
  
  getAvailableLanguages(): string[] {
    return ['en', 'fr', 'es', 'de'];
  }
}
```

### Translation Files
```json
// en.json
{
  "COMMON": {
    "SAVE": "Save",
    "CANCEL": "Cancel",
    "DELETE": "Delete",
    "CONFIRM": "Confirm"
  },
  "QUIZ": {
    "TITLE": "Quiz Platform",
    "CREATE": "Create Quiz",
    "PLAY": "Play Quiz",
    "MY_QUIZZES": "My Quizzes",
    "PUBLIC_QUIZZES": "Public Quizzes",
    "NO_QUIZZES": "No quizzes found",
    "LOADING": "Loading quizzes..."
  },
  "AUTH": {
    "LOGIN": "Login",
    "REGISTER": "Register",
    "LOGOUT": "Logout",
    "EMAIL": "Email",
    "PASSWORD": "Password",
    "USERNAME": "Username",
    "REMEMBER_ME": "Remember me",
    "FORGOT_PASSWORD": "Forgot password?"
  },
  "VALIDATION": {
    "REQUIRED": "This field is required",
    "EMAIL_INVALID": "Please enter a valid email",
    "PASSWORD_MIN": "Password must be at least 8 characters",
    "PASSWORD_MATCH": "Passwords do not match"
  }
}

// fr.json
{
  "COMMON": {
    "SAVE": "Enregistrer",
    "CANCEL": "Annuler",
    "DELETE": "Supprimer",
    "CONFIRM": "Confirmer"
  },
  "QUIZ": {
    "TITLE": "Plateforme de Quiz",
    "CREATE": "Créer un Quiz",
    "PLAY": "Jouer au Quiz",
    "MY_QUIZZES": "Mes Quiz",
    "PUBLIC_QUIZZES": "Quiz Publics",
    "NO_QUIZZES": "Aucun quiz trouvé",
    "LOADING": "Chargement des quiz..."
  }
}
```

### Language Switcher Component
```typescript
// language-switcher.component.ts
@Component({
  selector: 'app-language-switcher',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatMenuModule, MatIconModule],
  template: `
    <button mat-button [matMenuTriggerFor]="languageMenu">
      <mat-icon>language</mat-icon>
      {{ currentLanguage() | uppercase }}
    </button>
    
    <mat-menu #languageMenu="matMenu">
      <button mat-menu-item 
              *ngFor="let lang of availableLanguages()"
              (click)="changeLanguage(lang)">
        {{ lang | uppercase }}
      </button>
    </mat-menu>
  `,
  styles: [`
    button {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
  `]
})
export class LanguageSwitcherComponent {
  currentLanguage = signal<string>('en');
  availableLanguages = signal<string[]>(['en', 'fr', 'es', 'de']);
  
  constructor(private translateService: TranslateServiceWrapper) {
    this.currentLanguage.set(this.translateService.getCurrentLanguage());
  }
  
  changeLanguage(lang: string): void {
    this.translateService.setLanguage(lang);
    this.currentLanguage.set(lang);
  }
}
```

## Component Examples

### Quiz Creator Component
```typescript
// quiz-creator.component.ts
@Component({
  selector: 'app-quiz-creator',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatSliderModule,
    MatCheckboxModule,
    TranslateModule
  ],
  templateUrl: './quiz-creator.component.html',
  styleUrls: ['./quiz-creator.component.scss']
})
export class QuizCreatorComponent {
  quizForm = new FormGroup({
    title: new FormControl('', [Validators.required, Validators.maxLength(100)]),
    description: new FormControl('', [Validators.maxLength(500)]),
    subject: new FormControl('', [Validators.maxLength(50)]),
    difficulty: new FormControl<'EASY' | 'MEDIUM' | 'HARD'>('MEDIUM'),
    isPublic: new FormControl(false),
    questionCount: new FormControl(5, [Validators.min(1), Validators.max(100)])
  });
  
  difficulties = ['EASY', 'MEDIUM', 'HARD'];
  subjects = ['General Knowledge', 'Science', 'History', 'Mathematics', 'Language'];
  
  loading = signal(false);
  error = signal<string | null>(null);
  
  constructor(
    private quizService: QuizService,
    private router: Router,
    private notificationService: NotificationService
  ) { }
  
  onSubmit(): void {
    if (this.quizForm.invalid) {
      this.quizForm.markAllAsTouched();
      return;
    }
    
    this.loading.set(true);
    this.error.set(null);
    
    const quizRequest = this.quizForm.value as QuizRequest;
    
    this.quizService.createQuiz(quizRequest).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (quiz) => {
        this.notificationService.showSuccess('Quiz created successfully');
        this.router.navigate(['/quiz', quiz.id]);
      },
      error: (err) => {
        this.error.set(err.message || 'Failed to create quiz');
      }
    });
  }
  
  generateWithAI(): void {
    // Open AI generation dialog
  }
}
```

```html
<!-- quiz-creator.component.html -->
<form [formGroup]="quizForm" (ngSubmit)="onSubmit()" class="quiz-form">
  <h1>{{ 'QUIZ.CREATE' | translate }}</h1>
  
  <mat-form-field appearance="fill">
    <mat-label>{{ 'QUIZ.TITLE' | translate }}</mat-label>
    <input matInput formControlName="title" />
    <mat-error *ngIf="quizForm.get('title')?.hasError('required')">
      {{ 'VALIDATION.REQUIRED' | translate }}
    </mat-error>
    <mat-error *ngIf="quizForm.get('title')?.hasError('maxlength')">
      {{ 'VALIDATION.MAX_LENGTH' | translate: { max: 100 } }}
    </mat-error>
  </mat-form-field>
  
  <mat-form-field appearance="fill">
    <mat-label>{{ 'QUIZ.DESCRIPTION' | translate }}</mat-label>
    <textarea matInput formControlName="description" rows="3"></textarea>
    <mat-error *ngIf="quizForm.get('description')?.hasError('maxlength')">
      {{ 'VALIDATION.MAX_LENGTH' | translate: { max: 500 } }}
    </mat-error>
  </mat-form-field>
  
  <div class="form-row">
    <mat-form-field appearance="fill">
      <mat-label>{{ 'QUIZ.SUBJECT' | translate }}</mat-label>
      <mat-select formControlName="subject">
        <mat-option *ngFor="let subject of subjects" [value]="subject">
          {{ subject }}
        </mat-option>
      </mat-select>
    </mat-form-field>
    
    <mat-form-field appearance="fill">
      <mat-label>{{ 'QUIZ.DIFFICULTY' | translate }}</mat-label>
      <mat-select formControlName="difficulty">
        <mat-option *ngFor="let diff of difficulties" [value]="diff">
          {{ diff | translate }}
        </mat-option>
      </mat-select>
    </mat-form-field>
  </div>
  
  <mat-form-field appearance="fill">
    <mat-label>{{ 'QUIZ.QUESTION_COUNT' | translate }}</mat-label>
    <input matInput type="number" formControlName="questionCount" min="1" max="100" />
    <mat-error *ngIf="quizForm.get('questionCount')?.hasError('min')">
      {{ 'VALIDATION.MIN' | translate: { min: 1 } }}
    </mat-error>
    <mat-error *ngIf="quizForm.get('questionCount')?.hasError('max')">
      {{ 'VALIDATION.MAX' | translate: { max: 100 } }}
    </mat-error>
  </mat-form-field>
  
  <mat-checkbox formControlName="isPublic">
    {{ 'QUIZ.PUBLIC' | translate }}
  </mat-checkbox>
  
  <div class="form-actions">
    <button mat-raised-button color="primary" type="submit" [disabled]="quizForm.invalid || loading()">
      <span *ngIf="!loading()">{{ 'COMMON.SAVE' | translate }}</span>
      <mat-spinner *ngIf="loading()" diameter="20"></mat-spinner>
    </button>
    
    <button mat-button color="accent" type="button" (click)="generateWithAI()">
      <mat-icon>auto_awesome</mat-icon>
      {{ 'QUIZ.GENERATE_AI' | translate }}
    </button>
    
    <button mat-button type="button" [routerLink]="['/']">
      {{ 'COMMON.CANCEL' | translate }}
    </button>
  </div>
  
  <mat-error *ngIf="error()">{{ error() }}</mat-error>
</form>
```

```scss
/* quiz-creator.component.scss */
.quiz-form {
  max-width: 600px;
  margin: 2rem auto;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  h1 {
    margin-bottom: 2rem;
    color: #3f51b5;
    text-align: center;
  }
  
  mat-form-field {
    width: 100%;
    margin-bottom: 1.5rem;
  }
  
  .form-row {
    display: flex;
    gap: 1rem;
    
    mat-form-field {
      flex: 1;
      margin-bottom: 1.5rem;
    }
  }
  
  .form-actions {
    display: flex;
    gap: 1rem;
    margin-top: 2rem;
    justify-content: flex-end;
    
    button {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }
  }
  
  mat-error {
    margin-top: 0.5rem;
    display: block;
  }
}

@media (max-width: 600px) {
  .quiz-form {
    padding: 1rem;
    margin: 1rem;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .form-actions {
    flex-wrap: wrap;
    justify-content: center;
  }
}
```

### Quiz Player Component
```typescript
// quiz-player.component.ts
@Component({
  selector: 'app-quiz-player',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatRadioModule,
    MatProgressBarModule,
    TranslateModule
  ],
  templateUrl: './quiz-player.component.html',
  styleUrls: ['./quiz-player.component.scss']
})
export class QuizPlayerComponent implements OnInit {
  quiz = signal<Quiz | null>(null);
  currentQuestionIndex = signal(0);
  selectedAnswers = signal<Map<number, number>>(new Map());
  score = signal(0);
  completed = signal(false);
  loading = signal(true);
  
  constructor(
    private quizService: QuizService,
    private route: ActivatedRoute,
    private router: Router
  ) { }
  
  ngOnInit(): void {
    const quizId = this.route.snapshot.paramMap.get('id');
    if (!quizId) {
      this.router.navigate(['/']);
      return;
    }
    
    this.quizService.getQuizById(+quizId).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (quiz) => this.quiz.set(quiz),
      error: () => this.router.navigate(['/'])
    });
  }
  
  currentQuestion(): Question | undefined {
    const quiz = this.quiz();
    const index = this.currentQuestionIndex();
    return quiz?.questions[index];
  }
  
  progress(): number {
    const quiz = this.quiz();
    if (!quiz) return 0;
    return ((this.currentQuestionIndex() + 1) / quiz.questions.length) * 100;
  }
  
  selectAnswer(questionId: number, answerId: number): void {
    const answers = this.selectedAnswers();
    answers.set(questionId, answerId);
    this.selectedAnswers.set(new Map(answers));
  }
  
  nextQuestion(): void {
    const quiz = this.quiz();
    if (!quiz) return;
    
    if (this.currentQuestionIndex() < quiz.questions.length - 1) {
      this.currentQuestionIndex.update(i => i + 1);
    }
  }
  
  previousQuestion(): void {
    if (this.currentQuestionIndex() > 0) {
      this.currentQuestionIndex.update(i => i - 1);
    }
  }
  
  submitQuiz(): void {
    const quiz = this.quiz();
    if (!quiz) return;
    
    let correctCount = 0;
    const answers = this.selectedAnswers();
    
    quiz.questions.forEach((question, index) => {
      const selectedAnswerId = answers.get(question.id);
      if (selectedAnswerId !== undefined) {
        const selectedAnswer = question.answers.find(a => a.id === selectedAnswerId);
        if (selectedAnswer?.isCorrect) {
          correctCount++;
        }
      }
    });
    
    this.score.set(correctCount);
    this.completed.set(true);
  }
  
  restartQuiz(): void {
    this.currentQuestionIndex.set(0);
    this.selectedAnswers.set(new Map());
    this.score.set(0);
    this.completed.set(false);
  }
}
```

### QR Code Component
```typescript
// qr-code.component.ts
@Component({
  selector: 'app-qr-code',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatDialogModule],
  template: `
    <div class="qr-code-container">
      <img [src]="qrCodeImage()" [alt]="'QR Code for ' + url()" />
      <p class="url-text">{{ url() }}</p>
      <div class="actions">
        <button mat-button color="primary" (click)="downloadQRCode()">
          <mat-icon>download</mat-icon>
          {{ 'COMMON.DOWNLOAD' | translate }}
        </button>
        <button mat-button color="accent" (click)="copyToClipboard()">
          <mat-icon>content_copy</mat-icon>
          {{ 'COMMON.COPY' | translate }}
        </button>
      </div>
    </div>
  `,
  styles: [`
    .qr-code-container {
      text-align: center;
      padding: 1.5rem;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      background: white;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      
      img {
        width: 200px;
        height: 200px;
        margin-bottom: 1rem;
      }
      
      .url-text {
        word-break: break-all;
        color: #666;
        font-size: 0.9rem;
        margin-bottom: 1rem;
      }
      
      .actions {
        display: flex;
        gap: 0.5rem;
        justify-content: center;
        
        button {
          display: flex;
          align-items: center;
          gap: 0.5rem;
        }
      }
    }
  `]
})
export class QRCodeComponent {
  @Input() url = signal<string>('');
  qrCodeImage = signal<string>('');
  
  constructor(private qrCodeService: QRCodeService) { }
  
  ngOnChanges(): void {
    this.generateQRCode();
  }
  
  generateQRCode(): void {
    this.qrCodeService.generateQRCode(this.url()).subscribe({
      next: (image) => this.qrCodeImage.set(image),
      error: () => this.qrCodeImage.set('')
    });
  }
  
  downloadQRCode(): void {
    const link = document.createElement('a');
    link.href = this.qrCodeImage();
    link.download = `qr-code-${Date.now()}.png`;
    link.click();
  }
  
  copyToClipboard(): void {
    navigator.clipboard.writeText(this.url());
    // Show success message
  }
}
```

## Routing

### App Routing Module
```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: 'QUIZ.TITLE'
  },
  {
    path: 'quiz',
    children: [
      {
        path: '',
        component: QuizListComponent,
        title: 'QUIZ.PUBLIC_QUIZZES'
      },
      {
        path: 'my-quizzes',
        component: MyQuizzesComponent,
        title: 'QUIZ.MY_QUIZZES',
        canActivate: [AuthGuard]
      },
      {
        path: 'create',
        component: QuizCreatorComponent,
        title: 'QUIZ.CREATE',
        canActivate: [AuthGuard]
      },
      {
        path: ':id',
        component: QuizDetailComponent,
        title: 'QUIZ.DETAILS'
      },
      {
        path: ':id/play',
        component: QuizPlayerComponent,
        title: 'QUIZ.PLAY'
      },
      {
        path: ':id/share',
        component: QuizShareComponent,
        title: 'QUIZ.SHARE'
      }
    ]
  },
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        component: LoginComponent,
        title: 'AUTH.LOGIN'
      },
      {
        path: 'register',
        component: RegisterComponent,
        title: 'AUTH.REGISTER'
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
```

### Lazy Loading
```typescript
// For feature modules
{
  path: 'admin',
  loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule),
  canActivate: [AuthGuard, AdminGuard]
}
```

## State Management

### Using Signals (Angular 22.2)
```typescript
// Simple state management with signals
@Component({
  // ...
})
export class QuizListComponent {
  quizzes = signal<Quiz[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  
  constructor(private quizService: QuizService) { }
  
  loadQuizzes(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.quizService.getPublicQuizzes().pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (quizzes) => this.quizzes.set(quizzes),
      error: (err) => this.error.set(err.message)
    });
  }
  
  // Computed values
  quizCount = computed(() => this.quizzes().length);
  hasQuizzes = computed(() => this.quizzes().length > 0);
}
```

### Using RxJS for Complex State
```typescript
// For more complex state management
@Injectable({ providedIn: 'root' })
export class QuizStore {
  private quizzesSubject = new BehaviorSubject<Quiz[]>([]);
  quizzes$ = this.quizzesSubject.asObservable();
  
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();
  
  private errorSubject = new BehaviorSubject<string | null>(null);
  error$ = this.errorSubject.asObservable();
  
  constructor(private quizService: QuizService) { }
  
  loadQuizzes(): void {
    this.loadingSubject.next(true);
    this.errorSubject.next(null);
    
    this.quizService.getPublicQuizzes().pipe(
      finalize(() => this.loadingSubject.next(false))
    ).subscribe({
      next: (quizzes) => this.quizzesSubject.next(quizzes),
      error: (err) => this.errorSubject.next(err.message)
    });
  }
  
  addQuiz(quiz: Quiz): void {
    const current = this.quizzesSubject.value;
    this.quizzesSubject.next([...current, quiz]);
  }
  
  updateQuiz(updatedQuiz: Quiz): void {
    const current = this.quizzesSubject.value;
    this.quizzesSubject.next(
      current.map(q => q.id === updatedQuiz.id ? updatedQuiz : q)
    );
  }
  
  deleteQuiz(id: number): void {
    const current = this.quizzesSubject.value;
    this.quizzesSubject.next(current.filter(q => q.id !== id));
  }
}
```

## Testing

### Component Testing
```typescript
// quiz-creator.component.spec.ts
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { QuizCreatorComponent } from './quiz-creator.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('QuizCreatorComponent', () => {
  let component: QuizCreatorComponent;
  let fixture: ComponentFixture<QuizCreatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        QuizCreatorComponent,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        NoopAnimationsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(QuizCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.quizForm.value).toEqual({
      title: '',
      description: '',
      subject: '',
      difficulty: 'MEDIUM',
      isPublic: false,
      questionCount: 5
    });
  });

  it('should mark form as invalid when empty', () => {
    expect(component.quizForm.invalid).toBe(true);
  });

  it('should mark title as required', () => {
    const titleControl = component.quizForm.get('title');
    titleControl?.setValue('');
    expect(titleControl?.hasError('required')).toBe(true);
  });
});
```

### Service Testing
```typescript
// quiz.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { QuizService } from './quiz.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { environment } from '../../../environments/environment';

describe('QuizService', () => {
  let service: QuizService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [QuizService]
    });

    service = TestBed.inject(QuizService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve public quizzes', () => {
    const mockQuizzes: Quiz[] = [
      { id: 1, title: 'Test Quiz 1', questions: [] },
      { id: 2, title: 'Test Quiz 2', questions: [] }
    ];

    service.getPublicQuizzes().subscribe(quizzes => {
      expect(quizzes.length).toBe(2);
      expect(quizzes).toEqual(mockQuizzes);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/quizzes/public`);
    expect(req.request.method).toBe('GET');
    req.flush(mockQuizzes);
  });

  it('should create a quiz', () => {
    const quizRequest: QuizRequest = {
      title: 'New Quiz',
      description: 'Description',
      subject: 'General',
      difficulty: 'MEDIUM',
      isPublic: false,
      questionCount: 5
    };

    const mockQuiz: Quiz = {
      id: 1,
      title: 'New Quiz',
      description: 'Description',
      questions: []
    };

    service.createQuiz(quizRequest).subscribe(quiz => {
      expect(quiz).toEqual(mockQuiz);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/quizzes`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(quizRequest);
    req.flush(mockQuiz);
  });
});
```

## Performance Optimization

### Lazy Loading
```typescript
// Load feature modules lazily
const routes: Routes = [
  {
    path: 'quiz',
    loadChildren: () => import('./features/quiz/quiz.module').then(m => m.QuizModule)
  }
];
```

### Image Optimization
```html
<!-- Use modern image loading -->
<img [src]="imageUrl"
     [ngSrc]="imageUrl"
     [alt]="altText"
     loading="lazy"
     width="800"
     height="600" />
```

### Change Detection Strategy
```typescript
@Component({
  selector: 'app-quiz-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  // ...
})
export class QuizListComponent {
  // Use signals for better performance
  quizzes = signal<Quiz[]>([]);
  
  // Or use immutable data for OnPush
  @Input() quizzes: Quiz[] = [];
}
```

### TrackBy in NgFor
```html
<div *ngFor="let quiz of quizzes(); trackBy: trackById">
  {{ quiz.title }}
</div>
```

```typescript
// In component
trackById(index: number, quiz: Quiz): number {
  return quiz.id;
}
```

## Accessibility

### ARIA Attributes
```html
<button mat-raised-button
        color="primary"
        aria-label="Create new quiz"
        [disabled]="loading()">
  <span *ngIf="!loading()">Create Quiz</span>
  <mat-spinner *ngIf="loading()" aria-label="Loading"></mat-spinner>
</button>
```

### Keyboard Navigation
```html
<!-- Ensure all interactive elements are keyboard accessible -->
<button mat-button
        (click)="onClick()"
        (keydown.enter)="onClick()"
        (keydown.space)="onClick()">
  Click me
</button>
```

### Form Accessibility
```html
<form [formGroup]="quizForm" aria-label="Create quiz form">
  <mat-form-field appearance="fill">
    <mat-label for="title">Quiz Title</mat-label>
    <input matInput 
           id="title"
           formControlName="title"
           aria-required="true"
           aria-describedby="title-help" />
    <mat-hint id="title-help">Enter a descriptive title</mat-hint>
    <mat-error *ngIf="quizForm.get('title')?.hasError('required')">
      Title is required
    </mat-error>
  </mat-form-field>
</form>
```

## Responsive Design

### Breakpoints
```scss
// Use Angular Material breakpoints
@use '@angular/material' as mat;

.my-component {
  padding: 2rem;
  
  @media (max-width: mat.$breakpoint-md) {
    padding: 1rem;
  }
  
  @media (max-width: mat.$breakpoint-sm) {
    flex-direction: column;
  }
}
```

### Flex Layout
```html
<div class="container">
  <div class="sidebar">Sidebar</div>
  <div class="main">Main Content</div>
</div>
```

```scss
.container {
  display: flex;
  gap: 1rem;
  
  .sidebar {
    flex: 0 0 250px;
  }
  
  .main {
    flex: 1;
  }
  
  @media (max-width: 768px) {
    flex-direction: column;
    
    .sidebar {
      flex: none;
    }
  }
}
```

## Workflow

### 1. Understand the Task
- Read the user request carefully
- Check existing components for patterns
- Identify which files need to be modified/created

### 2. Plan the Implementation
- Break down into smaller components
- Identify dependencies
- Estimate time for each part

### 3. Create/Modify Files
- Follow existing code patterns
- Use standalone components
- Implement proper typing
- Add error handling

### 4. Test Your Changes
- Write unit tests
- Test manually in browser
- Verify responsive design
- Check accessibility

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

### Create New Component
1. Generate component with Angular CLI or manually
2. Make it standalone
3. Add to appropriate module or feature
4. Add inputs/outputs
5. Implement template and styles
6. Write tests

### Add New API Endpoint
1. Create interface/model for response
2. Add method to API service
3. Add method to feature service
4. Use in component
5. Handle errors
6. Write tests

### Add New Feature
1. Create feature module (if lazy-loaded)
2. Create components for the feature
3. Create services if needed
4. Add routing
5. Add navigation links
6. Write comprehensive tests

## Troubleshooting

### Common Issues
1. **CORS Errors** - Check backend CORS configuration
2. **401 Unauthorized** - Verify JWT token is being sent
3. **404 Not Found** - Check API endpoint URLs
4. **TypeScript Errors** - Verify type definitions
5. **Styling Issues** - Check SCSS nesting and specificity
6. **Change Detection Issues** - Use signals or OnPush strategy

### Debugging Tips
```bash
# Run with source maps
ng serve --source-map

# Debug in Chrome DevTools
# Press F12 and use Sources tab

# Check network requests
# Use Network tab in DevTools

# Check console for errors
# Use Console tab in DevTools
```

## Resources
- [Angular Documentation](https://angular.io/docs)
- [Angular Material Documentation](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)
- [ngx-translate Documentation](https://github.com/ngx-translate/core)
- [TypeScript Documentation](https://www.typescriptlang.org/docs/)
- [Angular Testing Guide](https://angular.io/guide/testing)
- [Accessibility Guide](https://angular.io/guide/accessibility)

## Best Practices

1. **Use Standalone Components** - Angular 22.2 recommends standalone
2. **Use Signals** - For local component state
3. **Use OnPush Change Detection** - For better performance
4. **Lazy Load Feature Modules** - For faster initial load
5. **Unsubscribe from Observables** - Prevent memory leaks
6. **Use Async Pipe** - Automatic subscription management
7. **Follow Accessibility Standards** - WCAG 2.1 AA
8. **Write Tests** - For all components and services
9. **Use Proper Typing** - Avoid 'any' type
10. **Keep Components Small** - Single responsibility principle
