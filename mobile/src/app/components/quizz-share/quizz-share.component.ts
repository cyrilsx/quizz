import { Component, OnInit } from '@angular/core';
import { QuizService } from '../../services/quiz.service';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { QrScannerService } from '../../services/qr-scanner.service';
import { AlertController, Platform } from '@ionic/angular';

@Component({
  selector: 'app-quizz-share',
  templateUrl: './quizz-share.component.html',
  styleUrls: ['./quizz-share.component.scss']
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
    private sanitizer: DomSanitizer,
    private qrScanner: QrScannerService,
    private alertController: AlertController,
    private platform: Platform
  ) {}

  ngOnInit(): void {
    this.quizId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadQuizDetails();
    this.generateShareLink();
  }

  loadQuizDetails(): void {
    this.quizService.getQuizById(this.quizId).subscribe({
      next: (quiz) => {
        this.quizTitle = quiz.title;
      },
      error: (err) => {
        this.error = this.translate.instant('ERROR.LOAD_QUIZ_FAILED');
      }
    });
  }

  generateShareLink(): void {
    this.isLoading = true;
    this.error = null;

    this.quizService.generateShareToken(this.quizId).subscribe({
      next: (response) => {
        this.shareToken = response.shareToken;
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

  async scanQRCode(): Promise<void> {
    if (!this.platform.is('capacitor')) {
      this.presentAlert('Error', 'QR scanning is only available on mobile devices');
      return;
    }

    try {
      const qrData = await this.qrScanner.startScan();
      this.presentAlert('QR Code Scanned', `Content: ${qrData}`);
    } catch (error) {
      this.presentAlert('Error', `QR scanning failed: ${error}`);
    }
  }

  copyToClipboard(): void {
    navigator.clipboard.writeText(this.shareUrl).then(() => {
      this.presentAlert('Success', 'URL copied to clipboard');
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

  async presentAlert(header: string, message: string): Promise<void> {
    const alert = await this.alertController.create({
      header,
      message,
      buttons: ['OK']
    });
    await alert.present();
  }
}