import {Component, OnInit, OnDestroy} from '@angular/core';
import {QuizService} from '../../core/quiz.service';
import {AuthService} from '../../core/auth.service';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatTableModule} from '@angular/material/table';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDividerModule} from '@angular/material/divider';
import {MatIconModule} from '@angular/material/icon';
import {ActivatedRoute, RouterModule, Router} from '@angular/router';
import {interval, Subscription} from 'rxjs';
import {DatePipe} from '@angular/common';

interface TempQuizAttendee {
  id: string;
  name: string;
  isAnonymous: boolean;
  joinedAt: string;
  completedAt?: string;
  score?: number;
  totalQuestions?: number;
  correctAnswers?: number;
  successRate?: number;
  durationSeconds?: number;
}

interface TempQuizStats {
  quizId: string;
  quizTitle: string;
  createdAt: string;
  expiresAt: string;
  durationMinutes: number;
  attendees: TempQuizAttendee[];
  totalAttendees: number;
  completedAttendees: number;
  averageScore?: number;
  averageSuccessRate?: number;
}

@Component({
  selector: 'app-temp-quiz-stats',
  standalone: true,
  imports: [
    CommonModule, TranslateModule, MatButtonModule, MatCardModule, 
    MatTableModule, MatProgressSpinnerModule, MatDividerModule, 
    MatIconModule, RouterModule, DatePipe
  ],
  template: `
    <div class="temp-quiz-stats-container">
      <mat-card class="stats-card">
        <mat-card-header>
          <mat-card-title>{{ 'TEMP_QUIZ.STATS_TITLE' | translate }}</mat-card-title>
          <mat-card-subtitle *ngIf="quizStats">
            {{ 'TEMP_QUIZ.QUIZ_ID' | translate }}: {{ quizStats.quizId }} | 
            {{ 'TEMP_QUIZ.CREATED' | translate }}: {{ quizStats.createdAt | date:'medium' }}
          </mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <div *ngIf="isLoading" class="loading-spinner">
            <mat-progress-spinner diameter="50" mode="indeterminate"></mat-progress-spinner>
          </div>

          <div *ngIf="!isLoading && quizStats">
            <!-- Quiz Timer -->
            <div class="quiz-timer" [class.expired]="isQuizExpired">
              <mat-icon>timer</mat-icon>
              <span *ngIf="!isQuizExpired">
                {{ 'TEMP_QUIZ.TIME_LEFT' | translate }}: 
                <strong>{{ timeLeft | date:'HH:mm:ss' }}</strong>
              </span>
              <span *ngIf="isQuizExpired" class="expired-text">
                {{ 'TEMP_QUIZ.QUIZ_EXPIRED' | translate }}
              </span>
            </div>

            <!-- Summary Stats -->
            <div class="stats-summary">
              <div class="stat-card">
                <div class="stat-value">{{ quizStats.totalAttendees }}</div>
                <div class="stat-label">{{ 'TEMP_QUIZ.TOTAL_ATTENDEES' | translate }}</div>
              </div>

              <div class="stat-card">
                <div class="stat-value">{{ quizStats.completedAttendees }}</div>
                <div class="stat-label">{{ 'TEMP_QUIZ.COMPLETED' | translate }}</div>
              </div>

              <div class="stat-card">
                <div class="stat-value">{{ quizStats.averageScore || 0 }}%</div>
                <div class="stat-label">{{ 'TEMP_QUIZ.AVG_SCORE' | translate }}</div>
              </div>

              <div class="stat-card">
                <div class="stat-value">{{ quizStats.averageSuccessRate || 0 }}%</div>
                <div class="stat-label">{{ 'TEMP_QUIZ.AVG_SUCCESS_RATE' | translate }}</div>
              </div>
            </div>

            <mat-divider></mat-divider>

            <!-- Attendees Table -->
            <div class="attendees-section">
              <h3>{{ 'TEMP_QUIZ.ATTENDEES_LIST' | translate }}</h3>
              
              <table mat-table [dataSource]="quizStats.attendees" class="attendees-table">
                <!-- Name Column -->
                <ng-container matColumnDef="name">
                  <th mat-header-cell *matHeaderCellDef>{{ 'TEMP_QUIZ.NAME' | translate }}</th>
                  <td mat-cell *matCellDef="let attendee">
                    {{ attendee.name }} 
                    <mat-icon *ngIf="attendee.isAnonymous" title="{{ 'TEMP_QUIZ.ANONYMOUS' | translate }}">
                      account_circle
                    </mat-icon>
                  </td>
                </ng-container>

                <!-- Status Column -->
                <ng-container matColumnDef="status">
                  <th mat-header-cell *matHeaderCellDef>{{ 'TEMP_QUIZ.STATUS' | translate }}</th>
                  <td mat-cell *matCellDef="let attendee">
                    <span [class.completed]="attendee.completedAt" class="status-badge">
                      {{ attendee.completedAt ? ('TEMP_QUIZ.COMPLETED' | translate) : ('TEMP_QUIZ.IN_PROGRESS' | translate) }}
                    </span>
                  </td>
                </ng-container>

                <!-- Score Column -->
                <ng-container matColumnDef="score">
                  <th mat-header-cell *matHeaderCellDef>{{ 'TEMP_QUIZ.SCORE' | translate }}</th>
                  <td mat-cell *matCellDef="let attendee">
                    {{ attendee.score || '-' }}
                  </td>
                </ng-container>

                <!-- Success Rate Column -->
                <ng-container matColumnDef="successRate">
                  <th mat-header-cell *matHeaderCellDef>{{ 'TEMP_QUIZ.SUCCESS_RATE' | translate }}</th>
                  <td mat-cell *matCellDef="let attendee">
                    {{ attendee.successRate ? (attendee.successRate | number:'1.1-2') + '%' : '-' }}
                  </td>
                </ng-container>

                <!-- Duration Column -->
                <ng-container matColumnDef="duration">
                  <th mat-header-cell *matHeaderCellDef>{{ 'TEMP_QUIZ.DURATION' | translate }}</th>
                  <td mat-cell *matCellDef="let attendee">
                    {{ attendee.durationSeconds ? (attendee.durationSeconds | date:'mm:ss') : '-' }}
                  </td>
                </ng-container>

                <!-- Joined At Column -->
                <ng-container matColumnDef="joinedAt">
                  <th mat-header-cell *matHeaderCellDef>{{ 'TEMP_QUIZ.JOINED_AT' | translate }}</th>
                  <td mat-cell *matCellDef="let attendee">
                    {{ attendee.joinedAt | date:'shortTime' }}
                  </td>
                </ng-container>

                <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
                <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
              </table>
            </div>
          </div>

          <div *ngIf="error" class="error-message">
            {{ error }}
          </div>
        </mat-card-content>
        
        <mat-card-actions>
          <button mat-button color="primary" routerLink="/home">
            {{ 'TEMP_QUIZ.BACK_TO_HOME' | translate }}
          </button>
          <button mat-button color="accent" (click)="refreshStats()">
            {{ 'TEMP_QUIZ.REFRESH' | translate }}
          </button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: [
    `
    .temp-quiz-stats-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    .stats-card {
      width: 100%;
    }

    .loading-spinner {
      display: flex;
      justify-content: center;
      padding: 2rem;
    }

    .quiz-timer {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 1rem;
      background-color: #e3f2fd;
      border-radius: 4px;
      margin-bottom: 1.5rem;
      font-size: 1.1rem;
    }

    .quiz-timer.expired {
      background-color: #ffebee;
    }

    .expired-text {
      color: #f44336;
      font-weight: bold;
    }

    .stats-summary {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1rem;
      margin: 1.5rem 0;
    }

    .stat-card {
      background-color: #f5f5f5;
      padding: 1.5rem;
      border-radius: 8px;
      text-align: center;
    }

    .stat-value {
      font-size: 2rem;
      font-weight: bold;
      color: #3f51b5;
      margin-bottom: 0.5rem;
    }

    .stat-label {
      font-size: 0.9rem;
      color: #666;
    }

    .attendees-section {
      margin: 2rem 0;
    }

    .attendees-table {
      width: 100%;
      margin-top: 1rem;
    }

    .status-badge {
      padding: 0.3rem 0.8rem;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: bold;
    }

    .status-badge.completed {
      background-color: #c8e6c9;
      color: #2e7d32;
    }

    .status-badge:not(.completed) {
      background-color: #ffecb3;
      color: #ff8f00;
    }

    .error-message {
      color: #f44336;
      padding: 1rem;
      background-color: #ffebee;
      border-radius: 4px;
      margin: 1rem 0;
    }

    mat-icon {
      vertical-align: middle;
      margin-right: 0.3rem;
    }
    `
  ]
})
export class TempQuizStatsComponent implements OnInit, OnDestroy {
  quizStats: TempQuizStats | null = null;
  isLoading: boolean = true;
  error: string | null = null;
  timeLeft: Date = new Date(0);
  isQuizExpired: boolean = false;
  displayedColumns: string[] = ['name', 'status', 'score', 'successRate', 'duration', 'joinedAt'];
  
  private quizId: string = '';
  private timerSubscription: Subscription = new Subscription();
  private refreshInterval: Subscription = new Subscription();

  constructor(
    private quizService: QuizService,
    private authService: AuthService,
    private translate: TranslateService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get('id') || '';
    if (!this.quizId) {
      this.error = this.translate.instant('TEMP_QUIZ.INVALID_QUIZ_ID');
      this.isLoading = false;
      return;
    }

    this.loadQuizStats();
    
    // Refresh stats every 30 seconds
    this.refreshInterval = interval(30000).subscribe(() => {
      this.loadQuizStats();
    });
  }

  ngOnDestroy(): void {
    this.timerSubscription.unsubscribe();
    this.refreshInterval.unsubscribe();
  }

  loadQuizStats(): void {
    this.isLoading = true;
    this.error = null;

    // In a real app, this would call the backend API
    // For demo purposes, we'll simulate data
    setTimeout(() => {
      this.quizStats = this.generateMockStats();
      this.updateTimer();
      this.isLoading = false;
    }, 1000);
  }

  updateTimer(): void {
    if (!this.quizStats) return;
    
    const expiresAt = new Date(this.quizStats.expiresAt);
    const now = new Date();
    
    if (now >= expiresAt) {
      this.isQuizExpired = true;
      this.timeLeft = new Date(0);
    } else {
      this.isQuizExpired = false;
      const timeDiff = expiresAt.getTime() - now.getTime();
      this.timeLeft = new Date(timeDiff);
      
      // Update timer every second
      this.timerSubscription.unsubscribe();
      this.timerSubscription = interval(1000).subscribe(() => {
        const now = new Date();
        const timeDiff = expiresAt.getTime() - now.getTime();
        if (timeDiff <= 0) {
          this.isQuizExpired = true;
          this.timeLeft = new Date(0);
        } else {
          this.timeLeft = new Date(timeDiff);
        }
      });
    }
  }

  refreshStats(): void {
    this.loadQuizStats();
  }

  private generateMockStats(): TempQuizStats {
    const mockAttendees: TempQuizAttendee[] = [];
    const totalQuestions = 10;
    const attendeeCount = Math.floor(Math.random() * 15) + 5;
    
    for (let i = 0; i < attendeeCount; i++) {
      const isCompleted = Math.random() > 0.3;
      const isAnonymous = Math.random() > 0.6;
      
      let score = 0;
      let correctAnswers = 0;
      let durationSeconds = 0;
      
      if (isCompleted) {
        score = Math.floor(Math.random() * 100);
        correctAnswers = Math.floor((score / 100) * totalQuestions);
        durationSeconds = 300 + Math.floor(Math.random() * 900); // 5-15 minutes
      }

      mockAttendees.push({
        id: `attendee-${i + 1}`,
        name: isAnonymous ? this.translate.instant('TEMP_QUIZ.ANONYMOUS') : `User ${i + 1}`,
        isAnonymous,
        joinedAt: new Date(Date.now() - Math.floor(Math.random() * 3600000)).toISOString(),
        completedAt: isCompleted ? new Date(Date.now() - Math.floor(Math.random() * 1800000)).toISOString() : undefined,
        score,
        totalQuestions,
        correctAnswers,
        successRate: score,
        durationSeconds
      });
    }

    const completedAttendees = mockAttendees.filter(a => a.completedAt).length;
    const totalScores = mockAttendees
      .filter(a => a.score)
      .reduce((sum, attendee) => sum + (attendee.score || 0), 0);
    
    const totalSuccessRates = mockAttendees
      .filter(a => a.successRate)
      .reduce((sum, attendee) => sum + (attendee.successRate || 0), 0);

    return {
      quizId: this.quizId,
      quizTitle: `Temp Quiz #${this.quizId}`,
      createdAt: new Date(Date.now() - 3600000).toISOString(), // 1 hour ago
      expiresAt: new Date(Date.now() + 7200000).toISOString(), // 2 hours from now
      durationMinutes: 60,
      attendees: mockAttendees,
      totalAttendees: mockAttendees.length,
      completedAttendees,
      averageScore: completedAttendees > 0 ? Math.round(totalScores / completedAttendees) : 0,
      averageSuccessRate: completedAttendees > 0 ? Math.round(totalSuccessRates / completedAttendees) : 0
    };
  }
}