import { Component, OnInit } from '@angular/core';
import { QuizService } from '../../core/quiz.service';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatProgressBarModule } from '@angular/material/progress-bar';

interface Question {
  id: number;
  text: string;
  answers: Answer[];
}

interface Answer {
  id: number;
  text: string;
  isCorrect: boolean;
  selected: boolean;
}

@Component({
  selector: 'app-quizz-player',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatCardModule, MatProgressSpinnerModule, MatProgressBarModule],
  templateUrl: './quizz-player.component.html',
  styleUrls: ['./quizz-player.component.css']
})
export class QuizzPlayerComponent implements OnInit {
  quizId: number = 0;
  quizTitle: string = '';
  questions: Question[] = [];
  currentQuestionIndex: number = 0;
  score: number = 0;
  isLoading: boolean = false;
  error: string | null = null;
  quizCompleted: boolean = false;

  constructor(
    private quizService: QuizService,
    private route: ActivatedRoute,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.quizId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadQuiz();
  }

  loadQuiz(): void {
    this.isLoading = true;
    this.error = null;

    this.quizService.getQuizById(this.quizId).subscribe({
      next: (quiz) => {
        this.quizTitle = quiz.title || '';
        // In a real implementation, you would load questions from the quiz
        this.initializeSampleQuestions();
        this.isLoading = false;
      },
      error: () => {
        this.error = this.translate.instant('ERROR.LOAD_QUIZ_FAILED');
        this.isLoading = false;
      }
    });
  }

  initializeSampleQuestions(): void {
    // This is sample data - in a real app, you would load from the API
    this.questions = [
      {
        id: 1,
        text: 'What is the capital of France?',
        answers: [
          { id: 1, text: 'Paris', isCorrect: true, selected: false },
          { id: 2, text: 'London', isCorrect: false, selected: false },
          { id: 3, text: 'Berlin', isCorrect: false, selected: false },
          { id: 4, text: 'Madrid', isCorrect: false, selected: false }
        ]
      },
      {
        id: 2,
        text: 'What is 2 + 2?',
        answers: [
          { id: 1, text: '3', isCorrect: false, selected: false },
          { id: 2, text: '4', isCorrect: true, selected: false },
          { id: 3, text: '5', isCorrect: false, selected: false },
          { id: 4, text: '6', isCorrect: false, selected: false }
        ]
      }
    ];
  }

  selectAnswer(answer: Answer): void {
    // Reset all answers for the current question
    this.questions[this.currentQuestionIndex].answers.forEach(a => a.selected = false);
    answer.selected = true;
  }

  submitAnswer(): void {
    const currentQuestion = this.questions[this.currentQuestionIndex];
    const selectedAnswer = currentQuestion.answers.find(a => a.selected);

    if (!selectedAnswer) {
      this.error = this.translate.instant('ERROR.NO_ANSWER_SELECTED');
      return;
    }

    if (selectedAnswer.isCorrect) {
      this.score++;
    }

    // Move to next question or complete quiz
    if (this.currentQuestionIndex < this.questions.length - 1) {
      this.currentQuestionIndex++;
    } else {
      this.quizCompleted = true;
    }

    this.error = null;
  }

  getCurrentQuestion(): Question {
    return this.questions[this.currentQuestionIndex];
  }

  getProgress(): number {
    return ((this.currentQuestionIndex + 1) / this.questions.length) * 100;
  }

  restartQuiz(): void {
    this.currentQuestionIndex = 0;
    this.score = 0;
    this.quizCompleted = false;
    this.questions.forEach(q => q.answers.forEach(a => a.selected = false));
  }
}