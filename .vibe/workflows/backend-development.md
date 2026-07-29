# Backend Development Workflow

## Overview
This workflow guides AI agents through the process of developing backend features for the Quizz Platform.

## Prerequisites
- Java 21 installed
- Maven installed
- PostgreSQL running
- Redis running
- Mistral API key configured

## Workflow Steps

### 1. Understand the Requirement
**Input:** User request or issue description

**Actions:**
- Read the request carefully
- Identify the feature or bug to implement/fix
- Check existing code for similar implementations
- Review AGENTS.md for project conventions
- Review .vibe/instructions/backend-developer.md for specific guidelines

**Output:** Clear understanding of what needs to be done

### 2. Analyze Existing Code
**Input:** Understanding of the requirement

**Actions:**
- Search for similar code patterns in the project
- Check the relevant module (auth, quizz, ia, translation)
- Review entity classes, repositories, services, and controllers
- Check configuration classes
- Review tests for existing features

**Commands:**
```bash
# Find relevant files
find backend/src -name "*.java" | grep -i quiz

# Search for specific patterns
grep -r "generateQuestions" backend/src/

# Check recent changes
git log --oneline --all -20
```

**Output:** List of files to modify or create

### 3. Plan the Implementation
**Input:** Analysis of existing code

**Actions:**
- Break down the task into smaller steps
- Identify dependencies between steps
- Estimate complexity of each step
- Identify potential risks or challenges
- Plan testing strategy

**Template:**
```markdown
## Implementation Plan

### Step 1: [Description]
- **Files to modify/create:** [list files]
- **Dependencies:** [list dependencies]
- **Complexity:** [Low/Medium/High]
- **Estimated time:** [time estimate]

### Step 2: [Description]
- **Files to modify/create:** [list files]
- **Dependencies:** [list dependencies]
- **Complexity:** [Low/Medium/High]
- **Estimated time:** [time estimate]

## Testing Plan
- Unit tests for: [list components]
- Integration tests for: [list scenarios]
- Manual testing: [list test cases]
```

**Output:** Implementation plan document

### 4. Create Feature Branch
**Input:** Implementation plan

**Actions:**
- Create a new branch with descriptive name
- Follow naming convention: `feature/[short-description]` or `fix/[short-description]`

**Commands:**
```bash
# Create and checkout new branch
git checkout -b feature/add-ai-question-generation

# Push branch to remote (if needed)
git push -u origin feature/add-ai-question-generation
```

**Output:** New feature branch

### 5. Implement Changes
**Input:** Feature branch

**Actions:**
- Follow the implementation plan
- Start with entity classes (if new database tables needed)
- Then create repository interfaces
- Then implement service layer
- Then create controller endpoints
- Finally add configuration if needed

**Order of Implementation:**
1. **Database Layer**
   - Entity classes
   - Repository interfaces
   - Schema updates (schema.sql)
   - Data migrations (if needed)

2. **Service Layer**
   - Service classes
   - Business logic
   - Validation
   - Error handling

3. **API Layer**
   - Controller classes
   - DTO/Record classes
   - Request/Response mappings
   - Exception handling

4. **Configuration**
   - Security configuration (if needed)
   - Caching configuration (if needed)
   - Rate limiting (if needed)

5. **Documentation**
   - Update OpenAPI documentation
   - Update README if needed

**Best Practices:**
- Follow existing code patterns
- Use Java 21 features (records, sealed classes)
- Add proper validation
- Handle errors gracefully
- Add comprehensive logging
- Write clean, readable code

### 6. Write Tests
**Input:** Implemented changes

**Actions:**
- Write unit tests for new classes
- Write integration tests for new features
- Update existing tests if behavior changed
- Test edge cases
- Test error scenarios

**Testing Strategy:**
1. **Unit Tests**
   - Test each public method
   - Test edge cases
   - Mock dependencies
   - Use JUnit 5

2. **Integration Tests**
   - Test interactions between components
   - Use TestContainers for database tests
   - Test API endpoints
   - Test error handling

3. **Manual Testing**
   - Test in development environment
   - Verify API responses
   - Check error handling
   - Verify edge cases

