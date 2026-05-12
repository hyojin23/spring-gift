<!--
Sync Impact Report
Version change: none → 1.0.0
Modified principles: added all principles
Added sections: Project Constraints, Development Workflow
Removed sections: none
Templates requiring updates: ✅ .specify/templates/plan-template.md (no action needed), ✅ .specify/templates/spec-template.md (no action needed), ✅ .specify/templates/tasks-template.md (no action needed)
Follow-up TODOs: none
-->

# spring-gift Constitution

## Core Principles

### I. Domain-First Architecture
Business logic MUST reside in service and domain layers; controllers and handlers MUST orchestrate requests, responses, and validation only.
This keeps the Spring Gift application modular, testable, and resilient to UI or persistence changes.

### II. Test-Driven Stability
Every new feature and every bug fix MUST be accompanied by automated tests that verify intended behavior.
Unit tests MUST cover service and domain logic, and integration tests MUST validate web endpoints and persistence interactions.

### III. Secure, Consistent Communication
All requests MUST be validated, authenticated, and authorized before business actions are executed.
Error responses MUST be consistent and MUST avoid leaking internal implementation or sensitive data.

### IV. Maintainable Simplicity
Design decisions MUST favor the smallest change that solves the current requirement and avoid premature abstraction.
Code MUST be readable, minimize duplication, and align with Kotlin/Java and Spring Boot idioms.

### V. Transparent Change Delivery
Work MUST be linked to a spec or issue, documented in PRs, and reviewed for principle compliance before merge.
Breaking changes, schema migrations, and auth updates MUST include explicit verification and migration notes.

## Project Constraints
The repository is a Spring Boot web application on the Kotlin/JVM stack with a Java 21 toolchain.
The project MUST use Spring Boot 3.5, Flyway for database migrations, ktlint for style, and JUnit Platform for automated tests.
All persistence changes MUST preserve compatibility with the current domain model and migration history.

## Development Workflow
Feature branches MUST be used for new work, and merges MUST only occur after a green build and review approval.
Reviewers MUST verify that code follows the constitution, tests exist for new behavior, and any architectural risk is documented.
All task and plan artifacts SHOULD be kept current so implementation work remains traceable and independently testable.

## Governance
This constitution is the authoritative guide for repository decisions, development workflow, and quality gates.
Amendments require a pull request that documents the rationale, lists affected principles or sections, and includes the updated constitution text.
Versioning uses semantic rules: major for principle redefinition or removal, minor for new principles or process additions, and patch for clarifications or wording fixes.
All changes to this document MUST update the version and the Last Amended date.

**Version**: 1.0.0 | **Ratified**: 2026-05-13 | **Last Amended**: 2026-05-13
