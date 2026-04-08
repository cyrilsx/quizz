import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BehaviorSubject, Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {AuthResponse, LoginRequest, RegisterRequest} from './api/models';
import {CookieService} from './cookie.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private tokenKey = 'auth_token';
  private usernameKey = 'username';
  private userIdKey = 'user_id';

  private authSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  authState$ = this.authSubject.asObservable();

  constructor(private http: HttpClient, private cookieService: CookieService) { }

  login(username: string, password: string): Observable<AuthResponse> {
    const request: LoginRequest = { username, password };
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, request).pipe(
      tap((response: AuthResponse) => {
        this.setAuthData(response.token || '', username);
        this.resetTempQuizCount(); // Reset quiz limit when user authenticates
        this.authSubject.next(true);
      })
    );
  }

  register(username: string, email: string, password: string): Observable<AuthResponse> {
    const request: RegisterRequest = { username, email, password };
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, request).pipe(
      tap((response: AuthResponse) => {
        this.setAuthData(response.token || '', username);
        this.resetTempQuizCount(); // Reset quiz limit when user authenticates
        this.authSubject.next(true);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.usernameKey);
    localStorage.removeItem(this.userIdKey);
    this.authSubject.next(false);
  }

  /**
   * Check if user can create a temporary quiz (client-side check)
   */
  canCreateTempQuiz(): boolean {
    return this.cookieService.canCreateQuiz();
  }

  /**
   * Increment temp quiz count (client-side tracking)
   */
  incrementTempQuizCount(): void {
    this.cookieService.incrementQuizCount();
  }

  /**
   * Reset temp quiz count (when user authenticates)
   */
  resetTempQuizCount(): void {
    this.cookieService.resetQuizCount();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getUsername(): string | null {
    return localStorage.getItem(this.usernameKey);
  }

  getUserId(): string | null {
    return localStorage.getItem(this.userIdKey);
  }

  private setAuthData(token: string, username: string): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.usernameKey, username);
    // In a real app, you would decode the JWT to get user ID
    // For now, we'll just store a dummy user ID
    localStorage.setItem(this.userIdKey, '1');
  }

  initTempSession(ipAddress: string): Observable<{sessionId: string}> {
    return this.http.post<{sessionId: string}>(`${this.apiUrl}/temp-sessions/init`, {}, {
      params: { ipAddress }
    });
  }

  checkTempSession(ipAddress: string): Observable<{valid: boolean, count: number}> {
    return this.http.get<{valid: boolean, count: number}>(`${this.apiUrl}/temp-sessions/check`, {
      params: { ipAddress }
    });
  }

  generateQRCode(quizId: number): Observable<{qrCodeImage: string}> {
    return this.http.get<{qrCodeImage: string}>(`${this.apiUrl}/qrcodes/quiz/${quizId}`);
  }
}