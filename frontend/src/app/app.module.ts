import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClient, provideHttpClient} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';


// Translation
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';

// Components
import {AppComponent} from './app.component';
import {QuizzCreatorComponent} from './features/quizz-creator/quizz-creator.component';
import {QuizzPlayerComponent} from './features/quizz-player/quizz-player.component';
import {QuizzShareComponent} from './features/quizz-share/quizz-share.component';
import {HomeComponent} from './features/home/home.component';

// Services
import {AuthService} from './core/auth.service';
import {QuizService} from './core/quiz.service';
import {AppTranslateService} from './core/translate.service';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot([
      { path: 'home', component: HomeComponent },
      { path: 'quiz/create', component: QuizzCreatorComponent },
      { path: 'quiz/play/:id', component: QuizzPlayerComponent },
      { path: 'quiz/share/:id', component: QuizzShareComponent },
      { path: '', redirectTo: '/home', pathMatch: 'full' }
    ]),

    // Translation
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    })
  ],
  providers: [
    AuthService,
    QuizService,
    AppTranslateService,
    provideHttpClient(),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }