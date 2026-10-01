import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Platform } from '@ionic/angular';
import { SplashScreen } from '@capacitor/splash-screen';
import { StatusBar, StatusBarStyle } from '@capacitor/status-bar';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  constructor(
    private platform: Platform,
    private translate: TranslateService
  ) {
    this.initializeApp();
  }

  initializeApp() {
    this.platform.ready().then(() => {
      this.setupStatusBar();
      this.setupTranslations();
    });
  }

  setupStatusBar() {
    if (this.platform.is('capacitor')) {
      StatusBar.setBackgroundColor({ color: '#3f51b5' });
      StatusBar.setStyle({ style: StatusBarStyle.Dark });
    }
  }

  setupTranslations() {
    this.translate.setDefaultLang('en');
    this.translate.use('en');
  }
}