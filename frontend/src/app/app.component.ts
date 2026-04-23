import {Component, OnInit} from '@angular/core';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {AuthService} from './core/auth.service';
import {AppTranslateService} from './core/translate.service';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {RouterOutlet, RouterModule} from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
    imports: [CommonModule, MatButtonModule, TranslateModule, RouterOutlet, RouterModule],
  template: `
    <div class="app-container">
      <nav class="navbar">
        <button mat-button class="navbar-brand" routerLink="/">
          Quizz Platform
        </button>
        <div class="navbar-actions">
          <button mat-button routerLink="/home">
            {{ 'AUTH.HOME' | translate }}
          </button>
          <button mat-button (click)="changeLanguage('en')">English</button>
          <button mat-button (click)="changeLanguage('fr')">Français</button>
          <button *ngIf="authService.isAuthenticated()" mat-button (click)="logout()">
            {{ 'AUTH.LOGOUT' | translate }}
          </button>
        </div>
      </nav>
      
      <div class="main-content">
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
  styles: [
    `
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 2rem;
      background-color: #3f51b5;
      color: white;
    }

    .navbar-brand {
      font-size: 1.5rem;
      font-weight: bold;
      color: white;
      background: none;
      box-shadow: none;
      padding: 0;
      height: auto;
      line-height: normal;
      cursor: pointer;
    }

    .navbar-brand:hover {
      text-decoration: underline;
    }

    .navbar-actions {
      display: flex;
      gap: 1rem;
    }

    .navbar-actions button:first-child {
      font-weight: bold;
    }

    .navbar-actions button {
      color: white;
    }

    .main-content {
      flex: 1;
      padding: 2rem;
      background-color: #f5f5f5;
    }
    `
  ]
})
export class AppComponent implements OnInit {
  constructor(
    private translate: TranslateService,
    public authService: AuthService,
    private appTranslate: AppTranslateService
  ) {}

  ngOnInit(): void {
    // Set default language
    this.appTranslate.use('en');
  }

  changeLanguage(lang: string): void {
    this.translate.use(lang);
  }

  logout(): void {
    this.authService.logout();
  }
}