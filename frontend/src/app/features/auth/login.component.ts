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

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatCardModule, FormsModule, RouterModule],
  template: `
    <div class="login-container">
      <mat-card class="login-card">
        <mat-card-header>
          <mat-card-title>{{ 'AUTH.LOGIN' | translate }}</mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <form (ngSubmit)="login()" class="login-form">
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

            <button mat-raised-button color="primary" type="submit" [disabled]="isLoading">
              <span *ngIf="!isLoading">{{ 'AUTH.LOGIN' | translate }}</span>
              <span *ngIf="isLoading">{{ 'AUTH.LOGIN' | translate }}...</span>
            </button>
          </form>
        </mat-card-content>
        <mat-card-actions>
          <button mat-button color="accent" routerLink="/auth/register">
            {{ 'AUTH.REGISTER' | translate }}
          </button>
        </mat-card-actions>
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

    button[type="submit"] {
      margin-top: 1rem;
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