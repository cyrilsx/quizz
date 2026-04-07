import {Component} from '@angular/core';
import {QuizService} from '../../core/quiz.service';
import {AuthService} from '../../core/auth.service';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {FormsModule} from '@angular/forms';
import {RouterModule, Router} from '@angular/router';

@Component({
  selector: 'app-temp-quiz-access',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatCardModule, MatCheckboxModule, FormsModule, RouterModule],
  template: `
    <div class="temp-quiz-access-container">
      <mat-card class="access-card">
        <mat-card-header>
          <mat-card-title>{{ 'TEMP_QUIZ.ACCESS_TITLE' | translate }}</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form (ngSubmit)="joinQuiz()" class="access-form">
            <mat-form-field appearance="fill" class="full-width">
              <mat-label>{{ 'TEMP_QUIZ.QUIZ_CODE' | translate }}</mat-label>
              <input matInput [(ngModel)]="quizCode" name="quizCode" required>
            </mat-form-field>

            <mat-form-field appearance="fill" class="full-width">
              <mat-label>{{ 'TEMP_QUIZ.YOUR_NAME' | translate }}</mat-label>
              <input matInput [(ngModel)]="attendeeName" name="attendeeName" [placeholder]="'TEMP_QUIZ.ANONYMOUS_PLACEHOLDER' | translate">
            </mat-form-field>

            <mat-checkbox [(ngModel)]="isAnonymous" name="isAnonymous" (change)="toggleAnonymous()">
              {{ 'TEMP_QUIZ.JOIN_ANONYMOUSLY' | translate }}
            </mat-checkbox>

            <div *ngIf="error" class="error-message">
              {{ error }}
            </div>

            <button mat-raised-button color="primary" type="submit" [disabled]="isLoading || !quizCode">
              <span *ngIf="!isLoading">{{ 'TEMP_QUIZ.JOIN_QUIZ' | translate }}</span>
              <span *ngIf="isLoading">{{ 'TEMP_QUIZ.JOINING' | translate }}...</span>
            </button>
          </form>
        </mat-card-content>
        <mat-card-footer>
          <p class="info-text">{{ 'TEMP_QUIZ.INFO_TEXT' | translate }}</p>
        </mat-card-footer>
      </mat-card>
    </div>
  `,
  styles: [
    `
    .temp-quiz-access-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      padding: 2rem;
    }

    .access-card {
      width: 100%;
      max-width: 500px;
    }

    .access-form {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .full-width {
      width: 100%;
    }

    .error-message {
      color: #f44336;
      padding: 0.5rem;
      text-align: center;
      background-color: #ffebee;
      border-radius: 4px;
    }

    .info-text {
      font-size: 0.9rem;
      color: #666;
      margin: 0.5rem 0;
      text-align: center;
    }

    mat-checkbox {
      margin-bottom: 1rem;
    }
    `
  ]
})
export class TempQuizAccessComponent {
  quizCode: string = '';
  attendeeName: string = '';
  isAnonymous: boolean = false;
  error: string | null = null;
  isLoading: boolean = false;

  constructor(
    private quizService: QuizService,
    private authService: AuthService,
    private translate: TranslateService,
    private router: Router
  ) {}

  toggleAnonymous(): void {
    if (this.isAnonymous) {
      this.attendeeName = this.translate.instant('TEMP_QUIZ.ANONYMOUS');
    } else {
      this.attendeeName = '';
    }
  }

  joinQuiz(): void {
    if (!this.quizCode) {
      this.error = this.translate.instant('TEMP_QUIZ.CODE_REQUIRED');
      return;
    }

    this.isLoading = true;
    this.error = null;

    // Store attendee info in session storage for temp quiz
    const attendeeInfo = {
      quizCode: this.quizCode,
      name: this.attendeeName || this.translate.instant('TEMP_QUIZ.ANONYMOUS'),
      isAnonymous: this.isAnonymous,
      joinedAt: new Date().toISOString()
    };

    sessionStorage.setItem('tempQuizAttendee', JSON.stringify(attendeeInfo));
    
    // In a real app, we would validate the quiz code with the backend
    // For now, we'll navigate directly to the quiz player
    this.router.navigate([`/quiz/play/${this.quizCode}`]);
    this.isLoading = false;
  }
}