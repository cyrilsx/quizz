import {Injectable} from '@angular/core';
import {TranslateService as NgxTranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class AppTranslateService {
  constructor(private translate: NgxTranslateService) {
    translate.setFallbackLang('en');
  }

  use(lang: string): void {
    this.translate.use(lang);
  }
}