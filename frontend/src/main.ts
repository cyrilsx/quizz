import {enableProdMode} from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';

import {AppComponent} from './app/app.component';
import {environment} from './environments/environment';
import {provideRouter} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {provideTranslateService} from '@ngx-translate/core';
import {provideTranslateHttpLoader} from '@ngx-translate/http-loader';

if (environment.production) {
  enableProdMode();
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: 'home', loadComponent: () => import('./app/features/home/home.component').then(m => m.HomeComponent) },
      { path: 'quiz/create', loadComponent: () => import('./app/features/quizz-creator/quizz-creator.component').then(m => m.QuizzCreatorComponent) },
      { path: 'quiz/play/:id', loadComponent: () => import('./app/features/quizz-player/quizz-player.component').then(m => m.QuizzPlayerComponent) },
      { path: 'quiz/share/:id', loadComponent: () => import('./app/features/quizz-share/quizz-share.component').then(m => m.QuizzShareComponent) },
      { path: 'auth/login', loadComponent: () => import('./app/features/auth/login.component').then(m => m.LoginComponent) },
      { path: 'auth/register', loadComponent: () => import('./app/features/auth/register.component').then(m => m.RegisterComponent) },
      { path: 'temp-quiz/access', loadComponent: () => import('./app/features/temp-quiz/temp-quiz-access.component').then(m => m.TempQuizAccessComponent) },
      { path: 'temp-quiz/stats/:id', loadComponent: () => import('./app/features/temp-quiz/temp-quiz-stats.component').then(m => m.TempQuizStatsComponent) },
      { path: '', redirectTo: '/home', pathMatch: 'full' }
    ]),
    provideHttpClient(),
    ...provideTranslateHttpLoader({prefix: './assets/i18n/', suffix: '.json'}),
    provideTranslateService()
  ]
})
  .catch(err => console.error(err));