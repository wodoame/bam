# Git Workflow Guide

This document describes the Git version control workflow used in the Bank Account Management System project.

## Overview

This project follows a **feature branch workflow** with best practices for version control, including:
- Feature branch development
- Selective commit integration using `cherry-pick`
- Clean commit history
- Proper branching and merging strategies

## Repository Structure

```
main (production-ready code)
├── feature/refactor (code refactoring and cleanup)
├── feature/exceptions (custom exception handling)
├── feature/testing (unit tests with JUnit 5)
├── feature/collections (migration to Java Collections)
├── feature/file-persistence (file I/O implementation)
└── feature/concurrency (thread-safe operations)
```

## Core Git Workflow

### 1. Initial Setup

```bash
# Initialize repository
git init

# Add all files to staging
git add .

# Create initial commit
git commit -m "Initial commit: Basic banking system structure"

# Set up remote repository (if using GitHub)
git remote add origin <repository-url>
git push -u origin main
```

### 2. Feature Branch Development

For each new feature or enhancement, create a dedicated branch:

```bash
# Create and switch to a new feature branch
git checkout -b feature/<feature-name>

# Example: Creating a refactoring branch
git checkout -b feature/refactor
```

**Branch Naming Convention:**
- `feature/<feature-name>` - For new features
- `bugfix/<bug-description>` - For bug fixes
- `hotfix/<urgent-fix>` - For critical production fixes
- `enhancement/<enhancement-name>` - For improvements to existing features

### 3. Making Changes and Commits

```bash
# Check status of modified files
git status

# Stage specific files
git add <filename>

# Or stage all changes
git add .

# Commit with descriptive message
git commit -m "feat: Add custom exception handling for invalid transactions"
```

**Commit Message Convention:**
- `feat:` - New feature
- `fix:` - Bug fix
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `docs:` - Documentation changes
- `style:` - Code style/formatting changes
- `chore:` - Maintenance tasks

### 4. Cherry-Picking Commits

Cherry-pick allows you to selectively apply commits from one branch to another:

```bash
# Switch to the target branch
git checkout feature/testing

# View commit history from source branch
git log feature/refactor --oneline

# Cherry-pick a specific commit
git cherry-pick <commit-hash>

# Cherry-pick multiple commits
git cherry-pick <commit-hash-1> <commit-hash-2>

# Cherry-pick with edit option (to modify commit message)
git cherry-pick -e <commit-hash>
```

**When to Use Cherry-Pick:**
- Applying critical bug fixes from one feature branch to another
- Reusing refactored utility methods across multiple feature branches
- Selectively integrating tested commits without merging entire branches

### 5. Merging Feature Branches

Once a feature is complete and tested:

```bash
# Switch to main branch
git checkout main

# Merge feature branch
git merge feature/<feature-name>

# If conflicts occur, resolve them and commit
git add <resolved-files>
git commit -m "Merge feature/<feature-name> into main"

# Delete merged feature branch (optional)
git branch -d feature/<feature-name>
```

### 6. Handling Merge Conflicts

When conflicts occur during merge or cherry-pick:

```bash
# View conflicted files
git status

# Open and manually resolve conflicts in the files
# (Look for <<<<<<, ======, >>>>>> markers)

# After resolving, stage the files
git add <resolved-files>

# Complete the merge/cherry-pick
git commit -m "Resolve merge conflicts"

# Or, if cherry-picking
git cherry-pick --continue
```

## Project-Specific Workflow Phases

### Phase 1: Setup and Refactoring (Week 1-2)

```bash
git checkout -b feature/refactor
# - Refactor Account and Transaction classes
# - Add JavaDoc comments
# - Improve code structure
git add src/com/bam/models/
git commit -m "refactor: Modularize Account and Transaction classes"
git push origin feature/refactor
```

### Phase 2: Exception Handling (Week 2)

```bash
git checkout -b feature/exceptions
# - Create custom exception classes
# - Implement try-catch blocks
# - Add input validation
git add src/com/bam/exceptions/
git commit -m "feat: Add custom exception handling for transactions"
git push origin feature/exceptions
```

