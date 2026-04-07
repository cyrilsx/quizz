import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {AuthService} from './auth.service';
import {
  QuestionGenerationRequest,
  QuestionResponse,
  QuizRequest,
  QuizResponse,
  ShareResponse
} from './api/model';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService) {}

  getAllQuizzes(): Observable<QuizResponse[]> {
    return this.http.get<QuizResponse[]>(`${this.apiUrl}/quizzes`);
  }

  getQuizById(id: number): Observable<QuizResponse> {
    return this.http.get<QuizResponse>(`${this.apiUrl}/quizzes/${id}`);
  }

  createQuiz(quizData: QuizRequest): Observable<QuizResponse> {
    return this.http.post<QuizResponse>(`${this.apiUrl}/quizzes`, quizData, { headers: this.getAuthHeaders() });
  }

  updateQuiz(id: number, quizData: QuizRequest): Observable<QuizResponse> {
    return this.http.put<QuizResponse>(`${this.apiUrl}/quizzes/${id}`, quizData, { headers: this.getAuthHeaders() });
  }

  deleteQuiz(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/quizzes/${id}`, { headers: this.getAuthHeaders() });
  }

  generateShareToken(quizId: number): Observable<ShareResponse> {
    return this.http.post<ShareResponse>(`${this.apiUrl}/quizzes/${quizId}/share`, {}, { headers: this.getAuthHeaders() });
  }

  generateQRCode(quizId: number, baseUrl: string = 'http://localhost:4200'): Observable<Blob> {
    const headers = this.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/qrcodes/quiz/${quizId}`, {
      headers,
      params: { baseUrl },
      responseType: 'blob'
    });
  }

  generateAIQuestions(topic: string, count: number = 5): Observable<QuestionResponse[]> {
    const request: QuestionGenerationRequest = { topic, count };
    return this.http.post<QuestionResponse[]>(`${this.apiUrl}/ia/generate-questions`, request, { headers: this.getAuthHeaders() });
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // Temporary session management
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
}