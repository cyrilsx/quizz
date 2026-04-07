// Basic API model definitions
// These would normally be generated from OpenAPI spec

/** Auth related models */
export interface AuthResponse {
  token?: string;
  userId?: string;
  username?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

/** Quiz related models */
export interface QuizRequest {
  title: string;
  description?: string;
  isPublic?: boolean;
  questions: QuestionRequest[];
}

export interface QuizResponse {
  id?: number;
  title: string;
  description?: string;
  isPublic?: boolean;
  createdAt?: string;
  updatedAt?: string;
  userId?: string;
  questions?: QuestionResponse[];
}

export interface QuestionRequest {
  text: string;
  answers: AnswerRequest[];
}

export interface QuestionResponse {
  id?: number;
  text: string;
  answers?: AnswerResponse[];
}

export interface AIGeneratedQuestionResponse {
  question: string;
  answers: string[];
  correctAnswerIndex: number;
  explanation?: string;
}

export interface AnswerRequest {
  text: string;
  isCorrect: boolean;
}

export interface AnswerResponse {
  id?: number;
  text: string;
  isCorrect: boolean;
}

/** Share related models */
export interface ShareResponse {
  shareToken?: string;
  expiresAt?: string;
}

/** AI Generation models */
export interface QuestionGenerationRequest {
  topic: string;
  count?: number;
  difficulty?: string;
}

export interface AIGeneratedQuestionResponse {
  question: string;
  answers: string[];
  correctAnswerIndex: number;
  explanation?: string;
}