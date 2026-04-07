import {enableProdMode} from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';

import {AppComponent} from './app/app.component';
import {environment} from './environments/environment';
import {provideRouter} from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import {importProvidersFrom} from '@angular/core';
import {TranslateModule, TranslateLoader} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {HttpClient} from '@angular/common/http';

if (environment.production) {
  enableProdMode();
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter([
      { path: 'home', loadComponent: () => import('./app/features/home/home.component').then(m => m.HomeComponent) },
      { path: 'quiz/create', loadComponent: () => import('./app/features/quizz-creator/quizz-creator.component').then(m => m.QuizzCreatorComponent) },
      { path: 'quiz/play/:id', loadComponent: () => import('./app/features/quizz-player/quizz-player.component').then(m => m.QuizzPlayerComponent) },
      { path: 'quiz/share/:id', loadComponent: () => import('./app/features/quizz-share/quizz-share.component').then(m => m.QuizzShareComponent) },
      { path: '', redirectTo: '/home', pathMatch: 'full' }
    ]),
    provideHttpClient(),
    importProvidersFrom(TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }))
  ]
})
  .catch(err => console.error(err));