# Data Model: Wish Exception Refactor

## Domain Entities

### WishException
- base type for Wish domain exceptions
- extends `RuntimeException`
- used for domain-specific error semantics in `wish` flows

### WishNotFoundException
- extends `WishException`
- indicates a requested wish item does not exist
- maps to HTTP 404

### UnauthorizedWishAccessException
- extends `WishException`
- indicates the authenticated member does not have permission to modify or access the wish
- maps to HTTP 403

### AuthenticationException
- extends `RuntimeException` or `WishException`
- indicates missing or invalid authentication information
- maps to HTTP 401

### ErrorResponse
- standard error payload returned to clients
- fields:
  - `code`: string, machine-readable error code
  - `message`: string, human-readable error message
  - `timestamp`: string, ISO 8601 instant
  - `details`: optional additional context or validation metadata

## Relationships

- `WishController` accepts requests and delegates to `WishService`.
- `WishService` throws domain exceptions for missing wish items and forbidden access.
- `GlobalExceptionHandler` maps exceptions to `ErrorResponse` and HTTP status codes.
- Authentication failures are converted to `AuthenticationException` before reaching the service layer.

## Validation rules

- missing or invalid auth -> `AuthenticationException` -> HTTP 401
- wish not found -> `WishNotFoundException` -> HTTP 404
- forbidden wish access -> `UnauthorizedWishAccessException` -> HTTP 403
- unexpected exceptions -> generic error response -> HTTP 500

## Scope

- scope is limited to Wish exception handling and central error mapping.
- product-not-found handling remains outside the current Wish exception refactor unless explicitly expanded.
