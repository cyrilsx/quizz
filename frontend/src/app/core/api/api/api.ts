export * from './aI.service';
import { AIService } from './aI.service';
export * from './authentication.service';
import { AuthenticationService } from './authentication.service';
export * from './quizzes.service';
import { QuizzesService } from './quizzes.service';
export * from './translations.service';
import { TranslationsService } from './translations.service';
export const APIS = [AIService, AuthenticationService, QuizzesService, TranslationsService];