**Commands:**
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=QuizServiceTest

# Run tests with coverage
./mvnw test -Dcoverage
```

**Output:** Passing tests with good coverage (>80%)

### 7. Code Review
**Input:** Implemented and tested changes

**Actions:**
- Self-review checklist:
  - [ ] Code follows project conventions
  - [ ] All tests pass
  - [ ] No breaking changes to existing APIs
  - [ ] Documentation updated
  - [ ] Security considerations addressed
  - [ ] Performance implications considered
  - [ ] Error handling is comprehensive
  - [ ] Logging is appropriate

- Use AI agent to review code:
  - Analyze for best practices
  - Check for security vulnerabilities
  - Verify performance implications
  - Suggest improvements

**Output:** Code ready for human review

### 8. Commit Changes
**Input:** Reviewed code

**Actions:**
- Stage all changes
- Write descriptive commit message
- Reference related issues
- Keep commits atomic

**Commit Message Format:**
```
feat: add AI question generation for quizzes

- Add IaService.generateQuestions() method
- Add MistralClient for API integration
- Add rate limiting for AI endpoints
- Add caching for generated questions
- Add unit and integration tests

Closes #123
```

**Commands:**
```bash
# Stage all changes
git add .

# Commit with message
git commit -m "feat: add AI question generation for quizzes"

# Push to remote
git push
```

**Output:** Committed changes

### 9. Create Pull Request
**Input:** Committed changes

**Actions:**
- Create draft pull request
- Add descriptive title
- Write detailed description
- Include screenshots if UI changes
- Reference related issues
- Request review from appropriate team members

**PR Template:**
```markdown
## Description

[Detailed description of changes]

## Related Issues

Closes #123

## Changes Made

- [ ] Backend changes
- [ ] Frontend changes
- [ ] Database changes
- [ ] Configuration changes
- [ ] Documentation updates
- [ ] Tests added/updated

## Testing

- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed
- [ ] Edge cases tested

## Screenshots

[Add screenshots if applicable]

## Notes

[Any additional notes or considerations]
```

**Commands:**
```bash
# Create draft PR
gh pr create --draft --title "feat: add AI question generation" --body-file pr-description.md
```

**Output:** Draft pull request

### 10. Monitor and Respond to Feedback
**Input:** Pull request created

**Actions:**
- Monitor for review comments
- Address feedback promptly
- Update code based on reviews
- Push changes to the same branch
- Request re-review when ready

**Commands:**
```bash
# Check PR status
gh pr view

# Address feedback, commit changes
git add .
git commit -m "fix: address PR feedback"
git push

# Request re-review
gh pr comment "Addressed feedback, ready for re-review"
```

**Output:** Approved pull request

## Common Workflows

### Add New API Endpoint

1. **Create DTO/Record**
   - Define request/response classes
   - Add validation annotations
   - Follow existing patterns

2. **Add Service Method**
   - Implement business logic
   - Add validation
   - Handle errors
   - Add logging

3. **Add Controller Endpoint**
   - Add proper HTTP method
   - Add request mapping
   - Add response type
   - Add security annotations

4. **Update OpenAPI Documentation**
   - Add endpoint to openapi.yaml
   - Add request/response schemas
   - Add descriptions
   - Add examples

5. **Write Tests**
   - Unit tests for service
   - Integration tests for endpoint
   - Test edge cases

6. **Test Manually**
   - Start application
   - Test endpoint with Postman/curl
   - Verify responses

### Add New Database Table

1. **Create Entity Class**
   - Define fields with proper types
   - Add JPA annotations
   - Add validation
   - Add relationships

2. **Create Repository Interface**
   - Extend JpaRepository
   - Add custom query methods
   - Add proper annotations

3. **Update Schema**
   - Add CREATE TABLE statement to schema.sql
   - Add indexes
   - Add constraints

4. **Create Migration (if using Flyway/Liquibase)**
   - Create migration script
   - Add rollback script
   - Test migration

5. **Update Services**
   - Add repository injection
   - Add methods to use new entity
   - Update business logic

6. **Write Tests**
   - Test repository methods
   - Test service methods
   - Test integration

### Add AI Feature

1. **Create Prompt Template**
   - Define structured prompt
   - Specify required format
   - Include context
   - Request specific output

2. **Update MistralClient**
   - Add new method if needed
   - Or use existing method

3. **Update IaService**
   - Add new method
   - Implement rate limiting
   - Add caching
   - Handle errors

4. **Add Controller Endpoint**
   - Add proper security
   - Add rate limiting
   - Add validation

5. **Update OpenAPI Documentation**
   - Add AI endpoints
   - Add schemas
   - Add descriptions

6. **Write Tests**
   - Mock Mistral API
   - Test prompt generation
   - Test response parsing
   - Test error handling

## Debugging Workflow

### 1. Identify the Problem
- Read error messages
- Check logs
- Reproduce the issue

### 2. Isolate the Component
- Determine which component is failing
- Check dependencies
- Identify the root cause

### 3. Debug
- Add debug logging
- Use debugger
- Check database state
- Verify API responses

**Commands:**
```bash
# Run with debug mode
java -jar -Dspring.profiles.active=dev -Ddebug quizz-backend.jar

