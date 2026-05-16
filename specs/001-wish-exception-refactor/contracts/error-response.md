# API Contract: Error Response

## Error Response Schema

All error responses MUST use the following JSON structure:

```json
{
  "code": "string",
  "message": "string",
  "timestamp": "string",
  "details": {
    "...": "..."
  }
}
```

### Fields

- `code`: machine-readable error code, e.g. `WISH.NOT_FOUND`, `AUTH.UNAUTHORIZED`, `WISH.ACCESS_DENIED`, `INTERNAL.SERVER_ERROR`.
- `message`: human-readable description of the failure.
- `timestamp`: ISO 8601 formatted timestamp when the error occurred.
- `details`: optional object with supplemental context or validation metadata.

## Wish Error Mappings

| Exception | HTTP Status | Example `code` | Description |
|---|---|---|---|
| `AuthenticationException` | 401 | `AUTH.UNAUTHORIZED` | 요청 헤더의 인증 정보가 없거나 유효하지 않을 때 |
| `UnauthorizedWishAccessException` | 403 | `WISH.ACCESS_DENIED` | 다른 사용자의 위시 아이템에 접근 시도할 때 |
| `WishNotFoundException` | 404 | `WISH.NOT_FOUND` | 요청한 위시 아이템을 찾을 수 없을 때 |
| any unexpected exception | 500 | `INTERNAL.SERVER_ERROR` | 예상치 못한 서버 오류 발생 시 |

## Example Responses

### 401 Unauthorized

```json
{
  "code": "AUTH.UNAUTHORIZED",
  "message": "Authentication credentials are missing or invalid.",
  "timestamp": "2026-05-17T12:00:00Z"
}
```

### 403 Forbidden

```json
{
  "code": "WISH.ACCESS_DENIED",
  "message": "You do not have permission to access this wish item.",
  "timestamp": "2026-05-17T12:00:00Z"
}
```

### 404 Not Found

```json
{
  "code": "WISH.NOT_FOUND",
  "message": "The requested wish item does not exist.",
  "timestamp": "2026-05-17T12:00:00Z"
}
```

### 500 Internal Server Error

```json
{
  "code": "INTERNAL.SERVER_ERROR",
  "message": "An unexpected error occurred.",
  "timestamp": "2026-05-17T12:00:00Z"
}
```

## Implementation Notes

- Controllers should not return raw status-only responses for Wish errors.
- All Wish-related error conditions should be raised as exceptions and handled by `GlobalExceptionHandler`.
- The contract applies to Wish flows and the shared global exception handling layer.
