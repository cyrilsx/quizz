-- Insert sample user
INSERT INTO users (username, email, password) VALUES 
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.M5c6S3b5J8JqLJ5J8JqLJ5J8JqLJ5J8');

-- Insert sample quiz
INSERT INTO quizzes (title, description, user_id, is_public) VALUES 
('Sample Quiz', 'This is a sample quiz', 1, TRUE);

-- Insert sample questions
INSERT INTO questions (quiz_id, question_text) VALUES 
(1, 'What is the capital of France?'),
(1, 'What is 2 + 2?');

-- Insert sample answers
INSERT INTO answers (question_id, answer_text, is_correct) VALUES 
(1, 'Paris', TRUE),
(1, 'London', FALSE),
(2, '4', TRUE),
(2, '5', FALSE);