# Contract: Option Error Response

Option API에서 도메인 예외가 발생하면 응답 본문은 `ErrorResponse` 형식을 따릅니다.

## Response Body

```json
{
  "code": "OPTION.NOT_FOUND",
  "message": "요청한 옵션을 찾을 수 없습니다.",
  "timestamp": "2026-05-17T00:00:00Z"
}
```

## Error Codes

| Scenario | HTTP Status | Code | Message |
|----------|-------------|------|---------|
| 대상 상품 없음 | 404 | `OPTION.PRODUCT_NOT_FOUND` | `요청한 상품을 찾을 수 없습니다.` |
| 대상 옵션 없음 | 404 | `OPTION.NOT_FOUND` | `요청한 옵션을 찾을 수 없습니다.` |
| 중복 옵션명 | 400 | `OPTION.DUPLICATE_NAME` | `이미 존재하는 옵션명입니다.` |
| 마지막 옵션 삭제 제한 | 400 | `OPTION.DELETE_NOT_ALLOWED` | `옵션이 1개인 상품은 옵션을 삭제할 수 없습니다.` |
| 옵션명 검증 실패 | 400 | `OPTION.INVALID_NAME` | 검증 실패 메시지 |

## Endpoints Covered

- `GET /api/products/{productId}/options`
- `POST /api/products/{productId}/options`
- `DELETE /api/products/{productId}/options/{optionId}`

## Compatibility Notes

- 정상 응답의 HTTP 상태와 본문 구조는 변경하지 않습니다.
- 기존 문자열 기반 400 응답은 표준 JSON 에러 응답으로 변경됩니다.
- 404 응답은 빈 본문 대신 표준 JSON 에러 응답을 반환합니다.
