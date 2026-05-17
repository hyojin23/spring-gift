# Error Response Contract: Product 서비스 및 예외 처리 리팩토링

Product API 오류는 `ErrorResponse` JSON 형식을 사용합니다.

## Response Shape

```json
{
  "code": "PRODUCT.NOT_FOUND",
  "message": "요청한 상품을 찾을 수 없습니다.",
  "timestamp": "2026-05-17T22:00:00"
}
```

## Error Codes

| Scenario | HTTP Status | Code | Message |
|----------|-------------|------|---------|
| 상품 미존재 | 404 | `PRODUCT.NOT_FOUND` | `요청한 상품을 찾을 수 없습니다.` |
| 생성/수정 대상 카테고리 미존재 | 404 | `PRODUCT.CATEGORY_NOT_FOUND` | `요청한 카테고리를 찾을 수 없습니다.` |
| 상품명 검증 실패 | 400 | `PRODUCT.INVALID_NAME` | `ProductNameValidator` 오류 메시지 |

## Non-goals

- Bean Validation 실패 응답 표준화는 이번 contract에 포함하지 않습니다.
- AdminProductController의 HTML form 오류 응답은 이번 contract에 포함하지 않습니다.