# Connect with remote debugger
java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 quizz-backend.jar

# View logs
tail -f logs/application.log

# Check database
psql -U quizz -d quizz -c "SELECT * FROM quizzes;"
```

### 4. Fix the Issue
- Implement the fix
- Test the fix
- Verify no regressions

### 5. Document the Fix
- Update commit message
- Add comments if needed
- Update documentation if needed

## Performance Optimization Workflow

### 1. Identify Performance Bottlenecks
- Check slow API endpoints
- Check database queries
- Check external API calls

### 2. Analyze
- Use profiling tools
- Check database query plans
- Identify slow operations

**Commands:**
```bash
# Enable SQL logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Check query performance
EXPLAIN ANALYZE SELECT * FROM quizzes WHERE user_id = 1;
```

### 3. Optimize
- Add indexes
- Add caching
- Optimize queries
- Batch operations
- Use pagination

### 4. Test
- Verify performance improvement
- Check no functional changes
- Test edge cases

### 5. Monitor
- Add metrics
- Set up alerts
- Monitor in production

## Security Review Workflow

### 1. Identify Security Concerns
- Check authentication/authorization
- Check input validation
- Check data sanitization
- Check error handling
- Check logging

### 2. Review Code
- Check for SQL injection
- Check for XSS vulnerabilities
- Check for CSRF protection
- Check for sensitive data exposure
- Check for proper access control

### 3. Test Security
- Test with invalid inputs
- Test with malicious inputs
- Test authorization boundaries
- Test error handling

### 4. Fix Issues
- Add proper validation
- Add proper sanitization
- Add proper authorization
- Fix error handling
- Remove sensitive data from logs

### 5. Verify
- Retest security
- Verify no regressions
- Update security documentation

## Best Practices Checklist

### Code Quality
- [ ] Follows project coding standards
- [ ] Consistent naming conventions
- [ ] Proper error handling
- [ ] Comprehensive logging
- [ ] Good code organization
- [ ] Single responsibility principle
- [ ] DRY (Don't Repeat Yourself)

### Testing
- [ ] Unit tests for all public methods
- [ ] Integration tests for interactions
- [ ] Edge cases tested
- [ ] Error scenarios tested
- [ ] >80% code coverage

### Security
- [ ] Input validation
- [ ] Output encoding
- [ ] Proper authorization
- [ ] Secure error handling
- [ ] No sensitive data in logs
- [ ] Rate limiting where appropriate

### Performance
- [ ] Proper indexing
- [ ] Appropriate caching
- [ ] Efficient queries
- [ ] Batch operations where possible
- [ ] Lazy loading where appropriate

### Documentation
- [ ] Code comments where needed
- [ ] JavaDoc for public methods
- [ ] Updated OpenAPI documentation
- [ ] Updated README if needed

### DevOps
- [ ] Proper branch naming
- [ ] Descriptive commit messages
- [ ] Atomic commits
- [ ] Pull request description
- [ ] References to related issues
