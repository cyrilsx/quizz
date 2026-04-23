import {Component} from '@angular/core';
import {AuthService} from '../../core/auth.service';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {FormsModule} from '@angular/forms';
import {RouterModule, Router} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatCardModule, FormsModule, RouterModule, MatIconModule],
  template: `
    <div class="login-container">
      <mat-card class="login-card">
        <mat-card-header>
          <mat-card-title>{{ 'AUTH.LOGIN' | translate }}</mat-card-title>
        </mat-card-header>
        
        <mat-card-content>
          <form (ngSubmit)="login()" class="login-form">
            <div class="form-actions-top">
              <button mat-button color="primary" routerLink="/" type="button">
                <mat-icon>arrow_back</mat-icon>
                {{ 'AUTH.BACK_HOME' | translate }}
              </button>
              <button mat-button color="accent" routerLink="/auth/register" type="button">
                {{ 'AUTH.REGISTER' | translate }}
              </button>
              <span class="spacer"></span>
              <button mat-raised-button color="primary" type="submit" [disabled]="isLoading">
                <span *ngIf="!isLoading">{{ 'AUTH.LOGIN' | translate }}</span>
                <span *ngIf="isLoading">{{ 'AUTH.LOGIN' | translate }}...</span>
              </button>
            </div>

            <mat-form-field appearance="fill" class="full-width">
              <mat-label>{{ 'AUTH.USERNAME' | translate }}</mat-label>
              <input matInput [(ngModel)]="username" name="username" required>
            </mat-form-field>

            <mat-form-field appearance="fill" class="full-width">
              <mat-label>{{ 'AUTH.PASSWORD' | translate }}</mat-label>
              <input matInput [(ngModel)]="password" name="password" type="password" required>
            </mat-form-field>

            <div *ngIf="error" class="error-message">
              {{ error }}
            </div>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [
    `
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      padding: 2rem;
    }

    .login-card {
      width: 100%;
      max-width: 400px;
    }

    .login-form {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .full-width {
      width: 100%;
    }

    .error-message {
      color: #f44336;
      padding: 0.5rem;
      text-align: center;
    }

    .form-actions-top {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 1px solid #e0e0e0;
    }

    .spacer {
      flex: 1;
    }

    .form-actions-top button[type="submit"] {
      margin-left: auto;
    }
    `
  ]
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  error: string | null = null;
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private translate: TranslateService,
    private router: Router
  ) {}

  login(): void {
    if (!this.username || !this.password) {
      this.error = this.translate.instant('ERROR.TITLE_REQUIRED');
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.authService.login(this.username, this.password).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.isLoading = false;
        this.error = err.error?.error || this.translate.instant('ERROR.LOAD_QUIZ_FAILED');
      }
    });
  }
}