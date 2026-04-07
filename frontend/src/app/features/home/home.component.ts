import {Component} from '@angular/core';
import {AuthService} from '../../core/auth.service';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {RouterModule} from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatCardModule, RouterModule],
  template: `
    <div class="home-container">
      <h1>{{ 'HOME.WELCOME' | translate }}</h1>
      
      <div class="feature-cards">
        <mat-card class="feature-card">
          <mat-card-header>
            <mat-card-title>{{ 'HOME.TEMP_QUIZ_TITLE' | translate }}</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p>{{ 'HOME.TEMP_QUIZ_DESCRIPTION' | translate }}</p>
            <ul>
              <li>{{ 'HOME.TEMP_QUIZ_LIMITED' | translate }}</li>
              <li>{{ 'HOME.TEMP_QUIZ_NO_SAVE' | translate }}</li>
              <li>{{ 'HOME.TEMP_QUIZ_QUICK' | translate }}</li>
            </ul>
          </mat-card-content>
          <mat-card-actions>
            <button mat-button color="primary" routerLink="/quiz/create">
              {{ 'HOME.START_TEMP_QUIZ' | translate }}
            </button>
          </mat-card-actions>
        </mat-card>

        <mat-card class="feature-card">
          <mat-card-header>
            <mat-card-title>{{ 'HOME.REGISTERED_TITLE' | translate }}</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <p>{{ 'HOME.REGISTERED_DESCRIPTION' | translate }}</p>
            <ul>
              <li>{{ 'HOME.REGISTERED_SAVE' | translate }}</li>
              <li>{{ 'HOME.REGISTERED_SHARE' | translate }}</li>
              <li>{{ 'HOME.REGISTERED_ANALYTICS' | translate }}</li>
              <li>{{ 'HOME.REGISTERED_AI' | translate }}</li>
              <li>{{ 'HOME.REGISTERED_HISTORY' | translate }}</li>
            </ul>
          </mat-card-content>
          <mat-card-actions>
            <button *ngIf="!authService.isAuthenticated()" mat-button color="accent" routerLink="/auth/register">
              {{ 'HOME.REGISTER_NOW' | translate }}
            </button>
            <button *ngIf="authService.isAuthenticated()" mat-button color="accent" routerLink="/quiz/create">
              {{ 'HOME.CREATE_QUIZ' | translate }}
            </button>
          </mat-card-actions>
        </mat-card>
      </div>
      
      <div *ngIf="!authService.isAuthenticated()" class="auth-prompt">
        <p>{{ 'HOME.LOGIN_REQUIRED' | translate }}</p>
        <div class="auth-buttons">
          <button mat-button color="primary" routerLink="/auth/login">
            {{ 'AUTH.LOGIN' | translate }}
          </button>
          <button mat-button color="accent" routerLink="/auth/register">
            {{ 'AUTH.REGISTER' | translate }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
    .home-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 2rem;
    }

    .feature-cards {
      display: flex;
      gap: 2rem;
      margin: 2rem 0;
      flex-wrap: wrap;
    }

    .feature-card {
      flex: 1;
      min-width: 300px;
    }

    mat-card {
      margin-bottom: 1rem;
    }

    ul {
      padding-left: 1.5rem;
    }

    .auth-prompt {
      text-align: center;
      margin-top: 2rem;
      padding: 1rem;
      background-color: #fff3cd;
      border-radius: 4px;
    }

    .auth-buttons {
      display: flex;
      justify-content: center;
      gap: 1rem;
      margin-top: 1rem;
    }
    `
  ]
})
export class HomeComponent {
  constructor(
    public authService: AuthService,
    private translate: TranslateService
  ) {}

  startTempQuiz(): void {
    // Initialize temporary session
    const ipAddress = '127.0.0.1'; // In real app, get actual IP
    this.authService.initTempSession(ipAddress).subscribe({
      next: (response) => {
        console.log('Temp session started:', response.sessionId);
        // Navigate to quiz creation with temp session
      },
      error: () => {
        this.translate.get('HOME.TEMP_SESSION_FAILED').subscribe(msg => {
          console.error(msg);
        });
      }
    });
  }
}