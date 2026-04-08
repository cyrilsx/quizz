import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CookieService {
  private readonly QUIZZ_LIMIT_COOKIE = 'quizz-limit';
  private readonly MAX_QUIZ_COUNT = 5;
  private readonly COOKIE_EXPIRY_DAYS = 1;

  constructor() {}

  /**
   * Get the current quiz count from cookie
   */
  getQuizCount(): number {
    const cookieValue = this.getCookie(this.QUIZZ_LIMIT_COOKIE);
    return cookieValue ? parseInt(cookieValue, 10) : 0;
  }

  /**
   * Increment the quiz count and update cookie
   */
  incrementQuizCount(): number {
    const currentCount = this.getQuizCount();
    if (currentCount >= this.MAX_QUIZ_COUNT) {
      throw new Error('Maximum quiz limit reached');
    }
    
    const newCount = currentCount + 1;
    this.setCookie(this.QUIZZ_LIMIT_COOKIE, newCount.toString(), this.COOKIE_EXPIRY_DAYS);
    return newCount;
  }

  /**
   * Check if user can create more quizzes
   */
  canCreateQuiz(): boolean {
    return this.getQuizCount() < this.MAX_QUIZ_COUNT;
  }

  /**
   * Reset quiz count (for testing or when user authenticates)
   */
  resetQuizCount(): void {
    this.setCookie(this.QUIZZ_LIMIT_COOKIE, '0', this.COOKIE_EXPIRY_DAYS);
  }

  /**
   * Get cookie by name
   */
  private getCookie(name: string): string | null {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return parts.pop()?.split(';').shift() || null;
    }
    return null;
  }

  /**
   * Set cookie with expiry
   */
  private setCookie(name: string, value: string, days: number): void {
    const date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    const expires = `expires=${date.toUTCString()}`;
    document.cookie = `${name}=${value}; ${expires}; path=/; SameSite=Lax`;
  }

  /**
   * Delete cookie
   */
  private deleteCookie(name: string): void {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
  }
}