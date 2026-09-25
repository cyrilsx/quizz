import {ComponentFixture, TestBed} from '@angular/core/testing';
import {AppComponent} from './app.component';
import {TranslateModule, TranslateService} from '@ngx-translate/core';
import {AuthService} from './core/auth.service';
import {AppTranslateService} from './core/translate.service';
import {provideRouter} from '@angular/router';
import {provideNoopAnimations} from '@angular/platform-browser/animations';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let appTranslateService: jasmine.SpyObj<AppTranslateService>;
  let translateService: TranslateService;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['logout', 'isAuthenticated']);
    const appTranslateSpy = jasmine.createSpyObj<AppTranslateService>('AppTranslateService', ['use']);

    await TestBed.configureTestingModule({
      imports: [AppComponent, TranslateModule.forRoot()],
      providers: [
        provideRouter([]),
        provideNoopAnimations(),
        {provide: AuthService, useValue: authServiceSpy},
        {provide: AppTranslateService, useValue: appTranslateSpy}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    appTranslateService = TestBed.inject(AppTranslateService) as jasmine.SpyObj<AppTranslateService>;
    translateService = TestBed.inject(TranslateService);
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should set default language on init', () => {
    component.ngOnInit();
    expect(appTranslateService.use).toHaveBeenCalledWith('en');
  });

  it('should change language when changeLanguage is called', () => {
    spyOn(translateService, 'use');
    component.changeLanguage('fr');
    expect(translateService.use).toHaveBeenCalledWith('fr');
  });

  it('should call authService.logout() when logout is called', () => {
    component.logout();
    expect(authService.logout).toHaveBeenCalled();
  });
});