### Phase 3: Testing (Week 2)

```bash
git checkout -b feature/testing
# - Add JUnit 5 tests
# - Create test cases for all core methods
git add src/test/java/
git commit -m "test: Add comprehensive JUnit tests for Account operations"
git push origin feature/testing
```

### Phase 4: Collections Migration (Week 3)

```bash
git checkout -b feature/collections
# - Replace arrays with ArrayList and HashMap
# - Implement functional programming with Streams
git add src/com/bam/services/
git commit -m "feat: Migrate from arrays to Java Collections Framework"
git push origin feature/collections
```

### Phase 5: File Persistence (Week 3)

```bash
git checkout -b feature/file-persistence
# - Implement NIO file operations
# - Add save/load functionality
git add src/com/bam/services/FilePersistenceService.java
git commit -m "feat: Add file persistence with Java NIO"
git push origin feature/file-persistence
```

### Phase 6: Concurrency (Week 3)

```bash
git checkout -b feature/concurrency
# - Add synchronized methods
# - Implement thread-safe transactions
git add src/com/bam/models/Account.java
git commit -m "feat: Implement thread-safe concurrent transactions"
git push origin feature/concurrency
```

## Best Practices

### 1. Commit Frequently
- Make small, logical commits
- Each commit should represent a single unit of work
- Commit working code, not broken code

### 2. Write Meaningful Commit Messages
```bash
# Bad
git commit -m "update"
git commit -m "fix stuff"

# Good
git commit -m "feat: Add overdraft limit validation for CheckingAccount"
git commit -m "fix: Resolve null pointer exception in TransactionManager"
```

### 3. Keep Branches Up to Date
```bash
# Regularly sync with main branch
git checkout feature/<your-branch>
git fetch origin
git merge origin/main
```

### 4. Review Before Merging
```bash
# View differences between branches
git diff main..feature/<feature-name>

# View commit history
git log --oneline --graph --decorate
```

### 5. Use .gitignore
Ensure the following are excluded from version control:
```
# Compiled files
*.class
target/
out/

# IDE specific files
.idea/
*.iml
.vscode/

# OS specific files
.DS_Store
Thumbs.db

# Data files (optional, depending on requirements)
data/*.txt
```

## Common Git Commands Reference

| Command | Description |
|---------|-------------|
| `git status` | Show working directory status |
| `git log --oneline` | View commit history (condensed) |
| `git branch` | List all branches |
| `git branch -d <branch>` | Delete a branch |
| `git checkout <branch>` | Switch to a branch |
| `git checkout -b <branch>` | Create and switch to new branch |
| `git add <file>` | Stage file for commit |
| `git commit -m "<message>"` | Commit staged changes |
| `git push origin <branch>` | Push branch to remote |
| `git pull origin <branch>` | Pull changes from remote |
| `git merge <branch>` | Merge branch into current branch |
| `git cherry-pick <hash>` | Apply specific commit |
| `git stash` | Temporarily save uncommitted changes |
| `git stash pop` | Restore stashed changes |
| `git reset --soft HEAD~1` | Undo last commit (keep changes) |
| `git reset --hard HEAD~1` | Undo last commit (discard changes) |

## Troubleshooting

### Undo Last Commit (Keep Changes)
```bash
git reset --soft HEAD~1
```

### Undo Last Commit (Discard Changes)
```bash
git reset --hard HEAD~1
```

### Discard Uncommitted Changes
```bash
git checkout -- <file>
# Or discard all changes
git reset --hard
```

### View Specific Commit Details
```bash
git show <commit-hash>
```

### Compare Branches
```bash
git diff main..feature/<branch-name>
```

## Resources

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Flow Guide](https://guides.github.com/introduction/flow/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Cherry-Pick Tutorial](https://git-scm.com/docs/git-cherry-pick)

---

**Last Updated:** December 17, 2025  
**Project:** Bank Account Management System  
**Version:** 1.0

