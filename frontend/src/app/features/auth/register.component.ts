import {Component} from '@angular/core';
import {AuthService} from '../../core/auth.service';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCardModule} from '@angular/material/card';
import {FormsModule} from '@angular/forms';
import {RouterModule, Router} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, TranslatePipe, MatButtonModule, MatInputModule, MatFormFieldModule, MatCardModule, FormsModule, RouterModule, MatIconModule],
  template: `
    <div class="register-container">
      <mat-card class="register-card">
        <mat-card-header>
          <mat-card-title>{{ 'AUTH.REGISTER' | translate }}</mat-card-title>
        </mat-card-header>
        
        <mat-card-content>
          <form (ngSubmit)="register()" class="register-form">
            <div class="form-actions-top">
              <button mat-button color="primary" routerLink="/" type="button">
                <mat-icon>arrow_back</mat-icon>
                {{ 'AUTH.BACK_HOME' | translate }}
              </button>
              <button mat-button color="accent" routerLink="/auth/login" type="button">
                {{ 'AUTH.LOGIN' | translate }}
              </button>
              <span class="spacer"></span>
              <button mat-raised-button color="primary" type="submit" [disabled]="isLoading">
                <span *ngIf="!isLoading">{{ 'AUTH.REGISTER' | translate }}</span>
                <span *ngIf="isLoading">{{ 'AUTH.REGISTER' | translate }}...</span>
              </button>
            </div>

            <mat-form-field appearance="fill" class="full-width">
              <mat-label>{{ 'AUTH.USERNAME' | translate }}</mat-label>
              <input matInput [(ngModel)]="username" name="username" required>
            </mat-form-field>

            <mat-form-field appearance="fill" class="full-width">
              <mat-label>{{ 'AUTH.EMAIL' | translate }}</mat-label>
              <input matInput [(ngModel)]="email" name="email" type="email" required>
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
    .register-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 80vh;
      padding: 2rem;
    }

    .register-card {
      width: 100%;
      max-width: 400px;
    }

    .register-form {
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
export class RegisterComponent {
  username: string = '';
  email: string = '';
  password: string = '';
  error: string | null = null;
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private translate: TranslateService,
    private router: Router
  ) {}

  register(): void {
    if (!this.username || !this.email || !this.password) {
      this.error = this.translate.instant('ERROR.TITLE_REQUIRED');
      return;
    }

    this.isLoading = true;
    this.error = null;

    this.authService.register(this.username, this.email, this.password).subscribe({
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