import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { QuizzesService } from './api/api/quizzes.service';
import { QuizRequest, QuizResponse, ShareResponse, QuestionGenerationRequest, QuestionResponse } from './api/model/models';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private authService: AuthService, private quizzesService: QuizzesService) {}

  getAllQuizzes(): Observable<QuizResponse[]> {
    return this.quizzesService.getAllQuizzes();
  }

  getQuizById(id: number): Observable<QuizResponse> {
    return this.quizzesService.getQuizById(id);
  }

  createQuiz(quizData: QuizRequest): Observable<QuizResponse> {
    return this.quizzesService.createQuiz(quizData);
  }

  updateQuiz(id: number, quizData: QuizRequest): Observable<QuizResponse> {
    return this.quizzesService.updateQuiz(id, quizData);
  }

  deleteQuiz(id: number): Observable<void> {
    return this.quizzesService.deleteQuiz(id);
  }

  generateShareToken(quizId: number): Observable<ShareResponse> {
    return this.quizzesService.generateShareToken(quizId);
  }

  generateQRCode(quizId: number, baseUrl: string = 'http://localhost:4200'): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get(`${this.apiUrl}/qrcodes/quiz/${quizId}`, {
      headers,
      params: { baseUrl }
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
  initTempSession(ipAddress: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/temp-sessions/init`, {}, {
      params: { ipAddress }
    });
  }

  checkTempSession(ipAddress: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/temp-sessions/check`, {
      params: { ipAddress }
    });
  }
}