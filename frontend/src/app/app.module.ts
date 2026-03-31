import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';



// Translation
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

// Components
import { AppComponent } from './app.component';
import { QuizzCreatorComponent } from './features/quizz-creator/quizz-creator.component';
import { QuizzPlayerComponent } from './features/quizz-player/quizz-player.component';
import { QuizzShareComponent } from './features/quizz-share/quizz-share.component';

// Services
import { AuthService } from './core/auth.service';
import { QuizService } from './core/quiz.service';
import { AppTranslateService } from './core/translate.service';

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot([
      { path: 'quiz/create', component: QuizzCreatorComponent },
      { path: 'quiz/play/:id', component: QuizzPlayerComponent },
      { path: 'quiz/share/:id', component: QuizzShareComponent },
      { path: '', redirectTo: '/quiz/create', pathMatch: 'full' }
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
    AppTranslateService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }