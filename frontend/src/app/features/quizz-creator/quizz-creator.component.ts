import {Component} from '@angular/core';
import {AuthService} from '../../core/auth.service';
import {CookieService} from '../../core/cookie.service';
import {QuizService} from '../../core/quiz.service';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {RouterModule} from '@angular/router';

interface Question {
  id: number;
  text: string;
  answers: Answer[];
}

interface Answer {
  id: number;
  text: string;
  isCorrect: boolean;
}

@Component({
  selector: 'app-quizz-creator',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatCheckboxModule, MatProgressSpinnerModule, FormsModule, MatIconModule, RouterModule],
  templateUrl: './quizz-creator.component.html',
  styleUrls: ['./quizz-creator.component.css']
})
export class QuizzCreatorComponent {
  quizTitle: string = '';
  quizDescription: string = '';
  isPublic: boolean = false;
  isLoading: boolean = false;
  error: string | null = null;
  success: string | null = null;
  questions: Question[] = [
    {
      id: 1,
      text: '',
      answers: [
        { id: 1, text: '', isCorrect: false },
        { id: 2, text: '', isCorrect: false },
        { id: 3, text: '', isCorrect: false },
        { id: 4, text: '', isCorrect: false }
      ]
    }
  ];

  constructor(
    private authService: AuthService,
    private cookieService: CookieService,
    private quizService: QuizService,
    private translate: TranslateService
  ) {}

  addQuestion() {
    const newId = this.questions.length > 0 ? Math.max(...this.questions.map(q => q.id)) + 1 : 1;
    this.questions.push({
      id: newId,
      text: '',
      answers: [
        { id: 1, text: '', isCorrect: false },
        { id: 2, text: '', isCorrect: false },
        { id: 3, text: '', isCorrect: false },
        { id: 4, text: '', isCorrect: false }
      ]
    });
  }

  removeQuestion(index: number) {
    this.questions.splice(index, 1);
  }

  addAnswer(questionIndex: number) {
    const question = this.questions[questionIndex];
    const newId = question.answers.length > 0 ? Math.max(...question.answers.map(a => a.id)) + 1 : 1;
    question.answers.push({ id: newId, text: '', isCorrect: false });
  }

  removeAnswer(questionIndex: number, answerIndex: number) {
    this.questions[questionIndex].answers.splice(answerIndex, 1);
  }

  createQuiz() {
    if (!this.quizTitle.trim()) {
      this.error = this.translate.instant('ERROR.TITLE_REQUIRED');
      return;
    }

    // Validate questions
    for (const question of this.questions) {
      if (!question.text.trim()) {
        this.error = this.translate.instant('ERROR.QUESTION_REQUIRED');
        return;
      }

      const correctAnswers = question.answers.filter(a => a.isCorrect);
      if (correctAnswers.length === 0) {
        this.error = this.translate.instant('ERROR.ANSWER_REQUIRED');
        return;
      }

      for (const answer of question.answers) {
        if (!answer.text.trim()) {
          this.error = this.translate.instant('ERROR.ANSWER_TEXT_REQUIRED');
          return;
        }
      }
    }

    // Check temp quiz limit for unauthenticated users
    if (!this.authService.isAuthenticated() && !this.cookieService.canCreateQuiz()) {
      this.error = this.translate.instant('HOME.TEMP_QUIZ_LIMIT_REACHED');
      return;
    }

    this.isLoading = true;
    this.error = null;
    this.success = null;

    const quizData = {
      title: this.quizTitle,
      description: this.quizDescription,
      isPublic: this.isPublic,
      questions: this.questions.map(q => ({
        text: q.text,
        answers: q.answers.map(a => ({
          text: a.text,
          isCorrect: a.isCorrect
        }))
      }))
    };

    this.quizService.createQuiz(quizData).subscribe({
      next: () => {
        this.success = this.translate.instant('SUCCESS.QUIZ_CREATED');
        this.resetForm();
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err.error?.error || this.translate.instant('ERROR.CREATE_FAILED');
        this.isLoading = false;
      }
    });
  }

  generateAIQuestions() {
    // TODO: Implement AI question generation
    console.log('Generating AI questions');
  }

  private resetForm() {
    this.quizTitle = '';
    this.quizDescription = '';
    this.isPublic = false;
  }
}