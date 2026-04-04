import { Component, OnInit } from '@angular/core';
import { QuizService } from '../../core/quiz.service';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-quizz-share',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule, MatCardModule, MatProgressSpinnerModule, MatIconModule],
  templateUrl: './quizz-share.component.html',
  styleUrls: ['./quizz-share.component.css']
})
export class QuizzShareComponent implements OnInit {
  quizId: number = 0;
  quizTitle: string = '';
  shareUrl: string = '';
  qrCodeImage: SafeUrl | null = null;
  shareToken: string = '';
  isLoading: boolean = false;
  error: string | null = null;

  constructor(
    private quizService: QuizService,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.quizId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadQuizDetails();
    this.generateShareLink();
  }

  loadQuizDetails(): void {
    this.quizService.getQuizById(this.quizId).subscribe({
      next: (quiz) => {
        this.quizTitle = quiz.title || '';
      },
      error: (err) => {
        this.error = this.translate.instant('ERROR.LOAD_QUIZ_FAILED');
      }
    });
  }

  generateShareLink(): void {
    this.isLoading = true;
    this.error = null;

    // Generate share token
    this.quizService.generateShareToken(this.quizId).subscribe({
      next: (response) => {
        this.shareToken = response.shareToken || '';
        this.shareUrl = `${window.location.origin}/quiz/play/${this.quizId}?token=${this.shareToken}`;
        this.generateQRCode();
      },
      error: (err) => {
        this.error = this.translate.instant('ERROR.GENERATE_SHARE_FAILED');
        this.isLoading = false;
      }
    });
  }

  generateQRCode(): void {
    this.quizService.generateQRCode(this.quizId).subscribe({
      next: (response) => {
        const qrCodeData = `data:image/png;base64,${response.qrCodeImage}`;
        this.qrCodeImage = this.sanitizer.bypassSecurityTrustUrl(qrCodeData);
        this.isLoading = false;
      },
      error: (err) => {
        this.error = this.translate.instant('ERROR.GENERATE_QR_FAILED');
        this.isLoading = false;
      }
    });
  }

  copyToClipboard(): void {
    navigator.clipboard.writeText(this.shareUrl).then(() => {
      // You could show a toast notification here
      console.log('URL copied to clipboard');
    }, (err) => {
      console.error('Could not copy text: ', err);
    });
  }

  downloadQRCode(): void {
    if (this.qrCodeImage) {
      const link = document.createElement('a');
      link.href = this.qrCodeImage.toString();
      link.download = `quiz-${this.quizId}-qrcode.png`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
}