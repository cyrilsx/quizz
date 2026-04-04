import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AuthenticationService } from './api/api/authentication.service';
import { LoginRequest, RegisterRequest, AuthResponse } from './api/model/models';

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

  constructor(private http: HttpClient, private authenticationService: AuthenticationService) { }

  login(username: string, password: string): Observable<AuthResponse> {
    const request: LoginRequest = { username, password };
    return this.authenticationService.login(request).pipe(
      tap((response: AuthResponse) => {
        this.setAuthData(response.token || '', username);
        this.authSubject.next(true);
      })
    );
  }

  register(username: string, email: string, password: string): Observable<AuthResponse> {
    const request: RegisterRequest = { username, email, password };
    return this.authenticationService.register(request).pipe(
      tap((response: AuthResponse) => {
        this.setAuthData(response.token || '', username);
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
}