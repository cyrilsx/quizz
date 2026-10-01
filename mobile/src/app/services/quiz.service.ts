import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, from } from 'rxjs';
import { Storage } from '@ionic/storage-angular';

@Injectable({
  providedIn: 'root'
})
export class QuizService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient, private storage: Storage) {
    this.initStorage();
  }

  async initStorage() {
    await this.storage.create();
  }

  getAllQuizzes(): Observable<any> {
    return this.http.get(`${this.apiUrl}/quizzes`);
  }

  getQuizById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/quizzes/${id}`);
  }

  createQuiz(quizData: any): Observable<any> {
    return from(this.getAuthHeaders().then(headers =>
      this.http.post(`${this.apiUrl}/quizzes`, quizData, { headers })));
  }

  updateQuiz(id: number, quizData: any): Observable<any> {
    return from(this.getAuthHeaders().then(headers =>
      this.http.put(`${this.apiUrl}/quizzes/${id}`, quizData, { headers })));
  }

  deleteQuiz(id: number): Observable<any> {
    return from(this.getAuthHeaders().then(headers =>
      this.http.delete(`${this.apiUrl}/quizzes/${id}`, { headers })));
  }

  generateShareToken(quizId: number): Observable<any> {
    return from(this.getAuthHeaders().then(headers =>
      this.http.post(`${this.apiUrl}/quizzes/${quizId}/share`, {}, { headers })));
  }

  generateQRCode(quizId: number, baseUrl: string = 'http://localhost:8100'): Observable<any> {
    return from(this.getAuthHeaders().then(headers =>
      this.http.get(`${this.apiUrl}/qrcodes/quiz/${quizId}`, {
        headers,
        params: { baseUrl }
      })));
  }

  generateAIQuestions(topic: string, count: number = 5): Observable<any> {
    return from(this.getAuthHeaders().then(headers =>
      this.http.post(`${this.apiUrl}/ia/generate-questions`, { topic, count }, { headers })));
  }

  private async getAuthHeaders(): Promise<HttpHeaders> {
    const token = await this.storage.get('auth_token');
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