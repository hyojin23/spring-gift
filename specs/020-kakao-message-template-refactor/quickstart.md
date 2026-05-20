# Quickstart: Kakao 메시지 템플릿 분리 리팩토링

## 구현 순서

1. `KakaoMessageTemplateBuilderTest`를 추가합니다.
2. 상품명, 옵션명, 수량, 총 금액이 포함되는지 테스트합니다.
3. 주문 메시지가 있으면 메시지 영역이 포함되는지 테스트합니다.
4. 주문 메시지가 null 또는 blank이면 메시지 영역이 생략되는지 테스트합니다.
5. `KakaoMessageTemplateBuilder`를 추가합니다.
6. `KakaoMessageClient`에 `KakaoMessageTemplateBuilder`를 주입합니다.
7. `KakaoMessageClient.buildTemplate()` private method를 제거합니다.
8. 관련 order 테스트를 실행합니다.

## 검증 명령

```powershell
.\gradlew.bat test --tests *Order*
```

## 수동 확인 포인트

- `KakaoMessageClient.sendToMe()` signature가 유지됩니다.
- `KakaoMessageClient`에 `buildTemplate()` private method가 없습니다.
- `template_object` form field 이름이 유지됩니다.
- 템플릿 builder 테스트가 메시지 있음/null/blank 케이스를 검증합니다.
